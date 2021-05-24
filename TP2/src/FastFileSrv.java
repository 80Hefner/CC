import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FastFileSrv {
    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket data_socket1 = new DatagramSocket();
        InetAddress self_address = InetAddress.getLocalHost();
        InetAddress address_gateway = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        System.out.println("address: " + address_gateway);

        //Establish connection with HttpGw
        byte[] buf = Serializer.Serialize_String("start connection");
        DatagramPacket p = new DatagramPacket(buf, buf.length,
                address_gateway, port);
        data_socket1.send(p);

        // Wait for HttpGw response
        p = new DatagramPacket(new byte[4096], 4096);
        data_socket1.receive(p);

        // Connect to given port
        port = Serializer.Deserialize_Int(p.getData());
        System.out.println("FastFileSrv connected. Port " + port);
        // Create thread to send beacon packets to HttpGw
        Thread beacon_worker = new Thread(new FastFileSrvBeacon(data_socket1, address_gateway));
        beacon_worker.start();

        // Cycle to wait for packets from HttpGwWorker
        while (true) {
            // Wait for message from HttpGwWorker
            DatagramSocket data_socket2 = new DatagramSocket(port);
            p = new DatagramPacket(new byte[4096], 4096);
            data_socket2.receive(p);

            // Parse message from HttpGwWorker
            InetAddress address = p.getAddress();
            String file_name = Serializer.Deserialize_String(p.getData());
            byte[] send_buf = Files.readAllBytes(Paths.get(System.getProperty("user.dir") + file_name));

            // Send file content to HttpGwWorker
            p = new DatagramPacket(send_buf, send_buf.length, address, port);
            data_socket2.send(p);

            data_socket2.close();
        }
    }
}
