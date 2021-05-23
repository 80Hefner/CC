import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FastFileSrv {

    public static void main(String[] args) throws IOException {
        DatagramSocket data_socket1 = new DatagramSocket();
        InetAddress selfAddress = InetAddress.getByName("localhost");
        System.out.println("address: " + selfAddress);
        byte[] buffer = new byte[4096];

        //Establish connection with HttpGw
        DatagramPacket p = new DatagramPacket(buffer, buffer.length,
                selfAddress, 8888);
        data_socket1.send(p);

        p = new DatagramPacket(buffer, buffer.length);
        data_socket1.receive(p);

        while (true) {
            // Wait for message from HttpGwWorker
            DatagramSocket data_socket2 = new DatagramSocket(8686);
            p = new DatagramPacket(buffer, buffer.length);
            data_socket2.receive(p);

            // Parse message from HttpGwWorker
            InetAddress address = p.getAddress();
            int port = p.getPort();
            buffer = p.getData();
            int size = 0;

            for (int i = 0; i < buffer.length; i++) {
                if (buffer[i] == 0) {
                    size = i;
                    break;
                }
            }

            String file_name = new String(buffer, 0, size);
            buffer = Files.readAllBytes(Paths.get(System.getProperty("user.dir") + file_name));

            p = new DatagramPacket(buffer, buffer.length, address, port);
            data_socket2.send(p);

            data_socket2.close();
        }
    }
}
