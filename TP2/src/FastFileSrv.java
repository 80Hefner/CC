import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FastFileSrv {
    public static void main(String[] args) throws IOException {
        // IP address and port of HttpGw
        InetAddress gateway_address = InetAddress.getByName(args[0]);
        int gateway_port = Integer.parseInt(args[1]); // porta à qual se pretende ligar

        // Getting self IP address and opening socket for communication with HttpGw
        InetAddress self_address = InetAddress.getLocalHost(); // meter no pacote e enviar ao gateway para ele registar
        DatagramSocket data_socket1 = new DatagramSocket(gateway_port);

        // Establish connection with HttpGw
        Packet packet = new Packet(-1, PacketType.CONNECTION, 0, 1, null);
        byte[] buf = Serializer.Serialize_Packet(packet);
        DatagramPacket data_packet = new DatagramPacket(buf, buf.length,
                gateway_address, gateway_port);
        data_socket1.send(data_packet);
        System.out.println(self_address + " connecting to gateway: " + gateway_address);

        // Wait for HttpGw response
        data_packet = new DatagramPacket(new byte[Packet.Max_Size], Packet.Max_Size);
        data_socket1.receive(data_packet);

        // Create thread to send beacon packets to HttpGw
        Thread beacon_worker = new Thread(new FastFileSrvBeacon(data_socket1, gateway_address));
        beacon_worker.start();

        // Cycle to wait for packets from HttpGwWorker
        while (true) {
            // Wait for message from HttpGwWorker
            data_packet = new DatagramPacket(new byte[Packet.Max_Size], Packet.Max_Size);
            data_socket1.receive(data_packet);
            byte[] buffer = new byte[data_packet.getLength()];
            System.arraycopy(data_packet.getData(), 0, buffer, 0, data_packet.getLength());
            System.out.println("Received packet from " + data_packet.getAddress());

            // Parse message from HttpGwWorker
            packet = Serializer.Deserialize_Packet(buffer);
            String file_name = Serializer.Deserialize_String(packet.getData());
            int packet_id = packet.getId();

            // Get file bytes
            byte[] file_bytes = Files.readAllBytes(Paths.get(System.getProperty("user.dir") + "/FastFiles" + file_name));
            int bytes_left = file_bytes.length, byte_offset = 0;
            int fragments = bytes_left / Packet.Max_Data_Size + 1;

            // Process all fragments
            while (bytes_left > 0) {
                // Get bytes of this fragment
                byte[] buf1, buf2;
                if (bytes_left <= Packet.Max_Data_Size)
                    buf1 = Arrays.copyOfRange(file_bytes, byte_offset, byte_offset + bytes_left);
                else
                    buf1 = Arrays.copyOfRange(file_bytes, byte_offset, byte_offset + Packet.Max_Data_Size);

                // Create a packet and send it
                packet = new Packet(packet_id, PacketType.DATA, byte_offset, fragments, buf1);
                buf2 = Serializer.Serialize_Packet(packet);
                data_packet = new DatagramPacket(buf2, buf2.length, gateway_address, gateway_port);
                data_socket1.send(data_packet);
                System.out.println("Sent packet to " + gateway_address);

                byte_offset += buf1.length;
                bytes_left -= buf1.length;
            }

        }
    }
}
