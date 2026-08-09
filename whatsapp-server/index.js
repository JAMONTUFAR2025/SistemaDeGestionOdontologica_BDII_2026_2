const { Client, LocalAuth } = require('whatsapp-web.js');
const qrcode = require('qrcode-terminal');
const express = require('express');

const app = express();
app.use(express.json());

const fs = require('fs');
const path = require('path');

// Busca Chrome o Edge en las rutas por defecto de Windows
function getBrowserExecutablePath() {
    const browserPaths = [
        "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
        "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
        "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
        "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe"
    ];
    for (let p of browserPaths) {
        if (fs.existsSync(p)) return p;
    }
    return undefined; 
}

const client = new Client({
    authStrategy: new LocalAuth({ dataPath: 'sessions' }),
    puppeteer: {
        executablePath: getBrowserExecutablePath(),
        headless: true,
        args: ['--no-sandbox', '--disable-setuid-sandbox']
    }
});

let clientReady = false;

client.on('qr', (qr) => {
    console.log('\n======================================================');
    console.log('✅ ESCANEA ESTE CÓDIGO QR CON EL WHATSAPP DE LA CLÍNICA');
    console.log('======================================================\n');
    qrcode.generate(qr, { small: true });
});

client.on('ready', () => {
    clientReady = true;
    console.log('WhatsApp Bot está LISTO y CONECTADO!');
});

client.on('auth_failure', msg => {
    console.error('Fallo en la autenticación de WhatsApp:', msg);
});

client.on('disconnected', (reason) => {
    console.log('WhatsApp Bot desconectado:', reason);
    clientReady = false;
});

client.initialize();

// API Endpoint para que Java mande mensajes
app.post('/api/send', async (req, res) => {
    try {
        const { telefono, mensaje } = req.body;
        
        if (!telefono || !mensaje) {
            return res.status(400).json({ error: 'Faltan parámetros: telefono, mensaje' });
        }

        if (!clientReady) {
            return res.status(503).json({ error: 'WhatsApp Bot no está listo todavía.' });
        }

        // Formato del número en WhatsApp (Honduras +504)
        let number = telefono.replace(/[^0-9]/g, '');
        if (number.length === 8) {
            number = '504' + number;
        }
        
        // Agregar sufijo de WhatsApp
        const chatId = number + '@c.us';

        // Enviar mensaje
        await client.sendMessage(chatId, mensaje);
        
        console.log(`Mensaje enviado exitosamente a ${telefono}`);
        res.json({ success: true, message: 'Enviado correctamente' });

    } catch (error) {
        console.error('Error al enviar mensaje:', error);
        res.status(500).json({ error: error.message });
    }
});

app.get('/api/status', (req, res) => {
    res.json({ ready: clientReady });
});

const PORT = 3001;
app.listen(PORT, () => {
    console.log(`Servidor puente de WhatsApp escuchando en http://localhost:${PORT}`);
});
