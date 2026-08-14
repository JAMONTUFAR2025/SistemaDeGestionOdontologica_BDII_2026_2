package application.model.dao;

import application.model.connection.DBConnection;
import application.model.entity.PersonalMedico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonalMedicoDAO {

    // ==========================================
    // USUARIOS DE LOGIN
    // ==========================================

    // Registrar SOLO un usuario de login (sin vincularlo a ningún médico)
    public boolean registrarUsuarioSolo(String nombreUsuario, String correo, String contrasenia, String rolSistema) {
        String query = "INSERT INTO Usuarios_Login (nombre_usuario, correo, contrasenia, rol_sistema) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                String hashedPass = application.util.SecurityUtils.hashPassword(contrasenia);
                stmt.setString(1, nombreUsuario);
                stmt.setString(2, correo);
                stmt.setString(3, hashedPass);
                stmt.setString(4, rolSistema);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Obtener usuarios que NO tienen un médico vinculado (id_personal_medico IS NULL) y cuyo rol es 'Medico'
    public java.util.List<java.util.Map<String, String>> obtenerUsuariosSinMedico() {
        return obtenerUsuariosParaMedico(0);
    }

    // Obtener usuarios con rol 'Medico' sin médico asignado o asignados al médico específico
    public java.util.List<java.util.Map<String, String>> obtenerUsuariosParaMedico(int idPersonalMedico) {
        java.util.List<java.util.Map<String, String>> usuarios = new java.util.ArrayList<>();
        String query;
        if (idPersonalMedico > 0) {
            query = "SELECT id_usuario_login, nombre_usuario, correo, rol_sistema FROM Usuarios_Login " +
                    "WHERE (id_personal_medico IS NULL OR id_personal_medico = ?) AND borrado = FALSE AND rol_sistema IN ('Medico', 'Administrador') " +
                    "ORDER BY id_usuario_login DESC";
        } else {
            query = "SELECT id_usuario_login, nombre_usuario, correo, rol_sistema FROM Usuarios_Login " +
                    "WHERE id_personal_medico IS NULL AND borrado = FALSE AND rol_sistema IN ('Medico', 'Administrador') " +
                    "ORDER BY id_usuario_login DESC";
        }

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                if (idPersonalMedico > 0) {
                    stmt.setInt(1, idPersonalMedico);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        java.util.Map<String, String> map = new java.util.HashMap<>();
                        map.put("id_usuario", String.valueOf(rs.getInt("id_usuario_login")));
                        map.put("nombre_usuario", rs.getString("nombre_usuario"));
                        map.put("correo", rs.getString("correo"));
                        map.put("rol_sistema", rs.getString("rol_sistema"));
                        usuarios.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios para médico: " + e.getMessage());
        }
        return usuarios;
    }

    // Registrar Personal Médico y VINCULAR con un usuario existente
    public boolean registrarPersonalYVincular(PersonalMedico pm, int idUsuario) {
        String insertPersonal = "INSERT INTO Personal_Medico (identidad, nombre_completo, telefono, id_especialidad, correo) "
                + "VALUES (?, ?, ?, ?, ?)";
        String getIdPersonal = "SELECT id_personal_medico FROM Personal_Medico WHERE identidad = ?";
        String updateUsuario = "UPDATE Usuarios_Login SET id_personal_medico = ? WHERE id_usuario_login = ?";

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

            // 2. Obtener el id_personal_medico recién generado
            int idPersonalMedico = -1;
            try (PreparedStatement stmtGet = conn.prepareStatement(getIdPersonal)) {
                stmtGet.setString(1, pm.getIdentidad());
                try (ResultSet rs = stmtGet.executeQuery()) {
                    if (rs.next()) {
                        idPersonalMedico = rs.getInt("id_personal_medico");
                    }
                }
            }

            // 3. Vincular el usuario existente con el nuevo médico
            if (idPersonalMedico > 0) {
                try (PreparedStatement stmtUpdate = conn.prepareStatement(updateUsuario)) {
                    stmtUpdate.setInt(1, idPersonalMedico);
                    stmtUpdate.setInt(2, idUsuario);
                    stmtUpdate.executeUpdate();
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

    // Método que crea personal médico y usuario de una vez
    public boolean registrarPersonalYUsuario(PersonalMedico pm) {
        String insertPersonal = "INSERT INTO Personal_Medico (identidad, nombre_completo, telefono, id_especialidad, correo) "
                + "VALUES (?, ?, ?, ?, ?)";
        String getIdPersonal = "SELECT id_personal_medico FROM Personal_Medico WHERE identidad = ?";
        String insertUsuario = "INSERT INTO Usuarios_Login (nombre_usuario, correo, contrasenia, id_personal_medico, rol_sistema) " +
                "VALUES (?, ?, ?, ?, ?)";

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

            // Obtener el ID generado
            int idPersonalMedico = -1;
            try (PreparedStatement stmtGet = conn.prepareStatement(getIdPersonal)) {
                stmtGet.setString(1, pm.getIdentidad());
                try (ResultSet rs = stmtGet.executeQuery()) {
                    if (rs.next()) {
                        idPersonalMedico = rs.getInt("id_personal_medico");
                    }
                }
            }

            try (PreparedStatement stmtUsuario = conn.prepareStatement(insertUsuario)) {
                stmtUsuario.setString(1, pm.getNombreUsuario());
                stmtUsuario.setString(2, pm.getCorreo());
                stmtUsuario.setString(3, pm.getContrasenia()); // already hashed? Make sure in controller
                stmtUsuario.setInt(4, idPersonalMedico);
                stmtUsuario.setString(5, pm.getRol_sistema());

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

    // ==========================================
    // CONSULTAS DE PERSONAL Y USUARIOS
    // ==========================================

    // Obtener todos los usuarios (activos e inactivos) para la tabla de personal
    public java.util.List<java.util.Map<String, Object>> obtenerUsuarios() {
        java.util.List<java.util.Map<String, Object>> lista = new java.util.ArrayList<>();
        String query = "SELECT id_usuario_login, nombre_usuario, correo, rol_sistema, borrado, fecha_creacion " +
                "FROM Usuarios_Login ORDER BY fecha_creacion DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                    map.put("id_usuario", rs.getInt("id_usuario_login"));
                    map.put("nombre_usuario", rs.getString("nombre_usuario"));
                    map.put("correo", rs.getString("correo"));
                    map.put("rol_sistema", rs.getString("rol_sistema"));
                    map.put("estado", rs.getBoolean("borrado") ? "Inactivo" : "Activo");
                    map.put("fecha_creacion", rs.getString("fecha_creacion"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return lista;
    }

    // Obtener todo el personal médico (activo e inactivo) para la tabla
    public java.util.List<java.util.Map<String, Object>> obtenerPersonalMedico() {
        java.util.List<java.util.Map<String, Object>> lista = new java.util.ArrayList<>();
        String query = "SELECT pm.id_personal_medico, pm.identidad, pm.nombre_completo, pm.telefono, pm.correo, pm.borrado, "
                + "e.id_especialidad, e.nombre_especialidad, u.id_usuario_login, u.nombre_usuario "
                + "FROM Personal_Medico pm "
                + "LEFT JOIN Especialidades e ON pm.id_especialidad = e.id_especialidad "
                + "LEFT JOIN Usuarios_Login u ON u.id_personal_medico = pm.id_personal_medico AND u.borrado = FALSE "
                + "ORDER BY pm.id_personal_medico DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                    map.put("idPersonalMedico", rs.getInt("id_personal_medico"));
                    map.put("identidad", rs.getString("identidad"));
                    map.put("nombreCompleto", rs.getString("nombre_completo"));
                    map.put("telefono", rs.getString("telefono"));
                    map.put("correo", rs.getString("correo"));
                    map.put("estado", rs.getBoolean("borrado") ? "Inactivo" : "Activo");
                    map.put("idEspecialidad", rs.getInt("id_especialidad"));
                    map.put("especialidadNombre", rs.getString("nombre_especialidad"));
                    map.put("idUsuario", rs.getObject("id_usuario_login") != null ? rs.getInt("id_usuario_login") : null);
                    map.put("nombreUsuario", rs.getString("nombre_usuario"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener personal médico: " + e.getMessage());
        }
        return lista;
    }

    // ==========================================
    // ACTUALIZAR / INACTIVAR
    // ==========================================

    // Actualizar datos de un usuario (rol y opcionalmente contraseña)
    public boolean actualizarUsuario(int idUsuario, String nombreUsuario, String correo, String rolSistema, String nuevaContrasenia) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (nuevaContrasenia != null && !nuevaContrasenia.isEmpty()) {
                String hashed = application.util.SecurityUtils.hashPassword(nuevaContrasenia);
                String q = "UPDATE Usuarios_Login SET nombre_usuario = ?, correo = ?, rol_sistema = ?, contrasenia = ? WHERE id_usuario_login = ?";
                try (PreparedStatement stmt = conn.prepareStatement(q)) {
                    stmt.setString(1, nombreUsuario);
                    stmt.setString(2, correo);
                    stmt.setString(3, rolSistema);
                    stmt.setString(4, hashed);
                    stmt.setInt(5, idUsuario);
                    return stmt.executeUpdate() > 0;
                }
            } else {
                String q = "UPDATE Usuarios_Login SET nombre_usuario = ?, correo = ?, rol_sistema = ? WHERE id_usuario_login = ?";
                try (PreparedStatement stmt = conn.prepareStatement(q)) {
                    stmt.setString(1, nombreUsuario);
                    stmt.setString(2, correo);
                    stmt.setString(3, rolSistema);
                    stmt.setInt(4, idUsuario);
                    return stmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // Inactivar usuario
    public boolean inactivarUsuario(int idUsuario) {
        String q = "UPDATE Usuarios_Login SET borrado = TRUE, fecha_borrado = NOW() WHERE id_usuario_login = ?";
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

    // Eliminar lógicamente un médico
    public boolean eliminarMedico(int idMedico) {
        String query = "UPDATE Personal_Medico SET borrado = TRUE, fecha_borrado = NOW() WHERE id_personal_medico = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idMedico);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar médico: " + e.getMessage());
            return false;
        }
    }

    // Reactivar un usuario inactivo
    public boolean reactivarUsuario(int idUsuario) {
        String query = "UPDATE Usuarios_Login SET borrado = FALSE, fecha_borrado = NULL WHERE id_usuario_login = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idUsuario);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al reactivar usuario: " + e.getMessage());
            return false;
        }
    }


    // Actualizar datos de personal médico y vincular nuevo usuario
    public boolean actualizarPersonalMedico(String identidadOriginal, String nuevaIdentidad, String nombreCompleto, String telefono,
            int idEspecialidad, Integer nuevoIdUsuario, String nuevoCorreo) {
        String getIdPersonal = "SELECT id_personal_medico FROM Personal_Medico WHERE identidad = ?";
        String updatePersonal = "UPDATE Personal_Medico SET identidad = ?, nombre_completo = ?, telefono = ?, id_especialidad = ?" 
                + (nuevoCorreo != null && !nuevoCorreo.isEmpty() ? ", correo = ?" : "") 
                + " WHERE id_personal_medico = ?";
        String desvincularAntiguos = "UPDATE Usuarios_Login SET id_personal_medico = NULL WHERE id_personal_medico = ? AND id_usuario_login != ?";
        String vincularNuevo = "UPDATE Usuarios_Login SET id_personal_medico = ? WHERE id_usuario_login = ?";

        Connection conn = DBConnection.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Obtener id_personal_medico usando la identidad original
            int idPersonalMedico = -1;
            try (PreparedStatement stmtGet = conn.prepareStatement(getIdPersonal)) {
                stmtGet.setString(1, (identidadOriginal != null && !identidadOriginal.isEmpty()) ? identidadOriginal : nuevaIdentidad);
                try (ResultSet rs = stmtGet.executeQuery()) {
                    if (rs.next()) {
                        idPersonalMedico = rs.getInt("id_personal_medico");
                    }
                }
            }

            if (idPersonalMedico <= 0) {
                conn.rollback();
                return false;
            }

            // 2. Actualizar Personal_Medico con nuevaIdentidad
            try (PreparedStatement stmtP = conn.prepareStatement(updatePersonal)) {
                stmtP.setString(1, nuevaIdentidad);
                stmtP.setString(2, nombreCompleto);
                stmtP.setString(3, telefono);
                stmtP.setInt(4, idEspecialidad);
                if (nuevoCorreo != null && !nuevoCorreo.isEmpty()) {
                    stmtP.setString(5, nuevoCorreo);
                    stmtP.setInt(6, idPersonalMedico);
                } else {
                    stmtP.setInt(5, idPersonalMedico);
                }
                stmtP.executeUpdate();
            }

            // 3. Si se especificó un nuevo usuario, reasignar vinculación
            if (nuevoIdUsuario != null && nuevoIdUsuario > 0) {
                // Desvincular cualquier otro usuario que estuviera ligado a este médico
                try (PreparedStatement stmtDesv = conn.prepareStatement(desvincularAntiguos)) {
                    stmtDesv.setInt(1, idPersonalMedico);
                    stmtDesv.setInt(2, nuevoIdUsuario);
                    stmtDesv.executeUpdate();
                }
                // Vincular el nuevo usuario seleccionado
                try (PreparedStatement stmtVinc = conn.prepareStatement(vincularNuevo)) {
                    stmtVinc.setInt(1, idPersonalMedico);
                    stmtVinc.setInt(2, nuevoIdUsuario);
                    stmtVinc.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en transacción al actualizar médico: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Inactivar personal médico
    public boolean inactivarPersonalMedico(String identidad) {
        String q = "UPDATE Personal_Medico SET borrado = TRUE, fecha_borrado = NOW() WHERE identidad = ?";
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

    // Reactivar personal médico
    public boolean reactivarPersonalMedico(String identidad) {
        String q = "UPDATE Personal_Medico SET borrado = FALSE, fecha_borrado = NULL WHERE identidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(q)) {
                stmt.setString(1, identidad);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al reactivar personal médico: " + e.getMessage());
            return false;
        }
    }
}
