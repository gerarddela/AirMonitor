// Archivo: ServicioEnEmisora.h
// Descripción: Este archivo define la clase ServicioEnEmisora, que gestiona la creación y activación
//               de un servicio BLE (Bluetooth Low Energy) y sus características asociadas. Permite
//               añadir características, activar el servicio y gestionar la escritura de datos.
// Diseño: Utiliza la biblioteca BLEService y BLECharacteristic para interactuar con la comunicación
//        BLE. Las características se almacenan en un vector para facilitar su gestión y activación.
// Parámetros: Contiene funciones para invertir arreglos y convertir cadenas en arreglos de uint8_t
//             en orden inverso para su uso en la comunicación BLE.

#ifndef SERVICIO_EMISORA_H_INCLUIDO
#define SERVICIO_EMISORA_H_INCLUIDO

#include <vector>
#include <BLEService.h>
#include <BLECharacteristic.h>

// Función plantilla que invierte el orden de los elementos en un arreglo
// Descripción: Esta función toma un puntero a un arreglo y su tamaño, y 
//              invierte el orden de sus elementos.
// Parámetros:
// - p: Puntero al arreglo de tipo T que se va a invertir.
// - n: Número de elementos en el arreglo.
// Retorno: Puntero al arreglo invertido.
template<typename T>
T* alReves(T* p, int n) {
    T aux;
    for (int i = 0; i < n / 2; i++) {
        aux = p[i];
        p[i] = p[n - i - 1];
        p[n - i - 1] = aux;
    }
    return p;
}

// Función que convierte una cadena de caracteres a un arreglo de uint8_t en orden inverso
// Descripción: Toma una cadena y la copia en un arreglo de uint8_t en orden inverso hasta un tamaño máximo.
// Parámetros:
// - pString: Puntero a la cadena de caracteres que se convertirá.
// - pUint: Puntero al arreglo uint8_t donde se almacenará la conversión.
// - tamMax: Tamaño máximo del arreglo donde se almacenará la cadena.
// Retorno: Puntero al arreglo uint8_t con la cadena invertida.
uint8_t* stringAUint8AlReves(const char* pString, uint8_t* pUint, int tamMax) {
    int longitudString = strlen(pString); // Longitud de la cadena de entrada
    int longitudCopiar = (longitudString > tamMax ? tamMax : longitudString); // Longitud a copiar
    for (int i = 0; i <= longitudCopiar - 1; i++) {
        pUint[tamMax - i - 1] = pString[i]; // Copia la cadena en orden inverso
    }
    return pUint; // Retorna el puntero al arreglo uint8_t
}

// Clase que representa un servicio en el dispositivo emisor BLE
class ServicioEnEmisora {
public:
    // Tipo de callback para manejar la escritura en características
    using CallbackCaracteristicaEscrita = void(uint16_t conn_handle, BLECharacteristic* chr, uint8_t* data, uint16_t len);

    // Clase interna que representa una característica dentro del servicio
    class Caracteristica {
    private:
        uint8_t uuidCaracteristica[16] = { // UUID de la característica en formato invertido
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        BLECharacteristic laCaracteristica; // Objeto BLECharacteristic asociado

    public:
        // Constructor de la clase Caracteristica
        // Descripción: Inicializa la característica con un nombre y genera su UUID en orden inverso.
        // Parámetros:
        // - nombreCaracteristica_: Nombre de la característica a inicializar.
        Caracteristica(const char* nombreCaracteristica_)
            : laCaracteristica(stringAUint8AlReves(nombreCaracteristica_, &uuidCaracteristica[0], 16)) {}

        // Método para activar la característica
        // Descripción: Inicia la característica BLE y muestra cualquier error en la activación.
        void activar() {
            err_t error = laCaracteristica.begin(); // Inicia la característica
            Serial.print("Error activating characteristic: ");
            Serial.println(error); // Muestra el error si ocurre
        }

        // Método para escribir datos en la característica
        // Descripción: Escribe una cadena en la característica BLE.
        // Parámetros:
        // - str: Cadena a escribir en la característica.
        // Retorno: Resultado de la escritura.
        uint16_t escribirDatos(const char* str) {
            return laCaracteristica.write(str); // Escribe la cadena en la característica
        }

        // Método para instalar un callback para manejar escrituras en la característica
        // Descripción: Establece una función callback que se llamará cuando se escriban datos en la característica.
        // Parámetros:
        // - cb: Función callback que maneja la escritura de datos.
        void instalarCallbackCaracteristicaEscrita(CallbackCaracteristicaEscrita cb) {
            laCaracteristica.setWriteCallback(cb); // Establece el callback
        }
    };

private:
    uint8_t uuidServicio[16] = { // UUID del servicio en formato invertido
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    BLEService elServicio; // Objeto BLEService asociado
    std::vector<Caracteristica*> lasCaracteristicas; // Vector para almacenar características
    bool initialized = false; // Estado de inicialización del servicio

public:
    // Constructor de la clase ServicioEnEmisora
    // Descripción: Inicializa el servicio con un nombre y genera su UUID en orden inverso.
    // Parámetros:
    // - nombreServicio_: Nombre del servicio a inicializar.
    ServicioEnEmisora(const char* nombreServicio_)
        : elServicio(stringAUint8AlReves(nombreServicio_, &uuidServicio[0], 16)) {}

    // Método para añadir una característica al servicio
    // Descripción: Agrega una característica a la lista de características del servicio.
    // Parámetros:
    // - car: Referencia a la característica que se desea añadir.
    void anyadirCaracteristica(Caracteristica& car) {
        lasCaracteristicas.push_back(&car); // Añade la característica al vector
    }

    // Método para activar el servicio
    // Descripción: Inicia el servicio BLE y activa todas las características asociadas.
    void activarServicio() {
        err_t error = elServicio.begin(); // Inicia el servicio
        Serial.print("Error activating service: ");
        Serial.println(error); // Muestra el error si ocurre
        initialized = (error == NRF_SUCCESS); // Actualiza el estado de inicialización

        for (auto pCar : lasCaracteristicas) {
            pCar->activar(); // Activa cada característica
        }
    }

    // Método para añadir el servicio a la publicidad BLE
    // Descripción: Agrega el servicio al objeto de publicidad si está inicializado.
    // Parámetros:
    // - advertising: Objeto de publicidad BLE donde se añadirá el servicio.
    void anyadirServicio(BLEAdvertising& advertising) {
        if (isInitialized()) {
            advertising.addService(elServicio); // Añade el servicio si está inicializado
        } else {
            Serial.println("Error: Service is not initialized."); // Mensaje de error si no está inicializado
        }
    }

    // Método para verificar si el servicio ha sido inicializado
    // Descripción: Devuelve el estado de inicialización del servicio.
    // Retorno: true si el servicio está inicializado, false en caso contrario.
    bool isInitialized() const {
        return initialized; // Retorna el estado de inicialización
    }

    // Operador de conversión para acceder al objeto BLEService
    // Descripción: Permite tratar la instancia de ServicioEnEmisora como un BLEService.
    // Retorno: Referencia al objeto BLEService asociado.
    operator BLEService&() {
        return elServicio; // Retorna el objeto BLEService
    }
};

#endif // SERVICIO_EMISORA_H_INCLUIDO
