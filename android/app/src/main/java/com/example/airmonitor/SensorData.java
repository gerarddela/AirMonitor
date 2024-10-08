package com.example.airmonitor;

public class SensorData {
    private String name; // Nombre del sensor
    private int value; // Valor medido por el sensor

    // Constructor que acepta un nombre de sensor y un valor
    public SensorData(String name, int value) {
        this.name = name;
        this.value = value;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
