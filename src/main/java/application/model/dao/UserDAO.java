package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean autenticarUsuario(String correo, String contrasenia) {
        String query = "SELECT id_usuario FROM Usuarios_Login WHERE correo = ? AND contrasenia = ? AND estado = 'Activo'";
        
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, correo);
                stmt.setString(2, contrasenia);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next(); // Si hay un resultado, el login es exitoso
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean verificarCorreoExistente(String correoOTelefono) {
        // Buscamos si existe el correo en Usuarios_Login O si coincide el telefono del medico asociado
        String query = "SELECT u.id_usuario FROM Usuarios_Login u " +
                       "LEFT JOIN Personal_Medico p ON u.id_medico = p.id_medico " +
                       "WHERE (u.correo = ? OR p.telefono = ?) AND u.estado = 'Activo'";
        
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, correoOTelefono);
                stmt.setString(2, correoOTelefono);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next(); // Retorna true si encontro a alguien
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar correo/telefono: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarContrasenia(String correoOTelefono, String nuevaContrasenia) {
        // Actualizar la contraseña del usuario donde coincida su correo o su telefono
        String query = "UPDATE Usuarios_Login u " +
                       "LEFT JOIN Personal_Medico p ON u.id_medico = p.id_medico " +
                       "SET u.contrasenia = ? " +
                       "WHERE (u.correo = ? OR p.telefono = ?) AND u.estado = 'Activo'";
                       
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, nuevaContrasenia);
                stmt.setString(2, correoOTelefono);
                stmt.setString(3, correoOTelefono);
                
                int filas = stmt.executeUpdate();
                return filas > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar la contraseña: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
