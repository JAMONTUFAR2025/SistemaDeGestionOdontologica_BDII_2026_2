package application.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class PDFGenerator {

    public static void generarReciboPago(Map<String, Object> factura, String rutaDestino) {
        // Formato Ticket (ej. 80mm ancho x largo dinámico, aprox. 226 ptos x 500 ptos)
        Rectangle pageSize = new Rectangle(226f, 500f);
        Document document = new Document(pageSize, 10, 10, 10, 10);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(rutaDestino));
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);

            // Cabecera
            Paragraph header = new Paragraph("CLÍNICA ODONTOLÓGICA", fontTitulo);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph("RECIBO DE PAGO", fontSub);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(subHeader);
            document.add(new Paragraph("------------------------------------------------", fontNormal));

            // Datos Factura
            document.add(new Paragraph("Recibo N°: " + (factura.get("numero_recibo") != null ? factura.get("numero_recibo") : factura.get("id_factura")), fontBold));
            document.add(new Paragraph("Fecha: " + factura.get("fecha_emision"), fontNormal));
            document.add(new Paragraph("Paciente: " + factura.get("paciente"), fontNormal));
            document.add(new Paragraph("RTN: " + (factura.get("rtn_cliente") != null ? factura.get("rtn_cliente") : "N/A"), fontNormal));
            document.add(new Paragraph("Concepto: " + factura.get("concepto"), fontNormal));
            
            document.add(new Paragraph("------------------------------------------------", fontNormal));

            // Totales
            document.add(new Paragraph("Subtotal: L " + factura.get("suma_neta"), fontNormal));
            document.add(new Paragraph("Total Recibido: L " + factura.get("total_neto_recibido"), fontBold));
            document.add(new Paragraph("Método de Pago: " + factura.get("metodo_pago"), fontNormal));
            
            document.add(new Paragraph("------------------------------------------------", fontNormal));
            
            Paragraph footer = new Paragraph("¡Gracias por su preferencia!", fontNormal);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            System.err.println("Error generando PDF de recibo: " + e.getMessage());
        }
    }

    public static void generarReporteCierreCaja(Map<String, Object> caja, Map<String, Object> arqueo, List<Map<String, Object>> movimientos, String rutaDestino) {
        Document document = new Document(PageSize.LETTER);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(rutaDestino));
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font fontSub = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

            Paragraph header = new Paragraph("CLÍNICA ODONTOLÓGICA", fontTitulo);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph("REPORTE DE CIERRE DE CAJA CHICA (ARQUEO)", fontSub);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(subHeader);
            document.add(new Paragraph(" "));

            // Datos de la Caja
            document.add(new Paragraph("ID de Sesión de Caja: " + caja.get("id_caja_sesion"), fontBold));
            document.add(new Paragraph("Fecha Apertura: " + caja.get("fecha_apertura") + " | Usuario: " + caja.get("usuario_apertura"), fontNormal));
            document.add(new Paragraph("Fecha Cierre: " + (caja.get("fecha_cierre") != null ? caja.get("fecha_cierre") : "N/A") + " | Usuario: " + (caja.get("usuario_cierre") != null ? caja.get("usuario_cierre") : "N/A"), fontNormal));
            document.add(new Paragraph("Estado: " + caja.get("estado"), fontBold));
            document.add(new Paragraph(" "));

            // Cuadro de Efectivo
            PdfPTable tableEfectivo = new PdfPTable(2);
            tableEfectivo.setWidthPercentage(60);
            tableEfectivo.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableEfectivo.addCell(new PdfPCell(new Phrase("Monto de Apertura", fontBold)));
            tableEfectivo.addCell(new PdfPCell(new Phrase("L " + arqueo.get("monto_apertura"), fontNormal)));
            
            tableEfectivo.addCell(new PdfPCell(new Phrase("(+) Ingresos en Efectivo", fontBold)));
            tableEfectivo.addCell(new PdfPCell(new Phrase("L " + arqueo.get("ingresos_efectivo"), fontNormal)));
            
            tableEfectivo.addCell(new PdfPCell(new Phrase("(-) Egresos en Efectivo", fontBold)));
            tableEfectivo.addCell(new PdfPCell(new Phrase("L " + arqueo.get("egresos_efectivo"), fontNormal)));
            
            tableEfectivo.addCell(new PdfPCell(new Phrase("(=) EFECTIVO ESPERADO EN CAJA", fontBold)));
            tableEfectivo.addCell(new PdfPCell(new Phrase("L " + arqueo.get("efectivo_esperado"), fontBold)));
            
            tableEfectivo.addCell(new PdfPCell(new Phrase("EFECTIVO REAL CONTADO", fontBold)));
            tableEfectivo.addCell(new PdfPCell(new Phrase("L " + (caja.get("monto_cierre_real") != null ? caja.get("monto_cierre_real") : "0.0"), fontBold)));
            
            tableEfectivo.addCell(new PdfPCell(new Phrase("DIFERENCIA (Faltante/Sobrante)", fontBold)));
            tableEfectivo.addCell(new PdfPCell(new Phrase("L " + (caja.get("diferencia") != null ? caja.get("diferencia") : "0.0"), fontBold)));
            
            document.add(tableEfectivo);
            document.add(new Paragraph(" "));

            // Lista de Movimientos
            document.add(new Paragraph("DESGLOSE DE MOVIMIENTOS", fontSub));
            document.add(new Paragraph(" "));
            
            PdfPTable tableMovimientos = new PdfPTable(5);
            tableMovimientos.setWidthPercentage(100);
            tableMovimientos.setWidths(new float[]{1.5f, 2f, 4f, 2f, 2f});
            
            String[] headers = {"Tipo", "Fecha", "Descripción", "Monto", "Método"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontBold));
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                tableMovimientos.addCell(cell);
            }
            
            for (Map<String, Object> mov : movimientos) {
                tableMovimientos.addCell(new Phrase((String) mov.get("tipo"), fontNormal));
                tableMovimientos.addCell(new Phrase(String.valueOf(mov.get("fecha")), fontNormal));
                tableMovimientos.addCell(new Phrase((String) mov.get("descripcion"), fontNormal));
                tableMovimientos.addCell(new Phrase("L " + mov.get("monto"), fontNormal));
                tableMovimientos.addCell(new Phrase((String) mov.get("metodo_pago"), fontNormal));
            }
            
            document.add(tableMovimientos);
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Observaciones: " + (caja.get("observaciones") != null ? caja.get("observaciones") : "Ninguna"), fontNormal));
            
            // Firmas
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            
            PdfPTable tableFirmas = new PdfPTable(2);
            tableFirmas.setWidthPercentage(100);
            tableFirmas.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            tableFirmas.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            
            tableFirmas.addCell(new Phrase("___________________________\nFirma de Cajero / Recepción", fontNormal));
            tableFirmas.addCell(new Phrase("___________________________\nFirma de Administración", fontNormal));
            
            document.add(tableFirmas);

            document.close();
        } catch (Exception e) {
            System.err.println("Error generando PDF de cierre de caja: " + e.getMessage());
        }
    }
}
