package application.model.dao;

import application.model.connection.DBConnection;
import application.model.entity.Cita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitaDAO {

    public boolean agendarCita(Cita cita) {
        String query = "INSERT INTO Citas (id_pacientes, id_personal_medico, fecha_hora, motivo_cita, estado) " +
                       "VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cita.getIdPacientes());
                if (cita.getIdPersonalMedico() != null) {
                    stmt.setInt(2, cita.getIdPersonalMedico());
                } else {
                    stmt.setNull(2, java.sql.Types.INTEGER);
                }
                stmt.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
                stmt.setString(4, cita.getMotivoCita());
                stmt.setString(5, cita.getEstado() != null ? cita.getEstado() : "Programada");

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al agendar cita: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, String>> obtenerCitasHoy(Integer idPersonalMedico, String rol) {
        List<Map<String, String>> citas = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT c.id_citas, c.fecha_hora, p.nombre_completo AS paciente, p.telefono, c.motivo_cita " +
            "FROM Citas c " +
            "JOIN Pacientes p ON c.id_pacientes = p.id_pacientes " +
            "WHERE DATE(c.fecha_hora) = CURDATE() " +
            "AND c.estado != 'Cancelada' AND c.estado != 'Ausente' AND c.estado != 'Completada'"
        );

        boolean esMedico = "Medico".equalsIgnoreCase(rol) || "Médico".equalsIgnoreCase(rol);
        if (esMedico && idPersonalMedico != null) {
            queryBuilder.append(" AND c.id_personal_medico = ?");
        }
        
        queryBuilder.append(" ORDER BY c.fecha_hora ASC");

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return citas;
            try (PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
                if (esMedico && idPersonalMedico != null) {
                    stmt.setInt(1, idPersonalMedico);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, String> map = new HashMap<>();
                        map.put("id_citas", String.valueOf(rs.getInt("id_citas")));
                        // Formatear hora (HH:mm a) de forma simple en Java (Timestamp a String)
                        Timestamp ts = rs.getTimestamp("fecha_hora");
                        String hora = new java.text.SimpleDateFormat("hh:mm a").format(ts);
                        map.put("hora", hora);
                        map.put("paciente", rs.getString("paciente"));
                        map.put("telefono", rs.getString("telefono") != null ? rs.getString("telefono") : "");
                        map.put("motivo_cita", rs.getString("motivo_cita") != null ? rs.getString("motivo_cita") : "");
                        citas.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener citas de hoy: " + e.getMessage());
            e.printStackTrace();
        }
        return citas;
    }

    public List<Map<String, String>> obtenerProximasCitas(Integer idPersonalMedico, String rol) {
        List<Map<String, String>> citas = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT c.id_citas, c.fecha_hora, p.nombre_completo AS paciente, p.telefono, c.motivo_cita, c.estado " +
            "FROM Citas c " +
            "JOIN Pacientes p ON c.id_pacientes = p.id_pacientes " +
            "WHERE DATE(c.fecha_hora) > CURDATE() " +
            "AND c.estado != 'Cancelada' AND c.estado != 'Ausente' AND c.estado != 'Completada'"
        );

        boolean esMedico = "Medico".equalsIgnoreCase(rol) || "Médico".equalsIgnoreCase(rol);
        if (esMedico && idPersonalMedico != null) {
            queryBuilder.append(" AND c.id_personal_medico = ?");
        }
        
        queryBuilder.append(" ORDER BY c.fecha_hora ASC");

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return citas;
            try (PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
                if (esMedico && idPersonalMedico != null) {
                    stmt.setInt(1, idPersonalMedico);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, String> map = new HashMap<>();
                        map.put("id_citas", String.valueOf(rs.getInt("id_citas")));
                        Timestamp ts = rs.getTimestamp("fecha_hora");
                        String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").format(ts);
                        String hora = new java.text.SimpleDateFormat("hh:mm a").format(ts);
                        map.put("fecha", fecha);
                        map.put("hora", hora);
                        map.put("paciente", rs.getString("paciente"));
                        map.put("telefono", rs.getString("telefono") != null ? rs.getString("telefono") : "");
                        map.put("motivo_cita", rs.getString("motivo_cita") != null ? rs.getString("motivo_cita") : "");
                        map.put("estado", rs.getString("estado"));
                        citas.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proximas citas: " + e.getMessage());
            e.printStackTrace();
        }
        return citas;
    }

    public Map<String, String> obtenerCitaPorId(int idCita) {
        Map<String, String> map = new HashMap<>();
        String query =
            "SELECT c.id_citas, c.id_pacientes, c.id_personal_medico, c.fecha_hora, " +
            "c.motivo_cita, c.estado, " +
            "p.nombre_completo AS paciente, p.telefono, " +
            "pm.nombre_completo AS medico " +
            "FROM Citas c " +
            "JOIN Pacientes p ON c.id_pacientes = p.id_pacientes " +
            "LEFT JOIN Personal_Medico pm ON c.id_personal_medico = pm.id_personal_medico " +
            "WHERE c.id_citas = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return map;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idCita);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        map.put("id_citas", String.valueOf(rs.getInt("id_citas")));
                        map.put("id_pacientes", String.valueOf(rs.getInt("id_pacientes")));
                        Object idMedico = rs.getObject("id_personal_medico");
                        map.put("id_personal_medico", idMedico != null ? idMedico.toString() : "");
                        Timestamp ts = rs.getTimestamp("fecha_hora");
                        if (ts != null) {
                            map.put("fecha", new java.text.SimpleDateFormat("yyyy-MM-dd").format(ts));
                            map.put("hora", new java.text.SimpleDateFormat("HH:mm").format(ts));
                        }
                        map.put("motivo_cita", rs.getString("motivo_cita") != null ? rs.getString("motivo_cita") : "");
                        map.put("estado", rs.getString("estado") != null ? rs.getString("estado") : "");
                        map.put("paciente", rs.getString("paciente") != null ? rs.getString("paciente") : "");
                        map.put("telefono", rs.getString("telefono") != null ? rs.getString("telefono") : "");
                        map.put("medico", rs.getString("medico") != null ? rs.getString("medico") : "");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cita por id: " + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    public boolean actualizarCita(Cita cita) {
        String query = "UPDATE Citas SET id_pacientes = ?, id_personal_medico = ?, fecha_hora = ?, motivo_cita = ?, estado = ? WHERE id_citas = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cita.getIdPacientes());
                if (cita.getIdPersonalMedico() != null) {
                    stmt.setInt(2, cita.getIdPersonalMedico());
                } else {
                    stmt.setNull(2, java.sql.Types.INTEGER);
                }
                stmt.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
                stmt.setString(4, cita.getMotivoCita());
                stmt.setString(5, cita.getEstado() != null ? cita.getEstado() : "Programada");
                stmt.setInt(6, cita.getIdCitas());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar cita: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
