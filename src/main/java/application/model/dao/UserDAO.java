package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean autenticarUsuario(String correo, String contrasenia) {
        // SchemaActual: borrado ENUM('Si','No'), id_usuarios_login
        String query = "SELECT id_usuarios_login FROM Usuarios_Login WHERE correo = ? AND contrasenia = ? AND borrado = 'No'";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                String hashedPass = application.util.SecurityUtils.hashPassword(contrasenia);
                stmt.setString(1, correo);
                stmt.setString(2, hashedPass);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean verificarCorreoExistente(String correoOTelefono) {
        // SchemaActual: Usuarios_Login.id_personal_medico (INT FK), borrado='No'
        String query = "SELECT u.id_usuarios_login FROM Usuarios_Login u " +
                "LEFT JOIN Personal_Medico p ON u.id_personal_medico = p.id_personal_medico " +
                "WHERE (u.correo = ? OR p.telefono = ?) AND u.borrado = 'No'";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, correoOTelefono);
                stmt.setString(2, correoOTelefono);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar correo/telefono: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarContrasenia(String correoOTelefono, String nuevaContrasenia) {
        // SchemaActual: JOIN por id_personal_medico, borrado='No'
        String query = "UPDATE Usuarios_Login u " +
                "LEFT JOIN Personal_Medico p ON u.id_personal_medico = p.id_personal_medico " +
                "SET u.contrasenia = ? " +
                "WHERE (u.correo = ? OR p.telefono = ?) AND u.borrado = 'No'";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                String hashedPass = application.util.SecurityUtils.hashPassword(nuevaContrasenia);
                stmt.setString(1, hashedPass);
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

    public java.util.List<String> obtenerCorreosActivos() {
        java.util.List<String> correos = new java.util.ArrayList<>();
        // SchemaActual: borrado='No'
        String query = "SELECT correo FROM Usuarios_Login WHERE borrado = 'No'";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    correos.add(rs.getString("correo"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener correos activos: " + e.getMessage());
            e.printStackTrace();
        }

        return correos;
    }

    // =========================================================================
    // MÉTODO: Obtener el rol del usuario para los permisos del sistema
    // =========================================================================
    public String obtenerRolPorCorreo(String correo) {
        String rol = "";
        String query = "SELECT rol_sistema FROM Usuarios_Login WHERE correo = ?";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, correo);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        rol = rs.getString("rol_sistema");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener rol: " + e.getMessage());
            e.printStackTrace();
        }
        return rol;
    }
}