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

    // ------------------------------------------------------------------
    // BÚSQUEDA CON FILTROS DINÁMICOS (texto y/o rango de fechas)
    // termino    : busca en descripcion y numero_comprobante (LIKE)
    // fechaDesde : fecha mínima del gasto (YYYY-MM-DD), ignorado si vacío
    // fechaHasta : fecha máxima del gasto (YYYY-MM-DD), ignorado si vacío
    // ------------------------------------------------------------------
    public java.util.List<java.util.Map<String, Object>> buscarEgresos(
            String termino, String fechaDesde, String fechaHasta) {

        java.util.List<java.util.Map<String, Object>> lista = new java.util.ArrayList<>();

        boolean hayTermino = termino    != null && !termino.trim().isEmpty();
        boolean hayDesde   = fechaDesde != null && !fechaDesde.trim().isEmpty();
        boolean hayHasta   = fechaHasta != null && !fechaHasta.trim().isEmpty();

        StringBuilder sb = new StringBuilder(
            "SELECT id_egresos_gastos, fecha, descripcion, monto, numero_comprobante, borrado " +
            "FROM Egresos_Gastos WHERE borrado = 'No'"
        );

        java.util.List<Object> params = new java.util.ArrayList<>();

        if (hayTermino) {
            sb.append(" AND (descripcion LIKE ? OR numero_comprobante LIKE ?)");
            String like = "%" + termino.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (hayDesde) {
            sb.append(" AND fecha >= ?");
            params.add(fechaDesde.trim());
        }
        if (hayHasta) {
            sb.append(" AND fecha <= ?");
            params.add(fechaHasta.trim());
        }

        sb.append(" ORDER BY id_egresos_gastos DESC");

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sb.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                        map.put("id_egreso",          rs.getInt("id_egresos_gastos"));
                        map.put("fecha",              rs.getString("fecha"));
                        map.put("descripcion",        rs.getString("descripcion"));
                        map.put("monto",              rs.getDouble("monto"));
                        map.put("numero_comprobante", rs.getString("numero_comprobante"));
                        map.put("estado",             rs.getString("borrado"));
                        lista.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar egresos: " + e.getMessage());
        }
        return lista;
    }
}

