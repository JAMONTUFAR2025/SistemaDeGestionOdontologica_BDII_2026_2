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
        String query = "SELECT COUNT(*) FROM Pacientes WHERE identidad = ?";
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
        // SchemaActual: columnas id_pacientes (PK auto), identidad (UNIQUE), borrado ENUM('Si','No') DEFAULT 'No'
        String query = "INSERT INTO Pacientes " +
                "(identidad, nombre_completo, fecha_nacimiento, genero, estado_civil, ocupacion, " +
                "domicilio, telefono, persona_responsable, telefono_responsable) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) {
                return "ERR|No se pudo establecer conexión con la base de datos.";
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, p.getIdentidad());
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
                stmt.setString(8, p.getTelefono());
                stmt.setString(9, p.getPersonaResponsable());
                stmt.setString(10, p.getTelefonoResponsable());

                int filasAfectadas = stmt.executeUpdate();
                if (filasAfectadas > 0) {
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
        // SchemaActual: no toca borrado al actualizar datos personales
        String query = "UPDATE Pacientes SET identidad=?, nombre_completo=?, fecha_nacimiento=?, genero=?, estado_civil=?, " +
                "ocupacion=?, domicilio=?, telefono=?, persona_responsable=?, telefono_responsable=? " +
                "WHERE identidad=?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) {
                return "ERR|No se pudo establecer conexión con la base de datos.";
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, p.getIdentidad());
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
                stmt.setString(8, p.getTelefono());
                stmt.setString(9, p.getPersonaResponsable());
                stmt.setString(10, p.getTelefonoResponsable());
                
                String idOriginal = (p.getIdentidadOriginal() != null && !p.getIdentidadOriginal().isEmpty()) 
                                    ? p.getIdentidadOriginal() : p.getIdentidad();
                stmt.setString(11, idOriginal);

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
        // SchemaActual: PK = id_pacientes, borrado ENUM('Si','No') DEFAULT 'No'
        String query = "SELECT * FROM Pacientes ORDER BY nombre_completo ASC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return lista;
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Paciente p = new Paciente();
                    try { p.setIdPaciente(rs.getInt("id_pacientes")); } catch (Exception ignored) {}
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
                    p.setPersonaResponsable(rs.getString("persona_responsable"));
                    p.setTelefonoResponsable(rs.getString("telefono_responsable"));
                    p.setEstado(rs.getString("borrado").equals("No") ? "Activo" : "Inactivo");

                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pacientes: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // Borrado lógico de un paciente — SchemaActual usa borrado='Si', fecha_borrado
    public boolean eliminarPaciente(String identidad) {
        String query = "UPDATE Pacientes SET borrado = 'Si', fecha_borrado = CURRENT_TIMESTAMP WHERE identidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identidad);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar borrado lógico de paciente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Reactivar un paciente inactivo
    public boolean reactivarPaciente(String identidad) {
        String query = "UPDATE Pacientes SET borrado = 'No', fecha_borrado = NULL WHERE identidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identidad);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al reactivar paciente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
