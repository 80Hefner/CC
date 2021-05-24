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

                byte[] buf = Serializer.Serialize_String("beacon");
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address_gateway, HttpGw.Default_UDP_Port);
                data_socket.send(packet);
                System.out.println("Sent beacon packet on port " + HttpGw.Default_UDP_Port);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
