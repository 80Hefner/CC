package FastFileSrv;

import HttpGw.HttpGw;
import utils.Serializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FastFileSrvBeacon implements Runnable {
    private final DatagramSocket data_socket;
    private final InetAddress self_address;
    private final int beacon_interval_time = 5000;

    public FastFileSrvBeacon(DatagramSocket data_socket, InetAddress self_address) {
        this.data_socket = data_socket;
        this.self_address = self_address;
    }

    public void run() {
        try {
            while (true) {
                Thread.sleep(beacon_interval_time);

                byte[] buf = Serializer.Serialize_String("beacon");
                DatagramPacket packet = new DatagramPacket(buf, buf.length, self_address, HttpGw.Default_UDP_Port);
                data_socket.send(packet);
                System.out.println("Sent beacon packet on port " + HttpGw.Default_UDP_Port);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
