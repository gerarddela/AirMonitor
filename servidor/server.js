const express = require('express');
const cors = require('cors');
const { Pool } = require('pg');

const pool = new Pool({
    host: process.env.DB_HOST || 'db',
    user: process.env.DB_USER || 'gerarddelafuente',
    password: process.env.DB_PASSWORD || '1234',
    database: process.env.DB_NAME || 'airmonitor_db',
    port: 5432,
});

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json()); 

app.get('/', (req, res) => {
    res.send('¡Servidor en funcionamiento!');
});

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

app.post('/mediciones', async (req, res) => {
    const { tipo_dato_nombre, valor } = req.body;

    console.log(`Datos recibidos en el servidor: tipo_dato_nombre: ${tipo_dato_nombre}, valor: ${valor}`); // Log aquí

    if (!tipo_dato_nombre || valor === undefined) {
        return res.status(400).send('Los campos "tipo_dato_nombre" y "valor" son requeridos.');
    }

    console.log(`Datos válidos: tipo_dato_nombre: ${tipo_dato_nombre}, valor: ${valor}`); // Log adicional

    if (isNaN(valor)) {
        return res.status(400).send('El campo "valor" debe ser un número.');
    }

    try {
        let co2 = null;
        let temperatura = null;

        if (tipo_dato_nombre === 'CO2') {
            co2 = parseFloat(valor);
        } else if (tipo_dato_nombre === 'Temperatura') {
            temperatura = parseFloat(valor);
        } else {
            return res.status(400).send('El tipo de dato no es válido. Debe ser "CO2" o "Temperatura".');
        }

        console.log(`Insertando medición: CO2: ${co2}, Temperatura: ${temperatura}`); // Log aquí
        await insertarMedicion(co2, temperatura);
        res.status(201).send('Medición insertada con éxito');
    } catch (err) {
        console.error('Error al insertar la medición:', err);
        res.status(500).send('Error al insertar la medición');
    }
});

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

async function iniciarServidor() {
    await conectarBaseDatos();
    await crearTablaMediciones();

    app.listen(PORT, () => {
        console.log(`Servidor escuchando en el puerto ${PORT}`);
    });
}

iniciarServidor();
