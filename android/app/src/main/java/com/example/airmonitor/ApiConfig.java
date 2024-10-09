// Archivo: ApiConfig.java
// Descripción: Esta clase contiene la configuración de la API para la aplicación AirMonitor.
//              Define la URL base que se utilizará para realizar las solicitudes a los servicios.
// Diseño: La URL base es una constante pública y estática, lo que permite su acceso sin necesidad
//        de instanciar la clase. Esta clase no tiene métodos ni constructores, ya que solo
//        sirve para almacenar configuraciones.
// Parámetros: No se utilizan parámetros en esta clase, ya que solo contiene una constante.

package com.example.airmonitor;

public class ApiConfig {

    // URL base para las solicitudes a la API.
    // Descripción: La dirección IP y el puerto donde se ejecuta el servidor.
    // Esta constante se usa en la configuración de Retrofit para realizar solicitudes HTTP.
    public static final String BASE_URL = "http://172.20.10.2:13000/";
}
