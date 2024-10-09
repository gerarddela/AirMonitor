// Archivo: PuertoSerie.h
// Descripción: Este archivo define la clase PuertoSerie, que gestiona la comunicación serie
//               a través de la interfaz Serial de Arduino. Permite inicializar la comunicación
//               en un determinado baud rate y proporciona métodos para esperar y enviar mensajes.
// Diseño: La clase utiliza el puerto serie de Arduino para enviar datos y controlar la disponibilidad
//        de la comunicación.
// Parámetros: El constructor recibe un valor de baud rate para iniciar la comunicación.

#ifndef PUERTO_SERIE_H_INCLUIDO
#define PUERTO_SERIE_H_INCLUIDO

// Clase que gestiona la comunicación a través del puerto serie
class PuertoSerie {
public:
  // Constructor de la clase PuertoSerie
  // Descripción: Inicializa la comunicación serie con el baud rate especificado.
  // Parámetros:
  // - baudios: Tasa de baudios para la comunicación serie (tipo long)
  PuertoSerie(long baudios) {
    Serial.begin(baudios); // Inicia la comunicación serie con el baud rate proporcionado
  }

  // Método para esperar un breve período para asegurar que el puerto esté disponible
  // Descripción: Realiza una pausa de 10 ms antes de continuar, útil para asegurar que
  //              la comunicación está lista.
  void esperarDisponible() {
    delay(10); // Espera 10 ms
  }

  // Método para escribir mensajes en el puerto serie
  // Descripción: Envía un mensaje al puerto serie utilizando el método print de la clase Serial.
  // Parámetros:
  // - mensaje: Mensaje a enviar, puede ser de cualquier tipo (tipo template)
  template<typename T>
  void escribir(T mensaje) {
    Serial.print(mensaje); // Envía el mensaje al puerto serie
  }
};

#endif // PUERTO_SERIE_H_INCLUIDO
