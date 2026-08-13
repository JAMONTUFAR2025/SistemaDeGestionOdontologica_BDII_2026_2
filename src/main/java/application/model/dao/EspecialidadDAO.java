package application.model.dao;

import application.model.connection.DBConnection;
import application.model.entity.Especialidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadDAO {

    public List<Especialidad> obtenerEspecialidades() {
        List<Especialidad> lista = new ArrayList<>();
        // SchemaActual: PK = id_especialidad
        String query = "SELECT id_especialidad, nombre_especialidad FROM Especialidades ORDER BY id_especialidad DESC";

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
        // SchemaActual: WHERE id_especialidad = ?
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

    // Eliminar especialidad (borrado físico)
    public boolean eliminarEspecialidad(int id) {
        // SchemaActual: WHERE id_especialidad = ?
        String q = "DELETE FROM Especialidades WHERE id_especialidad = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(q)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar especialidad: " + e.getMessage());
            return false;
        }
    }
}
