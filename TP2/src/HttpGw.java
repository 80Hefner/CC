import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class HttpGw {

    public static void main(String[] args) throws IOException {
        InetAddress hostname = InetAddress.getLocalHost();
        System.out.println(hostname.getHostAddress());

        List<FastFileSrv> fast_files = new ArrayList<>();

        ServerSocket server_socket = new ServerSocket(8080);

        // Establish UDP connection
        byte[] buf = new byte[4096];
        DatagramSocket data_socket = new DatagramSocket(8888);
        DatagramPacket data_packet = new DatagramPacket(buf, buf.length);
        data_socket.receive(data_packet);

        InetAddress address = data_packet.getAddress();
        System.out.println("adasfra: " + address);
        data_packet = new DatagramPacket(buf, buf.length, data_packet.getAddress(), data_packet.getPort());
        data_socket.send(data_packet);

        // TCP connection
        while(true){
            Socket socket = server_socket.accept();
            Thread worker = new Thread(new HttpGwWorker(socket, address, fast_files));
            worker.start();
        }


         //server_socket.close();


//        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
//        s.receive(response);
//
//        String quote = new String(buffer, 0, response.getLength());
//
//        System.out.println(quote);


    }
}


// byte[] ibytes = input.getBytes();
//             Packet packet = new Packet(ibytes);
//             byte[] pbytes = packet.serialize();

//             DatagramPacket p = new DatagramPacket(pbytes, pbytes.length, hostname, port);
//             System.out.println(p.toString());
//             try {
//                 udp_socket.send(p);
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }