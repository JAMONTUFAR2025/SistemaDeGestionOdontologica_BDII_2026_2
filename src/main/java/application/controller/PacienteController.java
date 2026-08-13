package application.controller;

import application.model.dao.PacienteDAO;
import application.model.entity.Paciente;

public class PacienteController extends BaseController {

    private PacienteDAO pacienteDAO;

    public PacienteController() {
        super();
        this.pacienteDAO = new PacienteDAO();
    }

    public String registrarPaciente(String jsonPaciente) {
        System.out.println("-> Petición de registro/actualización de paciente recibida en Java: " + jsonPaciente);
        try {
            Paciente paciente = gson.fromJson(jsonPaciente, Paciente.class);
            if (paciente == null) {
                return "ERR|Datos de paciente nulos.";
            }
            System.out.println("-> Gson deserializó correctamente a: " + paciente.getNombreCompleto());

            boolean esActualizacion = (paciente.getIdPaciente() > 0) ||
                                      (paciente.getIdentidadOriginal() != null && !paciente.getIdentidadOriginal().trim().isEmpty());

            if (esActualizacion) {
                if (paciente.getIdentidad() != null && !paciente.getIdentidad().trim().isEmpty()) {
                    if (!paciente.getIdentidad().equals(paciente.getIdentidadOriginal()) && pacienteDAO.existe(paciente.getIdentidad())) {
                        return "ERR|La nueva identidad ya está registrada para otro paciente.";
                    }
                }
                return pacienteDAO.actualizar(paciente);
            } else {
                if (paciente.getIdentidad() != null && !paciente.getIdentidad().trim().isEmpty() && pacienteDAO.existe(paciente.getIdentidad())) {
                    return "ERR|El paciente con esa identidad ya existe.";
                }
                return pacienteDAO.registrar(paciente);
            }
        } catch (Exception e) {
            System.err.println("-> ERROR procesando paciente: " + e.getMessage());
            e.printStackTrace();
            return "ERR|Error al procesar los datos del formulario: " + e.getMessage();
        }
    }

    public String obtenerPacientes() {
        try {
            java.util.List<Paciente> lista = pacienteDAO.obtenerPacientes();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerPacientes: " + t.getMessage());
            return "[]";
        }
    }

    public String eliminarPaciente(String identidad) {
        try {
            boolean exito = pacienteDAO.eliminarPaciente(identidad);
            if (exito) {
                return "OK|Paciente eliminado exitosamente.";
            } else {
                return "ERR|No se pudo eliminar el paciente.";
            }
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarPaciente: " + t.getMessage());
            return "ERR|Error al eliminar paciente: " + t.getMessage();
        }
    }


    public String reactivarPaciente(String correoAdmin, String passAdmin, String identidad) {
        try {
            application.model.dao.UserDAO userDAO = new application.model.dao.UserDAO();
            if (!userDAO.autenticarUsuario(correoAdmin, passAdmin)) {
                return "ERR|Contraseña incorrecta.";
            }
            if (!"Administrador".equals(userDAO.obtenerRolPorCorreo(correoAdmin))) {
                return "ERR|Solo un Administrador puede reactivar registros.";
            }

            boolean exito = pacienteDAO.reactivarPaciente(identidad);
            if (exito) {
                return "OK|Paciente reactivado exitosamente.";
            } else {
                return "ERR|No se pudo reactivar el paciente.";
            }
        } catch (Throwable t) {
            System.err.println("-> ERROR en reactivarPaciente: " + t.getMessage());
            return "ERR|Error al reactivar paciente: " + t.getMessage();
        }
    }
}
