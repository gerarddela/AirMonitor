#ifndef PUERTO_SERIE_H_INCLUIDO
#define PUERTO_SERIE_H_INCLUIDO

class PuertoSerie {
public:
  PuertoSerie(long baudios) {
    Serial.begin(baudios);
  }

  void esperarDisponible() {
    delay(10);
  }

  template<typename T>
  void escribir(T mensaje) {
    Serial.print(mensaje);
  }
};

#endif
