package application.model.entity;

public class CatalogoAlergia {

    private Integer idAlergia;
    private String nombreAlergia;

    public CatalogoAlergia() {
    }

    public CatalogoAlergia(Integer idAlergia, String nombreAlergia) {
        this.idAlergia = idAlergia;
        this.nombreAlergia = nombreAlergia;
    }

    public CatalogoAlergia(String nombreAlergia) {
        this.nombreAlergia = nombreAlergia;
    }

    public Integer getIdAlergia() {
        return idAlergia;
    }

    public void setIdAlergia(Integer idAlergia) {
        this.idAlergia = idAlergia;
    }

    public String getNombreAlergia() {
        return nombreAlergia;
    }

    public void setNombreAlergia(String nombreAlergia) {
        this.nombreAlergia = nombreAlergia;
    }

    @Override
    public String toString() {
        return nombreAlergia != null ? nombreAlergia : "";
    }
}
