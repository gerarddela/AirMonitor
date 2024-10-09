package com.example.airmonitor;

import java.util.Arrays;

public class TramaIBeacon {
    private byte[] prefijo = null; // Prefijo de la trama iBeacon
    private byte[] uuid = null; // Identificador único del dispositivo
    private byte[] major = null; // Valor mayor que se utiliza para identificar grupos de dispositivos
    private byte[] minor = null; // Valor menor que se utiliza para identificar dispositivos individuales
    private byte txPower = 0; // Potencia de transmisión
    private byte[] losBytes; // Bytes de la trama completa
    private byte[] advFlags = null; // Banderas de publicidad
    private byte[] advHeader = null; // Encabezado de publicidad
    private byte[] companyID = new byte[2]; // Identificador de la compañía
    private byte iBeaconType = 0; // Tipo de iBeacon
    private byte iBeaconLength = 0; // Longitud del iBeacon
    private boolean noadvFlags; // Indica si hay banderas de publicidad presentes

    // Métodos de acceso (getters)
    public byte[] getPrefijo() { return prefijo; } // Devuelve el prefijo
    public byte[] getUUID() { return uuid; } // Devuelve el UUID
    public byte[] getMajor() { return major; } // Devuelve el valor mayor
    public byte[] getMinor() { return minor; } // Devuelve el valor menor
    public byte getTxPower() { return txPower; } // Devuelve la potencia de transmisión
    public byte[] getLosBytes() { return losBytes; } // Devuelve los bytes de la trama
    public byte[] getAdvFlags() { return advFlags; } // Devuelve las banderas de publicidad
    public byte[] getAdvHeader() { return advHeader; } // Devuelve el encabezado de publicidad
    public byte[] getCompanyID() { return companyID; } // Devuelve el identificador de la compañía
    public byte getiBeaconType() { return iBeaconType; } // Devuelve el tipo de iBeacon
    public byte getiBeaconLength() { return iBeaconLength; } // Devuelve la longitud del iBeacon

    // Métodos de modificación (setters)
    public void setMajor(byte[] major) { this.major = major; } // Establece el valor mayor
    public void setMinor(byte[] minor) { this.minor = minor; } // Establece el valor menor

    // Constructor de la clase TramaIBeacon
    public TramaIBeacon(byte[] bytes) {
        this.losBytes = bytes; // Inicializa los bytes de la trama
        // Verifica si las banderas de publicidad están presentes
        noadvFlags = !(losBytes[0] == 02 && losBytes[1] == 01 && losBytes[2] == 06);

        // Procesa los bytes dependiendo de la presencia de banderas de publicidad
        if (noadvFlags) {
            prefijo = Arrays.copyOfRange(losBytes, 0, 6); // Extrae el prefijo
            uuid = Arrays.copyOfRange(losBytes, 6, 22); // Extrae el UUID
            major = Arrays.copyOfRange(losBytes, 22, 24); // Extrae el valor mayor
            minor = Arrays.copyOfRange(losBytes, 24, 26); // Extrae el valor menor
            txPower = losBytes[26]; // Extrae la potencia de transmisión
            advHeader = Arrays.copyOfRange(prefijo, 0, 2); // Extrae el encabezado de publicidad
            companyID = Arrays.copyOfRange(prefijo, 2, 4); // Extrae el identificador de la compañía
            iBeaconType = prefijo[4]; // Extrae el tipo de iBeacon
            iBeaconLength = prefijo[5]; // Extrae la longitud del iBeacon
        } else {
            prefijo = Arrays.copyOfRange(losBytes, 0, 9); // Extrae el prefijo alternativo
            uuid = Arrays.copyOfRange(losBytes, 9, 25); // Extrae el UUID alternativo
            major = Arrays.copyOfRange(losBytes, 25, 27); // Extrae el valor mayor alternativo
            minor = Arrays.copyOfRange(losBytes, 27, 29); // Extrae el valor menor alternativo
            txPower = losBytes[29]; // Extrae la potencia de transmisión alternativo
            advFlags = Arrays.copyOfRange(prefijo, 0, 3); // Extrae las banderas de publicidad
            advHeader = Arrays.copyOfRange(prefijo, 3, 5); // Extrae el encabezado de publicidad alternativo
            companyID = Arrays.copyOfRange(prefijo, 5, 7); // Extrae el identificador de la compañía alternativo
            iBeaconType = prefijo[7]; // Extrae el tipo de iBeacon alternativo
            iBeaconLength = prefijo[8]; // Extrae la longitud del iBeacon alternativo
        }
    }
}
