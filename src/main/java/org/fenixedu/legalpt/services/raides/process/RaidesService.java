package org.fenixedu.legalpt.services.raides.process;

import static org.fenixedu.legalpt.domain.raides.Raides.formatArgs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CompetenceCourseType;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.RootCourseGroup;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.organizationalStructure.UnitUtils;
import org.fenixedu.academic.domain.raides.DegreeClassification;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.domain.student.curriculum.conclusion.RegistrationConclusionInformation;
import org.fenixedu.academic.domain.student.curriculum.conclusion.RegistrationConclusionServices;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CycleCurriculumGroup;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.mapping.LegalMapping;
import org.fenixedu.legalpt.domain.raides.IGrauPrecedenteCompleto;
import org.fenixedu.legalpt.domain.raides.IMatricula;
import org.fenixedu.legalpt.domain.raides.Raides;
import org.fenixedu.legalpt.domain.raides.Raides.Ramo;
import org.fenixedu.legalpt.domain.raides.Raides.SituacaoProfissional;
import org.fenixedu.legalpt.domain.raides.TblInscrito;
import org.fenixedu.legalpt.domain.raides.mapping.BranchMappingType;
import org.fenixedu.legalpt.domain.raides.mapping.LegalMappingType;
import org.fenixedu.legalpt.domain.raides.report.RaidesRequestPeriodParameter;
import org.fenixedu.legalpt.domain.report.LegalReport;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.joda.time.DateTime;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class RaidesService {

    private static final int MAX_OTHER_SCHOOL_LEVEL_LENGTH = 80;
    protected LegalReport report;

    public RaidesService(final LegalReport report) {
        this.report = report;
    }

    protected String anoCurricular(final Registration registration, final ExecutionYear executionYear, final boolean mobility) {
        if (Raides.isDoctoralDegree(registration)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(Raides.AnoCurricular.NAO_APLICAVEL_CODE);
        }

        if (isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, mobility, CompetenceCourseType.DISSERTATION)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(Raides.AnoCurricular.DISSERTACAO_CODE);
        } else if (isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, mobility, CompetenceCourseType.INTERNSHIP)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(Raides.AnoCurricular.ESTAGIO_FINAL_CODE);
        } else if (isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, mobility,
                CompetenceCourseType.PROJECT_WORK)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR)
                    .translate(Raides.AnoCurricular.TRABALHO_PROJECTO_CODE);
        }

        return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR)
                .translate(String.valueOf(RegistrationServices.getCurricularYear(registration, executionYear).getResult()));
    }

    protected boolean isOnlyEnrolledOnCompetenceCourseType(final Registration registration, final ExecutionYear executionYear,
            final boolean mobility, final CompetenceCourseType competenceCourseType) {
        final Collection<Enrolment> enrolments = registration.getEnrolments(executionYear);

        final Set<CompetenceCourseType> typesSet = Sets.newHashSet();
        for (final Enrolment enrolment : enrolments) {

            if (!mobility && enrolment.getCurriculumGroup().isNoCourseGroupCurriculumGroup()) {
                continue;
            }

            final CurricularCourse curricularCourse = enrolment.getCurricularCourse();
            final CompetenceCourseType type =
                    curricularCourse != null ? curricularCourse.getCompetenceCourse().getType() : CompetenceCourseType.REGULAR;

            typesSet.add(type);
        }

        if (typesSet.size() != 1) {
            return false;
        }

        return typesSet.iterator().next() == competenceCourseType;
    }

    protected boolean isFirstTimeOnDegree(final Registration registration, final ExecutionYear executionYear) {
        // todo refactor
        if (!RegistrationServices.getPrecedentDegreeRegistrations(registration).isEmpty() && RegistrationServices
                .getEnrolmentYearsIncludingPrecedentRegistrations(registration, executionYear).size() > 1) {
            return false;
        }

        return executionYear == registration.getRegistrationYear();
    }

    /*
     * OTHER METHODS
     */

    protected void preencheInformacaoMatricula(final LegalReport report, final IMatricula bean, final ExecutionYear executionYear,
            final Registration registration) {

        bean.setIdAluno(registration.getStudent().getNumber().toString());

        bean.setAnoLectivo(executionYear.getQualifiedName());

        //HACK HACK: some institutions the same degree and each degree curricular plan is mapped to ministry code
        final DegreeCurricularPlan degreeCurricularPlan =
                getStudentCurricularPlanForBranch(registration, executionYear).getDegreeCurricularPlan();
        final LegalMapping oficialDegreeMapping =
                LegalMapping.find(report, LegalMappingType.DEGREE_CURRICULAR_PLAN_DEGREE_OFICIAL_CODE);
        if (oficialDegreeMapping != null && oficialDegreeMapping.isKeyDefined(degreeCurricularPlan)) {
            bean.setCurso(oficialDegreeMapping.translate(degreeCurricularPlan));
        } else {
            bean.setCurso(registration.getDegree().getMinistryCode());
        }

        preencheRamo(report, bean, executionYear, registration, false);

    }

    protected void preencheRamo(final LegalReport report, final IMatricula bean, final ExecutionYear executionYear,
            final Registration registration, final boolean forScholarPart) {
        final Set<CourseGroup> branches =
                forScholarPart ? scholarPartBranches(registration, executionYear) : branches(registration, executionYear);

        if (!forScholarPart) {
            bean.setRamo(null);
        }

        if (!branches.isEmpty()) {

            if (branches.size() > 1) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.enrolled.more.than.one.branch", formatArgs(registration, executionYear)));
            }

            bean.setRamo(BranchMappingType.readMapping(report).translate(branches.iterator().next()));
        } else {

            final RootCourseGroup rootCourseGroup =
                    getStudentCurricularPlanForBranch(registration, executionYear).getRoot().getDegreeModule();
            final LegalMapping branchMapping = BranchMappingType.readMapping(report);

            bean.setRamo(
                    branchMapping.isKeyDefined(rootCourseGroup) ? branchMapping.translate(rootCourseGroup) : Ramo.TRONCO_COMUM);
        }
    }

    private Set<CourseGroup> branches(final Registration registration, final ExecutionYear executionYear) {
        final Set<CourseGroup> result = Sets.newHashSet();

        final StudentCurricularPlan scp = getStudentCurricularPlanForBranch(registration, executionYear);

        for (final CurriculumGroup curriculumGroup : scp.getAllCurriculumGroups()) {
            if (curriculumGroup.getDegreeModule() == null) {
                continue;
            }

            if (isAffinity(curriculumGroup)) {
                continue;
            }

            final CourseGroup courseGroup = curriculumGroup.getDegreeModule();
            if (BranchMappingType.readMapping(report).isKeyDefined(courseGroup)) {
                result.add(courseGroup);
            }
        }

        return result;
    }

    private Set<CourseGroup> scholarPartBranches(final Registration registration, final ExecutionYear executionYear) {
        final Set<CourseGroup> result = Sets.newHashSet();

        RegistrationConclusionInformation conclusionInfoToUse = null;
        for (RegistrationConclusionInformation conclusionInfo : RegistrationConclusionServices.inferConclusion(registration)) {
            if (!conclusionInfo.isScholarPart()) {
                continue;
            }

            if (!conclusionInfo.isConcluded()) {
                continue;
            }

            conclusionInfoToUse = conclusionInfo;

            break;
        }

        for (final CurriculumGroup curriculumGroup : conclusionInfoToUse.getCurriculumGroup().getAllCurriculumGroups()) {
            if (curriculumGroup.getDegreeModule() == null) {
                continue;
            }

            if (isAffinity(curriculumGroup)) {
                continue;
            }

            final CourseGroup courseGroup = curriculumGroup.getDegreeModule();
            if (BranchMappingType.readMapping(report).isKeyDefined(courseGroup)) {
                result.add(courseGroup);
            }
        }

        return result;
    }

    private boolean isAffinity(final CurriculumGroup curriculumGroup) {
        final CycleCurriculumGroup parentCycleCurriculumGroup = curriculumGroup.getParentCycleCurriculumGroup();
        return curriculumGroup.isExternal() || (parentCycleCurriculumGroup != null && parentCycleCurriculumGroup.isExternal());
    }

    protected StudentCurricularPlan getStudentCurricularPlanForBranch(final Registration registration,
            final ExecutionYear executionYear) {
        return registration.getStudentCurricularPlansSet().size() == 1 ? registration
                .getLastStudentCurricularPlan() : registration.getStudentCurricularPlan(executionYear);
    }

    protected class DEGREE_VALUE_COMPARATOR implements Comparator<Degree> {

        protected Map<Degree, Integer> m;

        public DEGREE_VALUE_COMPARATOR(final Map<Degree, Integer> m) {
            this.m = m;
        }

        @Override
        public int compare(final Degree o1, final Degree o2) {
            int result = m.get(o1).compareTo(m.get(o2));

            if (result != 0) {
                return -result;
            }

            return o1.getExternalId().compareTo(o2.getExternalId());
        }

    }

    protected void preencheGrauPrecedentCompleto(final IGrauPrecedenteCompleto bean, final ExecutionYear executionYear,
            final Registration registration) {
        final StudentCandidacy studentCandidacy = registration.getStudentCandidacy();
        final PrecedentDegreeInformation lastCompletedQualification = studentCandidacy.getCompletedDegreeInformation();

        if (lastCompletedQualification == null) {
            return;
        }

        if (lastCompletedQualification.getSchoolLevel() != null) {
            bean.setEscolaridadeAnterior(LegalMapping.find(report, LegalMappingType.PRECEDENT_SCHOOL_LEVEL)
                    .translate(lastCompletedQualification.getSchoolLevel()));

            if (bean.getEscolaridadeAnterior().equals(Raides.NivelEscolaridadeAluno.OUTRO)) {

                if (!Strings.isNullOrEmpty(lastCompletedQualification.getOtherSchoolLevel())) {
                    bean.setOutroEscolaridadeAnterior(lastCompletedQualification.getOtherSchoolLevel().substring(0,
                            Math.min(MAX_OTHER_SCHOOL_LEVEL_LENGTH, lastCompletedQualification.getOtherSchoolLevel().length())));
                } else {
                    bean.setOutroEscolaridadeAnterior(lastCompletedQualification.getSchoolLevel().getLocalizedName().substring(0,
                            Math.min(MAX_OTHER_SCHOOL_LEVEL_LENGTH,
                                    lastCompletedQualification.getSchoolLevel().getLocalizedName().length())));
                }
            }
        }

        if (lastCompletedQualification.getCountry() != null) {
            bean.setPaisEscolaridadeAnt(lastCompletedQualification.getCountry().getCode());
        }

        if (lastCompletedQualification.getConclusionYear() != null) {
            bean.setAnoEscolaridadeAnt(lastCompletedQualification.getConclusionYear().toString());
        }

        if (isPortuguesePostHighSchool(lastCompletedQualification)
                && !Strings.isNullOrEmpty(lastCompletedQualification.getDegreeDesignation())) {

            if (lastCompletedQualification.getInstitution() != null && (lastCompletedQualification.getInstitution().isOfficial()
                    || UnitUtils.readInstitutionUnit() == lastCompletedQualification.getInstitution())) {
                bean.setEstabEscolaridadeAnt(lastCompletedQualification.getInstitution().getCode());
            } else if (lastCompletedQualification.getInstitution() != null) {
                bean.setEstabEscolaridadeAnt(Raides.Estabelecimentos.OUTRO);
                bean.setOutroEstabEscolarAnt(lastCompletedQualification.getInstitution().getNameI18n().getContent());
            }

            final DegreeDesignation degreeDesignation = DegreeDesignation.readByNameAndSchoolLevel(
                    lastCompletedQualification.getDegreeDesignation(), lastCompletedQualification.getSchoolLevel());

            if (degreeDesignation != null) {
                bean.setCursoEscolarAnt(degreeDesignation.getCode());
            } else {
                bean.setCursoEscolarAnt(Raides.Cursos.OUTRO);
                bean.setOutroCursoEscolarAnt(lastCompletedQualification.getDegreeDesignation());
            }
        }

        if (bean.isTipoEstabSecSpecified()) {
            if (lastCompletedQualification.getSchoolLevel() != null
                    && lastCompletedQualification.getSchoolLevel().isHighSchoolOrEquivalent()) {

                if (highSchoolType(studentCandidacy) != null) {
                    bean.setTipoEstabSec(LegalMapping.find(report, LegalMappingType.HIGH_SCHOOL_TYPE)
                            .translate(highSchoolType(studentCandidacy)));
                }

                if (Strings.isNullOrEmpty(bean.getTipoEstabSec())) {
                    bean.setTipoEstabSec(Raides.TipoEstabSec.PUBLICO);
                    LegalReportContext.addWarn("",
                            i18n("warn.Raides.highSchoolType.not.specified", formatArgs(registration, executionYear)));
                }
            }
        }

        if (Strings.isNullOrEmpty(bean.getTipoEstabSec()) && registration.getStudent().getLatestPersonalIngressionData() != null
                && registration.getStudent().getLatestPersonalIngressionData().getHighSchoolType() != null) {
            final PersonalIngressionData personalIngressionData = registration.getStudent().getLatestPersonalIngressionData();
            bean.setTipoEstabSec(LegalMapping.find(report, LegalMappingType.HIGH_SCHOOL_TYPE)
                    .translate(personalIngressionData.getHighSchoolType()));
        }

        validaGrauPrecedenteCompleto(executionYear, registration, lastCompletedQualification, bean);
        validaCursoOficialInstituicaoOficial(executionYear, registration, lastCompletedQualification, bean);
    }

    private AcademicalInstitutionType highSchoolType(StudentCandidacy studentCandidacy) {

        if (studentCandidacy.getCompletedDegreeInformation() != null
                && studentCandidacy.getCompletedDegreeInformation().getInstitutionType() != null) {
            return studentCandidacy.getCompletedDegreeInformation().getInstitutionType();
        }

        if (studentCandidacy.getPerson().getStudent() != null) {
            for (final PersonalIngressionData pid : studentCandidacy.getPerson().getStudent().getPersonalIngressionsDataSet()) {
                if (pid.getHighSchoolType() != null) {
                    return pid.getHighSchoolType();
                }
            }
        }

        return null;
    }

    protected boolean isPortuguesePostHighSchool(final PrecedentDegreeInformation lastCompletedQualification) {
        return lastCompletedQualification.getCountry() != null && lastCompletedQualification.getCountry().isDefaultCountry()
                && isPostHighEducation(lastCompletedQualification);
    }

    protected boolean isPostHighEducation(final PrecedentDegreeInformation precedentDegreeInformation) {
        return precedentDegreeInformation.getSchoolLevel() != null
                && (precedentDegreeInformation.getSchoolLevel().isHigherEducation()
                        || isCetEducation(precedentDegreeInformation));
    }

    protected boolean isCetEducation(final PrecedentDegreeInformation lastCompletedQualification) {
        return lastCompletedQualification.getSchoolLevel() != null
                && lastCompletedQualification.getSchoolLevel() == SchoolLevelType.TECHNICAL_SPECIALIZATION;
    }

    protected void validaGrauPrecedenteCompleto(final ExecutionYear executionYear, final Registration registration,
            final PrecedentDegreeInformation lastCompletedQualification, final IGrauPrecedenteCompleto bean) {

        if (Strings.isNullOrEmpty(bean.getEscolaridadeAnterior())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.previous.complete.school.level.missing",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getPaisEscolaridadeAnt())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.previous.complete.country.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getAnoEscolaridadeAnt())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.previous.complete.year.missing", formatArgs(registration, executionYear)));

            bean.markAsInvalid();
        }

        validaEstabelecimentoAnteriorCompleto(executionYear, registration, lastCompletedQualification, bean);
        validaCursoAnteriorCompleto(executionYear, registration, lastCompletedQualification, bean);

        if (!bean.isTipoEstabSecSpecified()) {
            return;
        }

        if (lastCompletedQualification.getSchoolLevel() != null
                && lastCompletedQualification.getSchoolLevel().isHighSchoolOrEquivalent()) {
            if (Strings.isNullOrEmpty(bean.getTipoEstabSec())) {
                LegalReportContext.addError("", i18n("error.Raides.validation.previous.complete.highSchoolType.missing",
                        formatArgs(registration, executionYear)));
            }
        }
    }

    protected void validaEstabelecimentoAnteriorCompleto(final ExecutionYear executionYear, final Registration registration,
            final PrecedentDegreeInformation lastCompletedQualification, final IGrauPrecedenteCompleto bean) {
        if (lastCompletedQualification.getCountry() == null || !lastCompletedQualification.getCountry().isDefaultCountry()) {
            return;
        }

        if (lastCompletedQualification.getSchoolLevel() == null || !isPostHighEducation(lastCompletedQualification)) {
            return;
        }

        if (Strings.isNullOrEmpty(bean.getEstabEscolaridadeAnt())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.previous.complete.institution.missing",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Raides.Estabelecimentos.OUTRO.equals(bean.getEstabEscolaridadeAnt())
                && Strings.isNullOrEmpty(bean.getOutroEstabEscolarAnt())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.previous.complete.other.institution.missing",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        } else if (Raides.Estabelecimentos.OUTRO.equals(bean.getEstabEscolaridadeAnt())) {
            LegalReportContext.addWarn("",
                    i18n("warn.Raides.validation.previous.complete.other.institution.given.instead.of.code",
                            formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }
    }

    protected void validaCursoOficialInstituicaoOficial(final ExecutionYear executionYear, final Registration registration,
            final PrecedentDegreeInformation lastCompletedQualification, final IGrauPrecedenteCompleto bean) {
        if (!isPortuguesePostHighSchool(lastCompletedQualification)) {
            return;
        }

        if (Strings.isNullOrEmpty(lastCompletedQualification.getDegreeDesignation())) {
            return;
        }

        final DegreeDesignation degreeDesignation = DegreeDesignation.readByNameAndSchoolLevel(
                lastCompletedQualification.getDegreeDesignation(), lastCompletedQualification.getSchoolLevel());

        if (degreeDesignation == null) {
            return;
        }

        boolean degreeDesignationContainsInstitution = false;
        for (final DegreeDesignation it : readByNameAndSchoolLevel(lastCompletedQualification.getDegreeDesignation(),
                lastCompletedQualification.getSchoolLevel())) {
            degreeDesignationContainsInstitution |=
                    it.getInstitutionUnitSet().contains(lastCompletedQualification.getInstitution());
        }

        if (!degreeDesignationContainsInstitution) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.official.precedent.degree.is.not.offered.by.institution",
                            formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if ((Raides.isMasterDegreeOrDoctoralDegree(registration) || Raides.isSpecializationDegree(registration))
                && !isPostHighEducation(lastCompletedQualification)) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.isMasterDoctoralOrSpecialization.but.completed.qualification.is.not.higher",
                            formatArgs(registration, executionYear)));
        }
    }

    protected void validaCursoAnteriorCompleto(final ExecutionYear executionYear, final Registration registration,
            final PrecedentDegreeInformation lastCompletedQualification, final IGrauPrecedenteCompleto bean) {
        if (lastCompletedQualification.getCountry() == null || !lastCompletedQualification.getCountry().isDefaultCountry()) {
            return;
        }

        if (lastCompletedQualification.getSchoolLevel() == null || !isPostHighEducation(lastCompletedQualification)) {
            return;
        }

        if (Strings.isNullOrEmpty(bean.getCursoEscolarAnt())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.previous.complete.degree.designation.missing",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Raides.NivelCursoOrigem.OUTRO.equals(bean.getCursoEscolarAnt())
                && Strings.isNullOrEmpty(bean.getOutroCursoEscolarAnt())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.previous.complete.other.degree.designation.missing",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (isPortuguesePostHighSchool(lastCompletedQualification) && Raides.Cursos.OUTRO.equals(bean.getCursoEscolarAnt())) {
            LegalReportContext.addError("", i18n(
                    "error.Raides.validation.previous.complete.other.degree.designation.set.even.if.level.is.portuguese.higher.education",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }
    }

    protected Set<DegreeDesignation> readByNameAndSchoolLevel(String degreeDesignationName, SchoolLevelType schoolLevel) {
        if ((schoolLevel == null) || (degreeDesignationName == null)) {
            return null;
        }

        List<DegreeClassification> possibleClassifications = new ArrayList<DegreeClassification>();
        for (String code : schoolLevel.getEquivalentDegreeClassifications()) {
            possibleClassifications.add(DegreeClassification.readByCode(code));
        }

        List<DegreeDesignation> possibleDesignations = new ArrayList<DegreeDesignation>();
        for (DegreeClassification classification : possibleClassifications) {
            if (!classification.getDegreeDesignationsSet().isEmpty()) {
                possibleDesignations.addAll(classification.getDegreeDesignationsSet());
            }
        }

        Set<DegreeDesignation> result = Sets.newHashSet();
        for (DegreeDesignation degreeDesignation : possibleDesignations) {
            if (degreeDesignation.getDescription().equalsIgnoreCase(degreeDesignationName)) {
                result.add(degreeDesignation);
            }
        }

        return result;
    }

    protected void preencheInformacaoPessoal(final ExecutionYear executionYear, final Registration registration,
            final TblInscrito bean) {

        if (registration.getPerson().getMaritalStatus() != null) {
            bean.setEstadoCivil(LegalMapping.find(report, LegalMappingType.MARITAL_STATUS)
                    .translate(registration.getPerson().getMaritalStatus()));
        }

        PersonalIngressionData ingressionData = Raides.personalIngressionData(registration, executionYear);
        if (ingressionData != null && ingressionData.getDislocatedFromPermanentResidence() != null) {
            bean.setAlunoDeslocado(LegalMapping.find(report, LegalMappingType.BOOLEAN)
                    .translate(ingressionData.getDislocatedFromPermanentResidence()));
        }

        final Country countryOfResidence = Raides.countryOfResidence(registration, executionYear);
        final DistrictSubdivision districtSubdivision = Raides.districtSubdivisionOfResidence(registration, executionYear);
        if (countryOfResidence != null && districtSubdivision != null) {
            bean.setResideConcelho(districtSubdivision.getDistrict().getCode() + districtSubdivision.getCode());
        } else if (Raides.countryOfResidence(registration, executionYear).isDefaultCountry()) {
            bean.setResideConcelho(Raides.Concelho.OUTRO);
        }

        if (ingressionData != null) {
            if (ingressionData.getFatherSchoolLevel() != null) {
                bean.setNivelEscolarPai(LegalMapping.find(report, LegalMappingType.SCHOOL_LEVEL)
                        .translate(ingressionData.getFatherSchoolLevel()));
            }

            if (ingressionData.getMotherSchoolLevel() != null) {
                bean.setNivelEscolarMae(LegalMapping.find(report, LegalMappingType.SCHOOL_LEVEL)
                        .translate(ingressionData.getMotherSchoolLevel()));
            }

            if (ingressionData.getFatherProfessionalCondition() != null) {
                bean.setSituacaoProfPai(LegalMapping.find(report, LegalMappingType.PROFESSIONAL_SITUATION_CONDITION)
                        .translate(ingressionData.getFatherProfessionalCondition()));
            }

            if (ingressionData.getMotherProfessionalCondition() != null) {
                bean.setSituacaoProfMae(LegalMapping.find(report, LegalMappingType.PROFESSIONAL_SITUATION_CONDITION)
                        .translate(ingressionData.getMotherProfessionalCondition()));
            }

            if (ingressionData.getProfessionalCondition() != null) {
                bean.setSituacaoProfAluno(LegalMapping.find(report, LegalMappingType.PROFESSIONAL_SITUATION_CONDITION)
                        .translate(ingressionData.getProfessionalCondition()));
            }

            if (ingressionData.getFatherProfessionType() != null) {
                bean.setProfissaoPai(transformProfessionTypeMappingValue(bean.getSituacaoProfPai(), LegalMapping
                        .find(report, LegalMappingType.PROFESSION_TYPE).translate(ingressionData.getFatherProfessionType())));
            }

            if (ingressionData.getMotherProfessionType() != null) {
                bean.setProfissaoMae(transformProfessionTypeMappingValue(bean.getSituacaoProfMae(), LegalMapping
                        .find(report, LegalMappingType.PROFESSION_TYPE).translate(ingressionData.getMotherProfessionType())));
            }

            if (ingressionData.getProfessionType() != null) {
                bean.setProfissaoAluno(transformProfessionTypeMappingValue(bean.getSituacaoProfAluno(), LegalMapping
                        .find(report, LegalMappingType.PROFESSION_TYPE).translate(ingressionData.getProfessionType())));
            }
        }

        validaInformacaoPessoal(executionYear, registration, bean);
    }

    private String transformProfessionTypeMappingValue(String professionalSituation, String professionType) {
        return SituacaoProfissional.requiresProfessionType(professionalSituation) ? professionType : null;
    }

    protected void validaInformacaoPessoal(final ExecutionYear executionYear, final Registration registration,
            final TblInscrito bean) {

        if (Strings.isNullOrEmpty(bean.getEstadoCivil())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.maritalStatus.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getAlunoDeslocado())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.dislocated.from.residence.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Raides.countryOfResidence(registration, executionYear) != null
                && Raides.countryOfResidence(registration, executionYear).isDefaultCountry()) {
            if (Strings.isNullOrEmpty(bean.getResideConcelho())) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.district.subdivision.missing", formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            }
        }

        if (Strings.isNullOrEmpty(bean.getSituacaoProfPai())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.fatherProfessionalSituationType.missing",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getNivelEscolarPai())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.fatherSchoolLevel.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (SituacaoProfissional.requiresProfessionType(bean.getSituacaoProfPai())
                && Strings.isNullOrEmpty(bean.getProfissaoPai())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.fatherProfession.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getSituacaoProfMae())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.motherProfessionalSituationType.missing",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getNivelEscolarMae())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.motherSchoolLevel.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (SituacaoProfissional.requiresProfessionType(bean.getSituacaoProfMae())
                && Strings.isNullOrEmpty(bean.getProfissaoMae())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.motherProfession.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getSituacaoProfAluno())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.studentProfessionalSituationType.missing",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (SituacaoProfissional.requiresProfessionType(bean.getSituacaoProfAluno())
                && Strings.isNullOrEmpty(bean.getProfissaoAluno())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.studentProfession.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

    }

    protected String regimeFrequencia(final Registration registration, final ExecutionYear executionYear, boolean mobility) {
        final boolean onlyEnrolledOnDissertation =
                isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, mobility, CompetenceCourseType.DISSERTATION);
        final boolean onlyEnrolledOnInternship =
                isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, mobility, CompetenceCourseType.INTERNSHIP);
        final boolean onlyEnrolledOnProjectWork =
                isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, mobility, CompetenceCourseType.PROJECT_WORK);

        if (onlyEnrolledOnDissertation || onlyEnrolledOnInternship || onlyEnrolledOnProjectWork) {
            return LegalMapping.find(report, LegalMappingType.REGIME_FREQUENCIA).translate(Raides.RegimeFrequencia.ETD_CODE);
        }

        return LegalMapping.find(report, LegalMappingType.REGIME_FREQUENCIA).translate(registration.getDegree().getExternalId());
    }

    protected DateTime findMaximumAnnulmentDate(final List<RaidesRequestPeriodParameter> periods,
            final ExecutionYear executionYear) {
        return periods.stream().filter(p -> p.getAcademicPeriod() == executionYear)
                .max(Comparator.comparing(RaidesRequestPeriodParameter::getEnd)).get().getEnd().plusDays(1)
                .toDateTimeAtStartOfDay().minusSeconds(1);
    }

    protected BigDecimal enrolledEcts(final ExecutionYear executionYear, final Registration registration,
            final DateTime maximumAnnulmentDate, final boolean mobility) {
        final StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(executionYear);
        double result = 0.0;

        for (final Enrolment enrolment : studentCurricularPlan.getEnrolmentsSet()) {
            if (Raides.isEnrolmentAnnuled(enrolment, maximumAnnulmentDate)) {
                continue;
            }

            if (!mobility && enrolment.getCurriculumGroup().isNoCourseGroupCurriculumGroup()) {
                continue;
            }

            if (enrolment.isValid(executionYear)) {
                result += enrolment.getEctsCredits();
            }
        }

        return new BigDecimal(result);
    }

    protected Set<Enrolment> scholarPartEnrolments(final ExecutionYear executionYear, final Registration registration) {
        final StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(executionYear);

        final Set<Enrolment> result = Sets.newHashSet();
        for (final Enrolment enrolment : studentCurricularPlan.getEnrolmentsSet()) {
            if (enrolment.getCurricularCourse() != null && enrolment.getCurricularCourse().isDissertation()) {
                continue;
            }

            if (!enrolment.isValid(executionYear)) {
                continue;
            }

            result.add(enrolment);
        }

        return result;
    }

    protected BigDecimal doctoralEnrolledEcts(final ExecutionYear executionYear, final Registration registration,
            final DateTime maximumAnnulmentDate) {
        if (isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, false, CompetenceCourseType.DISSERTATION)) {
            return null;
        }

        BigDecimal enrolledEcts = enrolledEcts(executionYear, registration, maximumAnnulmentDate, false);
        if (BigDecimal.ZERO.compareTo(enrolledEcts) != 0) {

            enrolledEcts =
                    enrolledEcts.subtract(phdDissertationEnrolledEctsCredits(executionYear, registration, maximumAnnulmentDate));

            return enrolledEcts;
        }

        return null;
    }

    private BigDecimal phdDissertationEnrolledEctsCredits(final ExecutionYear executionYear, final Registration registration,
            final DateTime maximumAnnulmentDate) {

        BigDecimal result = BigDecimal.ZERO;

        final StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(executionYear);

        Collection<CurricularCourse> allDissertationCurricularCourses =
                studentCurricularPlan.getDegreeCurricularPlan().getDcpDegreeModules(CurricularCourse.class, executionYear)
                        .stream().map(CurricularCourse.class::cast).filter(cc -> cc.isDissertation()).collect(Collectors.toSet());

        for (final CurricularCourse dissertation : allDissertationCurricularCourses) {

            boolean isEnrollmentInDissertationCurricularCourse = studentCurricularPlan.getEnrolmentsByExecutionYear(executionYear)
                    .stream().filter(e -> e.getCurricularCourse() == dissertation)
                    .filter(e -> !Raides.isEnrolmentAnnuled(e, maximumAnnulmentDate)).count() > 0;

            if (isEnrollmentInDissertationCurricularCourse) {
                result = result.add(new BigDecimal(dissertation.getEctsCredits()));
            }
        }

        return result;
    }

    public String i18n(String key, String... arguments) {
        return LegalPTUtil.bundle(key, arguments);
    }

}
