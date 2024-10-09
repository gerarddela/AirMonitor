// -*- mode: c++ -*-
#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

#include "EmisoraBLE.h"

class Publicador {

private:
    uint8_t beaconUUID[16] = { 
        'E', 'Q', 'U', 'I', 'P', 'O', '-', 'G', 
        'E', 'R', 'A', 'R', 'D', '-', '3', 'A' // EQUIPO-GERARD-3A
    };

public:
    EmisoraBLE laEmisora {
        "GTI-3A",   
        0x004c,     
        4           
    };

    const int RSSI = -53;

    enum MedicionesID {
        CO2 = 11,         
        TEMPERATURA = 12, 
        RUIDO = 13        
    };

    Publicador() {
    }

    void encenderEmisora() {
        laEmisora.encenderEmisora();
    }

    void publicarCO2(int16_t valorCO2, uint8_t contador, long tiempoEspera) {
        uint16_t major = MedicionesID::CO2;
        laEmisora.emitirAnuncioIBeacon(beaconUUID, major, valorCO2, RSSI);
        esperar(tiempoEspera);
        laEmisora.detenerAnuncio();
    }

    void publicarTemperatura(int16_t valorTemperatura, uint8_t contador, long tiempoEspera) {
        uint16_t major = MedicionesID::TEMPERATURA;
        laEmisora.emitirAnuncioIBeacon(beaconUUID, major, valorTemperatura, RSSI);
        esperar(tiempoEspera);
        laEmisora.detenerAnuncio();
    }

    
}; // class Publicador

#endif // PUBLICADOR_H_INCLUIDO
