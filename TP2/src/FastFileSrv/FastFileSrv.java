package FastFileSrv;

import HttpGw.HttpGw;
import utils.Serializer;

import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FastFileSrv {
    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket data_socket1 = new DatagramSocket();
        InetAddress self_address = InetAddress.getByName("localhost");
        System.out.println("address: " + self_address);

        //Establish connection with HttpGw
        byte[] buf = Serializer.Serialize_String("start connection");
        DatagramPacket p = new DatagramPacket(buf, buf.length,
                self_address, HttpGw.Default_UDP_Port);
        data_socket1.send(p);

        // Wait for HttpGw response
        p = new DatagramPacket(new byte[4096], 4096);
        data_socket1.receive(p);

        // Connect to given port
        int port = Serializer.Deserialize_Int(p.getData());
        System.out.println("FastFileSrv connected. Port " + port);

        // Create thread to send beacon packets to HttpGw
        Thread beacon_worker = new Thread(new FastFileSrvBeacon(data_socket1, self_address));
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
