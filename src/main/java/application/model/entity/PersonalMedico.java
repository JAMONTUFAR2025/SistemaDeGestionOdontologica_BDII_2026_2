package application.model.entity;

public class PersonalMedico {
    private String nombre_completo;
    private String identidad;
    private String telefono;
    private int id_especialidad;

    // Datos de usuario
    private String correo;
    private String contrasenia;
    private String rol_sistema;
    private String estado;

    public String getNombre_completo() {
        return nombre_completo;
    }

    public String getIdentidad() {
        return identidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getId_especialidad() {
        return id_especialidad;
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

    public String getEstado() {
        return estado;
    }
}
