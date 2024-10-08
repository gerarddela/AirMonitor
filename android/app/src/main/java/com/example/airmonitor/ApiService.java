package com.example.airmonitor;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("mediciones") // Esta ruta debe ser la que acepte los datos en tu servidor
    Call<Void> postData(@Body SensorData sensorData);
}
