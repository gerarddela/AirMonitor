// -*-c++-*- 

#include <bluefruit.h>  // Biblioteca para manejar BLE con Bluefruit

#undef min  // Desdefinición de min para evitar conflictos
#undef max  // Desdefinición de max para evitar conflictos

#include "LED.h"       // Inclusión de la clase LED
#include "PuertoSerie.h" // Inclusión de la clase para manejo de puerto serie
#include "EmisoraBLE.h" // Inclusión de la clase para emisora BLE
#include "Publicador.h"  // Inclusión de la clase para la publicación de datos
#include "Medidor.h"     // Inclusión de la clase para medición de CO2 y temperatura

// Espacio de nombres para mantener las variables globales
namespace Globales {
  LED elLED(7);                // Instancia de LED en el pin 7
  PuertoSerie elPuerto(115200); // Instancia de PuertoSerie a 115200 baudios
  Publicador elPublicador;     // Instancia de Publicador para enviar datos
  Medidor elMedidor;           // Instancia de Medidor para medir CO2 y temperatura
};

// Función para inicializar la placa
void inicializarPlaquita() {
  // Inicialización específica del hardware o configuraciones adicionales
}

// Función de configuración que se ejecuta una vez al inicio
void setup() {
  Serial.begin(9600);  // Inicializa la comunicación serial a 9600 baudios
  
  Globales::elPuerto.esperarDisponible(); // Espera a que el puerto esté disponible

  inicializarPlaquita(); // Llama a la función de inicialización de la placa

  Serial.println("Allowing sensor to stabilize..."); // Mensaje para estabilizar el sensor
  delay(10000);  // Espera 10 segundos para permitir que el sensor se estabilice

  Globales::elPublicador.encenderEmisora(); // Enciende la emisora BLE
  Globales::elMedidor.iniciarMedidor(); // Inicia el medidor de CO2 y temperatura

  esperar(1000); // Espera 1 segundo antes de continuar

  Globales::elPuerto.escribir("---- setup(): fin ---- \n "); // Mensaje indicando que la configuración ha terminado
}

// Función para controlar el parpadeo del LED
inline void lucecitas() {
  using namespace Globales; // Utiliza el espacio de nombres Globales

  elLED.brillar(100); // El LED brilla durante 100 ms
  esperar(400);       // Espera 400 ms
  elLED.brillar(100); // El LED brilla durante 100 ms
  esperar(400);       // Espera 400 ms
  elLED.brillar(100); // El LED brilla durante 100 ms
  esperar(400);       // Espera 400 ms
  elLED.brillar(1000); // El LED brilla durante 1000 ms
  esperar(400);       // Espera 400 ms
}

// Espacio de nombres para el bucle principal
namespace Loop {
  uint8_t cont = 0; // Contador para el número de iteraciones del bucle
};

// Bucle principal que se ejecuta repetidamente
void loop() {
  using namespace Loop;   // Utiliza el espacio de nombres Loop
  using namespace Globales; // Utiliza el espacio de nombres Globales

  cont++; // Incrementa el contador de iteraciones

  elPuerto.escribir("\n---- loop(): empieza "); // Mensaje que indica el inicio del bucle
  elPuerto.escribir(cont); // Escribe el valor del contador en el puerto
  elPuerto.escribir("\n"); // Nueva línea

  lucecitas(); // Llama a la función que controla el parpadeo del LED

  // Usar las funciones de Medidor para obtener los valores de CO2 y temperatura
  float valorCO2 = elMedidor.medirCO2();  // Mide el CO2 y guarda el resultado en una variable
  Serial.print("Valor CO2 medido (previo a publicación): "); // Mensaje para el valor de CO2
  Serial.println(valorCO2);  // Imprime el valor medido antes de la publicación
  elPublicador.publicarCO2(valorCO2, cont, 1000); // Publica el valor de CO2

  float valorTemperatura = elMedidor.medirTemperatura();  // Mide la temperatura y guarda el resultado
  Serial.print("Valor Temperatura medido (previo a publicación): "); // Mensaje para el valor de temperatura
  Serial.println(valorTemperatura);  // Imprime el valor medido antes de la publicación
  elPublicador.publicarTemperatura(valorTemperatura, cont, 1000); // Publica el valor de temperatura

  char datos[21] = { // Array de caracteres para los datos a emitir
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H'
  };

  elPublicador.laEmisora.emitirAnuncioIBeaconLibre(&datos[0], 21); // Envía un anuncio IBeacon con los datos

  esperar(100); // Espera 100 ms

  elPublicador.laEmisora.detenerAnuncio(); // Detiene el anuncio IBeacon

  elPuerto.escribir("---- loop(): acaba **** "); // Mensaje que indica que el bucle ha terminado
  elPuerto.escribir(cont); // Escribe el valor del contador en el puerto
  elPuerto.escribir("\n"); // Nueva línea
}
