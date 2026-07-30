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
            fechaHoraStr = fechaHoraStr.replace(" ", "T");
            cita.setFechaHora(java.time.LocalDateTime.parse(fechaHoraStr));

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
}
