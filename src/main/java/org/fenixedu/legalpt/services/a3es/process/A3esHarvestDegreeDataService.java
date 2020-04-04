package org.fenixedu.legalpt.services.a3es.process;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.EN;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PLUS;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PT;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.SEMICOLON;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._100;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._1000;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._200;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNLIMITED;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.createMLS;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.label;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.dto.a3es.A3esDegreeBean;
import org.fenixedu.legalpt.dto.a3es.A3esProcessBean;

public class A3esHarvestDegreeDataService {

    private final ExecutionYear year;
    private final DegreeCurricularPlan degreeCurricularPlan;
    private final Degree degree;
    private final DegreeInfo info;

    public A3esHarvestDegreeDataService(final A3esProcessBean bean) {
        this.year = bean.getExecutionYear();
        this.degreeCurricularPlan = bean.getDegreeCurricularPlan();
        this.degree = this.degreeCurricularPlan.getDegree();
        this.info = this.degree.getMostRecentDegreeInfo(this.year.getAcademicInterval());

        final A3esDegreeBean data = bean.getDegreeData();

        fillInstitutionName(data);
//        fillPartnerInstitutionsNames(data);
        fillSchoolName(data);
        fillDegreeName(data);
        fillDegreeType(data);
//        fillCurricularPlanPromulgation(data);
        fillMainScientificArea(data);
//        fillScientificAreas(data);
//        fillMajorAreaClassification(data);
//        fillFirstMinorAreaClassification(data);
//        fillSecondMinorAreaClassification(data);
        fillEctsCredits(data);
        fillDegreeDuration(data);
        fillNumerusClausus(data);
        fillIngressionSpecificConditions(data);
        fillBranches(data);
//        fillExecutionYear(data);
//        fillCurriculum(data);
        fillRegistrationRegime(data);
        fillCoordinators(data);
//        fillDiplomaIssuer(data);
//        fillTrainingCoaches(data);
//        fillCampus(data);
//        fillObservations(data);
//        fillApprovalCommittee(data);
//        fillDegreeCoordinationChairmen(data);
//        fillGeneralObjectives(data);
//        fillLearningObjectives(data);
//        fillKnowledgeUpdateStrategy(data);
//        fillSchoolStrategy(data);
//        fillCommunicationMedia(data);
//        fillOrganizationalStructure(data);
//        fillParticipationMeans(data);
//        fillInstitutionEducationalProject(data);
//        fillIntegratedEducationalProject(data);
//        fillQualityAssuranceMedia(data);
//        fillQualityAssuranceChairman(data);
//        fillQualityAssuranceProcedure(data);
//        fillQualityAssuranceImpact(data);
//        fillOtherAccreditations(data);
//        fillRequiredInfrastructure(data);
//        fillRequiredEquipment(data);
//        fillInternationalColaboration(data);
//        fillNationalColaboration(data);
//        fillInstitutionalColaboration(data);
//        fillFacultyEvaluationProcedure(data);
//        fillStaffInformation(data);
//        fillStaffQualification(data);
//        fillStaffEvaluation(data);
//        fillStaffImprovement(data);
//        fillMethodologyEffectiveness(data);
//        fillEctsRequirementsVerification(data);
//        fillKnowledgeRequirementsVerification(data);
//        fillStudentIntegrationMethodology(data);
//        fillEducationalEfficiency(data);
//        fillApprovalRateByScientificArea(data);
//        fillApprovalRateMeasurements(data);
//        fillEmployability(data);
//        fillResearchCentres(data);
//        fillScientificActivityImpact(data);
//        fillScientificActivitiesWithPartners(data);
//        fillScientificActivtiesImprovement(data);
//        fillOtherActivities(data);
//        fillOtherActivitiesImpact(data);
//        fillMarketingQuality(data);
//        fillInternationalizationLevel(data);
//        fillSimilarDegreesEmployability(data);
//        fillAttractiveness(data);
//        fillClosePartnerships(data);
//        fillEctsJustification(data);
//        fillEctsCalculationMethod(data);
//        fillEctsCalculationTeachersInput(data);
//        fillSimilarDegrees(data);
//        fillSimilarDegreesGoalsComparison(data);
//        fillCooperationProtocols(data);
//        fillTrainingFollowupResources(data);
//        fillSwotStrengths(data);
//        fillSwotWeaknesses(data);
//        fillSwotOpportunities(data);
//        fillSwotThreats(data);
//        fillConclusions(data);
//        fillSwotImprovements(data);
//        fillSwotWeaknessesClassification(data);
//        fillSwotImprovementMetrics(data);
    }

    static private void fillInstitutionName(final A3esDegreeBean data) {
        final Unit currentInstitution = Bennu.getInstance().getInstitutionUnit();
        final Unit university = currentInstitution.getParentUnits().stream().filter(u -> u.isUniversityUnit()).findFirst()
                .orElse(currentInstitution);
        data.addField("q-a1_name", "higherEducationInstitution", university.getName(), _UNLIMITED);
    }

