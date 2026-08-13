package application.model.entity;

import java.time.LocalDateTime;

public class Cita {

    private Integer idCita;
    private Integer idPaciente;
    private Integer idPersonalMedico;
    private LocalDateTime fechaHora;
    private String motivoCita;
    private String estado;

    public Cita() {
    }

    public Cita(Integer idCita, Integer idPaciente, Integer idPersonalMedico, LocalDateTime fechaHora, String motivoCita, String estado) {
        this.idCita = idCita;
        this.idPaciente = idPaciente;
        this.idPersonalMedico = idPersonalMedico;
        this.fechaHora = fechaHora;
        this.motivoCita = motivoCita;
        this.estado = estado;
    }

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }

    // Keep old setter for backward compatibility during migration
    public void setIdCitas(Integer idCitas) {
        this.idCita = idCitas;
    }

    public Integer getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Integer idPaciente) {
        this.idPaciente = idPaciente;
    }

    // Keep old setter for backward compatibility during migration
    public void setIdPacientes(Integer idPacientes) {
        this.idPaciente = idPacientes;
    }

    public Integer getIdPersonalMedico() {
        return idPersonalMedico;
    }

    public void setIdPersonalMedico(Integer idPersonalMedico) {
        this.idPersonalMedico = idPersonalMedico;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivoCita() {
        return motivoCita;
    }

    public void setMotivoCita(String motivoCita) {
        this.motivoCita = motivoCita;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Cita{" +
                "idCita=" + idCita +
                ", idPaciente=" + idPaciente +
                ", idPersonalMedico=" + idPersonalMedico +
                ", fechaHora=" + fechaHora +
                ", estado='" + estado + '\'' +
                '}';
    }
}
