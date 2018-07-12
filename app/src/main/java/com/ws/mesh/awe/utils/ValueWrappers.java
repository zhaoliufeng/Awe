package com.ws.mesh.awe.utils;

public final class ValueWrappers {
    public final static class Bool {
        public boolean value = false;

        public Bool(boolean v) {
            value = v;
        }
    }

    public final static class Int8 {
        public byte value = 0;

        public Int8(byte v) {
            value = v;
        }
    }

    public final static class Int16 {
        public short value = 0;

        public Int16(short v) {
            value = v;
        }
    }

    public final static class Int32 {
        public int value = 0;

        public Int32(int v) {
            value = v;
        }
    }

    public final static class Int64 {
        public long value = 0;

        public Int64(long v) {
            value = v;
        }
    }

    public static short Unsigned(byte v) {
        return (short) (v & 0xFF);
    }

    public static int Unsigned(short v) {
        return (int) (v & 0xFFFF);
    }

    public static long Unsigned(int v) {
        return v & 0xFFFFFFFFL;
    }

    public static byte Shink(byte to, long from) {
        return (byte) (0xFF & from);
    }

    public static short Shink(short to, long from) {
        return (short) (0xFFFF & from);
    }

    public static int Shink(int to, long from) {
        return (int) (0xFFFFFFFF & from);
    }

    public static byte Shink(byte to, double from) {
        return (byte) (0xFF * from);
    }

    public static short Shink(short to, double from) {
        return (short) (0xFFFF * from);
    }

    public static int Shink(int to, double from) {
        return (int) (0xFFFFFFFF * from);
    }

//	public static byte     UByte(int v)                { return (byte)(v & 0xFF); }
}
