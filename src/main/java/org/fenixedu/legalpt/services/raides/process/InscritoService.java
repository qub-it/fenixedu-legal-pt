package org.fenixedu.legalpt.services.raides.process;

import static org.fenixedu.legalpt.domain.raides.Raides.formatArgs;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.student.services.StatuteServices;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.mapping.LegalMapping;
import org.fenixedu.legalpt.domain.raides.Raides;
import org.fenixedu.legalpt.domain.raides.RaidesInstance;
import org.fenixedu.legalpt.domain.raides.TblInscrito;
import org.fenixedu.legalpt.domain.raides.mapping.LegalMappingType;
import org.fenixedu.legalpt.domain.raides.report.RaidesRequestParameter;
import org.fenixedu.legalpt.domain.report.LegalReport;
import org.joda.time.DateTime;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class InscritoService extends RaidesService {

    private static final BigDecimal MIN_NOTA_INGRESSO = new BigDecimal(95);
    private static final BigDecimal MAX_NOTA_INGRESSO = new BigDecimal(200);

    public InscritoService(final LegalReport report) {
        super(report);
    }

    public TblInscrito create(final RaidesRequestParameter raidesRequestParameter, final ExecutionYear executionYear,
            final Registration registration) {
        final Unit institutionUnit = raidesRequestParameter.getInstitution();

        final TblInscrito bean = new TblInscrito();
        bean.setRegistration(registration);

        preencheInformacaoMatricula(report, bean, institutionUnit, executionYear, registration);

        bean.setAnoCurricular(anoCurricular(registration, executionYear, false));
        bean.setPrimeiraVez(
                LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(isFirstTimeOnDegree(registration, executionYear)));

        bean.setRegimeFrequencia(regimeFrequencia(registration, executionYear, false));

        final DateTime maximumAnnulmentDate =
                findMaximumAnnulmentDate(raidesRequestParameter.getPeriodsForEnrolled(), executionYear);
        if (Raides.isDoctoralDegree(registration)) {
            bean.setEctsInscricao(doctoralEnrolledEcts(executionYear, registration, maximumAnnulmentDate));
        } else {
            bean.setEctsInscricao(enrolledEcts(executionYear, registration, maximumAnnulmentDate, false));
        }

        if (!isFirstTimeOnDegree(registration, executionYear)) {
            bean.setEctsAcumulados(ectsAcumulados(registration, executionYear));
        }

        bean.setTempoParcial(
                LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(isInPartialRegime(executionYear, registration)));
        bean.setBolseiro(bolseiro(registration, executionYear));

        if (isFirstCycle(registration) && isFirstTimeOnDegree(registration, executionYear)) {
            bean.setFormaIngresso(LegalMapping.find(report, LegalMappingType.REGISTRATION_INGRESSION_TYPE)
                    .translate(registration.getIngressionType()));

            if (isDegreeChangeOrTransfer(raidesRequestParameter, registration)) {
                final PrecedentDegreeInformation precedentQualification =
                        registration.getStudentCandidacy().getPreviousDegreeInformation();
                if (precedentQualification != null && precedentQualification.getInstitution() != null
                        && !Strings.isNullOrEmpty(precedentQualification.getInstitution().getCode())) {
                    bean.setEstabInscricaoAnt(precedentQualification.getInstitution().getCode());
                } else if (precedentQualification != null && precedentQualification.getInstitution() != null) {
                    bean.setEstabInscricaoAnt(Raides.Cursos.OUTRO);
                    bean.setOutroEstabInscAnt(precedentQualification.getInstitution().getNameI18n().getContent());
                }

                bean.setNumInscCursosAnt(precedentQualification.getNumberOfEnrolmentsInPreviousDegrees());

            } else if (isGeneralAccessRegime(raidesRequestParameter, registration)) {
                if (registration.getStudentCandidacy().getEntryGrade() != null) {
                    bean.setNotaIngresso(registration.getStudentCandidacy().getEntryGrade().toString());
                }

                if (registration.getStudentCandidacy().getPlacingOption() != null) {
                    bean.setOpcaoIngresso(registration.getStudentCandidacy().getPlacingOption().toString());
                }
            }
        }

        if (!isFirstTimeOnDegree(registration, executionYear)) {

            final List<ExecutionYear> enrolmentsExecutionYears = Lists.newArrayList(RegistrationServices
                    .getEnrolmentYearsIncludingPrecedentRegistrations(registration, executionYear.getPreviousExecutionYear()));

            if (enrolmentsExecutionYears.size() >= 1) {
                bean.setAnoUltimaInscricao(
                        Collections.max(enrolmentsExecutionYears, ExecutionYear.COMPARATOR_BY_YEAR).getQualifiedName());
            }

            bean.setNumInscNesteCurso(numberOfYearsEnrolled(executionYear, registration));
        }

        bean.setEstudanteTrabalhador(
                LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(isWorkingStudent(registration, executionYear)));

        preencheInformacaoPessoal(executionYear, registration, bean);
        preencheGrauPrecedentCompleto(bean, institutionUnit, executionYear, registration);

        validaInformacaoMudancaCursoTransferencia(raidesRequestParameter, bean, institutionUnit, executionYear, registration);
        validaInformacaoRegimeGeralAcesso(raidesRequestParameter, bean, institutionUnit, executionYear, registration);
        validaNumInscricoesNoCurso(raidesRequestParameter, bean, institutionUnit, executionYear, registration);

        return bean;
    }

    /*
     * VALIDACOES
     */

    private BigDecimal ectsAcumulados(final Registration registration, final ExecutionYear executionYear) {
        if (((RaidesInstance) report).isSumEctsCreditsBetweenPlans()
                && RegistrationServices.canCollectAllPlansForCurriculum(registration)) {
            LegalReportContext.addWarn("",
                    i18n("warn.Raides.ects.acumulados.sum.of.student.curricular.plans", formatArgs(registration, executionYear)));

            return sumEctsAcumulados(registration, executionYear);
        }

        return registration.getStudentCurricularPlan(executionYear).getRoot().getCurriculum(executionYear).getSumEctsCredits()
                .setScale(1);
    }

    private BigDecimal sumEctsAcumulados(final Registration registration, final ExecutionYear executionYear) {
        return RegistrationServices.getAllPlansCurriculum(registration, executionYear).getSumEctsCredits().setScale(1);
    }

    private void validaNumInscricoesNoCurso(final RaidesRequestParameter raidesRequestParameter, final TblInscrito bean,
            final Unit institutionUnit, final ExecutionYear executionYear, final Registration registration) {

        if (!isFirstTimeOnDegree(registration, executionYear) && new Integer(0).equals(bean.getNumInscNesteCurso())) {
            LegalReportContext.addError("", i18n(
                    "error.Raides.validation.is.not.first.time.student.but.number.previous.enrolments.in.registration.is.zero",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }
    }

    protected void validaInformacaoRegimeGeralAcesso(final RaidesRequestParameter raidesRequestParameter, final TblInscrito bean,
            final Unit institutionUnit, final ExecutionYear executionYear, final Registration registration) {
        if (!isFirstCycle(registration) || !isFirstTimeOnDegree(registration, executionYear)) {
            return;
        }

        if (!isGeneralAccessRegime(raidesRequestParameter, registration)) {
            return;
        }

        if (Strings.isNullOrEmpty(bean.getNotaIngresso()) || Strings.isNullOrEmpty(bean.getOpcaoIngresso())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.general.access.regime.incomplete", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
            return;
        }

        if (!Strings.isNullOrEmpty(bean.getNotaIngresso())) {
            try {
                final BigDecimal value = new BigDecimal(bean.getNotaIngresso());
                if (value.compareTo(MIN_NOTA_INGRESSO) < 0 || value.compareTo(MAX_NOTA_INGRESSO) > 0) {
                    LegalReportContext.addError("",
                            i18n("error.Raides.validation.general.access.regime.notaIngresso.in.wrong.interval",
                                    formatArgs(registration, executionYear)));
                    bean.markAsInvalid();
                    return;
                }
            } catch (NumberFormatException e) {
                LegalReportContext.addError("", i18n("error.Raides.validation.general.access.regime.notaIngresso.wrong.format",
                        formatArgs(registration, executionYear)));
                bean.markAsInvalid();
                return;
            }
        }
    }

    protected void validaInformacaoMudancaCursoTransferencia(final RaidesRequestParameter raidesRequestParameter,
            final TblInscrito bean, final Unit institutionUnit, final ExecutionYear executionYear,
            final Registration registration) {
        if (!isFirstCycle(registration) || !isFirstTimeOnDegree(registration, executionYear)) {
            return;
        }

        if (!isDegreeChangeOrTransfer(raidesRequestParameter, registration)) {
            return;
        }

        if (Strings.isNullOrEmpty(bean.getEstabInscricaoAnt())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.degree.change.or.transfer.requires.information",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        } else if (Raides.Estabelecimentos.OUTRO.equals(bean.getEstabInscricaoAnt())
                && Strings.isNullOrEmpty(bean.getOutroEstabInscAnt())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.degree.change.or.transfer.requires.information",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (bean.getNumInscCursosAnt() == null) {
            LegalReportContext.addError("", i18n("error.Raides.validation.degree.change.or.transfer.requires.information",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }
    }

    protected boolean isFirstCycle(final Registration registration) {
        return registration.getDegreeType().isFirstCycle();
    }

    protected String bolseiro(final Registration registration, final ExecutionYear executionYear) {

        final PersonalIngressionData pid = registration.getStudent().getPersonalIngressionDataByExecutionYear(executionYear);
        if (pid == null || pid.getGrantOwnerType() == null
                || pid.getGrantOwnerType() == GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP) {
            return LegalMapping.find(report, LegalMappingType.GRANT_OWNER_TYPE).translate(Raides.Bolseiro.NAO_BOLSEIRO);
        }

        if (!hasOtherActiveRegistrationWhichRequiresStatuteToReportGrantOwner(registration, executionYear)) {
            return LegalMapping.find(report, LegalMappingType.GRANT_OWNER_TYPE).translate(pid.getGrantOwnerType());
        }

        if (!hasGrantOwnerStatuteInAnyRegistration(registration.getStudent(), executionYear)) {
            LegalReportContext.addError("",
                    i18n("error.Raides.grantOwner.requires.statute", formatArgs(registration, executionYear)));

            return LegalMapping.find(report, LegalMappingType.GRANT_OWNER_TYPE).translate(Raides.Bolseiro.NAO_BOLSEIRO);
        }

        if (hasGrantOwnerStatute(registration, executionYear)) {
            return LegalMapping.find(report, LegalMappingType.GRANT_OWNER_TYPE).translate(pid.getGrantOwnerType());
        }

        return LegalMapping.find(report, LegalMappingType.GRANT_OWNER_TYPE).translate(Raides.Bolseiro.NAO_BOLSEIRO);

    }

    //TODO: replace with scholarship module
    private boolean hasOtherActiveRegistrationWhichRequiresStatuteToReportGrantOwner(final Registration registration,
            final ExecutionYear executionYear) {
        return registration.getStudent().getRegistrationsSet().stream()
                .anyMatch(r -> r != registration && RegistrationServices.getEnrolmentYears(r).contains(executionYear));
    }

    //TODO: replace with scholarship module
    private boolean hasGrantOwnerStatute(Registration registration, ExecutionYear executionYear) {
        return registration.getStudentStatutesSet().stream().anyMatch(s -> s.isValidOn(executionYear)
                && RaidesInstance.getInstance().getGrantOwnerStatuteTypesSet().contains(s.getType()));
    }

    //TODO: replace with scholarship module
    private boolean hasGrantOwnerStatuteInAnyRegistration(final Student student, final ExecutionYear executionYear) {
        return student.getRegistrationsSet().stream().anyMatch(r -> hasGrantOwnerStatute(r, executionYear));
    }

    protected boolean isWorkingStudent(final Registration registration, final ExecutionYear executionYear) {
        return StatuteServices.findStatuteTypes(registration, executionYear).stream().anyMatch(s -> s.isWorkingStudentStatute());
    }

    protected boolean isInPartialRegime(final ExecutionYear executionYear, final Registration registration) {
        return registration.isPartialRegime(executionYear);
    }

    private boolean isGeneralAccessRegime(final RaidesRequestParameter raidesRequestParameter, final Registration registration) {
        return Raides.isGeneralAccessRegime(raidesRequestParameter, registration.getIngressionType());
    }

    protected boolean isDegreeChangeOrTransfer(final RaidesRequestParameter raidesRequestParameter,
            final Registration registration) {
        return Raides.isDegreeChange(raidesRequestParameter, registration.getIngressionType())
                || Raides.isDegreeTransfer(raidesRequestParameter, registration.getIngressionType());
    }

    protected Integer numberOfYearsEnrolled(final ExecutionYear executionYear, final Registration registration) {
        return RegistrationServices
                .getEnrolmentYearsIncludingPrecedentRegistrations(registration, executionYear.getPreviousExecutionYear()).size();
    }

    protected boolean deliveredDissertation(final ExecutionYear executionYear, final Registration registration) {
        final StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(executionYear);

        Collection<Enrolment> dissertationEnrolments = studentCurricularPlan.getDissertationEnrolments();

        for (final Enrolment enrolment : dissertationEnrolments) {
            if (enrolment.isValid(executionYear) && enrolment.isApproved()) {
                return true;
            }
        }

        return false;
    }

}
