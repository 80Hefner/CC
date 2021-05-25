import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

enum PacketType {
    BEACON, CONNECTION, ACK_CONNECTION, DATA;
}

public class Packet implements Serializable, Comparable<Packet> {
    public static final int Max_Data_Size = 4096;
    public static final int Header_Size = 16;
    public static final int Max_Size = Header_Size + Max_Data_Size;

    private int id;  // packet id for defragmentation
    private PacketType type;  // packet type
    private int offset;
    private int fragments;
    private byte[] data;

    public Packet(int id, PacketType type, int offset, int fragments, byte[] data) {
        this.id = id;
        this.type = type;
        this.offset = offset;
        this.fragments = fragments;
        this.data = data;
    }

    public Packet(byte[] data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public PacketType getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    public int getFragments() {
        return fragments;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "id=" + id +
                ", type=" + type +
                ", offset=" + offset +
                ", fragments=" + fragments +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    @Override
    public int compareTo(Packet p) {
        return this.offset - p.getOffset();
    }

}
