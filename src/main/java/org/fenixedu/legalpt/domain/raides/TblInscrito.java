package org.fenixedu.legalpt.domain.raides;

import java.math.BigDecimal;

public class TblInscrito extends RaidesData implements IGrauPrecedenteCompleto, IMatricula {

    protected String idEstab;
    protected String idAluno;

    protected String curso;
    protected String ramo;
    protected String anoLectivo;

    protected String anoCurricular;
    protected String primeiraVez;

    protected String regimeFrequencia;

    protected Integer numInscNesteCurso;

    protected BigDecimal ectsInscricao;
    protected BigDecimal ectsAcumulados;

    protected String tempoParcial;
    protected String bolseiro;

    protected String formaIngresso;

    protected String estabInscricaoAnt;
    protected String outroEstabInscAnt;

    protected String notaIngresso;
    protected String opcaoIngresso;

    protected Integer numInscCursosAnt;

    protected String anoUltimaInscricao;

    protected String estudanteTrabalhador;

    protected String estadoCivil;
    protected String alunoDeslocado;
    protected String resideConcelho;
    protected String nivelEscolarPai;
    protected String nivelEscolarMae;
    protected String situacaoProfPai;
    protected String situacaoProfMae;
    protected String situacaoProfAluno;
    protected String profissaoPai;
    protected String profissaoMae;
    protected String profissaoAluno;

    protected String escolaridadeAnterior;
    protected String outroEscolaridadeAnterior;
    protected String paisEscolaridadeAnt;
    protected String anoEscolaridadeAnt;
    protected String estabEscolaridadeAnt;
    protected String outroEstabEscolarAnt;
    protected String cursoEscolarAnt;
    protected String outroCursoEscolarAnt;
    protected String tipoEstabSec;
    protected boolean valid = true;

    public TblInscrito() {
    }

    @Override
    public void markAsInvalid() {
        this.valid = false;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean isTipoEstabSecSpecified() {
        return true;
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

    public String getAnoCurricular() {
        return anoCurricular;
    }

    public void setAnoCurricular(String anoCurricular) {
        this.anoCurricular = anoCurricular;
    }

    public String getPrimeiraVez() {
        return primeiraVez;
    }

    public void setPrimeiraVez(String primeiraVez) {
        this.primeiraVez = primeiraVez;
    }

    public String getRegimeFrequencia() {
        return regimeFrequencia;
    }

    public void setRegimeFrequencia(String regimeFrequencia) {
        this.regimeFrequencia = regimeFrequencia;
    }

    public Integer getNumInscNesteCurso() {
        return numInscNesteCurso;
    }

    public void setNumInscNesteCurso(Integer numInscNesteCurso) {
        this.numInscNesteCurso = numInscNesteCurso;
    }

    public BigDecimal getEctsInscricao() {
        return ectsInscricao;
    }

    public void setEctsInscricao(BigDecimal ectsInscricao) {
        this.ectsInscricao = ectsInscricao;
    }

    public BigDecimal getEctsAcumulados() {
        return ectsAcumulados;
    }

    public void setEctsAcumulados(BigDecimal ectsAcumulados) {
        this.ectsAcumulados = ectsAcumulados;
    }

    public String getTempoParcial() {
        return tempoParcial;
    }

    public void setTempoParcial(String tempoParcial) {
        this.tempoParcial = tempoParcial;
    }

    public String getBolseiro() {
        return bolseiro;
    }

    public void setBolseiro(String bolseiro) {
        this.bolseiro = bolseiro;
    }

    public String getFormaIngresso() {
        return formaIngresso;
    }

    public void setFormaIngresso(String formaIngresso) {
        this.formaIngresso = formaIngresso;
    }

    public String getEstabInscricaoAnt() {
        return estabInscricaoAnt;
    }

    public void setEstabInscricaoAnt(String estabInscricaoAnt) {
        this.estabInscricaoAnt = estabInscricaoAnt;
    }

    public String getOutroEstabInscAnt() {
        return outroEstabInscAnt;
    }

    public void setOutroEstabInscAnt(String outroEstabInscAnt) {
        this.outroEstabInscAnt = outroEstabInscAnt;
    }

    public String getNotaIngresso() {
        return notaIngresso;
    }

    public void setNotaIngresso(String notaIngresso) {
        this.notaIngresso = notaIngresso;
    }

    public String getOpcaoIngresso() {
        return opcaoIngresso;
    }

    public void setOpcaoIngresso(String opcaoIngresso) {
        this.opcaoIngresso = opcaoIngresso;
    }

    public Integer getNumInscCursosAnt() {
        return numInscCursosAnt;
    }

    public void setNumInscCursosAnt(Integer numInscCursosAnt) {
        this.numInscCursosAnt = numInscCursosAnt;
    }

    public String getAnoUltimaInscricao() {
        return anoUltimaInscricao;
    }

    public void setAnoUltimaInscricao(String anoUltimaInscricao) {
        this.anoUltimaInscricao = anoUltimaInscricao;
    }

    public String getEstudanteTrabalhador() {
        return estudanteTrabalhador;
    }

    public void setEstudanteTrabalhador(String estudanteTrabalhador) {
        this.estudanteTrabalhador = estudanteTrabalhador;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getAlunoDeslocado() {
        return alunoDeslocado;
    }

    public void setAlunoDeslocado(String alunoDeslocado) {
        this.alunoDeslocado = alunoDeslocado;
    }

    public String getResideConcelho() {
        return resideConcelho;
    }

    public void setResideConcelho(String resideConcelho) {
        this.resideConcelho = resideConcelho;
    }

    public String getNivelEscolarPai() {
        return nivelEscolarPai;
    }

    public void setNivelEscolarPai(String nivelEscolarPai) {
        this.nivelEscolarPai = nivelEscolarPai;
    }

    public String getNivelEscolarMae() {
        return nivelEscolarMae;
    }

    public void setNivelEscolarMae(String nivelEscolarMae) {
        this.nivelEscolarMae = nivelEscolarMae;
    }

    public String getSituacaoProfPai() {
        return situacaoProfPai;
    }

    public void setSituacaoProfPai(String situacaoProfPai) {
        this.situacaoProfPai = situacaoProfPai;
    }

    public String getSituacaoProfMae() {
        return situacaoProfMae;
    }

    public void setSituacaoProfMae(String situacaoProfMae) {
        this.situacaoProfMae = situacaoProfMae;
    }

    public String getSituacaoProfAluno() {
        return situacaoProfAluno;
    }

    public void setSituacaoProfAluno(String situacaoProfAluno) {
        this.situacaoProfAluno = situacaoProfAluno;
    }

    public String getProfissaoPai() {
        return profissaoPai;
    }

    public void setProfissaoPai(String profissaoPai) {
        this.profissaoPai = profissaoPai;
    }

    public String getProfissaoMae() {
        return profissaoMae;
    }

    public void setProfissaoMae(String profissaoMae) {
        this.profissaoMae = profissaoMae;
    }

    public String getProfissaoAluno() {
        return profissaoAluno;
    }

    public void setProfissaoAluno(String profissaoAluno) {
        this.profissaoAluno = profissaoAluno;
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

    public String getTipoEstabSec() {
        return tipoEstabSec;
    }

    public void setTipoEstabSec(String tipoEstabSec) {
        this.tipoEstabSec = tipoEstabSec;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }


}
