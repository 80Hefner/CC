public class FastFileSrvInfo {
    private final int port;
    private int idle_time;
    private boolean available;

    public FastFileSrvInfo(int port, int idle_time) {
        this.port = port;
        this.idle_time = idle_time;
        this.available = true;
    }

    public void setAvailability(boolean bool) {
        this.available = bool;
    }

    public boolean isAvailable() {
        return this.available;
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
