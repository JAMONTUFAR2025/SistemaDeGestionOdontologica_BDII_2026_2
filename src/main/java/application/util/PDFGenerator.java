package application.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PDFGenerator {

    // Ancho de rollo térmico: 80mm = ~226.77 puntos. Alto: 800 puntos (se ajusta, pero le damos un alto grande).
    private static final Rectangle TICKET_SIZE = new Rectangle(226.77f, 800f);

    public static File generarReciboPdf(Map<String, Object> data, String medico) throws Exception {
        File tempFile = File.createTempFile("Recibo_" + data.get("numero_recibo"), ".pdf");
        
        Document document = new Document(TICKET_SIZE, 10, 10, 10, 10);
        PdfWriter.getInstance(document, new FileOutputStream(tempFile));
        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);

        // Logo
        try {
            URL logoUrl = PDFGenerator.class.getResource("/Logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.setAlignment(Element.ALIGN_CENTER);
                logo.scaleToFit(100, 100);
                document.add(logo);
            }
        } catch (Exception e) {
            System.err.println("Logo no encontrado o no se pudo cargar.");
        }

        Paragraph title = new Paragraph("CLÍNICA ODONTOLÓGICA", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph subtitle = new Paragraph("Comprobante de Pago", fontSubtitle);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);

        document.add(new Paragraph("--------------------------------------------------", fontNormal));

        document.add(new Paragraph("Recibo No: " + data.get("numero_recibo"), fontBold));
        document.add(new Paragraph("Fecha: " + data.get("fecha_emision"), fontNormal));
        document.add(new Paragraph("Paciente: " + data.get("nombre_paciente"), fontNormal));
        if (data.get("identidad_paciente") != null && !data.get("identidad_paciente").toString().isEmpty()) {
            document.add(new Paragraph("ID Paciente: " + data.get("identidad_paciente"), fontNormal));
        }
        if (data.get("rtn_cliente") != null && !data.get("rtn_cliente").toString().isEmpty()) {
            document.add(new Paragraph("RTN: " + data.get("rtn_cliente"), fontNormal));
        }
        document.add(new Paragraph("Médico Tratante: " + (medico != null && !medico.isEmpty() ? medico : "N/D"), fontNormal));
        
        document.add(new Paragraph("--------------------------------------------------", fontNormal));
        document.add(new Paragraph("CONCEPTO:", fontBold));
        document.add(new Paragraph(data.get("concepto") != null ? data.get("concepto").toString() : "", fontNormal));
        
        document.add(new Paragraph("--------------------------------------------------", fontNormal));
        document.add(new Paragraph("Suma Neta: L " + formatNumber(data.get("suma_neta")), fontNormal));
        
        if (data.get("total_honorarios") != null && Double.parseDouble(data.get("total_honorarios").toString()) > 0) {
            document.add(new Paragraph("Honorarios: L " + formatNumber(data.get("total_honorarios")), fontNormal));
        }
        if (data.get("total_retenido") != null && Double.parseDouble(data.get("total_retenido").toString()) > 0) {
            document.add(new Paragraph("Retenido: L " + formatNumber(data.get("total_retenido")), fontNormal));
        }
        
        document.add(new Paragraph("TOTAL: L " + formatNumber(data.get("total_neto_recibido")), fontBold));
        document.add(new Paragraph("Método de Pago: " + data.get("metodo_pago"), fontNormal));
        
        document.add(new Paragraph("--------------------------------------------------", fontNormal));
        Paragraph footer = new Paragraph("¡Gracias por su preferencia!", fontNormal);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return tempFile;
    }

    public static File generarArqueoPdf(Map<String, Object> data, List<Map<String, Object>> movimientos) throws Exception {
        File tempFile = File.createTempFile("CierreCaja_" + data.get("id_caja_sesion"), ".pdf");
        
        Document document = new Document(PageSize.LETTER, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(tempFile));
        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

        // Logo
        try {
            URL logoUrl = PDFGenerator.class.getResource("/Logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.setAlignment(Element.ALIGN_CENTER);
                logo.scaleToFit(150, 150);
                document.add(logo);
            }
        } catch (Exception e) {
            System.err.println("Logo no encontrado o no se pudo cargar.");
        }

        Paragraph title = new Paragraph("CLÍNICA ODONTOLÓGICA", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph subtitle = new Paragraph("Reporte de Cierre de Caja (Arqueo)", fontSubtitle);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);
        
        document.add(new Paragraph(" "));

        // Info de la caja
        document.add(new Paragraph("ID de Caja: " + data.get("id_caja_sesion"), fontNormal));
        document.add(new Paragraph("Fecha Apertura: " + data.get("fecha_apertura"), fontNormal));
        document.add(new Paragraph("Fecha Cierre: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), fontNormal));
        document.add(new Paragraph("Usuario Apertura: " + data.get("usuario_apertura"), fontNormal));
        document.add(new Paragraph("Usuario Cierre: " + (data.get("usuario_cierre") != null ? data.get("usuario_cierre") : "N/D"), fontNormal));
        
        document.add(new Paragraph(" "));
        document.add(new Paragraph("RESUMEN DE EFECTIVO", fontSubtitle));
        document.add(new Paragraph("------------------------------------------------------", fontNormal));
        document.add(new Paragraph("Monto Apertura: L " + formatNumber(data.get("monto_apertura")), fontNormal));
        document.add(new Paragraph("(+) Ingresos Efectivo: L " + formatNumber(data.get("ingresos_efectivo")), fontNormal));
        document.add(new Paragraph("(-) Egresos Efectivo: L " + formatNumber(data.get("egresos_efectivo")), fontNormal));
        document.add(new Paragraph("(=) Efectivo Esperado: L " + formatNumber(data.get("efectivo_esperado")), fontBold));
        document.add(new Paragraph("Efectivo Real Contado: L " + formatNumber(data.get("monto_cierre_real")), fontBold));
        document.add(new Paragraph("Diferencia: L " + formatNumber(data.get("diferencia")), fontNormal));

        document.add(new Paragraph(" "));
        document.add(new Paragraph("DESGLOSE POR MÉTODO DE PAGO", fontSubtitle));
        document.add(new Paragraph("------------------------------------------------------", fontNormal));
        document.add(new Paragraph("Total Efectivo: L " + formatNumber(data.get("ingresos_efectivo")), fontNormal));
        document.add(new Paragraph("Total Transferencias / Depósitos: L " + formatNumber(data.get("ingresos_transferencia")), fontNormal));
        document.add(new Paragraph("Total POS / Tarjetas: L " + formatNumber(data.get("ingresos_pos")), fontNormal));
        
        double totalGeneral = 0.0;
        if (data.get("ingresos_efectivo") != null) totalGeneral += Double.parseDouble(data.get("ingresos_efectivo").toString());
        if (data.get("ingresos_transferencia") != null) totalGeneral += Double.parseDouble(data.get("ingresos_transferencia").toString());
        if (data.get("ingresos_pos") != null) totalGeneral += Double.parseDouble(data.get("ingresos_pos").toString());
        document.add(new Paragraph("TOTAL GENERAL RECAUDADO: L " + formatNumber(totalGeneral), fontBold));
        
        document.add(new Paragraph(" "));
        document.add(new Paragraph("DETALLE DE TRANSACCIONES", fontSubtitle));
        document.add(new Paragraph("------------------------------------------------------", fontNormal));
        
        if (movimientos == null || movimientos.isEmpty()) {
            document.add(new Paragraph("Sin movimientos registrados en esta sesión.", fontNormal));
        } else {
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setWidths(new float[]{2f, 4.5f, 2f, 2f});
            
            table.addCell(new PdfPCell(new Phrase("Tipo", fontBold)));
            table.addCell(new PdfPCell(new Phrase("Concepto / Paciente", fontBold)));
            table.addCell(new PdfPCell(new Phrase("Método de Pago", fontBold)));
            table.addCell(new PdfPCell(new Phrase("Monto", fontBold)));
            
            for (Map<String, Object> mov : movimientos) {
                table.addCell(new PdfPCell(new Phrase(mov.get("tipo") != null ? mov.get("tipo").toString().toUpperCase() : "", fontNormal)));
                table.addCell(new PdfPCell(new Phrase(mov.get("descripcion") != null ? mov.get("descripcion").toString() : "", fontNormal)));
                table.addCell(new PdfPCell(new Phrase(mov.get("metodo_pago") != null ? mov.get("metodo_pago").toString() : "", fontNormal)));
                table.addCell(new PdfPCell(new Phrase("L " + formatNumber(mov.get("monto")), fontNormal)));
            }
            document.add(table);
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        
        PdfPTable firmasTable = new PdfPTable(2);
        firmasTable.setWidthPercentage(100);
        PdfPCell cajeroCell = new PdfPCell(new Phrase("______________________________\nFirma Cajero/Recepcionista", fontNormal));
        cajeroCell.setBorder(Rectangle.NO_BORDER);
        cajeroCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell adminCell = new PdfPCell(new Phrase("______________________________\nFirma Administración", fontNormal));
        adminCell.setBorder(Rectangle.NO_BORDER);
        adminCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        firmasTable.addCell(cajeroCell);
        firmasTable.addCell(adminCell);
        document.add(firmasTable);



        document.close();
        return tempFile;
    }

    private static String formatNumber(Object number) {
        if (number == null) return "0.00";
        try {
            double val = Double.parseDouble(number.toString());
            return String.format("%.2f", val);
        } catch (Exception e) {
            return "0.00";
        }
    }
}
