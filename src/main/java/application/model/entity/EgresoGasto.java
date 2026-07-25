package application.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EgresoGasto {

    private Integer idEgreso;
    private LocalDate fecha;
    private String descripcion;
    private BigDecimal monto;
    private String numeroComprobante;

    public EgresoGasto() {
    }

    public EgresoGasto(Integer idEgreso, LocalDate fecha, String descripcion, BigDecimal monto, String numeroComprobante) {
        this.idEgreso = idEgreso;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.monto = monto;
        this.numeroComprobante = numeroComprobante;
    }

    public Integer getIdEgreso() {
        return idEgreso;
    }

    public void setIdEgreso(Integer idEgreso) {
        this.idEgreso = idEgreso;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    @Override
    public String toString() {
        return "EgresoGasto{" +
                "idEgreso=" + idEgreso +
                ", fecha=" + fecha +
                ", monto=" + monto +
                '}';
    }
}
