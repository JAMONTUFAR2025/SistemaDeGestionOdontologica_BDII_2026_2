package application.model.connection;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

    // Configuración del correo emisor
    // ATENCION: Cambia "tu_contrasenia_de_aplicacion" por la generada en tu cuenta de Google.
    private static final String CORREO_REMITENTE = "anneralessandroteruelpineda@gmail.com";
    private static final String CONTRASENIA_APP = "xhvbrywbmetiylhb"; 
    
    public static boolean enviarCorreoNuevaContrasenia(String correoDestino, String codigoSeguridad) {
        
        // 1. Configurar las propiedades del servidor SMTP de Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        // 2. Crear la sesion con autenticación
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(CORREO_REMITENTE, CONTRASENIA_APP);
            }
        });

        try {
            // 3. Redactar el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(CORREO_REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
            message.setSubject("Código de Seguridad - SOE Odontología");
            
            String cuerpoHtml = "<div style='font-family: Arial, sans-serif; padding: 20px; color: #333;'>"
                              + "<h2 style='color: #007bf0;'>Clínica Odontológica SOE</h2>"
                              + "<p>Hola,</p>"
                              + "<p>Hemos recibido una solicitud para restablecer tu contraseña. Tu código de seguridad de 6 dígitos es:</p>"
                              + "<h1 style='background-color: #f4f4f4; padding: 15px; display: inline-block; border-radius: 5px; letter-spacing: 5px; color: #2c3e50;'>" + codigoSeguridad + "</h1>"
                              + "<p>Ingresa este código en el sistema para poder crear tu nueva contraseña.</p>"
                              + "<p><i>Si tú no solicitaste esto, ignora este mensaje.</i></p>"
                              + "<br>"
                              + "<p>Saludos cordiales,<br>El equipo de soporte.</p>"
                              + "</div>";
                              
            message.setContent(cuerpoHtml, "text/html; charset=utf-8");

            // 4. Enviar el correo
            Transport.send(message);
            System.out.println("-> Correo de recuperacion enviado exitosamente a: " + correoDestino);
            return true;

        } catch (MessagingException e) {
            System.err.println("-> Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
