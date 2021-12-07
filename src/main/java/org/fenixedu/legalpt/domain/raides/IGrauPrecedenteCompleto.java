package org.fenixedu.legalpt.domain.raides;

public interface IGrauPrecedenteCompleto {

    public String getEscolaridadeAnterior();

    public void setEscolaridadeAnterior(final String escolaridadeAnterior);

    public String getOutroEscolaridadeAnterior();

    public void setOutroEscolaridadeAnterior(final String outroEscolaridadeAnterior);

    public String getPaisEscolaridadeAnt();

    public void setPaisEscolaridadeAnt(final String paisEscolaridadeAnt);

    public String getAnoEscolaridadeAnt();

    public void setAnoEscolaridadeAnt(final String anoEscolaridadeAnt);

    public String getEstabEscolaridadeAnt();

    public void setEstabEscolaridadeAnt(final String estabEscolaridadeAnt);

    public String getOutroEstabEscolarAnt();

    public void setOutroEstabEscolarAnt(final String outroEstabEscolarAnt);

    public String getCursoEscolarAnt();

    public void setCursoEscolarAnt(final String cursoEscolarAnt);

    public String getOutroCursoEscolarAnt();

    public void setOutroCursoEscolarAnt(String outroCursoEscolarAnt);

    public boolean isTipoEstabSecSpecified();
    
    public String getTipoEstabSec();

    public void setTipoEstabSec(String tipoEstabSec);
    
    public void markAsInvalid();
    
    public boolean isValid();
}
