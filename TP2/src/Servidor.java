import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Servidor {

    public static void main(String[] args) throws IOException {
        DatagramSocket s = new DatagramSocket(80);
        byte[] buffer = new byte[1024];
        while (true) {
            DatagramPacket p = new DatagramPacket(buffer, buffer.length);
            s.receive(p);
            System.out.println(p.toString());
        }
    }
}
