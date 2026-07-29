package application.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Paciente {
    private int idPaciente;
    private String identidad;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private String genero;
    private String estadoCivil;
    private String ocupacion;
    private String domicilio;
    private String telefono;
    private String personaResponsable;
    private String telefonoResponsable;
    private LocalDateTime fechaRegistro;
    private String estado;
    private LocalDateTime fechaInactivacion;

    public Paciente() {
    }

    public Paciente(int idPaciente, String identidad, String nombreCompleto, LocalDate fechaNacimiento, String genero, String estadoCivil, String ocupacion, String domicilio, String telefono, String personaResponsable, String telefonoResponsable, LocalDateTime fechaRegistro, String estado, LocalDateTime fechaInactivacion) {
        this.idPaciente = idPaciente;
        this.identidad = identidad;
        this.nombreCompleto = nombreCompleto;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.estadoCivil = estadoCivil;
        this.ocupacion = ocupacion;
        this.domicilio = domicilio;
        this.telefono = telefono;
        this.personaResponsable = personaResponsable;
        this.telefonoResponsable = telefonoResponsable;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
        this.fechaInactivacion = fechaInactivacion;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getIdentidad() {
        return identidad;
    }

    public void setIdentidad(String identidad) {
        this.identidad = identidad;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPersonaResponsable() {
        return personaResponsable;
    }

    public void setPersonaResponsable(String personaResponsable) {
        this.personaResponsable = personaResponsable;
    }

    public String getTelefonoResponsable() {
        return telefonoResponsable;
    }

    public void setTelefonoResponsable(String telefonoResponsable) {
        this.telefonoResponsable = telefonoResponsable;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
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
