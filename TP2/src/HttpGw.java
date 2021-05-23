import java.io.IOException;
import java.net.*;
import java.util.Scanner;


public class HttpGw {


    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        //byte[] buffer = new byte[1024];
        InetAddress hostname = InetAddress.getLocalHost();
        System.out.println(hostname.toString());
        int port = 8080;
        DatagramSocket s = new DatagramSocket();


        while(true){
            System.out.println("Introduce message to send: ");
            String input = sc.nextLine();
            byte[] ibytes = input.getBytes();
            Packet packet = new Packet(ibytes);
            byte[] pbytes = packet.serialize();

            DatagramPacket p = new DatagramPacket(pbytes, pbytes.length, hostname, port);
            System.out.println(p.toString());
            try {
                s.send(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
//        s.receive(response);
//
//        String quote = new String(buffer, 0, response.getLength());
//
//        System.out.println(quote);
    }
}
