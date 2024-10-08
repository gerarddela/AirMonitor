#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

class Publicador {

private:
  uint8_t beaconUUID[16] = { 
    'E', 'Q', 'U', 'I', 'P', 'O', '-', 'G', 
    'E', 'R', 'A', 'R', 'D', '-', '3', 'A' //EQUIPO-GERARD-3A
  };
  uint8_t contador = 1;  // Contador como miembro de la clase
  const int RSSI = -53;

public:
  EmisoraBLE laEmisora {
    "GTI-3A", //  nombre emisora
    0x004c, // fabricanteID (Apple)
    4 // txPower
  };
  
public:
  enum MedicionesID  {
    CO2 = 11,
    TEMPERATURA = 12, //identifiador hexa 
    RUIDO = 13
  };

  Publicador() {}

  // Encender la emisora
  void encenderEmisora() {
    laEmisora.encenderEmisora();  // Eliminando el uso innecesario de (*this)
  }

  void publicarCO2(int16_t valorCO2, uint8_t contador, long tiempoEspera) {
    uint16_t major = (MedicionesID::CO2 << 8) + contador;
    Globales::elPuerto.escribir("Enviando CO2: ");
    Globales::elPuerto.escribir(valorCO2);
    Globales::elPuerto.escribir("\n");
    laEmisora.emitirAnuncioIBeacon(beaconUUID, major, valorCO2, RSSI);
    delay(tiempoEspera);
    laEmisora.detenerAnuncio();
  }

  void publicarTemperatura(int16_t valorTemperatura, uint8_t contador, long tiempoEspera) {
    uint16_t major = (MedicionesID::TEMPERATURA << 8) + contador;
    laEmisora.emitirAnuncioIBeacon(beaconUUID, major, valorTemperatura, RSSI);
    delay(tiempoEspera);
    laEmisora.detenerAnuncio();
  }

  // Publicar todas las mediciones (CO2 y Temperatura)
  void publicarMediciones() {
    // Simular valores de CO2 y temperatura
    int16_t valorCO2 = random(400, 3000);  // CO2 entre 400 ppm y 3000 ppm
    int16_t valorTemperatura = random(10, 35);  // Temperatura entre 10°C y 35°C

    long tiempoEspera = 1000;  // Espera de 1 segundo entre anuncios

    // Imprimir valores en el Monitor Serial
    Globales::elPuerto.escribir("CO2: ");
    Globales::elPuerto.escribir(valorCO2);
    Globales::elPuerto.escribir(" ppm, Temperatura: ");
    Globales::elPuerto.escribir(valorTemperatura);
    Globales::elPuerto.escribir(" °C\n");

    // Publicar CO2
    publicarCO2(valorCO2, contador, tiempoEspera);
    
    // Publicar temperatura
    publicarTemperatura(valorTemperatura, contador, tiempoEspera);

    contador++;  // Incrementar el contador para la próxima medición
  }   
};

#endif
