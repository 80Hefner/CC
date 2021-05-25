import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPListener implements Runnable {
    private final DatagramSocket data_socket;
    private final BeaconHandler beacon_handler;

    public UDPListener(BeaconHandler beacon_handler) throws SocketException {
        this.data_socket = new DatagramSocket(HttpGw.Default_UDP_Port);
        this.beacon_handler = beacon_handler;
    }

    public void run() {

        System.out.println("[UDPLISTENER] Listening in UDP " + HttpGw.Default_UDP_Port);

        while(true) {
            try {
                // Wait for UDP connection from FastFileSrv
                DatagramPacket data_packet = new DatagramPacket(new byte[Packet.Max_Size], Packet.Max_Size);
                data_socket.receive(data_packet);
                byte[] buffer = new byte[data_packet.getLength()];
                System.arraycopy(data_packet.getData(), 0, buffer, 0, data_packet.getLength());

                // Parse datagram packet info
                InetAddress fast_file_address = data_packet.getAddress();
                int port = data_packet.getPort();

                // Parse packet data
                Packet packet = Serializer.Deserialize_Packet(buffer);
                PacketType packet_type = packet.getType();

                if (packet_type == PacketType.CONNECTION) {
                    System.out.println("[UDPLISTENER] UDP connection received");

                    // Establish UDP connection with FastFileSrv
                    if (HttpGw.fast_files.containsKey(fast_file_address))
                        System.out.println("[UDPLISTENER] Connection rejected. IP already connected to Gateway");
                    else {
                        HttpGw.fast_files.put(fast_file_address, new FastFileSrvInfo(port, 0));
                        System.out.println("[UDPLISTENER] FastFileSrv with address " + fast_file_address + " connected to HttpGw");

                        // Send ACK packet to FastFileSrv
                        packet = new Packet(-1, PacketType.ACK_CONNECTION, 0, 1, null);
                        byte[] send_buf = Serializer.Serialize_Packet(packet);
                        data_packet = new DatagramPacket(send_buf, send_buf.length,
                                data_packet.getAddress(), data_packet.getPort());
                        data_socket.send(data_packet);
                    }
                }
                else if (packet_type == PacketType.BEACON) {
                    // Process beacon packet
                    beacon_handler.process_packet(data_packet);
                }
                else if (packet_type == PacketType.DATA) {
                    // Process data packet
                    System.out.println("[UDPLISTENER] Received packet with file data from " + fast_file_address);
                    int packet_id = packet.getId();
                    HttpGw.http_workers.get(packet_id).Add_Fragment(packet);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

