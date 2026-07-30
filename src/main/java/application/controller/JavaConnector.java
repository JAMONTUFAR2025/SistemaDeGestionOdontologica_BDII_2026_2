package application.controller;

import application.model.dao.PacienteDAO;
import application.model.dao.UserDAO;
import application.model.entity.Paciente;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public class JavaConnector {
    // Force VS Code to recompile
    private UserDAO userDAO;
    private PacienteDAO pacienteDAO;
    private Gson gson;
    
    // NUEVO: Variable para almacenar el rol del usuario que inició sesión
    private String rolUsuarioActual = "";
    private Integer idPersonalMedicoActual = null;

    public JavaConnector() {
        this.userDAO = new UserDAO();
        this.pacienteDAO = new PacienteDAO();
        // Configuramos Gson para manejar LocalDate (formato yyyy-MM-dd que viene del HTML input date)
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
                            // Intenta parsear dd-MM-yyyy o MM-dd-yyyy si el navegador envia otro formato
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

    // ==========================================
    // MÉTODO NUEVO PARA ENVIAR EL ROL AL FRONTEND
    // ==========================================
    public String obtenerRolActual() {
        return this.rolUsuarioActual;
    }

    // MODIFICADO: Retorna true si las credenciales son correctas y guarda el rol e id
    public boolean login(String correo, String contrasenia) {
        System.out.println("Intentando iniciar sesion con: " + correo);
        boolean exito = userDAO.autenticarUsuario(correo, contrasenia);
        if (exito) {
            System.out.println("-> Inicio de sesion EXITOSO para: " + correo);
            // Consultamos a la base de datos qué rol y qué id_personal_medico tiene este usuario
            this.rolUsuarioActual = userDAO.obtenerRolPorCorreo(correo); 
            this.idPersonalMedicoActual = userDAO.obtenerIdMedicoPorCorreo(correo);
        } else {
            System.out.println("-> Fila no encontrada o credenciales INCORRECTAS para: " + correo);
            this.rolUsuarioActual = ""; // Limpiamos por seguridad
            this.idPersonalMedicoActual = null;
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
            String codigoSeguridad = String.format("%06d", (int) (Math.random() * 1000000));

            // 2. Guardarlo en memoria (no en la base de datos)
            codigosRecuperacion.put(correo, codigoSeguridad);

            // 3. Enviar el correo
            System.out.println("-> Enviando correo a " + correo + "...");
            boolean enviado = application.model.connection.EmailService.enviarCorreoNuevaContrasenia(correo,
                    codigoSeguridad);
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

    // Retorna un mensaje indicando el resultado del guardado (inserta o actualiza)
    public String registrarPaciente(String jsonPaciente) {
        System.out.println("-> Petición de registro/actualización de paciente recibida en Java: " + jsonPaciente);
        try {
            Paciente paciente = gson.fromJson(jsonPaciente, Paciente.class);
            if (paciente == null || paciente.getIdentidad() == null || paciente.getIdentidad().trim().isEmpty()) {
                return "ERR|La identidad (DNI) del paciente es requerida.";
            }
            System.out.println("-> Gson deserializó correctamente a: " + paciente.getNombreCompleto());

            if (pacienteDAO.existe(paciente.getIdentidad())) {
                return pacienteDAO.actualizar(paciente);
            } else {
                return pacienteDAO.registrar(paciente);
            }
        } catch (Exception e) {
            System.err.println("-> ERROR procesando paciente: " + e.getMessage());
            e.printStackTrace();
            return "ERR|Error al procesar los datos del formulario: " + e.getMessage();
        }
    }

    public String obtenerPacientes() {
        try {
            java.util.List<Paciente> lista = pacienteDAO.obtenerPacientes();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerPacientes: " + t.getMessage());
            return "[]";
        }
    }

    public String eliminarPaciente(String identidad) {
        try {
            boolean exito = pacienteDAO.eliminarPaciente(identidad);
            if (exito) {
                return "OK|Paciente eliminado exitosamente.";
            } else {
                return "ERR|No se pudo eliminar el paciente.";
            }
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarPaciente: " + t.getMessage());
            return "ERR|Error al eliminar paciente: " + t.getMessage();
        }
    }

    // ==========================================
    // METODOS DE PERSONAL MEDICO
    // ==========================================

    public String obtenerEspecialidades() {
        System.out.println("-> JavaConnector: solicitando especialidades...");
        application.model.dao.EspecialidadDAO especialidadDAO = new application.model.dao.EspecialidadDAO();
        java.util.List<application.model.entity.Especialidad> lista = especialidadDAO.obtenerEspecialidades();
        System.out.println("-> Especialidades encontradas: " + lista.size());
        return gson.toJson(lista);
    }

    // Agregar nueva especialidad
    public String agregarEspecialidad(String nombre) {
        try {
            application.model.dao.EspecialidadDAO especialidadDAO = new application.model.dao.EspecialidadDAO();
            boolean exito = especialidadDAO.agregarEspecialidad(nombre.trim());
            return exito ? "OK|Especialidad registrada correctamente." : "ERR|No se pudo registrar la especialidad.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    // Actualizar nombre de especialidad
    public String actualizarEspecialidad(String idStr, String nombre) {
        try {
            int id = Integer.parseInt(idStr.trim());
            application.model.dao.EspecialidadDAO especialidadDAO = new application.model.dao.EspecialidadDAO();
            boolean exito = especialidadDAO.actualizarEspecialidad(id, nombre.trim());
            return exito ? "OK|Especialidad actualizada correctamente." : "ERR|No se pudo actualizar la especialidad.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    // Eliminar especialidad (borrado físico)
    public String eliminarEspecialidad(String idStr) {
        try {
            int id = Integer.parseInt(idStr.trim());
            application.model.dao.EspecialidadDAO especialidadDAO = new application.model.dao.EspecialidadDAO();
            boolean exito = especialidadDAO.eliminarEspecialidad(id);
            return exito ? "OK|Especialidad eliminada correctamente."
                    : "ERR|No se pudo eliminar. Puede estar en uso por algún médico.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String registrarPersonalMedico(String jsonPersonal) {
        System.out.println("-> Peticion de registro de medico en Java: " + jsonPersonal);
        try {
            application.model.entity.PersonalMedico pm = gson.fromJson(jsonPersonal,
                    application.model.entity.PersonalMedico.class);
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
            System.err.println(
                    "-> ERROR en obtenerUsuariosSinMedico: " + t.getClass().getName() + " - " + t.getMessage());
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

            application.model.entity.PersonalMedico pm = gson.fromJson(jsonMedico,
                    application.model.entity.PersonalMedico.class);
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();

            boolean exito = pmDAO.registrarPersonalYVincular(pm, idUsuario);
            if (exito) {
                return "OK|Médico registrado y vinculado al usuario exitosamente.";
            } else {
                return "ERR|Error al registrar. Verifica que la identidad no exista ya.";
            }
        } catch (Throwable t) {
            System.err.println(
                    "-> ERROR en registrarMedicoConUsuario: " + t.getClass().getName() + " - " + t.getMessage());
            t.printStackTrace();
            System.err.flush();
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

    // ==========================================
    // MÓDULO DE CITAS
    // ==========================================

    public String agendarCita(String jsonCita) {
        System.out.println("-> Peticion para agendar cita: " + jsonCita);
        try {
            application.model.entity.Cita cita = gson.fromJson(jsonCita, application.model.entity.Cita.class);
            // Gson deserializa fechas si están en formato ISO. Para evitar problemas con LocalDateTime:
            // Vamos a extraer la fechaHora manualmente usando JsonObject si falla:
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonCita).getAsJsonObject();
            String fechaHoraStr = obj.get("fecha_hora").getAsString(); 
            // reemplazar espacio por T para formato ISO localdatetime "yyyy-MM-dd HH:mm:ss" -> "yyyy-MM-ddTHH:mm:ss"
            fechaHoraStr = fechaHoraStr.replace(" ", "T");
            cita.setFechaHora(java.time.LocalDateTime.parse(fechaHoraStr));

            // Extraemos los enteros (Gson pudo haber hecho match, pero aseguramos)
            cita.setIdPacientes(obj.get("id_pacientes").getAsInt());
            cita.setIdPersonalMedico(obj.get("id_personal_medico").getAsInt());

            application.model.dao.CitaDAO citaDAO = new application.model.dao.CitaDAO();
            boolean exito = citaDAO.agendarCita(cita);
            
            return exito ? "OK|Cita agendada exitosamente." : "ERR|Error al guardar la cita en la base de datos.";
        } catch (Exception e) {
            System.err.println("Error en agendarCita: " + e.getMessage());
            e.printStackTrace();
            return "ERR|Error interno al procesar la cita: " + e.getMessage();
        }
    }

    public String obtenerCitasHoyDelUsuario() {
        application.model.dao.CitaDAO citaDAO = new application.model.dao.CitaDAO();
        java.util.List<java.util.Map<String, String>> citas = citaDAO.obtenerCitasHoy(this.idPersonalMedicoActual, this.rolUsuarioActual);
        return gson.toJson(citas);
    }

    public String obtenerProximasCitasDelUsuario() {
        application.model.dao.CitaDAO citaDAO = new application.model.dao.CitaDAO();
        java.util.List<java.util.Map<String, String>> citas = citaDAO.obtenerProximasCitas(this.idPersonalMedicoActual, this.rolUsuarioActual);
        return gson.toJson(citas);
    }

    // Obtener todos los usuarios activos (para la tabla del módulo de personal)
    public String obtenerUsuarios() {
        try {
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            java.util.List<java.util.Map<String, Object>> lista = pmDAO.obtenerUsuarios();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerUsuarios: " + t.getMessage());
            return "[]";
        }
    }

    // Obtener todo el personal médico activo (para la tabla del módulo de personal)
    public String obtenerPersonalMedico() {
        try {
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            java.util.List<java.util.Map<String, Object>> lista = pmDAO.obtenerPersonalMedico();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerPersonalMedico: " + t.getMessage());
            return "[]";
        }
    }

    // Actualizar usuario (rol y contraseña opcional)
    public String actualizarUsuario(String jsonUsuario) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonUsuario).getAsJsonObject();
            int idUsuario = obj.get("id_usuario").getAsInt();
            String rolSistema = obj.get("rol_sistema").getAsString();
            String contrasenia = obj.has("contrasenia") ? obj.get("contrasenia").getAsString() : "";
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            boolean exito = pmDAO.actualizarUsuario(idUsuario, rolSistema, contrasenia);
            return exito ? "OK|Usuario actualizado exitosamente." : "ERR|No se pudo actualizar el usuario.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    // Inactivar usuario (borrado lógico)
    public String inactivarUsuario(String idUsuarioStr) {
        try {
            int idUsuario = Integer.parseInt(idUsuarioStr.trim());
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            boolean exito = pmDAO.inactivarUsuario(idUsuario);
            return exito ? "OK|Usuario inactivado correctamente." : "ERR|No se pudo inactivar el usuario.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    // Actualizar personal médico
    public String actualizarPersonalMedico(String jsonMedico) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonMedico).getAsJsonObject();
            String identidad = obj.get("identidad").getAsString();
            String nombreCompleto = obj.get("nombre_completo").getAsString();
            String telefono = obj.get("telefono").getAsString();
            // SchemaActual: campo renombrado a id_especialidades
            int idEspecialidades = obj.has("id_especialidades")
                    ? obj.get("id_especialidades").getAsInt()
                    : (obj.has("id_especialidad") ? obj.get("id_especialidad").getAsInt() : 0);
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            boolean exito = pmDAO.actualizarPersonalMedico(identidad, nombreCompleto, telefono, idEspecialidades);
            return exito ? "OK|Médico actualizado exitosamente." : "ERR|No se pudo actualizar el médico.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    // Inactivar personal médico (borrado lógico)
    public String inactivarPersonalMedico(String identidad) {
        try {
            application.model.dao.PersonalMedicoDAO pmDAO = new application.model.dao.PersonalMedicoDAO();
            boolean exito = pmDAO.inactivarPersonalMedico(identidad);
            return exito ? "OK|Médico inactivado correctamente." : "ERR|No se pudo inactivar el médico.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    // ==========================================
    // MÉTODOS PARA EGRESOS Y GASTOS
    // ==========================================
    public String registrarEgreso(String jsonEgreso) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonEgreso).getAsJsonObject();
            String fecha = obj.has("fecha") ? obj.get("fecha").getAsString() : "";
            String descripcion = obj.has("descripcion") ? obj.get("descripcion").getAsString() : "";
            double monto = obj.has("monto") ? obj.get("monto").getAsDouble() : 0.0;
            String numeroComprobante = obj.has("numero_comprobante") ? obj.get("numero_comprobante").getAsString() : "";

            application.model.dao.EgresoGastoDAO dao = new application.model.dao.EgresoGastoDAO();
            boolean exito = dao.registrarEgreso(fecha, descripcion, monto, numeroComprobante);
            return exito ? "OK|Egreso registrado con éxito." : "ERR|Ocurrió un error al registrar el egreso.";
        } catch (Throwable t) {
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

    public String obtenerEgresos() {
        try {
            application.model.dao.EgresoGastoDAO dao = new application.model.dao.EgresoGastoDAO();
            java.util.List<java.util.Map<String, Object>> lista = dao.obtenerEgresos();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerEgresos: " + t.getMessage());
            return "[]";
        }
    }

    public String actualizarEgreso(String jsonEgreso) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonEgreso).getAsJsonObject();
            int id = obj.get("id_egreso").getAsInt();
            String fecha = obj.has("fecha") ? obj.get("fecha").getAsString() : "";
            String descripcion = obj.has("descripcion") ? obj.get("descripcion").getAsString() : "";
            double monto = obj.has("monto") ? obj.get("monto").getAsDouble() : 0.0;
            String numeroComprobante = obj.has("numero_comprobante") ? obj.get("numero_comprobante").getAsString() : "";

            application.model.dao.EgresoGastoDAO dao = new application.model.dao.EgresoGastoDAO();
            boolean exito = dao.actualizarEgreso(id, fecha, descripcion, monto, numeroComprobante);
            return exito ? "OK|Egreso actualizado con éxito." : "ERR|Ocurrió un error al actualizar el egreso.";
        } catch (Throwable t) {
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

    public String inactivarEgreso(String idStr) {
        try {
            int id = Integer.parseInt(idStr.trim());
            application.model.dao.EgresoGastoDAO dao = new application.model.dao.EgresoGastoDAO();
            boolean exito = dao.inactivarEgreso(id);
            return exito ? "OK|Egreso eliminado correctamente." : "ERR|No se pudo eliminar el egreso.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }
}