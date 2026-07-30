package application.model.entity;

import java.time.LocalDateTime;

public class Cita {

    private Integer idCitas;
    private Integer idPacientes;
    private Integer idPersonalMedico;
    private LocalDateTime fechaHora;
    private String motivoCita;
    private String estado;

    public Cita() {
    }

    public Cita(Integer idCitas, Integer idPacientes, Integer idPersonalMedico, LocalDateTime fechaHora, String motivoCita, String estado) {
        this.idCitas = idCitas;
        this.idPacientes = idPacientes;
        this.idPersonalMedico = idPersonalMedico;
        this.fechaHora = fechaHora;
        this.motivoCita = motivoCita;
        this.estado = estado;
    }

    public Integer getIdCitas() {
        return idCitas;
    }

    public void setIdCitas(Integer idCitas) {
        this.idCitas = idCitas;
    }

    public Integer getIdPacientes() {
        return idPacientes;
    }

    public void setIdPacientes(Integer idPacientes) {
        this.idPacientes = idPacientes;
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
                "idCitas=" + idCitas +
                ", idPacientes=" + idPacientes +
                ", idPersonalMedico=" + idPersonalMedico +
                ", fechaHora=" + fechaHora +
                ", estado='" + estado + '\'' +
                '}';
    }
}
