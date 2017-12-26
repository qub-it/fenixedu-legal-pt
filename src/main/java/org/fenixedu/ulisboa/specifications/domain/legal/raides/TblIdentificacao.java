package org.fenixedu.ulisboa.specifications.domain.legal.raides;

import org.joda.time.LocalDate;

public class TblIdentificacao extends RaidesData {

    protected String idEstab;
    protected Integer idAluno;
    protected String nome;
    protected String numId;
    protected String tipoId;
    protected String tipoIdDescr;
    protected String checkDigitId;
    protected LocalDate dataNasc;
    protected String sexo;
    protected String nacionalidade;
    protected String outroPaisDeNacionalidade;
    protected String residePais;
    protected String paisEnsinoSecundario;
    protected String observId;
    protected String observ;
    protected boolean valid = true;

    public void markAsInvalid() {
        this.valid = false;
    }
    
    public boolean isValid() {
        return this.valid;
    }

    /*
     * GETTERS & SETTERS
     */

    public String getIdEstab() {
        return idEstab;
    }

    public void setIdEstab(String idEstab) {
        this.idEstab = idEstab;
    }

    public Integer getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(Integer idAluno) {
        this.idAluno = idAluno;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumId() {
        return numId;
    }

    public void setNumId(String numId) {
        this.numId = numId;
    }

    public String getTipoId() {
        return tipoId;
    }

    public void setTipoId(String tipoId) {
        this.tipoId = tipoId;
    }

    public String getTipoIdDescr() {
        return tipoIdDescr;
    }

    public void setTipoIdDescr(String tipoIdDescr) {
        this.tipoIdDescr = tipoIdDescr;
    }

    public String getCheckDigitId() {
        return checkDigitId;
    }

    public void setCheckDigitId(String checkDigitId) {
        this.checkDigitId = checkDigitId;
    }

    public LocalDate getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(LocalDate dataNasc) {
        this.dataNasc = dataNasc;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }
    
    public String getOutroPaisDeNacionalidade() {
        return outroPaisDeNacionalidade;
    }
    
    public void setOutroPaisDeNacionalidade(String outroPaisDeNacionalidade) {
        this.outroPaisDeNacionalidade = outroPaisDeNacionalidade;
    }

    public String getResidePais() {
        return residePais;
    }

    public void setResidePais(String residePais) {
        this.residePais = residePais;
    }

    public String getPaisEnsinoSecundario() {
        return paisEnsinoSecundario;
    }

    public void setPaisEnsinoSecundario(String paisEnsinoSecundario) {
        this.paisEnsinoSecundario = paisEnsinoSecundario;
    }

    public String getObservId() {
        return observId;
    }

    public void setObservId(String observId) {
        this.observId = observId;
    }

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
