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

    public boolean registrarPersonalYUsuario(PersonalMedico pm) {
        String insertPersonal = "INSERT INTO Personal_Medico (nombre_completo, identidad, telefono, rol, id_especialidad, correo) VALUES (?, ?, ?, ?, ?, ?)";
        String insertUsuario = "INSERT INTO Usuarios_Login (correo, contrasenia, id_medico, rol_sistema, estado) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = DBConnection.getInstance().getConnection();
        
        try {
            // Iniciar Transaccion
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
                
                int filasPersonal = stmtPersonal.executeUpdate();
                if (filasPersonal == 0) {
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
            
            // 2. Insertar Usuario Login
            try (PreparedStatement stmtUsuario = conn.prepareStatement(insertUsuario)) {
                stmtUsuario.setString(1, pm.getCorreo());
                stmtUsuario.setString(2, pm.getContrasenia()); // Idealmente debe estar encriptada (hash)
                stmtUsuario.setInt(3, idMedicoGenerado);
                stmtUsuario.setString(4, pm.getRol_sistema());
                stmtUsuario.setString(5, pm.getEstado());
                
                int filasUsuario = stmtUsuario.executeUpdate();
                if (filasUsuario == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Si todo salió bien, guardamos los cambios definitivamente
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error en la transacción al registrar personal: " + e.getMessage());
            try {
                // Revertir si hubo error
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                // Restaurar autocommit a true para que no afecte otras operaciones
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
