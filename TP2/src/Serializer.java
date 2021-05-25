import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Serializer {

    public static byte[] Serialize_String(String str) {
        return str.getBytes();
    }

    public static String Deserialize_String(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] Serialize_Int(int x) {
        return ByteBuffer.allocate(4).putInt(x).array();
    }

    public static int Deserialize_Int(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static byte[] Serialize_Packet(Packet packet) {
        byte[] id = Serialize_Int(packet.getId());
        byte[] type = Serialize_Int(packet.getType().ordinal());
        byte[] offset = Serialize_Int(packet.getOffset());
        byte[] fragments = Serialize_Int(packet.getFragments());
        byte[] data = packet.getData();

        int data_length = data == null ? 0 : data.length;

        ByteBuffer buffer = ByteBuffer.wrap(new byte[Packet.Header_Size + data_length]);
        buffer.put(id);
        buffer.put(type);
        buffer.put(offset);
        buffer.put(fragments);
        if (data != null) buffer.put(data);

        return buffer.array();
    }

    public static Packet Deserialize_Packet(byte[] bytes) {
        byte[] id = Arrays.copyOfRange(bytes, 0, 4);
        byte[] type = Arrays.copyOfRange(bytes, 4, 8);
        byte[] offset = Arrays.copyOfRange(bytes, 8, 12);
        byte[] fragments = Arrays.copyOfRange(bytes, 12, 16);
        byte[] data = Arrays.copyOfRange(bytes, 16, bytes.length);

        return new Packet(Deserialize_Int(id), PacketType.values()[Deserialize_Int(type)], Deserialize_Int(offset),
                        Deserialize_Int(fragments), data);
    }
}
