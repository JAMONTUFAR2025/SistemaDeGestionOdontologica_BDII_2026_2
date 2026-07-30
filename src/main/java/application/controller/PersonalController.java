package application.controller;

public class PersonalController extends BaseController {

    private application.model.dao.UserDAO userDAO;

    public PersonalController() {
        super();
        this.userDAO = new application.model.dao.UserDAO();
    }

    public String obtenerEspecialidades() {
        System.out.println("-> PersonalController: solicitando especialidades...");
        application.model.dao.EspecialidadDAO especialidadDAO = new application.model.dao.EspecialidadDAO();
        java.util.List<application.model.entity.Especialidad> lista = especialidadDAO.obtenerEspecialidades();
        System.out.println("-> Especialidades encontradas: " + lista.size());
        return gson.toJson(lista);
    }

    public String agregarEspecialidad(String nombre) {
        try {
            application.model.dao.EspecialidadDAO especialidadDAO = new application.model.dao.EspecialidadDAO();
            boolean exito = especialidadDAO.agregarEspecialidad(nombre.trim());
            return exito ? "OK|Especialidad registrada correctamente." : "ERR|No se pudo registrar la especialidad.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String actualizarEspecialidad(String idStr, String nombre) {
        try {
            int id = Integer.parseInt(idStr.trim());
            application.model.dao.EspecialidadDAO especialidadDAO = new application.model.dao.EspecialidadDAO();
            boolean exito = especialidadDAO.actualizarEspecialidad(id, nombre.trim());
            return exito ? "OK|Especialidad actualizada correctamente." : "ERR|No se pudo actualizar la especialidad.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String eliminarEspecialidad(String idStr) {
        try {
            int id = Integer.parseInt(idStr.trim());
            application.model.dao.EspecialidadDAO especialidadDAO = new application.model.dao.EspecialidadDAO();
            boolean exito = especialidadDAO.eliminarEspecialidad(id);
            return exito ? "OK|Especialidad eliminada correctamente."
                    : "ERR|No se pudo eliminar. Puede estar en uso por algún médico.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String registrarPersonalMedico(String jsonPersonal) {
        System.out.println("-> Peticion de registro de medico en Java: " + jsonPersonal);
        try {
            application.model.entity.PersonalMedico pm = gson.fromJson(jsonPersonal,
                    application.model.entity.PersonalMedico.class);
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();

            boolean exito = pmDAO.registrarPersonalYUsuario(pm);
            if (exito) {
                return "OK|Personal y usuario registrados con éxito en la base de datos.";
            } else {
                return "ERR|Ocurrió un error al registrar. Es posible que el correo o identidad ya existan.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR|Error procesando datos: " + e.getMessage();
        }
    }

    public String obtenerCorreosActivos() {
        try {
            java.util.List<String> correos = userDAO.obtenerCorreosActivos();
            return gson.toJson(correos);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerCorreosActivos: " + t.getMessage());
            return "[]";
        }
    }

    public String registrarUsuarioSolo(String jsonUsuario) {
        System.out.println("-> Peticion de registro de usuario en Java: " + jsonUsuario);
        System.out.flush();
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonUsuario).getAsJsonObject();
            String correo = obj.get("correo").getAsString();
            String contrasenia = obj.get("contrasenia").getAsString();
            String rolSistema = obj.get("rol_sistema").getAsString();
            String estado = obj.get("estado").getAsString();

            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            boolean exito = pmDAO.registrarUsuarioSolo(correo, contrasenia, rolSistema, estado);
            if (exito) {
                return "OK|Usuario registrado exitosamente.";
            } else {
                return "ERR|No se pudo registrar. Es posible que el correo ya exista.";
            }
        } catch (Throwable t) {
            System.err.println("-> ERROR en registrarUsuarioSolo: " + t.getClass().getName() + " - " + t.getMessage());
            t.printStackTrace();
            System.err.flush();
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

    public String obtenerUsuariosSinMedico() {
        try {
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            java.util.List<java.util.Map<String, String>> lista = pmDAO.obtenerUsuariosSinMedico();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println(
                    "-> ERROR en obtenerUsuariosSinMedico: " + t.getClass().getName() + " - " + t.getMessage());
            t.printStackTrace();
            System.err.flush();
            return "[]";
        }
    }

    public String registrarMedicoConUsuario(String jsonMedico) {
        System.out.println("-> Peticion de registro de medico vinculado en Java: " + jsonMedico);
        System.out.flush();
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonMedico).getAsJsonObject();
            int idUsuario = obj.get("id_usuario").getAsInt();

            application.model.entity.PersonalMedico pm = gson.fromJson(jsonMedico,
                    application.model.entity.PersonalMedico.class);
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();

            boolean exito = pmDAO.registrarPersonalYVincular(pm, idUsuario);
            if (exito) {
                return "OK|Médico registrado y vinculado al usuario exitosamente.";
            } else {
                return "ERR|Error al registrar. Verifica que la identidad no exista ya.";
            }
        } catch (Throwable t) {
            System.err.println(
                    "-> ERROR en registrarMedicoConUsuario: " + t.getClass().getName() + " - " + t.getMessage());
            t.printStackTrace();
            System.err.flush();
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

    public String obtenerUsuarios() {
        try {
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            java.util.List<java.util.Map<String, Object>> lista = pmDAO.obtenerUsuarios();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerUsuarios: " + t.getMessage());
            return "[]";
        }
    }

    public String obtenerPersonalMedico() {
        try {
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            java.util.List<java.util.Map<String, Object>> lista = pmDAO.obtenerPersonalMedico();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerPersonalMedico: " + t.getMessage());
            return "[]";
        }
    }

    public String actualizarUsuario(String jsonUsuario) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonUsuario).getAsJsonObject();
            int idUsuario = obj.get("id_usuario").getAsInt();
            String rolSistema = obj.get("rol_sistema").getAsString();
            String contrasenia = obj.has("contrasenia") ? obj.get("contrasenia").getAsString() : "";
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            boolean exito = pmDAO.actualizarUsuario(idUsuario, rolSistema, contrasenia);
            return exito ? "OK|Usuario actualizado exitosamente." : "ERR|No se pudo actualizar el usuario.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String inactivarUsuario(String idUsuarioStr) {
        try {
            int idUsuario = Integer.parseInt(idUsuarioStr.trim());
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            boolean exito = pmDAO.inactivarUsuario(idUsuario);
            return exito ? "OK|Usuario inactivado correctamente." : "ERR|No se pudo inactivar el usuario.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String actualizarPersonalMedico(String jsonMedico) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonMedico).getAsJsonObject();
            String identidad = obj.get("identidad").getAsString();
            String nombreCompleto = obj.get("nombre_completo").getAsString();
            String telefono = obj.get("telefono").getAsString();
            int idEspecialidades = obj.has("id_especialidades")
                    ? obj.get("id_especialidades").getAsInt()
                    : (obj.has("id_especialidad") ? obj.get("id_especialidad").getAsInt() : 0);
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            boolean exito = pmDAO.actualizarPersonalMedico(identidad, nombreCompleto, telefono, idEspecialidades);
            return exito ? "OK|Médico actualizado exitosamente." : "ERR|No se pudo actualizar el médico.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String inactivarPersonalMedico(String identidad) {
        try {
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            boolean exito = pmDAO.inactivarPersonalMedico(identidad);
            return exito ? "OK|Médico inactivado correctamente." : "ERR|No se pudo inactivar el médico.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }
}
