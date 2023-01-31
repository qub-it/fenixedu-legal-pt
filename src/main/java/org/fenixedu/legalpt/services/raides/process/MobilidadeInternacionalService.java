package org.fenixedu.legalpt.services.raides.process;

import static org.fenixedu.legalpt.domain.raides.Raides.formatArgs;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.domain.student.mobility.MobilityRegistrationInformation;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.mapping.LegalMapping;
import org.fenixedu.legalpt.domain.raides.Raides;
import org.fenixedu.legalpt.domain.raides.Raides.Cursos;
import org.fenixedu.legalpt.domain.raides.TblMobilidadeInternacional;
import org.fenixedu.legalpt.domain.raides.mapping.BranchMappingType;
import org.fenixedu.legalpt.domain.raides.mapping.LegalMappingType;
import org.fenixedu.legalpt.domain.raides.report.RaidesRequestParameter;
import org.fenixedu.legalpt.domain.report.LegalReport;
import org.joda.time.DateTime;

import com.google.common.base.Strings;

public class MobilidadeInternacionalService extends RaidesService {

    public MobilidadeInternacionalService(final LegalReport report) {
        super(report);
    }

    public TblMobilidadeInternacional create(RaidesRequestParameter raidesRequestParameter, final ExecutionYear executionYear,
            final Registration registration) {
        final TblMobilidadeInternacional bean = new TblMobilidadeInternacional();

        bean.setRegistration(registration);
        preencheInformacaoMatricula(report, bean, executionYear, registration);

        bean.setCurso(null);
        bean.setRamo(null);

        bean.setAnoCurricular(
                LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(Raides.AnoCurricular.NAO_APLICAVEL_CODE));

        final DateTime maximumAnnulmentDate =
                findMaximumAnnulmentDate(raidesRequestParameter.getPeriodsForInternationalMobility(), executionYear);
        final BigDecimal enrolledEcts = enrolledEcts(executionYear, registration, maximumAnnulmentDate, true);
        if (enrolledEcts != null && enrolledEcts.compareTo(BigDecimal.ZERO) > 0) {
            //HACK HACK: some institutions declare ECTS that are not multiple of 0.5
            final Double enrollectsEctsAsDouble = enrolledEcts.doubleValue();
            if (enrollectsEctsAsDouble != enrollectsEctsAsDouble.intValue()
                    && !enrollectsEctsAsDouble.toString().endsWith(".5")) {
                bean.setEctsInscrito(String.valueOf(Math.round(enrollectsEctsAsDouble)));
            } else {
                bean.setEctsInscrito(enrolledEcts.toString());
            }
        } else {
            bean.setEctsInscrito(null);
        }

        bean.setRegimeFrequencia(regimeFrequencia(registration, executionYear, true));

        final MobilityRegistrationInformation mobility =
                MobilityRegistrationInformation.findInternationalIncomingInformation(registration, executionYear);

        if (mobility == null) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.mobility.information.is.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();

            return bean;
        }

        if (mobility.getMobilityProgramType() != null) {

            bean.setProgMobilidade(LegalMapping.find(report, LegalMappingType.INTERNATIONAL_MOBILITY_PROGRAM)
                    .translate(mobility.getMobilityProgramType()));

            if (Raides.ProgramaMobilidade.OUTRO_DOIS.equals(bean.getProgMobilidade())) {
                bean.setOutroPrograma(mobility.getMobilityProgramType().getName().getContent());
            }

        } 

        if (mobility.getMobilityActivityType() != null) {
            bean.setTipoProgMobilidade(LegalMapping.find(report, LegalMappingType.INTERNATIONAL_MOBILITY_ACTIVITY)
                    .translate(mobility.getMobilityActivityType()));
        } else {
            bean.setTipoProgMobilidade(Raides.ActividadeMobilidade.MOBILIDADE_ESTUDO);
        }

        if (mobility.getProgramDuration() != null) {
            bean.setDuracaoPrograma(
                    LegalMapping.find(report, LegalMappingType.SCHOOL_PERIOD_DURATION).translate(mobility.getProgramDuration()));
        }

        if (mobility.getOriginMobilityProgrammeLevel() != null) {
            bean.setNivelCursoOrigem(mobility.getOriginMobilityProgrammeLevel().getCode());

            if (mobility.getOriginMobilityProgrammeLevel().isOtherLevel()) {
                bean.setOutroNivelCurOrigem(mobility.getOtherOriginMobilityProgrammeLevel());
            }
        }

