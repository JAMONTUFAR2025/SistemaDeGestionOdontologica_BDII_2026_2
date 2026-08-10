package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para la tabla Catalogo_Procedimientos.
 * Permite CRUD completo del catálogo de procedimientos odontológicos.
 */
public class CatalogoProcedimientosDAO {

    /** Retorna todos los procedimientos del catálogo. */
    public List<Map<String, Object>> obtenerTodos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT id_catalogo_procedimientos, nombre_procedimiento, precio_sugerido " +
                       "FROM Catalogo_Procedimientos ORDER BY nombre_procedimiento ASC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id_catalogo_procedimientos", rs.getInt("id_catalogo_procedimientos"));
                    map.put("nombre_procedimiento", rs.getString("nombre_procedimiento"));
                    map.put("precio_sugerido", rs.getDouble("precio_sugerido"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener catálogo de procedimientos: " + e.getMessage());
        }
        return lista;
    }

    /** Inserta un nuevo procedimiento en el catálogo. */
    public boolean insertar(String nombreProcedimiento, double precioSugerido) {
        String query = "INSERT INTO Catalogo_Procedimientos (nombre_procedimiento, precio_sugerido) VALUES (?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nombreProcedimiento.trim());
                stmt.setDouble(2, precioSugerido);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar procedimiento: " + e.getMessage());
            return false;
        }
    }

    /** Actualiza un procedimiento existente. */
    public boolean actualizar(int id, String nombreProcedimiento, double precioSugerido) {
        String query = "UPDATE Catalogo_Procedimientos SET nombre_procedimiento = ?, precio_sugerido = ? " +
                       "WHERE id_catalogo_procedimientos = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nombreProcedimiento.trim());
                stmt.setDouble(2, precioSugerido);
                stmt.setInt(3, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar procedimiento: " + e.getMessage());
            return false;
        }
    }

    /** Elimina físicamente un procedimiento del catálogo. */
    public boolean eliminar(int id) {
        String query = "DELETE FROM Catalogo_Procedimientos WHERE id_catalogo_procedimientos = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar procedimiento: " + e.getMessage());
            return false;
        }
    }
}
