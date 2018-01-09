package org.fenixedu.legalpt.dto.rebides;

import java.io.Serializable;

public class QualificationBean implements Serializable {

    static final long serialVersionUID = 1L;

    protected String schoolLevel; //NivelFormacao
    protected String schoolLevelOrigin; //TipoNivelFormacao
    protected String instituition; //Estabelecimento
    protected String otherInstituition; //OutroEstabelecimento
    protected String degree; //Curso
    protected String otherDegree; //OutroCurso
    protected String expertiseArea; //Especialidade
    protected String otherExpertiseArea; //OutraEspecialidade
    protected String scientificArea; //AreaCientifica

    /*
     * GETTERS & SETTERS
     */

    public String getSchoolLevel() {
        return schoolLevel;
    }

    public void setSchoolLevel(String schoolLevel) {
        this.schoolLevel = schoolLevel;
    }

    public String getSchoolLevelOrigin() {
        return schoolLevelOrigin;
    }

    public void setSchoolLevelOrigin(String schoolLevelDegree) {
        this.schoolLevelOrigin = schoolLevelDegree;
    }

    public String getInstituition() {
        return instituition;
    }

    public void setInstituition(String instituition) {
        this.instituition = instituition;
    }

    public String getOtherInstituition() {
        return otherInstituition;
    }

    public void setOtherInstituition(String otherInstituition) {
        this.otherInstituition = otherInstituition;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getOtherDegree() {
        return otherDegree;
    }

    public void setOtherDegree(String otherDegree) {
        this.otherDegree = otherDegree;
    }

    public String getExpertiseArea() {
        return expertiseArea;
    }

    public void setExpertiseArea(String expertiseArea) {
        this.expertiseArea = expertiseArea;
    }

    public String getOtherExpertiseArea() {
        return otherExpertiseArea;
    }

    public void setOtherExpertiseArea(String otherExpertiseArea) {
        this.otherExpertiseArea = otherExpertiseArea;
    }

    public String getScientificArea() {
        return scientificArea;
    }

    public void setScientificArea(String scientificArea) {
        this.scientificArea = scientificArea;
    }
}
