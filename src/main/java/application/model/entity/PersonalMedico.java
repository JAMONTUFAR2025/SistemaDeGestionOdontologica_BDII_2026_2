package application.model.entity;

import java.time.LocalDateTime;

public class PersonalMedico {

    private Integer idMedico;
    private String nombreCompleto;
    private String identidad;
    private String rol;
    private Integer idEspecialidad;
    private String telefono;
    private String correo;
    private String estado;
    private LocalDateTime fechaInactivacion;

    public PersonalMedico() {
    }

    public PersonalMedico(Integer idMedico, String nombreCompleto, String identidad, String rol, Integer idEspecialidad, String telefono, String correo, String estado, LocalDateTime fechaInactivacion) {
        this.idMedico = idMedico;
        this.nombreCompleto = nombreCompleto;
        this.identidad = identidad;
        this.rol = rol;
        this.idEspecialidad = idEspecialidad;
        this.telefono = telefono;
        this.correo = correo;
        this.estado = estado;
        this.fechaInactivacion = fechaInactivacion;
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getIdentidad() {
        return identidad;
    }

    public void setIdentidad(String identidad) {
        this.identidad = identidad;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Integer getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Integer idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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
        return nombreCompleto != null ? nombreCompleto : "";
    }
}
