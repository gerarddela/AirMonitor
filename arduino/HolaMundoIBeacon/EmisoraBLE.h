// -*- mode: c++ -*-
// Archivo: Emisora.h
// Descripción: Este archivo define la clase EmisoraBLE, que gestiona la comunicación BLE
//               y el envío de datos a través de anuncios IBeacon.
// Diseño: La clase EmisoraBLE contiene métodos para encender la emisora, emitir anuncios,
//        manejar conexiones, y añadir servicios con características.
// Parámetros: La clase recibe parámetros en su constructor que se utilizan para configurar
//             la emisora BLE.

#ifndef EMISORA_H_INCLUIDO
#define EMISORA_H_INCLUIDO

#include "ServicioEnEmisora.h" // Inclusión del archivo de definición de ServicioEnEmisora
#include "PuertoSerie.h"       // Incluir el archivo donde se define Globales
#include <Arduino.h>           // Incluir la biblioteca Arduino

namespace Globales {
    extern PuertoSerie elPuerto; // Declarar elPuerto para que sea accesible en todo el programa
}

// Clase que gestiona la emisora BLE
class EmisoraBLE {
private:
    const char * nombreEmisora; // Nombre de la emisora
    const uint16_t fabricanteID; // ID del fabricante
    const int8_t txPower;        // Potencia de transmisión

public:
    // Tipos de callback para gestionar la conexión
    using CallbackConexionEstablecida = void(uint16_t connHandle);
    using CallbackConexionTerminada = void(uint16_t connHandle, uint8_t reason);

    // Constructor de la clase EmisoraBLE
    EmisoraBLE(const char * nombreEmisora_, const uint16_t fabricanteID_,
               const int8_t txPower_)
        : nombreEmisora(nombreEmisora_),      // Inicializa nombreEmisora
          fabricanteID(fabricanteID_),        // Inicializa fabricanteID
          txPower(txPower_) {}                 // Inicializa txPower

    // Encender la emisora
    void encenderEmisora() {
        Bluefruit.begin();                   // Inicializa el módulo Bluefruit
        (*this).detenerAnuncio();           // Detiene cualquier anuncio en curso
    }

    // Encender la emisora con callbacks para conexiones
    void encenderEmisora(CallbackConexionEstablecida cbce,
                         CallbackConexionTerminada cbct) {
        encenderEmisora();                   // Llama al método para encender la emisora
        instalarCallbackConexionEstablecida(cbce); // Instala el callback de conexión establecida
        instalarCallbackConexionTerminada(cbct);    // Instala el callback de conexión terminada
    }

    // Detener el anuncio si está activo
    void detenerAnuncio() {
        if ((*this).estaAnunciando()) {      // Verifica si está anunciando
            Bluefruit.Advertising.stop();    // Detiene el anuncio
        }
    }

    // Verificar si está anunciando
    bool estaAnunciando() {
        return Bluefruit.Advertising.isRunning(); // Devuelve verdadero si está activo
    }

    // Emitir un anuncio IBeacon
    void emitirAnuncioIBeacon(uint8_t * beaconUUID, int16_t major, int16_t minor, uint8_t rssi) {
        (*this).detenerAnuncio();            // Detiene cualquier anuncio en curso
        BLEBeacon elBeacon(beaconUUID, major, minor, rssi); // Crea un nuevo IBeacon
        elBeacon.setManufacturer((*this).fabricanteID); // Establece el ID del fabricante
        Bluefruit.setTxPower((*this).txPower); // Establece la potencia de transmisión
        Bluefruit.setName((*this).nombreEmisora); // Establece el nombre de la emisora
        Bluefruit.ScanResponse.addName();     // Añade el nombre a la respuesta de escaneo
        Bluefruit.Advertising.setBeacon(elBeacon); // Configura el anuncio como un IBeacon
        Bluefruit.Advertising.restartOnDisconnect(true); // Reinicia el anuncio al desconectar
        Bluefruit.Advertising.setInterval(100, 100); // Configura el intervalo de anuncios
        Bluefruit.Advertising.start(0);        // Inicia el anuncio
    }

