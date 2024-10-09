#include <bluefruit.h>

#undef min 
#undef max 

#include "LED.h"
#include "PuertoSerie.h"

const int VgasPin = A5;   
const int VrefPin = 28;  
const int VtempPin = 29;  

const int Voffset = 0;
const float TIA_GAIN = 499.0;  
const float SENSITIVITY_CODE = 44.26;
const float M = SENSITIVITY_CODE * TIA_GAIN * 1e-9 * 1e3;  

namespace Globales {
  LED elLED(7);
  PuertoSerie elPuerto(115200);
};

#include "EmisoraBLE.h"
#include "Publicador.h"
#include "Medidor.h"

namespace Globales {
  Medidor elMedidor; 
  Publicador elPublicador(elMedidor);
}

void inicializarPlaquita() {
}

void setup() {
  Serial.begin(9600);
  pinMode(VgasPin, INPUT);
  pinMode(VrefPin, INPUT);
  pinMode(VtempPin, INPUT);

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
  unsigned long previousMillis = 0;
  const long interval = 2000;
}

void loop() {
  using namespace Loop;
  using namespace Globales;

  unsigned long currentMillis = millis();

  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis; 

    cont++;

    elPuerto.escribir("\n---- loop(): empieza ");
    elPuerto.escribir(cont);
    elPuerto.escribir("\n");

    lucecitas();

    int valorCO2 = elMedidor.medirCO2();
    int valorTemperatura = elMedidor.medirTemperatura();

    elPublicador.publicarCO2(valorCO2, cont, 1000);
    elPublicador.publicarTemperatura(1000);

    Serial.print("CO2: ");
    Serial.print(valorCO2);
    Serial.print(", Temperatura: ");
    Serial.println(valorTemperatura);

    elPublicador.laEmisora.detenerAnuncio();

    char datos[21] = {
      'H', 'o', 'l', 'a',
      'H', 'o', 'l', 'a',
      'H', 'o', 'l', 'a',
      'H', 'o', 'l', 'a',
      'H', 'o', 'l', 'a',
      'H'
    };

    elPublicador.laEmisora.emitirAnuncioIBeaconLibre(&datos[0], 21);

    elPuerto.escribir("---- loop(): acaba **** ");
    elPuerto.escribir(cont);
    elPuerto.escribir("\n");
  }
}
