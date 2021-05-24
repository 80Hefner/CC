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

        System.out.println("UDP Listener: Listening in UDP 8888");

        while(true) {
            try {
                // Wait for UDP connection from FastFileSrv
                DatagramPacket data_packet = new DatagramPacket(new byte[4096], 4096);
                data_socket.receive(data_packet);
                System.out.println("Conexão UDP recebida");
                // Parse packet data
                String message = Serializer.Deserialize_String(data_packet.getData());
                InetAddress address = data_packet.getAddress();
                System.out.println("InetAddress: " + address);
                if (message.equals("start connection")) {
                    // Establish UDP connection with FastFileSrv
                    int port = HttpGw.NextPort++;
                    HttpGw.fast_files.put(address, new FastFileSrvInfo(port, 0));
                    System.out.println("UDP Listener: FastFileSrv with address " + address + " connected to HttpGw");

                    // ACK
                    byte[] send_buf = Serializer.Serialize_Int(port);
                    data_packet = new DatagramPacket(send_buf, send_buf.length, data_packet.getAddress(), data_packet.getPort());
                    data_socket.send(data_packet);
                    System.out.println("Gateway enviou pacote de resposta");
                }
                else if (message.equals("beacon")) {
                    // Process beacon packet
                    beacon_handler.process_packet(data_packet);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

