package org.fenixedu.legalpt.dto.a3es;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.legalpt.services.a3es.process.A3esHarvestDegreeDataService.GraduatedReportByYearEntry;

@SuppressWarnings({ "serial", "deprecation" })
public class A3esDegreeBean extends A3esAbstractBean {

    private String institutionName;
    private Set<String> partnerInstitutionsNames;
    private String schoolName;
    private MultiLanguageString degreeName;
    private MultiLanguageString degreeType;
    private String curricularPlanPromulgation;
    private MultiLanguageString mainScientificArea;
    private Set<ScientificAreaBean> scientificAreas;
    private String majorAreaClassification;
    private String firstMinorAreaClassification;
    private String secondMinorAreaClassification;
    private String ectsCredits;
    private MultiLanguageString degreeDuration;
    private String numerusClausus;
    private MultiLanguageString ingressionSpecificConditions;
    private String hasBranches;
    private String branches;
    private String executionYear;
    private Set<CurricularUnitBean> curriculum;
    private String registrationRegime;
    private Set<String> coordinators;
    private String diplomaIssuer;
    private Set<TrainingCoachBean> trainingCoaches;
    private String campus;
    private MultiLanguageString observations;
    private String approvalCommittee;
    private String degreeCoordinationChairmen;
    private MultiLanguageString generalObjectives;
    private MultiLanguageString learningObjectives;
    private MultiLanguageString knowledgeUpdateStrategy;
    private MultiLanguageString schoolStrategy;
    private MultiLanguageString communicationMedia;
    private MultiLanguageString organizationalStructure;
    private MultiLanguageString participationMeans;
    private MultiLanguageString institutionEducationalProject;
    private MultiLanguageString integratedEducationalProject;
    private MultiLanguageString qualityAssuranceMedia;
    private MultiLanguageString qualityAssuranceChairman;
    private MultiLanguageString qualityAssuranceProcedure;
    private MultiLanguageString qualityAssuranceImpact;
    private MultiLanguageString otherAccreditations;
    private Map<String, String> requiredInfrastructure;
    private Map<String, String> requiredEquipment;
    private MultiLanguageString internationalColaboration;
    private MultiLanguageString nationalColaboration;
    private MultiLanguageString institutionalColaboration;
    private MultiLanguageString facultyEvaluationProcedure;
    private MultiLanguageString staffInformation;
    private MultiLanguageString staffQualification;
    private MultiLanguageString staffEvaluation;
    private MultiLanguageString staffImprovement;
    private MultiLanguageString methodologyEffectiveness;
    private MultiLanguageString ectsRequirementsVerification;
    private MultiLanguageString knowledgeRequirementsVerification;
    private MultiLanguageString studentIntegrationMethodology;
    private TreeMap<ExecutionYear, GraduatedReportByYearEntry> educationalEfficiency;
    private MultiLanguageString approvalRateByScientificArea;
    private MultiLanguageString approvalRateMeasurements;
    private Map<String, String> employability;
    private Set<ResearchCentreBean> researchCentres;
    private MultiLanguageString scientificActivityImpact;
    private MultiLanguageString scientificActivitiesWithPartners;
    private MultiLanguageString scientificActivtiesImprovement;
    private MultiLanguageString otherActivities;
    private MultiLanguageString otherActivitiesImpact;
    private MultiLanguageString marketingQuality;
    private Map<String, String> internationalizationLevel;
    private MultiLanguageString similarDegreesEmployability;
    private MultiLanguageString attractiveness;
    private MultiLanguageString closePartnerships;
    private MultiLanguageString ectsJustification;
    private MultiLanguageString ectsCalculationMethod;
    private MultiLanguageString ectsCalculationTeachersInput;
    private MultiLanguageString similarDegrees;
    private MultiLanguageString similarDegreesGoalsComparison;
    private String cooperationProtocols;
    private MultiLanguageString trainingFollowupResources;
    private MultiLanguageString swotStrengths;
    private MultiLanguageString swotWeaknesses;
    private MultiLanguageString swotOpportunities;
    private MultiLanguageString swotThreats;
    private MultiLanguageString swotImprovements;
    private MultiLanguageString swotWeaknessesClassification;
    private MultiLanguageString swotImprovementMetrics;
    private MultiLanguageString conclusions;

