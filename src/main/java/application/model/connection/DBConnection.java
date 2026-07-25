package application.model.connection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try {
            // Load properties from the file
            Properties props = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("database.properties");
            
            if (inputStream == null) {
                System.err.println("No se encontro el archivo database.properties en src/main/resources");
                return;
            }
            
            props.load(inputStream);
            
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");
            
            // Register MySQL JDBC Driver (optional in newer JDBC versions, but good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish the connection
            this.connection = DriverManager.getConnection(url, user, pass);
            
        } catch (Exception e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        } else {
            try {
                if (instance.getConnection().isClosed()) {
                    instance = new DBConnection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
