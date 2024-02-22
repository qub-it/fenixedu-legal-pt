package org.fenixedu.legalpt.services.raides.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
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
import org.fenixedu.legalpt.domain.raides.RaidesReportEntryTarget;
import org.fenixedu.legalpt.domain.raides.TblDiplomado;
import org.fenixedu.legalpt.domain.raides.mapping.LegalMappingType;
import org.fenixedu.legalpt.domain.raides.report.RaidesRequestParameter;
import org.fenixedu.legalpt.domain.raides.report.RaidesRequestPeriodParameter;
import org.fenixedu.legalpt.domain.report.LegalReport;

import com.google.common.base.Strings;

public class DiplomadoService extends RaidesService {

    private final Integer MAX_SIZE_PROG_MOBILITY = 80;

    protected boolean valid = true;

    public DiplomadoService(final LegalReport report) {
        super(report);
    }

    public TblDiplomado createNormal(final RaidesRequestParameter raidesRequestParameter,
            final RaidesRequestPeriodParameter graduatedPeriod, final ExecutionYear executionYear,
            final Registration registration) {

        final RaidesReportEntryTarget target = RaidesReportEntryTarget.of(registration, executionYear);

        final TblDiplomado bean = new TblDiplomado();
        bean.setRegistration(registration);

        preencheInformacaoMatricula(report, bean, executionYear, registration);

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
                LegalReportContext.addError(target, i18n("error.Raides.validation.doctoral.degree.without.conclusion.process"),
                        i18n("error.Raides.validation.doctoral.degree.without.conclusion.process.action"));
                bean.markAsInvalid();
            }

            if (registrationConclusionBean.getDescriptiveGrade() != null
                    && !registrationConclusionBean.getDescriptiveGrade().isEmpty()
                    && isToReportDescriptiveGrade(registrationConclusionBean)) {

                String value = LegalMapping.find(report, LegalMappingType.GRADE)
                        .translate(finalGrade(registrationConclusionBean.getDescriptiveGrade().getValue()));

                if (StringUtils.isBlank(value)) {

                    LegalReportContext.addError(target,
                            i18n("error.Raides.validation.conclution.descriptiveGrade.missing.translate",
                                    registrationConclusionBean.getDescriptiveGrade().getValue()),
                            i18n("error.Raides.validation.grade.conclution.descriptiveGrade.translate.action",
                                    registrationConclusionBean.getDescriptiveGrade().getValue()));

                    bean.markAsInvalid();

                } else {
                    bean.setClassificacaoFinal(value);
                }

            } else if (registrationConclusionBean.getFinalGrade().isEmpty()) {
                LegalReportContext.addError(target, i18n("error.Raides.validation.finalGrade.set.but.empty"),
                        i18n("error.Raides.validation.finalGrade.set.but.empty.action"));
                bean.markAsInvalid();
            } else {
                
                
                String value = LegalMapping.find(report, LegalMappingType.GRADE)
                        .translate(finalGrade(registrationConclusionBean.getFinalGrade().getValue()));

                if (StringUtils.isBlank(value)) {

                    LegalReportContext.addError(target,
                            i18n("error.Raides.validation.conclution.finalGrade.missing.translate",
                                    registrationConclusionBean.getFinalGrade().getValue()),
                            i18n("error.Raides.validation.grade.finalGrade.finalGrade.translate.action",
                                    registrationConclusionBean.getFinalGrade().getValue()));

                    bean.markAsInvalid();

                } else {
                    bean.setClassificacaoFinal(value);
                }
                
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
        preencheGrauPrecedentCompleto(bean, executionYear, registration);

        validaClassificacao(executionYear, graduatedPeriod, registration, bean);
        validaMobilidadeCredito(executionYear, registration, bean);
        validaAreaInvestigacao(executionYear, registration, bean);

        return bean;
    }

    private boolean isToReportDescriptiveGrade(final RegistrationConclusionBean conclusionBean) {
        if (Raides.isDoctoralDegree(conclusionBean.getRegistration())) {
            return true;
        }

        if (conclusionBean.getFinalGrade().isEmpty()) {
            return true;
        }

        if (conclusionBean.getFinalGrade().isNumeric()
                && conclusionBean.getFinalGrade().getNumericValue().compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }

        return false;
    }

