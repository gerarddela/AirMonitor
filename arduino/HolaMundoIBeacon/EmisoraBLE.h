#ifndef EMISORA_H_INCLUIDO
#define EMISORA_H_INCLUIDO

#include "ServicioEnEmisora.h"

class EmisoraBLE {
private:
    const char *nombreEmisora;
    const uint16_t fabricanteID;
    const int8_t txPower;

public:
    using CallbackConexionEstablecida = void(uint16_t connHandle);
    using CallbackConexionTerminada = void(uint16_t connHandle, uint8_t reason);

    EmisoraBLE(const char *nombreEmisora_, const uint16_t fabricanteID_, const int8_t txPower_)
        : nombreEmisora(nombreEmisora_), fabricanteID(fabricanteID_), txPower(txPower_) {}

    void encenderEmisora() {
        Bluefruit.begin();
        detenerAnuncio();
    }

    void encenderEmisora(CallbackConexionEstablecida cbce, CallbackConexionTerminada cbct) {
        encenderEmisora();
        instalarCallbackConexionEstablecida(cbce);
        instalarCallbackConexionTerminada(cbct);
    }

    void detenerAnuncio() {
        if (estaAnunciando()) {
            Bluefruit.Advertising.stop();
        }
    }

    bool estaAnunciando() {
        return Bluefruit.Advertising.isRunning();
    }

    void emitirAnuncioIBeacon(uint8_t *beaconUUID, int16_t major, int16_t minor, uint8_t rssi) {
        detenerAnuncio();
        BLEBeacon elBeacon(beaconUUID, major, minor, rssi);
        elBeacon.setManufacturer(fabricanteID);
        Bluefruit.setTxPower(txPower);
        Bluefruit.setName(nombreEmisora);
        Bluefruit.ScanResponse.addName();
        Bluefruit.Advertising.setBeacon(elBeacon);
        Bluefruit.Advertising.restartOnDisconnect(true);
        Bluefruit.Advertising.setInterval(100, 100);
        Bluefruit.Advertising.start(0);

        Globales::elPuerto.escribir("Major: ");
        Globales::elPuerto.escribir(major);
        Globales::elPuerto.escribir(", Minor: ");
        Globales::elPuerto.escribir(minor);
        Globales::elPuerto.escribir("\n");
    }

    void emitirAnuncioIBeaconLibre(const char *carga, const uint8_t tamanyoCarga) {
        detenerAnuncio();
        Bluefruit.Advertising.clearData();
        Bluefruit.ScanResponse.clearData();
        Bluefruit.setName(nombreEmisora);
        Bluefruit.ScanResponse.addName();
        Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);

        uint8_t restoPrefijoYCarga[4 + 21] = {
            0x4c, 0x00, 0x02, 21, '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'
        };

        memcpy(&restoPrefijoYCarga[4], carga, (tamanyoCarga > 21 ? 21 : tamanyoCarga));
        Bluefruit.Advertising.addData(BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA,
                                      &restoPrefijoYCarga[0],
                                      4 + 21);

        Bluefruit.Advertising.restartOnDisconnect(true);
        Bluefruit.Advertising.setInterval(100, 100);
        Bluefruit.Advertising.setFastTimeout(1);
        Bluefruit.Advertising.start(0);

        Globales::elPuerto.escribir("emitiriBeacon libre Bluefruit.Advertising.start(0);\n");
    }

    void enviarDatos(float temperatura, float CO2) {
        String data = String(temperatura) + "," + String(CO2);
        Globales::elPuerto.escribir(data.c_str());
    }

    void instalarCallbackConexionEstablecida(CallbackConexionEstablecida cbce) {
        Bluefruit.Periph.setConnectCallback(cbce);
    }

    void instalarCallbackConexionTerminada(CallbackConexionTerminada cbct) {
        Bluefruit.Periph.setDisconnectCallback(cbct);
    }

    BLEConnection *getConexion(uint16_t connHandle) {
        return Bluefruit.Connection(connHandle);
    }

    bool anyadirServicio(ServicioEnEmisora &servicio) {
        Globales::elPuerto.escribir(" Bluefruit.Advertising.addService( servicio );\n");
        bool r = Bluefruit.Advertising.addService(servicio);

        if (!r) {
            Serial.println(" SERVICION NO AÑADIDO \n");
        }

        return r;
    }

    bool anyadirServicioConSusCaracteristicas(ServicioEnEmisora &servicio) {
        return anyadirServicio(servicio);
    }

    template <typename... T>
    bool anyadirServicioConSusCaracteristicas(ServicioEnEmisora &servicio,
                                              ServicioEnEmisora::Caracteristica &caracteristica,
                                              T &...restoCaracteristicas) {
        servicio.anyadirCaracteristica(caracteristica);
        return anyadirServicioConSusCaracteristicas(servicio, restoCaracteristicas...);
    }

    template <typename... T>
    bool anyadirServicioConSusCaracteristicasYActivar(ServicioEnEmisora &servicio, T &...restoCaracteristicas) {
        bool r = anyadirServicioConSusCaracteristicas(servicio, restoCaracteristicas...);
        servicio.activarServicio();
        return r;
    }
};

#endif
