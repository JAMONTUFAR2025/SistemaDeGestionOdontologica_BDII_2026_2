package application.model.dao;

import application.model.connection.DBConnection;
import application.model.entity.Especialidad;
import application.model.entity.PersonalMedico;

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

    // Insertar nueva especialidad
    public boolean agregarEspecialidad(String nombre) {
        String q = "INSERT INTO Especialidades (nombre_especialidad) VALUES (?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(q)) {
                stmt.setString(1, nombre);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar especialidad: " + e.getMessage());
            return false;
        }
    }

    // Actualizar nombre de especialidad
    public boolean actualizarEspecialidad(int id, String nombre) {
        String q = "UPDATE Especialidades SET nombre_especialidad = ? WHERE id_especialidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(q)) {
                stmt.setString(1, nombre);
                stmt.setInt(2, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar especialidad: " + e.getMessage());
            return false;
        }
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

    // Obtener usuarios que NO tienen un médico vinculado (identidad_medico IS NULL)
    public java.util.List<java.util.Map<String, String>> obtenerUsuariosSinMedico() {
        java.util.List<java.util.Map<String, String>> usuarios = new java.util.ArrayList<>();
        String query = "SELECT id_usuario, correo, rol_sistema FROM Usuarios_Login WHERE identidad_medico IS NULL AND estado = 'Activo'";
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
        String insertPersonal = "INSERT INTO Personal_Medico (identidad, nombre_completo, telefono, id_especialidad, correo) VALUES (?, ?, ?, ?, ?)";
        String updateUsuario = "UPDATE Usuarios_Login SET identidad_medico = ? WHERE id_usuario = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try {
            conn.setAutoCommit(false);

            // 1. Insertar Personal Medico
            try (PreparedStatement stmtPersonal = conn.prepareStatement(insertPersonal)) {
                stmtPersonal.setString(1, pm.getIdentidad());
                stmtPersonal.setString(2, pm.getNombre_completo());
                stmtPersonal.setString(3, pm.getTelefono());
                stmtPersonal.setInt(4, pm.getId_especialidad());
                stmtPersonal.setString(5, pm.getCorreo());

                int filas = stmtPersonal.executeUpdate();
                if (filas == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 2. Vincular el usuario existente con el nuevo médico
            try (PreparedStatement stmtUpdate = conn.prepareStatement(updateUsuario)) {
                stmtUpdate.setString(1, pm.getIdentidad());
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
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null)
                    conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo original conservado por compatibilidad (crea ambos de una vez)
    public boolean registrarPersonalYUsuario(PersonalMedico pm) {
        String insertPersonal = "INSERT INTO Personal_Medico (identidad, nombre_completo, telefono, id_especialidad, correo) VALUES (?, ?, ?, ?, ?)";
        String insertUsuario = "INSERT INTO Usuarios_Login (correo, contrasenia, identidad_medico, rol_sistema, estado) VALUES (?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getInstance().getConnection();

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPersonal = conn.prepareStatement(insertPersonal)) {
                stmtPersonal.setString(1, pm.getIdentidad());
                stmtPersonal.setString(2, pm.getNombre_completo());
                stmtPersonal.setString(3, pm.getTelefono());
                stmtPersonal.setInt(4, pm.getId_especialidad());
                stmtPersonal.setString(5, pm.getCorreo());

                int filasPersonal = stmtPersonal.executeUpdate();
                if (filasPersonal == 0) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement stmtUsuario = conn.prepareStatement(insertUsuario)) {
                stmtUsuario.setString(1, pm.getCorreo());
                stmtUsuario.setString(2, pm.getContrasenia());
                stmtUsuario.setString(3, pm.getIdentidad());
                stmtUsuario.setString(4, pm.getRol_sistema());
                stmtUsuario.setString(5, pm.getEstado());

                int filasUsuario = stmtUsuario.executeUpdate();
                if (filasUsuario == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en transacción: " + e.getMessage());
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null)
                    conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Obtener todos los usuarios activos para la tabla de personal
    public java.util.List<java.util.Map<String, Object>> obtenerUsuarios() {
        java.util.List<java.util.Map<String, Object>> lista = new java.util.ArrayList<>();
        String query = "SELECT id_usuario, correo, rol_sistema, estado, fecha_creacion FROM Usuarios_Login WHERE estado = 'Activo' ORDER BY fecha_creacion DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                    map.put("id_usuario", rs.getInt("id_usuario"));
                    map.put("correo", rs.getString("correo"));
                    map.put("rol_sistema", rs.getString("rol_sistema"));
                    map.put("estado", rs.getString("estado"));
                    map.put("fecha_creacion", rs.getString("fecha_creacion"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return lista;
    }

    // Obtener todo el personal médico activo para la tabla
    public java.util.List<java.util.Map<String, Object>> obtenerPersonalMedico() {
        java.util.List<java.util.Map<String, Object>> lista = new java.util.ArrayList<>();
        String query = "SELECT pm.identidad, pm.nombre_completo, pm.telefono, pm.correo, pm.estado, " +
                "e.nombre_especialidad FROM Personal_Medico pm " +
                "LEFT JOIN Especialidades e ON pm.id_especialidad = e.id_especialidad " +
                "WHERE pm.estado = 'Activo' ORDER BY pm.nombre_completo ASC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                    map.put("identidad", rs.getString("identidad"));
                    map.put("nombreCompleto", rs.getString("nombre_completo"));
                    map.put("telefono", rs.getString("telefono"));
                    map.put("correo", rs.getString("correo"));
                    map.put("estado", rs.getString("estado"));
                    map.put("especialidadNombre", rs.getString("nombre_especialidad"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener personal médico: " + e.getMessage());
        }
        return lista;
    }

    // Actualizar datos de un usuario (rol y opcionalmente contraseña)
    public boolean actualizarUsuario(int idUsuario, String rolSistema, String nuevaContrasenia) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (nuevaContrasenia != null && !nuevaContrasenia.isEmpty()) {
                String hashed = application.util.SecurityUtils.hashPassword(nuevaContrasenia);
                String q = "UPDATE Usuarios_Login SET rol_sistema = ?, contrasenia = ? WHERE id_usuario = ?";
                try (PreparedStatement stmt = conn.prepareStatement(q)) {
                    stmt.setString(1, rolSistema);
                    stmt.setString(2, hashed);
                    stmt.setInt(3, idUsuario);
                    return stmt.executeUpdate() > 0;
                }
            } else {
                String q = "UPDATE Usuarios_Login SET rol_sistema = ? WHERE id_usuario = ?";
                try (PreparedStatement stmt = conn.prepareStatement(q)) {
                    stmt.setString(1, rolSistema);
                    stmt.setInt(2, idUsuario);
                    return stmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // Inactivar usuario (borrado lógico)
    public boolean inactivarUsuario(int idUsuario) {
        String q = "UPDATE Usuarios_Login SET estado = 'Inactivo', fecha_inactivacion = NOW() WHERE id_usuario = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(q)) {
                stmt.setInt(1, idUsuario);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al inactivar usuario: " + e.getMessage());
            return false;
        }
    }

    // Actualizar datos de personal médico
    public boolean actualizarPersonalMedico(String identidad, String nombreCompleto, String telefono,
            int idEspecialidad) {
        String q = "UPDATE Personal_Medico SET nombre_completo = ?, telefono = ?, id_especialidad = ? WHERE identidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(q)) {
                stmt.setString(1, nombreCompleto);
                stmt.setString(2, telefono);
                stmt.setInt(3, idEspecialidad);
                stmt.setString(4, identidad);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar personal médico: " + e.getMessage());
            return false;
        }
    }

    // Inactivar personal médico (borrado lógico)
    public boolean inactivarPersonalMedico(String identidad) {
        String q = "UPDATE Personal_Medico SET estado = 'Inactivo', fecha_inactivacion = NOW() WHERE identidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(q)) {
                stmt.setString(1, identidad);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al inactivar personal médico: " + e.getMessage());
            return false;
        }
    }
}
