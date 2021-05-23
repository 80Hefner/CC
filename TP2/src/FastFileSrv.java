import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class FastFileSrv {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramSocket s = new DatagramSocket(8080);
        byte[] buffer = new byte[1024];
        while (true) {
            DatagramPacket p = new DatagramPacket(buffer, buffer.length);
            s.receive(p);

            byte[] ibytes = p.getData();
            Packet packet = Packet.deserialize(ibytes);

            System.out.println(packet.toString());
        }
    }
}
