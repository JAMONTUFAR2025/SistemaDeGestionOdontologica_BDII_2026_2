package application.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FacturacionRecibo {

    private Integer idFactura;
    private String numeroRecibo;
    private String identidadPaciente;
    private String rtnCliente;
    private LocalDate fechaEmision;
    private String concepto;
    private BigDecimal sumaNeta;
    private BigDecimal totalHonorarios;
    private BigDecimal totalRetenido;
    private BigDecimal totalNetoRecibido;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaInactivacion;

    public FacturacionRecibo() {
    }

    public FacturacionRecibo(Integer idFactura, String numeroRecibo, String identidadPaciente, String rtnCliente, LocalDate fechaEmision, String concepto, BigDecimal sumaNeta, BigDecimal totalHonorarios, BigDecimal totalRetenido, BigDecimal totalNetoRecibido, String metodoPago, String estado, LocalDateTime fechaInactivacion) {
        this.idFactura = idFactura;
        this.numeroRecibo = numeroRecibo;
        this.identidadPaciente = identidadPaciente;
        this.rtnCliente = rtnCliente;
        this.fechaEmision = fechaEmision;
        this.concepto = concepto;
        this.sumaNeta = sumaNeta;
        this.totalHonorarios = totalHonorarios;
        this.totalRetenido = totalRetenido;
        this.totalNetoRecibido = totalNetoRecibido;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.fechaInactivacion = fechaInactivacion;
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

    public String getIdentidadPaciente() {
        return identidadPaciente;
    }

    public void setIdentidadPaciente(String identidadPaciente) {
        this.identidadPaciente = identidadPaciente;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaInactivacion() {
        return fechaInactivacion;
    }

    public void setFechaInactivacion(LocalDateTime fechaInactivacion) {
        this.fechaInactivacion = fechaInactivacion;
    }

    @Override
    public String toString() {
        return "FacturacionRecibo{" +
                "idFactura=" + idFactura +
                ", numeroRecibo='" + numeroRecibo + '\'' +
                ", identidadPaciente='" + identidadPaciente + '\'' +
                ", totalNetoRecibido=" + totalNetoRecibido +
                ", estado='" + estado + '\'' +
                '}';
    }
}
