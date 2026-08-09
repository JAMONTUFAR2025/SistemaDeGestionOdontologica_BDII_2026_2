package application.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Facturacion {

    private Integer idFactura;
    private String numeroRecibo;
    private Integer idPaciente;
    private Integer idCajaSesion;
    private Integer idUsuario;
    private String rtnCliente;
    private LocalDate fechaEmision;
    private String concepto;
    private BigDecimal sumaNeta;
    private BigDecimal totalHonorarios;
    private BigDecimal totalRetenido;
    private BigDecimal totalNetoRecibido;
    private String metodoPago;
    private String anulado;
    private LocalDateTime fechaAnulado;

    public Facturacion() {
    }

    public Facturacion(Integer idFactura, String numeroRecibo, Integer idPaciente, Integer idCajaSesion, Integer idUsuario, String rtnCliente, LocalDate fechaEmision, String concepto, BigDecimal sumaNeta, BigDecimal totalHonorarios, BigDecimal totalRetenido, BigDecimal totalNetoRecibido, String metodoPago, String anulado, LocalDateTime fechaAnulado) {
        this.idFactura = idFactura;
        this.numeroRecibo = numeroRecibo;
        this.idPaciente = idPaciente;
        this.idCajaSesion = idCajaSesion;
        this.idUsuario = idUsuario;
        this.rtnCliente = rtnCliente;
        this.fechaEmision = fechaEmision;
        this.concepto = concepto;
        this.sumaNeta = sumaNeta;
        this.totalHonorarios = totalHonorarios;
        this.totalRetenido = totalRetenido;
        this.totalNetoRecibido = totalNetoRecibido;
        this.metodoPago = metodoPago;
        this.anulado = anulado;
        this.fechaAnulado = fechaAnulado;
    }

    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public String getNumeroRecibo() {
        return numeroRecibo;
    }

    public void setNumeroRecibo(String numeroRecibo) {
        this.numeroRecibo = numeroRecibo;
    }

    public Integer getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Integer idPaciente) {
        this.idPaciente = idPaciente;
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

    public String getRtnCliente() {
        return rtnCliente;
    }

    public void setRtnCliente(String rtnCliente) {
        this.rtnCliente = rtnCliente;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getSumaNeta() {
        return sumaNeta;
    }

    public void setSumaNeta(BigDecimal sumaNeta) {
        this.sumaNeta = sumaNeta;
    }

    public BigDecimal getTotalHonorarios() {
        return totalHonorarios;
    }

    public void setTotalHonorarios(BigDecimal totalHonorarios) {
        this.totalHonorarios = totalHonorarios;
    }

    public BigDecimal getTotalRetenido() {
        return totalRetenido;
    }

    public void setTotalRetenido(BigDecimal totalRetenido) {
        this.totalRetenido = totalRetenido;
    }

    public BigDecimal getTotalNetoRecibido() {
        return totalNetoRecibido;
    }

    public void setTotalNetoRecibido(BigDecimal totalNetoRecibido) {
        this.totalNetoRecibido = totalNetoRecibido;
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

    public LocalDateTime getFechaAnulado() {
        return fechaAnulado;
    }

    public void setFechaAnulado(LocalDateTime fechaAnulado) {
        this.fechaAnulado = fechaAnulado;
    }

    @Override
    public String toString() {
        return "Facturacion{" +
                "idFactura=" + idFactura +
                ", numeroRecibo='" + numeroRecibo + '\'' +
                ", idPaciente=" + idPaciente +
                ", totalNetoRecibido=" + totalNetoRecibido +
                ", anulado='" + anulado + '\'' +
                '}';
    }
}
