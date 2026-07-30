package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {

    // Strong reference to prevent Garbage Collection by JavaFX WebEngine
    private application.controller.JavaConnector javaConnector = new application.controller.JavaConnector();

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

        // Configurar el puente de Java a Javascript
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                netscape.javascript.JSObject window = (netscape.javascript.JSObject) webView.getEngine()
                        .executeScript("window");
                window.setMember("javaConnector", javaConnector);
            }
        });

        // Habilitar alert() de JavaScript para que se muestre como ventana emergente en
        // JavaFX
        webView.getEngine().setOnAlert(event -> {
            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            alerta.setTitle("SOE Odontología");
            alerta.setHeaderText(null);
            alerta.setContentText(event.getData());
            alerta.showAndWait();
        });

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