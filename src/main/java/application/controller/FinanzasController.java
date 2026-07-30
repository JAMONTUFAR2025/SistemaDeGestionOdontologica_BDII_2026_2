package application.controller;

public class FinanzasController extends BaseController {

    public FinanzasController() {
        super();
    }

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
