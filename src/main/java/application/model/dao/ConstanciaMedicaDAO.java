package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para la tabla Constancias_Medicas.
 * Gestiona las constancias médicas generadas por consulta.
 */
public class ConstanciaMedicaDAO {

    /** Retorna todas las constancias vinculadas a una evolución clínica. */
    public List<Map<String, Object>> obtenerPorEvolucion(int idEvolucionClinica) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT id_constancias_medicas, fecha_emision, hora_emision, tratamiento_realizado " +
                       "FROM Constancias_Medicas WHERE id_evolucion_clinica = ? " +
                       "ORDER BY fecha_emision DESC, hora_emision DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idEvolucionClinica);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_constancias_medicas",  rs.getInt("id_constancias_medicas"));
                        map.put("fecha_emision",           rs.getString("fecha_emision"));
                        map.put("hora_emision",            rs.getString("hora_emision"));
                        map.put("tratamiento_realizado",   rs.getString("tratamiento_realizado"));
                        lista.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener constancias médicas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Retorna TODAS las constancias de un paciente (joins para obtener fecha de consulta y médico).
     */
    public List<Map<String, Object>> obtenerPorPaciente(int idPacientes) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT cm.id_constancias_medicas, cm.fecha_emision, cm.hora_emision, " +
                       "cm.tratamiento_realizado, c.fecha_hora AS fecha_consulta, pm.nombre_completo AS nombre_medico " +
                       "FROM Constancias_Medicas cm " +
                       "INNER JOIN Evolucion_Clinica ec ON cm.id_evolucion_clinica = ec.id_evolucion_clinica " +
                       "LEFT JOIN Citas c ON ec.id_cita = c.id_cita " +
                       "INNER JOIN Personal_Medico pm ON ec.id_personal_medico = pm.id_personal_medico " +
                       "INNER JOIN Expediente_Base eb ON ec.id_expediente_base = eb.id_expediente_base " +
                       "WHERE eb.id_paciente = ? ORDER BY cm.fecha_emision DESC, cm.hora_emision DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPacientes);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_constancias_medicas", rs.getInt("id_constancias_medicas"));
                        map.put("fecha_emision",          rs.getString("fecha_emision"));
                        map.put("hora_emision",           rs.getString("hora_emision"));
                        map.put("tratamiento_realizado",  rs.getString("tratamiento_realizado"));
                        map.put("fecha_consulta",         rs.getString("fecha_consulta"));
                        map.put("nombre_medico",          rs.getString("nombre_medico"));
                        lista.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener constancias por paciente: " + e.getMessage());
        }
        return lista;
    }

    /** Registra una nueva constancia médica. */
    public boolean registrar(int idEvolucionClinica, String fechaEmision,
                             String horaEmision, String tratamientoRealizado) {
        String query = "INSERT INTO Constancias_Medicas " +
                       "(id_evolucion_clinica, fecha_emision, hora_emision, tratamiento_realizado) " +
                       "VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idEvolucionClinica);
                stmt.setString(2, fechaEmision);
                stmt.setString(3, horaEmision);
                stmt.setString(4, tratamientoRealizado);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar constancia médica: " + e.getMessage());
            return false;
        }
    }

    /** Elimina físicamente una constancia médica. */
    public boolean eliminar(int idConstancia) {
        String query = "DELETE FROM Constancias_Medicas WHERE id_constancias_medicas = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idConstancia);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar constancia médica: " + e.getMessage());
            return false;
        }
    }
}
