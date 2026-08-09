package application.controller;

public class FinanzasController extends BaseController {

    public FinanzasController() {
        super();
    }

    // =========================================================
    // MÉTODOS DE EGRESOS (existentes)
    // =========================================================

    public String registrarEgreso(String jsonEgreso) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonEgreso).getAsJsonObject();
            String fecha = obj.has("fecha") ? obj.get("fecha").getAsString() : "";
            String descripcion = obj.has("descripcion") ? obj.get("descripcion").getAsString() : "";
            double monto = obj.has("monto") ? obj.get("monto").getAsDouble() : 0.0;
            String numeroComprobante = obj.has("numero_comprobante") ? obj.get("numero_comprobante").getAsString() : "";
            int idCajaSesion = obj.has("id_caja_sesion") ? obj.get("id_caja_sesion").getAsInt() : 0;
            int idUsuario = obj.has("id_usuario") ? obj.get("id_usuario").getAsInt() : 0;
            String metodoPago = obj.has("metodo_pago") ? obj.get("metodo_pago").getAsString() : "Efectivo";

            application.model.dao.EgresoGastoDAO dao = new application.model.dao.EgresoGastoDAO();
            boolean exito = dao.registrarEgreso(idCajaSesion, idUsuario, fecha, descripcion, monto, metodoPago, numeroComprobante);
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
            int idCajaSesion = obj.has("id_caja_sesion") ? obj.get("id_caja_sesion").getAsInt() : 0;
            int idUsuario = obj.has("id_usuario") ? obj.get("id_usuario").getAsInt() : 0;
            String metodoPago = obj.has("metodo_pago") ? obj.get("metodo_pago").getAsString() : "Efectivo";

            application.model.dao.EgresoGastoDAO dao = new application.model.dao.EgresoGastoDAO();
            boolean exito = dao.actualizarEgreso(id, idCajaSesion, idUsuario, fecha, descripcion, monto, metodoPago, numeroComprobante);
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

    // =========================================================
    // MÉTODOS DE FACTURACIÓN / RECIBOS (nuevos)
    // =========================================================

    /**
     * Registra un nuevo recibo en Facturacion_Recibos.
     * JSON esperado: { numero_recibo, id_paciente, rtn_cliente, fecha_emision,
     *                  concepto, suma_neta, total_honorarios, total_retenido,
     *                  total_neto_recibido, metodo_pago }
     */
    public String registrarRecibo(String jsonRecibo) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonRecibo).getAsJsonObject();
            String numeroRecibo      = obj.has("numero_recibo")      ? obj.get("numero_recibo").getAsString()      : "";
            int    idPaciente        = obj.has("id_paciente")        ? obj.get("id_paciente").getAsInt()           : -1;
            int    idCajaSesion      = obj.has("id_caja_sesion")     ? obj.get("id_caja_sesion").getAsInt()        : 0;
            int    idUsuario         = obj.has("id_usuario")         ? obj.get("id_usuario").getAsInt()            : 0;
            String rtnCliente        = obj.has("rtn_cliente")        ? obj.get("rtn_cliente").getAsString()        : "";
            String fechaEmision      = obj.has("fecha_emision")      ? obj.get("fecha_emision").getAsString()      : "";
            String concepto          = obj.has("concepto")           ? obj.get("concepto").getAsString()           : "";
            double sumaNeta          = obj.has("suma_neta")          ? obj.get("suma_neta").getAsDouble()          : 0.0;
            double totalHonorarios   = obj.has("total_honorarios")   ? obj.get("total_honorarios").getAsDouble()   : 0.0;
            double totalRetenido     = obj.has("total_retenido")     ? obj.get("total_retenido").getAsDouble()     : 0.0;
            double totalNetoRecibido = obj.has("total_neto_recibido")? obj.get("total_neto_recibido").getAsDouble(): 0.0;
            String metodoPago        = obj.has("metodo_pago")        ? obj.get("metodo_pago").getAsString()        : "Efectivo";

            if (idPaciente <= 0) return "ERR|Debe seleccionar un paciente válido.";
            if (fechaEmision.isEmpty()) return "ERR|La fecha de emisión es obligatoria.";
            if (concepto.isEmpty())     return "ERR|El concepto es obligatorio.";

            application.model.dao.FacturacionDAO dao = new application.model.dao.FacturacionDAO();
            boolean exito = dao.registrarRecibo(numeroRecibo, idPaciente, idCajaSesion, idUsuario, rtnCliente, fechaEmision,
                    concepto, sumaNeta, totalHonorarios, totalRetenido, totalNetoRecibido, metodoPago);
            return exito ? "OK|Recibo registrado con éxito." : "ERR|Ocurrió un error al registrar el recibo.";
        } catch (Throwable t) {
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

    /**
     * Retorna el listado de recibos activos como JSON array.
     */
    public String obtenerRecibos() {
        try {
            application.model.dao.FacturacionDAO dao = new application.model.dao.FacturacionDAO();
            java.util.List<java.util.Map<String, Object>> lista = dao.obtenerRecibos();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerRecibos: " + t.getMessage());
            return "[]";
        }
    }



    /**
     * Anula lógicamente un recibo (borrado = 'Si').
     * @param idStr ID del recibo como String.
     */
    public String anularRecibo(String idStr) {
        try {
            int id = Integer.parseInt(idStr.trim());
            application.model.dao.FacturacionDAO dao = new application.model.dao.FacturacionDAO();
            boolean exito = dao.anularRecibo(id);
            return exito ? "OK|Recibo anulado correctamente." : "ERR|No se pudo anular el recibo.";
        } catch (Throwable t) {
            return "ERR|Error: " + t.getMessage();
        }
    }

    /**
     * Retorna los pacientes activos para poblar el selector del formulario.
     */
    public String obtenerPacientesParaFactura() {
        try {
            application.model.dao.FacturacionDAO dao = new application.model.dao.FacturacionDAO();
            java.util.List<java.util.Map<String, Object>> lista = dao.obtenerPacientesActivos();
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerPacientesParaFactura: " + t.getMessage());
            return "[]";
        }
    }

    // =========================================================
    // BÚSQUEDA CON FILTROS — FACTURACIÓN
    // JSON esperado: { "termino": "...", "fechaDesde": "YYYY-MM-DD", "fechaHasta": "YYYY-MM-DD" }
    // Todos los campos son opcionales; si vienen vacíos se ignoran.
    // =========================================================
    public String buscarRecibos(String jsonFiltros) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(
                    jsonFiltros == null ? "{}" : jsonFiltros).getAsJsonObject();

            String termino    = obj.has("termino")    ? obj.get("termino").getAsString().trim()    : "";
            String fechaDesde = obj.has("fechaDesde") ? obj.get("fechaDesde").getAsString().trim() : "";
            String fechaHasta = obj.has("fechaHasta") ? obj.get("fechaHasta").getAsString().trim() : "";

            application.model.dao.FacturacionDAO dao = new application.model.dao.FacturacionDAO();
            java.util.List<java.util.Map<String, Object>> lista =
                    dao.buscarRecibos(
                        termino.isEmpty()    ? null : termino,
                        fechaDesde.isEmpty() ? null : fechaDesde,
                        fechaHasta.isEmpty() ? null : fechaHasta
                    );
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en buscarRecibos: " + t.getMessage());
            return "[]";
        }
    }

    // =========================================================
    // BÚSQUEDA CON FILTROS — EGRESOS
    // JSON esperado: { "termino": "...", "fechaDesde": "YYYY-MM-DD", "fechaHasta": "YYYY-MM-DD" }
    // =========================================================
    public String buscarEgresos(String jsonFiltros) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(
                    jsonFiltros == null ? "{}" : jsonFiltros).getAsJsonObject();

            String termino    = obj.has("termino")    ? obj.get("termino").getAsString().trim()    : "";
            String fechaDesde = obj.has("fechaDesde") ? obj.get("fechaDesde").getAsString().trim() : "";
            String fechaHasta = obj.has("fechaHasta") ? obj.get("fechaHasta").getAsString().trim() : "";

            application.model.dao.EgresoGastoDAO dao = new application.model.dao.EgresoGastoDAO();
            java.util.List<java.util.Map<String, Object>> lista =
                    dao.buscarEgresos(
                        termino.isEmpty()    ? null : termino,
                        fechaDesde.isEmpty() ? null : fechaDesde,
                        fechaHasta.isEmpty() ? null : fechaHasta
                    );
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en buscarEgresos: " + t.getMessage());
            return "[]";
        }
    }
}

