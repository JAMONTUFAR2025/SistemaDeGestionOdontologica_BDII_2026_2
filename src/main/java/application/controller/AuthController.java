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

    public boolean login(String correo, String contrasenia) {
        System.out.println("Intentando iniciar sesion con: " + correo);
        boolean exito = userDAO.autenticarUsuario(correo, contrasenia);
        if (exito) {
            System.out.println("-> Inicio de sesion EXITOSO para: " + correo);
            rolUsuarioActual = userDAO.obtenerRolPorCorreo(correo);
            idPersonalMedicoActual = userDAO.obtenerIdMedicoPorCorreo(correo);
            idUsuarioLoginActual = userDAO.obtenerIdLoginPorCorreo(correo);
            correoUsuarioActual = correo;
            nombreMedicoActual = userDAO.obtenerNombreMedicoPorCorreo(correo);
        } else {
            System.out.println("-> Fila no encontrada o credenciales INCORRECTAS para: " + correo);
            clearSession();
        }
        return exito;
    }

    /** Retorna el nombre del médico si está asignado, de lo contrario devuelve el correo del usuario */
    public String obtenerNombreBienvenida() {
        if (nombreMedicoActual != null && !nombreMedicoActual.trim().isEmpty()) {
            return "Dr(a). " + nombreMedicoActual;
        }
        return correoUsuarioActual;
    }

    /** Cierra la sesión limpiando todas las variables estáticas. */
    public void logout() {
        clearSession();
    }

    /** Retorna el id_usuarios_login del usuario actualmente en sesión. */
    public String obtenerIdUsuarioActual() {
        return idUsuarioLoginActual != null ? String.valueOf(idUsuarioLoginActual) : "0";
    }

    public String enviarCodigoRecuperacion(String correo) {
        System.out.println("Solicitud de codigo para: " + correo);

        boolean existe = userDAO.verificarCorreoExistente(correo);
        if (existe) {
            String codigoSeguridad = String.format("%06d", (int) (Math.random() * 1000000));
            codigosRecuperacion.put(correo, codigoSeguridad);

            System.out.println("-> Enviando correo a " + correo + "...");
            boolean enviado = application.model.connection.EmailService.enviarCorreoNuevaContrasenia(correo,
                    codigoSeguridad);
            if (enviado) {
                return "OK|Código enviado con éxito. Revisa tu bandeja de entrada.";
            } else {
                return "ERR|El código se generó, pero hubo un error al enviar el correo.";
            }
        } else {
            return "ERR|No se encontro ningun usuario activo con ese correo.";
        }
    }

    public String verificarCodigo(String correo, String codigoIngresado) {
        String codigoReal = codigosRecuperacion.get(correo);
        if (codigoReal != null && codigoReal.equals(codigoIngresado)) {
            return "OK|Código verificado correctamente.";
        } else {
            return "ERR|El código ingresado es incorrecto o ha expirado.";
        }
    }

    public String restablecerContrasenia(String correo, String codigoIngresado, String nuevaContrasenia) {
        String codigoReal = codigosRecuperacion.get(correo);
        if (codigoReal == null || !codigoReal.equals(codigoIngresado)) {
            return "ERR|Intento inválido de cambio de contraseña.";
        }

        boolean actualizado = userDAO.actualizarContrasenia(correo, nuevaContrasenia);

        if (actualizado) {
            codigosRecuperacion.remove(correo);
            return "OK|Contraseña actualizada exitosamente. Ya puedes iniciar sesión.";
        } else {
            return "ERR|Ocurrió un error al guardar la nueva contraseña en la base de datos.";
        }
    }
}
