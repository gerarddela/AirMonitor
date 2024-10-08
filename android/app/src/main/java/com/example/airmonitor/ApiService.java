package com.example.airmonitor;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface ApiService {
    @GET("mediciones") // Asegúrate de que esta ruta exista en tu servidor
    Call<List<SensorData>> getAllData();

    @POST("mediciones") // Asegúrate de que esta ruta también exista en tu servidor
    Call<Void> postData(@Body SensorData sensorData);
}
