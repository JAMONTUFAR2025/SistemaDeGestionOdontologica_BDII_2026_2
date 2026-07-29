package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EgresoGastoDAO {

    public boolean registrarEgreso(String fecha, String descripcion, double monto, String numeroComprobante) {
        String query = "INSERT INTO Egresos_Gastos (fecha, descripcion, monto, numero_comprobante, estado) VALUES (?, ?, ?, ?, 'Activo')";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, fecha);
                stmt.setString(2, descripcion);
                stmt.setDouble(3, monto);
                stmt.setString(4, numeroComprobante != null ? numeroComprobante : "");
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar egreso: " + e.getMessage());
            return false;
        }
    }

    public java.util.List<java.util.Map<String, Object>> obtenerEgresos() {
        java.util.List<java.util.Map<String, Object>> lista = new java.util.ArrayList<>();
        String query = "SELECT id_egreso, fecha, descripcion, monto, numero_comprobante, estado FROM Egresos_Gastos WHERE estado = 'Activo' ORDER BY id_egreso DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                    map.put("id_egreso", rs.getInt("id_egreso"));
                    map.put("fecha", rs.getString("fecha"));
                    map.put("descripcion", rs.getString("descripcion"));
                    map.put("monto", rs.getDouble("monto"));
                    map.put("numero_comprobante", rs.getString("numero_comprobante"));
                    map.put("estado", rs.getString("estado"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener egresos: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarEgreso(int id, String fecha, String descripcion, double monto, String numeroComprobante) {
        String query = "UPDATE Egresos_Gastos SET fecha = ?, descripcion = ?, monto = ?, numero_comprobante = ? WHERE id_egreso = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, fecha);
                stmt.setString(2, descripcion);
                stmt.setDouble(3, monto);
                stmt.setString(4, numeroComprobante != null ? numeroComprobante : "");
                stmt.setInt(5, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar egreso: " + e.getMessage());
            return false;
        }
    }

    public boolean inactivarEgreso(int id) {
        String query = "UPDATE Egresos_Gastos SET estado = 'Inactivo', fecha_inactivacion = NOW() WHERE id_egreso = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al inactivar egreso: " + e.getMessage());
            return false;
        }
    }
}
