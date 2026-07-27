package application.model.dao;

public class PersonalMedico {
    private String nombre_completo;
    private String identidad;
    private String telefono;
    private String rol;
    private int id_especialidad;
    
    // Datos de usuario
    private String correo;
    private String contrasenia;
    private String rol_sistema;
    private String estado;

    public String getNombre_completo() { return nombre_completo; }
    public String getIdentidad() { return identidad; }
    public String getTelefono() { return telefono; }
    public String getRol() { return rol; }
    public int getId_especialidad() { return id_especialidad; }
    public String getCorreo() { return correo; }
    public String getContrasenia() { return contrasenia; }
    public String getRol_sistema() { return rol_sistema; }
    public String getEstado() { return estado; }
}
