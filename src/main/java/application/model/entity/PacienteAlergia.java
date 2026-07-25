package application.model.entity;

public class PacienteAlergia {

    private String identidadPaciente;
    private Integer idAlergia;

    public PacienteAlergia() {
    }

    public PacienteAlergia(String identidadPaciente, Integer idAlergia) {
        this.identidadPaciente = identidadPaciente;
        this.idAlergia = idAlergia;
    }

    public String getIdentidadPaciente() {
        return identidadPaciente;
    }

    public void setIdentidadPaciente(String identidadPaciente) {
        this.identidadPaciente = identidadPaciente;
    }

    public Integer getIdAlergia() {
        return idAlergia;
    }

    public void setIdAlergia(Integer idAlergia) {
        this.idAlergia = idAlergia;
    }

    @Override
    public String toString() {
        return "PacienteAlergia{" +
                "identidadPaciente='" + identidadPaciente + '\'' +
                ", idAlergia=" + idAlergia +
                '}';
    }
}
