// Archivo: ApiService.java
// Descripción: Esta interfaz define un servicio API para enviar datos de mediciones al servidor
//               utilizando la biblioteca Retrofit para la comunicación HTTP.
// Diseño: Se utiliza un método POST para enviar los datos de mediciones encapsulados en un objeto
//        SensorData al endpoint "/mediciones". La respuesta se maneja a través de un objeto Call.
// Parámetros: El método postData recibe un objeto de tipo SensorData que contiene la información
//             de las mediciones que se desean enviar.

package com.example.airmonitor;

import retrofit2.Call; // Importa la clase Call de Retrofit para manejar la respuesta de la API.
import retrofit2.http.Body; // Importa la anotación Body para enviar un cuerpo en la solicitud.
import retrofit2.http.POST; // Importa la anotación POST para definir el tipo de solicitud HTTP.

public interface ApiService {

    // Método para enviar datos de mediciones al servidor
    // Descripción: Envía un objeto SensorData al endpoint "/mediciones" mediante una solicitud POST.
    // Parámetros:
    // - sensorData: Objeto que contiene los datos de la medición que se van a enviar.
    // Retorno: Un objeto Call<Void> que permite gestionar la respuesta del servidor.
    @POST("/mediciones")
    Call<Void> postData(@Body SensorData sensorData);
}
