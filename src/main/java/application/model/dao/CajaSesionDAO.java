package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DAO para la tabla Caja_Sesiones.
 * Regla de negocio: solo puede existir UNA caja con estado='Abierta' a la vez en todo el sistema.
 */
public class CajaSesionDAO {

    // ------------------------------------------------------------------
    // Obtener la caja actualmente abierta (GLOBAL — una sola a la vez)
    // ------------------------------------------------------------------
    public Map<String, Object> obtenerCajaActiva() {
        String query = "SELECT id_caja_sesion, id_usuario_apertura, monto_apertura, fecha_apertura " +
                       "FROM Caja_Sesiones WHERE estado = 'Abierta' ORDER BY id_caja_sesion ASC LIMIT 1";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id_caja_sesion",      rs.getInt("id_caja_sesion"));
                    map.put("id_usuario_apertura", rs.getInt("id_usuario_apertura"));
                    map.put("monto_apertura",       rs.getDouble("monto_apertura"));
                    map.put("fecha_apertura",       rs.getString("fecha_apertura"));
                    return map;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener caja activa: " + e.getMessage());
        }
        return null;
    }

    public Map<String, Object> obtenerCajaPorId(int idCajaSesion) {
        String query = "SELECT c.id_caja_sesion, c.monto_apertura, c.monto_cierre_real, c.diferencia, " +
                       "c.estado, c.fecha_apertura, c.fecha_cierre, c.observaciones, " +
                       "u1.correo AS usuario_apertura, u2.correo AS usuario_cierre " +
                       "FROM Caja_Sesiones c " +
                       "LEFT JOIN Usuarios_Login u1 ON c.id_usuario_apertura = u1.id_usuarios_login " +
                       "LEFT JOIN Usuarios_Login u2 ON c.id_usuario_cierre = u2.id_usuarios_login " +
                       "WHERE c.id_caja_sesion = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idCajaSesion);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_caja_sesion",      rs.getInt("id_caja_sesion"));
                        map.put("monto_apertura",      rs.getDouble("monto_apertura"));
                        map.put("monto_cierre_real",   rs.getDouble("monto_cierre_real"));
                        map.put("diferencia",          rs.getDouble("diferencia"));
                        map.put("estado",              rs.getString("estado"));
                        map.put("fecha_apertura",      rs.getString("fecha_apertura"));
                        map.put("fecha_cierre",        rs.getString("fecha_cierre"));
                        map.put("observaciones",       rs.getString("observaciones"));
                        map.put("usuario_apertura",    rs.getString("usuario_apertura"));
                        map.put("usuario_cierre",      rs.getString("usuario_cierre"));
                        return map;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener caja por id: " + e.getMessage());
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Abrir nueva caja — solo si NO hay ninguna abierta
    // ------------------------------------------------------------------
    public boolean abrirCaja(int idUsuarioApertura, double montoApertura) {
        // Verificar primero que no hay ninguna caja abierta
        if (obtenerCajaActiva() != null) {
            System.err.println("Ya existe una caja abierta. No se puede abrir otra.");
            return false;
        }
        String query = "INSERT INTO Caja_Sesiones (id_usuario_apertura, monto_apertura, estado, fecha_apertura) " +
                       "VALUES (?, ?, 'Abierta', NOW())";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idUsuarioApertura);
                stmt.setDouble(2, montoApertura);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al abrir caja: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Calcular arqueo: retorna ingresos efectivo, egresos efectivo y saldo esperado
    // monto_cierre_esperado = monto_apertura + SUM(Facturacion efectivo) - SUM(Egresos efectivo)
    // ------------------------------------------------------------------
    public Map<String, Object> calcularArqueoCaja(int idCajaSesion) {
        Map<String, Object> resultado = new LinkedHashMap<>();

        // 1. Monto apertura
        double montoApertura = 0;
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT monto_apertura FROM Caja_Sesiones WHERE id_caja_sesion = ?")) {
                stmt.setInt(1, idCajaSesion);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) montoApertura = rs.getDouble("monto_apertura");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener monto apertura: " + e.getMessage());
        }

        // 2. Total ingresos en efectivo (Facturacion)
        double ingresosEfectivo = 0;
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String qIngr = "SELECT COALESCE(SUM(total_neto_recibido), 0) AS total " +
                           "FROM Facturacion WHERE id_caja_sesion = ? " +
                           "AND LOWER(metodo_pago) = 'efectivo' AND LOWER(anulado) = 'no'";
            try (PreparedStatement stmt = conn.prepareStatement(qIngr)) {
                stmt.setInt(1, idCajaSesion);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) ingresosEfectivo = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular ingresos efectivo: " + e.getMessage());
        }

        // 3. Total egresos en efectivo (Egresos_Gastos)
        double egresosEfectivo = 0;
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String qEgr = "SELECT COALESCE(SUM(monto), 0) AS total " +
                          "FROM Egresos_Gastos WHERE id_caja_sesion = ? " +
                          "AND LOWER(metodo_pago) = 'efectivo' AND LOWER(anulado) = 'no'";
            try (PreparedStatement stmt = conn.prepareStatement(qEgr)) {
                stmt.setInt(1, idCajaSesion);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) egresosEfectivo = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular egresos efectivo: " + e.getMessage());
        }

        // 4. Total ingresos en Transferencia (Facturacion)
        double ingresosTransferencia = 0;
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String qIngrT = "SELECT COALESCE(SUM(total_neto_recibido), 0) AS total " +
                            "FROM Facturacion WHERE id_caja_sesion = ? " +
                            "AND LOWER(metodo_pago) = 'transferencia' AND LOWER(anulado) = 'no'";
            try (PreparedStatement stmt = conn.prepareStatement(qIngrT)) {
                stmt.setInt(1, idCajaSesion);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) ingresosTransferencia = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular ingresos transferencia: " + e.getMessage());
        }

        // 5. Total ingresos en POS/Tarjeta (Facturacion)
        double ingresosPos = 0;
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String qIngrP = "SELECT COALESCE(SUM(total_neto_recibido), 0) AS total " +
                            "FROM Facturacion WHERE id_caja_sesion = ? " +
                            "AND LOWER(metodo_pago) IN ('pos', 'tarjeta') AND LOWER(anulado) = 'no'";
            try (PreparedStatement stmt = conn.prepareStatement(qIngrP)) {
                stmt.setInt(1, idCajaSesion);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) ingresosPos = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular ingresos POS: " + e.getMessage());
        }

        double efectivoEsperado = montoApertura + ingresosEfectivo - egresosEfectivo;

        resultado.put("monto_apertura",    montoApertura);
        resultado.put("ingresos_efectivo", ingresosEfectivo);
        resultado.put("egresos_efectivo",  egresosEfectivo);
        resultado.put("efectivo_esperado", efectivoEsperado);
        resultado.put("ingresos_transferencia", ingresosTransferencia);
        resultado.put("ingresos_pos",      ingresosPos);
        return resultado;
    }

    // ------------------------------------------------------------------
    // Cerrar caja: guarda monto_cierre_real, calcula diferencia, cambia estado
    // ------------------------------------------------------------------
    public boolean cerrarCaja(int idCajaSesion, int idUsuarioCierre, double montoCierreReal, String observaciones) {
        Map<String, Object> arqueo = calcularArqueoCaja(idCajaSesion);
        double efectivoEsperado = (double) arqueo.get("efectivo_esperado");
        double diferencia = montoCierreReal - efectivoEsperado;

        String query = "UPDATE Caja_Sesiones SET " +
                       "id_usuario_cierre = ?, monto_cierre_real = ?, diferencia = ?, " +
                       "estado = 'Cerrada', fecha_cierre = NOW(), observaciones = ? " +
                       "WHERE id_caja_sesion = ? AND LOWER(estado) = 'abierta'";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idUsuarioCierre);
                stmt.setDouble(2, montoCierreReal);
                stmt.setDouble(3, diferencia);
                stmt.setString(4, observaciones != null ? observaciones : "");
                stmt.setInt(5, idCajaSesion);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar caja: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Últimos movimientos del día para el dashboard (cobros + egresos de la sesión)
    // ------------------------------------------------------------------
    public java.util.List<Map<String, Object>> obtenerMovimientosDeSesion(int idCajaSesion) {
        java.util.List<Map<String, Object>> lista = new java.util.ArrayList<>();

        String query = "SELECT 'INGRESO' AS tipo, DATE_FORMAT(f.fecha_emision, '%Y-%m-%d') AS fecha, " +
                       "f.concepto, f.metodo_pago, f.total_neto_recibido AS monto " +
                       "FROM Facturacion f INNER JOIN Pacientes p ON f.id_pacientes = p.id_pacientes " +
                       "WHERE f.id_caja_sesion = ? AND LOWER(f.anulado) = 'no' " +
                       "UNION ALL " +
                       "SELECT 'EGRESO' AS tipo, DATE_FORMAT(fecha, '%Y-%m-%d') AS fecha, " +
                       "descripcion AS concepto, metodo_pago, monto " +
                       "FROM Egresos_Gastos WHERE id_caja_sesion = ? AND LOWER(anulado) = 'no' " +
                       "ORDER BY fecha ASC";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idCajaSesion);
                stmt.setInt(2, idCajaSesion);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("tipo",        rs.getString("tipo"));
                        m.put("fecha",       rs.getString("fecha") != null ? rs.getString("fecha") : "");
                        m.put("concepto",    rs.getString("concepto"));
                        m.put("monto",       rs.getDouble("monto"));
                        m.put("metodo_pago", rs.getString("metodo_pago"));
                        lista.add(m);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos de sesión: " + e.getMessage());
        }

        return lista;
    }

    // ------------------------------------------------------------------
    // Historial de Cierres para Contabilidad
    // ------------------------------------------------------------------
    public java.util.List<Map<String, Object>> obtenerHistorialCierres(String fechaInicio, String fechaFin, Integer idUsuario) {
        java.util.List<Map<String, Object>> lista = new java.util.ArrayList<>();
        StringBuilder query = new StringBuilder(
            "SELECT c.id_caja_sesion, c.monto_apertura, c.monto_cierre_real, c.diferencia, " +
            "c.estado, c.fecha_apertura, c.fecha_cierre, " +
            "u1.correo AS usuario_apertura, u2.correo AS usuario_cierre " +
            "FROM Caja_Sesiones c " +
            "LEFT JOIN Usuarios_Login u1 ON c.id_usuario_apertura = u1.id_usuarios_login " +
            "LEFT JOIN Usuarios_Login u2 ON c.id_usuario_cierre = u2.id_usuarios_login " +
            "WHERE LOWER(c.estado) = 'cerrada'"
        );

        java.util.List<Object> params = new java.util.ArrayList<>();
        if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
            query.append(" AND DATE(c.fecha_cierre) >= ?");
            params.add(fechaInicio.trim());
        }
        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            query.append(" AND DATE(c.fecha_cierre) <= ?");
            params.add(fechaFin.trim());
        }
        if (idUsuario != null && idUsuario > 0) {
            query.append(" AND c.id_usuario_cierre = ?");
            params.add(idUsuario);
        }

        query.append(" ORDER BY c.id_caja_sesion DESC");

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        int idSesion = rs.getInt("id_caja_sesion");
                        map.put("id_caja_sesion", idSesion);
                        map.put("fecha_cierre", rs.getString("fecha_cierre"));
                        map.put("usuario_apertura", rs.getString("usuario_apertura"));
                        map.put("usuario_cierre", rs.getString("usuario_cierre"));
                        map.put("monto_apertura", rs.getDouble("monto_apertura"));
                        map.put("monto_cierre_real", rs.getDouble("monto_cierre_real"));
                        map.put("diferencia", rs.getDouble("diferencia"));
                        map.put("estado", rs.getString("estado"));

                        // Obtener los totales agrupados desde calcularArqueoCaja para exportar completo
                        Map<String, Object> arqueo = calcularArqueoCaja(idSesion);
                        map.put("ingresos_efectivo", arqueo.get("ingresos_efectivo"));
                        map.put("ingresos_transferencia", arqueo.get("ingresos_transferencia"));
                        map.put("ingresos_pos", arqueo.get("ingresos_pos"));
                        
                        // Total General (Efectivo + Transferencia + POS)
                        double totG = (double) arqueo.get("ingresos_efectivo") + 
                                      (double) arqueo.get("ingresos_transferencia") + 
                                      (double) arqueo.get("ingresos_pos");
                        map.put("total_general", totG);

                        lista.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial de cierres: " + e.getMessage());
        }
        return lista;
    }
}
