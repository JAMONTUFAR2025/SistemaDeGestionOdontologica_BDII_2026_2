package application.model.entity;

import java.time.LocalDateTime;

public class User {

    private Integer idUsuariosLogin;
    private String correo;
    private String contrasenia;
    private Integer idPersonalMedico;
    private String rolSistema;
    private String borrado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaBorrado;

    public User() {
    }

    public User(Integer idUsuariosLogin, String correo, String contrasenia, Integer idPersonalMedico,
                String rolSistema, String borrado, LocalDateTime fechaCreacion, LocalDateTime fechaBorrado) {
        this.idUsuariosLogin = idUsuariosLogin;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.idPersonalMedico = idPersonalMedico;
        this.rolSistema = rolSistema;
        this.borrado = borrado;
        this.fechaCreacion = fechaCreacion;
        this.fechaBorrado = fechaBorrado;
    }

    public Integer getIdUsuariosLogin() {
        return idUsuariosLogin;
    }

    public void setIdUsuariosLogin(Integer idUsuariosLogin) {
        this.idUsuariosLogin = idUsuariosLogin;
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

    public Integer getIdPersonalMedico() {
        return idPersonalMedico;
    }

    public void setIdPersonalMedico(Integer idPersonalMedico) {
        this.idPersonalMedico = idPersonalMedico;
    }

    public String getRolSistema() {
        return rolSistema;
    }

    public void setRolSistema(String rolSistema) {
        this.rolSistema = rolSistema;
    }

    public String getBorrado() {
        return borrado;
    }

    public void setBorrado(String borrado) {
        this.borrado = borrado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaBorrado() {
        return fechaBorrado;
    }

    public void setFechaBorrado(LocalDateTime fechaBorrado) {
        this.fechaBorrado = fechaBorrado;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUsuariosLogin=" + idUsuariosLogin +
                ", correo='" + correo + '\'' +
                ", idPersonalMedico=" + idPersonalMedico +
                ", rolSistema='" + rolSistema + '\'' +
                ", borrado='" + borrado + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
