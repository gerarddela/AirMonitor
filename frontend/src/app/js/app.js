document.addEventListener('DOMContentLoaded', function() {
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

                const latestMeasurement = data[0];

                document.getElementById('co2Value').textContent = `CO2: ${latestMeasurement.co2}`;
                document.getElementById('tempValue').textContent = `Temperatura: ${latestMeasurement.temperatura}°C`;
            })
            .catch(error => console.error('Error fetching data:', error));
    }

    fetchLatestMeasurements();

    setInterval(fetchLatestMeasurements, 5000);
});
