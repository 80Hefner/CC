import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FastFileSrvBeacon implements Runnable {
    private final DatagramSocket data_socket;
    private final InetAddress address_gateway;
    private final int beacon_interval_time = 5000;

    public FastFileSrvBeacon(DatagramSocket data_socket, InetAddress address_gateway) {
        this.data_socket = data_socket;
        this.address_gateway = address_gateway;
    }

    public void run() {
        try {
            while (true) {
                Thread.sleep(beacon_interval_time);

                Packet packet = new Packet(-1, PacketType.BEACON, 0, 1, null);
                byte[] buf = Serializer.Serialize_Packet(packet);
                DatagramPacket data_packet = new DatagramPacket(buf, buf.length,
                        address_gateway, HttpGw.Default_UDP_Port);
                data_socket.send(data_packet);
                System.out.println("[FAST_FILE_SRV_BEACON] Sent beacon packet on port " + HttpGw.Default_UDP_Port);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
