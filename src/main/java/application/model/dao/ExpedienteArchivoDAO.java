package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para la tabla Expediente_Archivos.
 * Gestiona los archivos adjuntos (radiografías, fotografías, etc.) del expediente de un paciente.
 */
public class ExpedienteArchivoDAO {

    /** Retorna todos los archivos activos de un paciente. */
    public List<Map<String, Object>> obtenerPorPaciente(int idPaciente) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String query = "SELECT id_expediente_archivo, tipo_archivo, nombre_archivo, " +
                       "ruta_archivo, observaciones, fecha_subida, borrado " +
                       "FROM Expediente_Archivos " +
                       "WHERE id_paciente = ? " +
                       "ORDER BY fecha_subida DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPaciente);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id_expediente_archivos", rs.getInt("id_expediente_archivo")); // keep compat
                        map.put("tipo_archivo",  rs.getString("tipo_archivo"));
                        map.put("nombre_archivo", rs.getString("nombre_archivo"));
                        map.put("ruta_archivo",  rs.getString("ruta_archivo"));
                        map.put("observaciones", rs.getString("observaciones"));
                        map.put("fecha_subida",  rs.getString("fecha_subida"));
                        map.put("estado", rs.getBoolean("borrado") ? "Inactivo" : "Activo");
                        lista.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener archivos del expediente: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Registra un nuevo archivo en el expediente del paciente.
     * @param tipoArchivo uno de: 'Radiografia', 'Fotografia', 'Laboratorio', 'Otro'
     */
    public boolean registrar(int idPaciente, String tipoArchivo, String nombreArchivo,
                             String rutaArchivo, String observaciones) {
        String query = "INSERT INTO Expediente_Archivos " +
                       "(id_paciente, tipo_archivo, nombre_archivo, ruta_archivo, observaciones) " +
                       "VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idPaciente);
                stmt.setString(2, tipoArchivo);
                stmt.setString(3, nombreArchivo);
                stmt.setString(4, rutaArchivo);
                stmt.setString(5, observaciones != null ? observaciones.trim() : null);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar archivo del expediente: " + e.getMessage());
            return false;
        }
    }

    /** Borrado lógico de un archivo del expediente. */
    public boolean eliminar(int idExpedienteArchivo) {
        String query = "UPDATE Expediente_Archivos SET borrado = TRUE, fecha_borrado = NOW() " +
                       "WHERE id_expediente_archivo = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idExpedienteArchivo);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar archivo del expediente: " + e.getMessage());
            return false;
        }
    }

    /** Reactivar un archivo del expediente. */
    public boolean reactivarArchivo(int idExpedienteArchivo) {
        String query = "UPDATE Expediente_Archivos SET borrado = FALSE, fecha_borrado = NULL " +
                       "WHERE id_expediente_archivo = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idExpedienteArchivo);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al reactivar archivo del expediente: " + e.getMessage());
            return false;
        }
    }
}
