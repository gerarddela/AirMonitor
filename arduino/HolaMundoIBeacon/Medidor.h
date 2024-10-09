#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO

#include <Arduino.h> 

class Medidor {
private:
    const int16_t CO2_FIJO = 800;
    const int16_t TEMPERATURA_FIJA = 22;

    const int VgasPin = A0;
    const int VrefPin = A1;
    const int VtempPin = A2;

    const float Voffset = 0.1;
    const float M = 0.5;

public:
    Medidor() {}

    void iniciarMedidor() {

    }

    float medirCO2() {
        float Vgas = analogRead(VgasPin) * (3.0 / 1023.0);
        float Vref = analogRead(VrefPin) * (3.0 / 1023.0);
        
        float Vgas0 = Voffset + Vref;

        float Cx = (Vgas - Vgas0) * (1 / M);

        Serial.print("Vgas: "); Serial.println(Vgas, 4);
        Serial.print("Vref: "); Serial.println(Vref, 4);
        Serial.print("Ozone Concentration (ppm): "); Serial.println(Cx, 4);

        return Cx;
    }

    float medirTemperatura() {
        float Vtemp = analogRead(VtempPin) * (3.0 / 1023.0);

        float temperature = (87.0 / 3) * Vtemp - 18.0;

        Serial.print("Temperature (°C): "); Serial.println(temperature, 2);

        return temperature;
    }
};

#endif 
