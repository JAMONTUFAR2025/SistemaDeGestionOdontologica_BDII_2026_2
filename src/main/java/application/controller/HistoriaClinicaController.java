package application.controller;

/**
 * Controller para el módulo de Historia Clínica.
 * Expuesto al WebView JavaFX como: window.historiaController
 *
 * Métodos disponibles desde JavaScript:
 *   - buscarExpediente(identidad)       → JSON con paciente + expediente + evoluciones
 *   - registrarExpediente(jsonData)     → "OK|..." / "ERR|..."
 *   - agregarEvolucion(jsonData)        → "OK|..." / "ERR|..."
 *   - obtenerMedicos()                  → JSON array de médicos activos
 */
public class HistoriaClinicaController extends BaseController {

    public HistoriaClinicaController() {
        super();
    }

    // ------------------------------------------------------------------
    // BUSCAR EXPEDIENTE COMPLETO POR IDENTIDAD DEL PACIENTE
    // ------------------------------------------------------------------

    /**
     * Busca el expediente completo de un paciente por su número de identidad.
     * Retorna un JSON con: { paciente, expediente, evoluciones } o "NOT_FOUND" si no existe.
     */
    public String buscarExpediente(String identidad) {
        try {
            if (identidad == null || identidad.trim().isEmpty()) {
                return "ERR|Ingrese un número de identidad para buscar.";
            }
            application.model.dao.HistoriaClinicaDAO dao = new application.model.dao.HistoriaClinicaDAO();
            java.util.Map<String, Object> resultado = dao.buscarExpedienteCompleto(identidad.trim());
            if (resultado == null) {
                return "NOT_FOUND";
            }
            return gson.toJson(resultado);
        } catch (Throwable t) {
            System.err.println("-> ERROR en buscarExpediente: " + t.getMessage());
            return "ERR|Error al realizar la búsqueda: " + t.getMessage();
        }
    }

    // ------------------------------------------------------------------
    // REGISTRAR EXPEDIENTE BASE (Historia Clínica inicial)
    // ------------------------------------------------------------------

