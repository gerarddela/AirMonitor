const { Pool } = require('pg')
const pool = new Pool({
    host: 'db',
    port: 5432,
    user: 'gerarddelafuente',
    password: '1234',
    database: 'airmonitor_db'
})

module.exports = pool