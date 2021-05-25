import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class HttpGw {

    public static ConcurrentHashMap<InetAddress, FastFileSrvInfo> fast_files;
    public static ConcurrentHashMap<Integer, HttpGwWorker> http_workers;
    public static int Default_UDP_Port = 8888;
    public static int Default_TCP_Port = 8080;
    public static int Next_Client_ID = 0;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("[HTTPGW] Gateway connected in address: " + InetAddress.getLocalHost() + " in UDP port: " + Default_UDP_Port);
        System.out.println("[HTTPGW] Gateway connected in address: " + InetAddress.getLocalHost() + " in TCP port: " + Default_TCP_Port);

        // Initialize fast files and http workers map
        HttpGw.fast_files = new ConcurrentHashMap<>();
        HttpGw.http_workers = new ConcurrentHashMap<>();

        // Create beacon packet handler on UDP connection
        BeaconHandler beacon_handler = new BeaconHandler();
        Thread beacon_handler_thread = new Thread(beacon_handler);
        beacon_handler_thread.start();

        // Create thread to listen on UDP connection
        Thread udp_listener = new Thread(new UDPListener(beacon_handler));
        udp_listener.start();

        // Create server socket for TCP connections
        ServerSocket server_socket = new ServerSocket(Default_TCP_Port);

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
