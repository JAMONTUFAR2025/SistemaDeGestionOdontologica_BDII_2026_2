package application.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public abstract class BaseController {
    protected Gson gson;
    
    // Variables compartidas (estáticas) para mantener la sesión actual
    protected static String rolUsuarioActual = "";
    protected static Integer idPersonalMedicoActual = null;
    protected static Integer idUsuarioLoginActual = null;

    /** Limpia todos los datos de sesión (llamar al hacer logout) */
    public static void clearSession() {
        rolUsuarioActual = "";
        idPersonalMedicoActual = null;
        idUsuarioLoginActual = null;
    }

    public BaseController() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new com.google.gson.TypeAdapter<LocalDate>() {
                    @Override
                    public void write(com.google.gson.stream.JsonWriter jsonWriter, LocalDate localDate)
                            throws java.io.IOException {
                        if (localDate == null) {
                            jsonWriter.nullValue();
                        } else {
                            jsonWriter.value(localDate.toString());
                        }
                    }

                    @Override
                    public LocalDate read(com.google.gson.stream.JsonReader jsonReader) throws java.io.IOException {
                        if (jsonReader.peek() == com.google.gson.stream.JsonToken.NULL) {
                            jsonReader.nextNull();
                            return null;
                        }
                        String dateStr = jsonReader.nextString();
                        if (dateStr == null || dateStr.trim().isEmpty()) {
                            return null;
                        }
                        try {
                            return LocalDate.parse(dateStr);
                        } catch (java.time.format.DateTimeParseException e) {
                            try {
                                return LocalDate.parse(dateStr,
                                        java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                            } catch (Exception ex) {
                                try {
                                    return LocalDate.parse(dateStr,
                                            java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy"));
                                } catch (Exception ex2) {
                                    try {
                                        java.time.format.DateTimeFormatter fmtEn = new java.time.format.DateTimeFormatterBuilder()
                                                .parseCaseInsensitive().appendPattern("dd-MMM-yyyy").toFormatter(java.util.Locale.ENGLISH);
                                        return LocalDate.parse(dateStr, fmtEn);
                                    } catch (Exception ex3) {
                                        java.time.format.DateTimeFormatter fmtEs = new java.time.format.DateTimeFormatterBuilder()
                                                .parseCaseInsensitive().appendPattern("dd-MMM-yyyy").toFormatter(new java.util.Locale("es", "ES"));
                                        return LocalDate.parse(dateStr, fmtEs);
                                    }
                                }
                            }
                        }
                    }
                })
                .registerTypeAdapter(java.time.LocalDateTime.class,
                        new com.google.gson.TypeAdapter<java.time.LocalDateTime>() {
                            @Override
                            public void write(com.google.gson.stream.JsonWriter jsonWriter,
                                    java.time.LocalDateTime localDateTime) throws java.io.IOException {
                                if (localDateTime == null) {
                                    jsonWriter.nullValue();
                                } else {
                                    jsonWriter.value(localDateTime.toString());
                                }
                            }

                            @Override
                            public java.time.LocalDateTime read(com.google.gson.stream.JsonReader jsonReader)
                                    throws java.io.IOException {
                                if (jsonReader.peek() == com.google.gson.stream.JsonToken.NULL) {
                                    jsonReader.nextNull();
                                    return null;
                                }
                                String dateStr = jsonReader.nextString();
                                if (dateStr == null || dateStr.trim().isEmpty()) {
                                    return null;
                                }
                                return parseDateTimeRobust(dateStr);
                            }
                        })
                .create();
    }

    protected static java.time.LocalDateTime parseDateTimeRobust(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        str = str.trim();
        // Arreglar si el frontend pegó un ":00" después del AM/PM (ej: 01:30 PM:00 -> 01:30 PM)
        if (str.toUpperCase().endsWith("PM:00") || str.toUpperCase().endsWith("AM:00")) {
            str = str.substring(0, str.length() - 3);
        }
        
        java.time.format.DateTimeFormatter[] formatters = {
            java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            new java.time.format.DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MMM-yyyy HH:mm:ss").toFormatter(java.util.Locale.ENGLISH),
            new java.time.format.DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MMM-yyyy hh:mm a").toFormatter(java.util.Locale.ENGLISH),
            new java.time.format.DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MMM-yyyy HH:mm:ss").toFormatter(new java.util.Locale("es", "ES")),
            new java.time.format.DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MMM-yyyy hh:mm a").toFormatter(new java.util.Locale("es", "ES")),
            new java.time.format.DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MM-yyyy HH:mm:ss").toFormatter(java.util.Locale.ENGLISH),
            new java.time.format.DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MM-yyyy hh:mm a").toFormatter(java.util.Locale.ENGLISH)
        };
        
        for (java.time.format.DateTimeFormatter fmt : formatters) {
            try {
                return java.time.LocalDateTime.parse(str, fmt);
            } catch (Exception ignored) {}
        }
        
        try {
            return java.time.LocalDateTime.parse(str.replace(" ", "T"));
        } catch (Exception ignored) {}
        
        throw new java.time.format.DateTimeParseException("Text '" + str + "' could not be parsed", str, 0);
    }
}
