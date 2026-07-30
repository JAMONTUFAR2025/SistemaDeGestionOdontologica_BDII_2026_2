package application.model.entity;

public class PersonalMedico {
    private int idPersonalMedico;
    private String nombre_completo;
    private String identidad;
    private String telefono;
    private int id_especialidades;

    // Datos de usuario
    private String correo;
    private String contrasenia;
    private String rol_sistema;

    public int getIdPersonalMedico() {
        return idPersonalMedico;
    }

    public void setIdPersonalMedico(int idPersonalMedico) {
        this.idPersonalMedico = idPersonalMedico;
    }

    public String getNombre_completo() {
        return nombre_completo;
    }

    public String getIdentidad() {
        return identidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getId_especialidades() {
        return id_especialidades;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public String getRol_sistema() {
        return rol_sistema;
    }
}
