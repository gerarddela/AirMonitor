package com.example.airmonitor;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Utilidades {

    // Convierte un String a un arreglo de bytes
    // @param texto: cadena de texto a convertir
    // @return: arreglo de bytes que representa el texto
    public static byte[] stringToBytes(String texto) {
        return texto.getBytes();
    }

    // Convierte un String en formato UUID a un objeto UUID
    // @param uuid: cadena de 16 caracteres que representa el UUID
    // @return: objeto UUID generado a partir del String
    // @throws Error: si la longitud del String no es 16 caracteres
    public static UUID stringToUUID(String uuid) {
        if (uuid.length() != 16) {
            throw new Error("stringUUID: string no tiene 16 caracteres ");
        }
        byte[] comoBytes = uuid.getBytes();
        String masSignificativo = uuid.substring(0, 8); // Obtiene la parte más significativa del UUID
        String menosSignificativo = uuid.substring(8, 16); // Obtiene la parte menos significativa del UUID
        UUID res = new UUID(Utilidades.bytesToLong(masSignificativo.getBytes()), Utilidades.bytesToLong(menosSignificativo.getBytes()));
        return res;
    }

    // Convierte un objeto UUID a una representación de cadena
    // @param uuid: objeto UUID a convertir
    // @return: cadena que representa el UUID
    public static String uuidToString(UUID uuid) {
        return bytesToString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    }

    // Convierte un objeto UUID a una representación hexadecimal
    // @param uuid: objeto UUID a convertir
    // @return: cadena en formato hexadecimal que representa el UUID
    public static String uuidToHexString(UUID uuid) {
        return bytesToHexString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    }

    // Convierte un arreglo de bytes a un String
    // @param bytes: arreglo de bytes a convertir
    // @return: cadena generada a partir del arreglo de bytes
    public static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return ""; // Retorna cadena vacía si el arreglo es nulo
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) b); // Convierte cada byte a su carácter correspondiente
        }
        return sb.toString();
    }

    // Convierte dos long en un arreglo de bytes
    // @param masSignificativos: parte más significativa
    // @param menosSignificativos: parte menos significativa
    // @return: arreglo de bytes que representa ambos long
    public static byte[] dosLongToBytes(long masSignificativos, long menosSignificativos) {
        ByteBuffer buffer = ByteBuffer.allocate(2 * Long.BYTES);
        buffer.putLong(masSignificativos); // Almacena el long más significativo
        buffer.putLong(menosSignificativos); // Almacena el long menos significativo
        return buffer.array(); // Retorna el arreglo de bytes
    }

    // Convierte un arreglo de bytes a un entero
    // @param bytes: arreglo de bytes a convertir
    // @return: entero que representa el arreglo de bytes
    public static int bytesToInt(byte[] bytes) {
        return new BigInteger(bytes).intValue(); // Convierte y retorna el valor entero
    }

    // Convierte un arreglo de bytes a un long
    // @param bytes: arreglo de bytes a convertir
    // @return: long que representa el arreglo de bytes
    public static long bytesToLong(byte[] bytes) {
        return new BigInteger(bytes).longValue(); // Convierte y retorna el valor long
    }

    // Convierte un arreglo de bytes a un objeto UUID
    // @param bytes: arreglo de bytes que representa un UUID
    // @return: objeto UUID generado a partir del arreglo de bytes
    // @throws IllegalArgumentException: si el arreglo de bytes no tiene una longitud de 16
    public static UUID bytesToUUID(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("El arreglo de bytes debe tener una longitud de 16");
        }
        long msb = 0; // Parte más significativa
        long lsb = 0; // Parte menos significativa

        // Convierte los primeros 8 bytes a la parte más significativa
        for (int i = 0; i < 8; i++) {
            msb <<= 8;
            msb |= (bytes[i] & 0xff);
        }

        // Convierte los últimos 8 bytes a la parte menos significativa
        for (int i = 8; i < 16; i++) {
            lsb <<= 8;
            lsb |= (bytes[i] & 0xff);
        }

        return new UUID(msb, lsb); // Retorna el objeto UUID
    }

    // Convierte un arreglo de bytes a un entero con manejo de errores
    // @param bytes: arreglo de bytes a convertir
    // @return: entero resultante de la conversión
    // @throws Error: si el arreglo tiene más de 4 bytes
    public static int bytesToIntOK(byte[] bytes) {
        if (bytes == null) {
            return 0; // Retorna 0 si el arreglo es nulo
        }

        if (bytes.length > 4) {
            throw new Error("demasiados bytes para pasar a int "); // Lanza error si hay demasiados bytes
        }
        int res = 0; // Variable para almacenar el resultado

        // Convierte cada byte a un entero
        for (byte b : bytes) {
            res = (res << 8) + (b & 0xFF);
        }

        // Maneja el signo del entero
        if ((bytes[0] & 0x8) != 0) {
            res = -(~(byte) res) - 1; // Ajusta el valor si el primer byte indica un número negativo
        }

        return res; // Retorna el entero resultante
    }

    // Convierte un arreglo de bytes a una representación hexadecimal
    // @param bytes: arreglo de bytes a convertir
    // @return: cadena en formato hexadecimal que representa el arreglo de bytes
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return ""; // Retorna cadena vacía si el arreglo es nulo
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b)); // Formatea cada byte como hexadecimal
            sb.append(':'); // Añade separador
        }
        return sb.toString(); // Retorna la cadena en formato hexadecimal
    }
}
