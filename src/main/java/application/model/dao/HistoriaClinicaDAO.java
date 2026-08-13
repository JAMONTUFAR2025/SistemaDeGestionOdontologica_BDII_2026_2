package application.model.dao;

import application.model.connection.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para el módulo de Historia Clínica.
 * Maneja: Expediente_Base, Evolucion_Clinica y búsqueda de Pacientes + Personal_Medico.
 *
 * Esquema relevante:
 *   Pacientes          → id_paciente, identidad, nombre_completo, ...
 *   Expediente_Base    → id_expediente_base, id_paciente (FK, UNIQUE), remitido_por, ...
 *   Evolucion_Clinica  → id_evolucion_clinica, id_expediente_base (FK),
 *                        id_personal_medico (FK), numero_cita,
 *                        motivo_consulta, sintoma_principal, historia_enfermedad_actual,
 *                        presion_sistolica, presion_diastolica, pulso_cardiaco_bpm, temperatura_celsius,
 *                        tejidos_blandos_observacion, diagnostico, id_catalogo_procedimiento,
 *                        estado_odontograma, observaciones, fecha_registro
 *   Personal_Medico    → id_personal_medico, nombre_completo, borrado
 */
public class HistoriaClinicaDAO {

    // ==============================================================
    // BÚSQUEDA COMPLETA DEL EXPEDIENTE POR IDENTIDAD DEL PACIENTE
    // ==============================================================

    /**
     * Devuelve un Map con: paciente, expediente_base, evoluciones.
     * Si el paciente no existe retorna null.
     */
    public Map<String, Object> buscarExpedienteCompleto(String identidad) {
        // 1. Buscar paciente
        Map<String, Object> datosPaciente = buscarPacientePorIdentidad(identidad);
        if (datosPaciente == null) return null;

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("paciente", datosPaciente);

        int idPaciente = (int) datosPaciente.get("id_paciente");

        // 2. Buscar expediente base
        Map<String, Object> expediente = buscarExpedienteBasePorPaciente(idPaciente);
        resultado.put("expediente", expediente);

        // 3. Si hay expediente, cargar evoluciones
        if (expediente != null) {
            int idExpediente = (int) expediente.get("id_expediente_base");
            List<Map<String, Object>> evoluciones = obtenerEvolucionesPorExpediente(idExpediente);
            resultado.put("evoluciones", evoluciones);
        } else {
            resultado.put("evoluciones", new ArrayList<>());
        }

        // 4. Cargar alergias
        List<Map<String, Object>> alergias = new PacienteAlergiaDAO().obtenerPorPaciente(idPaciente);
        resultado.put("alergias", alergias);

        return resultado;
    }

