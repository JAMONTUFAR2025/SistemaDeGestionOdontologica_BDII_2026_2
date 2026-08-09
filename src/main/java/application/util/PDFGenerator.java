package application.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class PDFGenerator {

    public static void generarReciboPago(Map<String, Object> factura, String rutaDestino) {
        // Formato Apaisado (Mitad de carta: 8.5 x 5.5 pulgadas)
        Rectangle pageSize = new Rectangle(612f, 396f);
        Document document = new Document(pageSize, 20, 20, 20, 20);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(rutaDestino));
            document.open();
            PdfContentByte cb = writer.getDirectContent();

            // Dibujar rectángulo con bordes redondeados
            cb.setLineWidth(1f);
            cb.roundRectangle(15, 15, 582, 366, 10);
            cb.stroke();

            // Fuentes
            Font fontHeaderTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font fontHeaderTitleRed = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, java.awt.Color.RED);
            Font fontHeaderSub = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 7);
            Font fontSmallBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7);

            // Tabla principal cabecera
            PdfPTable tableHeader = new PdfPTable(2);
            tableHeader.setWidthPercentage(100);
            tableHeader.setWidths(new float[]{60f, 40f});

            // Columna Izquierda (Datos Dra.)
            PdfPCell cellIzq = new PdfPCell();
            cellIzq.setBorder(Rectangle.NO_BORDER);
            cellIzq.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph p1 = new Paragraph("DRA. CINDY CAROLINA ENAMORADO JUAREZ", fontHeaderTitle);
            p1.setAlignment(Element.ALIGN_CENTER);
            cellIzq.addElement(p1);

            Paragraph p2 = new Paragraph("ODONTOLOGA", fontHeaderSub);
            p2.setAlignment(Element.ALIGN_CENTER);
            cellIzq.addElement(p2);

            Paragraph p3 = new Paragraph("R. T. N. 05011988125225 - CEL. 98224993", fontBold);
            p3.setAlignment(Element.ALIGN_CENTER);
            cellIzq.addElement(p3);

            Paragraph p4 = new Paragraph("Barrio Galeras, calle principal, Santa Barbara, S. B. Honduras, C. A.", fontNormal);
            p4.setAlignment(Element.ALIGN_CENTER);
            cellIzq.addElement(p4);

            Paragraph p5 = new Paragraph("Email: dracindyenamorado@gmail.com", fontNormal);
            p5.setAlignment(Element.ALIGN_CENTER);
            cellIzq.addElement(p5);

            Paragraph p6 = new Paragraph("CAI: 522D52-B2AB16-9914E0-63BE03-09096D-7C", fontBold);
            p6.setAlignment(Element.ALIGN_CENTER);
            cellIzq.addElement(p6);

            tableHeader.addCell(cellIzq);

            // Columna Derecha (Datos Recibo)
            PdfPCell cellDer = new PdfPCell();
            cellDer.setBorder(Rectangle.NO_BORDER);
            cellDer.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph pr1 = new Paragraph("RECIBO POR HONORARIOS", fontHeaderTitle);
            pr1.setAlignment(Element.ALIGN_CENTER);
            cellDer.addElement(pr1);

            Paragraph pr2 = new Paragraph("000-001-04-00", fontNormal);
            pr2.setAlignment(Element.ALIGN_CENTER);
            cellDer.addElement(pr2);

            String numero = factura.get("numero_recibo") != null ? factura.get("numero_recibo").toString() : "";
            Paragraph pr3 = new Paragraph("Nº " + numero, fontHeaderTitleRed);
            pr3.setAlignment(Element.ALIGN_CENTER);
            cellDer.addElement(pr3);

            cellDer.addElement(new Paragraph(" "));

            // Por Lps caja
            PdfPTable tableLps = new PdfPTable(2);
            tableLps.setWidthPercentage(90);
            tableLps.setWidths(new float[]{30f, 70f});
            
            PdfPCell cellLpsLbl = new PdfPCell(new Phrase("Por Lps:", fontHeaderSub));
            cellLpsLbl.setBorder(Rectangle.NO_BORDER);
            cellLpsLbl.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tableLps.addCell(cellLpsLbl);
            
            Object objTotalNeto = factura.get("total_neto_recibido");
            if (objTotalNeto == null) objTotalNeto = factura.get("suma_neta");
            String valorLps = (objTotalNeto != null) ? String.format("%.2f", Double.parseDouble(objTotalNeto.toString())) : "0.00";

            PdfPCell cellLpsVal = new PdfPCell(new Phrase(valorLps, fontHeaderSub));
            cellLpsVal.setBorder(Rectangle.BOX);
            cellLpsVal.setUseVariableBorders(true);
            cellLpsVal.setBorderColor(java.awt.Color.BLACK);
            cellLpsVal.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellLpsVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellLpsVal.setPadding(5);
            tableLps.addCell(cellLpsVal);

            cellDer.addElement(tableLps);

            tableHeader.addCell(cellDer);
            document.add(tableHeader);

            // Cuerpo del recibo
            PdfPTable tableBody = new PdfPTable(2);
            tableBody.setWidthPercentage(100);
            tableBody.setWidths(new float[]{20f, 80f});
            tableBody.setSpacingBefore(5f);

            Object pacObj = factura.get("nombre_paciente");
            if (pacObj == null) pacObj = factura.get("paciente");
            String pac = pacObj != null ? pacObj.toString() : "";
            
            Object rtnObj = factura.get("rtn_cliente");
            String rtn = rtnObj != null ? rtnObj.toString() : "";

            String letras = convertirNumeroALetras(Double.parseDouble(objTotalNeto != null ? objTotalNeto.toString() : "0.00"));
            
            Object concObj = factura.get("concepto");
            String conc = concObj != null ? concObj.toString() : "";

            agregarFilaLinea(tableBody, "Recibí de:", pac, fontBold, fontNormal);
            agregarFilaLinea(tableBody, "RTN:", rtn, fontBold, fontNormal);
            agregarFilaLinea(tableBody, "La suma neta de:", letras + " LEMPIRAS", fontBold, fontNormal);
            agregarFilaLinea(tableBody, "Por concepto de:", conc, fontBold, fontNormal);

            document.add(tableBody);
            
            // Línea separadora reducida
            Paragraph sep = new Paragraph("_________________________________________________________________________________________________________________");
            sep.getFont().setSize(8);
            sep.setSpacingAfter(5f);
            document.add(sep);
            
            // Pie
            PdfPTable tableFooter = new PdfPTable(3);
            tableFooter.setWidthPercentage(100);
            tableFooter.setWidths(new float[]{45f, 40f, 15f});

            // Pie Izquierdo (Fecha, firma, copias)
            PdfPCell cellFtIzq = new PdfPCell();
            cellFtIzq.setBorder(Rectangle.NO_BORDER);
            
            String fecha = factura.get("fecha_emision") != null ? factura.get("fecha_emision").toString() : "";
            String dia = "   ";
            String mes = "      ";
            String anio = "      ";
            if (fecha.length() >= 10) {
                anio = fecha.substring(0,4);
                mes = obtenerNombreMes(Integer.parseInt(fecha.substring(5,7)));
                dia = fecha.substring(8,10);
            }
            
            Chunk cFecha = new Chunk("     " + dia + "     de     " + mes + "     del     " + anio + "     ", fontNormal);
            cFecha.setUnderline(0.5f, -2f);
            Paragraph fechaPar = new Paragraph(cFecha);
            fechaPar.setAlignment(Element.ALIGN_CENTER);
            fechaPar.setSpacingBefore(5f);
            cellFtIzq.addElement(fechaPar);
            
            Paragraph firmaLineSup = new Paragraph("_______________________", fontNormal);
            firmaLineSup.setAlignment(Element.ALIGN_CENTER);
            firmaLineSup.setSpacingBefore(15f);
            cellFtIzq.addElement(firmaLineSup);
            
            Paragraph firmaLine = new Paragraph("firma", fontNormal);
            firmaLine.setAlignment(Element.ALIGN_CENTER);
            cellFtIzq.addElement(firmaLine);
            
            Paragraph copiaLine = new Paragraph("Original: Cliente Copia: Emisor", fontSmallBold);
            copiaLine.setSpacingBefore(10f);
            cellFtIzq.addElement(copiaLine);

            tableFooter.addCell(cellFtIzq);

            // Pie Derecho (Totales)
            PdfPCell cellFtDer = new PdfPCell();
            cellFtDer.setBorder(Rectangle.NO_BORDER);
            
            PdfPTable tableTotales = new PdfPTable(2);
            tableTotales.setWidthPercentage(100);
            tableTotales.setWidths(new float[]{65f, 35f});

            String th = (factura.get("total_honorarios") != null) ? String.format("%.2f", Double.parseDouble(factura.get("total_honorarios").toString())) : valorLps;
            String tr = (factura.get("total_retenido") != null) ? String.format("%.2f", Double.parseDouble(factura.get("total_retenido").toString())) : "0.00";

            PdfPCell c1 = new PdfPCell(new Phrase("Total por Honorarios  L.:", fontNormal)); c1.setBorder(Rectangle.TOP | Rectangle.LEFT); c1.setPadding(3); tableTotales.addCell(c1);
            PdfPCell c2 = new PdfPCell(new Phrase(th, fontNormal)); c2.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM); c2.setHorizontalAlignment(Element.ALIGN_RIGHT); c2.setPadding(3); tableTotales.addCell(c2);
            
            PdfPCell c3 = new PdfPCell(new Phrase("Total Retenido           L.:", fontNormal)); c3.setBorder(Rectangle.LEFT); c3.setPadding(3); tableTotales.addCell(c3);
            PdfPCell c4 = new PdfPCell(new Phrase(tr, fontNormal)); c4.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM); c4.setHorizontalAlignment(Element.ALIGN_RIGHT); c4.setPadding(3); tableTotales.addCell(c4);
            
            PdfPCell c5 = new PdfPCell(new Phrase("Total Neto Recibido  L.:", fontNormal)); c5.setBorder(Rectangle.LEFT | Rectangle.BOTTOM); c5.setPadding(3); tableTotales.addCell(c5);
            PdfPCell c6 = new PdfPCell(new Phrase(valorLps, fontNormal)); c6.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM); c6.setHorizontalAlignment(Element.ALIGN_RIGHT); c6.setPadding(3); tableTotales.addCell(c6);

            cellFtDer.addElement(tableTotales);
            tableFooter.addCell(cellFtDer);

            // Pie Derecho Extra (Logo)
            PdfPCell cellFtLogo = new PdfPCell();
            cellFtLogo.setBorder(Rectangle.NO_BORDER);
            cellFtLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellFtLogo.setVerticalAlignment(Element.ALIGN_TOP);
            try {
                java.net.URL logoUrl = PDFGenerator.class.getResource("/Logo.png");
                if (logoUrl != null) {
                    Image logo = Image.getInstance(logoUrl);
                    logo.scaleToFit(85, 85); // Ajustar tamaño para el rincón
                    logo.setAlignment(Element.ALIGN_CENTER);
                    cellFtLogo.addElement(logo);
                }
            } catch (Exception e) {
                System.err.println("No se pudo cargar el logo: " + e.getMessage());
            }
            tableFooter.addCell(cellFtLogo);

            document.add(tableFooter);
            
            Paragraph imprenta = new Paragraph("Impr. Sta. Barbara - R. T. N. 05011965013770 - CERT. No. 05011965013770-000-1 - Tel. 2643-1398\n" +
                                               "4Td. 000-001-04-00000701  -  000-001-04-00000900 - Fech. Recepción - 19/05/2026 - Límite. Emisión - 19/05/2027", fontSmall);
            imprenta.setAlignment(Element.ALIGN_RIGHT);
            imprenta.setSpacingBefore(5f);
            document.add(imprenta);

            document.close();
        } catch (Exception e) {
            System.err.println("Error generando PDF de recibo: " + e.getMessage());
        }
    }

    private static void agregarFilaLinea(PdfPTable table, String label, String value, Font fLabel, Font fValue) {
        PdfPCell cL = new PdfPCell(new Phrase(label, fLabel));
        cL.setBorder(Rectangle.NO_BORDER);
        cL.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cL.setPaddingBottom(4);
        table.addCell(cL);
        
        PdfPCell cV = new PdfPCell(new Phrase(value, fValue));
        cV.setBorder(Rectangle.BOTTOM);
        cV.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cV.setPaddingBottom(2);
        table.addCell(cV);
    }

    private static final String[] UNIDADES = {"", "UN ", "DOS ", "TRES ", "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE "};
    private static final String[] DECENAS = {"DIEZ ", "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS ", "DIECISIETE ", "DIECIOCHO ", "DIECINUEVE ", "VEINTE ", "TREINTA ", "CUARENTA ", "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA "};
    private static final String[] CENTENAS = {"", "CIENTO ", "DOSCIENTOS ", "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ", "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "};

    private static String convertirNumeroALetras(double numero) {
        long enteros = (long) Math.floor(numero);
        int centavos = (int) Math.round((numero - enteros) * 100);
        if (enteros == 0) return "CERO CON " + String.format("%02d", centavos) + "/100";
        String letras = leerMillones(enteros);
        return letras.trim() + " CON " + String.format("%02d", centavos) + "/100";
    }

    private static String leerDecenas(long numero) {
        if (numero < 10) return UNIDADES[(int) numero];
        if (numero < 20) return DECENAS[(int) (numero - 10)];
        if (numero < 30) return (numero == 20) ? "VEINTE " : "VEINTI" + UNIDADES[(int) (numero - 20)];
        long decena = numero / 10;
        long unidad = numero % 10;
        return DECENAS[(int) (decena + 8)] + ((unidad > 0) ? "Y " + UNIDADES[(int) unidad] : "");
    }

    private static String leerCentenas(long numero) {
        if (numero == 100) return "CIEN ";
        long centena = numero / 100;
        long resto = numero % 100;
        return CENTENAS[(int) centena] + leerDecenas(resto);
    }

    private static String leerMiles(long numero) {
        long miles = numero / 1000;
        long resto = numero % 1000;
        String strMiles = "";
        if (miles > 0) {
            strMiles = (miles == 1) ? "MIL " : leerCentenas(miles) + "MIL ";
        }
        return strMiles + leerCentenas(resto);
    }

    private static String leerMillones(long numero) {
        long millones = numero / 1000000;
        long resto = numero % 1000000;
        String strMillones = "";
        if (millones > 0) {
            if (millones == 1) strMillones = "UN MILLON ";
            else strMillones = leerMiles(millones) + "MILLONES ";
        }
        return strMillones + leerMiles(resto);
    }

    private static String obtenerNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        if (mes >= 1 && mes <= 12) return meses[mes - 1];
        return "";
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
