package application.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Genera PDFs para documentos médicos: Constancias y Consentimientos Informados.
 * Usa OpenPDF (lowagie) con tamaño Letter, orientación portrait.
 * Pie de página fijo al fondo de cada página.
 */
public class DocumentoPDFGenerator {

    // Márgenes: izq=36, der=36, arriba=20, abajo=50 (para el pie de página fijo)
    private static final float MARGIN_LEFT   = 36f;
    private static final float MARGIN_RIGHT  = 36f;
    private static final float MARGIN_TOP    = 20f;
    private static final float MARGIN_BOTTOM = 60f;

    public static File generarDocumentoPdf(String htmlContenido, String tipoPlantilla,
                                            String nombrePaciente, String identidadPaciente, String edadPaciente, String extraDataJson) throws Exception {

        String domicilio = "";
        String responsableNombre = "";
        String responsableIdentidad = "";
        if (extraDataJson != null && !extraDataJson.trim().isEmpty()) {
            try {
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(extraDataJson).getAsJsonObject();
                if (obj.has("domicilio") && !obj.get("domicilio").isJsonNull()) domicilio = obj.get("domicilio").getAsString();
                if (obj.has("responsableNombre") && !obj.get("responsableNombre").isJsonNull()) responsableNombre = obj.get("responsableNombre").getAsString();
                if (obj.has("responsableIdentidad") && !obj.get("responsableIdentidad").isJsonNull()) responsableIdentidad = obj.get("responsableIdentidad").getAsString();
            } catch(Exception e){}
        }

        String prefijo = "DocMedico_";
        if ("constancia".equals(tipoPlantilla)) prefijo = "Constancia_";
        else if ("consentimiento_cirugia".equals(tipoPlantilla)) prefijo = "Consentimiento_Cirugia_";
        else if ("consentimiento_endodoncia".equals(tipoPlantilla)) prefijo = "Consentimiento_Endodoncia_";

        File tempFile = File.createTempFile(prefijo + (identidadPaciente != null ? identidadPaciente : ""), ".pdf");

        Document document = new Document(PageSize.LETTER, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, MARGIN_BOTTOM);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempFile));
        
        // Agregar Evento para el Pie de Página Fijo
        writer.setPageEvent(new FooterEvent());

        document.open();

        // Fuentes
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.ITALIC);
        Font fontSectionTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

        // --- ENCABEZADO CON LOGO ---
        agregarEncabezado(document, fontTitle, fontSubtitle);

        if ("constancia".equals(tipoPlantilla)) {
            generarConstancia(document, fontNormal, fontBold, fontSectionTitle, nombrePaciente, identidadPaciente, edadPaciente);
        } else if ("consentimiento_cirugia".equals(tipoPlantilla)) {
            generarConsentimientoCirugia(document, fontNormal, fontBold, fontSectionTitle, nombrePaciente, identidadPaciente, edadPaciente, domicilio, responsableNombre, responsableIdentidad);
        } else if ("consentimiento_endodoncia".equals(tipoPlantilla)) {
            generarConsentimientoEndodoncia(document, fontNormal, fontBold, fontSectionTitle, nombrePaciente, identidadPaciente, edadPaciente, domicilio);
        }

        document.close();
        return tempFile;
    }

    // ═══════════════════════════════════════════════════
    // EVENTO DE PIE DE PÁGINA ABSOLUTO
    // ═══════════════════════════════════════════════════
    public static class FooterEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfContentByte cb = writer.getDirectContent();
                Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 9);
                
                PdfPTable footerTable = new PdfPTable(2);
                footerTable.setTotalWidth(document.right() - document.left());
                footerTable.setWidths(new float[]{1f, 6f});
                
                // Línea separadora superior en el footer
                PdfPCell lineCell = new PdfPCell(new Phrase(" "));
                lineCell.setColspan(2);
                lineCell.setBorder(Rectangle.TOP);
                lineCell.setBorderWidthTop(1f);
                lineCell.setFixedHeight(10f); // Espacio de la línea
                footerTable.addCell(lineCell);

                // Logo
                PdfPCell logoCell = new PdfPCell();
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setPadding(0);
                try {
                    URL logoUrl = DocumentoPDFGenerator.class.getResource("/Logo.png");
                    if (logoUrl != null) {
                        Image logo = Image.getInstance(logoUrl);
                        logo.scaleToFit(25, 25);
                        logoCell.addElement(logo);
                    }
                } catch (Exception ignored) {}
                footerTable.addCell(logoCell);

                // Texto
                PdfPCell textoCell = new PdfPCell();
                textoCell.setBorder(Rectangle.NO_BORDER);
                textoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                textoCell.setPadding(0);
                Paragraph direccion = new Paragraph("Edificio Perla, Frente al Hospital Santa Barbara Integrado, 2do Nivel, Local #2  |  Tel: 98224903", fontSmall);
                direccion.setAlignment(Element.ALIGN_RIGHT);
                textoCell.addElement(direccion);
                footerTable.addCell(textoCell);

                // Dibujar tabla en coordenadas absolutas (fondo de la página)
                footerTable.writeSelectedRows(0, -1, document.left(), document.bottom() - 10, cb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void agregarEncabezado(Document document, Font fontTitle, Font fontSubtitle) throws Exception {
        try {
            URL logoUrl = DocumentoPDFGenerator.class.getResource("/Logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.setAlignment(Element.ALIGN_CENTER);
                logo.scaleToFit(60, 60);
                logo.setSpacingAfter(2f);
                document.add(logo);
            }
        } catch (Exception e) {
            System.err.println("Logo no encontrado.");
        }

        Paragraph title = new Paragraph("SERVICIOS ODONTOLOGICOS ENAMORADO", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(0f);
        document.add(title);

        Paragraph subtitle = new Paragraph("DRA. CINDY ENAMORADO", fontSubtitle);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(4f);
        document.add(subtitle);

        com.lowagie.text.pdf.draw.LineSeparator line = new com.lowagie.text.pdf.draw.LineSeparator();
        line.setPercentage(100);
        document.add(new Chunk(line));
    }

    // ═══════════════════════════════════════════════════
    // CONSTANCIA MÉDICA (1 PÁGINA)
    // ═══════════════════════════════════════════════════
    private static void generarConstancia(Document document, Font fontNormal, Font fontBold,
                                           Font fontSectionTitle, String nombre, String identidad, String edad) throws Exception {

        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd / MM / yyyy"));
        String horaActual = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        PdfPTable fechaTable = new PdfPTable(2);
        fechaTable.setWidthPercentage(100);
        fechaTable.setSpacingBefore(10f);
        PdfPCell fechaCell = new PdfPCell(new Phrase("Fecha: " + fechaActual, fontBold));
        fechaCell.setBorder(Rectangle.NO_BORDER);
        PdfPCell lugarCell = new PdfPCell(new Phrase("Santa Barbara, S.B.", fontBold));
        lugarCell.setBorder(Rectangle.NO_BORDER);
        lugarCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        fechaTable.addCell(fechaCell);
        fechaTable.addCell(lugarCell);
        document.add(fechaTable);

        Paragraph tituloDoc = new Paragraph("Constancia", fontSectionTitle);
        tituloDoc.setAlignment(Element.ALIGN_CENTER);
        tituloDoc.setSpacingBefore(12f);
        tituloDoc.setSpacingAfter(12f);
        document.add(tituloDoc);

        String cuerpo = "Servicios odontológicos Enamorado por medio de la presente, hace constar que el paciente "
                + (nombre != null && !nombre.isEmpty() ? nombre : "________________________________________")
                + " de " + (edad != null && !edad.isEmpty() ? edad : "______") + " años"
                + (identidad != null && !identidad.isEmpty() ? ", con # de ID " + identidad : "")
                + ", se presentó a consulta odontológica por "
                + "____________________________________________________________________________________.";
        Paragraph pCuerpo = new Paragraph(cuerpo, fontNormal);
        pCuerpo.setAlignment(Element.ALIGN_JUSTIFIED);
        pCuerpo.setLeading(18f);
        pCuerpo.setSpacingAfter(10f);
        document.add(pCuerpo);

        Paragraph pTratamiento = new Paragraph(
                "Se le realizo el tratamiento de ___________________________________________________\n"
                + "___________________________________________________________________________________\n"
                + "___________________________________________________________________________________.", fontNormal);
        pTratamiento.setLeading(18f);
        pTratamiento.setSpacingAfter(10f);
        document.add(pTratamiento);

        Paragraph pFirma = new Paragraph(
                "Para constancia se firma en la ciudad de Santa Barbara a las " + horaActual + " horas "
                + "el día " + fechaActual + ".", fontNormal);
        pFirma.setLeading(18f);
        pFirma.setSpacingAfter(20f);
        document.add(pFirma);

        Paragraph pAtentamente = new Paragraph("Atentamente.", fontNormal);
        pAtentamente.setSpacingAfter(50f);
        document.add(pAtentamente);

        document.add(new Paragraph("______________________________", fontNormal));
        Paragraph pDra = new Paragraph("Dra. Cindy Carolina Enamorado Juárez", fontBold);
        pDra.setSpacingAfter(0f);
        document.add(pDra);
        Paragraph pCargo = new Paragraph("Cirujano Dentista. Gerente Propietario", fontNormal);
        pCargo.setSpacingAfter(0f);
        document.add(pCargo);
    }

    // ═══════════════════════════════════════════════════
    // CONSENTIMIENTO INFORMADO - CIRUGÍA BUCAL (2 PÁGINAS)
    // ═══════════════════════════════════════════════════
    private static void generarConsentimientoCirugia(Document document, Font fontNormal, Font fontBold,
                                                      Font fontSectionTitle, String nombre, String identidad, String edad, String domicilio, String responsableNombre, String responsableIdentidad) throws Exception {

        Paragraph tituloDoc = new Paragraph("CONSENTIMIENTO INFORMADO PARA TRATAMIENTO DE CIRUGÍA BUCAL", fontSectionTitle);
        tituloDoc.setAlignment(Element.ALIGN_CENTER);
        tituloDoc.setSpacingBefore(15f);
        tituloDoc.setSpacingAfter(15f);
        document.add(tituloDoc);

        String intro = "Yo, " + (responsableNombre != null && !responsableNombre.isEmpty() ? responsableNombre : "________________________________________")
                + " (representante legal o tutor/a de " + (nombre != null && !nombre.isEmpty() ? nombre : "________________________________________")
                + (responsableIdentidad != null && !responsableIdentidad.isEmpty() ? ") que me identifico con el documento de identidad " + responsableIdentidad : ")")
                + (domicilio != null && !domicilio.isEmpty() ? " y que resido en el domicilio de " + domicilio : " y que resido en el domicilio de ________________________________________________")
                + " por medio del presente documento hago constar lo siguiente.";
        Paragraph pIntro = new Paragraph(intro, fontNormal);
        pIntro.setAlignment(Element.ALIGN_JUSTIFIED);
        pIntro.setLeading(22f);
        pIntro.setSpacingAfter(10f);
        document.add(pIntro);

        String[] puntos = {
            "Que he acudido a la clínica ________________________________________________ donde he sido atendido por ________________________________",
            "Me han explicado de forma clara el diagnóstico de la enfermedad, que padezco, así como su evolución y los daños que ha generado; al igual que las alternativas de tratamiento para dicha enfermedad.",
            "He aceptado que el tratamiento que se me pratique sea ________________________________________________",
            "He entendido los posibles riesgos y complicaciones del plan de tratamiento propuesto para mi caso particular.",
            "Se me ha explicado que para poder ser intervenido debo presentar los exámenes de laboratorio que el profesional de cirugía bucal crea necesarios.",
            "He aceptado que se me practiquen tomas de imágenes como radiografías, fotografías o videos del procedimiento, y se me ha garantizado que se guardará mi identidad o la de mi representado/a, usándolas para mi expediente médico.",
        };

        com.lowagie.text.List lista = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
        lista.setListSymbol("• ");
        for (String punto : puntos) {
            ListItem item = new ListItem(punto, fontNormal);
            item.setLeading(20f);
            item.setSpacingAfter(6f);
            lista.add(item);
        }

        ListItem itemComplicaciones = new ListItem("Comprendo que en el área de cirugía bucal todos los procedimientos no están exentos de las complicaciones frecuentes como lo son:", fontNormal);
        itemComplicaciones.setLeading(20f);
        lista.add(itemComplicaciones);
        document.add(lista);

        com.lowagie.text.List subLista = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
        subLista.setListSymbol("   ○ ");
        String[] complicaciones = {
            "Riesgo de la técnica de anestesia que se utiliza",
            "Posible inflamación y dolor del área afectada en la intervención",
            "Dificultad para abrir la boca y masticar; después del procedimiento",
            "Riesgo de hemorragia y aparición de hematomas en zonas adyacentes",
            "Infección de las heridas quirúrgicas",
            "Posibilidad de pérdida de sensibilidad temporal o no",
            "Riesgo de fracturas óseas, sinusitis, dislocación mandibular, comunicación bucosinusal."
        };
        for (String c : complicaciones) {
            ListItem subItem = new ListItem(c, fontNormal);
            subItem.setLeading(18f);
            subLista.add(subItem);
        }
        document.add(subLista);

        com.lowagie.text.List listaFinal = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
        listaFinal.setListSymbol("• ");
        ListItem itemInd = new ListItem("Estoy enterado que se me darán indicaciones pre y post operatorias, según convenga a mi caso en particular, así como la indicación farmacológica que pueda requerir antes o después de la intervención.", fontNormal);
        itemInd.setLeading(20f);
        listaFinal.add(itemInd);
        ListItem itemConf = new ListItem("Confirmo que se me han aclarado todas mis dudas en torno al procedimiento quirúrgico que se me ha propuesto y me comprometo a cumplir con las indicaciones, las citas necesarias y los controles pertinentes.", fontNormal);
        itemConf.setLeading(20f);
        listaFinal.add(itemConf);
        document.add(listaFinal);

        Paragraph pConsentimiento = new Paragraph("Doy mi consentimiento y autorización para la intervención descrita anteriormente, sin coacción ni manipulación de ningún tipo.", fontNormal);
        pConsentimiento.setAlignment(Element.ALIGN_JUSTIFIED);
        pConsentimiento.setLeading(20f);
        pConsentimiento.setSpacingBefore(15f);
        document.add(pConsentimiento);

        agregarFirmasConsentimiento(document, fontBold);
    }

    // ═══════════════════════════════════════════════════
    // CONSENTIMIENTO INFORMADO - ENDODONCIA (2 PÁGINAS)
    // ═══════════════════════════════════════════════════
    private static void generarConsentimientoEndodoncia(Document document, Font fontNormal, Font fontBold,
                                                         Font fontSectionTitle, String nombre, String identidad, String edad, String domicilio) throws Exception {

        Paragraph tituloDoc = new Paragraph("CONSENTIMIENTO INFORMADO PARA TRATAMIENTO DE ENDODONCIA", fontSectionTitle);
        tituloDoc.setAlignment(Element.ALIGN_CENTER);
        tituloDoc.setSpacingBefore(15f);
        tituloDoc.setSpacingAfter(15f);
        document.add(tituloDoc);

        String intro = "Yo, " + (nombre != null && !nombre.isEmpty() ? nombre : "________________________________________")
                + " que me identifico con el documento de identidad "
                + (identidad != null && !identidad.isEmpty() ? identidad : "____________________")
                + (domicilio != null && !domicilio.isEmpty() ? " y que resido en el domicilio de " + domicilio : " y que resido en el domicilio de ________________________________________________")
                + " por medio del presente documento hago constar lo siguiente.";
        Paragraph pIntro = new Paragraph(intro, fontNormal);
        pIntro.setAlignment(Element.ALIGN_JUSTIFIED);
        pIntro.setLeading(22f);
        pIntro.setSpacingAfter(10f);
        document.add(pIntro);

        String[] puntos = {
            "Que he acudido a la clínica ________________________________________________ donde he sido atendido por ________________________________",
            "Me han explicado de forma clara el diagnóstico de la enfermedad, que padezco, así como su evolución y los daños que ha generado; al igual que las alternativas de tratamiento para dicha enfermedad.",
            "He aceptado que el tratamiento que se me pratique sea ________________________________________________",
            "He entendido los posibles riesgos y complicaciones del plan de tratamiento propuesto para mi caso particular.",
            "He aceptado que se me practiquen tomas de imágenes como radiografías, fotografías o videos del procedimiento, y se me ha garantizado que se guardará mi identidad o la de mi representado/a, usándolas para mi expediente médico.",
        };

        com.lowagie.text.List lista = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
        lista.setListSymbol("• ");
        for (String punto : puntos) {
            ListItem item = new ListItem(punto, fontNormal);
            item.setLeading(20f);
            item.setSpacingAfter(6f);
            lista.add(item);
        }

        ListItem itemRiesgos = new ListItem("Comprendo que en el área de odontología todos los procedimientos no están exentos de riesgos, y algunos de los accidentes que pueden ocurrir durante el procedimiento son:", fontNormal);
        itemRiesgos.setLeading(20f);
        lista.add(itemRiesgos);
        document.add(lista);

        com.lowagie.text.List subLista = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
        subLista.setListSymbol("   ○ ");
        String[] riesgos = {
            "Perforación radicular.",
            "Riesgo de extrusión de sustancia desinfectante.",
            "Fractura de instrumental dentro de conducto.",
            "Sobre obturación o extrusión de material sellante del conducto."
        };
        for (String r : riesgos) {
            ListItem subItem = new ListItem(r, fontNormal);
            subItem.setLeading(18f);
            subLista.add(subItem);
        }
        document.add(subLista);

        com.lowagie.text.List listaPost = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
        listaPost.setListSymbol("• ");
        ListItem itemPost = new ListItem("Se me indica que hay sintomatología post-endodoncia.", fontNormal);
        itemPost.setLeading(20f);
        listaPost.add(itemPost);
        document.add(listaPost);

        com.lowagie.text.List subListaPost = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
        subListaPost.setListSymbol("   ○ ");
        String[] postSintomas = {
            "Posible inflamación y dolor del área afectada en la intervención, post operatorio.",
            "Dificultad para abrir la boca y masticar; después del procedimiento.",
            "Dolor al morder."
        };
        for (String s : postSintomas) {
            ListItem subItem = new ListItem(s, fontNormal);
            subItem.setLeading(18f);
            subListaPost.add(subItem);
        }
        document.add(subListaPost);

        com.lowagie.text.List listaFinal = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
        listaFinal.setListSymbol("• ");
        ListItem itemInd = new ListItem("Estoy enterado que se me darán indicaciones pre y post operatorias, según convenga a mi caso en particular, así como la indicación farmacológica que pueda requerir antes o después de la intervención.", fontNormal);
        itemInd.setLeading(20f);
        listaFinal.add(itemInd);
        ListItem itemConf = new ListItem("Confirmo que se me han aclarado todas mis dudas en torno al procedimiento de endodoncia que se me ha propuesto y me comprometo a cumplir con las indicaciones, las citas necesarias y los controles pertinentes.", fontNormal);
        itemConf.setLeading(20f);
        listaFinal.add(itemConf);
        document.add(listaFinal);

        Paragraph pConsentimiento = new Paragraph("Doy mi consentimiento y autorización para la intervención descrita anteriormente, sin coacción ni manipulación de ningún tipo.", fontNormal);
        pConsentimiento.setAlignment(Element.ALIGN_JUSTIFIED);
        pConsentimiento.setLeading(20f);
        pConsentimiento.setSpacingBefore(15f);
        document.add(pConsentimiento);

        agregarFirmasConsentimiento(document, fontBold);
    }

    // ═══════════════════════════════════════════════════
    // FIRMAS (COMÚN PARA CONSENTIMIENTOS)
    // ═══════════════════════════════════════════════════
    private static void agregarFirmasConsentimiento(Document document, Font fontBold) throws Exception {
        PdfPTable firmasTable = new PdfPTable(2);
        firmasTable.setWidthPercentage(100);
        firmasTable.setSpacingBefore(40f);

        PdfPCell pacienteCell = new PdfPCell();
        pacienteCell.setBorder(Rectangle.NO_BORDER);
        pacienteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pacienteCell.addElement(new Phrase("______________________________\nFirma de paciente", fontBold));

        PdfPCell doctorCell = new PdfPCell();
        doctorCell.setBorder(Rectangle.NO_BORDER);
        doctorCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        doctorCell.addElement(new Phrase("______________________________\nFirma de doctor/a", fontBold));

        firmasTable.addCell(pacienteCell);
        firmasTable.addCell(doctorCell);
        document.add(firmasTable);

        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd / MM / yyyy"));
        Paragraph fechaFirma = new Paragraph("______________________________\nFecha: " + fechaActual, fontBold);
        fechaFirma.setAlignment(Element.ALIGN_CENTER);
        fechaFirma.setSpacingBefore(40f);
        document.add(fechaFirma);
    }
}
