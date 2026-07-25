package application;

import application.model.connection.DBConnection;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Intentando conectar a la base de datos...");
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("¡CONEXION EXITOSA!");
                System.out.println("Motor: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Version: " + conn.getMetaData().getDatabaseProductVersion());
            } else {
                System.out.println("¡ERROR! La conexion devolvio null o esta cerrada.");
            }
        } catch (Exception e) {
            System.err.println("¡ERROR DE CONEXION!");
            e.printStackTrace();
        }
    }
}
