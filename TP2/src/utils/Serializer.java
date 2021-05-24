package utils;

import java.nio.ByteBuffer;

public class Serializer {

    public static byte[] Serialize_String(String str) {
        return str.getBytes();
    }

    public static String Deserialize_String(byte[] bytes) {
        int size = 0;

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0) {
                size = i;
                break;
            }
        }

        return new String(bytes, 0, size);
    }

    public static byte[] Serialize_Int(int x) {
        return ByteBuffer.allocate(4).putInt(x).array();
    }

    public static int Deserialize_Int(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }
}
