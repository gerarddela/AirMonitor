#ifndef EMISORA_H_INCLUIDO
#define EMISORA_H_INCLUIDO

#include "ServicioEnEmisora.h"

class EmisoraBLE {
  
private:
  const char * nombreEmisora;
  const uint16_t fabricanteID;
  const int8_t txPower;

public:
  void enviarDatos(float temperatura, float CO2) {
    String data = String(temperatura) + "," + String(CO2); // Corrigido aquí
    Globales::elPuerto.escribir(data.c_str());
  }

  using CallbackConexionEstablecida = void (uint16_t connHandle);
  using CallbackConexionTerminada = void (uint16_t connHandle, uint8_t reason);

  EmisoraBLE(const char * nombreEmisora_, const uint16_t fabricanteID_, const int8_t txPower_)
    : nombreEmisora(nombreEmisora_), fabricanteID(fabricanteID_), txPower(txPower_) {}

  void encenderEmisora() {
    Bluefruit.begin(); 
    detenerAnuncio();
  }

  void encenderEmisora(CallbackConexionEstablecida cbce, CallbackConexionTerminada cbct) {
    encenderEmisora();
    instalarCallbackConexionEstablecida(cbce); // Asegúrate de tener esta función declarada
    instalarCallbackConexionTerminada(cbct); // Asegúrate de tener esta función declarada
  }

  void detenerAnuncio() {
    if (estaAnunciando()) {
      Bluefruit.Advertising.stop(); 
    }
  }

  bool estaAnunciando() {
    return Bluefruit.Advertising.isRunning();
  }

  void emitirAnuncioIBeacon(uint8_t * beaconUUID, int16_t major, int16_t minor, uint8_t rssi) {
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
  }

  void emitirAnuncioIBeaconLibre(const char * carga, const uint8_t tamanyoCarga) {
    detenerAnuncio(); 
    Bluefruit.Advertising.clearData();
    Bluefruit.ScanResponse.clearData();
    Bluefruit.setName(nombreEmisora);
    Bluefruit.ScanResponse.addName();
    Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);

    uint8_t restoPrefijoYCarga[4 + 21] = {
      0x4c, 0x00,
      0x02,
      21,
      '-', '-', '-', '-', 
      '-', '-', '-', '-', 
      '-', '-', '-', '-', 
      '-', '-', '-', '-', 
      '-', '-', '-', '-'
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

  // Declaraciones de las funciones que necesitas
  void instalarCallbackConexionEstablecida(CallbackConexionEstablecida cbce);
  void instalarCallbackConexionTerminada(CallbackConexionTerminada cbct);

  // ... (resto del código sin cambios)
};

#endif
