package com.example.airmonitor;

public class SensorData {
    private String name; // Nombre del sensor
    private double value; // Valor medido por el sensor

    // Constructor que acepta un nombre de sensor como String y un valor como double
    public SensorData(String name, double value) {
        this.name = name;
        this.value = value;
    }

    // Getters y Setters para el nombre y el valor
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
