import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpGwWorker implements Runnable {
    private final Socket socket;
    private final InetAddress fastFileAddress;
    private final BufferedReader dis;
    private final DataOutputStream dos;
    private List<FastFileSrv> fast_files;

    public HttpGwWorker (Socket socket, InetAddress fastFileAddress, List<FastFileSrv> fast_files) throws IOException {
        this.socket = socket;
        this.fastFileAddress = fastFileAddress;
        this.dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.fast_files = fast_files;
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
        System.out.println(file_name);

        // Establish connection with FastFileSrv
        byte[] buf = file_name.getBytes();
        DatagramSocket data_socket;
        DatagramPacket packet;
        try {
            data_socket = new DatagramSocket();
            packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("localhost"), 8686);

            // Send request to FastFileSrv
            data_socket.send(packet);

            // Receive response from FastFileSrv
            packet = new DatagramPacket(new byte[4096], 4096);
            data_socket.receive(packet);

            // Send packet data to client
            dos.write(packet.getData());
            dos.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}