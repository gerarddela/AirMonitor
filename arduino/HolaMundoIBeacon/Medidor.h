#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO

// Archivo: Medidor.h
// Descripción: Este archivo define la clase Medidor, que simula la medición de CO2 y temperatura.
//               Utiliza valores fijos para estas mediciones en un entorno de prueba.
// Diseño: La clase Medidor proporciona métodos para inicializar el medidor y obtener los valores
//        de CO2 y temperatura.
// Parámetros: La clase no recibe parámetros en el constructor, ya que utiliza valores constantes.

#include <Arduino.h> // Incluir la biblioteca Arduino para funciones básicas

// Clase que representa un medidor de CO2 y temperatura
class Medidor {
private:
    const int16_t CO2_FIJO = 800;         // Valor fijo simulado de CO2 en partes por millón (ppm)
    const int16_t TEMPERATURA_FIJA = 22;   // Valor fijo simulado de temperatura en grados Celsius (°C)

public:
    // Constructor de la clase Medidor
    Medidor() {} // Constructor vacío, se puede agregar lógica de inicialización si es necesario

    // Método para inicializar el medidor
    void iniciarMedidor() {
        // Puedes agregar lógica de inicialización si es necesario
        // Por ejemplo, configuración de sensores, calibraciones, etc.
    }

    // Método para medir CO2
    // Descripción: Imprime y retorna el valor fijo de CO2
    // Retorna: Valor fijo de CO2 en ppm
    float medirCO2() {
        Serial.print("Medición de CO2 (ppm): "); // Mensaje para mostrar en el monitor serie
        Serial.println(CO2_FIJO, 4); // Imprime el valor de CO2 con 4 decimales
        return CO2_FIJO; // Retorna el valor fijo de CO2
    }

    // Método para medir la temperatura
    // Descripción: Imprime y retorna el valor fijo de temperatura
    // Retorna: Valor fijo de temperatura en °C
    float medirTemperatura() {
        Serial.print("Medición de temperatura (°C): "); // Mensaje para mostrar en el monitor serie
        Serial.println(TEMPERATURA_FIJA, 2); // Imprime el valor de temperatura con 2 decimales
        return TEMPERATURA_FIJA; // Retorna el valor fijo de temperatura
    }

};

#endif // MEDIDOR_H_INCLUIDO
