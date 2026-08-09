package application.controller;

public class FinanzasController extends BaseController {

    public FinanzasController() {
        super();
    }

    // =========================================================
    // MÉTODOS DE CAJA_SESIONES
    // =========================================================

    /**
     * Retorna la caja actualmente abierta como JSON, o {"abierta": false} si no hay ninguna.
     */
    public String obtenerCajaActiva() {
        try {
            application.model.dao.CajaSesionDAO dao = new application.model.dao.CajaSesionDAO();
            java.util.Map<String, Object> caja = dao.obtenerCajaActiva();
            if (caja == null) {
                return "{\"abierta\":false}";
            }
            caja.put("abierta", true);
            return gson.toJson(caja);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerCajaActiva: " + t.getMessage());
            return "{\"abierta\":false}";
        }
    }

    /**
     * Abre una nueva sesión de caja.
     * JSON esperado: { "monto_apertura": 500.00 }
     */
    public String abrirCaja(String jsonApertura) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonApertura).getAsJsonObject();
            double montoApertura = obj.has("monto_apertura") ? obj.get("monto_apertura").getAsDouble() : 0.0;

            if (idUsuarioLoginActual == null || idUsuarioLoginActual <= 0) {
                return "ERR|No hay un usuario en sesión. Inicia sesión nuevamente.";
            }
            if (montoApertura < 0) {
                return "ERR|El monto de apertura no puede ser negativo.";
            }

            application.model.dao.CajaSesionDAO dao = new application.model.dao.CajaSesionDAO();
            boolean exito = dao.abrirCaja(idUsuarioLoginActual, montoApertura);
            return exito ? "OK|Caja abierta exitosamente."
                        : "ERR|Ya existe una caja abierta en el sistema. Ciérrala antes de abrir una nueva.";
        } catch (Throwable t) {
            return "ERR|Error al abrir caja: " + t.getMessage();
        }
    }

    /**
     * Calcula el arqueo de la caja activa.
     * @param idCajaStr ID de la sesión de caja como String.
     */
    public String calcularArqueoCaja(String idCajaStr) {
        try {
            int idCajaSesion = Integer.parseInt(idCajaStr.trim());
            application.model.dao.CajaSesionDAO dao = new application.model.dao.CajaSesionDAO();
            java.util.Map<String, Object> arqueo = dao.calcularArqueoCaja(idCajaSesion);
            return gson.toJson(arqueo);
        } catch (Throwable t) {
            System.err.println("-> ERROR en calcularArqueoCaja: " + t.getMessage());
            return "{}";
        }
    }

    /**
     * Cierra la caja activa con el monto real contado.
     * JSON esperado: { "id_caja_sesion": 1, "monto_cierre_real": 1250.00, "observaciones": "..." }
     */
    public String cerrarCaja(String jsonCierre) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonCierre).getAsJsonObject();
            int idCajaSesion    = obj.has("id_caja_sesion")    ? obj.get("id_caja_sesion").getAsInt()      : 0;
            double montoCierre  = obj.has("monto_cierre_real") ? obj.get("monto_cierre_real").getAsDouble(): 0.0;
            String observaciones= obj.has("observaciones")     ? obj.get("observaciones").getAsString()    : "";

            if (idUsuarioLoginActual == null || idUsuarioLoginActual <= 0) {
                return "ERR|No hay un usuario en sesión. Inicia sesión nuevamente.";
            }
            if (idCajaSesion <= 0) {
                return "ERR|ID de caja inválido.";
            }

            application.model.dao.CajaSesionDAO dao = new application.model.dao.CajaSesionDAO();
            boolean exito = dao.cerrarCaja(idCajaSesion, idUsuarioLoginActual, montoCierre, observaciones);
            return exito ? "OK|Caja cerrada exitosamente."
                        : "ERR|No se pudo cerrar la caja. Verifique que esté abierta.";
        } catch (Throwable t) {
            return "ERR|Error al cerrar caja: " + t.getMessage();
        }
    }

    /**
     * Retorna los últimos movimientos (cobros + egresos) de la sesión activa.
     */
    public String obtenerMovimientosDeSesion(String idCajaStr) {
        try {
            int idCajaSesion = Integer.parseInt(idCajaStr.trim());
            application.model.dao.CajaSesionDAO dao = new application.model.dao.CajaSesionDAO();
            java.util.List<java.util.Map<String, Object>> lista = dao.obtenerMovimientosDeSesion(idCajaSesion);
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerMovimientosDeSesion: " + t.getMessage());
            return "[]";
        }
    }

    // =========================================================
    // MÉTODOS DE EGRESOS
    // =========================================================

    /**
     * Registra un egreso.
     * JSON esperado: { fecha, descripcion, monto, metodo_pago, numero_comprobante,
     *                  afecta_caja: true/false, id_caja_sesion (si afecta_caja=true) }
     */
    public String registrarEgreso(String jsonEgreso) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonEgreso).getAsJsonObject();
            String fecha             = obj.has("fecha")             ? obj.get("fecha").getAsString()              : "";
            String descripcion       = obj.has("descripcion")       ? obj.get("descripcion").getAsString()        : "";
            double monto             = obj.has("monto")             ? obj.get("monto").getAsDouble()              : 0.0;
            String metodoPago        = obj.has("metodo_pago")       ? obj.get("metodo_pago").getAsString()        : "Efectivo";
            String numeroComprobante = obj.has("numero_comprobante")? obj.get("numero_comprobante").getAsString() : "";
            boolean afectaCaja       = obj.has("afecta_caja") && obj.get("afecta_caja").getAsBoolean();

            Integer idCajaSesion = null;
            if (afectaCaja && obj.has("id_caja_sesion") && !obj.get("id_caja_sesion").isJsonNull()) {
                idCajaSesion = obj.get("id_caja_sesion").getAsInt();
            }

            int idUsuario = (idUsuarioLoginActual != null) ? idUsuarioLoginActual : 0;

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
            int    id                = obj.get("id_egreso").getAsInt();
            String fecha             = obj.has("fecha")             ? obj.get("fecha").getAsString()              : "";
            String descripcion       = obj.has("descripcion")       ? obj.get("descripcion").getAsString()        : "";
            double monto             = obj.has("monto")             ? obj.get("monto").getAsDouble()              : 0.0;
            String metodoPago        = obj.has("metodo_pago")       ? obj.get("metodo_pago").getAsString()        : "Efectivo";
            String numeroComprobante = obj.has("numero_comprobante")? obj.get("numero_comprobante").getAsString() : "";
            boolean afectaCaja       = obj.has("afecta_caja") && obj.get("afecta_caja").getAsBoolean();

            Integer idCajaSesion = null;
            if (afectaCaja && obj.has("id_caja_sesion") && !obj.get("id_caja_sesion").isJsonNull()) {
                idCajaSesion = obj.get("id_caja_sesion").getAsInt();
            }

            int idUsuario = (idUsuarioLoginActual != null) ? idUsuarioLoginActual : 0;

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
    // MÉTODOS DE FACTURACIÓN / RECIBOS
    // =========================================================

    /**
     * Registra un nuevo recibo.
     * JSON esperado: { numero_recibo, id_paciente, id_caja_sesion, rtn_cliente, fecha_emision,
     *                  concepto, suma_neta, total_honorarios, total_retenido,
     *                  total_neto_recibido, metodo_pago }
     */
    public String registrarRecibo(String jsonRecibo) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonRecibo).getAsJsonObject();
            String numeroRecibo      = obj.has("numero_recibo")       ? obj.get("numero_recibo").getAsString()       : "";
            int    idPaciente        = obj.has("id_paciente")         ? obj.get("id_paciente").getAsInt()            : -1;
            int    idCajaSesion      = obj.has("id_caja_sesion")      ? obj.get("id_caja_sesion").getAsInt()         : 0;
            String rtnCliente        = obj.has("rtn_cliente")         ? obj.get("rtn_cliente").getAsString()         : "";
            String fechaEmision      = obj.has("fecha_emision")       ? obj.get("fecha_emision").getAsString()       : "";
            String concepto          = obj.has("concepto")            ? obj.get("concepto").getAsString()            : "";
            double sumaNeta          = obj.has("suma_neta")           ? obj.get("suma_neta").getAsDouble()           : 0.0;
            double totalHonorarios   = obj.has("total_honorarios")    ? obj.get("total_honorarios").getAsDouble()    : 0.0;
            double totalRetenido     = obj.has("total_retenido")      ? obj.get("total_retenido").getAsDouble()      : 0.0;
            double totalNetoRecibido = obj.has("total_neto_recibido") ? obj.get("total_neto_recibido").getAsDouble() : 0.0;
            String metodoPago        = obj.has("metodo_pago")         ? obj.get("metodo_pago").getAsString()         : "Efectivo";

            // id_usuario siempre viene de la sesión activa
            int idUsuario = (idUsuarioLoginActual != null) ? idUsuarioLoginActual : 0;

            if (idPaciente <= 0) return "ERR|Debe seleccionar un paciente válido.";
            if (fechaEmision.isEmpty()) return "ERR|La fecha de emisión es obligatoria.";
            if (concepto.isEmpty())     return "ERR|El concepto es obligatorio.";
            if (idCajaSesion <= 0)      return "ERR|No hay una sesión de caja abierta. Abre la caja antes de registrar un cobro.";

            application.model.dao.FacturacionDAO dao = new application.model.dao.FacturacionDAO();
            boolean exito = dao.registrarRecibo(numeroRecibo, idPaciente, idCajaSesion, idUsuario, rtnCliente,
                    fechaEmision, concepto, sumaNeta, totalHonorarios, totalRetenido, totalNetoRecibido, metodoPago);
            return exito ? "OK|Recibo registrado con éxito." : "ERR|Ocurrió un error al registrar el recibo.";
        } catch (Throwable t) {
            return "ERR|Error procesando datos: " + t.getMessage();
        }
    }

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
    // =========================================================
    public String buscarRecibos(String jsonFiltros) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(
                    jsonFiltros == null ? "{}" : jsonFiltros).getAsJsonObject();
            String termino    = obj.has("termino")    ? obj.get("termino").getAsString().trim()    : "";
            String fechaDesde = obj.has("fechaDesde") ? obj.get("fechaDesde").getAsString().trim() : "";
            String fechaHasta = obj.has("fechaHasta") ? obj.get("fechaHasta").getAsString().trim() : "";

            application.model.dao.FacturacionDAO dao = new application.model.dao.FacturacionDAO();
            java.util.List<java.util.Map<String, Object>> lista = dao.buscarRecibos(
                    termino.isEmpty()    ? null : termino,
                    fechaDesde.isEmpty() ? null : fechaDesde,
                    fechaHasta.isEmpty() ? null : fechaHasta);
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en buscarRecibos: " + t.getMessage());
            return "[]";
        }
    }

    // =========================================================
    // BÚSQUEDA CON FILTROS — EGRESOS
    // =========================================================
    public String buscarEgresos(String jsonFiltros) {
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(
                    jsonFiltros == null ? "{}" : jsonFiltros).getAsJsonObject();
            String termino    = obj.has("termino")    ? obj.get("termino").getAsString().trim()    : "";
            String fechaDesde = obj.has("fechaDesde") ? obj.get("fechaDesde").getAsString().trim() : "";
            String fechaHasta = obj.has("fechaHasta") ? obj.get("fechaHasta").getAsString().trim() : "";

            application.model.dao.EgresoGastoDAO dao = new application.model.dao.EgresoGastoDAO();
            java.util.List<java.util.Map<String, Object>> lista = dao.buscarEgresos(
                    termino.isEmpty()    ? null : termino,
                    fechaDesde.isEmpty() ? null : fechaDesde,
                    fechaHasta.isEmpty() ? null : fechaHasta);
            return gson.toJson(lista);
        } catch (Throwable t) {
            System.err.println("-> ERROR en buscarEgresos: " + t.getMessage());
            return "[]";
        }
    }
}