    private A3esStudentsBean studentsData;
    private final Set<A3esTeacherBean> teachers = new TreeSet<A3esTeacherBean>(A3esAbstractBean.COMPARE_BY_ID);
    private final Set<A3esCourseBean> courses = new TreeSet<A3esCourseBean>(A3esAbstractBean.COMPARE_BY_ID);

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Set<String> getPartnerInstitutionsNames() {
        return partnerInstitutionsNames;
    }

    public void setPartnerInstitutionsNames(Set<String> partnerInstitutionsNames) {
        this.partnerInstitutionsNames = partnerInstitutionsNames;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public MultiLanguageString getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(MultiLanguageString degreeName) {
        this.degreeName = degreeName;
    }

    public MultiLanguageString getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(MultiLanguageString degreeType) {
        this.degreeType = degreeType;
    }

    public String getCurricularPlanPromulgation() {
        return curricularPlanPromulgation;
    }

    public void setCurricularPlanPromulgation(String curricularPlanPromulgation) {
        this.curricularPlanPromulgation = curricularPlanPromulgation;
    }

    public MultiLanguageString getMainScientificArea() {
        return mainScientificArea;
    }

    public void setMainScientificArea(MultiLanguageString mainScientificArea) {
        this.mainScientificArea = mainScientificArea;
    }

    public Set<ScientificAreaBean> getScientificAreas() {
        return scientificAreas;
    }

    public void setScientificAreas(Set<ScientificAreaBean> scientificAreas) {
        this.scientificAreas = scientificAreas;
    }

    public String getMajorAreaClassification() {
        return majorAreaClassification;
    }

    public void setMajorAreaClassification(String majorAreaClassification) {
        this.majorAreaClassification = majorAreaClassification;
    }

    public String getFirstMinorAreaClassification() {
        return firstMinorAreaClassification;
    }

    public void setFirstMinorAreaClassification(String firstMinorAreaClassification) {
        this.firstMinorAreaClassification = firstMinorAreaClassification;
    }

    public String getSecondMinorAreaClassification() {
        return secondMinorAreaClassification;
    }

    public void setSecondMinorAreaClassification(String secondMinorAreaClassification) {
        this.secondMinorAreaClassification = secondMinorAreaClassification;
    }

    public String getEctsCredits() {
        return ectsCredits;
    }

    public void setEctsCredits(String ectsCredits) {
        this.ectsCredits = ectsCredits;
    }

    public MultiLanguageString getDegreeDuration() {
        return degreeDuration;
    }

    public void setDegreeDuration(MultiLanguageString degreeDuration) {
        this.degreeDuration = degreeDuration;
    }

    public String getNumerusClausus() {
        return numerusClausus;
    }

    public void setNumerusClausus(String numerusClausus) {
        this.numerusClausus = numerusClausus;
    }

    public MultiLanguageString getIngressionSpecificConditions() {
        return ingressionSpecificConditions;
    }

    public void setIngressionSpecificConditions(MultiLanguageString ingressionSpecificConditions) {
        this.ingressionSpecificConditions = ingressionSpecificConditions;
    }

    public String getHasBranches() {
        return hasBranches;
    }

    public void setHasBranches(String hasBranches) {
        this.hasBranches = hasBranches;
    }

    public String getBranches() {
        return branches;
    }

    public void setBranches(String branches) {
        this.branches = branches;
    }

    public String getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(String executionYear) {
        this.executionYear = executionYear;
    }

    public Set<CurricularUnitBean> getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(Set<CurricularUnitBean> curriculum) {
        this.curriculum = curriculum;
    }

    public String getRegistrationRegime() {
        return registrationRegime;
    }

    public void setRegistrationRegime(String registrationRegime) {
        this.registrationRegime = registrationRegime;
    }

    public Set<String> getCoordinators() {
        return coordinators;
    }

    public void setCoordinators(Set<String> coordinators) {
        this.coordinators = coordinators;
    }

    public String getDiplomaIssuer() {
        return diplomaIssuer;
    }

    public void setDiplomaIssuer(String diplomaIssuer) {
        this.diplomaIssuer = diplomaIssuer;
    }

    public Set<TrainingCoachBean> getTrainingCoaches() {
        return trainingCoaches;
    }

    public void setTrainingCoaches(Set<TrainingCoachBean> trainingCoaches) {
        this.trainingCoaches = trainingCoaches;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public MultiLanguageString getObservations() {
        return observations;
    }

    public void setObservations(MultiLanguageString observations) {
        this.observations = observations;
    }

    public String getApprovalCommittee() {
        return approvalCommittee;
    }

    public void setApprovalCommittee(String approvalCommittee) {
        this.approvalCommittee = approvalCommittee;
    }

    public String getDegreeCoordinationChairmen() {
        return degreeCoordinationChairmen;
    }

    public void setDegreeCoordinationChairmen(String degreeCoordinationChairmen) {
        this.degreeCoordinationChairmen = degreeCoordinationChairmen;
    }

    public MultiLanguageString getGeneralObjectives() {
        return generalObjectives;
    }

    public void setGeneralObjectives(MultiLanguageString generalObjectives) {
        this.generalObjectives = generalObjectives;
    }

    public MultiLanguageString getLearningObjectives() {
        return learningObjectives;
    }

    public void setLearningObjectives(MultiLanguageString learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    public MultiLanguageString getKnowledgeUpdateStrategy() {
        return knowledgeUpdateStrategy;
    }

    public void setKnowledgeUpdateStrategy(MultiLanguageString knowledgeUpdateStrategy) {
        this.knowledgeUpdateStrategy = knowledgeUpdateStrategy;
    }

    public MultiLanguageString getSchoolStrategy() {
        return schoolStrategy;
    }

    public void setSchoolStrategy(MultiLanguageString schoolStrategy) {
        this.schoolStrategy = schoolStrategy;
    }

    public MultiLanguageString getCommunicationMedia() {
        return communicationMedia;
    }

    public void setCommunicationMedia(MultiLanguageString communicationMedia) {
        this.communicationMedia = communicationMedia;
    }

    public MultiLanguageString getOrganizationalStructure() {
        return organizationalStructure;
    }

    public void setOrganizationalStructure(MultiLanguageString organizationalStructure) {
        this.organizationalStructure = organizationalStructure;
    }

    public MultiLanguageString getParticipationMeans() {
        return participationMeans;
    }

    public void setParticipationMeans(MultiLanguageString participationMeans) {
        this.participationMeans = participationMeans;
    }

    public MultiLanguageString getInstitutionEducationalProject() {
        return institutionEducationalProject;
    }

    public void setInstitutionEducationalProject(MultiLanguageString institutionEducationalProject) {
        this.institutionEducationalProject = institutionEducationalProject;
    }

    public MultiLanguageString getIntegratedEducationalProject() {
        return integratedEducationalProject;
    }

    public void setIntegratedEducationalProject(MultiLanguageString integratedEducationalProject) {
        this.integratedEducationalProject = integratedEducationalProject;
    }

    public MultiLanguageString getQualityAssuranceMedia() {
        return qualityAssuranceMedia;
    }

    public void setQualityAssuranceMedia(MultiLanguageString qualityAssuranceMedia) {
        this.qualityAssuranceMedia = qualityAssuranceMedia;
    }

    public MultiLanguageString getQualityAssuranceChairman() {
        return qualityAssuranceChairman;
    }

    public void setQualityAssuranceChairman(MultiLanguageString qualityAssuranceChairman) {
        this.qualityAssuranceChairman = qualityAssuranceChairman;
    }

    public MultiLanguageString getQualityAssuranceProcedure() {
        return qualityAssuranceProcedure;
    }

    public void setQualityAssuranceProcedure(MultiLanguageString qualityAssuranceProcedure) {
        this.qualityAssuranceProcedure = qualityAssuranceProcedure;
    }

    public MultiLanguageString getQualityAssuranceImpact() {
        return qualityAssuranceImpact;
    }

    public void setQualityAssuranceImpact(MultiLanguageString qualityAssuranceImpact) {
        this.qualityAssuranceImpact = qualityAssuranceImpact;
    }

    public MultiLanguageString getOtherAccreditations() {
        return otherAccreditations;
    }

    public void setOtherAccreditations(MultiLanguageString otherAccreditations) {
        this.otherAccreditations = otherAccreditations;
    }

    public Map<String, String> getRequiredInfrastructure() {
        return requiredInfrastructure;
    }

    public void setRequiredInfrastructure(Map<String, String> requiredInfrastructure) {
        this.requiredInfrastructure = requiredInfrastructure;
    }

    public Map<String, String> getRequiredEquipment() {
        return requiredEquipment;
    }

    public void setRequiredEquipment(Map<String, String> requiredEquipment) {
        this.requiredEquipment = requiredEquipment;
    }

    public MultiLanguageString getInternationalColaboration() {
        return internationalColaboration;
    }

    public void setInternationalColaboration(MultiLanguageString internationalColaboration) {
        this.internationalColaboration = internationalColaboration;
    }

    public MultiLanguageString getNationalColaboration() {
        return nationalColaboration;
    }

    public void setNationalColaboration(MultiLanguageString nationalColaboration) {
        this.nationalColaboration = nationalColaboration;
    }

    public MultiLanguageString getInstitutionalColaboration() {
        return institutionalColaboration;
    }

    public void setInstitutionalColaboration(MultiLanguageString institutionalColaboration) {
        this.institutionalColaboration = institutionalColaboration;
    }

    public MultiLanguageString getFacultyEvaluationProcedure() {
        return facultyEvaluationProcedure;
    }

    public void setFacultyEvaluationProcedure(MultiLanguageString facultyEvaluationProcedure) {
        this.facultyEvaluationProcedure = facultyEvaluationProcedure;
    }

    public MultiLanguageString getStaffInformation() {
        return staffInformation;
    }

    public void setStaffInformation(MultiLanguageString staffInformation) {
        this.staffInformation = staffInformation;
    }

    public MultiLanguageString getStaffQualification() {
        return staffQualification;
    }

    public void setStaffQualification(MultiLanguageString staffQualification) {
        this.staffQualification = staffQualification;
    }

    public MultiLanguageString getStaffEvaluation() {
        return staffEvaluation;
    }

    public void setStaffEvaluation(MultiLanguageString staffEvaluation) {
        this.staffEvaluation = staffEvaluation;
    }

    public MultiLanguageString getStaffImprovement() {
        return staffImprovement;
    }

    public void setStaffImprovement(MultiLanguageString staffImprovement) {
        this.staffImprovement = staffImprovement;
    }

    public MultiLanguageString getMethodologyEffectiveness() {
        return methodologyEffectiveness;
    }

    public void setMethodologyEffectiveness(MultiLanguageString methodologyEffectiveness) {
        this.methodologyEffectiveness = methodologyEffectiveness;
    }

    public MultiLanguageString getEctsRequirementsVerification() {
        return ectsRequirementsVerification;
    }

    public void setEctsRequirementsVerification(MultiLanguageString ectsRequirementsVerification) {
        this.ectsRequirementsVerification = ectsRequirementsVerification;
    }

    public MultiLanguageString getKnowledgeRequirmentsVerification() {
        return knowledgeRequirementsVerification;
    }

    public void setKnowledgeRequirmentsVerification(MultiLanguageString knowledgeRequirementsVerification) {
        this.knowledgeRequirementsVerification = knowledgeRequirementsVerification;
    }

    public MultiLanguageString getStudentIntegrationMethodology() {
        return studentIntegrationMethodology;
    }

    public void setStudentIntegrationMethodology(MultiLanguageString studentIntegrationMethodology) {
        this.studentIntegrationMethodology = studentIntegrationMethodology;
    }

    public TreeMap<ExecutionYear, GraduatedReportByYearEntry> getEducationalEfficiency() {
        return educationalEfficiency;
    }

    public void setEducationalEfficiency(TreeMap<ExecutionYear, GraduatedReportByYearEntry> educationalEfficiency) {
        this.educationalEfficiency = educationalEfficiency;
    }

    public MultiLanguageString getApprovalRateByScientificArea() {
        return approvalRateByScientificArea;
    }

    public void setApprovalRateByScientificArea(MultiLanguageString approvalRateByScientificArea) {
        this.approvalRateByScientificArea = approvalRateByScientificArea;
    }

    public MultiLanguageString getApprovalRateMeasurements() {
        return approvalRateMeasurements;
    }

    public void setApprovalRateMeasurements(MultiLanguageString approvalRateMeasurements) {
        this.approvalRateMeasurements = approvalRateMeasurements;
    }

    public Map<String, String> getEmployability() {
        return employability;
    }

    public void setEmployability(Map<String, String> employability) {
        this.employability = employability;
    }

    public Set<ResearchCentreBean> getResearchCentres() {
        return researchCentres;
    }

    public void setResearchCentres(Set<ResearchCentreBean> researchCentres) {
        this.researchCentres = researchCentres;
    }

    public MultiLanguageString getScientificActivityImpact() {
        return scientificActivityImpact;
    }

    public void setScientificActivityImpact(MultiLanguageString scientificActivityImpact) {
        this.scientificActivityImpact = scientificActivityImpact;
    }

    public MultiLanguageString getScientificActivitiesWithPartners() {
        return scientificActivitiesWithPartners;
    }

    public void setScientificActivitiesWithPartners(MultiLanguageString scientificActivitiesWithPartners) {
        this.scientificActivitiesWithPartners = scientificActivitiesWithPartners;
    }

    public MultiLanguageString getScientificActivtiesImprovement() {
        return scientificActivtiesImprovement;
    }

    public void setScientificActivtiesImprovement(MultiLanguageString scientificActivtiesImprovement) {
        this.scientificActivtiesImprovement = scientificActivtiesImprovement;
    }

    public MultiLanguageString getOtherActivities() {
        return otherActivities;
    }

    public void setOtherActivities(MultiLanguageString otherActivities) {
        this.otherActivities = otherActivities;
    }

    public MultiLanguageString getOtherActivitiesImpact() {
        return otherActivitiesImpact;
    }

    public void setOtherActivitiesImpact(MultiLanguageString otherActivitiesImpact) {
        this.otherActivitiesImpact = otherActivitiesImpact;
    }

    public MultiLanguageString getMarketingQuality() {
        return marketingQuality;
    }

    public void setMarketingQuality(MultiLanguageString marketingQuality) {
        this.marketingQuality = marketingQuality;
    }

    public Map<String, String> getInternationalizationLevel() {
        return internationalizationLevel;
    }

    public void setInternationalizationLevel(Map<String, String> internationalizationLevel) {
        this.internationalizationLevel = internationalizationLevel;
    }

    public MultiLanguageString getSimilarDegreesEmployability() {
        return similarDegreesEmployability;
    }

    public void setSimilarDegreesEmployability(MultiLanguageString similarDegreesEmployability) {
        this.similarDegreesEmployability = similarDegreesEmployability;
    }

    public MultiLanguageString getAttractiveness() {
        return attractiveness;
    }

    public void setAttractiveness(MultiLanguageString attractiveness) {
        this.attractiveness = attractiveness;
    }

    public MultiLanguageString getClosePartnerships() {
        return closePartnerships;
    }

    public void setClosePartnerships(MultiLanguageString closePartnerships) {
        this.closePartnerships = closePartnerships;
    }

    public MultiLanguageString getEctsJustification() {
        return ectsJustification;
    }

    public void setEctsJustification(MultiLanguageString ectsJustification) {
        this.ectsJustification = ectsJustification;
    }

    public MultiLanguageString getEctsCalculationMethod() {
        return ectsCalculationMethod;
    }

    public void setEctsCalculationMethod(MultiLanguageString ectsCalculationMethod) {
        this.ectsCalculationMethod = ectsCalculationMethod;
    }

    public MultiLanguageString getEctsCalculationTeachersInput() {
        return ectsCalculationTeachersInput;
    }

    public void setEctsCalculationTeachersInput(MultiLanguageString ectsCalculationTeachersInput) {
        this.ectsCalculationTeachersInput = ectsCalculationTeachersInput;
    }

    public MultiLanguageString getSimilarDegrees() {
        return similarDegrees;
    }

    public void setSimilarDegrees(MultiLanguageString similarDegrees) {
        this.similarDegrees = similarDegrees;
    }

    public MultiLanguageString getSimilarDegreesGoalsComparison() {
        return similarDegreesGoalsComparison;
    }

    public void setSimilarDegreesGoalsComparison(MultiLanguageString similarDegreesGoalsComparison) {
        this.similarDegreesGoalsComparison = similarDegreesGoalsComparison;
    }

    public String getCooperationProtocols() {
        return cooperationProtocols;
    }

    public void setCooperationProtocols(String cooperationProtocols) {
        this.cooperationProtocols = cooperationProtocols;
    }

    public MultiLanguageString getTrainingFollowupResources() {
        return trainingFollowupResources;
    }

    public void setTrainingFollowupResources(MultiLanguageString trainingFollowupResources) {
        this.trainingFollowupResources = trainingFollowupResources;
    }

    public MultiLanguageString getSwotStrengths() {
        return swotStrengths;
    }

    public void setSwotStrengths(MultiLanguageString swotStrengths) {
        this.swotStrengths = swotStrengths;
    }

    public MultiLanguageString getSwotWeaknesses() {
        return swotWeaknesses;
    }

    public void setSwotWeaknesses(MultiLanguageString swotWeaknesses) {
        this.swotWeaknesses = swotWeaknesses;
    }

    public MultiLanguageString getSwotOpportunities() {
        return swotOpportunities;
    }

    public void setSwotOpportunities(MultiLanguageString swotOpportunities) {
        this.swotOpportunities = swotOpportunities;
    }

    public MultiLanguageString getSwotThreats() {
        return swotThreats;
    }

    public void setSwotThreats(MultiLanguageString swotThreats) {
        this.swotThreats = swotThreats;
    }

    public MultiLanguageString getSwotImprovements() {
        return swotImprovements;
    }

    public void setSwotImprovements(MultiLanguageString swotImprovements) {
        this.swotImprovements = swotImprovements;
    }

    public MultiLanguageString getSwotWeaknessesClassification() {
        return swotWeaknessesClassification;
    }

    public void setSwotWeaknessesClassification(MultiLanguageString swotWeaknessesClassification) {
        this.swotWeaknessesClassification = swotWeaknessesClassification;
    }

    public MultiLanguageString getSwotImprovementMetrics() {
        return swotImprovementMetrics;
    }

    public void setSwotImprovementMetrics(MultiLanguageString swotImprovementMetrics) {
        this.swotImprovementMetrics = swotImprovementMetrics;
    }

    public MultiLanguageString getConclusions() {
        return conclusions;
    }

    public void setConclusions(MultiLanguageString conclusions) {
        this.conclusions = conclusions;
    }

    public A3esStudentsBean getStudentsData() {
        return studentsData;
    }

    public void setStudentsData(A3esStudentsBean studentsData) {
        this.studentsData = studentsData;
    }

    public Set<A3esTeacherBean> getTeachers() {
        return teachers;
    }

    public void addTeacher(A3esTeacherBean teacher) {
        teachers.add(teacher);
    }

    public void addTeachers(Collection<A3esTeacherBean> teachers) {
        this.teachers.addAll(teachers);
    }

    public Set<A3esCourseBean> getCourses() {
        return courses;
    }

    public void addCourse(A3esCourseBean course) {
        courses.add(course);
    }

    public void addCourses(Collection<A3esCourseBean> courses) {
        this.courses.addAll(courses);
    }

    public class ScientificAreaBean extends A3esAbstractBean {

        private static final long serialVersionUID = 1L;

        private MultiLanguageString scientificArea;
        private String sigla;
        private String mandatoryECTS;
        private String optionalECTS;

        public MultiLanguageString getScientificArea() {
            return scientificArea;
        }

        public void setScientificArea(MultiLanguageString scientificArea) {
            this.scientificArea = scientificArea;
        }

        public String getSigla() {
            return sigla;
        }

        public void setSigla(String sigla) {
            this.sigla = sigla;
        }

        public String getMandatoryECTS() {
            return mandatoryECTS;
        }

        public void setMandatoryECTS(String mandatoryECTS) {
            this.mandatoryECTS = mandatoryECTS;
        }

        public String getOptionalECTS() {
            return optionalECTS;
        }

        public void setOptionalECTS(String optionalECTS) {
            this.optionalECTS = optionalECTS;
        }
    }

    public class CurricularUnitBean extends A3esAbstractBean {

        private static final long serialVersionUID = 1L;

        private MultiLanguageString curricularUnitName;
        private MultiLanguageString scientificArea;
        private String duration;
        private String workingHours;
        private String classHours;
        private String ects;
        private MultiLanguageString observations;
        private String curricularPeriod;
        private String group;

        public MultiLanguageString getCurricularUnitName() {
            return curricularUnitName;
        }

        public void setCurricularUnitName(MultiLanguageString curricularUnitName) {
            this.curricularUnitName = curricularUnitName;
        }

        public MultiLanguageString getScientificArea() {
            return scientificArea;
        }

        public void setScientificArea(MultiLanguageString scientificArea) {
            this.scientificArea = scientificArea;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getWorkingHours() {
            return workingHours;
        }

        public void setWorkingHours(String workingHours) {
            this.workingHours = workingHours;
        }

        public String getClassHours() {
            return classHours;
        }

        public void setClassHours(String classHours) {
            this.classHours = classHours;
        }

        public String getEcts() {
            return ects;
        }

        public void setEcts(String ects) {
            this.ects = ects;
        }

        public MultiLanguageString getObservations() {
            return observations;
        }

        public void setObservations(MultiLanguageString observations) {
            this.observations = observations;
        }

        public String getCurricularPeriod() {
            return curricularPeriod;
        }

        public void setCurricularPeriod(String curricularPeriod) {
            this.curricularPeriod = curricularPeriod;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

    }

    public class TrainingCoachBean extends A3esAbstractBean {

        private static final long serialVersionUID = 1L;

        private String coachName;
        private String coachInstitution;
        private String professionalCategory;
        private String education;
        private String yearsOfService;

        public String getCoachName() {
            return coachName;
        }

        public void setCoachName(String coachName) {
            this.coachName = coachName;
        }

        public String getCoachInstitution() {
            return coachInstitution;
        }

        public void setCoachInstitution(String coachInstitution) {
            this.coachInstitution = coachInstitution;
        }

        public String getProfessionalCategory() {
            return professionalCategory;
        }

        public void setProfessionalCategory(String professionalCategory) {
            this.professionalCategory = professionalCategory;
        }

        public String getEducation() {
            return education;
        }

        public void setEducation(String education) {
            this.education = education;
        }

        public String getYearsOfService() {
            return yearsOfService;
        }

        public void setYearsOfService(String yearsOfService) {
            this.yearsOfService = yearsOfService;
        }
    }

    public class ResearchCentreBean extends A3esAbstractBean {

        private static final long serialVersionUID = 1L;

        private MultiLanguageString researchCentre;
        private MultiLanguageString fctClassification;
        private String associatedInstitution;
        private MultiLanguageString observations;

        public MultiLanguageString getResearchCentre() {
            return researchCentre;
        }

        public void setResearchCentre(MultiLanguageString researchCentre) {
            this.researchCentre = researchCentre;
        }

        public MultiLanguageString getFctClassification() {
            return fctClassification;
        }

        public void setFctClassification(MultiLanguageString fctClassification) {
            this.fctClassification = fctClassification;
        }

        public String getAssociatedInstitution() {
            return associatedInstitution;
        }

        public void setAssociatedInstitution(String associatedInstitution) {
            this.associatedInstitution = associatedInstitution;
        }

        public MultiLanguageString getObservations() {
            return observations;
        }

        public void setObservations(MultiLanguageString observations) {
            this.observations = observations;
        }
    }
}
