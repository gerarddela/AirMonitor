// -*- mode: c++ -*-
// Archivo: Publicador.h
// Descripción: Este archivo define la clase Publicador, que gestiona la emisión de anuncios IBeacon 
//               para publicar mediciones de CO2 y temperatura a través de la clase EmisoraBLE.
// Diseño: La clase Publicador utiliza un UUID de beacon fijo y un RSSI constante para identificar 
//        el dispositivo. Los métodos permiten encender la emisora y publicar mediciones específicas.
// Parámetros: No se reciben parámetros en el constructor, ya que se utilizan valores predeterminados.

#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

#include "EmisoraBLE.h" // Incluir la definición de la clase EmisoraBLE

// Clase que representa un publicador de datos de mediciones
class Publicador {
private:
    // UUID del beacon utilizado para la publicación
    uint8_t beaconUUID[16] = { 
        'E', 'Q', 'U', 'I', 'P', 'O', '-', 'G', 
        'E', 'R', 'A', 'R', 'D', '-', '3', 'A' // EQUIPO-GERARD-3A
    };

public:
    // Instancia de la clase EmisoraBLE con nombre, ID de fabricante y potencia de transmisión
    EmisoraBLE laEmisora {
        "GTI-3A",   // Nombre de la emisora
        0x004c,     // ID del fabricante
        4           // Potencia de transmisión
    };

    const int RSSI = -53; // Valor RSSI fijo para las emisiones

    // Enumeración para identificar tipos de mediciones
    enum MedicionesID {
        CO2 = 11,         // ID para la medición de CO2
        TEMPERATURA = 12, // ID para la medición de temperatura
        RUIDO = 13        // ID para la medición de ruido (si se implementa)
    };

    // Constructor de la clase Publicador
    Publicador() {
    }

    // Método para encender la emisora
    // Descripción: Llama al método para encender la emisora BLE
    void encenderEmisora() {
        laEmisora.encenderEmisora(); // Enciende la emisora
    }

    // Método para publicar mediciones de CO2
    // Descripción: Emite un anuncio IBeacon con el valor de CO2 medido
    // Parámetros:
    // - valorCO2: Valor de CO2 a publicar (tipo int16_t)
    // - contador: Contador para la publicación (tipo uint8_t)
    // - tiempoEspera: Tiempo en milisegundos para esperar antes de detener el anuncio (tipo long)
    void publicarCO2(int16_t valorCO2, uint8_t contador, long tiempoEspera) {
        uint16_t major = MedicionesID::CO2; // Asignar ID de medición para CO2
        laEmisora.emitirAnuncioIBeacon(beaconUUID, major, valorCO2, RSSI); // Emitir anuncio
        esperar(tiempoEspera); // Esperar el tiempo especificado
        laEmisora.detenerAnuncio(); // Detener el anuncio
    }

    // Método para publicar mediciones de temperatura
    // Descripción: Emite un anuncio IBeacon con el valor de temperatura medido
    // Parámetros:
    // - valorTemperatura: Valor de temperatura a publicar (tipo int16_t)
    // - contador: Contador para la publicación (tipo uint8_t)
    // - tiempoEspera: Tiempo en milisegundos para esperar antes de detener el anuncio (tipo long)
    void publicarTemperatura(int16_t valorTemperatura, uint8_t contador, long tiempoEspera) {
        uint16_t major = MedicionesID::TEMPERATURA; // Asignar ID de medición para temperatura
        laEmisora.emitirAnuncioIBeacon(beaconUUID, major, valorTemperatura, RSSI); // Emitir anuncio
        esperar(tiempoEspera); // Esperar el tiempo especificado
        laEmisora.detenerAnuncio(); // Detener el anuncio
    }
}; // class Publicador

#endif // PUBLICADOR_H_INCLUIDO
