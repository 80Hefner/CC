package HttpGw;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class BeaconHandler implements Runnable {
    private final int max_idle_time = 10000;
    private final int check_interval_time = 1000;

    public void process_packet(DatagramPacket packet) {
        InetAddress address = packet.getAddress();

        FastFileSrvInfo info = HttpGw.fast_files.get(address);
        info.reset_idle_time();

        System.out.println("Received beacon packet from " + address);
    }

    public void run() {
        try {
            while (true) {
                HttpGw.fast_files.values().forEach(f -> f.increase_idle_time(check_interval_time));
                HttpGw.fast_files.entrySet().stream()
                        .filter(e -> e.getValue().get_idle_time() > max_idle_time)
                        .forEach(e -> {
                            HttpGw.fast_files.remove(e.getKey());
                            System.out.println("Removed FastFileSrv with address: " + e.getKey() + " due to inactivity.");
                        });

                Thread.sleep(check_interval_time);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
