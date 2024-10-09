// -*-c++-*- 

#include <bluefruit.h>

#undef min
#undef max

#include "LED.h"
#include "PuertoSerie.h"
#include "EmisoraBLE.h"
#include "Publicador.h"
#include "Medidor.h"

namespace Globales {
  LED elLED(7);
  PuertoSerie elPuerto(115200);
  Publicador elPublicador;
  Medidor elMedidor;
};

void inicializarPlaquita() {
}

void setup() {
  Serial.begin(9600);
  
  Globales::elPuerto.esperarDisponible();

  inicializarPlaquita();

  Serial.println("Allowing sensor to stabilize...");
  delay(10000);

  Globales::elPublicador.encenderEmisora();
  Globales::elMedidor.iniciarMedidor();

  esperar(1000);

  Globales::elPuerto.escribir("---- setup(): fin ---- \n ");
}

inline void lucecitas() {
  using namespace Globales;

  elLED.brillar(100);
  esperar(400);
  elLED.brillar(100);
  esperar(400);
  elLED.brillar(100);
  esperar(400);
  elLED.brillar(1000);
  esperar(400);
}

namespace Loop {
  uint8_t cont = 0;
};

void loop() {
  using namespace Loop;
  using namespace Globales;

  cont++;

  elPuerto.escribir("\n---- loop(): empieza ");
  elPuerto.escribir(cont);
  elPuerto.escribir("\n");

  lucecitas();

  // Usar las funciones de Medidor para obtener los valores de CO2 y temperatura
  float valorCO2 = elMedidor.medirCO2();  // Ahora está en float
  Serial.print("Valor CO2 medido (previo a publicación): ");
  Serial.println(valorCO2);  // Imprime el valor medido antes de la publicación
  elPublicador.publicarCO2(valorCO2, cont, 1000);

  float valorTemperatura = elMedidor.medirTemperatura();  // Ahora está en float
  Serial.print("Valor Temperatura medido (previo a publicación): ");
  Serial.println(valorTemperatura);  // Imprime el valor medido antes de la publicación
  elPublicador.publicarTemperatura(valorTemperatura, cont, 1000);

  char datos[21] = {
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H'
  };

  elPublicador.laEmisora.emitirAnuncioIBeaconLibre(&datos[0], 21);

  esperar(100);

  elPublicador.laEmisora.detenerAnuncio();

  elPuerto.escribir("---- loop(): acaba **** ");
  elPuerto.escribir(cont);
  elPuerto.escribir("\n");
}