        if (mobility.getDegreeBased()) {
            bean.setCurso(mobility.getDegree().getMinistryCode());
            bean.setRamo(mobility.getBranchCourseGroup() != null ? BranchMappingType.readMapping(report)
                    .translate(mobility.getBranchCourseGroup()) : Raides.Ramo.TRONCO_COMUM);
            bean.setAreaCientifica(null);
            bean.setNivelCursoDestino(null);
            bean.setOutroNivelCursoDestino(null);

            if (!Raides.isDoctoralDegree(registration)) {
                bean.setAnoCurricular(LegalMapping.find(report, LegalMappingType.CURRICULAR_YEAR).translate(
                        String.valueOf(RegistrationServices.getCurricularYear(registration, executionYear).getResult())));
            }

        } else {
            bean.setCurso(Raides.Cursos.OUTRO);
            bean.setRamo(Raides.Ramo.OUTRO);

            if (mobility.getMobilityScientificArea() != null) {
                bean.setAreaCientifica(mobility.getMobilityScientificArea().getCode());
            }

            if (mobility.getIncomingMobilityProgrammeLevel() != null) {
                bean.setNivelCursoDestino(mobility.getIncomingMobilityProgrammeLevel().getCode());

                if (mobility.getIncomingMobilityProgrammeLevel().isOtherLevel()) {
                    bean.setOutroNivelCursoDestino(mobility.getOtherIncomingMobilityProgrammeLevel());
                }
            }
        }

        if (mobility.hasCountry()) {
            bean.setPaisOrigemMobilidadeCredito(mobility.getCountry().getCode());
        }

        validaPaisOrigemMobilidadeCredito(executionYear, registration, bean);
        validaProgramaMobilidade(executionYear, registration, bean);
        validaDuracaoPrograma(executionYear, registration, bean);
        validaNivelCursoOrigem(executionYear, registration, bean);
        validaCursoAreaCientificaNivelCursoDestino(executionYear, registration, bean);
        validaRegimeFrequencia(executionYear, registration, bean);

        return bean;
    }

    private void validaProgramaMobilidade(ExecutionYear executionYear, Registration registration,
            TblMobilidadeInternacional bean) {

        if (Strings.isNullOrEmpty(bean.getTipoProgMobilidade()) || Strings.isNullOrEmpty(bean.getProgMobilidade())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.mobility.program.type.empty", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Raides.ProgramaMobilidade.OUTRO_DOIS.equals(bean.getProgMobilidade())
                && Strings.isNullOrEmpty(bean.getOutroPrograma())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.mobility.other.program.type.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

    }

    private void validaDuracaoPrograma(ExecutionYear executionYear, Registration registration, TblMobilidadeInternacional bean) {
        if (Strings.isNullOrEmpty(bean.getDuracaoPrograma())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.mobility.program.duration.empty", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

    }

    protected void validaNivelCursoOrigem(final ExecutionYear executionYear, final Registration registration,
            final TblMobilidadeInternacional bean) {

        if (Strings.isNullOrEmpty(bean.getNivelCursoOrigem())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.mobility.provenance.school.level.empty",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();

        } else if (Raides.NivelCursoOrigem.OUTRO.equals(bean.getNivelCursoOrigem())
                && Strings.isNullOrEmpty(bean.getOutroNivelCurOrigem())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.mobility.other.provenance.school.level.empty",
                    formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }
    }

    private void validaCursoAreaCientificaNivelCursoDestino(ExecutionYear executionYear, Registration registration,
            TblMobilidadeInternacional bean) {

        if (Cursos.OUTRO.equals(bean.getCurso())) {

            if (Strings.isNullOrEmpty(bean.getAreaCientifica())) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.mobility.scientifica.area.cannot.be.empty.for.other.degree",
                                formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            }

            if (Strings.isNullOrEmpty(bean.getNivelCursoDestino())) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.mobility.incoming.mobility.program.level.cannot.be.empty.for.other.degree",
                                formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            }

        }

    }

    private void validaRegimeFrequencia(ExecutionYear executionYear, Registration registration, TblMobilidadeInternacional bean) {
        if (StringUtils.isEmpty(bean.getRegimeFrequencia())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.missing.mapping.for.regime.frequence",
                    registration.getDegree().getPresentationName() + " [" + registration.getDegree().getCode() + "]"));
            bean.markAsInvalid();
        }

    }

    private void validaPaisOrigemMobilidadeCredito(ExecutionYear executionYear, Registration registration,
            TblMobilidadeInternacional bean) {
        if (Strings.isNullOrEmpty(bean.getPaisOrigemMobilidadeCredito())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.mobility.country.empty", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

    }

}
