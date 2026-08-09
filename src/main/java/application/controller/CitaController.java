package application.controller;

public class CitaController extends BaseController {

    public CitaController() {
        super();
    }

    public String agendarCita(String jsonCita) {
        System.out.println("-> Peticion para agendar cita: " + jsonCita);
        try {
            application.model.entity.Cita cita = gson.fromJson(jsonCita, application.model.entity.Cita.class);
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonCita).getAsJsonObject();
            String fechaHoraStr = obj.get("fecha_hora").getAsString(); 
            java.time.LocalDateTime dt = application.controller.BaseController.parseDateTimeRobust(fechaHoraStr);
            if (dt.toLocalDate().isBefore(java.time.LocalDate.now())) {
                return "ERR|No puedes agendar citas en días anteriores. Por favor, selecciona una fecha actual o futura.";
            }
            cita.setFechaHora(dt);

            cita.setIdPacientes(obj.get("id_pacientes").getAsInt());
            cita.setIdPersonalMedico(obj.get("id_personal_medico").getAsInt());

            application.model.dao.CitaDAO citaDAO = new application.model.dao.CitaDAO();
            boolean exito = citaDAO.agendarCita(cita);
            
            return exito ? "OK|Cita agendada exitosamente." : "ERR|Error al guardar la cita en la base de datos.";
        } catch (Exception e) {
            System.err.println("Error en agendarCita: " + e.getMessage());
            e.printStackTrace();
            return "ERR|Error interno al procesar la cita: " + e.getMessage();
        }
    }

    public String obtenerCitasHoyDelUsuario() {
        application.model.dao.CitaDAO citaDAO = new application.model.dao.CitaDAO();
        java.util.List<java.util.Map<String, String>> citas = citaDAO.obtenerCitasHoy(idPersonalMedicoActual, rolUsuarioActual);
        return gson.toJson(citas);
    }

    public String obtenerProximasCitasDelUsuario() {
        application.model.dao.CitaDAO citaDAO = new application.model.dao.CitaDAO();
        java.util.List<java.util.Map<String, String>> citas = citaDAO.obtenerProximasCitas(idPersonalMedicoActual, rolUsuarioActual);
        return gson.toJson(citas);
    }

    public String obtenerCitaPorId(String idStr) {
        try {
            int id = Integer.parseInt(idStr.trim());
            application.model.dao.CitaDAO citaDAO = new application.model.dao.CitaDAO();
            java.util.Map<String, String> cita = citaDAO.obtenerCitaPorId(id);
            return gson.toJson(cita);
        } catch (Exception e) {
            System.err.println("Error en obtenerCitaPorId: " + e.getMessage());
            return "{}";
        }
    }

    public String actualizarCita(String jsonCita) {
        System.out.println("-> Peticion para actualizar cita: " + jsonCita);
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonCita).getAsJsonObject();
            application.model.entity.Cita cita = new application.model.entity.Cita();
            cita.setIdCitas(obj.get("id_citas").getAsInt());
            cita.setIdPacientes(obj.get("id_pacientes").getAsInt());
            if (obj.has("id_personal_medico") && !obj.get("id_personal_medico").isJsonNull()
                    && !obj.get("id_personal_medico").getAsString().isEmpty()) {
                cita.setIdPersonalMedico(obj.get("id_personal_medico").getAsInt());
            }
            String fechaHoraStr = obj.get("fecha_hora").getAsString();
            java.time.LocalDateTime dt = application.controller.BaseController.parseDateTimeRobust(fechaHoraStr);
            if (dt.toLocalDate().isBefore(java.time.LocalDate.now())) {
                return "ERR|No puedes agendar citas en días anteriores. Por favor, selecciona una fecha actual o futura.";
            }
            cita.setFechaHora(dt);
            cita.setEstado(obj.get("estado").getAsString());
            cita.setMotivoCita(obj.has("motivoCita") ? obj.get("motivoCita").getAsString() : "");

            application.model.dao.CitaDAO citaDAO = new application.model.dao.CitaDAO();
            boolean exito = citaDAO.actualizarCita(cita);
            return exito ? "OK|Cita actualizada exitosamente." : "ERR|Error al actualizar la cita en la base de datos.";
        } catch (Exception e) {
            System.err.println("Error en actualizarCita: " + e.getMessage());
            e.printStackTrace();
            return "ERR|Error interno al procesar la cita: " + e.getMessage();
        }
    }
}
