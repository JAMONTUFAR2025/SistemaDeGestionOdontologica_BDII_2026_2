package application.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EgresoGasto {

    private Integer idEgreso;
    private Integer idCajaSesion;
    private Integer idUsuario;
    private LocalDate fecha;
    private String descripcion;
    private BigDecimal monto;
    private String metodoPago;
    private String numeroComprobante;
    private String anulado;
    private java.time.LocalDateTime fechaAnulado;

    public EgresoGasto() {
    }

    public EgresoGasto(Integer idEgreso, Integer idCajaSesion, Integer idUsuario, LocalDate fecha, String descripcion, BigDecimal monto, String metodoPago, String numeroComprobante, String anulado, java.time.LocalDateTime fechaAnulado) {
        this.idEgreso = idEgreso;
        this.idCajaSesion = idCajaSesion;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.numeroComprobante = numeroComprobante;
        this.anulado = anulado;
        this.fechaAnulado = fechaAnulado;
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

    public Integer getIdCajaSesion() {
        return idCajaSesion;
    }

    public void setIdCajaSesion(Integer idCajaSesion) {
        this.idCajaSesion = idCajaSesion;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getAnulado() {
        return anulado;
    }

    public void setAnulado(String anulado) {
        this.anulado = anulado;
    }

    public java.time.LocalDateTime getFechaAnulado() {
        return fechaAnulado;
    }

    public void setFechaAnulado(java.time.LocalDateTime fechaAnulado) {
        this.fechaAnulado = fechaAnulado;
    }

    @Override
    public String toString() {
        return "EgresoGasto{" +
                "idEgreso=" + idEgreso +
                ", fecha=" + fecha +
                ", monto=" + monto +
                ", anulado='" + anulado + '\'' +
                '}';
    }
}
