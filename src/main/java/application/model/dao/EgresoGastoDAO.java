package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EgresoGastoDAO {

    /**
     * @param idCajaSesion NULL si el gasto no afecta caja chica; valor entero si sí afecta.
     */
    public boolean registrarEgreso(Integer idCajaSesion, int idUsuario, String fecha, String descripcion,
                                   double monto, String metodoPago, String numeroComprobante) {
        String query = "INSERT INTO Egresos_Gastos " +
                       "(id_caja_sesion, id_usuario_login, fecha, descripcion, monto, metodo_pago, numero_comprobante) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                if (idCajaSesion != null) stmt.setInt(1, idCajaSesion);
                else                      stmt.setNull(1, java.sql.Types.INTEGER);
                stmt.setInt(2, idUsuario);
                stmt.setString(3, fecha);
                stmt.setString(4, descripcion);
                stmt.setDouble(5, monto);
                stmt.setString(6, metodoPago != null ? metodoPago : "Efectivo");
                stmt.setString(7, numeroComprobante != null ? numeroComprobante : "");
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar egreso: " + e.getMessage());
            return false;
        }
    }

    /** Devuelve TODOS los egresos (activos e inactivos) con campo "estado" = Activo / Inactivo */
    public List<Map<String, Object>> obtenerEgresos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT id_egreso_gasto, id_caja_sesion, id_usuario_login, fecha, " +
                       "descripcion, monto, metodo_pago, numero_comprobante, anulado " +
                       "FROM Egresos_Gastos ORDER BY id_egreso_gasto DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener egresos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * @param idCajaSesion NULL si el gasto no afecta caja chica.
     */
    public boolean actualizarEgreso(int id, Integer idCajaSesion, int idUsuario, String fecha,
                                    String descripcion, double monto, String metodoPago, String numeroComprobante) {
        String query = "UPDATE Egresos_Gastos " +
                       "SET id_caja_sesion = ?, id_usuario_login = ?, fecha = ?, descripcion = ?, " +
                       "monto = ?, metodo_pago = ?, numero_comprobante = ? " +
                       "WHERE id_egreso_gasto = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                if (idCajaSesion != null) stmt.setInt(1, idCajaSesion);
                else                      stmt.setNull(1, java.sql.Types.INTEGER);
                stmt.setInt(2, idUsuario);
                stmt.setString(3, fecha);
                stmt.setString(4, descripcion);
                stmt.setDouble(5, monto);
                stmt.setString(6, metodoPago != null ? metodoPago : "Efectivo");
                stmt.setString(7, numeroComprobante != null ? numeroComprobante : "");
                stmt.setInt(8, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar egreso: " + e.getMessage());
            return false;
        }
    }

    public boolean inactivarEgreso(int id) {
        String query = "UPDATE Egresos_Gastos e " +
                       "LEFT JOIN Caja_Sesiones c ON e.id_caja_sesion = c.id_caja_sesion " +
                       "SET e.anulado = TRUE, e.fecha_anulado = NOW() " +
                       "WHERE e.id_egreso_gasto = ? AND (c.id_caja_sesion IS NULL OR LOWER(c.estado) = 'abierta')";
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

    /** Búsqueda con filtros (texto y/o rango de fechas). Devuelve activos e inactivos. */
    public List<Map<String, Object>> buscarEgresos(String termino, String fechaDesde, String fechaHasta) {
        List<Map<String, Object>> lista = new ArrayList<>();

        boolean hayTermino = termino    != null && !termino.trim().isEmpty();
        boolean hayDesde   = fechaDesde != null && !fechaDesde.trim().isEmpty();
        boolean hayHasta   = fechaHasta != null && !fechaHasta.trim().isEmpty();

        StringBuilder sb = new StringBuilder(
            "SELECT id_egreso_gasto, id_caja_sesion, id_usuario_login, fecha, " +
            "descripcion, monto, metodo_pago, numero_comprobante, anulado " +
            "FROM Egresos_Gastos WHERE anulado = FALSE"
        );

        List<Object> params = new ArrayList<>();

        if (hayTermino) {
            sb.append(" AND (descripcion LIKE ? OR numero_comprobante LIKE ?)");
            String like = "%" + termino.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (hayDesde) {
            sb.append(" AND DATE(fecha) >= ?");
            params.add(fechaDesde.trim());
        }
        if (hayHasta) {
            sb.append(" AND DATE(fecha) <= ?");
            params.add(fechaHasta.trim());
        }
        sb.append(" ORDER BY id_egreso_gasto DESC");

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sb.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapRow(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar egresos: " + e.getMessage());
        }
        return lista;
    }

    /** Helper: mapea una fila del ResultSet a Map, con "estado" como Activo/Inactivo */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id_egreso",          rs.getInt("id_egreso_gasto"));
        int cajaSesion = rs.getInt("id_caja_sesion");
        map.put("id_caja_sesion",     rs.wasNull() ? null : cajaSesion);
        map.put("id_usuario",         rs.getInt("id_usuario_login")); // keep frontend compatible key
        map.put("fecha",              rs.getString("fecha"));
        map.put("descripcion",        rs.getString("descripcion"));
        map.put("monto",              rs.getDouble("monto"));
        map.put("metodo_pago",        rs.getString("metodo_pago"));
        map.put("numero_comprobante", rs.getString("numero_comprobante"));
        boolean anulado = rs.getBoolean("anulado");
        map.put("estado",             anulado ? "Inactivo" : "Activo");
        return map;
    }
}
