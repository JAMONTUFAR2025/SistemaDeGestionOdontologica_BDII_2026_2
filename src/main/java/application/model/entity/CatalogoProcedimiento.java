package application.model.entity;

import java.math.BigDecimal;

public class CatalogoProcedimiento {

    private Integer idCatProcedimiento;
    private String nombreProcedimiento;
    private BigDecimal precioSugerido;

    public CatalogoProcedimiento() {
    }

    public CatalogoProcedimiento(Integer idCatProcedimiento, String nombreProcedimiento, BigDecimal precioSugerido) {
        this.idCatProcedimiento = idCatProcedimiento;
        this.nombreProcedimiento = nombreProcedimiento;
        this.precioSugerido = precioSugerido;
    }

    public Integer getIdCatProcedimiento() {
        return idCatProcedimiento;
    }

    public void setIdCatProcedimiento(Integer idCatProcedimiento) {
        this.idCatProcedimiento = idCatProcedimiento;
    }

    public String getNombreProcedimiento() {
        return nombreProcedimiento;
    }

    public void setNombreProcedimiento(String nombreProcedimiento) {
        this.nombreProcedimiento = nombreProcedimiento;
    }

    public BigDecimal getPrecioSugerido() {
        return precioSugerido;
    }

    public void setPrecioSugerido(BigDecimal precioSugerido) {
        this.precioSugerido = precioSugerido;
    }

    @Override
    public String toString() {
        return nombreProcedimiento != null ? nombreProcedimiento : "";
    }
}
