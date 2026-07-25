package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Crear el navegador interno (WebView)
        WebView webView = new WebView();

        // 2. Cargar login.html desde la carpeta resources/
        var resource = getClass().getResource("/login.html");
        if (resource == null) {
            throw new RuntimeException("No se encontró el archivo '/login.html' en src/main/resources/");
        }
        String urlHtml = resource.toExternalForm();
        webView.getEngine().load(urlHtml);

        // 3. Configurar la ventana
        Scene scene = new Scene(webView, 420, 550);
        primaryStage.setTitle("SOE Odontología - Inicio de Sesión");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}