package application.model.entity;

public class Especialidad {
    private int idEspecialidad;
    private String nombreEspecialidad;

    public Especialidad(int idEspecialidad, String nombreEspecialidad) {
        this.idEspecialidad = idEspecialidad;
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }
}
