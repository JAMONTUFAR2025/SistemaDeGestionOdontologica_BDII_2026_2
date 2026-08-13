package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para la tabla Catalogo_Alergias.
 * Permite CRUD completo del catálogo de alergias.
 */
public class CatalogoAlergiasDAO {

    /** Retorna todas las alergias del catálogo. */
    public List<Map<String, Object>> obtenerTodas() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT id_catalogo_alergia, nombre_alergia FROM Catalogo_Alergias ORDER BY nombre_alergia ASC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id_catalogo_alergias", rs.getInt("id_catalogo_alergia")); // Keep map key for frontend compatibility
                    map.put("nombre_alergia", rs.getString("nombre_alergia"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener catálogo de alergias: " + e.getMessage());
        }
        return lista;
    }

    /** Inserta una nueva alergia en el catálogo. */
    public boolean insertar(String nombreAlergia) {
        String query = "INSERT INTO Catalogo_Alergias (nombre_alergia) VALUES (?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nombreAlergia.trim());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar alergia: " + e.getMessage());
            return false;
        }
    }

    /** Actualiza una alergia existente. */
    public boolean actualizar(int id, String nombreAlergia) {
        String query = "UPDATE Catalogo_Alergias SET nombre_alergia = ? WHERE id_catalogo_alergia = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nombreAlergia.trim());
                stmt.setInt(2, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar alergia: " + e.getMessage());
            return false;
        }
    }

    /** Elimina físicamente una alergia del catálogo. */
    public boolean eliminar(int id) {
        String query = "DELETE FROM Catalogo_Alergias WHERE id_catalogo_alergia = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar alergia: " + e.getMessage());
            return false;
        }
    }
}
