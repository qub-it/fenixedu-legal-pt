package org.fenixedu.ulisboa.specifications.domain.legal.raides;

import org.joda.time.LocalDate;

public class TblDiplomado extends RaidesData implements IGrauPrecedenteCompleto, IMatricula {

    protected String idEstab;
    protected String idAluno;

    protected String curso;
    protected String ramo;

    protected String anoLectivo;
    
    protected String areaInvestigacao;

    protected String concluiGrau;

    protected String numInscConclusao;

    protected String classificacaoFinal;

    protected LocalDate dataDiploma;

    protected String conclusaoMd;

    protected String classificacaoFinalMd;

    protected String mobilidadeCredito;
    protected String tipoMobilidadeCredito;
    protected String progMobilidadeCredito;
    protected String outroProgMobCredito;
    protected String paisMobilidadeCredito;

    protected String escolaridadeAnterior;
    protected String outroEscolaridadeAnterior;
    protected String paisEscolaridadeAnt;
    protected String anoEscolaridadeAnt;
    protected String estabEscolaridadeAnt;
    protected String outroEstabEscolarAnt;
    protected String cursoEscolarAnt;
    protected String outroCursoEscolarAnt;
    
    protected boolean valid = true;

    @Override
    public void markAsInvalid() {
        this.valid = false;
    }

    @Override
    public boolean isValid() {
        return valid;
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

    public String getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(String idAluno) {
        this.idAluno = idAluno;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getRamo() {
        return ramo;
    }

    public void setRamo(String ramo) {
        this.ramo = ramo;
    }

    public String getAnoLectivo() {
        return anoLectivo;
    }

    public void setAnoLectivo(String anoLectivo) {
        this.anoLectivo = anoLectivo;
    }
    
    public String getAreaInvestigacao() {
        return areaInvestigacao;
    }
    
    public void setAreaInvestigacao(String areaInvestigacao) {
        this.areaInvestigacao = areaInvestigacao;
    }

    public String getConcluiGrau() {
        return concluiGrau;
    }

    public void setConcluiGrau(String concluiGrau) {
        this.concluiGrau = concluiGrau;
    }

    public String getNumInscConclusao() {
        return numInscConclusao;
    }

    public void setNumInscConclusao(String numInscConclusao) {
        this.numInscConclusao = numInscConclusao;
    }

    public String getClassificacaoFinal() {
        return classificacaoFinal;
    }

    public void setClassificacaoFinal(String classificacaoFinal) {
        this.classificacaoFinal = classificacaoFinal;
    }

    public LocalDate getDataDiploma() {
        return dataDiploma;
    }

    public void setDataDiploma(LocalDate dataDiploma) {
        this.dataDiploma = dataDiploma;
    }

    public String getConclusaoMd() {
        return conclusaoMd;
    }

    public void setConclusaoMd(String conclusaoMd) {
        this.conclusaoMd = conclusaoMd;
    }

    public String getClassificacaoFinalMd() {
        return classificacaoFinalMd;
    }

    public void setClassificacaoFinalMd(String classificacaoFinalMd) {
        this.classificacaoFinalMd = classificacaoFinalMd;
    }

    public String getMobilidadeCredito() {
        return mobilidadeCredito;
    }

    public void setMobilidadeCredito(String mobilidadeCredito) {
        this.mobilidadeCredito = mobilidadeCredito;
    }

    public String getTipoMobilidadeCredito() {
        return tipoMobilidadeCredito;
    }

    public void setTipoMobilidadeCredito(String tipoMobilidadeCredito) {
        this.tipoMobilidadeCredito = tipoMobilidadeCredito;
    }

    public String getProgMobilidadeCredito() {
        return progMobilidadeCredito;
    }

    public void setProgMobilidadeCredito(String progMobilidadeCredito) {
        this.progMobilidadeCredito = progMobilidadeCredito;
    }

    public String getOutroProgMobCredito() {
        return outroProgMobCredito;
    }

    public void setOutroProgMobCredito(String outroProgMobCredito) {
        this.outroProgMobCredito = outroProgMobCredito;
    }

    public String getPaisMobilidadeCredito() {
        return paisMobilidadeCredito;
    }

    public void setPaisMobilidadeCredito(String paisMobilidadeCredito) {
        this.paisMobilidadeCredito = paisMobilidadeCredito;
    }

    public String getEscolaridadeAnterior() {
        return escolaridadeAnterior;
    }

    public void setEscolaridadeAnterior(String escolaridadeAnterior) {
        this.escolaridadeAnterior = escolaridadeAnterior;
    }

    public String getOutroEscolaridadeAnterior() {
        return outroEscolaridadeAnterior;
    }

    public void setOutroEscolaridadeAnterior(String outroEscolaridadeAnterior) {
        this.outroEscolaridadeAnterior = outroEscolaridadeAnterior;
    }

    public String getPaisEscolaridadeAnt() {
        return paisEscolaridadeAnt;
    }

    public void setPaisEscolaridadeAnt(String paisEscolaridadeAnt) {
        this.paisEscolaridadeAnt = paisEscolaridadeAnt;
    }

    public String getAnoEscolaridadeAnt() {
        return anoEscolaridadeAnt;
    }

    public void setAnoEscolaridadeAnt(String anoEscolaridadeAnt) {
        this.anoEscolaridadeAnt = anoEscolaridadeAnt;
    }

    public String getEstabEscolaridadeAnt() {
        return estabEscolaridadeAnt;
    }

    public void setEstabEscolaridadeAnt(String estabEscolaridadeAnt) {
        this.estabEscolaridadeAnt = estabEscolaridadeAnt;
    }

    public String getOutroEstabEscolarAnt() {
        return outroEstabEscolarAnt;
    }

    public void setOutroEstabEscolarAnt(String outroEstabEscolarAnt) {
        this.outroEstabEscolarAnt = outroEstabEscolarAnt;
    }

    public String getCursoEscolarAnt() {
        return cursoEscolarAnt;
    }

    public void setCursoEscolarAnt(String cursoEscolarAnt) {
        this.cursoEscolarAnt = cursoEscolarAnt;
    }

    public String getOutroCursoEscolarAnt() {
        return outroCursoEscolarAnt;
    }

    public void setOutroCursoEscolarAnt(String outroCursoEscolarAnt) {
        this.outroCursoEscolarAnt = outroCursoEscolarAnt;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    
    
    
    @Override
    public boolean isTipoEstabSecSpecified() {
        return false;
    }

    @Override
    public String getTipoEstabSec() {
        return null;
    }

    @Override
    public void setTipoEstabSec(String tipoEstabSec) {

    }
    
}
