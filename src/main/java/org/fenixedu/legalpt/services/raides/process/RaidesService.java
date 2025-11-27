package org.fenixedu.legalpt.services.raides.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseType;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.RootCourseGroup;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.organizationalStructure.UnitUtils;
import org.fenixedu.academic.domain.raides.DegreeClassification;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.student.curriculum.conclusion.RegistrationConclusionInformation;
import org.fenixedu.academic.domain.student.curriculum.conclusion.RegistrationConclusionServices;
import org.fenixedu.academic.domain.student.personaldata.EducationLevelType;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CycleCurriculumGroup;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.mapping.LegalMapping;
import org.fenixedu.legalpt.domain.raides.IGrauPrecedenteCompleto;
import org.fenixedu.legalpt.domain.raides.IMatricula;
import org.fenixedu.legalpt.domain.raides.Raides;
import org.fenixedu.legalpt.domain.raides.Raides.Ramo;
import org.fenixedu.legalpt.domain.raides.Raides.SituacaoProfissional;
import org.fenixedu.legalpt.domain.raides.RaidesInstance;
import org.fenixedu.legalpt.domain.raides.RaidesReportEntryTarget;
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
    private static final String TECHNICAL_SPECIALIZATION = "TECHNICAL_SPECIALIZATION";
    protected LegalReport report;
    private static final CompetenceCourseType dissertationCompetenceCourseType =
            CompetenceCourseType.findByCode(CompetenceCourseType.DISSERTATION).orElseThrow();
    private static final CompetenceCourseType internshipCompetenceCourseType =
            CompetenceCourseType.findByCode(CompetenceCourseType.INTERNSHIP).orElseThrow();
    private static final CompetenceCourseType projectWorkCompetenceCourseType =
            CompetenceCourseType.findByCode(CompetenceCourseType.PROJECT_WORK).orElseThrow();

    public RaidesService(final LegalReport report) {
        this.report = report;
    }

    protected String anoCurricular(final Registration registration, final ExecutionYear executionYear, final boolean mobility) {
        if (Raides.isDoctoralDegree(registration)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(Raides.AnoCurricular.NAO_APLICAVEL_CODE);
        }

        if (isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, mobility, dissertationCompetenceCourseType)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(Raides.AnoCurricular.DISSERTACAO_CODE);
        } else if (isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, mobility, internshipCompetenceCourseType)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(Raides.AnoCurricular.ESTAGIO_FINAL_CODE);
        } else if (isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, mobility, projectWorkCompetenceCourseType)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR)
                    .translate(Raides.AnoCurricular.TRABALHO_PROJECTO_CODE);
        }

        return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR)
                .translate(String.valueOf(RegistrationServices.getCurricularYear(registration, executionYear).getResult()));
    }

    public static boolean isOnlyEnrolledOnCompetenceCourseType(final Registration registration, final ExecutionYear executionYear,
            final boolean mobility, final CompetenceCourseType competenceCourseType) {
        CompetenceCourseType regular = CompetenceCourseType.findByCode(CompetenceCourseType.REGULAR).orElseThrow();

        final Collection<Enrolment> enrolments = registration.getEnrolments(executionYear);

        final Set<CompetenceCourseType> typesSet = Sets.newHashSet();
        for (final Enrolment enrolment : enrolments) {

            if (!mobility && enrolment.getCurriculumGroup().isNoCourseGroupCurriculumGroup()) {
                continue;
            }

            final CurricularCourse curricularCourse = enrolment.getCurricularCourse();
            final CompetenceCourseType type =
                    curricularCourse != null ? curricularCourse.getCompetenceCourse().getCompetenceCourseType() : regular;

            typesSet.add(type);
        }

        if (typesSet.size() != 1) {
            return false;
        }

        return typesSet.iterator().next() == competenceCourseType;
    }

    protected boolean isFirstTimeOnDegree(final Registration registration, final ExecutionYear executionYear) {
        // todo refactor
        if (!RegistrationServices.getPrecedentDegreeRegistrations(registration).isEmpty()
                && RegistrationServices.getEnrolmentYearsIncludingPrecedentRegistrations(registration, executionYear).size()
                > 1) {
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
                LegalReportContext.addError(RaidesReportEntryTarget.of(registration, executionYear),
                        i18n("error.Raides.validation.enrolled.more.than.one.branch"),
                        i18n("error.Raides.validation.enrolled.more.than.one.branch.action"));
                bean.markAsInvalid();
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
        return registration.getStudentCurricularPlansSet().size()
                == 1 ? registration.getLastStudentCurricularPlan() : registration.getStudentCurricularPlan(executionYear);
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
        final PrecedentDegreeInformation lastCompletedQualification = registration.getCompletedDegreeInformation();

        final RaidesReportEntryTarget target = RaidesReportEntryTarget.of(registration, executionYear);

        if (lastCompletedQualification == null) {
            return;
        }

        if (lastCompletedQualification.getEducationLevelType() != null) {
            String value = LegalMapping.find(report, LegalMappingType.PRECEDENT_EDUCATION_LEVEL)
                    .translate(lastCompletedQualification.getEducationLevelType());

            if (StringUtils.isBlank(value)) {
                LegalReportContext.addError(target,
                        i18n("error.Raides.validation.previous.complete.education.level.missing.translate",
                                lastCompletedQualification.getEducationLevelType().getName().getContent()),
                        i18n("error.Raides.validation.previous.complete.education.level.missing.translate.action",
                                lastCompletedQualification.getEducationLevelType().getName().getContent()));

                bean.markAsInvalid();

            } else {
                bean.setEscolaridadeAnterior(value);

                if (bean.getEscolaridadeAnterior() != null && bean.getEscolaridadeAnterior()
                        .equals(Raides.NivelEscolaridadeAluno.OUTRO)) {

                    if (!Strings.isNullOrEmpty(lastCompletedQualification.getOtherSchoolLevel())) {
                        bean.setOutroEscolaridadeAnterior(lastCompletedQualification.getOtherSchoolLevel().substring(0,
                                Math.min(MAX_OTHER_SCHOOL_LEVEL_LENGTH,
                                        lastCompletedQualification.getOtherSchoolLevel().length())));
                    } else {
                        bean.setOutroEscolaridadeAnterior(
                                lastCompletedQualification.getEducationLevelType().getName().getContent()
                                .substring(0, Math.min(MAX_OTHER_SCHOOL_LEVEL_LENGTH,
                                        lastCompletedQualification.getEducationLevelType().getName().getContent().length())));
                    }
                }

            }

        }

        if (lastCompletedQualification.getCountry() != null) {
            bean.setPaisEscolaridadeAnt(lastCompletedQualification.getCountry().getCode());
        }

        if (lastCompletedQualification.getConclusionYear() != null) {
            bean.setAnoEscolaridadeAnt(lastCompletedQualification.getConclusionYear().toString());
        }

        if (isPortuguesePostHighSchool(lastCompletedQualification) && !Strings.isNullOrEmpty(
                lastCompletedQualification.getDegreeDesignation())) {

            if (lastCompletedQualification.getInstitution() != null && (lastCompletedQualification.getInstitution().isOfficial()
                    || UnitUtils.readInstitutionUnit() == lastCompletedQualification.getInstitution())) {
                bean.setEstabEscolaridadeAnt(lastCompletedQualification.getInstitution().getCode());
            } else if (lastCompletedQualification.getInstitution() != null) {
                bean.setEstabEscolaridadeAnt(Raides.Estabelecimentos.OUTRO);
                bean.setOutroEstabEscolarAnt(lastCompletedQualification.getInstitution().getNameI18n().getContent());
            }

            final DegreeDesignation degreeDesignation =
                    DegreeDesignation.readByNameAndEducationLevelType(lastCompletedQualification.getDegreeDesignation(),
                            lastCompletedQualification.getEducationLevelType());

            if (degreeDesignation != null) {
                bean.setCursoEscolarAnt(degreeDesignation.getCode());

                final List<String> ministryCodes =
                        Stream.concat(Stream.of(Objects.requireNonNull(registration.getDegree()).getMinistryCode()),
                                registration.getDegree().getPrecedentDegreesSet().stream().map(Degree::getMinistryCode)).toList();

                if (ministryCodes.contains(degreeDesignation.getCode())) {
                    // send a warning!!
                    LegalReportContext.addWarn(RaidesReportEntryTarget.of(registration, executionYear),
                            i18n("warn.Raides.completed.qualification.has.same.code.as.degree"));
                }

            } else {
                bean.setCursoEscolarAnt(Raides.Cursos.OUTRO);
                bean.setOutroCursoEscolarAnt(lastCompletedQualification.getDegreeDesignation());
            }
        }

        if (bean.isTipoEstabSecSpecified()) {
            if (lastCompletedQualification.getEducationLevelType() != null && lastCompletedQualification.getEducationLevelType()
                    .getHighSchoolOrEquivalent()) {

                if (highSchoolType(registration) != null) {

                    String value = LegalMapping.find(report, LegalMappingType.HIGH_SCHOOL_TYPE)
                            .translate(highSchoolType(registration));

                    if (StringUtils.isBlank(value)) {
                        LegalReportContext.addError(target, i18n("error.Raides.validation.highSchoolType.missing.translate",
                                        highSchoolType(registration).getName()),
                                i18n("error.Raides.validation.highSchoolType.missing.translate.action",
                                        highSchoolType(registration).getName()));

                        bean.markAsInvalid();
                    } else {
                        bean.setTipoEstabSec(value);
                    }

                }

                if (Strings.isNullOrEmpty(bean.getTipoEstabSec())) {
                    bean.setTipoEstabSec(Raides.TipoEstabSec.PUBLICO);
                    LegalReportContext.addWarn(RaidesReportEntryTarget.of(registration, executionYear),
                            i18n("warn.Raides.highSchoolType.not.specified"));
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

    private AcademicalInstitutionType highSchoolType(Registration registration) {

        PrecedentDegreeInformation completedDegreeInformation = registration.getCompletedDegreeInformation();
        if (completedDegreeInformation != null && completedDegreeInformation.getInstitutionType() != null) {
            return completedDegreeInformation.getInstitutionType();
        }

        if (registration.getPerson().getStudent() != null) {
            for (final PersonalIngressionData pid : registration.getPerson().getStudent().getPersonalIngressionsDataSet()) {
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
        return precedentDegreeInformation.getEducationLevelType() != null && (
                precedentDegreeInformation.getEducationLevelType().getHigherEducation() || isCetEducation(
                        precedentDegreeInformation));
    }

    protected boolean isCetEducation(final PrecedentDegreeInformation lastCompletedQualification) {
        return lastCompletedQualification.getEducationLevelType() != null && Objects.equals(
                lastCompletedQualification.getEducationLevelType().getCode(), TECHNICAL_SPECIALIZATION);
    }

    protected void validaGrauPrecedenteCompleto(final ExecutionYear executionYear, final Registration registration,
            final PrecedentDegreeInformation lastCompletedQualification, final IGrauPrecedenteCompleto bean) {

        final RaidesReportEntryTarget target = RaidesReportEntryTarget.of(registration, executionYear);

        if (Strings.isNullOrEmpty(bean.getEscolaridadeAnterior())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.previous.complete.education.level.missing"),
                    i18n("error.Raides.validation.previous.complete.education.level.missing.action"));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getPaisEscolaridadeAnt())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.previous.complete.country.missing"),
                    i18n("error.Raides.validation.previous.complete.country.missing.action"));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getAnoEscolaridadeAnt())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.previous.complete.year.missing"),
                    i18n("error.Raides.validation.previous.complete.year.missing.action"));

            bean.markAsInvalid();
        } else if (registration.getPerson().getDateOfBirthYearMonthDay() != null) {
            // Se a EscolaridadeAnterior indicada for Ensino secundário[13], Curso de especialização tecnológica[14], Bacharelato[15],
            // Licenciatura[16], Mestrado[17], Doutoramento[18], Curso técnico superior profissional[20] ou Licenciatura 1.º ciclo[30], 
            // o valor a introduzir deve ser igual ou superior ao somatório do ano da data de nascimento do aluno mais 16.

            Integer previousConclusionYear = Integer.valueOf(bean.getAnoEscolaridadeAnt());

            if (previousConclusionYear < (registration.getPerson().getDateOfBirthYearMonthDay().getYear() + Raides.Idade.MIN)) {
                LegalReportContext.addError(target, i18n("error.Raides.validation.previous.complete.year.invalid"),
                        i18n("error.Raides.validation.previous.complete.year.invalid.action"));

                bean.markAsInvalid();
            }

        }

        validaEstabelecimentoAnteriorCompleto(executionYear, registration, lastCompletedQualification, bean);
        validaCursoAnteriorCompleto(executionYear, registration, lastCompletedQualification, bean);

        if (!bean.isTipoEstabSecSpecified()) {
            return;
        }

        if (lastCompletedQualification.getEducationLevelType() != null && lastCompletedQualification.getEducationLevelType()
                .getHighSchoolOrEquivalent()) {
            if (Strings.isNullOrEmpty(bean.getTipoEstabSec())) {
                LegalReportContext.addError(target, i18n("error.Raides.validation.previous.complete.highSchoolType.missing"),
                        i18n("error.Raides.validation.previous.complete.highSchoolType.missing.action"));
                bean.markAsInvalid();
            }
        }
    }

    protected void validaEstabelecimentoAnteriorCompleto(final ExecutionYear executionYear, final Registration registration,
            final PrecedentDegreeInformation lastCompletedQualification, final IGrauPrecedenteCompleto bean) {

        final RaidesReportEntryTarget target = RaidesReportEntryTarget.of(registration, executionYear);

        if (lastCompletedQualification.getCountry() == null || !lastCompletedQualification.getCountry().isDefaultCountry()) {
            return;
        }

        if (lastCompletedQualification.getEducationLevelType() == null || !isPostHighEducation(lastCompletedQualification)) {
            return;
        }

        if (Strings.isNullOrEmpty(bean.getEstabEscolaridadeAnt())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.previous.complete.institution.missing"),
                    i18n("error.Raides.validation.previous.complete.institution.missing.action"));
            bean.markAsInvalid();
        }

        if (Raides.Estabelecimentos.OUTRO.equals(bean.getEstabEscolaridadeAnt()) && Strings.isNullOrEmpty(
                bean.getOutroEstabEscolarAnt())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.previous.complete.other.institution.missing"),
                    i18n("error.Raides.validation.previous.complete.other.institution.missing.action"));
            bean.markAsInvalid();
        } else if (Raides.Estabelecimentos.OUTRO.equals(bean.getEstabEscolaridadeAnt())) {
            LegalReportContext.addWarn(target,
                    i18n("warn.Raides.validation.previous.complete.other.institution.given.instead.of.code"));
            bean.markAsInvalid();
        }
    }

    protected void validaCursoOficialInstituicaoOficial(final ExecutionYear executionYear, final Registration registration,
            final PrecedentDegreeInformation lastCompletedQualification, final IGrauPrecedenteCompleto bean) {

        final RaidesReportEntryTarget target = RaidesReportEntryTarget.of(registration, executionYear);

        if (!isPortuguesePostHighSchool(lastCompletedQualification)) {
            return;
        }

        if (Strings.isNullOrEmpty(lastCompletedQualification.getDegreeDesignation())) {
            return;
        }

        final DegreeDesignation degreeDesignation =
                DegreeDesignation.readByNameAndEducationLevelType(lastCompletedQualification.getDegreeDesignation(),
                        lastCompletedQualification.getEducationLevelType());

        if (degreeDesignation == null) {
            return;
        }

        boolean degreeDesignationContainsInstitution = false;
        for (final DegreeDesignation it : readByNameAndEducationLevelType(lastCompletedQualification.getDegreeDesignation(),
                lastCompletedQualification.getEducationLevelType())) {
            degreeDesignationContainsInstitution |=
                    it.getInstitutionUnitSet().contains(lastCompletedQualification.getInstitution());
        }

        if (!degreeDesignationContainsInstitution) {
            LegalReportContext.addError(target,
                    i18n("error.Raides.validation.official.precedent.degree.is.not.offered.by.institution"),
                    i18n("error.Raides.validation.official.precedent.degree.is.not.offered.by.institution.action"));
            bean.markAsInvalid();
        }

        if ((Raides.isMasterDegreeOrDoctoralDegree(registration) || Raides.isSpecializationDegree(registration))
                && !isPostHighEducation(lastCompletedQualification)) {
            LegalReportContext.addError(target,
                    i18n("error.Raides.validation.isMasterDoctoralOrSpecialization.but.completed.qualification.is.not.higher"),
                    i18n("error.Raides.validation.isMasterDoctoralOrSpecialization.but.completed.qualification.is.not.higher.action"));
            bean.markAsInvalid();
        }
    }

    protected void validaCursoAnteriorCompleto(final ExecutionYear executionYear, final Registration registration,
            final PrecedentDegreeInformation lastCompletedQualification, final IGrauPrecedenteCompleto bean) {

        final RaidesReportEntryTarget target = RaidesReportEntryTarget.of(registration, executionYear);

        if (lastCompletedQualification.getCountry() == null || !lastCompletedQualification.getCountry().isDefaultCountry()) {
            return;
        }

        if (lastCompletedQualification.getEducationLevelType() == null || !isPostHighEducation(lastCompletedQualification)) {
            return;
        }

        if (Strings.isNullOrEmpty(bean.getCursoEscolarAnt())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.previous.complete.degree.designation.missing"),
                    i18n("error.Raides.validation.previous.complete.degree.designation.missing.action"));
            bean.markAsInvalid();
        }

        if (Raides.NivelCursoOrigem.OUTRO.equals(bean.getCursoEscolarAnt()) && Strings.isNullOrEmpty(
                bean.getOutroCursoEscolarAnt())) {
            LegalReportContext.addError(target,
                    i18n("error.Raides.validation.previous.complete.other.degree.designation.missing"),
                    i18n("error.Raides.validation.previous.complete.other.degree.designation.missing.action"));
            bean.markAsInvalid();
        }

        if (isPortuguesePostHighSchool(lastCompletedQualification) && Raides.Cursos.OUTRO.equals(bean.getCursoEscolarAnt())) {
            LegalReportContext.addError(target,
                    i18n("error.Raides.validation.previous.complete.other.degree.designation.set.even.if.level.is.portuguese.higher.education"),
                    i18n("error.Raides.validation.previous.complete.other.degree.designation.set.even.if.level.is.portuguese.higher.education.action"));
            bean.markAsInvalid();
        }
    }

    protected Set<DegreeDesignation> readByNameAndEducationLevelType(String degreeDesignationName,
            EducationLevelType educationLevelType) {
        if ((educationLevelType == null) || (degreeDesignationName == null)) {
            return null;
        }

        Set<DegreeClassification> possibleClassifications = educationLevelType.getDegreeClassificationsSet();

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

        final RaidesReportEntryTarget target = RaidesReportEntryTarget.of(registration, executionYear);

        if (registration.getPerson().getMaritalStatus() != null) {

            String value = LegalMapping.find(report, LegalMappingType.MARITAL_STATUS)
                    .translate(registration.getPerson().getMaritalStatus());

            if (StringUtils.isBlank(value)) {
                LegalReportContext.addError(target, i18n("error.Raides.validation.maritalStatus.missing.translate",
                                registration.getPerson().getMaritalStatus().getLocalizedName()),
                        i18n("error.Raides.validation.maritalStatus.missing.translate.action",
                                registration.getPerson().getMaritalStatus().getLocalizedName()));

                bean.markAsInvalid();

            } else {
                bean.setEstadoCivil(value);
            }

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
        } else if (countryOfResidence != null && countryOfResidence.isDefaultCountry()) {
            bean.setResideConcelho(Raides.Concelho.OUTRO);
        }

        if (ingressionData != null) {
            if (ingressionData.getFatherEducationLevelType() != null) {

                String value = LegalMapping.find(report, LegalMappingType.EDUCATION_LEVEL)
                        .translate(ingressionData.getFatherEducationLevelType());

                if (StringUtils.isBlank(value)) {
                    LegalReportContext.addError(target, i18n("error.Raides.validation.fatherEducationLevel.missing.translate",
                                    ingressionData.getFatherEducationLevelType().getName().getContent()),
                            i18n("error.Raides.validation.fatherEducationLevel.missing.translate.action",
                                    ingressionData.getFatherEducationLevelType().getName().getContent()));

                    bean.markAsInvalid();

                } else {
                    bean.setNivelEscolarPai(value);
                }

            }

            if (ingressionData.getMotherEducationLevelType() != null) {

                String value = LegalMapping.find(report, LegalMappingType.EDUCATION_LEVEL)
                        .translate(ingressionData.getMotherEducationLevelType());

                if (StringUtils.isBlank(value)) {
                    LegalReportContext.addError(target, i18n("error.Raides.validation.motherEducationLevel.missing.translate",
                                    ingressionData.getMotherEducationLevelType().getName().getContent()),
                            i18n("error.Raides.validation.motherEducationLevel.missing.translate.action",
                                    ingressionData.getMotherEducationLevelType().getName().getContent()));

                    bean.markAsInvalid();

                } else {
                    bean.setNivelEscolarMae(value);
                }

            }

            if (ingressionData.getFatherProfessionalStatusType() != null) {

                String value = LegalMapping.find(report, LegalMappingType.PROFESSIONAL_STATUS)
                        .translate(ingressionData.getFatherProfessionalStatusType());

                if (StringUtils.isBlank(value)) {
                    LegalReportContext.addError(target,
                            i18n("error.Raides.validation.fatherProfessionalStatusType.missing.translate",
                                    ingressionData.getFatherProfessionalStatusType().getName().getContent()),
                            i18n("error.Raides.validation.fatherProfessionalStatusType.missing.translate.action",
                                    ingressionData.getFatherProfessionalStatusType().getName().getContent()));

                    bean.markAsInvalid();

                } else {
                    bean.setSituacaoProfPai(value);
                }

            }

        }

        if (ingressionData.getMotherProfessionalStatusType() != null) {

            String value = LegalMapping.find(report, LegalMappingType.PROFESSIONAL_STATUS)
                    .translate(ingressionData.getMotherProfessionalStatusType());

            if (StringUtils.isBlank(value)) {
                LegalReportContext.addError(target, i18n("error.Raides.validation.motherProfessionalStatusType.missing.translate",
                                ingressionData.getMotherProfessionalStatusType().getName().getContent()),
                        i18n("error.Raides.validation.motherProfessionalStatusType.missing.translate.action",
                                ingressionData.getMotherProfessionalStatusType().getName().getContent()));

                bean.markAsInvalid();

            } else {
                bean.setSituacaoProfMae(value);
            }
        }

        if (ingressionData.getProfessionalStatusType() != null) {

            String value = LegalMapping.find(report, LegalMappingType.PROFESSIONAL_STATUS)
                    .translate(ingressionData.getProfessionalStatusType());

            if (StringUtils.isBlank(value)) {
                LegalReportContext.addError(target,
                        i18n("error.Raides.validation.studentProfessionalStatusType.missing.translate",
                                ingressionData.getProfessionalStatusType().getName().getContent()),
                        i18n("error.Raides.validation.studentProfessionalStatusType.missing.translate.action",
                                ingressionData.getProfessionalStatusType().getName().getContent()));

                bean.markAsInvalid();

            } else {
                bean.setSituacaoProfAluno(value);
            }

        }

        if (ingressionData.getFatherProfessionCategoryType() != null) {

            String value = LegalMapping.find(report, LegalMappingType.PROFESSION_CATEGORY)
                    .translate(ingressionData.getFatherProfessionCategoryType());

            if (StringUtils.isBlank(value)) {
                LegalReportContext.addError(target, i18n("error.Raides.validation.fatherProfession.missing.translate",
                                ingressionData.getFatherProfessionCategoryType().getName().getContent()),
                        i18n("error.Raides.validation.fatherProfession.missing.translate.action",
                                ingressionData.getFatherProfessionCategoryType().getName().getContent()));

                bean.markAsInvalid();

            } else {
                bean.setProfissaoPai(transformProfessionCategoryTypeMappingValue(bean.getSituacaoProfPai(), value));
            }

        }

        if (ingressionData.getMotherProfessionCategoryType() != null) {

            String value = LegalMapping.find(report, LegalMappingType.PROFESSION_CATEGORY)
                    .translate(ingressionData.getMotherProfessionCategoryType());

            if (StringUtils.isBlank(value)) {
                LegalReportContext.addError(target, i18n("error.Raides.validation.motherProfession.missing.translate",
                                ingressionData.getMotherProfessionCategoryType().getName().getContent()),
                        i18n("error.Raides.validation.motherProfession.missing.translate.action",
                                ingressionData.getMotherProfessionCategoryType().getName().getContent()));

                bean.markAsInvalid();

            } else {
                bean.setProfissaoMae(transformProfessionCategoryTypeMappingValue(bean.getSituacaoProfMae(), value));
            }

        }

        if (ingressionData.getProfessionCategoryType() != null) {

            String value = LegalMapping.find(report, LegalMappingType.PROFESSION_CATEGORY)
                    .translate(ingressionData.getProfessionCategoryType());

            if (StringUtils.isBlank(value)) {
                LegalReportContext.addError(target, i18n("error.Raides.validation.studentProfession.missing.translate",
                                ingressionData.getProfessionCategoryType().getName().getContent()),
                        i18n("error.Raides.validation.studentProfession.missing.translate.action",
                                ingressionData.getProfessionCategoryType().getName().getContent()));

                bean.markAsInvalid();

            } else {
                bean.setProfissaoAluno(transformProfessionCategoryTypeMappingValue(bean.getSituacaoProfAluno(), value));
            }

        }

        validaInformacaoPessoal(executionYear, registration, bean);

    }

    private String transformProfessionCategoryTypeMappingValue(String professionalStatus, String professionCategory) {
        return SituacaoProfissional.requiresProfessionType(professionalStatus) ? professionCategory : null;
    }

    protected void validaInformacaoPessoal(final ExecutionYear executionYear, final Registration registration,
            final TblInscrito bean) {

        final RaidesReportEntryTarget target = RaidesReportEntryTarget.of(registration, executionYear);

        if (Strings.isNullOrEmpty(bean.getEstadoCivil())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.maritalStatus.missing"),
                    i18n("error.Raides.validation.maritalStatus.missing.action"));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getAlunoDeslocado())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.dislocated.from.residence.missing"),
                    i18n("error.Raides.validation.dislocated.from.residence.missing.action"));
            bean.markAsInvalid();
        }

        if (Raides.countryOfResidence(registration, executionYear) != null && Raides.countryOfResidence(registration,
                executionYear).isDefaultCountry()) {
            if (Strings.isNullOrEmpty(bean.getResideConcelho())) {
                LegalReportContext.addError(target, i18n("error.Raides.validation.district.subdivision.missing"),
                        i18n("error.Raides.validation.district.subdivision.missing.action"));
                bean.markAsInvalid();
            }
        }

        if (Strings.isNullOrEmpty(bean.getSituacaoProfPai())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.fatherProfessionalStatusType.missing"),
                    i18n("error.Raides.validation.fatherProfessionalStatusType.missing.action"));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getNivelEscolarPai())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.fatherEducationLevel.missing"),
                    i18n("error.Raides.validation.fatherEducationLevel.missing.action"));
            bean.markAsInvalid();
        }

        if (SituacaoProfissional.requiresProfessionType(bean.getSituacaoProfPai()) && Strings.isNullOrEmpty(
                bean.getProfissaoPai())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.fatherProfession.missing"),
                    i18n("error.Raides.validation.fatherProfession.missing.action"));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getSituacaoProfMae())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.motherProfessionalStatusType.missing"),
                    i18n("error.Raides.validation.motherProfessionalStatusType.missing.action"));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getNivelEscolarMae())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.motherEducationLevel.missing"),
                    i18n("error.Raides.validation.motherEducationLevel.missing.action"));
            bean.markAsInvalid();
        }

        if (SituacaoProfissional.requiresProfessionType(bean.getSituacaoProfMae()) && Strings.isNullOrEmpty(
                bean.getProfissaoMae())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.motherProfession.missing"),
                    i18n("error.Raides.validation.motherProfession.missing.action"));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getSituacaoProfAluno())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.studentProfessionalStatusType.missing"),
                    i18n("error.Raides.validation.studentProfessionalStatusType.missing.action"));
            bean.markAsInvalid();
        }

        if (SituacaoProfissional.requiresProfessionType(bean.getSituacaoProfAluno()) && Strings.isNullOrEmpty(
                bean.getProfissaoAluno())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.studentProfession.missing"),
                    i18n("error.Raides.validation.studentProfession.missing.action"));
            bean.markAsInvalid();
        }

    }

    public static interface AttendanceRegimeProvider {
        public String provide(LegalReport report, Registration registration, ExecutionYear executionYear, boolean mobility);
    }

    private static AttendanceRegimeProvider ATTENDANCE_REGIME_PROVIDER = (rpt, r, ey, mob) -> {
        final boolean onlyEnrolledOnDissertation =
                isOnlyEnrolledOnCompetenceCourseType(r, ey, mob, dissertationCompetenceCourseType);
        final boolean onlyEnrolledOnInternship = isOnlyEnrolledOnCompetenceCourseType(r, ey, mob, internshipCompetenceCourseType);
        final boolean onlyEnrolledOnProjectWork =
                isOnlyEnrolledOnCompetenceCourseType(r, ey, mob, projectWorkCompetenceCourseType);

        if (onlyEnrolledOnDissertation || onlyEnrolledOnInternship || onlyEnrolledOnProjectWork) {
            return LegalMapping.find(rpt, LegalMappingType.REGIME_FREQUENCIA).translate(Raides.RegimeFrequencia.ETD_CODE);
        }

        return LegalMapping.find(rpt, LegalMappingType.REGIME_FREQUENCIA).translate(r.getDegree().getExternalId());

    };

    public static void setAttendanceRegimeProvider(final AttendanceRegimeProvider provider) {
        ATTENDANCE_REGIME_PROVIDER = provider;
    }

    protected String regimeFrequencia(final Registration registration, final ExecutionYear executionYear, boolean mobility) {
        return ATTENDANCE_REGIME_PROVIDER.provide(report, registration, executionYear, mobility);
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
        if (isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, false, dissertationCompetenceCourseType)) {
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

            boolean isEnrollmentInDissertationCurricularCourse =
                    studentCurricularPlan.getEnrolmentsByExecutionYear(executionYear).stream()
                            .filter(e -> e.getCurricularCourse() == dissertation)
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

    public static boolean isMobilityStudent(final Person person, final ExecutionYear executionYear) {
        final Student student = person.getStudent();
        if (student == null) {
            return false;
        }

        final Collection<RegistrationProtocol> mobilityAggreements = RaidesInstance.getInstance().getMobilityAgreementsSet();
        return !mobilityAggreements.isEmpty() && student.getRegistrationStream()
                .filter(r -> r.getLastRegistrationState(executionYear).isActive() || r.getRegistrationYear() == executionYear)
                .anyMatch(r -> mobilityAggreements.contains(r.getRegistrationProtocol()));
    }
}
