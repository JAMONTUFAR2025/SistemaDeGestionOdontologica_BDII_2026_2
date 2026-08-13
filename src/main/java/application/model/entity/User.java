package application.model.entity;

import java.time.LocalDateTime;

public class User {

    private Integer idUsuarioLogin;
    private String nombreUsuario;
    private String correo;
    private String contrasenia;
    private Integer idPersonalMedico;
    private String rolSistema;
    private boolean borrado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaBorrado;

    public User() {
    }

    public Integer getIdUsuarioLogin() {
        return idUsuarioLogin;
    }

    public void setIdUsuarioLogin(Integer idUsuarioLogin) {
        this.idUsuarioLogin = idUsuarioLogin;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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

    public boolean isBorrado() {
        return borrado;
    }

    public void setBorrado(boolean borrado) {
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
                "idUsuarioLogin=" + idUsuarioLogin +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", correo='" + correo + '\'' +
                ", idPersonalMedico=" + idPersonalMedico +
                ", rolSistema='" + rolSistema + '\'' +
                ", borrado=" + borrado +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
