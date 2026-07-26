package application.model.dao;

import application.model.connection.DBConnection;
import application.model.entity.Paciente;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PacienteDAO {

    public boolean registrar(Paciente p) {
        String query = "INSERT INTO Pacientes (identidad, nombre_completo, fecha_nacimiento, genero, ocupacion, domicilio, telefono, persona_responsable, telefono_responsable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
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
            stmt.setString(5, p.getOcupacion());
            stmt.setString(6, p.getDomicilio());
            stmt.setString(7, p.getTelefono());
            stmt.setString(8, p.getPersonaResponsable());
            stmt.setString(9, p.getTelefonoResponsable());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar paciente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