    public TblDiplomado createIntegratedCycleFirstCyle(final RaidesRequestParameter raidesRequestParameter,
            final RaidesRequestPeriodParameter graduatedPeriod, final ExecutionYear executionYear,
            final Registration registration) {

        final TblDiplomado bean = new TblDiplomado();
        bean.setRegistration(registration);

        preencheInformacaoMatricula(report, bean, executionYear, registration);

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
        preencheGrauPrecedentCompleto(bean, executionYear, registration);

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

            if (Raides.ProgramaMobilidade.OUTRO_DOIS.equals(bean.getProgMobilidadeCredito())) {
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

        final RaidesReportEntryTarget target = RaidesReportEntryTarget.of(registration, executionYear);

        if (MobilityRegistrationInformation.findInternationalOutgoingInformationsUntil(registration, executionYear).size() > 1
                && MobilityRegistrationInformation.findMainInternationalOutgoingInformationUntil(registration,
                        executionYear) == null) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.graduated.mobility.mainInformation.missing"),
                    i18n("error.Raides.validation.graduated.mobility.mainInformation.missing.action"));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getTipoMobilidadeCredito())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.graduated.mobility.credit.type.missing"),
                    i18n("error.Raides.validation.graduated.mobility.credit.type.missing.action"));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getProgMobilidadeCredito())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.graduated.mobility.program.type.missing"),
                    i18n("error.Raides.validation.graduated.mobility.program.type.missing.action"));
            bean.markAsInvalid();
        }

        if (Raides.ProgramaMobilidade.OUTRO_DOIS.equals(bean.getProgMobilidadeCredito())
                && Strings.isNullOrEmpty(bean.getOutroProgMobCredito())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.graduated.mobility.other.program.type.missing"),
                    i18n("error.Raides.validation.graduated.mobility.other.program.type.missing.action"));
            bean.markAsInvalid();
        }

        if (Raides.ProgramaMobilidade.OUTRO_DOIS.equals(bean.getProgMobilidadeCredito())
                && Strings.isNullOrEmpty(bean.getOutroProgMobCredito())
                && bean.getOutroProgMobCredito().length() > MAX_SIZE_PROG_MOBILITY) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.graduated.mobility.other.program.type.max.size"),
                    i18n("error.Raides.validation.graduated.mobility.other.program.type.max.size.action"));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getPaisMobilidadeCredito())) {
            LegalReportContext.addError(target, i18n("error.Raides.validation.graduated.mobility.country.missing"),
                    i18n("error.Raides.validation.graduated.mobility.country.missing.action"));
            bean.markAsInvalid();
        }

    }

    protected void validaClassificacao(final ExecutionYear executionYear, RaidesRequestPeriodParameter graduatedPeriod,
            final Registration registration, final TblDiplomado bean) {

        final RaidesReportEntryTarget target = RaidesReportEntryTarget.of(registration, executionYear);

        if (bean.getConclusaoMd() != null
                && bean.getConclusaoMd().equals(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true))) {
            if (Strings.isNullOrEmpty(bean.getClassificacaoFinalMd()) || "0".equals(bean.getClassificacaoFinalMd())) {
                LegalReportContext.addError(target,
                        i18n("error.Raides.validation.masterOrDoctoral.scholarpart.classification.empty.or.zero"),
                        i18n("error.Raides.validation.masterOrDoctoral.scholarpart.classification.empty.or.zero.action"));
                bean.markAsInvalid();
            }
        }

        if (bean.getConcluiGrau().equals(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true))
                && Strings.isNullOrEmpty(bean.getClassificacaoFinal()) || "0".equals(bean.getClassificacaoFinal())) {
            LegalReportContext.addError(target,
                    i18n("error.Raides.validation.masterOrDoctoral.terminalpart.classification.empty.or.zero"),
                    i18n("error.Raides.validation.masterOrDoctoral.terminalpart.classification.empty.or.zero.action"));
            bean.markAsInvalid();
        }
    }

    private void validaAreaInvestigacao(ExecutionYear executionYear, Registration registration, TblDiplomado bean) {
        if (Raides.isDoctoralDegree(registration)
                && bean.getConcluiGrau().equals(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true))
                && Strings.isNullOrEmpty(bean.getAreaInvestigacao())) {
            LegalReportContext.addError(RaidesReportEntryTarget.of(registration, executionYear),
                    i18n("error.Raides.validation.doctoral.requires.research.area"),
                    i18n("error.Raides.validation.doctoral.requires.research.area.action"));
            bean.markAsInvalid();
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
