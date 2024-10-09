#ifndef LED_H_INCLUIDO
#define LED_H_INCLUIDO

// Archivo: LED.h
// Descripción: Este archivo define la clase LED, que proporciona métodos para controlar un LED
//               conectado a un pin específico de la placa Arduino.
// Diseño: La clase LED permite encender, apagar, alternar y hacer brillar el LED por un tiempo
//        específico. Utiliza un pin de salida para controlar el estado del LED.
// Parámetros: El constructor recibe el número del pin al que está conectado el LED.

void esperar(long tiempo) {
    delay(tiempo); // Pausa la ejecución durante el tiempo especificado en milisegundos
}

// Clase que representa un LED
class LED {
private:
    int numeroLED;    // Número del pin al que está conectado el LED
    bool encendido;   // Estado del LED (encendido o apagado)

public:
    // Constructor de la clase LED
    // Parámetros:
    //   - numero: El número del pin al que se conecta el LED
    LED(int numero)
        : numeroLED(numero), encendido(false) // Inicializa el número del pin y el estado
    {
        pinMode(numeroLED, OUTPUT); // Configura el pin como salida
        apagar(); // Asegura que el LED esté apagado al iniciar
    }

    // Encender el LED
    void encender() {
        digitalWrite(numeroLED, HIGH); // Establece el pin en alto para encender el LED
        encendido = true; // Actualiza el estado del LED
    }

    // Apagar el LED
    void apagar() {
        digitalWrite(numeroLED, LOW); // Establece el pin en bajo para apagar el LED
        encendido = false; // Actualiza el estado del LED
    }

    // Alternar el estado del LED (encender si está apagado, y apagar si está encendido)
    void alternar() {
        if (encendido) { // Si el LED está encendido
            apagar();    // Apagar el LED
        } else {        // Si el LED está apagado
            encender();  // Encender el LED
        }
    }

    // Hacer brillar el LED durante un tiempo específico
    // Parámetros:
    //   - tiempo: Tiempo en milisegundos que el LED debe permanecer encendido
    void brillar(long tiempo) {
        encender(); // Enciende el LED
        esperar(tiempo); // Espera el tiempo especificado
        apagar(); // Apaga el LED después de esperar
    }
};

#endif // LED_H_INCLUIDO
