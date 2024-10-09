package com.example.airmonitor;

import java.util.Arrays;

public class TramaIBeacon {
    private byte[] prefijo = null;
    private byte[] uuid = null;
    private byte[] major = null;
    private byte[] minor = null;
    private byte txPower = 0;
    private byte[] losBytes;

    private byte[] advFlags = null;
    private byte[] advHeader = null;
    private byte[] companyID = new byte[2];
    private byte iBeaconType = 0;
    private byte iBeaconLength = 0;

    private boolean noadvFlags;

    public byte[] getPrefijo() { return prefijo; }
    public byte[] getUUID() { return uuid; }
    public byte[] getMajor() { return major; }
    public byte[] getMinor() { return minor; }
    public byte getTxPower() { return txPower; }
    public byte[] getLosBytes() { return losBytes; }
    public byte[] getAdvFlags() { return advFlags; }
    public byte[] getAdvHeader() { return advHeader; }
    public byte[] getCompanyID() { return companyID; }
    public byte getiBeaconType() { return iBeaconType; }
    public byte getiBeaconLength() { return iBeaconLength; }

    public TramaIBeacon(byte[] bytes) {
        this.losBytes = bytes;

        if (losBytes.length < 30) {
            throw new IllegalArgumentException("losBytes debe tener al menos 30 bytes");
        }

        noadvFlags = !(losBytes[0] == 0x02 && losBytes[1] == 0x01 && losBytes[2] == 0x06);

        if (noadvFlags) {
            prefijo = Arrays.copyOfRange(losBytes, 0, 6);
            uuid = Arrays.copyOfRange(losBytes, 6, 22);
            major = Arrays.copyOfRange(losBytes, 22, 24);
            minor = Arrays.copyOfRange(losBytes, 24, 26);
            txPower = losBytes[26];

            advHeader = Arrays.copyOfRange(prefijo, 0, 2);
            companyID = Arrays.copyOfRange(prefijo, 2, 4);
            iBeaconType = prefijo[4];
            iBeaconLength = prefijo[5];
        } else {
            prefijo = Arrays.copyOfRange(losBytes, 0, 9);
            uuid = Arrays.copyOfRange(losBytes, 9, 25);
            major = Arrays.copyOfRange(losBytes, 25, 27);
            minor = Arrays.copyOfRange(losBytes, 27, 29);
            txPower = losBytes[29];

            advFlags = Arrays.copyOfRange(prefijo, 0, 3);
            advHeader = Arrays.copyOfRange(prefijo, 3, 5);
            companyID = Arrays.copyOfRange(prefijo, 5, 7);
            iBeaconType = prefijo[7];
            iBeaconLength = prefijo[8];
        }
    }

}
