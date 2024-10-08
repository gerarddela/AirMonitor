const express = require('express');
const cors = require('cors');
const { Pool } = require('pg');

// Configuración de la base de datos
const pool = new Pool({
    host: process.env.DB_HOST || 'db',
    user: process.env.DB_USER || 'gerarddelafuente',
    password: process.env.DB_PASSWORD || '1234',
    database: process.env.DB_NAME || 'airmonitor_db',
    port: 5432,
});

// Crear un servidor Express
const app = express();
const PORT = 3000;

// Middleware para CORS
app.use(cors());
app.use(express.json()); // Permitir que el servidor entienda el JSON

// Middleware para manejar las solicitudes
app.get('/', (req, res) => {
    res.send('¡Servidor en funcionamiento!');
});

// Ruta para obtener las mediciones
app.get('/mediciones', async (req, res) => {
    const query = 'SELECT * FROM mediciones ORDER BY fecha DESC LIMIT 10';

    try {
        const result = await pool.query(query);
        res.json(result.rows);
    } catch (err) {
        console.error('Error al obtener las mediciones:', err);
        res.status(500).send('Error al obtener las mediciones');
    }
});

// Nueva ruta POST para agregar mediciones
app.post('/mediciones', async (req, res) => {
    const { name, value } = req.body; // Cambiado para que coincida con la estructura de SensorData

    // Verificar que ambos campos son proporcionados
    if (!name || !value) {
        return res.status(400).send('Los campos "name" y "value" son requeridos.');
    }

    // Comprobación para asegurar que el valor es un número
    if (isNaN(value)) {
        return res.status(400).send('El campo "value" debe ser un número.');
    }

    try {
        // Aquí podrías hacer una lógica para asegurarte de que el nombre corresponde a CO2 o Temperatura
        await insertarMedicion(name === 'CO2' ? parseFloat(value) : null, name === 'Temperatura' ? parseFloat(value) : null);
        res.status(201).send('Medición insertada con éxito');
    } catch (err) {
        console.error('Error al insertar la medición:', err);
        res.status(500).send('Error al insertar la medición');
    }
});

// Función para conectar a la base de datos con reintentos
async function conectarBaseDatos() {
    let retries = 5;
    while (retries) {
        try {
            await pool.connect();
            console.log('Conectado a la base de datos');
            return;
        } catch (err) {
            console.error('Error de conexión a la base de datos, reintentando...', err);
            retries -= 1;
            await new Promise(res => setTimeout(res, 5000));
        }
    }
    throw new Error('No se pudo conectar a la base de datos después de varios intentos.');
}

// Función para crear la tabla de mediciones
async function crearTablaMediciones() {
    const query = `
        CREATE TABLE IF NOT EXISTS mediciones (
            id SERIAL PRIMARY KEY,
            co2 DECIMAL(5, 2),
            temperatura DECIMAL(5, 2),
            fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    `;

    const client = await pool.connect();
    try {
        await client.query(query);
        console.log('Tabla de mediciones creada exitosamente');
    } catch (err) {
        console.error('Error al crear la tabla de mediciones:', err);
    } finally {
        client.release();
    }
}

// Función para insertar mediciones
async function insertarMedicion(co2, temperatura) {
    const query = `
        INSERT INTO mediciones (co2, temperatura)
        VALUES ($1, $2);
    `;

    const client = await pool.connect();
    try {
        await client.query(query, [co2, temperatura]);
        console.log(`Medición insertada: CO2: ${co2}, Temperatura: ${temperatura}`);
    } catch (err) {
        console.error('Error al insertar la medición:', err);
    } finally {
        client.release();
    }
}

// Llamar a la función para crear la tabla y agregar valores fijos al iniciar el servidor
async function iniciarServidor() {
    await conectarBaseDatos();
    await crearTablaMediciones();

    app.listen(PORT, () => {
        console.log(`Servidor escuchando en el puerto ${PORT}`);
    });
}

// Iniciar el proceso
iniciarServidor();
