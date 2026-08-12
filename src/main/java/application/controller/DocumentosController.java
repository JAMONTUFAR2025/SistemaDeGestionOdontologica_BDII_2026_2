package application.controller;

/**
 * Controller para el módulo de Documentos Clínicos.
 * Expuesto al WebView como: window.documentosController
 *
 * Métodos disponibles desde JavaScript:
 *   Búsqueda general:
 *     - buscarPacienteParaDocumentos(identidad)         → JSON con datos básicos del paciente
 *
 *   Archivos del Expediente:
 *     - obtenerArchivos(idPacientes)                    → JSON array
 *     - registrarArchivo(jsonData)                      → "OK|..." / "ERR|..."
 *     - eliminarArchivo(id)                             → "OK|..." / "ERR|..."
 *
 *   Alergias del Paciente:
 *     - obtenerAlergiasDelPaciente(idPacientes)         → JSON array
 *     - agregarAlergiaPaciente(idPacientes, idAlergia)  → "OK|..." / "ERR|..."
 *     - eliminarAlergiaPaciente(idPacienteAlergia)      → "OK|..." / "ERR|..."
 *
 *   Consentimientos Informados:
 *     - obtenerConsentimientos(idEvolucion)             → JSON array
 *     - registrarConsentimiento(jsonData)               → "OK|..." / "ERR|..."
 *     - eliminarConsentimiento(id)                      → "OK|..." / "ERR|..."
 *
 *   Constancias Médicas:
 *     - obtenerConstancias(idPacientes)                 → JSON array de todas las constancias del paciente
 *     - registrarConstancia(jsonData)                   → "OK|..." / "ERR|..."
 *     - eliminarConstancia(id)                          → "OK|..." / "ERR|..."
 *
 *   Evoluciones (para selección):
 *     - obtenerEvolucionesDelPaciente(idPacientes)      → JSON array simplificado de evoluciones
 */
public class DocumentosController extends BaseController {

    public DocumentosController() {
        super();
    }

    // =====================================================
    // BÚSQUEDA DE PACIENTE
    // =====================================================

