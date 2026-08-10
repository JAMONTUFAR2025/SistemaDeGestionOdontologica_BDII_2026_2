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
    public List<Map<String, Object>> obtenerPorPaciente(int idPacientes) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT pa.id_paciente_alergias, pa.id_catalogo_alergias, ca.nombre_alergia " +
                       "FROM Paciente_Alergias pa " +
                       "INNER JOIN Catalogo_Alergias ca ON pa.id_catalogo_alergias = ca.id_catalogo_alergias " +
                       "WHERE pa.id_pacientes = ? ORDER BY ca.nombre_alergia ASC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPacientes);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_paciente_alergias", rs.getInt("id_paciente_alergias"));
                        map.put("id_catalogo_alergias", rs.getInt("id_catalogo_alergias"));
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
    public boolean agregar(int idPacientes, int idCatalogoAlergia) {
        String query = "INSERT IGNORE INTO Paciente_Alergias (id_pacientes, id_catalogo_alergias) VALUES (?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPacientes);
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
        String query = "DELETE FROM Paciente_Alergias WHERE id_paciente_alergias = ?";
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
    public boolean eliminarTodasDeUnPaciente(int idPacientes) {
        String query = "DELETE FROM Paciente_Alergias WHERE id_pacientes = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPacientes);
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar todas las alergias del paciente: " + e.getMessage());
            return false;
        }
    }
}
