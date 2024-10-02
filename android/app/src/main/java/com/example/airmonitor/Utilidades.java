package com.example.airmonitor;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Utilidades {

    public static byte[] stringToBytes(String texto) {
        return texto.getBytes();
    }

    public static UUID stringToUUID(String uuid) {
        if (uuid.length() != 16) {
            throw new Error("stringUUID: string no tiene 16 caracteres ");
        }
        byte[] comoBytes = uuid.getBytes();

        String masSignificativo = uuid.substring(0, 8);
        String menosSignificativo = uuid.substring(8, 16);
        UUID res = new UUID(Utilidades.bytesToLong(masSignificativo.getBytes()), Utilidades.bytesToLong(menosSignificativo.getBytes()));

        return res;
    }

    public static String uuidToString(UUID uuid) {
        return bytesToString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    }

    public static String uuidToHexString(UUID uuid) {
        return bytesToHexString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    }

    public static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    public static byte[] dosLongToBytes(long masSignificativos, long menosSignificativos) {
        ByteBuffer buffer = ByteBuffer.allocate(2 * Long.BYTES);
        buffer.putLong(masSignificativos);
        buffer.putLong(menosSignificativos);
        return buffer.array();
    }

    public static int bytesToInt(byte[] bytes) {
        return new BigInteger(bytes).intValue();
    }

    public static long bytesToLong(byte[] bytes) {
        return new BigInteger(bytes).longValue();
    }

    public static int bytesToIntOK(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }

        if (bytes.length > 4) {
            throw new Error("demasiados bytes para pasar a int ");
        }
        int res = 0;

        for (byte b : bytes) {
            res = (res << 8) + (b & 0xFF);
        }

        if ((bytes[0] & 0x8) != 0) {
            res = -(~(byte) res) - 1;
        }

        return res;
    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static UUID bytesToUUID(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("El arreglo de bytes debe tener una longitud de 16");
        }
        long msb = 0;
        long lsb = 0;

        for (int i = 0; i < 8; i++) {
            msb <<= 8;
            msb |= (bytes[i] & 0xff);
        }

        for (int i = 8; i < 16; i++) {
            lsb <<= 8;
            lsb |= (bytes[i] & 0xff);
        }

        return new UUID(msb, lsb);
    }

    private static byte[] toByteArray(UUID uuid) {
        byte[] bytes = new byte[16];
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (mostSigBits >>> (8 * (7 - i)));
        }
        for (int i = 0; i < 8; i++) {
            bytes[8 + i] = (byte) (leastSigBits >>> (8 * (7 - i)));
        }
        return bytes;
    }
}
