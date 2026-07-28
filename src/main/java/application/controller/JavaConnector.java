package application.controller;

import application.model.dao.PacienteDAO;
import application.model.dao.UserDAO;
import application.model.entity.Paciente;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.time.LocalDate;

public class JavaConnector {
    // Force VS Code to recompile
    private UserDAO userDAO;
    private PacienteDAO pacienteDAO;
    private Gson gson;

    public JavaConnector() {
        this.userDAO = new UserDAO();
        this.pacienteDAO = new PacienteDAO();
        // Configuramos Gson para manejar LocalDate (formato yyyy-MM-dd que viene del HTML input date)
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new com.google.gson.TypeAdapter<LocalDate>() {
                    @Override
                    public void write(com.google.gson.stream.JsonWriter jsonWriter, LocalDate localDate) throws java.io.IOException {
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
                            // Intenta parsear dd-MM-yyyy o MM-dd-yyyy si el navegador envia otro formato
                            try {
                                return LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                            } catch (Exception ex) {
                                return LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy"));
                            }
                        }
                    }
                })
                .registerTypeAdapter(java.time.LocalDateTime.class, new com.google.gson.TypeAdapter<java.time.LocalDateTime>() {
                    @Override
                    public void write(com.google.gson.stream.JsonWriter jsonWriter, java.time.LocalDateTime localDateTime) throws java.io.IOException {
                        if (localDateTime == null) {
                            jsonWriter.nullValue();
                        } else {
                            jsonWriter.value(localDateTime.toString());
                        }
                    }
                    @Override
                    public java.time.LocalDateTime read(com.google.gson.stream.JsonReader jsonReader) throws java.io.IOException {
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

    // Retorna true si las credenciales son correctas
    public boolean login(String correo, String contrasenia) {
        System.out.println("Intentando iniciar sesion con: " + correo);
        boolean exito = userDAO.autenticarUsuario(correo, contrasenia);
        if (exito) {
            System.out.println("-> Inicio de sesion EXITOSO para: " + correo);
        } else {
            System.out.println("-> Fila no encontrada o credenciales INCORRECTAS para: " + correo);
        }
        return exito;
    }

    // Mapa para almacenar los codigos de seguridad temporalmente (Correo -> Codigo)
    private java.util.Map<String, String> codigosRecuperacion = new java.util.HashMap<>();

    // Paso 1: Generar y enviar el codigo de recuperacion
    public String enviarCodigoRecuperacion(String correo) {
        System.out.println("Solicitud de codigo para: " + correo);
        
        boolean existe = userDAO.verificarCorreoExistente(correo);
        if (existe) {
            // 1. Generar nuevo codigo aleatorio de 6 digitos
            String codigoSeguridad = String.format("%06d", (int)(Math.random() * 1000000));
            
            // 2. Guardarlo en memoria (no en la base de datos)
            codigosRecuperacion.put(correo, codigoSeguridad);

            // 3. Enviar el correo
            System.out.println("-> Enviando correo a " + correo + "...");
            boolean enviado = application.model.connection.EmailService.enviarCorreoNuevaContrasenia(correo, codigoSeguridad);
            if (enviado) {
                return "OK|Código enviado con éxito. Revisa tu bandeja de entrada.";
            } else {
                return "ERR|El código se generó, pero hubo un error al enviar el correo.";
            }
        } else {
            return "ERR|No se encontro ningun usuario activo con ese correo.";
        }
    }

    // Paso 2: Verificar que el usuario ingresó el código correcto
    public String verificarCodigo(String correo, String codigoIngresado) {
        String codigoReal = codigosRecuperacion.get(correo);
        if (codigoReal != null && codigoReal.equals(codigoIngresado)) {
            return "OK|Código verificado correctamente.";
        } else {
            return "ERR|El código ingresado es incorrecto o ha expirado.";
        }
    }

    // Paso 3: Guardar la nueva contraseña en la base de datos
    public String restablecerContrasenia(String correo, String codigoIngresado, String nuevaContrasenia) {
        // Doble verificacion por seguridad
        String codigoReal = codigosRecuperacion.get(correo);
        if (codigoReal == null || !codigoReal.equals(codigoIngresado)) {
            return "ERR|Intento inválido de cambio de contraseña.";
        }

        // Actualizar en la base de datos
        boolean actualizado = userDAO.actualizarContrasenia(correo, nuevaContrasenia);
        
        if (actualizado) {
            codigosRecuperacion.remove(correo); // Borrar el codigo de la memoria por seguridad
            return "OK|Contraseña actualizada exitosamente. Ya puedes iniciar sesión.";
        } else {
            return "ERR|Ocurrió un error al guardar la nueva contraseña en la base de datos.";
        }
    }
    // Retorna un mensaje indicando el resultado del guardado
    public String registrarPaciente(String jsonPaciente) {
        System.out.println("-> Peticion de registro recibida en Java: " + jsonPaciente);
        try {
            Paciente paciente = gson.fromJson(jsonPaciente, Paciente.class);
            System.out.println("-> Gson deserializo correctamente a: " + paciente.getNombreCompleto());
            
            boolean exito = pacienteDAO.registrar(paciente);
            if (exito) {
                System.out.println("-> Paciente insertado en DB con exito.");
                return "OK|Paciente registrado exitosamente.";
            } else {
                System.out.println("-> Error en la insercion DB (PacienteDAO retorno false).");
                return "ERR|No se pudo registrar el paciente en la base de datos.";
            }
        } catch (Exception e) {
            System.err.println("-> ERROR procesando paciente: " + e.getMessage());
            e.printStackTrace();
            return "ERR|Error al procesar los datos del formulario.";
        }
    }
        
    // ==========================================
    // METODOS DE PERSONAL MEDICO
    // ==========================================

    public String obtenerEspecialidades() {
        System.out.println("-> JavaConnector: solicitando especialidades...");
        application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
        java.util.List<application.model.dao.Especialidad> lista = pmDAO.obtenerEspecialidades();
        System.out.println("-> Especialidades encontradas: " + lista.size());
        return gson.toJson(lista);
    }

    public String registrarPersonalMedico(String jsonPersonal) {
        System.out.println("-> Peticion de registro de medico en Java: " + jsonPersonal);
        try {
            application.model.dao.PersonalMedico pm = gson.fromJson(jsonPersonal, application.model.dao.PersonalMedico.class);
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            
            boolean exito = pmDAO.registrarPersonalYUsuario(pm);
            if (exito) {
                return "OK|Personal y usuario registrados con éxito en la base de datos.";
            } else {
                return "ERR|Ocurrió un error al registrar. Es posible que el correo o identidad ya existan.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR|Error procesando datos: " + e.getMessage();
        }
    }

    public String obtenerCorreosActivos() {
        try {
            java.util.List<String> correos = userDAO.obtenerCorreosActivos();
            return gson.toJson(correos);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerCorreosActivos: " + t.getMessage());
            return "[]";
        }
    }

    // Registrar solo un usuario (sin médico vinculado)
    public String registrarUsuarioSolo(String jsonUsuario) {
        System.out.println("-> Peticion de registro de usuario en Java: " + jsonUsuario);
        System.out.flush();
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonUsuario).getAsJsonObject();
            String correo = obj.get("correo").getAsString();
            String contrasenia = obj.get("contrasenia").getAsString();
            String rolSistema = obj.get("rol_sistema").getAsString();
            String estado = obj.get("estado").getAsString();
            
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            boolean exito = pmDAO.registrarUsuarioSolo(correo, contrasenia, rolSistema, estado);
            if (exito) {
                return "OK|Usuario registrado exitosamente.";
            } else {
                return "ERR|No se pudo registrar. Es posible que el correo ya exista.";
            }
        } catch (Throwable t) {
            System.err.println("-> ERROR en registrarUsuarioSolo: " + t.getClass().getName() + " - " + t.getMessage());
            t.printStackTrace();
            System.err.flush();
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

    // Obtener usuarios que aún no tienen médico vinculado
    public String obtenerUsuariosSinMedico() {
        try {
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            java.util.List<java.util.Map<String, String>> lista = pmDAO.obtenerUsuariosSinMedico();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerUsuariosSinMedico: " + t.getClass().getName() + " - " + t.getMessage());
            t.printStackTrace();
            System.err.flush();
            return "[]";
        }
    }

    // Registrar médico y vincularlo a un usuario existente
    public String registrarMedicoConUsuario(String jsonMedico) {
        System.out.println("-> Peticion de registro de medico vinculado en Java: " + jsonMedico);
        System.out.flush();
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonMedico).getAsJsonObject();
            int idUsuario = obj.get("id_usuario").getAsInt();
            
            application.model.dao.PersonalMedico pm = gson.fromJson(jsonMedico, application.model.dao.PersonalMedico.class);
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            
            boolean exito = pmDAO.registrarPersonalYVincular(pm, idUsuario);
            if (exito) {
                return "OK|Médico registrado y vinculado al usuario exitosamente.";
            } else {
                return "ERR|Error al registrar. Verifica que la identidad no exista ya.";
            }
        } catch (Throwable t) {
            System.err.println("-> ERROR en registrarMedicoConUsuario: " + t.getClass().getName() + " - " + t.getMessage());
            t.printStackTrace();
            System.err.flush();
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }
}
