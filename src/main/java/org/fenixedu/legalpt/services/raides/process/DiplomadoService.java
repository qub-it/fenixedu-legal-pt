package org.fenixedu.legalpt.services.raides.process;

import static org.fenixedu.legalpt.domain.raides.Raides.formatArgs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.domain.student.curriculum.conclusion.RegistrationConclusionInformation;
import org.fenixedu.academic.domain.student.curriculum.conclusion.RegistrationConclusionServices;
import org.fenixedu.academic.domain.student.mobility.MobilityRegistrationInformation;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.mapping.LegalMapping;
import org.fenixedu.legalpt.domain.raides.Raides;
import org.fenixedu.legalpt.domain.raides.RaidesInstance;
import org.fenixedu.legalpt.domain.raides.TblDiplomado;
import org.fenixedu.legalpt.domain.raides.mapping.LegalMappingType;
import org.fenixedu.legalpt.domain.raides.report.RaidesRequestParameter;
import org.fenixedu.legalpt.domain.raides.report.RaidesRequestPeriodParameter;
import org.fenixedu.legalpt.domain.report.LegalReport;

import com.google.common.base.Strings;

public class DiplomadoService extends RaidesService {

    protected boolean valid = true;

    public DiplomadoService(final LegalReport report) {
        super(report);
    }

    public TblDiplomado createNormal(final RaidesRequestParameter raidesRequestParameter,
            final RaidesRequestPeriodParameter graduatedPeriod, final ExecutionYear executionYear,
            final Registration registration) {

        final Unit institutionUnit = raidesRequestParameter.getInstitution();

        final TblDiplomado bean = new TblDiplomado();
        bean.setRegistration(registration);

        preencheInformacaoMatricula(report, bean, institutionUnit, executionYear, registration);

        final RegistrationConclusionInformation terminalConclusionInfo =
                terminalConclusionInformation(registration, graduatedPeriod, executionYear);
        if (terminalConclusionInfo != null && (RaidesInstance.getInstance().isReportGraduatedWithoutConclusionProcess()
                || terminalConclusionInfo.getRegistrationConclusionBean().isConclusionProcessed())) {

            final RegistrationConclusionBean registrationConclusionBean = terminalConclusionInfo.getRegistrationConclusionBean();

            bean.setConcluiGrau(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true));
            bean.setAnoLectivo(raidesRequestParameter.getGraduatedExecutionYear() != null ? raidesRequestParameter
                    .getGraduatedExecutionYear()
                    .getQualifiedName() : terminalConclusionInfo.getConclusionYear().getQualifiedName());
            bean.setNumInscConclusao(
                    String.valueOf(RegistrationServices.getEnrolmentYearsIncludingPrecedentRegistrations(registration).size()));

            if (Raides.isDoctoralDegree(registration) && !registrationConclusionBean.isConclusionProcessed()) {
                LegalReportContext.addError("", i18n("error.Raides.validation.doctoral.degree.without.conclusion.process",
                        formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            }

            if (registrationConclusionBean.getDescriptiveGrade() != null
                    && !registrationConclusionBean.getDescriptiveGrade().isEmpty() && Raides.isDoctoralDegree(registration)) {
                bean.setClassificacaoFinal(LegalMapping.find(report, LegalMappingType.GRADE)
                        .translate(finalGrade(registrationConclusionBean.getDescriptiveGrade().getValue())));
            } else if (registrationConclusionBean.getFinalGrade().isEmpty()) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.finalGrade.set.but.empty", formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            } else {
                bean.setClassificacaoFinal(LegalMapping.find(report, LegalMappingType.GRADE)
                        .translate(finalGrade(registrationConclusionBean.getFinalGrade().getValue())));
            }

            bean.setDataDiploma(registrationConclusionBean.getConclusionDate().toLocalDate());
            
            if (Raides.isDoctoralDegree(registration)) {
                bean.setAreaInvestigacao(registration.getResearchArea() != null ? registration.getResearchArea().getCode() : "");
            }

        } else {
            bean.setConcluiGrau(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(false));
        }

        if (Raides.isMasterDegreeOrDoctoralDegree(registration)) {

            final RegistrationConclusionInformation scholarPartConclusionInfo =
                    scholarPartConclusionInformation(registration, graduatedPeriod, executionYear);

            if (scholarPartConclusionInfo != null && (RaidesInstance.getInstance().isReportGraduatedWithoutConclusionProcess()
                    || scholarPartConclusionInfo.getRegistrationConclusionBean().isConclusionProcessed())) {

                final RegistrationConclusionBean conclusionBean = scholarPartConclusionInfo.getRegistrationConclusionBean();

                bean.setAnoLectivo(raidesRequestParameter.getGraduatedExecutionYear() != null ? raidesRequestParameter
                        .getGraduatedExecutionYear().getQualifiedName() : conclusionBean.getConclusionYear().getQualifiedName());
                bean.setConclusaoMd(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true));
                bean.setClassificacaoFinalMd(LegalMapping.find(report, LegalMappingType.GRADE)
                        .translate(finalGrade(conclusionBean.getFinalGrade().getValue())));

                if (Strings.isNullOrEmpty(bean.getClassificacaoFinalMd()) || "0".equals(bean.getClassificacaoFinalMd())) {
                    if (Raides.isDoctoralDegree(registration) && conclusionBean.getDescriptiveGrade() != null) {
                        bean.setClassificacaoFinalMd(LegalMapping.find(report, LegalMappingType.GRADE)
                                .translate(finalGrade(conclusionBean.getDescriptiveGrade().getValue())));
                    }
                }

            } else {
                bean.setConclusaoMd(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(false));
            }
        }

