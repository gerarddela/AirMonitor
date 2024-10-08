#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO

class Medidor {
public:
    Medidor() {}

    void iniciarMedidor() {}

    int medirCO2() {
        return random(400, 3000);  // Generar valores aleatorios entre 400 y 3000 ppm
    }

    int medirTemperatura() {
        return random(10, 35);  // Generar valores aleatorios entre 10 y 35 °C
    }
};

#endif
