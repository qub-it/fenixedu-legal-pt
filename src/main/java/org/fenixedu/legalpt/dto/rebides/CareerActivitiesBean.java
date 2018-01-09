package org.fenixedu.legalpt.dto.rebides;

import java.io.Serializable;
import java.math.BigDecimal;

public class CareerActivitiesBean implements Serializable {

    static final long serialVersionUID = 1L;

    protected String department; //Departamento
    protected String otherDepartment; //OutroDepartamento
    protected String category; //Categoria
    protected Boolean guest; //Convidado
    protected Boolean schoolStaff; //Equiparado
    protected String aptitudeTests; //ProvasAptidao
    protected String aggregationTests; //ProvasAgregacao
    protected String expertTests; //ProvasEspecialista
    protected String serviceRegime; //RegimePrestacaoServico
    protected String partialRegime; //RegimeParcial
    protected String bindingRegime; //RegimeVinculacao
    protected String contractWageLevel; //Indice
    protected BigDecimal researchPercentage; //PercentagemInvestigacao
    protected Boolean teaching; //Docencia
    protected BigDecimal teachingHoursPercentage; //HorasLetivas
    protected Integer teachingHoursUnit; //UnidadeHL
    protected Boolean rootUnitPresident; //ActReitor
    protected Boolean rootUnitVicePresident; //ActViceReitor (Vice-reitor ou vice-presidente ou órgão correspondente)
    protected Boolean unitPresident; //ActDiretor (Diretor ou presidente da unidade orgânica)
    protected Boolean unitCoordinator; //ActCoordena (Atividades de coordenação ou direção de centro de investigação, departamento ou equivalente)
    protected Boolean coordenatorGeneralCouncil; //ActConsGeral (Atividades de conselho geral ou órgão correspondente)
    protected Boolean managementCouncilActivities; //ActConsGest (Atividades de conselho de gestão ou órgão correspondente
    protected Boolean scientificCouncilActivities; //ActConsCient (Atividades de conselho científico/técnico-científico ou órgão correspondente)
    protected Boolean pedagogicCouncilActivities; //ActConsPedag (Atividades de conselho pedagógico)
    protected Boolean coachingOrResearchProjectActivities; //ActFormacao (Atividades de formação ou em projetos de investigação)
    protected Boolean otherActivity; //ActOutra (Outra atividade)
    protected String otherActivityDescription; //ActOutraDescr (Descrição da outra atividade)

    /*
     * GETTERS & SETTERS
     */

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOtherDepartment() {
        return otherDepartment;
    }

    public void setOtherDepartment(String otherDepartment) {
        this.otherDepartment = otherDepartment;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getGuest() {
        return guest;
    }

    public void setGuest(Boolean invited) {
        this.guest = invited;
    }

    public Boolean getSchoolStaff() {
        return schoolStaff;
    }

    public void setSchoolStaff(Boolean equated) {
        this.schoolStaff = equated;
    }

    public String getAptitudeTests() {
        return aptitudeTests;
    }

    public void setAptitudeTests(String aptitudeTests) {
        this.aptitudeTests = aptitudeTests;
    }

    public String getAggregationTests() {
        return aggregationTests;
    }

    public void setAggregationTests(String aggregationTests) {
        this.aggregationTests = aggregationTests;
    }

    public String getExpertTests() {
        return expertTests;
    }

    public void setExpertTests(String expertTests) {
        this.expertTests = expertTests;
    }

    public String getServiceRegime() {
        return serviceRegime;
    }

    public void setServiceRegime(String serviceRegime) {
        this.serviceRegime = serviceRegime;
    }

    public String getPartialRegime() {
        return partialRegime;
    }

    public void setPartialRegime(String partialRegime) {
        this.partialRegime = partialRegime;
    }

    public String getBindingRegime() {
        return bindingRegime;
    }

    public void setBindingRegime(String bindingRegime) {
        this.bindingRegime = bindingRegime;
    }

    public String getContractWageLevel() {
        return contractWageLevel;
    }

    public void setContractWageLevel(String contractWageLevel) {
        this.contractWageLevel = contractWageLevel;
    }

    public BigDecimal getResearchPercentage() {
        return researchPercentage;
    }

    public void setResearchPercentage(BigDecimal researchPercentage) {
        this.researchPercentage = researchPercentage;
    }

    public Boolean getTeaching() {
        return teaching;
    }

    public void setTeaching(Boolean teaching) {
        this.teaching = teaching;
    }

    public BigDecimal getTeachingHoursPercentage() {
        return teachingHoursPercentage;
    }

    public void setTeachingHoursPercentage(BigDecimal teachingHoursPercentage) {
        this.teachingHoursPercentage = teachingHoursPercentage;
    }

    public Integer getTeachingHoursUnit() {
        return teachingHoursUnit;
    }

    public void setTeachingHoursUnit(Integer teachingHoursUnit) {
        this.teachingHoursUnit = teachingHoursUnit;
    }

    public Boolean getRootUnitPresident() {
        return rootUnitPresident;
    }

    public void setRootUnitPresident(Boolean rootUnitPresident) {
        this.rootUnitPresident = rootUnitPresident;
    }

    public Boolean getRootUnitVicePresident() {
        return rootUnitVicePresident;
    }

    public void setRootUnitVicePresident(Boolean rootUnitVicePresident) {
        this.rootUnitVicePresident = rootUnitVicePresident;
    }

    public Boolean getUnitPresident() {
        return unitPresident;
    }

    public void setUnitPresident(Boolean unitPresident) {
        this.unitPresident = unitPresident;
    }

    public Boolean getUnitCoordinator() {
        return unitCoordinator;
    }

    public void setUnitCoordinator(Boolean unitCoordinator) {
        this.unitCoordinator = unitCoordinator;
    }

    public Boolean getCoordenatorGeneralCouncil() {
        return coordenatorGeneralCouncil;
    }

    public void setCoordenatorGeneralCouncil(Boolean coordenatorGeneralCouncil) {
        this.coordenatorGeneralCouncil = coordenatorGeneralCouncil;
    }

    public Boolean getManagementCouncilActivities() {
        return managementCouncilActivities;
    }

    public void setManagementCouncilActivities(Boolean managementCouncilActivities) {
        this.managementCouncilActivities = managementCouncilActivities;
    }

    public Boolean getScientificCouncilActivities() {
        return scientificCouncilActivities;
    }

    public void setScientificCouncilActivities(Boolean cientificCouncilActivities) {
        this.scientificCouncilActivities = cientificCouncilActivities;
    }

    public Boolean getPedagogicCouncilActivities() {
        return pedagogicCouncilActivities;
    }

    public void setPedagogicCouncilActivities(Boolean pedagogicCouncilActivities) {
        this.pedagogicCouncilActivities = pedagogicCouncilActivities;
    }

    public Boolean getCoachingOrResearchProjectActivities() {
        return coachingOrResearchProjectActivities;
    }

    public void setCoachingOrResearchProjectActivities(Boolean coachingOrResearchProjectActivities) {
        this.coachingOrResearchProjectActivities = coachingOrResearchProjectActivities;
    }

    public Boolean getOtherActivity() {
        return otherActivity;
    }

    public void setOtherActivity(Boolean otherActivity) {
        this.otherActivity = otherActivity;
    }

    public String getOtherActivityDescription() {
        return otherActivityDescription;
    }

    public void setOtherActivityDescription(String otherActivityDescription) {
        this.otherActivityDescription = otherActivityDescription;
    }

}
