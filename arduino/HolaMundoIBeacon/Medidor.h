#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO

#include <Arduino.h> 

class Medidor {
private:
    const int16_t CO2_FIJO = 800;
    const int16_t TEMPERATURA_FIJA = 22; 

public:
    Medidor() {}

    void iniciarMedidor() {
        // Puedes agregar lógica de inicialización si es necesario
    }

    float medirCO2() {
        Serial.print("Medición de CO2 (ppm): ");
        Serial.println(CO2_FIJO, 4);
        return CO2_FIJO; // Retorna el valor fijo de CO2
    }

    float medirTemperatura() {
        Serial.print("Medición de temperatura (°C): ");
        Serial.println(TEMPERATURA_FIJA, 2);
        return TEMPERATURA_FIJA; // Retorna el valor fijo de temperatura
    }

};

#endif 
