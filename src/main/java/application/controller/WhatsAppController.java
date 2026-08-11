package application.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.io.File;

public class WhatsAppController {

    private static boolean nodeInstalado = true;
    private static boolean servidorIniciado = false;
    private static Process processNode = null;

    private static String comandoNode = "node";

    static {
        verificarDependencias();
    }

    private static void verificarDependencias() {
        try {
            File localNode = new File(System.getProperty("user.dir"), "whatsapp-server/node.exe");
            if (localNode.exists()) {
                comandoNode = localNode.getAbsolutePath();
                nodeInstalado = true;
                return;
            }

            Process process = new ProcessBuilder(comandoNode, "-v").start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                nodeInstalado = false;
                System.err.println("Node.js no está instalado o no está en el PATH. Funciones de WhatsApp deshabilitadas.");
            }
        } catch (Exception e) {
            nodeInstalado = false;
            System.err.println("No se pudo detectar Node.js. Funciones de WhatsApp deshabilitadas.");
        }
    }

    public static void iniciarServidorSiAplica() {
        if (!nodeInstalado || servidorIniciado) {
            return;
        }
        
        try {
            File workingDir = new File(System.getProperty("user.dir"), "whatsapp-server");
            if (!workingDir.exists()) {
                System.err.println("El directorio whatsapp-server no existe. No se puede iniciar el bot.");
                return;
            }

            // 1. Intentar apagar un servidor Node.js huérfano antes de iniciar uno nuevo
            try {
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL("http://localhost:3001/api/shutdown").openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(1500);
                conn.setReadTimeout(1500);
                conn.getResponseCode(); // Ejecutar
                System.out.println("-> Se detectó un servidor antiguo. Se ha enviado la señal de apagado...");
                Thread.sleep(2500); // Esperar a que Puppeteer y Node se cierren completamente
            } catch (Exception ignored) {
                // Puerto libre o inaccesible (esperado)
            }

            // 2. Iniciar el nuevo servidor
            ProcessBuilder pb = new ProcessBuilder(comandoNode, "index.js");
            pb.directory(workingDir);
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processNode = pb.start();
            servidorIniciado = true;
            System.out.println("-> Servidor de WhatsApp iniciado localmente.");

            // Add hook to kill node process when Java exits
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (processNode != null && processNode.isAlive()) {
                    processNode.destroyForcibly();
                    System.out.println("Servidor de WhatsApp detenido.");
                }
            }));
            
        } catch (Exception e) {
            System.err.println("Error al intentar iniciar el servidor de WhatsApp: " + e.getMessage());
        }
    }

    public String enviarMensaje(String telefono, String mensaje) {
        if (!nodeInstalado) {
            return "ERR|Node.js no está instalado en este equipo. El envío automático está deshabilitado.";
        }
        if (!servidorIniciado) {
            return "ERR|El servidor de WhatsApp no está iniciado.";
        }

        try {
            URL url = new URL("http://localhost:3001/api/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject payload = new JsonObject();
            payload.addProperty("telefono", telefono);
            payload.addProperty("mensaje", mensaje);
            String jsonInputString = payload.toString();

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                return "OK|Mensaje enviado a " + telefono;
            } else if (responseCode == 503) {
                return "ERR|El sistema de WhatsApp aún no está listo. Revisa la consola y escanea el código QR.";
            } else {
                Scanner s = new Scanner(conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream());
                s.useDelimiter("\\A");
                String responseBody = s.hasNext() ? s.next() : "";
                s.close();
                return "ERR|Falló el envío. Código: " + responseCode + " - " + responseBody;
            }

        } catch (java.net.ConnectException ce) {
            return "{\"status\":\"loading\", \"qr\":\"El servidor de WhatsApp se está iniciando. Por favor, espera unos 10 segundos y vuelve a intentar.\"}";
        } catch (Exception e) {
            return "ERR|Error de conexión con el microservicio de WhatsApp: " + e.getMessage();
        }
    }

    public String obtenerQR() {
        if (!nodeInstalado) {
            return "{\"status\":\"error\", \"qr\":\"Node.js no está instalado\"}";
        }
        if (!servidorIniciado) {
            return "{\"status\":\"error\", \"qr\":\"El servidor de WhatsApp no está iniciado\"}";
        }
        try {
            URL url = new URL("http://localhost:3001/api/qr");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                Scanner s = new Scanner(conn.getInputStream());
                s.useDelimiter("\\A");
                String responseBody = s.hasNext() ? s.next() : "";
                s.close();
                return responseBody;
            } else {
                return "{\"status\":\"error\", \"qr\":\"Error del servidor Node\"}";
            }
        } catch (java.net.ConnectException ce) {
            return "{\"status\":\"loading\", \"qr\":\"El servidor de WhatsApp está arrancando, espera un momento y vuelve a intentar.\"}";
        } catch (Exception e) {
            return "{\"status\":\"error\", \"qr\":\"Error de conexión: " + e.getMessage() + "\"}";
        }
    }
}
