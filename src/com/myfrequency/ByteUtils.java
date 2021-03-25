package com.myfrequency;

import java.nio.ByteBuffer;

public class ByteUtils {
    private final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    //float arraya zrobić jakoś z tego
    public static float bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getFloat();
    }
}
