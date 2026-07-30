package application.model.entity;

public class Especialidad {
    private int idEspecialidades;
    private String nombreEspecialidad;

    public Especialidad(int idEspecialidades, String nombreEspecialidad) {
        this.idEspecialidades = idEspecialidades;
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public int getIdEspecialidades() {
        return idEspecialidades;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }
}
