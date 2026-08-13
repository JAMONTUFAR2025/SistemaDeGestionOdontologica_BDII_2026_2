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
    public boolean registrarUsuarioSolo(String correo, String contrasenia, String rolSistema, String estadoIgnorado) {
        // SchemaActual: no tiene columna "estado" — usa borrado ENUM('Si','No') DEFAULT
        // 'No'
        String query = "INSERT INTO Usuarios_Login (correo, contrasenia, rol_sistema) VALUES (?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                String hashedPass = application.util.SecurityUtils.hashPassword(contrasenia);
                stmt.setString(1, correo);
                stmt.setString(2, hashedPass);
                stmt.setString(3, rolSistema);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Obtener usuarios que NO tienen un médico vinculado (id_personal_medico IS
    // NULL)
    public java.util.List<java.util.Map<String, String>> obtenerUsuariosSinMedico() {
        java.util.List<java.util.Map<String, String>> usuarios = new java.util.ArrayList<>();
        // SchemaActual: FK = id_personal_medico (INT), borrado='No'
        String query = "SELECT id_usuarios_login, correo, rol_sistema FROM Usuarios_Login " +
                "WHERE id_personal_medico IS NULL AND borrado = 'No' ORDER BY id_usuarios_login DESC";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, String> map = new java.util.HashMap<>();
                    // Se mantiene clave "id_usuario" para compatibilidad con el frontend existente
                    map.put("id_usuario", String.valueOf(rs.getInt("id_usuarios_login")));
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
        // SchemaActual: id_especialidades (no id_especialidad), id_personal_medico (INT
        // PK auto)
        String insertPersonal = "INSERT INTO Personal_Medico (identidad, nombre_completo, telefono, id_especialidades, correo) "
                +
                "VALUES (?, ?, ?, ?, ?)";
        // SchemaActual: FK en Usuarios_Login = id_personal_medico (INT)
        String getIdPersonal = "SELECT id_personal_medico FROM Personal_Medico WHERE identidad = ?";
        String updateUsuario = "UPDATE Usuarios_Login SET id_personal_medico = ? WHERE id_usuarios_login = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try {
            conn.setAutoCommit(false);

            // 1. Insertar Personal Medico
            try (PreparedStatement stmtPersonal = conn.prepareStatement(insertPersonal)) {
                stmtPersonal.setString(1, pm.getIdentidad());
                stmtPersonal.setString(2, pm.getNombre_completo());
                stmtPersonal.setString(3, pm.getTelefono());
                stmtPersonal.setInt(4, pm.getId_especialidades());
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
        // SchemaActual: id_especialidades, borrado DEFAULT 'No' (no hay columna estado)
        String insertPersonal = "INSERT INTO Personal_Medico (identidad, nombre_completo, telefono, id_especialidades, correo) "
                +
                "VALUES (?, ?, ?, ?, ?)";
        String getIdPersonal = "SELECT id_personal_medico FROM Personal_Medico WHERE identidad = ?";
        String insertUsuario = "INSERT INTO Usuarios_Login (correo, contrasenia, id_personal_medico, rol_sistema) " +
                "VALUES (?, ?, ?, ?)";

        Connection conn = DBConnection.getInstance().getConnection();

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPersonal = conn.prepareStatement(insertPersonal)) {
                stmtPersonal.setString(1, pm.getIdentidad());
                stmtPersonal.setString(2, pm.getNombre_completo());
                stmtPersonal.setString(3, pm.getTelefono());
                stmtPersonal.setInt(4, pm.getId_especialidades());
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
                stmtUsuario.setString(1, pm.getCorreo());
                stmtUsuario.setString(2, pm.getContrasenia());
                stmtUsuario.setInt(3, idPersonalMedico);
                stmtUsuario.setString(4, pm.getRol_sistema());

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
        // SchemaActual: PK = id_usuarios_login, borrado='No' (o 'Si')
        String query = "SELECT id_usuarios_login, correo, rol_sistema, borrado, fecha_creacion " +
                "FROM Usuarios_Login ORDER BY fecha_creacion DESC";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                    // "id_usuario" se mantiene para compatibilidad con el frontend
                    map.put("id_usuario", rs.getInt("id_usuarios_login"));
                    map.put("correo", rs.getString("correo"));
                    map.put("rol_sistema", rs.getString("rol_sistema"));
                    map.put("estado", rs.getString("borrado").equals("No") ? "Activo" : "Inactivo");
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
        // SchemaActual: FK id_especialidades, borrado='No' (o 'Si')
        String query = "SELECT pm.id_personal_medico, pm.identidad, pm.nombre_completo, pm.telefono, pm.correo, pm.borrado, "
                +
                "e.nombre_especialidad FROM Personal_Medico pm " +
                "LEFT JOIN Especialidades e ON pm.id_especialidades = e.id_especialidades " +
                "ORDER BY pm.id_personal_medico DESC";
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
                    map.put("estado", rs.getString("borrado").equals("No") ? "Activo" : "Inactivo");
                    map.put("especialidadNombre", rs.getString("nombre_especialidad"));
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
    public boolean actualizarUsuario(int idUsuario, String rolSistema, String nuevaContrasenia) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            // SchemaActual: WHERE id_usuarios_login = ?
            if (nuevaContrasenia != null && !nuevaContrasenia.isEmpty()) {
                String hashed = application.util.SecurityUtils.hashPassword(nuevaContrasenia);
                String q = "UPDATE Usuarios_Login SET rol_sistema = ?, contrasenia = ? WHERE id_usuarios_login = ?";
                try (PreparedStatement stmt = conn.prepareStatement(q)) {
                    stmt.setString(1, rolSistema);
                    stmt.setString(2, hashed);
                    stmt.setInt(3, idUsuario);
                    return stmt.executeUpdate() > 0;
                }
            } else {
                String q = "UPDATE Usuarios_Login SET rol_sistema = ? WHERE id_usuarios_login = ?";
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

    // Inactivar usuario — SchemaActual: borrado='Si', fecha_borrado
    public boolean inactivarUsuario(int idUsuario) {
        String q = "UPDATE Usuarios_Login SET borrado = 'Si', fecha_borrado = NOW() WHERE id_usuarios_login = ?";
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
        String query = "UPDATE Personal_Medico SET borrado = 'Si', fecha_borrado = NOW() WHERE id_personal_medico = ?";
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
        String query = "UPDATE Usuarios_Login SET borrado = 'No', fecha_borrado = NULL WHERE id_usuarios_login = ?";
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


    // Actualizar datos de personal médico
    public boolean actualizarPersonalMedico(String identidad, String nombreCompleto, String telefono,
            int idEspecialidades) {
        // SchemaActual: id_especialidades (no id_especialidad)
        String q = "UPDATE Personal_Medico SET nombre_completo = ?, telefono = ?, id_especialidades = ? WHERE identidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(q)) {
                stmt.setString(1, nombreCompleto);
                stmt.setString(2, telefono);
                stmt.setInt(3, idEspecialidades);
                stmt.setString(4, identidad);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar personal médico: " + e.getMessage());
            return false;
        }
    }

    // Inactivar personal médico — SchemaActual: borrado='Si', fecha_borrado
    public boolean inactivarPersonalMedico(String identidad) {
        String q = "UPDATE Personal_Medico SET borrado = 'Si', fecha_borrado = NOW() WHERE identidad = ?";
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
        String q = "UPDATE Personal_Medico SET borrado = 'No', fecha_borrado = NULL WHERE identidad = ?";
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
