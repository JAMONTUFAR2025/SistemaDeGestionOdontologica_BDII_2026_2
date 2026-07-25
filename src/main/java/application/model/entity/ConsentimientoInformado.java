package application.model.entity;

import java.time.LocalDate;

public class ConsentimientoInformado {

    private Integer idConsentimiento;
    private Integer idEvolucion;
    private String tipoProcedimiento;
    private String representanteLegal;
    private String identidadRepresentante;
    private LocalDate fechaFirma;

    public ConsentimientoInformado() {
    }

    public ConsentimientoInformado(Integer idConsentimiento, Integer idEvolucion, String tipoProcedimiento, String representanteLegal, String identidadRepresentante, LocalDate fechaFirma) {
        this.idConsentimiento = idConsentimiento;
        this.idEvolucion = idEvolucion;
        this.tipoProcedimiento = tipoProcedimiento;
        this.representanteLegal = representanteLegal;
        this.identidadRepresentante = identidadRepresentante;
        this.fechaFirma = fechaFirma;
    }

    public Integer getIdConsentimiento() {
        return idConsentimiento;
    }

    public void setIdConsentimiento(Integer idConsentimiento) {
        this.idConsentimiento = idConsentimiento;
    }

    public Integer getIdEvolucion() {
        return idEvolucion;
    }

    public void setIdEvolucion(Integer idEvolucion) {
        this.idEvolucion = idEvolucion;
    }

    public String getTipoProcedimiento() {
        return tipoProcedimiento;
    }

    public void setTipoProcedimiento(String tipoProcedimiento) {
        this.tipoProcedimiento = tipoProcedimiento;
    }

    public String getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(String representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public String getIdentidadRepresentante() {
        return identidadRepresentante;
    }

    public void setIdentidadRepresentante(String identidadRepresentante) {
        this.identidadRepresentante = identidadRepresentante;
    }

    public LocalDate getFechaFirma() {
        return fechaFirma;
    }

    public void setFechaFirma(LocalDate fechaFirma) {
        this.fechaFirma = fechaFirma;
    }

    @Override
    public String toString() {
        return "ConsentimientoInformado{" +
                "idConsentimiento=" + idConsentimiento +
                ", idEvolucion=" + idEvolucion +
                ", tipoProcedimiento='" + tipoProcedimiento + '\'' +
                ", fechaFirma=" + fechaFirma +
                '}';
    }
}
