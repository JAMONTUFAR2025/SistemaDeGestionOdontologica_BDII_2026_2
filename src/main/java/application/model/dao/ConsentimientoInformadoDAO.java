package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para la tabla Consentimientos_Informados.
 * Gestiona los consentimientos informados vinculados a evoluciones clínicas.
 */
public class ConsentimientoInformadoDAO {

    /** Retorna todos los consentimientos vinculados a una evolución clínica. */
    public List<Map<String, Object>> obtenerPorEvolucion(int idEvolucionClinica) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT id_consentimientos_informados, tipo_procedimiento, " +
                       "representante_legal, identidad_representante, fecha_firma " +
                       "FROM Consentimientos_Informados " +
                       "WHERE id_evolucion_clinica = ? ORDER BY fecha_firma DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idEvolucionClinica);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_consentimientos_informados", rs.getInt("id_consentimientos_informados"));
                        map.put("tipo_procedimiento",     rs.getString("tipo_procedimiento"));
                        map.put("representante_legal",    rs.getString("representante_legal"));
                        map.put("identidad_representante", rs.getString("identidad_representante"));
                        map.put("fecha_firma",            rs.getString("fecha_firma"));
                        lista.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener consentimientos informados: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Registra un nuevo consentimiento informado.
     * @param tipoProcedimiento uno de: 'Cirugia Bucal', 'Endodoncia', 'Otro'
     */
    public boolean registrar(int idEvolucionClinica, String tipoProcedimiento,
                             String representanteLegal, String identidadRepresentante,
                             String fechaFirma) {
        String query = "INSERT INTO Consentimientos_Informados " +
                       "(id_evolucion_clinica, tipo_procedimiento, representante_legal, " +
                       "identidad_representante, fecha_firma) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idEvolucionClinica);
                stmt.setString(2, tipoProcedimiento);
                stmt.setString(3, representanteLegal != null && !representanteLegal.trim().isEmpty()
                        ? representanteLegal.trim() : null);
                stmt.setString(4, identidadRepresentante != null && !identidadRepresentante.trim().isEmpty()
                        ? identidadRepresentante.trim() : null);
                stmt.setString(5, fechaFirma);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar consentimiento informado: " + e.getMessage());
            return false;
        }
    }

    /** Elimina físicamente un consentimiento informado. */
    public boolean eliminar(int idConsentimiento) {
        String query = "DELETE FROM Consentimientos_Informados WHERE id_consentimientos_informados = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idConsentimiento);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar consentimiento informado: " + e.getMessage());
            return false;
        }
    }
}
