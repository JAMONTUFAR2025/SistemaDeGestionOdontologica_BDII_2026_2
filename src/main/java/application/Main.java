package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Crear un layout simple
        StackPane root = new StackPane();
        Label label = new Label("¡Hola, JavaFX está funcionando!");
        root.getChildren().add(label);
        
        // Configurar la escena
        Scene scene = new Scene(root, 400, 300);
        
        // Configurar el Stage (ventana)
        primaryStage.setTitle("Ventana de Prueba");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
