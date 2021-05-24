package HttpGw;

public class FastFileSrvInfo {
    private final int port;
    private int idle_time;

    public FastFileSrvInfo(int port, int idle_time) {
        this.port = port;
        this.idle_time = idle_time;
    }

    public int getPort() {
        return port;
    }

    public int get_idle_time() {
        return idle_time;
    }

    public void reset_idle_time() {
        this.idle_time = 0;
    }

    public void increase_idle_time(int inc) {
        this.idle_time += inc;
    }
}
