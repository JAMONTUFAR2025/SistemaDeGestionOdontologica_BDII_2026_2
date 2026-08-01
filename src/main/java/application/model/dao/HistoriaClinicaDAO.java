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
 *   Pacientes          → id_pacientes, identidad, nombre_completo, ...
 *   Expediente_Base    → id_expediente_base, id_pacientes (FK, UNIQUE), remitido_por, ...
 *   Evolucion_Clinica  → id_evolucion_clinica, id_pacientes (FK), id_expediente_base (FK),
 *                        id_personal_medico (FK), numero_cita, fecha_consulta, motivo_consulta,
 *                        sintoma_principal, presion_arterial, pulso_cardiaco, temperatura,
 *                        tejidos_blandos_observacion, diagnostico, id_catalogo_procedimientos,
 *                        estado_odontograma, pago_abono, observaciones, fecha_registro
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

        int idPacientes = (int) datosPaciente.get("id_pacientes");

        // 2. Buscar expediente base
        Map<String, Object> expediente = buscarExpedienteBasePorPaciente(idPacientes);
        resultado.put("expediente", expediente);

        // 3. Si hay expediente, cargar evoluciones
        if (expediente != null) {
            int idExpediente = (int) expediente.get("id_expediente_base");
            List<Map<String, Object>> evoluciones = obtenerEvolucionesPorExpediente(idExpediente, idPacientes);
            resultado.put("evoluciones", evoluciones);
        } else {
            resultado.put("evoluciones", new ArrayList<>());
        }

        return resultado;
    }

    private Map<String, Object> buscarPacientePorIdentidad(String identidad) {
        String query = "SELECT id_pacientes, identidad, nombre_completo, fecha_nacimiento, " +
                       "genero, estado_civil, ocupacion, domicilio, telefono, " +
                       "persona_responsable, telefono_responsable, fecha_registro " +
                       "FROM Pacientes WHERE identidad = ? AND borrado = 'No'";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identidad);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_pacientes",          rs.getInt("id_pacientes"));
                        map.put("identidad",             rs.getString("identidad"));
                        map.put("nombre_completo",       rs.getString("nombre_completo"));
                        map.put("fecha_nacimiento",      rs.getString("fecha_nacimiento"));
                        map.put("genero",                rs.getString("genero"));
                        map.put("estado_civil",          rs.getString("estado_civil"));
                        map.put("ocupacion",             rs.getString("ocupacion"));
                        map.put("domicilio",             rs.getString("domicilio"));
                        map.put("telefono",              rs.getString("telefono"));
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

    private Map<String, Object> buscarExpedienteBasePorPaciente(int idPacientes) {
        String query = "SELECT id_expediente_base, remitido_por, antecedentes_patologicos, " +
                       "antecedentes_odontologicos, antecedentes_quirurgicos, antecedentes_ginecobstetros, " +
                       "habitos_toxicos, farmacos_uso_habitual, reaccion_anestesicos, especifique_anestesia, " +
                       "complicaciones_tratamientos_previos, habitos_bucales, frecuencia_cepillado, " +
                       "tipo_cepillo_cerdas, uso_hilo_dental, tipo_mordida, " +
                       "diagnostico_presuntivo, observaciones_generales " +
                       "FROM Expediente_Base WHERE id_pacientes = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPacientes);
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
                        map.put("reaccion_anestesicos",                  rs.getString("reaccion_anestesicos"));
                        map.put("especifique_anestesia",                 rs.getString("especifique_anestesia"));
                        map.put("complicaciones_tratamientos_previos",   rs.getString("complicaciones_tratamientos_previos"));
                        map.put("habitos_bucales",                       rs.getString("habitos_bucales"));
                        map.put("frecuencia_cepillado",                  rs.getString("frecuencia_cepillado"));
                        map.put("tipo_cepillo_cerdas",                   rs.getString("tipo_cepillo_cerdas"));
                        map.put("uso_hilo_dental",                       rs.getString("uso_hilo_dental"));
                        map.put("tipo_mordida",                          rs.getString("tipo_mordida"));
                        map.put("diagnostico_presuntivo",                rs.getString("diagnostico_presuntivo"));
                        map.put("observaciones_generales",               rs.getString("observaciones_generales"));
                        return map;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar expediente base: " + e.getMessage());
        }
        return null;
    }

    private List<Map<String, Object>> obtenerEvolucionesPorExpediente(int idExpediente, int idPacientes) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT ec.id_evolucion_clinica, ec.numero_cita, ec.fecha_consulta, " +
                       "ec.motivo_consulta, ec.sintoma_principal, ec.presion_arterial, " +
                       "ec.pulso_cardiaco, ec.temperatura, ec.tejidos_blandos_observacion, " +
                       "ec.diagnostico, ec.estado_odontograma, ec.pago_abono, ec.observaciones, " +
                       "ec.fecha_registro, " +
                       "pm.nombre_completo AS nombre_medico, ec.id_personal_medico, " +
                       "cp.nombre_procedimiento " +
                       "FROM Evolucion_Clinica ec " +
                       "LEFT JOIN Personal_Medico pm ON ec.id_personal_medico = pm.id_personal_medico " +
                       "LEFT JOIN Catalogo_Procedimientos cp ON ec.id_catalogo_procedimientos = cp.id_catalogo_procedimientos " +
                       "WHERE ec.id_expediente_base = ? AND ec.id_pacientes = ? " +
                       "ORDER BY ec.fecha_consulta DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idExpediente);
                stmt.setInt(2, idPacientes);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_evolucion",         rs.getInt("id_evolucion_clinica"));
                        map.put("numero_cita",          rs.getInt("numero_cita"));
                        map.put("fecha_consulta",       rs.getString("fecha_consulta"));
                        map.put("motivo_consulta",      rs.getString("motivo_consulta"));
                        map.put("sintoma_principal",    rs.getString("sintoma_principal"));
                        map.put("presion_arterial",     rs.getString("presion_arterial"));
                        map.put("pulso_cardiaco",       rs.getString("pulso_cardiaco"));
                        map.put("temperatura",          rs.getString("temperatura"));
                        map.put("tejidos_blandos",      rs.getString("tejidos_blandos_observacion"));
                        map.put("diagnostico",          rs.getString("diagnostico"));
                        map.put("estado_odontograma",   rs.getString("estado_odontograma"));
                        map.put("pago_abono",           rs.getDouble("pago_abono"));
                        map.put("observaciones",        rs.getString("observaciones"));
                        map.put("fecha_registro",       rs.getString("fecha_registro"));
                        map.put("nombre_medico",        rs.getString("nombre_medico"));
                        map.put("id_medico",            rs.getInt("id_personal_medico"));
                        map.put("nombre_procedimiento", rs.getString("nombre_procedimiento"));
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

    public String registrarExpedienteBase(int idPacientes, String remitidoPor,
            String antPatologicos, String antOdontologicos, String antQuirurgicos,
            String antGinecoObstetros, String habitosToxicos, String farmacosHabituales,
            String reaccionAnest, String especAnest, String complicaciones,
            String habitosBucales, String frecuenciaCepillado, String tipoCerdas,
            String usoHiloDental, String tipoMordida,
            String tejidosBlandos, String diagnosticoPresuntivo, String observacionesGenerales) {

        // Verificar si ya existe expediente para el paciente (restricción UNIQUE en schema)
        String checkQuery = "SELECT id_expediente_base FROM Expediente_Base WHERE id_pacientes = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement check = conn.prepareStatement(checkQuery)) {
                check.setInt(1, idPacientes);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        return "ERR|Este paciente ya tiene un Expediente Base registrado. Use la función de Evolución Clínica para registrar consultas nuevas.";
                    }
                }
            }

            String query = "INSERT INTO Expediente_Base " +
                    "(id_pacientes, remitido_por, antecedentes_patologicos, antecedentes_odontologicos, " +
                    "antecedentes_quirurgicos, antecedentes_ginecobstetros, habitos_toxicos, " +
                    "farmacos_uso_habitual, reaccion_anestesicos, especifique_anestesia, " +
                    "complicaciones_tratamientos_previos, habitos_bucales, frecuencia_cepillado, " +
                    "tipo_cepillo_cerdas, uso_hilo_dental, tipo_mordida, " +
                    "diagnostico_presuntivo, observaciones_generales) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPacientes);
                stmt.setString(2, nullIfEmpty(remitidoPor));
                stmt.setString(3, nullIfEmpty(antPatologicos));
                stmt.setString(4, nullIfEmpty(antOdontologicos));
                stmt.setString(5, nullIfEmpty(antQuirurgicos));
                stmt.setString(6, nullIfEmpty(antGinecoObstetros));
                stmt.setString(7, nullIfEmpty(habitosToxicos));
                stmt.setString(8, nullIfEmpty(farmacosHabituales));
                // reaccion_anestesicos es ENUM('Si','No')
                stmt.setString(9,  "Si".equals(reaccionAnest) ? "Si" : "No");
                stmt.setString(10, nullIfEmpty(especAnest));
                stmt.setString(11, nullIfEmpty(complicaciones));
                stmt.setString(12, nullIfEmpty(habitosBucales));
                stmt.setString(13, nullIfEmpty(frecuenciaCepillado));
                stmt.setString(14, nullIfEmpty(tipoCerdas));
                stmt.setString(15, nullIfEmpty(usoHiloDental));
                stmt.setString(16, nullIfEmpty(tipoMordida));
                stmt.setString(17, nullIfEmpty(diagnosticoPresuntivo));
                stmt.setString(18, nullIfEmpty(observacionesGenerales));

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

    public String registrarEvolucion(int idPacientes, int idExpediente, int idMedico,
            String fechaConsulta, String motivoConsulta, String sintomaPrincipal,
            String presionArterial, String pulsoCardiaco, String temperatura,
            String tejidosBlandos, String diagnostico, String estadoOdontograma,
            double pagoAbono, String observaciones) {
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
                    "(id_pacientes, id_expediente_base, id_personal_medico, numero_cita, fecha_consulta, " +
                    "motivo_consulta, sintoma_principal, presion_arterial, pulso_cardiaco, temperatura, " +
                    "tejidos_blandos_observacion, diagnostico, estado_odontograma, pago_abono, observaciones) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPacientes);
                stmt.setInt(2, idExpediente);
                stmt.setInt(3, idMedico);
                stmt.setInt(4, numeroCita);
                stmt.setString(5, fechaConsulta);
                stmt.setString(6, nullIfEmpty(motivoConsulta));
                stmt.setString(7, nullIfEmpty(sintomaPrincipal));
                stmt.setString(8, nullIfEmpty(presionArterial));
                stmt.setString(9, nullIfEmpty(pulsoCardiaco));
                stmt.setString(10, nullIfEmpty(temperatura));
                stmt.setString(11, nullIfEmpty(tejidosBlandos));
                stmt.setString(12, nullIfEmpty(diagnostico));
                stmt.setString(13, nullIfEmpty(estadoOdontograma));
                stmt.setDouble(14, pagoAbono);
                stmt.setString(15, nullIfEmpty(observaciones));

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
                       "FROM Personal_Medico WHERE borrado = 'No' ORDER BY nombre_completo ASC";
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
    // HELPER
    // ==============================================================

    private String nullIfEmpty(String val) {
        return (val == null || val.trim().isEmpty()) ? null : val.trim();
    }
}
