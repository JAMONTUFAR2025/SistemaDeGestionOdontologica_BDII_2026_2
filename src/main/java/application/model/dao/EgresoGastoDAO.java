package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EgresoGastoDAO {

    public boolean registrarEgreso(String fecha, String descripcion, double monto, String numeroComprobante) {
        // SchemaActual: columnas id_egresos_gastos, borrado ENUM('Si','No') DEFAULT 'No'
        // No existe columna "estado" en SchemaActual — solo borrado
        String query = "INSERT INTO Egresos_Gastos (fecha, descripcion, monto, numero_comprobante) VALUES (?, ?, ?, ?)";
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
        // SchemaActual: PK = id_egresos_gastos, borrado ENUM('Si','No')
        String query = "SELECT id_egresos_gastos, fecha, descripcion, monto, numero_comprobante, borrado " +
                       "FROM Egresos_Gastos WHERE borrado = 'No' ORDER BY id_egresos_gastos DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                    map.put("id_egreso", rs.getInt("id_egresos_gastos"));
                    map.put("fecha", rs.getString("fecha"));
                    map.put("descripcion", rs.getString("descripcion"));
                    map.put("monto", rs.getDouble("monto"));
                    map.put("numero_comprobante", rs.getString("numero_comprobante"));
                    map.put("estado", rs.getString("borrado"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener egresos: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarEgreso(int id, String fecha, String descripcion, double monto, String numeroComprobante) {
        // SchemaActual: WHERE id_egresos_gastos = ?
        String query = "UPDATE Egresos_Gastos SET fecha = ?, descripcion = ?, monto = ?, numero_comprobante = ? " +
                       "WHERE id_egresos_gastos = ?";
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
        // SchemaActual: borrado='Si', fecha_borrado en vez de estado/fecha_inactivacion
        String query = "UPDATE Egresos_Gastos SET borrado = 'Si', fecha_borrado = NOW() WHERE id_egresos_gastos = ?";
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
