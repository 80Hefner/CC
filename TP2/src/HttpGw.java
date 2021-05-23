import java.io.*;
import java.net.*;
import java.util.Map;

public class HttpGw {
    private Map<FastFileSrv> fast_files;
    
    public static void main(String[] args) throws IOException {
        InetAddress hostname = InetAddress.getLocalHost();
        System.out.println(hostname.toString());

        ServerSocket server_socket = new ServerSocket(8080);

        while(true){
            Socket socket = server_socket.accept();
            Thread worker = new Thread(new HttpGwWorker(socket));
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