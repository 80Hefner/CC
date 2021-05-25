import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListener implements Runnable {
    private final ServerSocket server_socket;

    public TCPListener(ServerSocket server_socket) {
        this.server_socket = server_socket;
    }

    public void run() {
        while(true){
            try {
                // Accept connection from client
                Socket socket = this.server_socket.accept();

                // Create worker for this client and add it to HttpGw database
                int worker_id = HttpGw.Next_Client_ID++;
                HttpGwWorker worker = new HttpGwWorker(worker_id, socket);
                HttpGw.http_workers.put(worker_id, worker);

                // Start worker thread
                Thread worker_thread = new Thread(worker);
                worker_thread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
