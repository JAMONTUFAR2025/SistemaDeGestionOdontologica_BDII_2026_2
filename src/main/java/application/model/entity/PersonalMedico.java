package application.model.entity;

public class PersonalMedico {
    private int idPersonalMedico;
    private String nombre_completo;
    private String identidad;
    private String telefono;
    private int id_especialidad; // Renamed to match new DB schema id_especialidad

    // Datos de usuario
    private String nombreUsuario;
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
    
    public void setNombre_completo(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public String getIdentidad() {
        return identidad;
    }
    
    public void setIdentidad(String identidad) {
        this.identidad = identidad;
    }

    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getId_especialidad() {
        return id_especialidad;
    }
    
    // For backwards compatibility during transition
    public int getId_especialidades() {
        return id_especialidad;
    }

    public void setId_especialidad(int id_especialidad) {
        this.id_especialidad = id_especialidad;
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

    public String getRol_sistema() {
        return rol_sistema;
    }
    
    public void setRol_sistema(String rol_sistema) {
        this.rol_sistema = rol_sistema;
    }
}
