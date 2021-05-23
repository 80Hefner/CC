import java.io.*;
import java.net.Socket;

public class HttpGwWorker implements Runnable {
    private final Socket socket;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    public HttpGwWorker (Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void run() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read all lines from the socket
        String inputLine, content = "";
        while (true) {
            try {
                if (!!(inputLine = in.readLine()).equals("")) break;
                content += inputLine + "\n";
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Parse filename from HTTP request
        String[] tokens = content.split(" ");
        String filename = tokens[1];

        

        //socket.close();
    }



}