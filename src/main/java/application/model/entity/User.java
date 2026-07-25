package application.model.entity;

import java.time.LocalDateTime;

public class User {

    private Integer idUsuario;
    private String correo;
    private String contrasenia;
    private Integer idMedico;
    private String rolSistema;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaInactivacion;

    public User() {
    }

    public User(Integer idUsuario, String correo, String contrasenia, Integer idMedico, String rolSistema, String estado, LocalDateTime fechaCreacion, LocalDateTime fechaInactivacion) {
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.idMedico = idMedico;
        this.rolSistema = rolSistema;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaInactivacion = fechaInactivacion;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
    }

    public String getRolSistema() {
        return rolSistema;
    }

    public void setRolSistema(String rolSistema) {
        this.rolSistema = rolSistema;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaInactivacion() {
        return fechaInactivacion;
    }

    public void setFechaInactivacion(LocalDateTime fechaInactivacion) {
        this.fechaInactivacion = fechaInactivacion;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUsuario=" + idUsuario +
                ", correo='" + correo + '\'' +
                ", idMedico=" + idMedico +
                ", rolSistema='" + rolSistema + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaInactivacion=" + fechaInactivacion +
                '}';
    }
}
