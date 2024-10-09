package com.example.airmonitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SensorData {
    private String tipo_dato_nombre; // Nombre del tipo de dato (e.g., CO2, Temperatura)
    private float valor; // Valor registrado por el sensor
    private String timestamp; // Timestamp de cuando se registró el dato

    // Constructor de la clase SensorData
    public SensorData(String tipo_dato_nombre, float valor) {
        this.tipo_dato_nombre = tipo_dato_nombre; // Inicializa el tipo de dato
        this.valor = valor; // Inicializa el valor del sensor
        this.timestamp = getCurrentTimestamp(); // Asigna el timestamp actual
    }

    // Método privado para obtener el timestamp actual en formato ISO 8601
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        return sdf.format(new Date()); // Formatea la fecha actual y la devuelve
    }

    // Método para obtener el nombre del tipo de dato
    public String getTipo_dato_nombre() {
        return tipo_dato_nombre; // Devuelve el nombre del tipo de dato
    }

    // Método para obtener el valor registrado por el sensor
    public float getValor() {
        return valor; // Devuelve el valor del sensor
    }

    // Método para establecer un nuevo valor para el sensor
    public void setValor(float valor) {
        this.valor = valor; // Actualiza el valor del sensor
    }

    // Método para obtener el timestamp registrado
    public String getTimestamp() {
        return timestamp; // Devuelve el timestamp registrado
    }

    // Método para establecer un nuevo timestamp
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp; // Actualiza el timestamp
    }
}
