#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

class Publicador {

private:
  uint8_t beaconUUID[16] = { 
    'E', 'P', 'S', 'G', '-', 'G', 'T', 'I', 
    '-', 'P', 'R', 'O', 'Y', '-', '3', 'A'
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
    TEMPERATURA = 12,
    RUIDO = 13
  };

  Publicador() {}

  // Encender la emisora
  void encenderEmisora() {
    laEmisora.encenderEmisora();  // Eliminando el uso innecesario de (*this)
  }

  // Publicar valor de CO2
  void publicarCO2(int16_t valorCO2, uint8_t contador, long tiempoEspera) {
    uint16_t major = (MedicionesID::CO2 << 8) + contador;
    laEmisora.emitirAnuncioIBeacon(beaconUUID, major, valorCO2, RSSI);
    delay(tiempoEspera);
    laEmisora.detenerAnuncio();
  }

  // Publicar valor de temperatura
  void publicarTemperatura(int16_t valorTemperatura, uint8_t contador, long tiempoEspera) {
    uint16_t major = (MedicionesID::TEMPERATURA << 8) + contador;
    laEmisora.emitirAnuncioIBeacon(beaconUUID, major, valorTemperatura, RSSI);
    delay(tiempoEspera);
    laEmisora.detenerAnuncio();
  }

  // Publicar todas las mediciones (CO2 y Temperatura)
  void publicarMediciones() {
    // Simular valores de CO2 y temperatura
    int16_t valorCO2 = random(400, 1000);  // CO2 entre 400 ppm y 1000 ppm
    int16_t valorTemperatura = random(18, 26);  // Temperatura entre 18°C y 26°C

    long tiempoEspera = 1000;  // Espera de 1 segundo entre anuncios

    // Publicar CO2
    publicarCO2(valorCO2, contador, tiempoEspera);
    
    // Publicar temperatura
    publicarTemperatura(valorTemperatura, contador, tiempoEspera);

    contador++;  // Incrementar el contador para la próxima medición
  }  
};

#endif
