package application.model.dao;

import application.model.connection.DBConnection;
import application.model.entity.Paciente;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PacienteDAO {

    public boolean registrar(Paciente p) {
        String query = "INSERT INTO Pacientes (identidad, nombre_completo, fecha_nacimiento, genero, estado_civil, ocupacion, domicilio, telefono, persona_responsable, telefono_responsable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
            
                stmt.setString(1, p.getIdentidad());
                stmt.setString(2, p.getNombreCompleto());
                
                // Si la fecha no es nula, la convertimos a java.sql.Date
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
                return filasAfectadas > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar paciente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Borrado lógico de un paciente
    public boolean eliminarPaciente(String identidad) {
        String query = "UPDATE Pacientes SET estado = 'Inactivo', fecha_inactivacion = CURRENT_TIMESTAMP WHERE identidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
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
}
