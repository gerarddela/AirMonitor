#include <bluefruit.h>

#undef min
#undef max

#include "LED.h"
#include "PuertoSerie.h"

namespace Globales {
  
  LED elLED(7);

  PuertoSerie elPuerto(115200);

};

#include "EmisoraBLE.h"
#include "Publicador.h"
#include "Medidor.h"

namespace Globales {

  Publicador elPublicador;

  Medidor elMedidor;

};

void inicializarPlaquita() {}

void setup() {

  Globales::elPuerto.esperarDisponible();

  inicializarPlaquita();

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
  Globales::elLED.brillar(100);
  esperar(400);
  Globales::elLED.brillar(1000);
  esperar(1000);
}

namespace Loop {
  uint8_t cont = 0;
}

void loop() {

  using namespace Loop;
  using namespace Globales;

  cont++;

  elPuerto.escribir("\n---- loop(): empieza ");
  elPuerto.escribir(cont);
  elPuerto.escribir("\n");

  lucecitas();

  int valorCO2 = elMedidor.medirCO2();
  
  elPublicador.publicarCO2(valorCO2, cont, 1000);

  int valorTemperatura = elMedidor.medirTemperatura();
  
  elPublicador.publicarTemperatura(valorTemperatura, cont, 1000);

  char datos[21] = {
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H'
  };

  // Accede a la emisora a través de elPublicador
  elPublicador.encenderEmisora();
  elPublicador.laEmisora.emitirAnuncioIBeaconLibre(&datos[0], 21);

  esperar(2000);

  elPublicador.laEmisora.detenerAnuncio();

  elPuerto.escribir("---- loop(): acaba **** ");
  elPuerto.escribir(cont);
  elPuerto.escribir("\n");
}
