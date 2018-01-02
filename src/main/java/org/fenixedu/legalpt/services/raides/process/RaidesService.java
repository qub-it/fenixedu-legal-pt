package org.fenixedu.legalpt.services.raides.process;

import static org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides.formatArgs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CompetenceCourseType;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.IEnrolment;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.RootCourseGroup;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.raides.DegreeClassification;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.IGrauPrecedenteCompleto;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.IMatricula;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides.Ramo;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblInscrito;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.BranchMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.LegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesRequestPeriodParameter;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion.RegistrationConclusionInformation;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion.RegistrationConclusionServices;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.DateTime;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken.TypeSet;

public class RaidesService {

    private static final int MAX_OTHER_SCHOOL_LEVEL_LENGTH = 80;
    protected LegalReport report;

    public RaidesService(final LegalReport report) {
        this.report = report;
    }

    protected String anoCurricular(final Registration registration, final ExecutionYear executionYear) {
        if (Raides.isDoctoralDegree(registration)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(Raides.AnoCurricular.NAO_APLICAVEL_CODE);
        }

        if (isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, CompetenceCourseType.DISSERTATION)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(Raides.AnoCurricular.DISSERTACAO_CODE);
        } else if(isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, CompetenceCourseType.INTERNSHIP)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(Raides.AnoCurricular.ESTAGIO_FINAL_CODE);
        } else if(isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, CompetenceCourseType.PROJECT_WORK)) {
            return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(Raides.AnoCurricular.TRABALHO_PROJECTO_CODE);
        }

        return LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR)
                .translate(String.valueOf(RegistrationServices.getCurricularYear(registration, executionYear).getResult()));
    }

    protected boolean isOnlyEnrolledOnCompetenceCourseType(final Registration registration, final ExecutionYear executionYear, 
            final CompetenceCourseType competenceCourseType) {
        final Collection<Enrolment> enrolments = registration.getEnrolments(executionYear);

        final Set<CompetenceCourseType> typesSet = Sets.newHashSet();
        for (final Enrolment enrolment : enrolments) {
            final CurricularCourse curricularCourse = enrolment.getCurricularCourse();
            final CompetenceCourseType type = curricularCourse != null ? curricularCourse.getCompetenceCourse().getType() : CompetenceCourseType.REGULAR;
            
            typesSet.add(type);
        }
        
        if (typesSet.size() != 1) {
            return false;
        }
        
        return typesSet.iterator().next() == competenceCourseType;
    }
    
    protected boolean isFirstTimeOnDegree(final Registration registration, final ExecutionYear executionYear) {
        if (!Raides.getPrecedentDegreeRegistrations(registration).isEmpty()) {
            return false;
        }

        return executionYear == registration.getRegistrationYear();
    }

    /*
     * OTHER METHODS
     */

    protected void preencheInformacaoMatricula(final LegalReport report, final IMatricula bean, final Unit institutionUnit,
            final ExecutionYear executionYear, final Registration registration) {

        bean.setIdEstab(institutionUnit.getCode());
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
            bean.setCurso(degree(registration).getMinistryCode());
        }

        preencheRamo(report, bean, executionYear, registration, false);

    }

    protected void preencheRamo(final LegalReport report, final IMatricula bean,  final ExecutionYear executionYear, 
            final Registration registration, final boolean forScholarPart) {
        final Set<CourseGroup> branches = forScholarPart ? scholarPartBranches(registration, executionYear) : branches(registration, executionYear);
        
        if(!forScholarPart) {
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
            if(!conclusionInfo.isScholarPart()) {
                continue;
            }
            
            if(!conclusionInfo.isConcluded()) {
                continue;
            }
            
            conclusionInfoToUse = conclusionInfo;
            
            
            break;
        }

        for (final CurriculumGroup curriculumGroup : conclusionInfoToUse.getCurriculumGroup().getAllCurriculumGroups()) {
            if (curriculumGroup.getDegreeModule() == null) {
                continue;
            }
            
            final CourseGroup courseGroup = curriculumGroup.getDegreeModule();
            if (BranchMappingType.readMapping(report).isKeyDefined(courseGroup)) {
                result.add(courseGroup);
            }
        }

        return result;
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

    protected Degree degree(final Registration registration) {
        if (!registration.getDegree().isEmpty()) {
            return registration.getDegree();
        }

        final Map<Degree, Integer> enrolmentsByDegreeCountMap = new HashMap<Degree, Integer>();
        Collection<CurriculumLine> allCurriculumLines = Raides.getAllCurriculumLines(registration);

        for (final CurriculumLine curriculumLine : allCurriculumLines) {
            if (!curriculumLine.isEnrolment()) {
                continue;
            }

            Degree degree = ((Enrolment) curriculumLine).getDegreeModule().getDegree();

            if (!enrolmentsByDegreeCountMap.containsKey(degree)) {
                enrolmentsByDegreeCountMap.put(degree, 0);
            }

            enrolmentsByDegreeCountMap.put(degree, enrolmentsByDegreeCountMap.get(degree) + 1);
        }

        final Map<Degree, Integer> enrolmentsByDegreeCountMapSorted =
                new TreeMap<Degree, Integer>(new DEGREE_VALUE_COMPARATOR(enrolmentsByDegreeCountMap));
        enrolmentsByDegreeCountMapSorted.putAll(enrolmentsByDegreeCountMap);

        return enrolmentsByDegreeCountMapSorted.entrySet().iterator().next().getKey();

    }

    protected void preencheGrauPrecedentCompleto(final IGrauPrecedenteCompleto bean, final Unit institutionUnit,
            final ExecutionYear executionYear, final Registration registration) {
        final StudentCandidacy studentCandidacy = registration.getStudentCandidacy();
        final PrecedentDegreeInformation lastCompletedQualification = studentCandidacy.getPrecedentDegreeInformation();

        if (lastCompletedQualification == null) {
            return;
        }

        if (lastCompletedQualification.getSchoolLevel() != null) {
            bean.setEscolaridadeAnterior(LegalMapping.find(report, LegalMappingType.PRECEDENT_SCHOOL_LEVEL)
                    .translate(lastCompletedQualification.getSchoolLevel()));

            if (SchoolLevelType.OTHER.equals(lastCompletedQualification.getSchoolLevel())) {
                
                if(!Strings.isNullOrEmpty(lastCompletedQualification.getOtherSchoolLevel())){
                    bean.setOutroEscolaridadeAnterior(lastCompletedQualification.getOtherSchoolLevel().substring(0,
                            Math.min(MAX_OTHER_SCHOOL_LEVEL_LENGTH, lastCompletedQualification.getOtherSchoolLevel().length())));    
                }
            }
        }

        if (lastCompletedQualification.getCountry() != null) {
            bean.setPaisEscolaridadeAnt(lastCompletedQualification.getCountry().getCode());
        }

        if (lastCompletedQualification.getConclusionYear() != null) {
            bean.setAnoEscolaridadeAnt(lastCompletedQualification.getConclusionYear().toString());
        }

        if (lastCompletedQualification.getInstitution() != null && lastCompletedQualification.getInstitution().isOfficial()) {
            bean.setEstabEscolaridadeAnt(lastCompletedQualification.getInstitution().getCode());
        } else if (lastCompletedQualification.getInstitution() != null) {
            bean.setEstabEscolaridadeAnt(Raides.Estabelecimentos.OUTRO);
            bean.setOutroEstabEscolarAnt(lastCompletedQualification.getInstitution().getNameI18n().getContent());
        }

        boolean precedentDegreeDesignationFilled = false;
        if (isPrecedentDegreePortugueseHigherEducation(lastCompletedQualification)
                && !Strings.isNullOrEmpty(lastCompletedQualification.getDegreeDesignation())) {
            final DegreeDesignation degreeDesignation = DegreeDesignation.readByNameAndSchoolLevel(
                    lastCompletedQualification.getDegreeDesignation(), lastCompletedQualification.getSchoolLevel());

            if (degreeDesignation != null) {
                bean.setCursoEscolarAnt(degreeDesignation.getCode());
                precedentDegreeDesignationFilled = true;
            }
        }

        if (!precedentDegreeDesignationFilled && !Strings.isNullOrEmpty(lastCompletedQualification.getDegreeDesignation())) {
            bean.setCursoEscolarAnt(Raides.Cursos.OUTRO);
            bean.setOutroCursoEscolarAnt(lastCompletedQualification.getDegreeDesignation());
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

        validaGrauPrecedenteCompleto(institutionUnit, executionYear, registration, lastCompletedQualification, bean);
        validaCursoOficialInstituicaoOficial(institutionUnit, executionYear, registration, lastCompletedQualification, bean);
    }

    private AcademicalInstitutionType highSchoolType(StudentCandidacy studentCandidacy) {
        if (studentCandidacy.getHighSchoolType() != null) {
            return studentCandidacy.getHighSchoolType();
        }

        if (studentCandidacy.getPrecedentDegreeInformation() != null
                && studentCandidacy.getPrecedentDegreeInformation().getPersonalIngressionData() != null
                && studentCandidacy.getPrecedentDegreeInformation().getPersonalIngressionData().getHighSchoolType() != null) {
            return studentCandidacy.getPrecedentDegreeInformation().getPersonalIngressionData().getHighSchoolType();
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

    protected boolean isPrecedentDegreePortugueseHigherEducation(final PrecedentDegreeInformation lastCompletedQualification) {
        return lastCompletedQualification.getCountry() != null && lastCompletedQualification.getCountry().isDefaultCountry()
                && isHigherEducation(lastCompletedQualification);
    }

    protected boolean isHigherEducation(final PrecedentDegreeInformation lastCompletedQualification) {
        return lastCompletedQualification.getSchoolLevel() != null
                && lastCompletedQualification.getSchoolLevel().isHigherEducation();
    }

    protected void validaGrauPrecedenteCompleto(final Unit institutionUnit, final ExecutionYear executionYear,
            final Registration registration, final PrecedentDegreeInformation lastCompletedQualification,
            final IGrauPrecedenteCompleto bean) {

        if (Strings.isNullOrEmpty(bean.getEscolaridadeAnterior())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.previous.complete.school.level.missing",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Raides.NivelCursoOrigem.OUTRO.equals(Strings.isNullOrEmpty(bean.getOutroEscolaridadeAnterior()))) {
            if (Strings.isNullOrEmpty(bean.getOutroEscolaridadeAnterior())) {
                LegalReportContext.addError("", i18n("error.Raides.validation.previous.complete.other.school.level.missing",
                        formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            }
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

        /*
        if(Strings.isNullOrEmpty(bean.getAnoEscolaridadeAnt())) {
            try {
                
            } catch(NumberFormatException e) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.previous.complete.year.missing",
                                String.valueOf(registration.getStudent().getNumber()), registration.getDegree().getCode(),registration.getDegreeNameWithDescription(),
                                executionYear.getQualifiedName()));
                bean.markAsInvalid();
            }
        }
        */

        validaEstabelecimentoAnteriorCompleto(institutionUnit, executionYear, registration, lastCompletedQualification, bean);
        validaCursoAnteriorCompleto(institutionUnit, executionYear, registration, lastCompletedQualification, bean);

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

    protected void validaEstabelecimentoAnteriorCompleto(final Unit institutionUnit, final ExecutionYear executionYear,
            final Registration registration, final PrecedentDegreeInformation lastCompletedQualification,
            final IGrauPrecedenteCompleto bean) {
        if (lastCompletedQualification.getCountry() == null || !lastCompletedQualification.getCountry().isDefaultCountry()) {
            return;
        }

        if (lastCompletedQualification.getSchoolLevel() == null
                || !lastCompletedQualification.getSchoolLevel().isHigherEducation()) {
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

    protected void validaCursoOficialInstituicaoOficial(final Unit institutionUnit, final ExecutionYear executionYear,
            final Registration registration, final PrecedentDegreeInformation lastCompletedQualification,
            final IGrauPrecedenteCompleto bean) {
        if (!isPrecedentDegreePortugueseHigherEducation(lastCompletedQualification)) {
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
                && !isHigherEducation(lastCompletedQualification)) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.isMasterDoctoralOrSpecialization.but.completed.qualification.is.not.higher",
                            formatArgs(registration, executionYear)));
        }
    }

    protected void validaCursoAnteriorCompleto(final Unit institutionUnit, final ExecutionYear executionYear,
            final Registration registration, final PrecedentDegreeInformation lastCompletedQualification,
            final IGrauPrecedenteCompleto bean) {
        if (lastCompletedQualification.getCountry() == null || !lastCompletedQualification.getCountry().isDefaultCountry()) {
            return;
        }

        if (lastCompletedQualification.getSchoolLevel() == null
                || !lastCompletedQualification.getSchoolLevel().isHigherEducation()) {
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

        if (isPrecedentDegreePortugueseHigherEducation(lastCompletedQualification)
                && Raides.Cursos.OUTRO.equals(bean.getCursoEscolarAnt())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.previous.complete.other.degree.designation.set.even.if.level.is.portuguese.higher.education",
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

        PersonalIngressionData personalIngressionData = Raides.personalIngressionData(registration, executionYear);
        if (personalIngressionData != null && personalIngressionData.getDislocatedFromPermanentResidence() != null) {
            bean.setAlunoDeslocado(LegalMapping.find(report, LegalMappingType.BOOLEAN)
                    .translate(personalIngressionData.getDislocatedFromPermanentResidence()));
        }

        if (Strings.isNullOrEmpty(bean.getAlunoDeslocado())
                && ((RaidesInstance) report).getDefaultDistrictOfResidence() != null) {

            if (Raides.countryOfResidence(registration, executionYear) != null
                    && !Raides.countryOfResidence(registration, executionYear).isDefaultCountry()) {
                bean.setAlunoDeslocado(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(false));
            } else if (Raides.countryOfResidence(registration, executionYear) != null
                    && Raides.districtSubdivisionOfResidence(registration, executionYear) != null) {
                bean.setAlunoDeslocado(
                        LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(Raides.districtOfResidence(registration,
                                executionYear) != ((RaidesInstance) report).getDefaultDistrictOfResidence()));
            }

            if (!Strings.isNullOrEmpty(bean.getAlunoDeslocado())) {
                LegalReportContext.addWarn("", i18n("warn.Raides.validation.dislocated.from.residence.missing",
                        formatArgs(registration, executionYear)));
            }

        }

        final Country countryOfResidence = Raides.countryOfResidence(registration, executionYear);
        final DistrictSubdivision districtSubdivision = Raides.districtSubdivisionOfResidence(registration, executionYear);
        if (countryOfResidence != null && districtSubdivision != null) {
            bean.setResideConcelho(districtSubdivision.getDistrict().getCode() + districtSubdivision.getCode());
        }

        if (personalIngressionData != null) {
            if (personalIngressionData.getFatherSchoolLevel() != null) {
                bean.setNivelEscolarPai(LegalMapping.find(report, LegalMappingType.SCHOOL_LEVEL)
                        .translate(personalIngressionData.getFatherSchoolLevel()));
            }

            if (personalIngressionData.getMotherSchoolLevel() != null) {
                bean.setNivelEscolarMae(LegalMapping.find(report, LegalMappingType.SCHOOL_LEVEL)
                        .translate(personalIngressionData.getMotherSchoolLevel()));
            }

            if (personalIngressionData.getFatherProfessionalCondition() != null) {
                bean.setSituacaoProfPai(LegalMapping.find(report, LegalMappingType.PROFESSIONAL_SITUATION_CONDITION)
                        .translate(personalIngressionData.getFatherProfessionalCondition()));
            }

            if (personalIngressionData.getMotherProfessionalCondition() != null) {
                bean.setSituacaoProfMae(LegalMapping.find(report, LegalMappingType.PROFESSIONAL_SITUATION_CONDITION)
                        .translate(personalIngressionData.getMotherProfessionalCondition()));
            }

            if (personalIngressionData.getProfessionalCondition() != null) {
                bean.setSituacaoProfAluno(LegalMapping.find(report, LegalMappingType.PROFESSIONAL_SITUATION_CONDITION)
                        .translate(personalIngressionData.getProfessionalCondition()));
            }

            if (personalIngressionData.getFatherProfessionType() != null) {
                bean.setProfissaoPai(LegalMapping.find(report, LegalMappingType.PROFESSION_TYPE)
                        .translate(personalIngressionData.getFatherProfessionType()));
            }

            if (personalIngressionData.getMotherProfessionType() != null) {
                bean.setProfissaoMae(LegalMapping.find(report, LegalMappingType.PROFESSION_TYPE)
                        .translate(personalIngressionData.getMotherProfessionType()));
            }

            if (personalIngressionData.getProfessionType() != null) {
                bean.setProfissaoAluno(LegalMapping.find(report, LegalMappingType.PROFESSION_TYPE)
                        .translate(personalIngressionData.getProfessionType()));
            }
        }

        validaInformacaoPessoal(executionYear, registration, bean);
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

        if (!Strings.isNullOrEmpty(bean.getProfissaoAluno())
                && Raides.Profissao.NAO_DISPONIVEL.equals(bean.getProfissaoAluno())) {
            // errors.addError("error.Raides.validation.student.profession.cannot.be.not.available", registration.getStudent()
            //        .getNumber(), registration.getDegree().getNameI18N().getContent(), executionYear.getQualifiedName());
        }

        if (Strings.isNullOrEmpty(bean.getNivelEscolarPai()) || Strings.isNullOrEmpty(bean.getNivelEscolarMae())
                || Strings.isNullOrEmpty(bean.getSituacaoProfPai()) || Strings.isNullOrEmpty(bean.getSituacaoProfMae())
                || Strings.isNullOrEmpty(bean.getSituacaoProfAluno()) || Strings.isNullOrEmpty(bean.getProfissaoPai())
                || Strings.isNullOrEmpty(bean.getProfissaoMae()) || Strings.isNullOrEmpty(bean.getProfissaoAluno())) {
            // errors.addError("error.Raides.validation.profesional.situation.missing", registration.getStudent().getNumber(),
            //        registration.getDegree().getNameI18N().getContent(), executionYear.getQualifiedName());
        }
    }

    protected String regimeFrequencia(final Registration registration, final ExecutionYear executionYear) {
        final boolean onlyEnrolledOnDissertation = isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, CompetenceCourseType.DISSERTATION);
        final boolean onlyEnrolledOnInternship = isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, CompetenceCourseType.INTERNSHIP);
        final boolean onlyEnrolledOnProjectWork = isOnlyEnrolledOnCompetenceCourseType(registration, executionYear, CompetenceCourseType.PROJECT_WORK);

        if (onlyEnrolledOnDissertation || onlyEnrolledOnInternship || onlyEnrolledOnProjectWork) {
            return LegalMapping.find(report, LegalMappingType.REGIME_FREQUENCIA).translate(Raides.RegimeFrequencia.ETD_CODE);
        }

        return LegalMapping.find(report, LegalMappingType.REGIME_FREQUENCIA).translate(registration.getDegree().getExternalId());
    }

    protected DateTime findMaximumAnnulmentDate(final List<RaidesRequestPeriodParameter> periods, final ExecutionYear executionYear) {
        return periods.stream().filter(p -> p.getAcademicPeriod() == executionYear)
                .max(Comparator.comparing(RaidesRequestPeriodParameter::getEnd)).get().getEnd()
                .plusDays(1).toDateTimeAtStartOfDay().minusSeconds(1);
    }

    protected BigDecimal enrolledEcts(final ExecutionYear executionYear, final Registration registration, final DateTime maximumAnnulmentDate) {
        final StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(executionYear);
        double result = 0.0;

        for (final Enrolment enrolment : studentCurricularPlan.getEnrolmentsSet()) {
            if(Raides.isEnrolmentAnnuled(enrolment, maximumAnnulmentDate)) {
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

    protected BigDecimal doctoralEnrolledEcts(final ExecutionYear executionYear, final Registration registration, final DateTime maximumAnnulmentDate) {
        if (BigDecimal.ZERO.compareTo(enrolledEcts(executionYear, registration, maximumAnnulmentDate)) != 0) {
            final BigDecimal enrolledEcts = enrolledEcts(executionYear, registration, maximumAnnulmentDate);

            CurricularCourse dissertation = phdDissertation(executionYear, registration);
            if (dissertation != null && enrolledEcts.compareTo(new BigDecimal(dissertation.getEctsCredits())) >= 0) {
                BigDecimal result = enrolledEcts.subtract(new BigDecimal(dissertation.getEctsCredits()));

                if (BigDecimal.ZERO.compareTo(result) == 0) {
                    return null;
                }
            }

            return enrolledEcts;
        }

        return null;
    }

    private CurricularCourse phdDissertation(final ExecutionYear executionYear, final Registration registration) {
        final StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(executionYear);

        Collection<CurricularCourse> allDissertationCurricularCourses =
                studentCurricularPlan.getDegreeCurricularPlan().getDissertationCurricularCourses(executionYear);

        for (final CurricularCourse dissertation : allDissertationCurricularCourses) {
            if (BigDecimal.ZERO.compareTo(new BigDecimal(dissertation.getCredits())) != 0) {
                return dissertation;
            }
        }

        return null;
    }

    public String i18n(String key, String... arguments) {
        return ULisboaSpecificationsUtil.bundle(key, arguments);
    }

}
