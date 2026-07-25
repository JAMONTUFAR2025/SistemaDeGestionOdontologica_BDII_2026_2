package application.model.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class ConstanciaMedica {

    private Integer idConstancia;
    private Integer idEvolucion;
    private LocalDate fechaEmision;
    private LocalTime horaEmision;
    private String tratamientoRealizado;

    public ConstanciaMedica() {
    }

    public ConstanciaMedica(Integer idConstancia, Integer idEvolucion, LocalDate fechaEmision, LocalTime horaEmision, String tratamientoRealizado) {
        this.idConstancia = idConstancia;
        this.idEvolucion = idEvolucion;
        this.fechaEmision = fechaEmision;
        this.horaEmision = horaEmision;
        this.tratamientoRealizado = tratamientoRealizado;
    }

    public Integer getIdConstancia() {
        return idConstancia;
    }

    public void setIdConstancia(Integer idConstancia) {
        this.idConstancia = idConstancia;
    }

    public Integer getIdEvolucion() {
        return idEvolucion;
    }

    public void setIdEvolucion(Integer idEvolucion) {
        this.idEvolucion = idEvolucion;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalTime getHoraEmision() {
        return horaEmision;
    }

    public void setHoraEmision(LocalTime horaEmision) {
        this.horaEmision = horaEmision;
    }

    public String getTratamientoRealizado() {
        return tratamientoRealizado;
    }

    public void setTratamientoRealizado(String tratamientoRealizado) {
        this.tratamientoRealizado = tratamientoRealizado;
    }

    @Override
    public String toString() {
        return "ConstanciaMedica{" +
                "idConstancia=" + idConstancia +
                ", idEvolucion=" + idEvolucion +
                ", fechaEmision=" + fechaEmision +
                '}';
    }
}
