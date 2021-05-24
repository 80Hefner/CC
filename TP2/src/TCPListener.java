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
                Socket socket = this.server_socket.accept();
                Thread worker = new Thread(new HttpGwWorker(socket));
                worker.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
