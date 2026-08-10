package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {

    // Strong references to prevent Garbage Collection by JavaFX WebEngine
    private application.controller.AuthController authController = new application.controller.AuthController();
    private application.controller.PacienteController pacienteController = new application.controller.PacienteController();
    private application.controller.PersonalController personalController = new application.controller.PersonalController();
    private application.controller.CitaController citaController = new application.controller.CitaController();
    private application.controller.FinanzasController finanzasController = new application.controller.FinanzasController();
    private application.controller.HistoriaClinicaController historiaClinicaController = new application.controller.HistoriaClinicaController();
    private application.controller.WhatsAppController whatsAppController = new application.controller.WhatsAppController();
    private application.controller.DocumentosController documentosController = new application.controller.DocumentosController();
    private application.controller.CatalogoController catalogoController = new application.controller.CatalogoController();

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
                window.setMember("authController", authController);
                window.setMember("pacienteController", pacienteController);
                window.setMember("personalController", personalController);
                window.setMember("citaController", citaController);
                window.setMember("finanzasController", finanzasController);
                window.setMember("historiaClinicaController", historiaClinicaController);
                window.setMember("whatsAppController", whatsAppController);
                window.setMember("documentosController", documentosController);
                window.setMember("catalogoController", catalogoController);
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
        primaryStage.setMaximized(true); // Maximizar pantalla sin tapar la barra de tareas
        primaryStage.show();
    }

    public static void main(String[] args) {
        application.controller.WhatsAppController.iniciarServidorSiAplica();
        launch(args);
    }
}