    /**
     * Crea un nuevo Expediente_Base para un paciente.
     * JSON esperado: { identidad_paciente, remitido_por, antecedentes_patologicos,
     *   antecedentes_odontologicos, antecedentes_quirurgicos, antecedentes_ginecobstetros,
     *   habitos_toxicos, farmacos_uso_habitual, reaccion_anestesicos, especifique_anestesia,
     *   complicaciones_tratamientos_previos, habitos_bucales, frecuencia_cepillado,
     *   tipo_cepillo_cerdas, uso_hilo_dental, tipo_mordida,
     *   tejidos_blandos_observacion, diagnostico_presuntivo, observaciones_generales }
     */
    public String registrarExpediente(String jsonData) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonData).getAsJsonObject();

            // Resolver id_pacientes desde identidad
            String identidad = obj.has("identidad_paciente") ? obj.get("identidad_paciente").getAsString() : "";
            if (identidad.trim().isEmpty()) return "ERR|La identidad del paciente es obligatoria.";

            // Buscar id_paciente por identidad
            int idPaciente = resolverIdPaciente(identidad.trim());
            if (idPaciente <= 0) {
                return "ERR|No se encontró ningún paciente con identidad: " + identidad +
                       ". Debe registrar al paciente primero.";
            }

            String remitidoPor       = str(obj, "remitido_por");
            String antPatologicos    = str(obj, "antecedentes_patologicos");
            String antOdontologicos  = str(obj, "antecedentes_odontologicos");
            String antQuirurgicos    = str(obj, "antecedentes_quirurgicos");
            String antGineco         = str(obj, "antecedentes_ginecobstetros");
            String habitosToxicos    = str(obj, "habitos_toxicos");
            String farmacos          = str(obj, "farmacos_uso_habitual");
            boolean reaccionAnest     = "true".equalsIgnoreCase(str(obj, "reaccion_anestesicos")) || "1".equals(str(obj, "reaccion_anestesicos"));
            String especAnest        = str(obj, "especifique_anestesia");
            String complicaciones    = str(obj, "complicaciones_tratamientos_previos");
            String habitosBucales    = str(obj, "habitos_bucales");
            String frecCepillado     = str(obj, "frecuencia_cepillado");
            String tipoCerdas        = str(obj, "tipo_cepillo_cerdas");
            String usoHilo           = str(obj, "uso_hilo_dental");
            String tipoMordida       = str(obj, "tipo_mordida");

            application.model.dao.HistoriaClinicaDAO dao = new application.model.dao.HistoriaClinicaDAO();
            return dao.registrarExpedienteBase(idPaciente, remitidoPor,
                    antPatologicos, antOdontologicos, antQuirurgicos, antGineco,
                    habitosToxicos, farmacos, reaccionAnest, especAnest, complicaciones,
                    habitosBucales, frecCepillado, tipoCerdas, usoHilo, tipoMordida);
        } catch (Throwable t) {
            System.err.println("-> ERROR en registrarExpediente: " + t.getMessage());
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

    // ------------------------------------------------------------------
    // AGREGAR EVOLUCIÓN CLÍNICA (nueva consulta)
    // ------------------------------------------------------------------

    /**
     * Registra una nueva evolución/consulta en Evolucion_Clinica.
     * JSON esperado: { identidad_paciente, id_medico, fecha_consulta,
     *   motivo_consulta, sintoma_principal, presion_arterial, pulso_cardiaco,
     *   temperatura, tejidos_blandos_observacion, diagnostico,
     *   estado_odontograma, pago_abono, observaciones }
     */
    public String agregarEvolucion(String jsonData) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonData).getAsJsonObject();

            String identidad = obj.has("identidad_paciente") ? obj.get("identidad_paciente").getAsString() : "";
            if (identidad.trim().isEmpty()) return "ERR|La identidad del paciente es obligatoria.";

            int idPaciente = resolverIdPaciente(identidad.trim());
            if (idPaciente <= 0) return "ERR|Paciente no encontrado con identidad: " + identidad;

            // Obtener el id_expediente_base del paciente
            int idExpediente = resolverIdExpediente(idPaciente);
            if (idExpediente <= 0) {
                return "ERR|El paciente no tiene Expediente Base. Registre primero la Historia Clínica inicial.";
            }

            int idMedico = obj.has("id_medico") ? obj.get("id_medico").getAsInt() : 0;
            if (idMedico <= 0) return "ERR|Debe seleccionar un médico tratante.";

            // Nuevos campos opcionales
            Integer idCitas = (obj.has("id_cita") && !obj.get("id_cita").isJsonNull())
                    ? obj.get("id_cita").getAsInt() : null;
            Integer idCatalogoProcedimientos = (obj.has("id_catalogo_procedimientos") && !obj.get("id_catalogo_procedimientos").isJsonNull())
                    ? obj.get("id_catalogo_procedimientos").getAsInt() : null;

            String motivoConsulta   = str(obj, "motivo_consulta");
            String sintomaPrincipal = str(obj, "sintoma_principal");
            String presionArterial  = str(obj, "presion_arterial");
            String pulsoCardiaco    = str(obj, "pulso_cardiaco");
            String temperatura      = str(obj, "temperatura");
            String tejidosBlandos   = str(obj, "tejidos_blandos_observacion");
            String diagnostico      = str(obj, "diagnostico");
            String odontograma      = str(obj, "estado_odontograma");
            String observaciones    = str(obj, "observaciones");
            
            Integer ps = null, pd = null;
            if (presionArterial != null && presionArterial.contains("/")) {
                try {
                    String[] parts = presionArterial.split("/");
                    ps = Integer.parseInt(parts[0].trim());
                    pd = Integer.parseInt(parts[1].trim());
                } catch (Exception ignored) {}
            }
            Integer pulso = null;
            if (pulsoCardiaco != null && !pulsoCardiaco.isEmpty()) {
                try { pulso = Integer.parseInt(pulsoCardiaco.replaceAll("\\D", "")); } catch (Exception ignored) {}
            }
            Double temp = null;
            if (temperatura != null && !temperatura.isEmpty()) {
                try { temp = Double.parseDouble(temperatura.replaceAll("[^0-9.]", "")); } catch (Exception ignored) {}
            }

            application.model.dao.HistoriaClinicaDAO dao = new application.model.dao.HistoriaClinicaDAO();
            return dao.registrarEvolucion(idExpediente, idMedico,
                    idCitas, idCatalogoProcedimientos,
                    motivoConsulta, sintomaPrincipal, null, // historiaEnfermedadActual
                    ps, pd, pulso, temp,
                    tejidosBlandos, diagnostico, odontograma, observaciones);

        } catch (Throwable t) {
            System.err.println("-> ERROR en agregarEvolucion: " + t.getMessage());
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

    // ------------------------------------------------------------------
    // ACTUALIZAR Y ELIMINAR EXPEDIENTE BASE
    // ------------------------------------------------------------------

    public String actualizarExpediente(String jsonData) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonData).getAsJsonObject();

            String identidad = obj.has("identidad_paciente") ? obj.get("identidad_paciente").getAsString() : "";
            if (identidad.trim().isEmpty()) return "ERR|La identidad del paciente es obligatoria.";

            int idPaciente = resolverIdPaciente(identidad.trim());
            if (idPaciente <= 0) {
                return "ERR|No se encontró ningún paciente con identidad: " + identidad;
            }

            String remitidoPor       = str(obj, "remitido_por");
            String antPatologicos    = str(obj, "antecedentes_patologicos");
            String antOdontologicos  = str(obj, "antecedentes_odontologicos");
            String antQuirurgicos    = str(obj, "antecedentes_quirurgicos");
            String antGineco         = str(obj, "antecedentes_ginecobstetros");
            String habitosToxicos    = str(obj, "habitos_toxicos");
            String farmacos          = str(obj, "farmacos_uso_habitual");
            boolean reaccionAnest     = "true".equalsIgnoreCase(str(obj, "reaccion_anestesicos")) || "1".equals(str(obj, "reaccion_anestesicos"));
            String especAnest        = str(obj, "especifique_anestesia");
            String complicaciones    = str(obj, "complicaciones_tratamientos_previos");
            String habitosBucales    = str(obj, "habitos_bucales");
            String frecCepillado     = str(obj, "frecuencia_cepillado");
            String tipoCerdas        = str(obj, "tipo_cepillo_cerdas");
            String usoHilo           = str(obj, "uso_hilo_dental");
            String tipoMordida       = str(obj, "tipo_mordida");

            application.model.dao.HistoriaClinicaDAO dao = new application.model.dao.HistoriaClinicaDAO();
            return dao.actualizarExpedienteBase(idPaciente, remitidoPor,
                    antPatologicos, antOdontologicos, antQuirurgicos, antGineco,
                    habitosToxicos, farmacos, reaccionAnest, especAnest, complicaciones,
                    habitosBucales, frecCepillado, tipoCerdas, usoHilo, tipoMordida);
        } catch (Throwable t) {
            System.err.println("-> ERROR en actualizarExpediente: " + t.getMessage());
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

    public String eliminarExpediente(String identidad) {
        try {
            if (identidad == null || identidad.trim().isEmpty()) {
                return "ERR|La identidad del paciente es obligatoria.";
            }
            int idPaciente = resolverIdPaciente(identidad.trim());
            if (idPaciente <= 0) {
                return "ERR|No se encontró ningún paciente con identidad: " + identidad;
            }

            application.model.dao.HistoriaClinicaDAO dao = new application.model.dao.HistoriaClinicaDAO();
            return dao.eliminarExpedienteBase(idPaciente);
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarExpediente: " + t.getMessage());
            return "ERR|Error al eliminar expediente: " + t.getMessage();
        }
    }

    // ------------------------------------------------------------------
    // ACTUALIZAR Y ELIMINAR EVOLUCIÓN CLÍNICA
    // ------------------------------------------------------------------

    public String actualizarEvolucion(String jsonData) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonData).getAsJsonObject();

            int idEvolucion = obj.has("id_evolucion") ? obj.get("id_evolucion").getAsInt() : 0;
            if (idEvolucion <= 0) return "ERR|El ID de evolución es obligatorio.";

            int idMedico = obj.has("id_medico") ? obj.get("id_medico").getAsInt() : 0;
            if (idMedico <= 0) return "ERR|Debe seleccionar un médico tratante.";

            Integer idCatalogoProcedimientos = (obj.has("id_catalogo_procedimientos") && !obj.get("id_catalogo_procedimientos").isJsonNull())
                    ? obj.get("id_catalogo_procedimientos").getAsInt() : null;

            String motivoConsulta   = str(obj, "motivo_consulta");
            String sintomaPrincipal = str(obj, "sintoma_principal");
            String presionArterial  = str(obj, "presion_arterial");
            String pulsoCardiaco    = str(obj, "pulso_cardiaco");
            String temperatura      = str(obj, "temperatura");
            String tejidosBlandos   = str(obj, "tejidos_blandos_observacion");
            String diagnostico      = str(obj, "diagnostico");
            String odontograma      = str(obj, "estado_odontograma");
            String observaciones    = str(obj, "observaciones");
            
            Integer ps = null, pd = null;
            if (presionArterial != null && presionArterial.contains("/")) {
                try {
                    String[] parts = presionArterial.split("/");
                    ps = Integer.parseInt(parts[0].trim());
                    pd = Integer.parseInt(parts[1].trim());
                } catch (Exception ignored) {}
            }
            Integer pulso = null;
            if (pulsoCardiaco != null && !pulsoCardiaco.isEmpty()) {
                try { pulso = Integer.parseInt(pulsoCardiaco.replaceAll("\\D", "")); } catch (Exception ignored) {}
            }
            Double temp = null;
            if (temperatura != null && !temperatura.isEmpty()) {
                try { temp = Double.parseDouble(temperatura.replaceAll("[^0-9.]", "")); } catch (Exception ignored) {}
            }

            application.model.dao.HistoriaClinicaDAO dao = new application.model.dao.HistoriaClinicaDAO();
            return dao.actualizarEvolucion(idEvolucion, idMedico, idCatalogoProcedimientos,
                    motivoConsulta, sintomaPrincipal, null, // historiaEnfermedadActual
                    ps, pd, pulso, temp,
                    tejidosBlandos, diagnostico, odontograma, observaciones);

        } catch (Throwable t) {
            System.err.println("-> ERROR en actualizarEvolucion: " + t.getMessage());
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

    public String eliminarEvolucion(int idEvolucion) {
        try {
            if (idEvolucion <= 0) return "ERR|El ID de evolución no es válido.";
            application.model.dao.HistoriaClinicaDAO dao = new application.model.dao.HistoriaClinicaDAO();
            return dao.eliminarEvolucion(idEvolucion);
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarEvolucion: " + t.getMessage());
            return "ERR|Error al eliminar evolución: " + t.getMessage();
        }
    }

    // ------------------------------------------------------------------
    // OBTENER MÉDICOS ACTIVOS (para el selector)
    // ------------------------------------------------------------------

    public String obtenerMedicos() {
        try {
            application.model.dao.HistoriaClinicaDAO dao = new application.model.dao.HistoriaClinicaDAO();
            java.util.List<java.util.Map<String, Object>> lista = dao.obtenerMedicosActivos();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerMedicos: " + t.getMessage());
            return "[]";
        }
    }

    // ------------------------------------------------------------------
    // HELPERS PRIVADOS
    // ------------------------------------------------------------------

    private int resolverIdPaciente(String identidad) {
        String query = "SELECT id_paciente FROM Pacientes WHERE identidad = ? AND borrado = FALSE";
        try {
            java.sql.Connection conn = application.model.connection.DBConnection.getInstance().getConnection();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identidad);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al resolver id_paciente: " + e.getMessage());
        }
        return -1;
    }

    private int resolverIdExpediente(int idPaciente) {
        String query = "SELECT id_expediente_base FROM Expediente_Base WHERE id_paciente = ?";
        try {
            java.sql.Connection conn = application.model.connection.DBConnection.getInstance().getConnection();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPaciente);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al resolver id_expediente: " + e.getMessage());
        }
        return -1;
    }

    /** Lee un campo String de un JsonObject; retorna "" si no existe o es null. */
    private String str(com.google.gson.JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) return "";
        return obj.get(key).getAsString();
    }

    // ------------------------------------------------------------------
    // GENERAR PDF DE DOCUMENTO MÉDICO (Constancia / Consentimientos)
    // ------------------------------------------------------------------
    /**
     * Recibe el contenido HTML del documento visible en el modal,
     * el tipo de plantilla y los datos del paciente.
     * Genera un PDF tamaño Letter, márgenes 15mm y lo abre en el visor del sistema.
     */
    public String generarPdfDocumentoMedico(String htmlContenido, String tipoPlantilla,
                                             String nombrePaciente, String identidadPaciente, String edadPaciente,
                                             String extraDataJson) {
        javafx.application.Platform.runLater(() -> {
            try {
                java.io.File file = application.util.DocumentoPDFGenerator.generarDocumentoPdf(
                        htmlContenido, tipoPlantilla, nombrePaciente, identidadPaciente, edadPaciente, extraDataJson);
                if (file != null && file.exists()) {
                    java.awt.Desktop.getDesktop().open(file);
                    System.out.println("-> PDF de documento médico generado: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("Error al generar PDF de documento médico: " + e.getMessage());
                e.printStackTrace();
            }
        });
        return "{\"status\":\"ok\"}";
    }

    // ------------------------------------------------------------------
    // REACTIVAR ARCHIVO DEL EXPEDIENTE
    // ------------------------------------------------------------------
    public String reactivarArchivo(String correoAdmin, String passAdmin, String idArchivoStr) {
        try {
            application.model.dao.UserDAO userDAO = new application.model.dao.UserDAO();
            if (!userDAO.autenticarUsuario(correoAdmin, passAdmin)) {
                return "ERR|Contraseña incorrecta.";
            }
            if (!"Administrador".equals(userDAO.obtenerRolPorCorreo(correoAdmin))) {
                return "ERR|Solo un Administrador puede reactivar registros.";
            }

            int idArchivo = Integer.parseInt(idArchivoStr.trim());
            application.model.dao.ExpedienteArchivoDAO archivoDAO = new application.model.dao.ExpedienteArchivoDAO();
            boolean exito = archivoDAO.reactivarArchivo(idArchivo);
            
            if (exito) {
                return "OK|Archivo reactivado exitosamente.";
            } else {
                return "ERR|No se pudo reactivar el archivo.";
            }
        } catch (Throwable t) {
            System.err.println("-> ERROR en reactivarArchivo: " + t.getMessage());
            return "ERR|Error al reactivar archivo: " + t.getMessage();
        }
    }
}
