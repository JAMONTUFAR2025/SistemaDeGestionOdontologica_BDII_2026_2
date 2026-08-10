package application.model.dao;

import application.model.connection.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para la tabla Facturacion.
 */
public class FacturacionDAO {

    public boolean registrarRecibo(String numeroRecibo, int idPaciente, int idCajaSesion, int idUsuario, String rtnCliente,
                                   String fechaEmision, String concepto,
                                   double sumaNeta, double totalHonorarios,
                                   double totalRetenido, double totalNetoRecibido,
                                   String metodoPago) {
        String query = "INSERT INTO Facturacion " +
                "(numero_recibo, id_pacientes, id_caja_sesion, id_usuario, rtn_cliente, fecha_emision, concepto, " +
                "suma_neta, total_honorarios, total_retenido, total_neto_recibido, metodo_pago) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, numeroRecibo != null ? numeroRecibo : "");
                stmt.setInt(2, idPaciente);
                stmt.setInt(3, idCajaSesion);
                stmt.setInt(4, idUsuario);
                stmt.setString(5, rtnCliente != null ? rtnCliente : "");
                stmt.setString(6, fechaEmision);
                stmt.setString(7, concepto);
                stmt.setDouble(8, sumaNeta);
                stmt.setDouble(9, totalHonorarios);
                stmt.setDouble(10, totalRetenido);
                stmt.setDouble(11, totalNetoRecibido);
                stmt.setString(12, metodoPago);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar recibo: " + e.getMessage());
            return false;
        }
    }

    /** Devuelve TODOS los recibos (activos e inactivos) con campo "estado" = Activo / Inactivo */
    public List<Map<String, Object>> obtenerRecibos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT fr.id_facturacion_recibos, fr.numero_recibo, fr.id_pacientes, " +
                       "fr.id_caja_sesion, fr.id_usuario, " +
                       "p.nombre_completo AS nombre_paciente, p.identidad AS identidad_paciente, " +
                       "fr.rtn_cliente, fr.fecha_emision, fr.concepto, " +
                       "fr.suma_neta, fr.total_honorarios, fr.total_retenido, " +
                       "fr.total_neto_recibido, fr.metodo_pago, fr.anulado " +
                       "FROM Facturacion fr " +
                       "INNER JOIN Pacientes p ON fr.id_pacientes = p.id_pacientes " +
                       "ORDER BY fr.id_facturacion_recibos DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener recibos: " + e.getMessage());
        }
        return lista;
    }

    public Map<String, Object> obtenerReciboPorId(int idFactura) {
        String query = "SELECT fr.id_facturacion_recibos, fr.numero_recibo, fr.id_pacientes, " +
                       "fr.id_caja_sesion, fr.id_usuario, " +
                       "p.nombre_completo AS nombre_paciente, p.identidad AS identidad_paciente, " +
                       "fr.rtn_cliente, fr.fecha_emision, fr.concepto, " +
                       "fr.suma_neta, fr.total_honorarios, fr.total_retenido, " +
                       "fr.total_neto_recibido, fr.metodo_pago, fr.anulado " +
                       "FROM Facturacion fr " +
                       "INNER JOIN Pacientes p ON fr.id_pacientes = p.id_pacientes " +
                       "WHERE fr.id_facturacion_recibos = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idFactura);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapRow(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener recibo por id: " + e.getMessage());
        }
        return null;
    }

    public String obtenerMedicoDeCitaReciente(int idPaciente) {
        String query = "SELECT pm.nombre_completo " +
                       "FROM Citas c " +
                       "INNER JOIN Personal_Medico pm ON c.id_personal_medico = pm.id_personal_medico " +
                       "WHERE c.id_pacientes = ? AND c.estado = 'Completada' " +
                       "ORDER BY c.fecha_hora DESC LIMIT 1";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPaciente);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("nombre_completo");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener medico de cita: " + e.getMessage());
        }
        return "N/D";
    }

    public boolean anularRecibo(int id) {
        String query = "UPDATE Facturacion SET anulado = 'Si', fecha_anulado = NOW() " +
                       "WHERE id_facturacion_recibos = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al anular recibo: " + e.getMessage());
            return false;
        }
    }

    public int obtenerIdPacientePorIdentidad(String identidad) {
        String query = "SELECT id_pacientes FROM Pacientes WHERE identidad = ? AND borrado = 'No'";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identidad);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return rs.getInt("id_pacientes");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar paciente por identidad: " + e.getMessage());
        }
        return -1;
    }

    public List<Map<String, Object>> obtenerPacientesActivos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT id_pacientes, identidad, nombre_completo FROM Pacientes " +
                       "WHERE borrado = 'No' ORDER BY nombre_completo ASC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id_paciente", rs.getInt("id_pacientes"));
                    map.put("identidad", rs.getString("identidad"));
                    map.put("nombre_completo", rs.getString("nombre_completo"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar pacientes activos: " + e.getMessage());
        }
        return lista;
    }

    public List<Map<String, Object>> buscarRecibos(String termino, String fechaDesde, String fechaHasta) {
        List<Map<String, Object>> lista = new ArrayList<>();

        boolean hayTermino   = termino    != null && !termino.trim().isEmpty();
        boolean hayDesde     = fechaDesde != null && !fechaDesde.trim().isEmpty();
        boolean hayHasta     = fechaHasta != null && !fechaHasta.trim().isEmpty();

        StringBuilder sb = new StringBuilder(
            "SELECT fr.id_facturacion_recibos, fr.numero_recibo, fr.id_pacientes, " +
            "fr.id_caja_sesion, fr.id_usuario, " +
            "p.nombre_completo AS nombre_paciente, p.identidad AS identidad_paciente, " +
            "fr.rtn_cliente, fr.fecha_emision, fr.concepto, " +
            "fr.suma_neta, fr.total_honorarios, fr.total_retenido, " +
            "fr.total_neto_recibido, fr.metodo_pago, fr.anulado " +
            "FROM Facturacion fr " +
            "INNER JOIN Pacientes p ON fr.id_pacientes = p.id_pacientes " +
            "WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (hayTermino) {
            sb.append(" AND (fr.numero_recibo LIKE ? OR p.nombre_completo LIKE ? OR fr.concepto LIKE ?)");
            String like = "%" + termino.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (hayDesde) {
            sb.append(" AND fr.fecha_emision >= ?");
            params.add(fechaDesde.trim());
        }
        if (hayHasta) {
            sb.append(" AND fr.fecha_emision <= ?");
            params.add(fechaHasta.trim());
        }

        sb.append(" ORDER BY fr.id_facturacion_recibos DESC");

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
            System.err.println("Error al buscar recibos: " + e.getMessage());
        }
        return lista;
    }

    /** Helper: mapea una fila del ResultSet a Map, con "estado" como Activo/Inactivo */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id_factura",          rs.getInt("id_facturacion_recibos"));
        map.put("numero_recibo",       rs.getString("numero_recibo"));
        map.put("id_paciente",         rs.getInt("id_pacientes"));
        map.put("id_caja_sesion",      rs.getInt("id_caja_sesion"));
        map.put("id_usuario",          rs.getInt("id_usuario"));
        map.put("nombre_paciente",     rs.getString("nombre_paciente"));
        map.put("identidad_paciente",  rs.getString("identidad_paciente"));
        map.put("rtn_cliente",         rs.getString("rtn_cliente"));
        map.put("fecha_emision",       rs.getString("fecha_emision"));
        map.put("concepto",            rs.getString("concepto"));
        map.put("suma_neta",           rs.getDouble("suma_neta"));
        map.put("total_honorarios",    rs.getDouble("total_honorarios"));
        map.put("total_retenido",      rs.getDouble("total_retenido"));
        map.put("total_neto_recibido", rs.getDouble("total_neto_recibido"));
        map.put("metodo_pago",         rs.getString("metodo_pago"));
        String anulado = rs.getString("anulado");
        map.put("estado",              "Si".equals(anulado) ? "Inactivo" : "Activo");
        return map;
    }
}
