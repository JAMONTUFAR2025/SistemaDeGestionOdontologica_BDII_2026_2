package application.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CajaSesion {

    private Integer idCajaSesion;
    private Integer idUsuarioApertura;
    private Integer idUsuarioCierre;
    private BigDecimal montoApertura;
    private BigDecimal montoCierreReal;
    private BigDecimal diferencia;
    private String estado; // 'Abierta' | 'Cerrada'
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private String observaciones;

    public CajaSesion() {}

    public CajaSesion(Integer idCajaSesion, Integer idUsuarioApertura, Integer idUsuarioCierre,
                      BigDecimal montoApertura, BigDecimal montoCierreReal, BigDecimal diferencia,
                      String estado, LocalDateTime fechaApertura, LocalDateTime fechaCierre,
                      String observaciones) {
        this.idCajaSesion = idCajaSesion;
        this.idUsuarioApertura = idUsuarioApertura;
        this.idUsuarioCierre = idUsuarioCierre;
        this.montoApertura = montoApertura;
        this.montoCierreReal = montoCierreReal;
        this.diferencia = diferencia;
        this.estado = estado;
        this.fechaApertura = fechaApertura;
        this.fechaCierre = fechaCierre;
        this.observaciones = observaciones;
    }

    public Integer getIdCajaSesion() { return idCajaSesion; }
    public void setIdCajaSesion(Integer idCajaSesion) { this.idCajaSesion = idCajaSesion; }

    public Integer getIdUsuarioApertura() { return idUsuarioApertura; }
    public void setIdUsuarioApertura(Integer idUsuarioApertura) { this.idUsuarioApertura = idUsuarioApertura; }

    public Integer getIdUsuarioCierre() { return idUsuarioCierre; }
    public void setIdUsuarioCierre(Integer idUsuarioCierre) { this.idUsuarioCierre = idUsuarioCierre; }

    public BigDecimal getMontoApertura() { return montoApertura; }
    public void setMontoApertura(BigDecimal montoApertura) { this.montoApertura = montoApertura; }

    public BigDecimal getMontoCierreReal() { return montoCierreReal; }
    public void setMontoCierreReal(BigDecimal montoCierreReal) { this.montoCierreReal = montoCierreReal; }

    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }

    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return "CajaSesion{id=" + idCajaSesion + ", estado='" + estado + "', apertura=" + montoApertura + "}";
    }
}
