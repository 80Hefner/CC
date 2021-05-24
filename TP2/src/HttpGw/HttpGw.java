package HttpGw;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class HttpGw {

    public static ConcurrentHashMap<InetAddress, FastFileSrvInfo> fast_files;
    public static int Default_UDP_Port = 8888;
    public static int NextPort = 8889;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(InetAddress.getLocalHost());

        // Initialize fast files array
        HttpGw.fast_files = new ConcurrentHashMap<>();

        // Create beacon packet handler on UDP connection
        BeaconHandler beacon_handler = new BeaconHandler();
        Thread beacon_handler_thread = new Thread(beacon_handler);
        beacon_handler_thread.start();

        // Create thread to listen on UDP connection
        Thread udp_listener = new Thread(new UDPListener(beacon_handler));
        udp_listener.start();

        // Create server socket for TCP connections
        ServerSocket server_socket = new ServerSocket(8080);

        // Create thread to listen TCP connections
        Thread tcp_listener = new Thread(new TCPListener(server_socket));
        tcp_listener.start();

        // Wait for listener threads to exit
        udp_listener.join();
        tcp_listener.join();

        // Close server socket and exit program
        server_socket.close();
    }
}
