package application.model.dao;

import application.model.connection.DBConnection;
import application.model.entity.Paciente;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    // Verifica si un paciente existe por identidad
    public boolean existe(String identidad) {
        String query = "SELECT COUNT(*) FROM Pacientes WHERE identidad = ? AND borrado = FALSE";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identidad);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de paciente: " + e.getMessage());
        }
        return false;
    }

    public String registrar(Paciente p) {
        String query = "INSERT INTO Pacientes " +
                "(identidad, nombre_completo, fecha_nacimiento, genero, estado_civil, ocupacion, " +
                "domicilio, telefono, id_responsable) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) {
                return "ERR|No se pudo establecer conexión con la base de datos.";
            }
            try (PreparedStatement stmt = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                if (p.getIdentidad() != null && !p.getIdentidad().trim().isEmpty()) {
                    stmt.setString(1, p.getIdentidad());
                } else {
                    stmt.setNull(1, java.sql.Types.VARCHAR);
                }
                stmt.setString(2, p.getNombreCompleto());

                if (p.getFechaNacimiento() != null) {
                    stmt.setDate(3, Date.valueOf(p.getFechaNacimiento()));
                } else {
                    stmt.setNull(3, java.sql.Types.DATE);
                }

                stmt.setString(4, p.getGenero());
                stmt.setString(5, p.getEstadoCivil());
                stmt.setString(6, p.getOcupacion());
                stmt.setString(7, p.getDomicilio());
                if (p.getTelefono() != null && !p.getTelefono().trim().isEmpty()) {
                    stmt.setString(8, p.getTelefono());
                } else {
                    stmt.setNull(8, java.sql.Types.VARCHAR);
                }
                if (p.getIdResponsable() != null) {
                    stmt.setInt(9, p.getIdResponsable());
                } else {
                    stmt.setNull(9, java.sql.Types.INTEGER);
                }

                int filasAfectadas = stmt.executeUpdate();
                if (filasAfectadas > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int newId = generatedKeys.getInt(1);
                            return "OK|Paciente registrado exitosamente.|" + newId;
                        }
                    }
                    return "OK|Paciente registrado exitosamente.";
                } else {
                    return "ERR|No se pudo registrar el paciente en la base de datos.";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar paciente: " + e.getMessage());
            e.printStackTrace();
            return "ERR|Error de base de datos: " + e.getMessage();
        }
    }

    public String actualizar(Paciente p) {
        String query = "UPDATE Pacientes SET identidad=?, nombre_completo=?, fecha_nacimiento=?, genero=?, estado_civil=?, " +
                "ocupacion=?, domicilio=?, telefono=?, id_responsable=? " +
                "WHERE id_paciente=?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) {
                return "ERR|No se pudo establecer conexión con la base de datos.";
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                if (p.getIdentidad() != null && !p.getIdentidad().trim().isEmpty()) {
                    stmt.setString(1, p.getIdentidad());
                } else {
                    stmt.setNull(1, java.sql.Types.VARCHAR);
                }
                stmt.setString(2, p.getNombreCompleto());
                if (p.getFechaNacimiento() != null) {
                    stmt.setDate(3, Date.valueOf(p.getFechaNacimiento()));
                } else {
                    stmt.setNull(3, java.sql.Types.DATE);
                }
                stmt.setString(4, p.getGenero());
                stmt.setString(5, p.getEstadoCivil());
                stmt.setString(6, p.getOcupacion());
                stmt.setString(7, p.getDomicilio());
                if (p.getTelefono() != null && !p.getTelefono().trim().isEmpty()) {
                    stmt.setString(8, p.getTelefono());
                } else {
                    stmt.setNull(8, java.sql.Types.VARCHAR);
                }
                if (p.getIdResponsable() != null) {
                    stmt.setInt(9, p.getIdResponsable());
                } else {
                    stmt.setNull(9, java.sql.Types.INTEGER);
                }
                
                stmt.setInt(10, p.getIdPaciente());

                int filasAfectadas = stmt.executeUpdate();
                if (filasAfectadas > 0) {
                    return "OK|Paciente actualizado exitosamente.";
                } else {
                    return "ERR|No se pudo actualizar el paciente en la base de datos.";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar paciente: " + e.getMessage());
            e.printStackTrace();
            return "ERR|Error de base de datos: " + e.getMessage();
        }
    }

    public List<Paciente> obtenerPacientes() {
        List<Paciente> lista = new ArrayList<>();
        String query = "SELECT p.*, r.nombre_completo AS persona_responsable, r.telefono AS telefono_responsable " +
                       "FROM Pacientes p " +
                       "LEFT JOIN Responsables r ON p.id_responsable = r.id_responsable " +
                       "ORDER BY p.nombre_completo ASC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return lista;
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Paciente p = new Paciente();
                    try { p.setIdPaciente(rs.getInt("id_paciente")); } catch (Exception ignored) {}
                    p.setIdentidad(rs.getString("identidad"));
                    p.setNombreCompleto(rs.getString("nombre_completo"));

                    Date fechaNac = rs.getDate("fecha_nacimiento");
                    if (fechaNac != null) {
                        p.setFechaNacimiento(fechaNac.toLocalDate());
                    }

                    p.setGenero(rs.getString("genero"));
                    try { p.setEstadoCivil(rs.getString("estado_civil")); } catch (Exception ignored) {}
                    p.setOcupacion(rs.getString("ocupacion"));
                    p.setDomicilio(rs.getString("domicilio"));
                    p.setTelefono(rs.getString("telefono"));
                    p.setTelefonoResponsable(rs.getString("telefono_responsable"));
                    Object idResponsable = rs.getObject("id_responsable");
                    if (idResponsable != null) {
                        p.setIdResponsable((Integer) idResponsable);
                    }
                    
                    p.setEstado(rs.getBoolean("borrado") ? "Inactivo" : "Activo");

                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pacientes: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // Borrado lógico por id_paciente (o identidad como fallback)
    public boolean eliminarPaciente(String idOrIdentidad) {
        // Intentar primero por id numérico
        try {
            int id = Integer.parseInt(idOrIdentidad.trim());
            String query = "UPDATE Pacientes SET borrado = TRUE, fecha_borrado = CURRENT_TIMESTAMP WHERE id_paciente = ?";
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (NumberFormatException e) {
            // No era un ID numérico, buscar por identidad
        } catch (SQLException e) {
            System.err.println("Error al realizar borrado lógico de paciente por id: " + e.getMessage());
            e.printStackTrace();
        }
        String query = "UPDATE Pacientes SET borrado = TRUE, fecha_borrado = CURRENT_TIMESTAMP WHERE identidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, idOrIdentidad);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar borrado lógico de paciente por identidad: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Reactivar por id_paciente (o identidad como fallback)
    public boolean reactivarPaciente(String idOrIdentidad) {
        try {
            int id = Integer.parseInt(idOrIdentidad.trim());
            String query = "UPDATE Pacientes SET borrado = FALSE, fecha_borrado = NULL WHERE id_paciente = ?";
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (NumberFormatException e) {
            // No era un ID numérico, buscar por identidad
        } catch (SQLException e) {
            System.err.println("Error al reactivar paciente por id: " + e.getMessage());
            e.printStackTrace();
        }
        String query = "UPDATE Pacientes SET borrado = FALSE, fecha_borrado = NULL WHERE identidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, idOrIdentidad);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al reactivar paciente por identidad: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