    public String buscarPacienteParaDocumentos(String identidad) {
        try {
            if (identidad == null || identidad.trim().isEmpty())
                return "ERR|Ingrese un número de identidad para buscar.";

            String query = "SELECT id_pacientes, identidad, nombre_completo, telefono " +
                           "FROM Pacientes WHERE identidad = ? AND borrado = 'No'";
            java.sql.Connection conn = application.model.connection.DBConnection.getInstance().getConnection();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identidad.trim());
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                        map.put("id_pacientes",    rs.getInt("id_pacientes"));
                        map.put("identidad",       rs.getString("identidad"));
                        map.put("nombre_completo", rs.getString("nombre_completo"));
                        map.put("telefono",        rs.getString("telefono"));
                        return gson.toJson(map);
                    }
                }
            }
            return "NOT_FOUND";
        } catch (Throwable t) {
            System.err.println("-> ERROR en buscarPacienteParaDocumentos: " + t.getMessage());
            return "ERR|Error al buscar paciente: " + t.getMessage();
        }
    }

    // =====================================================
    // ARCHIVOS DEL EXPEDIENTE
    // =====================================================

    public String obtenerArchivos(int idPacientes) {
        try {
            application.model.dao.ExpedienteArchivoDAO dao = new application.model.dao.ExpedienteArchivoDAO();
            return gson.toJson(dao.obtenerPorPaciente(idPacientes));
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerArchivos: " + t.getMessage());
            return "[]";
        }
    }

    public String seleccionarArchivoPath() {
        try {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Seleccionar Archivo Clínico");
            java.io.File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                return file.getAbsolutePath();
            }
            return "";
        } catch (Exception e) {
            System.err.println("-> ERROR en seleccionarArchivoPath: " + e.getMessage());
            return "";
        }
    }

    public String abrirArchivo(String ruta) {
        try {
            java.io.File file = new java.io.File(ruta);
            if (file.exists()) {
                java.awt.Desktop.getDesktop().open(file);
                return "OK";
            }
            return "ERR|El archivo no existe en la ruta especificada.";
        } catch (Exception e) {
            System.err.println("-> ERROR en abrirArchivo: " + e.getMessage());
            return "ERR|No se pudo abrir el archivo: " + e.getMessage();
        }
    }

    public String registrarArchivo(String jsonData) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonData).getAsJsonObject();
            int idPacientes = obj.has("id_pacientes") ? obj.get("id_pacientes").getAsInt() : 0;
            if (idPacientes <= 0) return "ERR|El ID del paciente es obligatorio.";

            String tipoArchivo   = str(obj, "tipo_archivo");
            String nombreArchivoOriginal = str(obj, "nombre_archivo");
            String rutaArchivoOriginal   = str(obj, "ruta_archivo");
            String observaciones = str(obj, "observaciones");

            if (tipoArchivo.isEmpty())   return "ERR|El tipo de archivo es obligatorio.";
            if (rutaArchivoOriginal.isEmpty())   return "ERR|La ruta del archivo original es obligatoria.";

            java.io.File sourceFile = new java.io.File(rutaArchivoOriginal);
            if (!sourceFile.exists()) {
                return "ERR|El archivo seleccionado ya no existe en la ruta original.";
            }

            // Crear carpeta del paciente
            String userHome = System.getProperty("user.home");
            java.nio.file.Path targetDir = java.nio.file.Paths.get(userHome, "SOE_Archivos", "Pacientes", String.valueOf(idPacientes));
            if (!java.nio.file.Files.exists(targetDir)) {
                java.nio.file.Files.createDirectories(targetDir);
            }

            // Generar nombre de archivo: Tipo_YYYYMMDD_HHMMSS.ext
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String fileName = sourceFile.getName();
            String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
            String newFileName = tipoArchivo + "_" + timestamp + ext;
            
            java.nio.file.Path targetPath = targetDir.resolve(newFileName);
            
            // Copiar el archivo fisicamente
            java.nio.file.Files.copy(sourceFile.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            String rutaFinal = targetPath.toAbsolutePath().toString();
            String nombreFinal = nombreArchivoOriginal.isEmpty() ? newFileName : nombreArchivoOriginal;

            application.model.dao.ExpedienteArchivoDAO dao = new application.model.dao.ExpedienteArchivoDAO();
            boolean ok = dao.registrar(idPacientes, tipoArchivo, nombreFinal, rutaFinal, observaciones);
            return ok ? "OK|Archivo registrado exitosamente." : "ERR|No se pudo registrar el archivo en la base de datos.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en registrarArchivo: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String eliminarArchivo(int id) {
        try {
            application.model.dao.ExpedienteArchivoDAO dao = new application.model.dao.ExpedienteArchivoDAO();
            boolean ok = dao.eliminar(id);
            return ok ? "OK|Archivo eliminado exitosamente." : "ERR|No se pudo eliminar el archivo.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarArchivo: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    // =====================================================
    // ALERGIAS DEL PACIENTE
    // =====================================================

    public String obtenerAlergiasDelPaciente(int idPacientes) {
        try {
            application.model.dao.PacienteAlergiaDAO dao = new application.model.dao.PacienteAlergiaDAO();
            return gson.toJson(dao.obtenerPorPaciente(idPacientes));
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerAlergiasDelPaciente: " + t.getMessage());
            return "[]";
        }
    }

    public String agregarAlergiaPaciente(int idPacientes, int idCatalogoAlergia) {
        try {
            application.model.dao.PacienteAlergiaDAO dao = new application.model.dao.PacienteAlergiaDAO();
            boolean ok = dao.agregar(idPacientes, idCatalogoAlergia);
            return ok ? "OK|Alergia asignada correctamente." : "ERR|La alergia ya estaba registrada.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en agregarAlergiaPaciente: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String eliminarAlergiaPaciente(int idPacienteAlergia) {
        try {
            application.model.dao.PacienteAlergiaDAO dao = new application.model.dao.PacienteAlergiaDAO();
            boolean ok = dao.eliminar(idPacienteAlergia);
            return ok ? "OK|Alergia eliminada del paciente." : "ERR|No se pudo eliminar la alergia.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarAlergiaPaciente: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    // =====================================================
    // CONSENTIMIENTOS INFORMADOS
    // =====================================================

    public String obtenerConsentimientos(int idEvolucion) {
        try {
            application.model.dao.ConsentimientoInformadoDAO dao = new application.model.dao.ConsentimientoInformadoDAO();
            return gson.toJson(dao.obtenerPorEvolucion(idEvolucion));
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerConsentimientos: " + t.getMessage());
            return "[]";
        }
    }

    public String registrarConsentimiento(String jsonData) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonData).getAsJsonObject();
            int idEvolucion = obj.has("id_evolucion_clinica") ? obj.get("id_evolucion_clinica").getAsInt() : 0;
            if (idEvolucion <= 0) return "ERR|El ID de la evolución clínica es obligatorio.";

            String tipoProcedimiento     = str(obj, "tipo_procedimiento");
            String representanteLegal    = str(obj, "representante_legal");
            String identidadRepresentante = str(obj, "identidad_representante");
            String fechaFirma            = str(obj, "fecha_firma");

            if (tipoProcedimiento.isEmpty()) return "ERR|El tipo de procedimiento es obligatorio.";
            if (fechaFirma.isEmpty())        return "ERR|La fecha de firma es obligatoria.";

            application.model.dao.ConsentimientoInformadoDAO dao = new application.model.dao.ConsentimientoInformadoDAO();
            boolean ok = dao.registrar(idEvolucion, tipoProcedimiento, representanteLegal,
                                       identidadRepresentante, fechaFirma);
            return ok ? "OK|Consentimiento registrado exitosamente." : "ERR|No se pudo registrar el consentimiento.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en registrarConsentimiento: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String eliminarConsentimiento(int id) {
        try {
            application.model.dao.ConsentimientoInformadoDAO dao = new application.model.dao.ConsentimientoInformadoDAO();
            boolean ok = dao.eliminar(id);
            return ok ? "OK|Consentimiento eliminado." : "ERR|No se pudo eliminar el consentimiento.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarConsentimiento: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    // =====================================================
    // CONSTANCIAS MÉDICAS
    // =====================================================

    public String obtenerConstancias(int idPacientes) {
        try {
            application.model.dao.ConstanciaMedicaDAO dao = new application.model.dao.ConstanciaMedicaDAO();
            return gson.toJson(dao.obtenerPorPaciente(idPacientes));
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerConstancias: " + t.getMessage());
            return "[]";
        }
    }

    public String registrarConstancia(String jsonData) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonData).getAsJsonObject();
            int idEvolucion = obj.has("id_evolucion_clinica") ? obj.get("id_evolucion_clinica").getAsInt() : 0;
            if (idEvolucion <= 0) return "ERR|El ID de la evolución clínica es obligatorio.";

            String fechaEmision       = str(obj, "fecha_emision");
            String horaEmision        = str(obj, "hora_emision");
            String tratamientoRealizado = str(obj, "tratamiento_realizado");

            if (fechaEmision.isEmpty())         return "ERR|La fecha de emisión es obligatoria.";
            if (horaEmision.isEmpty())          return "ERR|La hora de emisión es obligatoria.";
            if (tratamientoRealizado.isEmpty()) return "ERR|El tratamiento realizado es obligatorio.";

            application.model.dao.ConstanciaMedicaDAO dao = new application.model.dao.ConstanciaMedicaDAO();
            boolean ok = dao.registrar(idEvolucion, fechaEmision, horaEmision, tratamientoRealizado);
            return ok ? "OK|Constancia médica registrada exitosamente." : "ERR|No se pudo registrar la constancia.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en registrarConstancia: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String eliminarConstancia(int id) {
        try {
            application.model.dao.ConstanciaMedicaDAO dao = new application.model.dao.ConstanciaMedicaDAO();
            boolean ok = dao.eliminar(id);
            return ok ? "OK|Constancia médica eliminada." : "ERR|No se pudo eliminar la constancia.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarConstancia: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    // =====================================================
    // EVOLUCIONES DEL PACIENTE (para selector)
    // =====================================================

    public String obtenerEvolucionesDelPaciente(int idPacientes) {
        try {
            String query = "SELECT ec.id_evolucion_clinica, ec.numero_cita, ec.fecha_consulta, " +
                           "pm.nombre_completo AS nombre_medico " +
                           "FROM Evolucion_Clinica ec " +
                           "LEFT JOIN Personal_Medico pm ON ec.id_personal_medico = pm.id_personal_medico " +
                           "WHERE ec.id_pacientes = ? ORDER BY ec.fecha_consulta DESC";
            java.util.List<java.util.Map<String, Object>> lista = new java.util.ArrayList<>();
            java.sql.Connection conn = application.model.connection.DBConnection.getInstance().getConnection();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPacientes);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                        map.put("id_evolucion_clinica", rs.getInt("id_evolucion_clinica"));
                        map.put("numero_cita",          rs.getInt("numero_cita"));
                        map.put("fecha_consulta",       rs.getString("fecha_consulta"));
                        map.put("nombre_medico",        rs.getString("nombre_medico"));
                        lista.add(map);
                    }
                }
            }
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerEvolucionesDelPaciente: " + t.getMessage());
            return "[]";
        }
    }

    // =====================================================
    // HELPER
    // =====================================================

    private String str(com.google.gson.JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) return "";
        return obj.get(key).getAsString();
    }
}
