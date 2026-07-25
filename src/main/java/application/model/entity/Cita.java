package application.model.entity;

import java.time.LocalDateTime;

public class Cita {

    private Integer idCita;
    private String identidadPaciente;
    private Integer idMedico;
    private LocalDateTime fechaHora;
    private String motivoCita;
    private String estado;

    public Cita() {
    }

    public Cita(Integer idCita, String identidadPaciente, Integer idMedico, LocalDateTime fechaHora, String motivoCita, String estado) {
        this.idCita = idCita;
        this.identidadPaciente = identidadPaciente;
        this.idMedico = idMedico;
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

    public String getIdentidadPaciente() {
        return identidadPaciente;
    }

    public void setIdentidadPaciente(String identidadPaciente) {
        this.identidadPaciente = identidadPaciente;
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
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
                ", identidadPaciente='" + identidadPaciente + '\'' +
                ", idMedico=" + idMedico +
                ", fechaHora=" + fechaHora +
                ", estado='" + estado + '\'' +
                '}';
    }
}
