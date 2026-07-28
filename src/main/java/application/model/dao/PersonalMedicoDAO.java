package application.model.dao;

import application.model.connection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PersonalMedicoDAO {

    public List<Especialidad> obtenerEspecialidades() {
        List<Especialidad> lista = new ArrayList<>();
        String query = "SELECT id_especialidad, nombre_especialidad FROM Especialidades";
        
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    lista.add(new Especialidad(rs.getInt("id_especialidad"), rs.getString("nombre_especialidad")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener especialidades: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // Registrar SOLO un usuario de login (sin vincularlo a ningún médico)
    public boolean registrarUsuarioSolo(String correo, String contrasenia, String rolSistema, String estado) {
        String query = "INSERT INTO Usuarios_Login (correo, contrasenia, rol_sistema, estado) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                String hashedPass = application.util.SecurityUtils.hashPassword(contrasenia);
                stmt.setString(1, correo);
                stmt.setString(2, hashedPass);
                stmt.setString(3, rolSistema);
                stmt.setString(4, estado);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Obtener usuarios que NO tienen un médico vinculado (id_medico IS NULL)
    public java.util.List<java.util.Map<String, String>> obtenerUsuariosSinMedico() {
        java.util.List<java.util.Map<String, String>> usuarios = new java.util.ArrayList<>();
        String query = "SELECT id_usuario, correo, rol_sistema FROM Usuarios_Login WHERE id_medico IS NULL AND estado = 'Activo'";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, String> map = new java.util.HashMap<>();
                    map.put("id_usuario", String.valueOf(rs.getInt("id_usuario")));
                    map.put("correo", rs.getString("correo"));
                    map.put("rol_sistema", rs.getString("rol_sistema"));
                    usuarios.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios sin médico: " + e.getMessage());
        }
        return usuarios;
    }

    // Registrar Personal Médico y VINCULAR con un usuario existente
    public boolean registrarPersonalYVincular(PersonalMedico pm, int idUsuario) {
        String insertPersonal = "INSERT INTO Personal_Medico (nombre_completo, identidad, telefono, rol, id_especialidad, correo) VALUES (?, ?, ?, ?, ?, ?)";
        String updateUsuario = "UPDATE Usuarios_Login SET id_medico = ? WHERE id_usuario = ?";
        
        Connection conn = DBConnection.getInstance().getConnection();
        
        try {
            conn.setAutoCommit(false);
            
            int idMedicoGenerado = -1;
            
            // 1. Insertar Personal Medico
            try (PreparedStatement stmtPersonal = conn.prepareStatement(insertPersonal, Statement.RETURN_GENERATED_KEYS)) {
                stmtPersonal.setString(1, pm.getNombre_completo());
                stmtPersonal.setString(2, pm.getIdentidad());
                stmtPersonal.setString(3, pm.getTelefono());
                stmtPersonal.setString(4, pm.getRol());
                stmtPersonal.setInt(5, pm.getId_especialidad());
                stmtPersonal.setString(6, pm.getCorreo());
                
                int filas = stmtPersonal.executeUpdate();
                if (filas == 0) {
                    conn.rollback();
                    return false;
                }
                
                try (ResultSet generatedKeys = stmtPersonal.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idMedicoGenerado = generatedKeys.getInt(1);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            // 2. Vincular el usuario existente con el nuevo médico
            try (PreparedStatement stmtUpdate = conn.prepareStatement(updateUsuario)) {
                stmtUpdate.setInt(1, idMedicoGenerado);
                stmtUpdate.setInt(2, idUsuario);
                
                int filas = stmtUpdate.executeUpdate();
                if (filas == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error en la transacción al registrar personal: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo original conservado por compatibilidad (crea ambos de una vez)
    public boolean registrarPersonalYUsuario(PersonalMedico pm) {
        String insertPersonal = "INSERT INTO Personal_Medico (nombre_completo, identidad, telefono, rol, id_especialidad, correo) VALUES (?, ?, ?, ?, ?, ?)";
        String insertUsuario = "INSERT INTO Usuarios_Login (correo, contrasenia, id_medico, rol_sistema, estado) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = DBConnection.getInstance().getConnection();
        
        try {
            conn.setAutoCommit(false);
            int idMedicoGenerado = -1;
            
            try (PreparedStatement stmtPersonal = conn.prepareStatement(insertPersonal, Statement.RETURN_GENERATED_KEYS)) {
                stmtPersonal.setString(1, pm.getNombre_completo());
                stmtPersonal.setString(2, pm.getIdentidad());
                stmtPersonal.setString(3, pm.getTelefono());
                stmtPersonal.setString(4, pm.getRol());
                stmtPersonal.setInt(5, pm.getId_especialidad());
                stmtPersonal.setString(6, pm.getCorreo());
                
                int filasPersonal = stmtPersonal.executeUpdate();
                if (filasPersonal == 0) { conn.rollback(); return false; }
                
                try (ResultSet generatedKeys = stmtPersonal.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idMedicoGenerado = generatedKeys.getInt(1);
                    } else { conn.rollback(); return false; }
                }
            }
            
            try (PreparedStatement stmtUsuario = conn.prepareStatement(insertUsuario)) {
                stmtUsuario.setString(1, pm.getCorreo());
                stmtUsuario.setString(2, pm.getContrasenia());
                stmtUsuario.setInt(3, idMedicoGenerado);
                stmtUsuario.setString(4, pm.getRol_sistema());
                stmtUsuario.setString(5, pm.getEstado());
                
                int filasUsuario = stmtUsuario.executeUpdate();
                if (filasUsuario == 0) { conn.rollback(); return false; }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en transacción: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
