package HttpGw;

import utils.Serializer;

import java.io.*;
import java.net.*;

public class HttpGwWorker implements Runnable {
    private final Socket socket;
    private final BufferedReader dis;
    private final DataOutputStream dos;

    public HttpGwWorker (Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
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
        DatagramSocket data_socket;
        DatagramPacket packet;
        try {
            data_socket = new DatagramSocket();
            byte[] buf = Serializer.Serialize_String(file_name);
            packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("localhost"), 8889);

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
