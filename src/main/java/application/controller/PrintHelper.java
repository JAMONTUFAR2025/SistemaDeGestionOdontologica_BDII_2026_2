package application.controller;

import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Helper de impresión para JavaFX WebView.
 * Se registra como window.printHelper en el WebEngine.
 *
 * Uso desde JavaScript:
 *   window.printHelper.printCurrentPage();
 */
public class PrintHelper {

    private WebView webView;

    public PrintHelper(WebView webView) {
        this.webView = webView;
    }

    /**
     * Imprime el contenido actual del WebView completo usando el diálogo
     * de impresión nativo del sistema operativo.
     * Configura el papel a Letter, portrait, márgenes de 15mm (~42.52 pts).
     */
    public void printCurrentPage() {
        javafx.application.Platform.runLater(() -> {
            try {
                PrinterJob job = PrinterJob.createPrinterJob();
                if (job == null) {
                    System.err.println("-> ERROR: No se pudo crear el trabajo de impresión. ¿Hay impresora instalada?");
                    return;
                }

                // Configurar página: Letter, Portrait, márgenes de 15mm
                Printer printer = job.getPrinter();
                double marginMM = 15.0;
                // Convertir mm a puntos (1mm = 2.8346 pts)
                double marginPts = marginMM * 2.8346;
                PageLayout pageLayout = printer.createPageLayout(
                        Paper.NA_LETTER,
                        PageOrientation.PORTRAIT,
                        marginPts, marginPts, marginPts, marginPts
                );
                job.getJobSettings().setPageLayout(pageLayout);

                // Mostrar el diálogo de impresión al usuario
                boolean proceed = job.showPrintDialog(webView.getScene().getWindow());
                if (proceed) {
                    WebEngine engine = webView.getEngine();
                    engine.print(job);
                    job.endJob();
                    System.out.println("-> Documento enviado a la impresora correctamente.");
                } else {
                    job.cancelJob();
                    System.out.println("-> Impresión cancelada por el usuario.");
                }
            } catch (Exception e) {
                System.err.println("-> ERROR al imprimir: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