        preencheMobilidadeCredito(registration, bean, executionYear);
        preencheGrauPrecedentCompleto(bean, institutionUnit, executionYear, registration);

        validaClassificacao(executionYear, graduatedPeriod, registration, bean);
        validaMobilidadeCredito(executionYear, registration, bean);
        validaAreaInvestigacao(executionYear, registration, bean);

        return bean;
    }

    public TblDiplomado createIntegratedCycleFirstCyle(final RaidesRequestParameter raidesRequestParameter,
            final RaidesRequestPeriodParameter graduatedPeriod, final ExecutionYear executionYear,
            final Registration registration) {

        final TblDiplomado bean = new TblDiplomado();
        bean.setRegistration(registration);

        preencheInformacaoMatricula(report, bean, raidesRequestParameter.getInstitution(), executionYear, registration);

        final RegistrationConclusionInformation scholarPartConclusionInfo =
                scholarPartConclusionInformation(registration, graduatedPeriod, executionYear);
        final RegistrationConclusionBean scholarPartConclusionBean = scholarPartConclusionInfo.getRegistrationConclusionBean();

        bean.setCurso(LegalMapping.find(report, LegalMappingType.INTEGRATED_MASTER_FIRST_CYCLE_CODES)
                .translate(registration.getDegree()));
        bean.setConcluiGrau(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true));
        bean.setAnoLectivo(
                raidesRequestParameter.getGraduatedExecutionYear() != null ? raidesRequestParameter.getGraduatedExecutionYear()
                        .getQualifiedName() : scholarPartConclusionBean.getConclusionYear().getQualifiedName());
        bean.setNumInscConclusao(String.valueOf(RegistrationServices
                .getEnrolmentYearsIncludingPrecedentRegistrations(registration, scholarPartConclusionBean.getConclusionYear())
                .size()));

        bean.setClassificacaoFinal(LegalMapping.find(report, LegalMappingType.GRADE)
                .translate(finalGrade(scholarPartConclusionBean.getFinalGrade().getValue())));

        bean.setDataDiploma(scholarPartConclusionBean.getConclusionDate().toLocalDate());

        /* Override Ramo to report the branch open inside first cycle curriculum group */
        preencheRamo(report, bean, executionYear, registration, true);

        preencheMobilidadeCredito(registration, bean, executionYear);
        preencheGrauPrecedentCompleto(bean, raidesRequestParameter.getInstitution(), executionYear, registration);

        validaClassificacao(executionYear, graduatedPeriod, registration, bean);
        validaMobilidadeCredito(executionYear, registration, bean);

        return bean;
    }

    protected void preencheMobilidadeCredito(final Registration registration, final TblDiplomado bean,
            final ExecutionYear executionYear) {

        bean.setMobilidadeCredito(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(false));

        if (MobilityRegistrationInformation.hasAnyInternationalOutgoingMobilityUntil(registration, executionYear)) {

            final MobilityRegistrationInformation mobility = findOutgoingMobility(registration, executionYear);

            bean.setMobilidadeCredito(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true));
            bean.setTipoMobilidadeCredito(LegalMapping.find(report, LegalMappingType.INTERNATIONAL_MOBILITY_ACTIVITY)
                    .translate(mobility.getMobilityActivityType()));
            bean.setProgMobilidadeCredito(LegalMapping.find(report, LegalMappingType.INTERNATIONAL_MOBILITY_PROGRAM)
                    .translate(mobility.getMobilityProgramType()));

            if (Raides.ProgramaMobilidade.OUTRO_DOIS.equals(bean.getProgMobilidadeCredito())
                    || Raides.ProgramaMobilidade.OUTRO_TRES.equals(bean.getProgMobilidadeCredito())) {
                bean.setOutroProgMobCredito(mobility.getMobilityProgramType().getName().getContent());
            }

            if (mobility.hasCountry()) {
                bean.setPaisMobilidadeCredito(mobility.getCountry().getCode());
            }
        }
    }

    private MobilityRegistrationInformation findOutgoingMobility(Registration registration, ExecutionYear executionYear) {

        final MobilityRegistrationInformation mainInformation =
                MobilityRegistrationInformation.findMainInternationalOutgoingInformationUntil(registration, executionYear);
        if (mainInformation != null) {
            return mainInformation;
        }

        return MobilityRegistrationInformation.findInternationalOutgoingInformationsUntil(registration, executionYear).stream()
                .sorted((x, y) -> x.getExternalId().compareTo(y.getExternalId())).findFirst().orElse(null);
    }

    private String finalGrade(final String value) {
        if (!Strings.isNullOrEmpty(value) && value.matches("\\d+\\.\\d+")) {
            return new BigDecimal(value).setScale(0, RoundingMode.HALF_UP).toString();
        }

        return value;
    }

    private boolean isScholarPartConcluded(final Registration registration, RaidesRequestPeriodParameter graduatedPeriod,
            final ExecutionYear executionYear) {
        final RegistrationConclusionInformation conclusionInfo =
                scholarPartConclusionInformation(registration, graduatedPeriod, executionYear);
        return conclusionInfo != null && (RaidesInstance.getInstance().getReportGraduatedWithoutConclusionProcess()
                || conclusionInfo.getRegistrationConclusionBean().isConclusionProcessed());
    }

    private RegistrationConclusionInformation scholarPartConclusionInformation(final Registration registration,
            RaidesRequestPeriodParameter graduatedPeriod, final ExecutionYear executionYear) {

        final Set<RegistrationConclusionInformation> conclusionInfoSet =
                RegistrationConclusionServices.inferConclusion(registration);

        for (final RegistrationConclusionInformation rci : conclusionInfoSet) {

            if (!rci.isConcluded()) {
                continue;
            }

            if (!rci.isScholarPart()) {
                continue;
            }

            if (rci.getConclusionYear() != executionYear) {
                continue;
            }

            if (!graduatedPeriod.getInterval().contains(rci.getConclusionDate().toDateTimeAtStartOfDay())) {
                continue;
            }

            return rci;
        }

        return null;
    }

    public static boolean isTerminalConcluded(final Registration registration, RaidesRequestPeriodParameter graduatedPeriod,
            final ExecutionYear executionYear) {
        final RegistrationConclusionInformation conclusionInfo =
                terminalConclusionInformation(registration, graduatedPeriod, executionYear);
        return conclusionInfo != null && (RaidesInstance.getInstance().getReportGraduatedWithoutConclusionProcess()
                || conclusionInfo.getRegistrationConclusionBean().isConclusionProcessed());
    }

    private static RegistrationConclusionInformation terminalConclusionInformation(final Registration registration,
            RaidesRequestPeriodParameter graduatedPeriod, final ExecutionYear executionYear) {

        for (final RegistrationConclusionInformation rci : RegistrationConclusionServices.inferConclusion(registration)) {

            if (!rci.isConcluded()) {
                continue;
            }

            if (rci.isScholarPart()) {
                continue;
            }

            if (rci.getConclusionYear() != executionYear) {
                continue;
            }

            if (!graduatedPeriod.getInterval().contains(rci.getConclusionDate().toDateTimeAtStartOfDay())) {
                continue;
            }

            return rci;
        }

        return null;
    }

    protected void validaMobilidadeCredito(final ExecutionYear executionYear, final Registration registration,
            final TblDiplomado bean) {

        if (!MobilityRegistrationInformation.hasAnyInternationalOutgoingMobilityUntil(registration, executionYear)) {
            return;
        }

        if (MobilityRegistrationInformation.findInternationalOutgoingInformationsUntil(registration, executionYear).size() > 1
                && MobilityRegistrationInformation.findMainInternationalOutgoingInformationUntil(registration,
                        executionYear) == null) {
            LegalReportContext.addError("", i18n("error.Raides.validation.graduated.mobility.mainInformation.missing",
                    formatArgs(registration, executionYear)));
        }

        if (Strings.isNullOrEmpty(bean.getTipoMobilidadeCredito())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.graduated.mobility.credit.type.missing",
                    formatArgs(registration, executionYear)));
        }

        if (Strings.isNullOrEmpty(bean.getProgMobilidadeCredito())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.graduated.mobility.program.type.missing",
                    formatArgs(registration, executionYear)));
        }

        if ((Raides.ProgramaMobilidade.OUTRO_DOIS.equals(bean.getProgMobilidadeCredito())
                || Raides.ProgramaMobilidade.OUTRO_TRES.equals(bean.getProgMobilidadeCredito()))
                && Strings.isNullOrEmpty(bean.getOutroProgMobCredito())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.graduated.mobility.other.program.type.missing",
                    formatArgs(registration, executionYear)));
        }

        if (Strings.isNullOrEmpty(bean.getPaisMobilidadeCredito())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.graduated.mobility.country.missing", formatArgs(registration, executionYear)));
        }

    }

    protected void validaClassificacao(final ExecutionYear executionYear, RaidesRequestPeriodParameter graduatedPeriod,
            final Registration registration, final TblDiplomado bean) {

        if (bean.getConclusaoMd() != null
                && bean.getConclusaoMd().equals(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true))) {
            if (Strings.isNullOrEmpty(bean.getClassificacaoFinalMd()) || "0".equals(bean.getClassificacaoFinalMd())) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.masterOrDoctoral.scholarpart.classification.empty.or.zero",
                                formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            }
        }

        if (bean.getConcluiGrau().equals(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true))
                && Strings.isNullOrEmpty(bean.getClassificacaoFinal()) || "0".equals(bean.getClassificacaoFinal())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.masterOrDoctoral.terminalpart.classification.empty.or.zero",
                            formatArgs(registration, executionYear)));
        }
    }

    private void validaAreaInvestigacao(ExecutionYear executionYear, Registration registration, TblDiplomado bean) {
        if (Raides.isDoctoralDegree(registration)
                && bean.getConcluiGrau().equals(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true))
                && Strings.isNullOrEmpty(bean.getAreaInvestigacao())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.doctoral.requires.research.area", formatArgs(registration, executionYear)));
        }
    }

    public boolean isToReportNormal(final RaidesRequestPeriodParameter graduatedPeriod, final ExecutionYear executionYear,
            final Registration registration) {
        return isTerminalConcluded(registration, graduatedPeriod, executionYear)
                || (Raides.isMasterDegreeOrDoctoralDegree(registration)
                        && isScholarPartConcluded(registration, graduatedPeriod, executionYear));
    }

    //TODO: clean logic and remove integrated cycle first cycle conclusion report option
    public boolean isToReportIntegratedCycleFirstCycle(final RaidesRequestPeriodParameter graduatedPeriod,
            final ExecutionYear executionYear, final Registration registration) {

        if (Raides.isIntegratedMasterDegree(registration)
                && isScholarPartConcluded(registration, graduatedPeriod, executionYear)) {

            final RaidesInstance instance = (RaidesInstance) report;

            if (instance.isReportGraduatedWithoutConclusionProcess()
                    && instance.isToReportAllIntegratedMasterFirstCycleGraduatedStudents()) {
                return true;
            }

            if (instance.isToReportIntegratedMasterFirstCycleGraduatedStudentsOnlyWithConclusionProcess()
                    && scholarPartConclusionInformation(registration, graduatedPeriod, executionYear)
                            .getRegistrationConclusionBean().isConclusionProcessed()) {
                return true;
            }

        }

        return false;
    }

}