    static private void fillSchoolName(final A3esDegreeBean data) {
        data.addField("ext-comp-1625", "organicUnit", Bennu.getInstance().getInstitutionUnit().getName(), _UNLIMITED);
    }

    private void fillDegreeName(final A3esDegreeBean data) {
        final LocalizedString source = this.degree.getNameI18N();
        data.addField("q-II.1.3_pt", "plan", PT, source, _UNLIMITED);
        data.addField("q-II.1.3_en", "plan", EN, source, _UNLIMITED);
    }

    private void fillDegreeType(final A3esDegreeBean data) {
        final DegreeType degreeType = this.degree.getDegreeType();
        data.addField("q-II.1.4_name", "degreeType", degreeType.getName().getContent(PT), _UNLIMITED);
    }

    private void fillMainScientificArea(final A3esDegreeBean data) {
        final LocalizedString source = this.info.getPrevailingScientificArea();
        data.addField("q-II.1.6_pt", "mainScientificArea", PT, source, _UNLIMITED);
        data.addField("q-II.1.6_en", "mainScientificArea", EN, source, _UNLIMITED);
    }

    private void fillEctsCredits(final A3esDegreeBean data) {
        final Double source = this.degreeCurricularPlan.getRoot().getMinEctsCredits(this.year.getFirstExecutionPeriod());
        data.addField("q-II.1.8", "ectsCredits", BigDecimal.valueOf(source).stripTrailingZeros().toPlainString(), _UNLIMITED);
    }

    private void fillDegreeDuration(final A3esDegreeBean data) {
        final LocalizedString source = this.info.getExtendedDegreeInfo().getStudyProgrammeDuration();
        data.addField("q-II.1.9_pt", "degreeDuration", PT, source, _UNLIMITED);
        data.addField("q-II.1.9_en", "degreeDuration", EN, source, _UNLIMITED);
    }

    private void fillNumerusClausus(final A3esDegreeBean data) {
        final Integer drifts = this.info.getDriftsInitial();
        final String source = drifts == null ? null : String.valueOf(drifts);
        data.addField("q-II.1.10", "numerusClausus", source, _100);
    }

    private void fillIngressionSpecificConditions(final A3esDegreeBean data) {

        data.addField("testIngression_pt", "testIngression", PT, this.info.getTestIngression(), _1000);
        data.addField("testIngression_en", "testIngression", EN, this.info.getTestIngression(), _1000);

        data.addField("accessRequisites_pt", "accessRequisites", PT, this.info.getAccessRequisites(), _1000);
        data.addField("accessRequisites_en", "accessRequisites", EN, this.info.getAccessRequisites(), _1000);

        final LocalizedString source = this.info.getClassifications();
        data.addField("q-II.1.11_pt", "ingressionSpecificConditions", PT, source, _1000);
        data.addField("q-II.1.11_en", "ingressionSpecificConditions", EN, source, _1000);
    }

    private void fillRegistrationRegime(final A3esDegreeBean data) {
        final LocalizedString source = this.info.getExtendedDegreeInfo().getStudyRegime();
        data.addField("q-II.1.12.1_pt", "registrationRegime", PT, source, _100);
        data.addField("q-II.1.12.1_en", "registrationRegime", EN, source, _100);
    }

    private void fillBranches(final A3esDegreeBean data) {
        final Set<LocalizedString> majors = this.degreeCurricularPlan.getMajorBranches().stream()
                .map(i -> i.getNameI18N(this.year)).collect(Collectors.toSet());
        final Set<LocalizedString> minors = this.degreeCurricularPlan.getMinorBranches().stream()
                .map(i -> i.getNameI18N(this.year)).collect(Collectors.toSet());

        final Stream<String> ptStream =
                Stream.concat(majors.stream().map(i -> i.getContent(PT)), minors.stream().map(i -> i.getContent(PT)));
        final Stream<String> enStream =
                Stream.concat(majors.stream().map(i -> i.getContent(EN)), minors.stream().map(i -> i.getContent(EN)));

        final String pt = ptStream.collect(Collectors.joining(SEMICOLON));
        final String en = enStream.collect(Collectors.joining(SEMICOLON));

        final LocalizedString source = createMLS(pt, en);
        data.addField("branches", "branches", PT, source, _200);
        data.addField("branches", "branches", EN, source, _200);
    }

    private void fillCoordinators(final A3esDegreeBean data) {
        String source = null;

        final ExecutionDegree executionDegree = this.degreeCurricularPlan.getExecutionDegreeByYear(this.year);
        if (executionDegree != null) {
            source = executionDegree.getCoordinatorsListSet().stream()
                    .map(coordinator -> coordinator.getPerson().getName()
                            + (coordinator.isResponsible() ? " (" + label("responsable") + ")" : ""))
                    .collect(Collectors.joining(PLUS));
        }

        data.addField("q-II.3.1", "coordinators", source, _1000);
    }

}
