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

// Evitar que el servidor Node se caiga por errores internos de Puppeteer
process.on('uncaughtException', function (err) {
    console.error('Excepción no capturada:', err);
});
process.on('unhandledRejection', (reason, promise) => {
    console.error('Rechazo no manejado en:', promise, 'razón:', reason);
});

const client = new Client({
    authStrategy: new LocalAuth({ dataPath: 'sessions' }),
    webVersionCache: {
        type: 'none' // Desactivar la caché estricta para evitar crasheos de versión antigua
    },
    puppeteer: {
        executablePath: getBrowserExecutablePath(),
        headless: true,
        args: [
            '--no-sandbox', 
            '--disable-setuid-sandbox',
            '--disable-features=IsolateOrigins,site-per-process'
        ]
    }
});

let clientReady = false;
let currentQR = "";
let internetConnected = false;

client.on('qr', (qr) => {
    currentQR = qr;
    console.log('\n======================================================');
    console.log('✅ CÓDIGO QR GENERADO. ABRIR EN EL SISTEMA PARA ESCANEAR');
    console.log('======================================================\n');
    // Ya no lo mostramos en terminal, solo lo guardamos
});

client.on('ready', () => {
    clientReady = true;
    currentQR = ""; // Limpiar QR cuando ya está conectado
    console.log('WhatsApp Bot está LISTO y CONECTADO!');
});

client.on('auth_failure', msg => {
    console.error('Fallo en la autenticación de WhatsApp:', msg);
});

client.on('disconnected', (reason) => {
    console.log('WhatsApp Bot desconectado:', reason);
    clientReady = false;
});

let initialized = false;
function tryInitialize() {
    if (initialized) return;
    require('dns').lookup('web.whatsapp.com', (err) => {
        if (err && (err.code === "ENOTFOUND" || err.code === "EAI_AGAIN")) {
            internetConnected = false;
            console.log("Sin conexión a Internet. WhatsApp no se puede inicializar todavía. Reintentando en 10 segundos...");
            setTimeout(tryInitialize, 10000);
        } else {
            internetConnected = true;
            console.log("Conexión detectada. Inicializando cliente de WhatsApp...");
            initialized = true;
            client.initialize().catch(e => {
                console.error("Fallo al inicializar WhatsApp. Reintentando...", e.message);
                initialized = false;
                setTimeout(tryInitialize, 10000);
            });
        }
    });
}

tryInitialize();

// API Endpoint para que Java mande mensajes
app.post('/api/send', async (req, res) => {
    try {
        const { telefono, mensaje } = req.body;
        
        if (!telefono || !mensaje) {
            return res.status(400).json({ error: 'Faltan parámetros: telefono, mensaje' });
        }

        if (!internetConnected) {
            return res.status(503).json({ error: 'NO_INTERNET' });
        }

        if (!clientReady) {
            return res.status(503).json({ error: 'NOT_READY' });
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
    res.json({ ready: clientReady, internet: internetConnected });
});

app.get('/api/qr', (req, res) => {
    if (!internetConnected) {
        return res.json({ qr: "", status: "no_internet" });
    }
    if (clientReady) {
        return res.json({ qr: "", status: "connected" });
    }
    if (currentQR) {
        return res.json({ qr: currentQR, status: "pending" });
    }
    return res.json({ qr: "", status: "loading" });
});

app.post('/api/shutdown', async (req, res) => {
    console.log("Señal de apagado recibida. Cerrando sesión de Puppeteer y el servidor Node...");
    res.json({ status: "shutting_down" });
    try {
        await client.destroy();
    } catch(e) {}
    process.exit(0);
});

app.post('/api/logout', async (req, res) => {
    console.log("Señal de cierre de sesión recibida. Desvinculando WhatsApp...");
    try {
        await client.logout();
        clientReady = false;
        currentQR = "";
        res.json({ success: true, message: "Sesión cerrada correctamente" });
        // The client will emit 'disconnected' and might need to be re-initialized 
        // to show a new QR. For simplicity, we can restart the client manually here.
        setTimeout(() => {
            console.log("Reinicializando cliente después del logout...");
            client.initialize().catch(e => console.error("Error al reinicializar", e));
        }, 2000);
    } catch (e) {
        console.error("Error al cerrar sesión:", e);
        res.status(500).json({ error: e.message });
    }
});

const PORT = 3001;
app.listen(PORT, () => {
    console.log(`Servidor puente de WhatsApp escuchando en http://localhost:${PORT}`);
});
