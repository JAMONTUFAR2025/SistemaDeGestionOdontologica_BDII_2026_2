package application.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EvolucionClinica {

    private Integer idEvolucion;
    private String identidadPaciente;
    private Integer idExpediente;
    private Integer idCita;
    private Integer idMedico;
    private Integer numeroCita;
    private LocalDateTime fechaConsulta;
    private String motivoConsulta;
    private String sintomaPrincipal;
    private String presionArterial;
    private String pulsoCardiaco;
    private String temperatura;
    private String tejidosBlandosObservacion;
    private String diagnostico;
    private Integer idCatProcedimiento;
    private String estadoOdontograma;
    private BigDecimal pagoAbono;
    private String observaciones;
    private LocalDateTime fechaRegistro;

    public EvolucionClinica() {
    }

    public EvolucionClinica(Integer idEvolucion, String identidadPaciente, Integer idExpediente, Integer idCita, Integer idMedico, Integer numeroCita, LocalDateTime fechaConsulta, String motivoConsulta, String sintomaPrincipal, String presionArterial, String pulsoCardiaco, String temperatura, String tejidosBlandosObservacion, String diagnostico, Integer idCatProcedimiento, String estadoOdontograma, BigDecimal pagoAbono, String observaciones, LocalDateTime fechaRegistro) {
        this.idEvolucion = idEvolucion;
        this.identidadPaciente = identidadPaciente;
        this.idExpediente = idExpediente;
        this.idCita = idCita;
        this.idMedico = idMedico;
        this.numeroCita = numeroCita;
        this.fechaConsulta = fechaConsulta;
        this.motivoConsulta = motivoConsulta;
        this.sintomaPrincipal = sintomaPrincipal;
        this.presionArterial = presionArterial;
        this.pulsoCardiaco = pulsoCardiaco;
        this.temperatura = temperatura;
        this.tejidosBlandosObservacion = tejidosBlandosObservacion;
        this.diagnostico = diagnostico;
        this.idCatProcedimiento = idCatProcedimiento;
        this.estadoOdontograma = estadoOdontograma;
        this.pagoAbono = pagoAbono;
        this.observaciones = observaciones;
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getIdEvolucion() {
        return idEvolucion;
    }

    public void setIdEvolucion(Integer idEvolucion) {
        this.idEvolucion = idEvolucion;
    }

    public String getIdentidadPaciente() {
        return identidadPaciente;
    }

    public void setIdentidadPaciente(String identidadPaciente) {
        this.identidadPaciente = identidadPaciente;
    }

    public Integer getIdExpediente() {
        return idExpediente;
    }

    public void setIdExpediente(Integer idExpediente) {
        this.idExpediente = idExpediente;
    }

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
    }

    public Integer getNumeroCita() {
        return numeroCita;
    }

    public void setNumeroCita(Integer numeroCita) {
        this.numeroCita = numeroCita;
    }

    public LocalDateTime getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(LocalDateTime fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getSintomaPrincipal() {
        return sintomaPrincipal;
    }

    public void setSintomaPrincipal(String sintomaPrincipal) {
        this.sintomaPrincipal = sintomaPrincipal;
    }

    public String getPresionArterial() {
        return presionArterial;
    }

    public void setPresionArterial(String presionArterial) {
        this.presionArterial = presionArterial;
    }

    public String getPulsoCardiaco() {
        return pulsoCardiaco;
    }

    public void setPulsoCardiaco(String pulsoCardiaco) {
        this.pulsoCardiaco = pulsoCardiaco;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getTejidosBlandosObservacion() {
        return tejidosBlandosObservacion;
    }

    public void setTejidosBlandosObservacion(String tejidosBlandosObservacion) {
        this.tejidosBlandosObservacion = tejidosBlandosObservacion;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public Integer getIdCatProcedimiento() {
        return idCatProcedimiento;
    }

    public void setIdCatProcedimiento(Integer idCatProcedimiento) {
        this.idCatProcedimiento = idCatProcedimiento;
    }

    public String getEstadoOdontograma() {
        return estadoOdontograma;
    }

    public void setEstadoOdontograma(String estadoOdontograma) {
        this.estadoOdontograma = estadoOdontograma;
    }

    public BigDecimal getPagoAbono() {
        return pagoAbono;
    }

    public void setPagoAbono(BigDecimal pagoAbono) {
        this.pagoAbono = pagoAbono;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "EvolucionClinica{" +
                "idEvolucion=" + idEvolucion +
                ", identidadPaciente='" + identidadPaciente + '\'' +
                ", idExpediente=" + idExpediente +
                ", idMedico=" + idMedico +
                ", fechaConsulta=" + fechaConsulta +
                '}';
    }
}
