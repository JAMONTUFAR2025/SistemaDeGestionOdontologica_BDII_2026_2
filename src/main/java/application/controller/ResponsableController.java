package application.controller;

import application.model.dao.ResponsableDAO;
import application.model.entity.Responsable;

public class ResponsableController extends BaseController {

    private ResponsableDAO responsableDAO;

    public ResponsableController() {
        super();
        this.responsableDAO = new ResponsableDAO();
    }

    public String registrarResponsable(String jsonResponsable) {
        System.out.println("-> Petición de registro/actualización de responsable recibida en Java: " + jsonResponsable);
        try {
            Responsable responsable = gson.fromJson(jsonResponsable, Responsable.class);
            if (responsable == null || responsable.getIdentidad() == null || responsable.getIdentidad().trim().isEmpty()) {
                return "ERR|La identidad (DNI) del responsable es requerida.";
            }

            boolean esActualizacion = responsable.getIdResponsable() > 0; // If ID > 0, it's update

            if (esActualizacion) {
                return responsableDAO.actualizar(responsable.getIdResponsable(), responsable.getIdentidad(), responsable.getNombreCompleto(), responsable.getTelefono(), responsable.getCorreo(), responsable.getParentesco());
            } else {
                if (responsableDAO.existe(responsable.getIdentidad())) {
                    return "ERR|El responsable con esa identidad ya existe.";
                }
                return responsableDAO.registrar(responsable);
            }
        } catch (Exception e) {
            System.err.println("-> ERROR procesando responsable: " + e.getMessage());
            e.printStackTrace();
            return "ERR|Error al procesar los datos del formulario: " + e.getMessage();
        }
    }

    public String obtenerResponsables() {
        try {
            java.util.List<java.util.Map<String, Object>> lista = responsableDAO.obtenerTodos(true);
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerResponsables: " + t.getMessage());
            return "[]";
        }
    }

    public String eliminarResponsable(int idResponsable) {
        try {
            boolean exito = responsableDAO.eliminar(idResponsable);
            if (exito) {
                return "OK|Responsable eliminado exitosamente.";
            } else {
                return "ERR|No se pudo eliminar el responsable.";
            }
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarResponsable: " + t.getMessage());
            return "ERR|Error al eliminar responsable: " + t.getMessage();
        }
    }

    public String reactivarResponsable(String correoAdmin, String passAdmin, int idResponsable) {
        try {
            application.model.dao.UserDAO userDAO = new application.model.dao.UserDAO();
            if (!userDAO.autenticarUsuario(correoAdmin, passAdmin)) {
                return "ERR|Contraseña incorrecta.";
            }
            if (!"Administrador".equals(userDAO.obtenerRolPorCorreo(correoAdmin))) {
                return "ERR|Solo un Administrador puede reactivar registros.";
            }

            boolean exito = responsableDAO.reactivar(idResponsable);
            if (exito) {
                return "OK|Responsable reactivado exitosamente.";
            } else {
                return "ERR|No se pudo reactivar el responsable.";
            }
        } catch (Throwable t) {
            System.err.println("-> ERROR en reactivarResponsable: " + t.getMessage());
            return "ERR|Error al reactivar responsable: " + t.getMessage();
        }
    }
}
