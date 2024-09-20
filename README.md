# AirMonitor🌍💨

### ¡La calidad del aire bajo control!

**AirMonitor** es un proyecto innovador que utiliza la tecnología de crowdsensing móvil para brindar a los ciudadanos información precisa y en tiempo real sobre la **calidad del aire** en su entorno. Nos enfocamos en zonas con baja cobertura de estaciones de monitoreo. ¡Con tu colaboración, podemos mejorar la salud ambiental y mapear la contaminación en alta resolución!

---

## 🚀 Objetivos del Proyecto

- **Monitoreo ambiental distribuido**: Recolección de datos mediante sensores móviles distribuidos.
- **Participación ciudadana**: Empoderar a los ciudadanos para que sean parte activa en la recolección de datos.
- **Mapas de alta resolución**: Crear mapas detallados de contaminación con una precisión sin precedentes.
- **Validación científica**: Comparar los datos generados con estaciones de referencia para asegurar su precisión.
- **Educación e información**: Informar a la ciudadanía sobre los peligros de la contaminación y su impacto en la salud.

---

## 🛠️ ¿Qué incluye el proyecto?

### 1. **Nodo Sensor Móvil**
- **💡 Sensores electroquímicos**: Sensores asequibles y ligeros para medir contaminantes (inicialmente **PM2.5**).
- **🔌 Conectividad Bluetooth LE**: Transmisión de datos eficiente hacia el smartphone del usuario.
- **🔋 Batería recargable**: Con conector micro USB para facilitar la carga.

### 2. **Aplicación Móvil 📱**
- **📊 Datos en tiempo real**: Visualización de niveles de contaminación y mapas interactivos.
- **📍 Geo-posicionamiento**: Las mediciones se envían automáticamente al servidor, proporcionando datos geolocalizados.
- **📸 Captura de imágenes**: Los usuarios pueden tomar fotos del paisaje urbano para mejorar el mapeo.

### 3. **Aplicación en Servidor 💻**
- **⚙️ Procesamiento de datos**: Aplicación de algoritmos para reducir la variabilidad y mejorar la precisión de los datos.
- **🗺️ Mapas de contaminación**: Generación de mapas detallados con **Python** y **QGIS**, usando técnicas avanzadas de interpolación espacial.
- **🔄 Comparación con estaciones de referencia**: Ajuste en tiempo real de los datos, comparándolos con estaciones de monitoreo oficiales.

---

## 📡 Arquitectura del Sistema

### Comunicación entre componentes:
1. **Smartphone ↔️ Servidor**:
   - **Protocolo HTTP/REST**: Utilizamos el protocolo HTTP para comunicaciones simples y eficientes entre los sensores y el servidor.
   - **Protocolo MQTT (opcional)**: Evaluamos el uso de MQTT para mejorar la escalabilidad del sistema cuando hay muchos sensores.

2. **Formato de datos**: Utilizamos **JSON**, un formato ampliamente compatible para facilitar el intercambio de datos entre dispositivos.

---

## 🛠️ Tecnologías utilizadas

- **Lenguajes**: Python, Kotlin, Java, C++
- **Frameworks**: Android SDK, iOS SDK
- **Herramientas de Procesamiento**: Python, QGIS
- **Protocolos de comunicación**: HTTP/REST, MQTT (opcional)
- **Formato de datos**: JSON
- **Hardware**: Sensores electroquímicos, Microcontroladores con Bluetooth LE, Smartphones

---

## 📊 ¿Cómo contribuir?

¡Cualquier persona interesada en la **monitorización ambiental** y el desarrollo de aplicaciones móviles puede colaborar en **AirMonitor**!
- **Desarrollo de aplicaciones móviles** (iOS/Android)
- **Optimización del procesamiento de datos** y la creación de mapas
- **Pruebas de campo** y validación de sensores
- **Difusión y educación**: Ayuda a concienciar a más personas sobre la importancia de la calidad del aire.

### 📝 Pasos para contribuir:
1. Haz un fork de este repositorio.
2. Crea una rama para tu funcionalidad (`git checkout -b feature/nueva-funcionalidad`).
3. Haz commit de tus cambios (`git commit -m 'Añadir nueva funcionalidad'`).
4. Haz push a la rama (`git push origin feature/nueva-funcionalidad`).
5. Abre un Pull Request 🚀.

---

## 📄 Licencia

Este proyecto está licenciado bajo la **Licencia MIT**. Consulta el archivo [LICENSE](LICENSE.md) para más detalles.

---

## 💡 Contacto

Para más información, sugerencias o preguntas, no dudes en contactarnos:
- **Email**: airmonitor@upv.es
- **Desarrollador principal**: [Gerard de la Fuente Carbonero](https://www.upv.es/) (3º GTI, UPV)

¡Gracias por tu interés en **AirMonitor**! Con tu colaboración, podemos mejorar la calidad del aire que respiramos y la salud ambiental de todos. 💚🌱
