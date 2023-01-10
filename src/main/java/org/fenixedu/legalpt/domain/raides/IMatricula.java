package org.fenixedu.legalpt.domain.raides;

public interface IMatricula {

    public String getIdAluno();

    public void setIdAluno(final String idAluno);

    public String getAnoLectivo();

    public void setAnoLectivo(final String anoLectivo);

    public String getCurso();

    public void setCurso(final String curso);

    public String getRamo();

    public void setRamo(final String ramo);

    public void markAsInvalid();

    public boolean isValid();
}
