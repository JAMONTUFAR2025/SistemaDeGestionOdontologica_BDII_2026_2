package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para la tabla Paciente_Alergias.
 * Gestión de alergias asignadas a un paciente específico.
 */
public class PacienteAlergiaDAO {

    /** Retorna todas las alergias de un paciente con su nombre descriptivo. */
    public List<Map<String, Object>> obtenerPorPaciente(int idPaciente) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT pa.id_paciente_alergia, pa.id_catalogo_alergia, ca.nombre_alergia " +
                       "FROM Paciente_Alergias pa " +
                       "INNER JOIN Catalogo_Alergias ca ON pa.id_catalogo_alergia = ca.id_catalogo_alergia " +
                       "WHERE pa.id_paciente = ? ORDER BY ca.nombre_alergia ASC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPaciente);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_paciente_alergias", rs.getInt("id_paciente_alergia")); // compat
                        map.put("id_catalogo_alergias", rs.getInt("id_catalogo_alergia")); // compat
                        map.put("nombre_alergia", rs.getString("nombre_alergia"));
                        lista.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener alergias del paciente: " + e.getMessage());
        }
        return lista;
    }

    /** Asigna una alergia del catálogo a un paciente. Ignora duplicados silenciosamente. */
    public boolean agregar(int idPaciente, int idCatalogoAlergia) {
        String query = "INSERT IGNORE INTO Paciente_Alergias (id_paciente, id_catalogo_alergia) VALUES (?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPaciente);
                stmt.setInt(2, idCatalogoAlergia);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar alergia al paciente: " + e.getMessage());
            return false;
        }
    }

    /** Elimina la asociación alergia-paciente por su ID de registro. */
    public boolean eliminar(int idPacienteAlergia) {
        String query = "DELETE FROM Paciente_Alergias WHERE id_paciente_alergia = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPacienteAlergia);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar alergia del paciente: " + e.getMessage());
            return false;
        }
    }

    /** Elimina TODAS las alergias de un paciente (útil para resetear). */
    public boolean eliminarTodasDeUnPaciente(int idPaciente) {
        String query = "DELETE FROM Paciente_Alergias WHERE id_paciente = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPaciente);
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar todas las alergias del paciente: " + e.getMessage());
            return false;
        }
    }
}
