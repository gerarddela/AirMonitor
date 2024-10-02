#ifndef SERVICIO_EMISORA_H_INCLUIDO
#define SERVICIO_EMISORA_H_INCLUIDO

#include <vector>
#include <BLEService.h>
#include <BLECharacteristic.h>

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

uint8_t* stringAUint8AlReves(const char* pString, uint8_t* pUint, int tamMax) {
    int longitudString = strlen(pString);
    int longitudCopiar = (longitudString > tamMax ? tamMax : longitudString);
    for (int i = 0; i <= longitudCopiar - 1; i++) {
        pUint[tamMax - i - 1] = pString[i];
    }
    return pUint;
}

class ServicioEnEmisora {
public:
    using CallbackCaracteristicaEscrita = void(uint16_t conn_handle,
        BLECharacteristic* chr,
        uint8_t* data, uint16_t len);

    class Caracteristica {
    private:
        uint8_t uuidCaracteristica[16] = { /* UUID reversed */ };
        BLECharacteristic laCaracteristica;

    public:
        Caracteristica(const char* nombreCaracteristica_)
            : laCaracteristica(stringAUint8AlReves(nombreCaracteristica_, &uuidCaracteristica[0], 16)) { }

        void activar() {
            err_t error = laCaracteristica.begin();
            Serial.print("Error activating characteristic: ");
            Serial.println(error);
        }
    };

private:
    uint8_t uuidServicio[16] = { /* UUID reversed */ };
    BLEService elServicio;
    std::vector<Caracteristica*> lasCaracteristicas;
    bool initialized = false;

public:
    ServicioEnEmisora(const char* nombreServicio_)
        : elServicio(stringAUint8AlReves(nombreServicio_, &uuidServicio[0], 16)) { }

    void anyadirCaracteristica(Caracteristica& car) {
        lasCaracteristicas.push_back(&car);
    }

    void activarServicio() {
        err_t error = elServicio.begin();
        Serial.print("Error activating service: ");
        Serial.println(error);
        initialized = (error == NRF_SUCCESS);

        for (auto pCar : lasCaracteristicas) {
            pCar->activar();
        }
    }

    void anyadirServicio(BLEAdvertising& advertising) {
        if (isInitialized()) {
            advertising.addService(elServicio);
        } else {
            Serial.println("Error: Service is not initialized.");
        }
    }

    bool isInitialized() const {
        return initialized;
    }

    operator BLEService&() {
        return elServicio;
    }
};

#endif
