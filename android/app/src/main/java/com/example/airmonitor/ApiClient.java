// Archivo: ApiClient.java
// Descripción: Esta clase proporciona un cliente API utilizando Retrofit para realizar
//              solicitudes HTTP. Se asegura de que solo haya una instancia de Retrofit
//              a través del patrón de diseño Singleton.
// Diseño: La clase tiene un método estático `getClient` que permite obtener la instancia
//        de Retrofit, asegurando que se utilice la misma instancia para la misma URL base.
// Parámetros:
// - `baseUrl`: La URL base que se usará para las solicitudes API.

package com.example.airmonitor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Instancia estática de Retrofit.
    // Descripción: La variable `retrofit` es estática y se inicializa como nula.
//              Almacena la instancia de Retrofit que se crea una sola vez y se reutiliza.
    private static Retrofit retrofit = null;

    // Método estático para obtener la instancia de Retrofit.
    // Descripción: Este método comprueba si la instancia de Retrofit ya existe o si la URL
    //              base ha cambiado. Si no existe o ha cambiado, crea una nueva instancia
    //              con la URL base proporcionada y un convertidor de JSON (Gson).
    // Parámetros:
    // - `baseUrl`: La URL base que se usará para construir el cliente API.
    // Retorno: Devuelve la instancia de Retrofit configurada.
    public static Retrofit getClient(String baseUrl) {
        // Verifica si retrofit es nulo o si la URL base actual no coincide con la nueva URL base.
        if (retrofit == null || !retrofit.baseUrl().toString().equals(baseUrl)) {
            // Crea una nueva instancia de Retrofit.
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl) // Configura la URL base del cliente API.
                    .addConverterFactory(GsonConverterFactory.create()) // Añade el convertidor de JSON.
                    .build(); // Construye la instancia de Retrofit.
        }
        return retrofit; // Devuelve la instancia de Retrofit.
    }
}
