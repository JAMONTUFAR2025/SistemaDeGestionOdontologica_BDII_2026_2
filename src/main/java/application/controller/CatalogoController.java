package application.controller;

/**
 * Controller para la gestión de Catálogos del sistema.
 * Expuesto al WebView como: window.catalogoController
 *
 * Métodos disponibles desde JavaScript:
 *   - obtenerProcedimientos()                         → JSON array
 *   - agregarProcedimiento(nombre, precio)             → "OK|..." / "ERR|..."
 *   - actualizarProcedimiento(id, nombre, precio)      → "OK|..." / "ERR|..."
 *   - eliminarProcedimiento(id)                        → "OK|..." / "ERR|..."
 *   - obtenerAlergias()                               → JSON array
 *   - agregarAlergia(nombre)                          → "OK|..." / "ERR|..."
 *   - actualizarAlergia(id, nombre)                   → "OK|..." / "ERR|..."
 *   - eliminarAlergia(id)                             → "OK|..." / "ERR|..."
 */
public class CatalogoController extends BaseController {

    public CatalogoController() {
        super();
    }

    // =====================================================
    // CATÁLOGO DE PROCEDIMIENTOS
    // =====================================================

    public String obtenerProcedimientos() {
        try {
            application.model.dao.CatalogoProcedimientosDAO dao = new application.model.dao.CatalogoProcedimientosDAO();
            return gson.toJson(dao.obtenerTodos());
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerProcedimientos: " + t.getMessage());
            return "[]";
        }
    }

    public String agregarProcedimiento(String nombre, double precio) {
        try {
            if (nombre == null || nombre.trim().isEmpty())
                return "ERR|El nombre del procedimiento es obligatorio.";
            application.model.dao.CatalogoProcedimientosDAO dao = new application.model.dao.CatalogoProcedimientosDAO();
            boolean ok = dao.insertar(nombre.trim(), precio);
            return ok ? "OK|Procedimiento agregado exitosamente." : "ERR|No se pudo agregar el procedimiento.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en agregarProcedimiento: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String actualizarProcedimiento(int id, String nombre, double precio) {
        try {
            if (nombre == null || nombre.trim().isEmpty())
                return "ERR|El nombre del procedimiento es obligatorio.";
            application.model.dao.CatalogoProcedimientosDAO dao = new application.model.dao.CatalogoProcedimientosDAO();
            boolean ok = dao.actualizar(id, nombre.trim(), precio);
            return ok ? "OK|Procedimiento actualizado exitosamente." : "ERR|No se pudo actualizar el procedimiento.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en actualizarProcedimiento: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String eliminarProcedimiento(int id) {
        try {
            application.model.dao.CatalogoProcedimientosDAO dao = new application.model.dao.CatalogoProcedimientosDAO();
            boolean ok = dao.eliminar(id);
            return ok ? "OK|Procedimiento eliminado exitosamente." : "ERR|No se pudo eliminar. Puede estar en uso.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarProcedimiento: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    // =====================================================
    // CATÁLOGO DE ALERGIAS
    // =====================================================

    public String obtenerAlergias() {
        try {
            application.model.dao.CatalogoAlergiasDAO dao = new application.model.dao.CatalogoAlergiasDAO();
            return gson.toJson(dao.obtenerTodas());
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerAlergias: " + t.getMessage());
            return "[]";
        }
    }

    public String agregarAlergia(String nombre) {
        try {
            if (nombre == null || nombre.trim().isEmpty())
                return "ERR|El nombre de la alergia es obligatorio.";
            application.model.dao.CatalogoAlergiasDAO dao = new application.model.dao.CatalogoAlergiasDAO();
            boolean ok = dao.insertar(nombre.trim());
            return ok ? "OK|Alergia agregada exitosamente." : "ERR|No se pudo agregar la alergia.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en agregarAlergia: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String actualizarAlergia(int id, String nombre) {
        try {
            if (nombre == null || nombre.trim().isEmpty())
                return "ERR|El nombre de la alergia es obligatorio.";
            application.model.dao.CatalogoAlergiasDAO dao = new application.model.dao.CatalogoAlergiasDAO();
            boolean ok = dao.actualizar(id, nombre.trim());
            return ok ? "OK|Alergia actualizada exitosamente." : "ERR|No se pudo actualizar la alergia.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en actualizarAlergia: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String eliminarAlergia(int id) {
        try {
            application.model.dao.CatalogoAlergiasDAO dao = new application.model.dao.CatalogoAlergiasDAO();
            boolean ok = dao.eliminar(id);
            return ok ? "OK|Alergia eliminada exitosamente." : "ERR|No se pudo eliminar. Puede estar asignada a un paciente.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarAlergia: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    // =====================================================
    // CATÁLOGO DE ESPECIALIDADES
    // =====================================================

    public String obtenerEspecialidades() {
        try {
            application.model.dao.EspecialidadDAO dao = new application.model.dao.EspecialidadDAO();
            return gson.toJson(dao.obtenerEspecialidades());
        } catch (Throwable t) {
            System.err.println("-> ERROR en obtenerEspecialidades: " + t.getMessage());
            return "[]";
        }
    }

    public String agregarEspecialidad(String nombre) {
        try {
            if (nombre == null || nombre.trim().isEmpty())
                return "ERR|El nombre de la especialidad es obligatorio.";
            application.model.dao.EspecialidadDAO dao = new application.model.dao.EspecialidadDAO();
            boolean ok = dao.agregarEspecialidad(nombre.trim());
            return ok ? "OK|Especialidad agregada exitosamente." : "ERR|No se pudo agregar la especialidad.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en agregarEspecialidad: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String actualizarEspecialidad(int id, String nombre) {
        try {
            if (nombre == null || nombre.trim().isEmpty())
                return "ERR|El nombre de la especialidad es obligatorio.";
            application.model.dao.EspecialidadDAO dao = new application.model.dao.EspecialidadDAO();
            boolean ok = dao.actualizarEspecialidad(id, nombre.trim());
            return ok ? "OK|Especialidad actualizada exitosamente." : "ERR|No se pudo actualizar la especialidad.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en actualizarEspecialidad: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }

    public String eliminarEspecialidad(int id) {
        try {
            application.model.dao.EspecialidadDAO dao = new application.model.dao.EspecialidadDAO();
            boolean ok = dao.eliminarEspecialidad(id);
            return ok ? "OK|Especialidad eliminada exitosamente." : "ERR|No se pudo eliminar. Puede estar asignada a personal médico.";
        } catch (Throwable t) {
            System.err.println("-> ERROR en eliminarEspecialidad: " + t.getMessage());
            return "ERR|Error: " + t.getMessage();
        }
    }
}
