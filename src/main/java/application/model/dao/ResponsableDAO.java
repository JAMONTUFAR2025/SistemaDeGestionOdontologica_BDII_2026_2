package application.model.dao;

import application.model.connection.DBConnection;
import application.model.entity.Responsable;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para la tabla Responsables.
 * CRUD completo con borrado lógico.
 */
public class ResponsableDAO {

    public String registrar(Responsable r) {
        String query = "INSERT INTO Responsables (identidad, nombre_completo, telefono, correo, parentesco) " +
                       "VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nullIfEmpty(r.getIdentidad()));
                stmt.setString(2, r.getNombreCompleto());
                stmt.setString(3, r.getTelefono());
                stmt.setString(4, nullIfEmpty(r.getCorreo()));
                stmt.setString(5, r.getParentesco());
                int rows = stmt.executeUpdate();
                return rows > 0 ? "OK|Responsable registrado exitosamente." :
                                  "ERR|No se pudo registrar el responsable.";
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar responsable: " + e.getMessage());
            return "ERR|Error de base de datos: " + e.getMessage();
        }
    }

    public String actualizar(int idResponsable, String identidad, String nombreCompleto,
                             String telefono, String correo, String parentesco) {
        String query = "UPDATE Responsables SET identidad = ?, nombre_completo = ?, telefono = ?, " +
                       "correo = ?, parentesco = ? WHERE id_responsable = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nullIfEmpty(identidad));
                stmt.setString(2, nombreCompleto);
                stmt.setString(3, telefono);
                stmt.setString(4, nullIfEmpty(correo));
                stmt.setString(5, parentesco);
                stmt.setInt(6, idResponsable);
                int rows = stmt.executeUpdate();
                return rows > 0 ? "OK|Responsable actualizado exitosamente." :
                                  "ERR|No se encontró el responsable a actualizar.";
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar responsable: " + e.getMessage());
            return "ERR|Error de base de datos: " + e.getMessage();
        }
    }

    public List<Map<String, Object>> obtenerTodos(boolean incluirInactivos) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT id_responsable, identidad, nombre_completo, telefono, correo, " +
                       "parentesco, borrado, fecha_borrado FROM Responsables ";
        if (!incluirInactivos) {
            query += "WHERE borrado = FALSE ";
        }
        query += "ORDER BY id_responsable DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id_responsable", rs.getInt("id_responsable"));
                    map.put("identidad", rs.getString("identidad"));
                    map.put("nombre_completo", rs.getString("nombre_completo"));
                    map.put("telefono", rs.getString("telefono"));
                    map.put("correo", rs.getString("correo"));
                    map.put("parentesco", rs.getString("parentesco"));
                    map.put("estado", rs.getBoolean("borrado") ? "Inactivo" : "Activo");
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener responsables: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminar(int idResponsable) {
        String query = "UPDATE Responsables SET borrado = TRUE, fecha_borrado = NOW() WHERE id_responsable = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idResponsable);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar responsable: " + e.getMessage());
            return false;
        }
    }

    public boolean reactivar(int idResponsable) {
        String query = "UPDATE Responsables SET borrado = FALSE, fecha_borrado = NULL WHERE id_responsable = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idResponsable);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al reactivar responsable: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Object> buscarPorId(int idResponsable) {
        String query = "SELECT id_responsable, identidad, nombre_completo, telefono, correo, parentesco, borrado " +
                       "FROM Responsables WHERE id_responsable = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idResponsable);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_responsable", rs.getInt("id_responsable"));
                        map.put("identidad", rs.getString("identidad"));
                        map.put("nombre_completo", rs.getString("nombre_completo"));
                        map.put("telefono", rs.getString("telefono"));
                        map.put("correo", rs.getString("correo"));
                        map.put("parentesco", rs.getString("parentesco"));
                        map.put("estado", rs.getBoolean("borrado") ? "Inactivo" : "Activo");
                        return map;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar responsable: " + e.getMessage());
        }
        return null;
    }

    public boolean existe(String identidad) {
        String query = "SELECT COUNT(*) FROM Responsables WHERE identidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identidad);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar si existe responsable: " + e.getMessage());
        }
        return false;
    }

    private String nullIfEmpty(String val) {
        return (val == null || val.trim().isEmpty()) ? null : val.trim();
    }
}
