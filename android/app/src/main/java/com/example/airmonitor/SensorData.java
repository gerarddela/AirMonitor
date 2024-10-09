package com.example.airmonitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SensorData {
    private String tipo_dato_nombre;
    private float valor;
    private String timestamp;

    public SensorData(String tipo_dato_nombre, float valor) {
        this.tipo_dato_nombre = tipo_dato_nombre;
        this.valor = valor;
        this.timestamp = getCurrentTimestamp();
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getTipo_dato_nombre() { return tipo_dato_nombre; }
    public void setTipo_dato_nombre(String tipo_dato_nombre) { this.tipo_dato_nombre = tipo_dato_nombre; }

    public float getValor() { return valor; }
    public void setValor(float valor) { this.valor = valor; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
