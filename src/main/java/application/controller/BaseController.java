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
                                return LocalDate.parse(dateStr,
                                        java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy"));
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
                                return java.time.LocalDateTime.parse(dateStr);
                            }
                        })
                .create();
    }
}
