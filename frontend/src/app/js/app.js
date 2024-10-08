document.addEventListener('DOMContentLoaded', function() {
    // Función para obtener las últimas mediciones
    function fetchLatestMeasurements() {
        fetch('http://localhost:13000/mediciones')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                if (data.length === 0) {
                    console.log('No hay mediciones disponibles');
                    return;
                }

                // Asume que el último registro es el más reciente
                const latestMeasurement = data[0];

                document.getElementById('co2Value').textContent = `CO2: ${latestMeasurement.co2}`;
                document.getElementById('tempValue').textContent = `Temperatura: ${latestMeasurement.temperatura}°C`;
            })
            .catch(error => console.error('Error fetching data:', error));
    }

    // Llamar a la función inmediatamente al cargar la página
    fetchLatestMeasurements();

    // Establecer un intervalo para actualizar los valores cada 5 segundos (5000 ms)
    setInterval(fetchLatestMeasurements, 5000);
});
