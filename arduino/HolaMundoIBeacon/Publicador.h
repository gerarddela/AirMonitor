#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

#include "Medidor.h"

class Publicador {

private:

  uint8_t beaconUUID[16] = { 
    'E', 'Q', 'U', 'I', 'P', 'O', '-', 'G', 
    'E', 'R', 'A', 'R', 'D', '-', '3', 'A' // EQUIPO-GERARD-3A
  };

  uint8_t contador = 1;
  const int RSSI = -53;

  Medidor& elMedidor;

public:

  EmisoraBLE laEmisora {
    "GTI-3A",
    0x004c,
    4 
  };

  enum MedicionesID {
    CO2 = 11,   
    TEMPERATURA = 12,
    RUIDO = 13 
  };

  Publicador(Medidor& medidor) : elMedidor(medidor) {}

  void encenderEmisora() {
    laEmisora.encenderEmisora();
  }

  void publicarCO2(int valorCO2, uint8_t cont, long tiempoEspera) {
    uint16_t major = (MedicionesID::CO2 << 8) + cont; 
    laEmisora.emitirAnuncioIBeacon(beaconUUID, major, valorCO2, RSSI);
    esperar(tiempoEspera);
    laEmisora.detenerAnuncio();
  }

  void publicarTemperatura(long tiempoEspera) {
    int16_t valorTemperatura = elMedidor.medirTemperatura();
    uint16_t major = (MedicionesID::TEMPERATURA << 8) + contador;
    laEmisora.emitirAnuncioIBeacon(beaconUUID, major, valorTemperatura, RSSI);
    esperar(tiempoEspera);
    laEmisora.detenerAnuncio();
    contador++;
  }

  void publicarMediciones() {
    int16_t co2 = elMedidor.medirCO2();
    int16_t temperatura = elMedidor.medirTemperatura();
    laEmisora.emitirAnuncioIBeacon(beaconUUID, MedicionesID::CO2, temperatura, RSSI);
    laEmisora.enviarDatos(temperatura, co2);
  }
};

#endif 