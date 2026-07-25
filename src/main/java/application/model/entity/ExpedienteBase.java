package application.model.entity;

public class ExpedienteBase {

    private Integer idExpediente;
    private String identidadPaciente;
    private String remitidoPor;
    private String antecedentesPatologicos;
    private String antecedentesOdontologicos;
    private String antecedentesQuirurgicos;
    private String antecedentesGinecobstetros;
    private String habitosToxicos;
    private String farmacosUsoHabitual;
    private Boolean reaccionAnestesicos;
    private String especifiqueAnestesia;
    private String complicacionesTratamientosPrevios;
    private String habitosBucales;
    private String frecuenciaCepillado;
    private String tipoCepilloCerdas;
    private String usoHiloDental;
    private String tipoMordida;

    public ExpedienteBase() {
    }

    public ExpedienteBase(Integer idExpediente, String identidadPaciente, String remitidoPor, String antecedentesPatologicos, String antecedentesOdontologicos, String antecedentesQuirurgicos, String antecedentesGinecobstetros, String habitosToxicos, String farmacosUsoHabitual, Boolean reaccionAnestesicos, String especifiqueAnestesia, String complicacionesTratamientosPrevios, String habitosBucales, String frecuenciaCepillado, String tipoCepilloCerdas, String usoHiloDental, String tipoMordida) {
        this.idExpediente = idExpediente;
        this.identidadPaciente = identidadPaciente;
        this.remitidoPor = remitidoPor;
        this.antecedentesPatologicos = antecedentesPatologicos;
        this.antecedentesOdontologicos = antecedentesOdontologicos;
        this.antecedentesQuirurgicos = antecedentesQuirurgicos;
        this.antecedentesGinecobstetros = antecedentesGinecobstetros;
        this.habitosToxicos = habitosToxicos;
        this.farmacosUsoHabitual = farmacosUsoHabitual;
        this.reaccionAnestesicos = reaccionAnestesicos;
        this.especifiqueAnestesia = especifiqueAnestesia;
        this.complicacionesTratamientosPrevios = complicacionesTratamientosPrevios;
        this.habitosBucales = habitosBucales;
        this.frecuenciaCepillado = frecuenciaCepillado;
        this.tipoCepilloCerdas = tipoCepilloCerdas;
        this.usoHiloDental = usoHiloDental;
        this.tipoMordida = tipoMordida;
    }

    public Integer getIdExpediente() {
        return idExpediente;
    }

    public void setIdExpediente(Integer idExpediente) {
        this.idExpediente = idExpediente;
    }

    public String getIdentidadPaciente() {
        return identidadPaciente;
    }

    public void setIdentidadPaciente(String identidadPaciente) {
        this.identidadPaciente = identidadPaciente;
    }

    public String getRemitidoPor() {
        return remitidoPor;
    }

    public void setRemitidoPor(String remitidoPor) {
        this.remitidoPor = remitidoPor;
    }

    public String getAntecedentesPatologicos() {
        return antecedentesPatologicos;
    }

    public void setAntecedentesPatologicos(String antecedentesPatologicos) {
        this.antecedentesPatologicos = antecedentesPatologicos;
    }

    public String getAntecedentesOdontologicos() {
        return antecedentesOdontologicos;
    }

    public void setAntecedentesOdontologicos(String antecedentesOdontologicos) {
        this.antecedentesOdontologicos = antecedentesOdontologicos;
    }

    public String getAntecedentesQuirurgicos() {
        return antecedentesQuirurgicos;
    }

    public void setAntecedentesQuirurgicos(String antecedentesQuirurgicos) {
        this.antecedentesQuirurgicos = antecedentesQuirurgicos;
    }

    public String getAntecedentesGinecobstetros() {
        return antecedentesGinecobstetros;
    }

    public void setAntecedentesGinecobstetros(String antecedentesGinecobstetros) {
        this.antecedentesGinecobstetros = antecedentesGinecobstetros;
    }

    public String getHabitosToxicos() {
        return habitosToxicos;
    }

    public void setHabitosToxicos(String habitosToxicos) {
        this.habitosToxicos = habitosToxicos;
    }

    public String getFarmacosUsoHabitual() {
        return farmacosUsoHabitual;
    }

    public void setFarmacosUsoHabitual(String farmacosUsoHabitual) {
        this.farmacosUsoHabitual = farmacosUsoHabitual;
    }

    public Boolean getReaccionAnestesicos() {
        return reaccionAnestesicos;
    }

    public void setReaccionAnestesicos(Boolean reaccionAnestesicos) {
        this.reaccionAnestesicos = reaccionAnestesicos;
    }

    public String getEspecifiqueAnestesia() {
        return especifiqueAnestesia;
    }

    public void setEspecifiqueAnestesia(String especifiqueAnestesia) {
        this.especifiqueAnestesia = especifiqueAnestesia;
    }

    public String getComplicacionesTratamientosPrevios() {
        return complicacionesTratamientosPrevios;
    }

    public void setComplicacionesTratamientosPrevios(String complicacionesTratamientosPrevios) {
        this.complicacionesTratamientosPrevios = complicacionesTratamientosPrevios;
    }

    public String getHabitosBucales() {
        return habitosBucales;
    }

    public void setHabitosBucales(String habitosBucales) {
        this.habitosBucales = habitosBucales;
    }

    public String getFrecuenciaCepillado() {
        return frecuenciaCepillado;
    }

    public void setFrecuenciaCepillado(String frecuenciaCepillado) {
        this.frecuenciaCepillado = frecuenciaCepillado;
    }

    public String getTipoCepilloCerdas() {
        return tipoCepilloCerdas;
    }

    public void setTipoCepilloCerdas(String tipoCepilloCerdas) {
        this.tipoCepilloCerdas = tipoCepilloCerdas;
    }

    public String getUsoHiloDental() {
        return usoHiloDental;
    }

    public void setUsoHiloDental(String usoHiloDental) {
        this.usoHiloDental = usoHiloDental;
    }

    public String getTipoMordida() {
        return tipoMordida;
    }

    public void setTipoMordida(String tipoMordida) {
        this.tipoMordida = tipoMordida;
    }

    @Override
    public String toString() {
        return "ExpedienteBase{" +
                "idExpediente=" + idExpediente +
                ", identidadPaciente='" + identidadPaciente + '\'' +
                '}';
    }
}
