package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean autenticarUsuario(String correoONombre, String contrasenia) {
        String query = "SELECT id_usuario_login FROM Usuarios_Login WHERE (correo = ? OR nombre_usuario = ?) AND contrasenia = ? AND borrado = FALSE";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                String hashedPass = application.util.SecurityUtils.hashPassword(contrasenia);
                stmt.setString(1, correoONombre);
                stmt.setString(2, correoONombre);
                stmt.setString(3, hashedPass);

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

    public boolean verificarCorreoExistente(String correoOTelefonoONombre) {
        String query = "SELECT u.id_usuario_login FROM Usuarios_Login u " +
                "LEFT JOIN Personal_Medico p ON u.id_personal_medico = p.id_personal_medico " +
                "WHERE (u.correo = ? OR u.nombre_usuario = ? OR p.telefono = ?) AND u.borrado = FALSE";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, correoOTelefonoONombre);
                stmt.setString(2, correoOTelefonoONombre);
                stmt.setString(3, correoOTelefonoONombre);

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

    public String obtenerCorreoReal(String identificador) {
        String query = "SELECT correo FROM Usuarios_Login " +
                "WHERE (correo = ? OR nombre_usuario = ?) AND borrado = FALSE";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identificador);
                stmt.setString(2, identificador);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("correo");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener correo real: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarContrasenia(String identificador, String nuevaContrasenia) {
        String query = "UPDATE Usuarios_Login u " +
                "LEFT JOIN Personal_Medico p ON u.id_personal_medico = p.id_personal_medico " +
                "SET u.contrasenia = ? " +
                "WHERE (u.correo = ? OR u.nombre_usuario = ? OR p.telefono = ?) AND u.borrado = FALSE";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                String hashedPass = application.util.SecurityUtils.hashPassword(nuevaContrasenia);
                stmt.setString(1, hashedPass);
                stmt.setString(2, identificador);
                stmt.setString(3, identificador);
                stmt.setString(4, identificador);

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
        String query = "SELECT correo FROM Usuarios_Login WHERE borrado = FALSE";

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

    public java.util.List<String> obtenerNombresUsuarioActivos() {
        java.util.List<String> usuarios = new java.util.ArrayList<>();
        String query = "SELECT nombre_usuario FROM Usuarios_Login WHERE borrado = FALSE";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    usuarios.add(rs.getString("nombre_usuario"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombres de usuario activos: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    // =========================================================================
    // MÉTODO: Obtener el rol del usuario para los permisos del sistema
    // =========================================================================
    public String obtenerRolPorCorreo(String identificador) {
        String rol = "";
        String query = "SELECT rol_sistema FROM Usuarios_Login WHERE correo = ? OR nombre_usuario = ?";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, identificador);
                stmt.setString(2, identificador);

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

    public Integer obtenerIdMedicoPorCorreo(String identificador) {
        Integer idMedico = null;
        String query = "SELECT id_personal_medico FROM Usuarios_Login WHERE (correo = ? OR nombre_usuario = ?) AND borrado = FALSE";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return null;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identificador);
                stmt.setString(2, identificador);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id_personal_medico");
                        if (!rs.wasNull()) {
                            idMedico = id;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener id_personal_medico: " + e.getMessage());
            e.printStackTrace();
        }
        return idMedico;
    }

    public Integer obtenerIdLoginPorCorreo(String identificador) {
        Integer idLogin = null;
        String query = "SELECT id_usuario_login FROM Usuarios_Login WHERE (correo = ? OR nombre_usuario = ?) AND borrado = FALSE";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return null;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identificador);
                stmt.setString(2, identificador);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        idLogin = rs.getInt("id_usuario_login");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener id_usuarios_login: " + e.getMessage());
        }
        return idLogin;
    }

    public String obtenerNombreUsuario(String identificador) {
        String nombreUsuario = null;
        String query = "SELECT nombre_usuario FROM Usuarios_Login WHERE (correo = ? OR nombre_usuario = ?) AND borrado = FALSE";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return null;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identificador);
                stmt.setString(2, identificador);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        nombreUsuario = rs.getString("nombre_usuario");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre_usuario: " + e.getMessage());
        }
        return nombreUsuario;
    }

    public String obtenerNombreMedicoPorCorreo(String identificador) {
        String nombre = null;
        String query = "SELECT pm.nombre_completo FROM Usuarios_Login ul " +
                       "JOIN Personal_Medico pm ON ul.id_personal_medico = pm.id_personal_medico " +
                       "WHERE (ul.correo = ? OR ul.nombre_usuario = ?) AND ul.borrado = FALSE AND pm.borrado = FALSE";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) return null;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identificador);
                stmt.setString(2, identificador);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        nombre = rs.getString("nombre_completo");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre_completo de médico: " + e.getMessage());
        }
        return nombre;
    }
}