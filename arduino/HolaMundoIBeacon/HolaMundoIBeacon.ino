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

    // Encender la emisora solo una vez al principio
    Globales::elPublicador.encenderEmisora();

    Globales::elMedidor.iniciarMedidor();
    esperar(1000);

    randomSeed(analogRead(0));  // Inicializar generador de números aleatorios

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
  esperar(1000);
}

namespace Loop {
  uint8_t cont = 0;
  unsigned long previousMillis = 0;   // Variable para medir el tiempo
  const long interval = 2000;         // Intervalo de 2 segundos para los anuncios
}

void loop() {
    using namespace Loop;
    using namespace Globales;

    unsigned long currentMillis = millis();

    // Verifica si ha pasado el tiempo especificado (2 segundos)
    if (currentMillis - previousMillis >= interval) {
        previousMillis = currentMillis;  // Actualiza el tiempo anterior

        cont++;

        elPuerto.escribir("\n---- loop(): empieza ");
        elPuerto.escribir(cont);
        elPuerto.escribir("\n");

        lucecitas();

        // Publicar mediciones (CO2 y Temperatura)
        elPublicador.publicarMediciones();  // Llama a esta función

        // Detener el anuncio anterior antes de emitir un nuevo anuncio libre
        elPublicador.laEmisora.detenerAnuncio();

        char datos[21] = {
            'H', 'o', 'l', 'a',
            'H', 'o', 'l', 'a',
            'H', 'o', 'l', 'a',
            'H', 'o', 'l', 'a',
            'H', 'o', 'l', 'a',
            'H'
        };

        // Emitir un anuncio de iBeacon sin detenerlo en cada loop
        elPublicador.laEmisora.emitirAnuncioIBeaconLibre(&datos[0], 21);

        elPuerto.escribir("---- loop(): acaba **** ");
        elPuerto.escribir(cont);
        elPuerto.escribir("\n");
    }
}