    // Emitir un anuncio IBeacon libre
    void emitirAnuncioIBeaconLibre(const char * carga, const uint8_t tamanyoCarga) {
        (*this).detenerAnuncio();            // Detiene cualquier anuncio en curso
        Bluefruit.Advertising.clearData();   // Limpia los datos del anuncio
        Bluefruit.ScanResponse.clearData();   // Limpia los datos de la respuesta de escaneo
        Bluefruit.setName((*this).nombreEmisora); // Establece el nombre de la emisora
        Bluefruit.ScanResponse.addName();     // Añade el nombre a la respuesta de escaneo
        Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE); // Añade banderas

        // Preparar datos del anuncio IBeacon libre
        uint8_t restoPrefijoYCarga[4 + 21] = {
            0x4c, 0x00, // Prefijo de IBeacon
            0x02,       // Tipo de IBeacon
            21,         // Longitud de carga
            '-', '-', '-', '-', 
            '-', '-', '-', '-', 
            '-', '-', '-', '-', 
            '-', '-', '-', '-', 
            '-', '-', '-', '-', 
            '-'
        };

        // Copia la carga de datos al arreglo
        memcpy(&restoPrefijoYCarga[4], &carga[0], (tamanyoCarga > 21 ? 21 : tamanyoCarga));
        Bluefruit.Advertising.addData(BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA,
                                       &restoPrefijoYCarga[0],
                                       4 + 21); // Añade los datos al anuncio
        Bluefruit.Advertising.restartOnDisconnect(true); // Reinicia el anuncio al desconectar
        Bluefruit.Advertising.setInterval(100, 100); // Configura el intervalo de anuncios
        Bluefruit.Advertising.setFastTimeout(1); // Establece el tiempo de espera rápido
        Bluefruit.Advertising.start(0); // Inicia el anuncio
        Globales::elPuerto.escribir("emitiriBeacon libre  Bluefruit.Advertising.start(0);\n"); // Escribe en el puerto
    }

    // Añadir un servicio al anuncio
    bool anyadirServicio(ServicioEnEmisora & servicio) {
        Globales::elPuerto.escribir("Bluefruit.Advertising.addService(servicio);\n"); // Escribe en el puerto
        bool r = Bluefruit.Advertising.addService(servicio); // Añade el servicio
        if (!r) {
            Serial.println("SERVICION NO AÑADIDO\n"); // Mensaje de error si no se añade el servicio
        }
        return r; // Devuelve el resultado de la operación
    }

    // Añadir un servicio y sus características
    bool anyadirServicioConSusCaracteristicas(ServicioEnEmisora & servicio) {
        return (*this).anyadirServicio(servicio); // Llama al método para añadir servicio
    }

    // Plantilla para añadir un servicio con múltiples características
    template <typename ... T>
    bool anyadirServicioConSusCaracteristicas(ServicioEnEmisora & servicio,
                                               ServicioEnEmisora::Caracteristica & caracteristica,
                                               T& ... restoCaracteristicas) {
        servicio.anyadirCaracteristica(caracteristica); // Añade la característica al servicio
        return anyadirServicioConSusCaracteristicas(servicio, restoCaracteristicas...); // Llama recursivamente
    }

    // Plantilla para añadir un servicio con características y activarlo
    template <typename ... T>
    bool anyadirServicioConSusCaracteristicasYActivar(ServicioEnEmisora & servicio,
                                                       T& ... restoCaracteristicas) {
        bool r = anyadirServicioConSusCaracteristicas(servicio, restoCaracteristicas...); // Añade el servicio
        servicio.activarServicio(); // Activa el servicio
        return r; // Devuelve el resultado
    }

    // Instalar un callback para la conexión establecida
    void instalarCallbackConexionEstablecida(CallbackConexionEstablecida cb) {
        Bluefruit.Periph.setConnectCallback(cb); // Establece el callback
    }

    // Instalar un callback para la conexión terminada
    void instalarCallbackConexionTerminada(CallbackConexionTerminada cb) {
        Bluefruit.Periph.setDisconnectCallback(cb); // Establece el callback
    }

    // Obtener la conexión a través del manejador de conexión
    BLEConnection * getConexion(uint16_t connHandle) {
        return Bluefruit.Connection(connHandle); // Devuelve el objeto de conexión
    }
};

#endif // EMISORA_H_INCLUIDO
