import java.io.*;
import java.util.Arrays;

public class Packet implements Serializable {
    private int id;
    private int type;
    private String transfer_id;
    private int chunk;
    private byte[] data;

    public Packet(int id, int type, String transfer_id, int chunk, byte[] data) {
        this.id = id;
        this.type = type;
        this.transfer_id = transfer_id;
        this.chunk = chunk;
        this.data = data;
    }

    public Packet(byte[] data) {
        this.data = data;
    }


    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getTransfer_id() {
        return transfer_id;
    }

    public int getChunk() {
        return chunk;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "id=" + id +
                ", type=" + type +
                ", transfer_id='" + transfer_id + '\'' +
                ", chunk=" + chunk +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(this);

        return out.toByteArray();
    }

    public static Packet deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (Packet) is.readObject();
    }
}