    private Map<String, Object> buscarPacientePorIdentidad(String identidad) {
        String query = "SELECT p.id_paciente, p.identidad, p.nombre_completo, p.fecha_nacimiento, " +
                       "p.genero, p.estado_civil, p.ocupacion, p.domicilio, p.telefono, " +
                       "p.id_responsable, p.fecha_registro, " +
                       "r.nombre_completo AS persona_responsable, r.telefono AS telefono_responsable " +
                       "FROM Pacientes p " +
                       "LEFT JOIN Responsables r ON p.id_responsable = r.id_responsable " +
                       "WHERE p.identidad = ? AND p.borrado = FALSE";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identidad);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_paciente",           rs.getInt("id_paciente"));
                        map.put("id_pacientes",          rs.getInt("id_paciente")); // Compatibilidad
                        map.put("identidad",             rs.getString("identidad"));
                        map.put("nombre_completo",       rs.getString("nombre_completo"));
                        map.put("fecha_nacimiento",      rs.getString("fecha_nacimiento"));
                        map.put("genero",                rs.getString("genero"));
                        map.put("estado_civil",          rs.getString("estado_civil"));
                        map.put("ocupacion",             rs.getString("ocupacion"));
                        map.put("domicilio",             rs.getString("domicilio"));
                        map.put("telefono",              rs.getString("telefono"));
                        map.put("id_responsable",        rs.getObject("id_responsable"));
                        map.put("persona_responsable",   rs.getString("persona_responsable"));
                        map.put("telefono_responsable",  rs.getString("telefono_responsable"));
                        map.put("fecha_registro",        rs.getString("fecha_registro"));
                        return map;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar paciente: " + e.getMessage());
        }
        return null;
    }

    private Map<String, Object> buscarExpedienteBasePorPaciente(int idPaciente) {
        String query = "SELECT id_expediente_base, remitido_por, antecedentes_patologicos, " +
                       "antecedentes_odontologicos, antecedentes_quirurgicos, antecedentes_ginecobstetros, " +
                       "habitos_toxicos, farmacos_uso_habitual, reaccion_anestesicos, especifique_anestesia, " +
                       "complicaciones_tratamientos_previos, habitos_bucales, frecuencia_cepillado, " +
                       "tipo_cepillo_cerdas, uso_hilo_dental, tipo_mordida " +
                       "FROM Expediente_Base WHERE id_paciente = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPaciente);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_expediente_base",                    rs.getInt("id_expediente_base"));
                        map.put("remitido_por",                          rs.getString("remitido_por"));
                        map.put("antecedentes_patologicos",              rs.getString("antecedentes_patologicos"));
                        map.put("antecedentes_odontologicos",            rs.getString("antecedentes_odontologicos"));
                        map.put("antecedentes_quirurgicos",              rs.getString("antecedentes_quirurgicos"));
                        map.put("antecedentes_ginecobstetros",           rs.getString("antecedentes_ginecobstetros"));
                        map.put("habitos_toxicos",                       rs.getString("habitos_toxicos"));
                        map.put("farmacos_uso_habitual",                 rs.getString("farmacos_uso_habitual"));
                        map.put("reaccion_anestesicos",                  rs.getBoolean("reaccion_anestesicos") ? "Si" : "No"); // Mantener API
                        map.put("especifique_anestesia",                 rs.getString("especifique_anestesia"));
                        map.put("complicaciones_tratamientos_previos",   rs.getString("complicaciones_tratamientos_previos"));
                        map.put("habitos_bucales",                       rs.getString("habitos_bucales"));
                        map.put("frecuencia_cepillado",                  rs.getString("frecuencia_cepillado"));
                        map.put("tipo_cepillo_cerdas",                   rs.getString("tipo_cepillo_cerdas"));
                        map.put("uso_hilo_dental",                       rs.getString("uso_hilo_dental"));
                        map.put("tipo_mordida",                          rs.getString("tipo_mordida"));
                        return map;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar expediente base: " + e.getMessage());
        }
        return null;
    }

    private List<Map<String, Object>> obtenerEvolucionesPorExpediente(int idExpediente) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT ec.id_evolucion_clinica, ec.numero_cita, ec.id_cita, " +
                       "ec.motivo_consulta, ec.sintoma_principal, ec.historia_enfermedad_actual, " +
                       "ec.presion_sistolica, ec.presion_diastolica, " +
                       "ec.pulso_cardiaco_bpm, ec.temperatura_celsius, ec.tejidos_blandos_observacion, " +
                       "ec.diagnostico, ec.estado_odontograma, ec.observaciones, " +
                       "ec.fecha_registro, " +
                       "c.fecha_hora AS fecha_consulta, " + // Recuperar fecha_consulta de la cita
                       "pm.nombre_completo AS nombre_medico, ec.id_personal_medico, " +
                       "cp.nombre_procedimiento, ec.id_catalogo_procedimiento " +
                       "FROM Evolucion_Clinica ec " +
                       "LEFT JOIN Personal_Medico pm ON ec.id_personal_medico = pm.id_personal_medico " +
                       "LEFT JOIN Catalogo_Procedimientos cp ON ec.id_catalogo_procedimiento = cp.id_catalogo_procedimiento " +
                       "LEFT JOIN Citas c ON ec.id_cita = c.id_cita " +
                       "WHERE ec.id_expediente_base = ? " +
                       "ORDER BY ec.id_evolucion_clinica DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idExpediente);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_evolucion",         rs.getInt("id_evolucion_clinica"));
                        map.put("numero_cita",          rs.getInt("numero_cita"));
                        map.put("id_cita",              rs.getObject("id_cita"));
                        map.put("fecha_consulta",       rs.getString("fecha_consulta"));
                        map.put("motivo_consulta",      rs.getString("motivo_consulta"));
                        map.put("sintoma_principal",    rs.getString("sintoma_principal"));
                        map.put("historia_enfermedad_actual", rs.getString("historia_enfermedad_actual"));
                        
                        // Reconstruir para el frontend (temporalmente hasta que se adapte el JS si es necesario)
                        String ps = rs.getString("presion_sistolica");
                        String pd = rs.getString("presion_diastolica");
                        if (ps != null && pd != null) map.put("presion_arterial", ps + "/" + pd);
                        else map.put("presion_arterial", "");
                        
                        map.put("presion_sistolica",    rs.getObject("presion_sistolica"));
                        map.put("presion_diastolica",   rs.getObject("presion_diastolica"));
                        map.put("pulso_cardiaco",       rs.getString("pulso_cardiaco_bpm"));
                        map.put("temperatura",          rs.getString("temperatura_celsius"));
                        map.put("tejidos_blandos",      rs.getString("tejidos_blandos_observacion"));
                        map.put("diagnostico",          rs.getString("diagnostico"));
                        map.put("estado_odontograma",   rs.getString("estado_odontograma"));
                        map.put("observaciones",        rs.getString("observaciones"));
                        map.put("fecha_registro",       rs.getString("fecha_registro"));
                        map.put("nombre_medico",        rs.getString("nombre_medico"));
                        map.put("id_medico",            rs.getInt("id_personal_medico"));
                        map.put("nombre_procedimiento", rs.getString("nombre_procedimiento"));
                        map.put("id_catalogo_procedimientos", rs.getObject("id_catalogo_procedimiento"));
                        lista.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener evoluciones: " + e.getMessage());
        }
        return lista;
    }

    // ==============================================================
    // REGISTRAR EXPEDIENTE BASE (INSERT — uno por paciente)
    // ==============================================================

    public String registrarExpedienteBase(int idPaciente, String remitidoPor,
            String antPatologicos, String antOdontologicos, String antQuirurgicos,
            String antGinecoObstetros, String habitosToxicos, String farmacosHabituales,
            boolean reaccionAnest, String especAnest, String complicaciones,
            String habitosBucales, String frecuenciaCepillado, String tipoCerdas,
            String usoHiloDental, String tipoMordida) {

        // Verificar si ya existe expediente para el paciente (restricción UNIQUE en schema)
        String checkQuery = "SELECT id_expediente_base FROM Expediente_Base WHERE id_paciente = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement check = conn.prepareStatement(checkQuery)) {
                check.setInt(1, idPaciente);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        return "ERR|Este paciente ya tiene un Expediente Base registrado. Use la función de Evolución Clínica para registrar consultas nuevas.";
                    }
                }
            }

            String query = "INSERT INTO Expediente_Base " +
                    "(id_paciente, remitido_por, antecedentes_patologicos, antecedentes_odontologicos, " +
                    "antecedentes_quirurgicos, antecedentes_ginecobstetros, habitos_toxicos, " +
                    "farmacos_uso_habitual, reaccion_anestesicos, especifique_anestesia, " +
                    "complicaciones_tratamientos_previos, habitos_bucales, frecuencia_cepillado, " +
                    "tipo_cepillo_cerdas, uso_hilo_dental, tipo_mordida) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPaciente);
                stmt.setString(2, nullIfEmpty(remitidoPor));
                stmt.setString(3, nullIfEmpty(antPatologicos));
                stmt.setString(4, nullIfEmpty(antOdontologicos));
                stmt.setString(5, nullIfEmpty(antQuirurgicos));
                stmt.setString(6, nullIfEmpty(antGinecoObstetros));
                stmt.setString(7, nullIfEmpty(habitosToxicos));
                stmt.setString(8, nullIfEmpty(farmacosHabituales));
                stmt.setBoolean(9, reaccionAnest);
                stmt.setString(10, nullIfEmpty(especAnest));
                stmt.setString(11, nullIfEmpty(complicaciones));
                stmt.setString(12, nullIfEmpty(habitosBucales));
                stmt.setString(13, nullIfEmpty(frecuenciaCepillado));
                stmt.setString(14, nullIfEmpty(tipoCerdas));
                stmt.setString(15, nullIfEmpty(usoHiloDental));
                stmt.setString(16, nullIfEmpty(tipoMordida));

                int rows = stmt.executeUpdate();
                return rows > 0 ? "OK|Historia Clínica registrada exitosamente." :
                                  "ERR|No se pudo guardar el expediente.";
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar expediente: " + e.getMessage());
            return "ERR|Error de base de datos: " + e.getMessage();
        }
    }

    // ==============================================================
    // REGISTRAR EVOLUCIÓN CLÍNICA
    // ==============================================================

    public String registrarEvolucion(int idExpediente, int idMedico,
            Integer idCita, Integer idCatalogoProcedimiento,
            String motivoConsulta, String sintomaPrincipal, String historiaEnfermedadActual,
            Integer presionSistolica, Integer presionDiastolica, Integer pulsoCardiacoBpm, Double temperaturaCelsius,
            String tejidosBlandos, String diagnostico, String estadoOdontograma, String observaciones) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();

            // Calcular número de cita (siguiente = max + 1)
            int numeroCita = 1;
            String countQuery = "SELECT COALESCE(MAX(numero_cita), 0) FROM Evolucion_Clinica " +
                                "WHERE id_expediente_base = ?";
            try (PreparedStatement cs = conn.prepareStatement(countQuery)) {
                cs.setInt(1, idExpediente);
                try (ResultSet rs = cs.executeQuery()) {
                    if (rs.next()) numeroCita = rs.getInt(1) + 1;
                }
            }

            String query = "INSERT INTO Evolucion_Clinica " +
                    "(id_expediente_base, id_personal_medico, id_cita, id_catalogo_procedimiento, " +
                    "numero_cita, motivo_consulta, sintoma_principal, historia_enfermedad_actual, " +
                    "presion_sistolica, presion_diastolica, pulso_cardiaco_bpm, temperatura_celsius, " +
                    "tejidos_blandos_observacion, diagnostico, estado_odontograma, observaciones) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idExpediente);
                stmt.setInt(2, idMedico);
                if (idCita != null && idCita > 0) stmt.setInt(3, idCita);
                else stmt.setNull(3, java.sql.Types.INTEGER);
                if (idCatalogoProcedimiento != null && idCatalogoProcedimiento > 0) stmt.setInt(4, idCatalogoProcedimiento);
                else stmt.setNull(4, java.sql.Types.INTEGER);
                stmt.setInt(5, numeroCita);
                stmt.setString(6, nullIfEmpty(motivoConsulta));
                stmt.setString(7, nullIfEmpty(sintomaPrincipal));
                stmt.setString(8, nullIfEmpty(historiaEnfermedadActual));
                if (presionSistolica != null) stmt.setInt(9, presionSistolica); else stmt.setNull(9, java.sql.Types.INTEGER);
                if (presionDiastolica != null) stmt.setInt(10, presionDiastolica); else stmt.setNull(10, java.sql.Types.INTEGER);
                if (pulsoCardiacoBpm != null) stmt.setInt(11, pulsoCardiacoBpm); else stmt.setNull(11, java.sql.Types.INTEGER);
                if (temperaturaCelsius != null) stmt.setDouble(12, temperaturaCelsius); else stmt.setNull(12, java.sql.Types.DECIMAL);
                stmt.setString(13, nullIfEmpty(tejidosBlandos));
                stmt.setString(14, nullIfEmpty(diagnostico));
                stmt.setString(15, nullIfEmpty(estadoOdontograma));
                stmt.setString(16, nullIfEmpty(observaciones));

                int rows = stmt.executeUpdate();
                return rows > 0 ? "OK|Evolución clínica registrada (Cita #" + numeroCita + ")." :
                                  "ERR|No se pudo guardar la evolución.";
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar evolución: " + e.getMessage());
            return "ERR|Error de base de datos: " + e.getMessage();
        }
    }

    // ==============================================================
    // LISTAR MÉDICOS ACTIVOS (para el selector del formulario)
    // ==============================================================

    public List<Map<String, Object>> obtenerMedicosActivos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT id_personal_medico, nombre_completo " +
                       "FROM Personal_Medico WHERE borrado = FALSE ORDER BY nombre_completo ASC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id_medico",        rs.getInt("id_personal_medico"));
                    map.put("nombre_completo",  rs.getString("nombre_completo"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener médicos: " + e.getMessage());
        }
        return lista;
    }

    // ==============================================================
    // ACTUALIZAR Y ELIMINAR EXPEDIENTE BASE
    // ==============================================================

    public String actualizarExpedienteBase(int idPaciente, String remitidoPor,
            String antPatologicos, String antOdontologicos, String antQuirurgicos,
            String antGinecoObstetros, String habitosToxicos, String farmacosHabituales,
            boolean reaccionAnest, String especAnest, String complicaciones,
            String habitosBucales, String frecuenciaCepillado, String tipoCerdas,
            String usoHiloDental, String tipoMordida) {

        String query = "UPDATE Expediente_Base SET " +
                "remitido_por = ?, antecedentes_patologicos = ?, antecedentes_odontologicos = ?, " +
                "antecedentes_quirurgicos = ?, antecedentes_ginecobstetros = ?, habitos_toxicos = ?, " +
                "farmacos_uso_habitual = ?, reaccion_anestesicos = ?, especifique_anestesia = ?, " +
                "complicaciones_tratamientos_previos = ?, habitos_bucales = ?, frecuencia_cepillado = ?, " +
                "tipo_cepillo_cerdas = ?, uso_hilo_dental = ?, tipo_mordida = ? " +
                "WHERE id_paciente = ?";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nullIfEmpty(remitidoPor));
                stmt.setString(2, nullIfEmpty(antPatologicos));
                stmt.setString(3, nullIfEmpty(antOdontologicos));
                stmt.setString(4, nullIfEmpty(antQuirurgicos));
                stmt.setString(5, nullIfEmpty(antGinecoObstetros));
                stmt.setString(6, nullIfEmpty(habitosToxicos));
                stmt.setString(7, nullIfEmpty(farmacosHabituales));
                stmt.setBoolean(8, reaccionAnest);
                stmt.setString(9, nullIfEmpty(especAnest));
                stmt.setString(10, nullIfEmpty(complicaciones));
                stmt.setString(11, nullIfEmpty(habitosBucales));
                stmt.setString(12, nullIfEmpty(frecuenciaCepillado));
                stmt.setString(13, nullIfEmpty(tipoCerdas));
                stmt.setString(14, nullIfEmpty(usoHiloDental));
                stmt.setString(15, nullIfEmpty(tipoMordida));
                stmt.setInt(16, idPaciente);

                int rows = stmt.executeUpdate();
                return rows > 0 ? "OK|Historia Clínica actualizada exitosamente." :
                                  "ERR|No se encontró el expediente para actualizar.";
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar expediente: " + e.getMessage());
            return "ERR|Error de base de datos: " + e.getMessage();
        }
    }

    public String eliminarExpedienteBase(int idPaciente) {
        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Eliminar evoluciones clínicas dependientes
            String delEvoluciones = "DELETE FROM Evolucion_Clinica WHERE id_expediente_base = (SELECT id_expediente_base FROM Expediente_Base WHERE id_paciente = ?)";
            try (PreparedStatement stmt1 = conn.prepareStatement(delEvoluciones)) {
                stmt1.setInt(1, idPaciente);
                stmt1.executeUpdate();
            }

            // 2. Eliminar expediente base
            String delExpediente = "DELETE FROM Expediente_Base WHERE id_paciente = ?";
            try (PreparedStatement stmt2 = conn.prepareStatement(delExpediente)) {
                stmt2.setInt(1, idPaciente);
                int rows = stmt2.executeUpdate();
                
                if (rows > 0) {
                    conn.commit();
                    return "OK|Expediente y sus evoluciones eliminados correctamente.";
                } else {
                    conn.rollback();
                    return "ERR|No se encontró el expediente a eliminar.";
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { }
            }
            System.err.println("Error al eliminar expediente: " + e.getMessage());
            return "ERR|Error al eliminar expediente: " + e.getMessage();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { }
            }
        }
    }

    // ==============================================================
    // ACTUALIZAR Y ELIMINAR EVOLUCION CLINICA
    // ==============================================================

    public String actualizarEvolucion(int idEvolucion, int idMedico,
            Integer idCatalogoProcedimiento,
            String motivoConsulta, String sintomaPrincipal, String historiaEnfermedadActual,
            Integer presionSistolica, Integer presionDiastolica, Integer pulsoCardiacoBpm, Double temperaturaCelsius,
            String tejidosBlandos, String diagnostico, String estadoOdontograma, String observaciones) {

        String query = "UPDATE Evolucion_Clinica SET " +
                "id_personal_medico = ?, id_catalogo_procedimiento = ?, motivo_consulta = ?, " +
                "sintoma_principal = ?, historia_enfermedad_actual = ?, presion_sistolica = ?, presion_diastolica = ?, " +
                "pulso_cardiaco_bpm = ?, temperatura_celsius = ?, tejidos_blandos_observacion = ?, diagnostico = ?, " +
                "estado_odontograma = ?, observaciones = ? " +
                "WHERE id_evolucion_clinica = ?";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idMedico);
                if (idCatalogoProcedimiento != null && idCatalogoProcedimiento > 0)
                    stmt.setInt(2, idCatalogoProcedimiento);
                else
                    stmt.setNull(2, java.sql.Types.INTEGER);
                stmt.setString(3, nullIfEmpty(motivoConsulta));
                stmt.setString(4, nullIfEmpty(sintomaPrincipal));
                stmt.setString(5, nullIfEmpty(historiaEnfermedadActual));
                if (presionSistolica != null) stmt.setInt(6, presionSistolica); else stmt.setNull(6, java.sql.Types.INTEGER);
                if (presionDiastolica != null) stmt.setInt(7, presionDiastolica); else stmt.setNull(7, java.sql.Types.INTEGER);
                if (pulsoCardiacoBpm != null) stmt.setInt(8, pulsoCardiacoBpm); else stmt.setNull(8, java.sql.Types.INTEGER);
                if (temperaturaCelsius != null) stmt.setDouble(9, temperaturaCelsius); else stmt.setNull(9, java.sql.Types.DECIMAL);
                stmt.setString(10, nullIfEmpty(tejidosBlandos));
                stmt.setString(11, nullIfEmpty(diagnostico));
                stmt.setString(12, nullIfEmpty(estadoOdontograma));
                stmt.setString(13, nullIfEmpty(observaciones));
                stmt.setInt(14, idEvolucion);

                int rows = stmt.executeUpdate();
                return rows > 0 ? "OK|Evolución clínica actualizada exitosamente." :
                                  "ERR|No se pudo actualizar la evolución.";
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar evolución: " + e.getMessage());
            return "ERR|Error de base de datos: " + e.getMessage();
        }
    }

    public String eliminarEvolucion(int idEvolucion) {
        String query = "DELETE FROM Evolucion_Clinica WHERE id_evolucion_clinica = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idEvolucion);
                int rows = stmt.executeUpdate();
                return rows > 0 ? "OK|Evolución clínica eliminada exitosamente." :
                                  "ERR|No se encontró la evolución clínica a eliminar.";
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar evolución: " + e.getMessage());
            return "ERR|Error de base de datos: " + e.getMessage();
        }
    }

    // ==============================================================
    // HELPER
    // ==============================================================

    private String nullIfEmpty(String val) {
        return (val == null || val.trim().isEmpty()) ? null : val.trim();
    }
}
