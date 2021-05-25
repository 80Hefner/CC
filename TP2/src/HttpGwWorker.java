import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class HttpGwWorker implements Runnable {
    private final int worker_id;
    private final Socket socket;
    private final BufferedReader dis;
    private final DataOutputStream dos;
    private final ReentrantLock wait_lock;
    private final Condition wait;
    private List<Packet> packet_fragments;
    private ReentrantLock fragments_lock;
    private int fragments;
    private boolean error_found;

    public HttpGwWorker (int worker_id, Socket socket) throws IOException {
        this.worker_id = worker_id;
        this.socket = socket;
        this.dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.wait_lock = new ReentrantLock();
        this.wait = wait_lock.newCondition();
        this.packet_fragments = new ArrayList<>();
        this.fragments_lock = new ReentrantLock();
        this.fragments = 0;
        this.error_found = false;
    }

    public void run() {
        // Read http request from client
        String line, http_cont = "";
        try {
            do {
                line = dis.readLine();
                http_cont += line + "\n";
            } while (!line.equals(""));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Parse http request to get file name
        String file_name = http_cont.split(" ")[1];

        // Establish connection with FastFileSrv
        DatagramSocket data_socket;
        DatagramPacket data_packet;
        Packet packet;
        try {
            data_socket = new DatagramSocket();
            List<InetAddress> available_addresses = HttpGw.fast_files.entrySet()
                    .stream()
                    .filter(e -> e.getValue().isAvailable())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            InetAddress address = available_addresses.get(0);
            HttpGw.fast_files.get(address).setAvailability(false);

            // Prepare packet to send to FastFileSrv
            byte[] buf = Serializer.Serialize_String(file_name);
            packet = new Packet(worker_id, PacketType.DATA, 0, 1, buf);
            byte[] buf2 = Serializer.Serialize_Packet(packet);
            data_packet = new DatagramPacket(buf2, buf2.length, address, HttpGw.Default_UDP_Port);

            // Send request to FastFileSrv
            data_socket.send(data_packet);
            System.out.println("[WORKER " + worker_id + "] sent packet to " + data_packet.getAddress());

            // Wait for response from FastFileSrv
            wait_lock.lock();
            try {
                wait.await();
                this.fragments = this.packet_fragments.get(0).getFragments();
            } finally {
                wait_lock.unlock();
            }

            if (!this.error_found) {
                // Cycle if packet received is fragmented
                wait_lock.lock();
                try {
                    for (int i = 1; i < this.fragments; i++)
                        wait.await();
                } finally {
                    wait_lock.unlock();
                }

                // Defragment fragments
                byte[] return_bytes;
                if (this.packet_fragments.size() > 1)
                    return_bytes = Defragment_Fragments();
                else
                    return_bytes = this.packet_fragments.iterator().next().getData();

                // Send packet data to client
                System.out.println("[WORKER " + worker_id + "] sending requested file to client " + socket.getInetAddress());
                dos.write(return_bytes);
            }
            else {
                System.out.println("[WORKER " + worker_id + "] sending error message in a file to client " + socket.getInetAddress());
                String error_message = Serializer.Deserialize_String(this.packet_fragments.get(0).getData());
                System.out.println("ERROR: " + error_message);
                dos.write(Serializer.Serialize_String(error_message));
            }

            dos.flush();
            socket.close();

            // Remove this worker from HttpGw database
            HttpGw.http_workers.remove(this.worker_id);

            // Set FastFileSrv used as available
            HttpGw.fast_files.get(address).setAvailability(true);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Signal_Fragment() {
        this.wait_lock.lock();
        try {
            this.wait.signalAll();
        } finally {
            this.wait_lock.unlock();
        }
    }

    public void Add_Fragment(Packet fragment) {
        fragments_lock.lock();
        try {
            this.packet_fragments.add(fragment);
        } finally {
            fragments_lock.unlock();
        }

        System.out.println("[WORKER " + worker_id + "] received packet #" + fragment.getOffset()/Packet.Max_Data_Size);
    }

    public void Add_Error_Packet(Packet packet) {
        fragments_lock.lock();
        try {
            this.error_found = true;
            this.packet_fragments.add(0, packet);
        } finally {
            fragments_lock.unlock();
        }

        System.out.println("[WORKER " + worker_id + "] received error packet");
    }

    private byte[] Defragment_Fragments() {
        Collections.sort(packet_fragments);

        Packet last_fragment = packet_fragments.get(packet_fragments.size() - 1);
        int total_bytes = last_fragment.getOffset() + last_fragment.getData().length;
        ByteBuffer buffer = ByteBuffer.wrap(new byte[total_bytes]);

        for (Packet p : packet_fragments) {
            buffer.put(p.getData());
        }

        return buffer.array();
    }
}
