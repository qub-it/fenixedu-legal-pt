package org.fenixedu.legalpt.services.a3es.process;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.EN;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PT;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.SEPARATOR_1;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.createEmptyMLS;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.createMLS;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.i18n;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.label;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Coordinator;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.domain.DegreeOfficialPublication;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.CycleCourseGroup;
import org.fenixedu.academic.domain.degreeStructure.OptionalCurricularCourse;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.studentCurriculum.CycleCurriculumGroup;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.dto.a3es.A3esDegreeBean;
import org.fenixedu.legalpt.dto.a3es.A3esDegreeBean.CurricularUnitBean;
import org.fenixedu.legalpt.dto.a3es.A3esDegreeBean.ResearchCentreBean;
import org.fenixedu.legalpt.dto.a3es.A3esDegreeBean.ScientificAreaBean;
import org.fenixedu.legalpt.dto.a3es.A3esDegreeBean.TrainingCoachBean;
import org.joda.time.DateTime;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class A3esHarvestDegreeDataService {

    public static class GraduatedReportByYearEntry {

        public int graduatedCount;

        public int graduatedNYears;

        public int graduatedNPlus1Years;

        public int graduatedNPlus2Years;

        public int graduatedNPlusMore2Years;
    }

    private final DegreeCurricularPlan degreeCurricularPlan;
    private final Degree degree;
    private final ExecutionYear year;
    private final A3esDegreeBean data;

    public A3esHarvestDegreeDataService(final DegreeCurricularPlan degreeCurricularPlan, final ExecutionYear executionYear) {
        this.degreeCurricularPlan = degreeCurricularPlan;
        this.degree = degreeCurricularPlan.getDegree();
        this.year = executionYear;
        this.data = new A3esDegreeBean();
    }

    public A3esDegreeBean processA3ES() {

        fillInstitutionName();
        fillPartnerInstitutionsNames();
        fillSchoolName();
        fillDegreeName();
        fillDegreeType();
        fillCurricularPlanPromulgation();
        fillMainScientificArea();
        fillScientificAreas();
        fillMajorAreaClassification();
        fillFirstMinorAreaClassification();
        fillSecondMinorAreaClassification();
        fillEctsCredits();
        fillDegreeDuration();
        fillNumerusClausus();
        fillIngressionSpecificConditions();
        fillHasBranches();
        fillBranches();
        fillExecutionYear();
        fillCurriculum();
        fillRegistrationRegime();
        fillCoordinators();
        fillDiplomaIssuer();
        fillTrainingCoaches();
        fillCampus();
        fillObservations();
        fillApprovalCommittee();
        fillDegreeCoordinationChairmen();
        fillGeneralObjectives();
        fillLearningObjectives();
        fillKnowledgeUpdateStrategy();
        fillSchoolStrategy();
        fillCommunicationMedia();
        fillOrganizationalStructure();
        fillParticipationMeans();
        fillInstitutionEducationalProject();
        fillIntegratedEducationalProject();
        fillQualityAssuranceMedia();
        fillQualityAssuranceChairman();
        fillQualityAssuranceProcedure();
        fillQualityAssuranceImpact();
        fillOtherAccreditations();
        fillRequiredInfrastructure();
        fillRequiredEquipment();
        fillInternationalColaboration();
        fillNationalColaboration();
        fillInstitutionalColaboration();
        fillFacultyEvaluationProcedure();
        fillStaffInformation();
        fillStaffQualification();
        fillStaffEvaluation();
        fillStaffImprovement();
        fillMethodologyEffectiveness();
        fillEctsRequirementsVerification();
        fillKnowledgeRequirementsVerification();
        fillStudentIntegrationMethodology();
        fillEducationalEfficiency();
        fillApprovalRateByScientificArea();
        fillApprovalRateMeasurements();
        fillEmployability();
        fillResearchCentres();
        fillScientificActivityImpact();
        fillScientificActivitiesWithPartners();
        fillScientificActivtiesImprovement();
        fillOtherActivities();
        fillOtherActivitiesImpact();
        fillMarketingQuality();
        fillInternationalizationLevel();
        fillSimilarDegreesEmployability();
        fillAttractiveness();
        fillClosePartnerships();
        fillEctsJustification();
        fillEctsCalculationMethod();
        fillEctsCalculationTeachersInput();
        fillSimilarDegrees();
        fillSimilarDegreesGoalsComparison();
        fillCooperationProtocols();
        fillTrainingFollowupResources();
        fillSwotStrengths();
        fillSwotWeaknesses();
        fillSwotOpportunities();
        fillSwotThreats();
        fillConclusions();
        fillSwotImprovements();
        fillSwotWeaknessesClassification();
        fillSwotImprovementMetrics();

        A3esHarvestStudentsDataService studentsDataHarvester =
                new A3esHarvestStudentsDataService(this.degreeCurricularPlan, year);
        this.data.setStudentsData(studentsDataHarvester.getStudentsData());

        A3esHarvestCoursesDataService coursesHarvester = null; // new A3esHarvestCoursesDataService(this.degreeCurricularPlan, year);
//        this.data.addCourses(coursesHarvester.getCoursesData());

        A3esHarvestTeachersDataService teachersHarvester = null; // new A3esHarvestTeachersDataService(this.degreeCurricularPlan, year);
//        this.data.addTeachers(teachersHarvester.getTeachersData());

        return this.data;
    }

    private void fillInstitutionName() {
        Unit institutionUnit = Bennu.getInstance().getInstitutionUnit();

        if (institutionUnit.isUniversityUnit()) {
            this.data.setInstitutionName(institutionUnit.getName());
            return;
        }

        for (Unit parentUnit : institutionUnit.getParentUnits()) {

            if (!parentUnit.isUniversityUnit()) {
                continue;
            }

            this.data.setInstitutionName(parentUnit.getName());
            break;
        }
    }

    private void fillPartnerInstitutionsNames() {
        Set<String> partnerInstitutions = new HashSet<String>();
        this.data.setPartnerInstitutionsNames(partnerInstitutions);
    }

    private void fillSchoolName() {
        String schoolName = Bennu.getInstance().getInstitutionUnit().getName();
        this.data.setSchoolName(schoolName);
    }

    private void fillDegreeName() {
        this.data.setDegreeName(this.degree.getNameI18N());
    }

    private void fillDegreeType() {
        final DegreeType degreeType = this.degreeCurricularPlan.getDegreeType();
        this.data.setDegreeType(createMLS(degreeType.getName().getContent(PT), degreeType.getName().getContent(EN)));
    }

    private void fillCurricularPlanPromulgation() {
        final DegreeOfficialPublication officialPublication = this.degree.getOfficialPublication(new DateTime());

        if (officialPublication != null) {
            String curricularPlanPromulgation = officialPublication.getOfficialReference();
            this.data.setCurricularPlanPromulgation(curricularPlanPromulgation);
        }
    }

    private void fillMainScientificArea() {
        this.data.setMainScientificArea(getMostRecentDegreeInfo().getPrevailingScientificArea());
    }

    private DegreeInfo getMostRecentDegreeInfo() {
        return this.degree.getMostRecentDegreeInfo(year.getAcademicInterval());
    }

    private void fillScientificAreas() {
        Set<ScientificAreaBean> scientificAreas = new HashSet<ScientificAreaBean>();
        ScientificAreaBean bean = this.data.new ScientificAreaBean();
        bean.setScientificArea(createEmptyMLS());
        bean.setSigla(null);
        bean.setMandatoryECTS(null);
        bean.setOptionalECTS(null);
        scientificAreas.add(bean);
        this.data.setScientificAreas(scientificAreas);
    }

    private void fillMajorAreaClassification() {
        this.data.setMajorAreaClassification(null);
    }

    private void fillFirstMinorAreaClassification() {
        this.data.setFirstMinorAreaClassification(null);
    }

    private void fillSecondMinorAreaClassification() {
        this.data.setSecondMinorAreaClassification(null);
    }

    private void fillEctsCredits() {
        double ectsCredits = 0;
        for (final CycleCourseGroup courseGroup : this.degreeCurricularPlan.getRoot().getCycleCourseGroups()) {
            courseGroup.getMinEctsCredits(year.getFirstExecutionPeriod());
        }

        this.data.setEctsCredits(String.valueOf(ectsCredits));

    }

    private void fillDegreeDuration() {
        final String degreeDuration = this.degreeCurricularPlan.getDegreeDuration().toString();
        this.data.setDegreeDuration(createMLS(degreeDuration, degreeDuration));
    }

    private void fillNumerusClausus() {
        this.data.setNumerusClausus(null);
    }

    private void fillIngressionSpecificConditions() {
        this.data.setIngressionSpecificConditions(getMostRecentDegreeInfo().getTestIngression());
    }

    private void fillHasBranches() {
//        String hasBranches = this.degreeCurricularPlan.getCourseGroupBranches().isEmpty() ? label("yes") : label("no");
//        a3esDegree.setHasBranches(hasBranches);
    }

    private void fillBranches() {

        final Collection<CourseGroup> activeBranches = null;
//                Collections2.filter(this.degreeCurricularPlan.getCourseGroupBranches(), new Predicate<CourseGroup>() {
//                    @Override
//                    public boolean apply(CourseGroup courseGroup) {
//                        return !courseGroup.getParentContextsByExecutionYear(executionYear).isEmpty();
//                    }
//                });

        final StringBuilder branchesString = new StringBuilder();
        for (final CourseGroup branch : activeBranches) {
            branchesString.append(branch.getName()).append(SEPARATOR_1);
        }

        if (branchesString.toString().endsWith(SEPARATOR_1)) {
            branchesString.delete(branchesString.length() - SEPARATOR_1.length(), branchesString.length());
        }

        this.data.setBranches(branchesString.toString());
    }

    private void fillExecutionYear() {
        this.data.setExecutionYear(year.getQualifiedName());
    }

    private void fillCurriculum() {

        final Collection<CourseGroup> activeCourseGroups =
                Collections2.filter(this.degreeCurricularPlan.getAllCoursesGroups(), new Predicate<CourseGroup>() {

                    @Override
                    public boolean apply(CourseGroup arg0) {
                        return !arg0.getParentContextsByExecutionYear(year).isEmpty();
                    }
                });

        Set<CurricularUnitBean> curriculum = new HashSet<CurricularUnitBean>();
        for (final CourseGroup activeCourseGroup : activeCourseGroups) {
            for (final Context context : activeCourseGroup.getChildContexts(CurricularCourse.class)) {

                if (!context.isValid(year)) {
                    continue;
                }

                final CurricularCourse curricularCourse = (CurricularCourse) context.getChildDegreeModule();

                CurricularUnitBean bean = this.data.new CurricularUnitBean();
                bean.setCurricularUnitName(curricularCourse.getNameI18N());

                if (curricularCourse.isOptionalCurricularCourse()) {
                    bean.setScientificArea(createEmptyMLS());
                    bean.setDuration(null);
                    final OptionalCurricularCourse optionalCurricularCourse = (OptionalCurricularCourse) curricularCourse;
                    bean.setEcts(optionalCurricularCourse.getMinEctsCredits(year.getFirstExecutionPeriod()).toString());
                    bean.setClassHours(null);
                    bean.setWorkingHours(null);

                } else {
                    bean.setScientificArea(curricularCourse.getCompetenceCourse().getScientificAreaUnit().getNameI18n());
                    bean.setDuration(i18n(
                            curricularCourse.getCompetenceCourse().getAcademicPeriod(year.getFirstExecutionPeriod()).getName()));
                    bean.setEcts(curricularCourse.getEctsCredits(year).toString());
                    bean.setClassHours(String
                            .valueOf(curricularCourse.getCompetenceCourse().getContactLoad(year.getFirstExecutionPeriod())));
                    bean.setWorkingHours(String.valueOf(curricularCourse.getAutonomousWorkHours(year.getFirstExecutionPeriod())));

                }

                bean.setObservations(curricularCourse.isOptionalCurricularCourse()
                        || activeCourseGroup.isOptionalCourseGroup() ? createMLS("Optativa", "Optional") : createEmptyMLS());
                bean.setGroup(activeCourseGroup.getOneFullName());
                bean.setCurricularPeriod(context.getCurricularPeriod().getLabel());

                curriculum.add(bean);
            }

        }

        this.data.setCurriculum(curriculum);

    }

    private void fillRegistrationRegime() {
        this.data.setRegistrationRegime(null);
    }

    private void fillCoordinators() {
        Set<String> coordinatorList = new HashSet<String>();

        for (ExecutionDegree executionDegree : this.degree.getExecutionDegrees()) {
            if (executionDegree.getExecutionYear() == year) {
                for (Coordinator coordinator : executionDegree.getCoordinatorsListSet()) {
                    coordinatorList.add(coordinator.getPerson().getName()
                            + (coordinator.isResponsible() ? " (" + label("responsable") + ")" : ""));
                }
            }
        }

        this.data.setCoordinators(coordinatorList);
    }

    private void fillDiplomaIssuer() {
        this.data.setDiplomaIssuer(null);
    }

    private void fillTrainingCoaches() {
        Set<TrainingCoachBean> trainingCoaches = new HashSet<TrainingCoachBean>();
        TrainingCoachBean bean = this.data.new TrainingCoachBean();
        bean.setCoachName(null);
        bean.setCoachInstitution(null);
        bean.setProfessionalCategory(null);
        bean.setEducation(null);
        bean.setYearsOfService(null);
        trainingCoaches.add(bean);
        this.data.setTrainingCoaches(trainingCoaches);
    }

    private void fillCampus() {
        String campus = null;

        if (this.degreeCurricularPlan.getCampus(year) != null) {
            campus = this.degreeCurricularPlan.getCampus(year).getName();
        }

        this.data.setCampus(campus);
    }

    private void fillObservations() {
        this.data.setObservations(createEmptyMLS());
    }

    private void fillApprovalCommittee() {
        String approvalCommittee = null;
        this.data.setApprovalCommittee(approvalCommittee);
    }

    private void fillDegreeCoordinationChairmen() {
        String degreeCoordinationChairmen = null;
        this.data.setDegreeCoordinationChairmen(degreeCoordinationChairmen);
    }

    private void fillGeneralObjectives() {
        this.data.setGeneralObjectives(createEmptyMLS());
    }

    private void fillLearningObjectives() {
        this.data.setLearningObjectives(createEmptyMLS());
    }

    private void fillKnowledgeUpdateStrategy() {
        this.data.setKnowledgeUpdateStrategy(createEmptyMLS());
    }

    private void fillSchoolStrategy() {
        this.data.setSchoolStrategy(createEmptyMLS());
    }

    private void fillCommunicationMedia() {
        this.data.setCommunicationMedia(createEmptyMLS());
    }

    private void fillOrganizationalStructure() {
        this.data.setOrganizationalStructure(createEmptyMLS());
    }

    private void fillParticipationMeans() {
        this.data.setParticipationMeans(createEmptyMLS());
    }

    private void fillInstitutionEducationalProject() {
        this.data.setInstitutionEducationalProject(createEmptyMLS());
    }

    private void fillIntegratedEducationalProject() {
        this.data.setIntegratedEducationalProject(createEmptyMLS());
    }

    private void fillQualityAssuranceMedia() {
        this.data.setQualityAssuranceMedia(createEmptyMLS());
    }

    private void fillQualityAssuranceChairman() {
        this.data.setQualityAssuranceChairman(null);
    }

    private void fillQualityAssuranceProcedure() {
        this.data.setQualityAssuranceProcedure(null);
    }

    private void fillQualityAssuranceImpact() {
        this.data.setQualityAssuranceImpact(null);
    }

    private void fillOtherAccreditations() {
        this.data.setOtherAccreditations(null);
    }

    private void fillRequiredInfrastructure() {
        Map<String, String> requiredInfrastructure = new HashMap<String, String>();
        this.data.setRequiredInfrastructure(requiredInfrastructure);
    }

    private void fillRequiredEquipment() {
        Map<String, String> requiredEquipment = new HashMap<String, String>();
        this.data.setRequiredEquipment(requiredEquipment);
    }

    private void fillInternationalColaboration() {
        this.data.setInternationalColaboration(null);
    }

    private void fillNationalColaboration() {
        this.data.setNationalColaboration(null);
    }

    private void fillInstitutionalColaboration() {
        this.data.setInstitutionalColaboration(null);
    }

    private void fillFacultyEvaluationProcedure() {
        this.data.setFacultyEvaluationProcedure(null);
    }

    private void fillStaffInformation() {
        this.data.setStaffInformation(null);
    }

    private void fillStaffQualification() {
        this.data.setStaffQualification(null);
    }

    private void fillStaffEvaluation() {
        this.data.setStaffEvaluation(null);
    }

    private void fillStaffImprovement() {
        this.data.setStaffImprovement(null);
    }

    private void fillMethodologyEffectiveness() {
        this.data.setMethodologyEffectiveness(null);
    }

    private void fillEctsRequirementsVerification() {
        this.data.setEctsRequirementsVerification(null);
    }

    private void fillKnowledgeRequirementsVerification() {
        this.data.setKnowledgeRequirmentsVerification(null);
    }

    private void fillStudentIntegrationMethodology() {
        this.data.setStudentIntegrationMethodology(null);
    }

    private void fillEducationalEfficiency() {

        final Collection<ExecutionYear> executionYearsToCalculate =
                Arrays.asList(year.getPreviousExecutionYear(), year.getPreviousExecutionYear().getPreviousExecutionYear(),
                        year.getPreviousExecutionYear().getPreviousExecutionYear().getPreviousExecutionYear());

        final Multimap<ExecutionYear, Registration> concludedByYear = HashMultimap.create();
        for (final Registration registration : this.degreeCurricularPlan.getStudentCurricularPlansSet().stream()
                .map(scp -> scp.getRegistration()).collect(Collectors.toSet())) {

            if (!registration.hasConcluded()) {
                continue;
            }

            final CycleCurriculumGroup cycleCurriculumGroup =
                    registration.getLastStudentCurricularPlan().getCycle(registration.getDegreeType().getLastOrderedCycleType());
            if (cycleCurriculumGroup == null || !cycleCurriculumGroup.isCycleCurriculumGroup()) {
                continue;
            }

            final RegistrationConclusionBean conclusionBean = new RegistrationConclusionBean(registration, cycleCurriculumGroup);
            if (executionYearsToCalculate.contains(conclusionBean.getConclusionYear())) {
                concludedByYear.put(conclusionBean.getConclusionYear(), registration);
            }

        }

        final TreeMap<ExecutionYear, GraduatedReportByYearEntry> educationalEfficiency = Maps.newTreeMap();
        final Integer degreeDuration = this.degreeCurricularPlan.getDegreeDuration();
        for (final ExecutionYear executionYear : executionYearsToCalculate) {

            if (!educationalEfficiency.containsKey(executionYear)) {
                educationalEfficiency.put(executionYear, new GraduatedReportByYearEntry());
            }

            final GraduatedReportByYearEntry report = educationalEfficiency.get(executionYear);

            final Collection<Registration> concludedInExecutionYear = concludedByYear.get(executionYear);

            if (concludedInExecutionYear == null) {
                continue;
            }

            report.graduatedCount = concludedInExecutionYear.size();

            final Multimap<Integer, Registration> concludedByYearDuration = HashMultimap.create();
            for (final Registration registration : concludedInExecutionYear) {
                final Integer yearsDuration = null; // registration.getEnrolmentYearsIncludingPrecedentRegistrations().size();
                if (yearsDuration.intValue() >= degreeDuration.intValue()) {
                    concludedByYearDuration.put(yearsDuration, registration);
                }
            }

            for (final Map.Entry<Integer, Collection<Registration>> entry : concludedByYearDuration.asMap().entrySet()) {

                if (entry.getKey().intValue() == degreeDuration.intValue()) {
                    report.graduatedNYears += entry.getValue().size();
                } else if (entry.getKey().intValue() == (degreeDuration.intValue() + 1)) {
                    report.graduatedNPlus1Years += entry.getValue().size();
                } else if (entry.getKey().intValue() == (degreeDuration.intValue() + 2)) {
                    report.graduatedNPlus2Years += entry.getValue().size();
                } else {
                    report.graduatedNPlusMore2Years += entry.getValue().size();
                }
            }

        }

        this.data.setEducationalEfficiency(educationalEfficiency);
    }

    private void fillApprovalRateByScientificArea() {
        this.data.setApprovalRateByScientificArea(null);
    }

    private void fillApprovalRateMeasurements() {
        this.data.setApprovalRateMeasurements(null);
    }

    private void fillEmployability() {
        Map<String, String> employability = new HashMap<String, String>();
        this.data.setEmployability(employability);
    }

    private void fillResearchCentres() {
        Set<ResearchCentreBean> researchCentres = new HashSet<ResearchCentreBean>();
        ResearchCentreBean bean = this.data.new ResearchCentreBean();
        bean.setResearchCentre(createEmptyMLS());
        bean.setFctClassification(createEmptyMLS());
        bean.setAssociatedInstitution(null);
        bean.setObservations(createEmptyMLS());
        researchCentres.add(bean);
        this.data.setResearchCentres(researchCentres);
    }

    private void fillScientificActivityImpact() {
        this.data.setScientificActivityImpact(null);
    }

    private void fillScientificActivitiesWithPartners() {
        this.data.setScientificActivitiesWithPartners(null);
    }

    private void fillScientificActivtiesImprovement() {
        this.data.setScientificActivtiesImprovement(null);
    }

    private void fillOtherActivities() {
        this.data.setOtherActivities(null);
    }

    private void fillOtherActivitiesImpact() {
        this.data.setOtherActivitiesImpact(null);
    }

    private void fillMarketingQuality() {
        this.data.setMarketingQuality(null);
    }

    private void fillInternationalizationLevel() {
        Map<String, String> internationalizationLevel = new HashMap<String, String>();
        this.data.setInternationalizationLevel(internationalizationLevel);
    }

    private void fillSimilarDegreesEmployability() {
        this.data.setSimilarDegreesEmployability(null);
    }

    private void fillAttractiveness() {
        this.data.setAttractiveness(null);
    }

    private void fillClosePartnerships() {
        this.data.setClosePartnerships(null);
    }

    private void fillEctsJustification() {
        this.data.setEctsJustification(null);
    }

    private void fillEctsCalculationMethod() {
        this.data.setEctsCalculationMethod(null);
    }

    private void fillEctsCalculationTeachersInput() {
        this.data.setEctsCalculationTeachersInput(null);
    }

    private void fillSimilarDegrees() {
        this.data.setSimilarDegrees(null);
    }

    private void fillSimilarDegreesGoalsComparison() {
        this.data.setSimilarDegreesGoalsComparison(null);
    }

    private void fillCooperationProtocols() {
        this.data.setCooperationProtocols(null);
    }

    private void fillTrainingFollowupResources() {
        this.data.setTrainingFollowupResources(null);
    }

    private void fillSwotStrengths() {
        this.data.setSwotStrengths(null);
    }

    private void fillSwotWeaknesses() {
        this.data.setSwotWeaknesses(null);
    }

    private void fillSwotOpportunities() {
        this.data.setSwotOpportunities(null);
    }

    private void fillSwotThreats() {
        this.data.setSwotThreats(null);
    }

    private void fillConclusions() {
        this.data.setConclusions(null);
    }

    private void fillSwotImprovements() {
        this.data.setSwotImprovements(null);
    }

    private void fillSwotWeaknessesClassification() {
        this.data.setSwotWeaknessesClassification(null);
    }

    private void fillSwotImprovementMetrics() {
        this.data.setSwotImprovementMetrics(null);
    }

}
