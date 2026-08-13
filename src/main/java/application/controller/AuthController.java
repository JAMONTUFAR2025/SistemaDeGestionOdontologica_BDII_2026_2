package application.controller;

import application.model.dao.UserDAO;

public class AuthController extends BaseController {

    private UserDAO userDAO;
    private java.util.Map<String, String> codigosRecuperacion;

    public AuthController() {
        super();
        this.userDAO = new UserDAO();
        this.codigosRecuperacion = new java.util.HashMap<>();
    }

    public String obtenerRolActual() {
        return rolUsuarioActual;
    }

    public boolean login(String identificador, String contrasenia) {
        System.out.println("Intentando iniciar sesion con: " + identificador);
        boolean exito = userDAO.autenticarUsuario(identificador, contrasenia);
        if (exito) {
            System.out.println("-> Inicio de sesion EXITOSO para: " + identificador);
            rolUsuarioActual = userDAO.obtenerRolPorCorreo(identificador);
            idPersonalMedicoActual = userDAO.obtenerIdMedicoPorCorreo(identificador);
            idUsuarioLoginActual = userDAO.obtenerIdLoginPorCorreo(identificador);
            correoUsuarioActual = identificador; // Store username or email (used mostly for UI)
            nombreUsuarioActual = userDAO.obtenerNombreUsuario(identificador);
            nombreMedicoActual = userDAO.obtenerNombreMedicoPorCorreo(identificador);
        } else {
            System.out.println("-> Fila no encontrada o credenciales INCORRECTAS para: " + identificador);
            clearSession();
        }
        return exito;
    }

    /** Retorna el nombre de usuario de login, de lo contrario devuelve el identificador usado */
    public String obtenerNombreBienvenida() {
        if (nombreUsuarioActual != null && !nombreUsuarioActual.trim().isEmpty()) {
            return nombreUsuarioActual;
        }
        return correoUsuarioActual;
    }

    /** Cierra la sesión limpiando todas las variables estáticas. */
    public void logout() {
        clearSession();
    }

    /** Retorna el id_usuario_login del usuario actualmente en sesión. */
    public String obtenerIdUsuarioActual() {
        return idUsuarioLoginActual != null ? String.valueOf(idUsuarioLoginActual) : "0";
    }

    public String enviarCodigoRecuperacion(String correo) {
        System.out.println("Solicitud de codigo para correo: " + correo);

        String correoReal = userDAO.obtenerCorreoReal(correo);
        if (correoReal != null && !correoReal.isEmpty()) {
            String codigoSeguridad = String.format("%06d", (int) (Math.random() * 1000000));
            codigosRecuperacion.put(correo, codigoSeguridad);

            System.out.println("-> Enviando correo a " + correoReal + "...");
            boolean enviado = application.model.connection.EmailService.enviarCorreoNuevaContrasenia(correoReal,
                    codigoSeguridad);
            if (enviado) {
                return "OK|Código enviado con éxito al correo registrado. Revisa tu bandeja de entrada.";
            } else {
                return "ERR|El código se generó, pero hubo un error al enviar el correo.";
            }
        } else {
            return "ERR|No se encontró ningún usuario activo con ese correo electrónico.";
        }
    }

    public String verificarCodigo(String identificador, String codigoIngresado) {
        String codigoReal = codigosRecuperacion.get(identificador);
        if (codigoReal != null && codigoReal.equals(codigoIngresado)) {
            return "OK|Código verificado correctamente.";
        } else {
            return "ERR|El código ingresado es incorrecto o ha expirado.";
        }
    }

    public String restablecerContrasenia(String identificador, String codigoIngresado, String nuevaContrasenia) {
        String codigoReal = codigosRecuperacion.get(identificador);
        if (codigoReal == null || !codigoReal.equals(codigoIngresado)) {
            return "ERR|Intento inválido de cambio de contraseña.";
        }

        boolean actualizado = userDAO.actualizarContrasenia(identificador, nuevaContrasenia);

        if (actualizado) {
            codigosRecuperacion.remove(identificador);
            return "OK|Contraseña actualizada exitosamente. Ya puedes iniciar sesión.";
        } else {
            return "ERR|Ocurrió un error al guardar la nueva contraseña en la base de datos.";
        }
    }
}
