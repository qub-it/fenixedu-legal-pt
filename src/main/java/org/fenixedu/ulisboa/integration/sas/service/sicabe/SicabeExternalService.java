package org.fenixedu.ulisboa.integration.sas.service.sicabe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.person.identificationDocument.IdentificationDocumentType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilderForXLSX;
import org.fenixedu.ulisboa.integration.sas.domain.CandidacyState;
import org.fenixedu.ulisboa.integration.sas.domain.SasScholarshipCandidacy;
import org.fenixedu.ulisboa.integration.sas.domain.SasScholarshipCandidacyState;
import org.fenixedu.ulisboa.integration.sas.domain.SasScholarshipData;
import org.fenixedu.ulisboa.integration.sas.domain.SasScholarshipDataChangeLog;
import org.fenixedu.ulisboa.integration.sas.dto.AbstractScholarshipStudentBean;
import org.fenixedu.ulisboa.integration.sas.dto.ScholarshipStudentFirstYearBean;
import org.fenixedu.ulisboa.integration.sas.dto.ScholarshipStudentOtherYearBean;
import org.fenixedu.ulisboa.integration.sas.service.process.AbstractFillScholarshipService;
import org.fenixedu.ulisboa.integration.sas.service.process.FillScholarshipException;
import org.fenixedu.ulisboa.integration.sas.service.process.FillScholarshipFirstYearService;
import org.fenixedu.ulisboa.integration.sas.service.process.FillScholarshipServiceOtherYearService;
import org.fenixedu.ulisboa.integration.sas.util.SasPTUtil;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.base.Objects;
import com.qubit.solution.fenixedu.bennu.webservices.services.client.BennuWebServiceClient;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

import pt.dges.schemas.data.sicabe.v1.AlterarCursoInsituicaoRequest;
import pt.dges.schemas.data.sicabe.v1.AlterarDadosAcademicosPrimeiraVezRequest;
import pt.dges.schemas.data.sicabe.v1.AlterarDadosAcademicosRestantesCasosRequest;
import pt.dges.schemas.data.sicabe.v1.CandidaturaSubmetida;
import pt.dges.schemas.data.sicabe.v1.IdentificadorCandidatura;
import pt.dges.schemas.data.sicabe.v1.ObjectFactory;
import pt.dges.schemas.data.sicabe.v1.ObterCandidaturasSubmetidasRequest;
import pt.dges.schemas.data.sicabe.v1.ObterCandidaturasSubmetidasResponse;
import pt.dges.schemas.data.sicabe.v1.ObterEstadoCandidaturaRequest;
import pt.dges.schemas.data.sicabe.v1.RegistarMatriculaAlunoRequest;
import pt.dges.schemas.data.sicabe.v1.ResultadoEstadoCandidatura;
import pt.dges.schemas.data.sicabe.v1.TipoDocumentoIdentificacao;
import pt.dges.schemas.data.sicabe.v1.TipoRegime;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicos;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicosAlterarDadosAcademicosPrimeiraVezSicabeBusinessMessageFaultFaultMessage;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicosAlterarDadosAcademicosPrimeiraVezSicabeErrorMessageFaultFaultMessage;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicosAlterarDadosAcademicosPrimeiraVezSicabeValidationMessageFaultFaultMessage;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicosAlterarDadosAcademicosRestantesCasosSicabeBusinessMessageFaultFaultMessage;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicosAlterarDadosAcademicosRestantesCasosSicabeErrorMessageFaultFaultMessage;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicosAlterarDadosAcademicosRestantesCasosSicabeValidationMessageFaultFaultMessage;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicosObterCandidaturasSubmetidasSicabeBusinessMessageFaultFaultMessage;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicosObterCandidaturasSubmetidasSicabeErrorMessageFaultFaultMessage;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicosObterCandidaturasSubmetidasSicabeValidationMessageFaultFaultMessage;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicos_Service;
import pt.ist.fenixframework.Atomic;

public class SicabeExternalService extends BennuWebServiceClient<DadosAcademicos> {

    public SicabeExternalService() {
    }

    @Atomic
    public static void init() {
        new SicabeExternalService();
    }

    @Override
    protected BindingProvider getService() {
        return (BindingProvider) new DadosAcademicos_Service().getCustomBindingDadosAcademicos();
    }

    @Atomic
    public void removeAllCandidaciesWithoutRegistrationAndLoadAllSasCandidacies(ExecutionYear executionYear)
            throws ServerSOAPFaultException, DadosAcademicosObterCandidaturasSubmetidasSicabeBusinessMessageFaultFaultMessage,
            DadosAcademicosObterCandidaturasSubmetidasSicabeErrorMessageFaultFaultMessage,
            DadosAcademicosObterCandidaturasSubmetidasSicabeValidationMessageFaultFaultMessage {

        removeAllCandidaciesWithoutRegistration(executionYear);
        loadAllSasScholarshipCandidacies(executionYear);
    }

    private void removeAllCandidaciesWithoutRegistration(ExecutionYear executionYear) {
        SasScholarshipCandidacy.findAll().stream()
                .filter(c -> c.getExecutionYear() == executionYear && c.getRegistration() == null)
                .forEach(c -> removeSasScholarshipsCandidacy(c));

    }

    @Atomic
    private void loadAllSasScholarshipCandidacies(ExecutionYear executionYear)
            throws DadosAcademicosObterCandidaturasSubmetidasSicabeBusinessMessageFaultFaultMessage,
            DadosAcademicosObterCandidaturasSubmetidasSicabeErrorMessageFaultFaultMessage,
            DadosAcademicosObterCandidaturasSubmetidasSicabeValidationMessageFaultFaultMessage, ServerSOAPFaultException {
        final ObterCandidaturasSubmetidasRequest parameters = new ObterCandidaturasSubmetidasRequest();
        parameters.setAnoLectivo(executionYear.getAcademicInterval().getStart().getYear());

        final ObterCandidaturasSubmetidasResponse obterCandidaturasSubmetidas =
                getClient().obterCandidaturasSubmetidas(parameters);
        obterCandidaturasSubmetidas.getCandidaturas().getCandidaturaSubmetida().stream()
                .filter(c -> Bennu.getInstance().getSocialServicesConfiguration().getInstitutionCodes()
                        .contains(String.valueOf(c.getCodigoInstituicaoEnsino())))
                .forEach(input -> updateOrCreateSasScholarshipCandidacy(input, executionYear));
    }

    private void updateOrCreateSasScholarshipCandidacy(CandidaturaSubmetida input, ExecutionYear executionYear) {

        SasScholarshipCandidacy candidacy = SasScholarshipCandidacy.findAll().stream()
                .filter(c -> c.getExecutionYear() == executionYear && (c.getFiscalNumber().equalsIgnoreCase(input.getNif())
                        || (c.getDocIdType().equalsIgnoreCase(input.getTipoDocumentoIdentificacao().name())) && c.getDocIdNumber()
                        .equalsIgnoreCase(input.getNumeroDocumentoIdentificacao())) && c.getDegreeCode()
                        .equals(input.getCodigoCurso())).findFirst().orElse(null);

        if (candidacy == null) {

            candidacy = new SasScholarshipCandidacy();
            fillCandidacyInfos(input, executionYear, true, candidacy);

        } else if (!equalsDataBetweenCandidacyAndInput(candidacy, input)) {

            fillCandidacyInfos(input, executionYear, false, candidacy);

        }

    }

    private boolean equalsDataBetweenCandidacyAndInput(SasScholarshipCandidacy candidacy, CandidaturaSubmetida input) {

        if (Objects.equal(candidacy.getDegreeCode(), input.getCodigoCurso()) && Objects.equal(candidacy.getInstitutionCode(),
                input.getCodigoInstituicaoEnsino()) && Objects.equal(candidacy.getInstitutionName(), input.getInstituicaoEnsino())
                && Objects.equal(candidacy.getDegreeName(), input.getCurso()) && Objects.equal(candidacy.getFiscalNumber(),
                input.getNif()) &&

                Objects.equal((candidacy.getSubmissionDate() != null ? candidacy.getSubmissionDate().toString() : null),
                        (input.getDataSubmissao() != null ? new DateTime(
                                input.getDataSubmissao().toGregorianCalendar().getTime()).toString() : null)) &&

                Objects.equal(candidacy.getTechnicianEmail(), input.getEmailTecnico().getValue()) &&

                Objects.equal((candidacy.getAssignmentDate() != null ? candidacy.getAssignmentDate().toString() : null),
                        (input.getEstadoCandidatura().getDataAtribuicao() != null ? new DateTime(
                                input.getEstadoCandidatura().getDataAtribuicao().toGregorianCalendar()
                                        .getTime()).toString() : null)) &&

                Objects.equal(candidacy.getDescription(), input.getEstadoCandidatura().getDescricao().getValue())
                && Objects.equal(candidacy.getGratuityAmount(), input.getEstadoCandidatura().getValorBolsa()) &&

                Objects.equal(candidacy.getCandidacyName(), input.getNomeCandidato().getValue()) && Objects.equal(
                candidacy.getTechnicianName(), input.getNomeTecnico().getValue()) && Objects.equal(candidacy.getStudentNumber(),
                input.getNumeroAluno().getValue()) && Objects.equal(candidacy.getCandidacyNumber(), input.getNumeroCandidatura())
                && Objects.equal(candidacy.getDocIdNumber(), input.getNumeroDocumentoIdentificacao()) && Objects.equal(
                candidacy.getDocIdType(), input.getTipoDocumentoIdentificacao().name()) &&

                Objects.equal(candidacy.getCetQualificationOwner(), input.getTitularidade().getValue().isTitularCET())
                && Objects.equal(candidacy.getCstpQualificationOwner(), input.getTitularidade().getValue().isTitularCSTP())
                && Objects.equal(candidacy.getPhdQualificationOwner(), input.getTitularidade().getValue().isTitularDoutoramento())
                && Objects.equal(candidacy.getDegreeQualificationOwner(),
                input.getTitularidade().getValue().isTitularLicenciatura()) && Objects.equal(
                candidacy.getMasterQualificationOwner(), input.getTitularidade().getValue().isTitularMestrado())

        ) {
            return true;
        }

        return false;

    }

    private void fillCandidacyInfos(CandidaturaSubmetida input, ExecutionYear executionYear, boolean isNewCandidacy,
            SasScholarshipCandidacy candidacy) {

        boolean forceUpdateRegistration =
                (isNewCandidacy || candidacy.getDegreeCode().equals(input.getCodigoCurso())) ? false : true;

        candidacy.setDegreeCode(input.getCodigoCurso());
        candidacy.setInstitutionCode(input.getCodigoInstituicaoEnsino());
        candidacy.setDegreeName(input.getCurso());
        candidacy.setSubmissionDate(
                input.getDataSubmissao() != null ? new DateTime(input.getDataSubmissao().toGregorianCalendar().getTime()) : null);
        candidacy.setTechnicianEmail(input.getEmailTecnico().getValue());

        candidacy.setAssignmentDate(input.getEstadoCandidatura().getDataAtribuicao() != null ? new DateTime(
                input.getEstadoCandidatura().getDataAtribuicao().toGregorianCalendar().getTime()) : null);
        candidacy.setDescription(input.getEstadoCandidatura().getDescricao().getValue());
        candidacy.setCandidacyState(convertCandidacyState(input.getEstadoCandidatura().getResultadoEstadoCandidatura()));
        candidacy.setGratuityAmount(input.getEstadoCandidatura().getValorBolsa());

        candidacy.setInstitutionName(input.getInstituicaoEnsino());
        candidacy.setFiscalNumber(input.getNif());
        candidacy.setCandidacyName(input.getNomeCandidato().getValue());
        candidacy.setTechnicianName(input.getNomeTecnico().getValue());

        //only update student number if it's connected to registration, otherwise registration number takes precedence
        if (candidacy.getRegistration() == null) {
            candidacy.setStudentNumber(input.getNumeroAluno().getValue());
        }

        candidacy.setCandidacyNumber(input.getNumeroCandidatura());
        candidacy.setDocIdNumber(input.getNumeroDocumentoIdentificacao());
        candidacy.setDocIdType(input.getTipoDocumentoIdentificacao().name());

        // Ownership
        candidacy.setCetQualificationOwner(input.getTitularidade().getValue().isTitularCET());
        candidacy.setCstpQualificationOwner(input.getTitularidade().getValue().isTitularCSTP());
        candidacy.setPhdQualificationOwner(input.getTitularidade().getValue().isTitularDoutoramento());

        candidacy.setDegreeQualificationOwner(input.getTitularidade().getValue().isTitularLicenciatura());
        candidacy.setMasterQualificationOwner(input.getTitularidade().getValue().isTitularMestrado());

        if (executionYear == null) {
            throw new DomainException("error.SicabeExternalService.ExecutionYear.not.found");
        }

        if (isNewCandidacy) {
            executionYear.addSasScholarshipCandidacies(candidacy);
            candidacy.changeState(SasScholarshipCandidacyState.PENDING);
        }

        createLogEntry(candidacy, isNewCandidacy ? BundleUtil.getString(AbstractFillScholarshipService.SAS_BUNDLE,
                "message.fillCandidacyInfos.new") : BundleUtil.getString(AbstractFillScholarshipService.SAS_BUNDLE,
                "message.fillCandidacyInfos.update"), candidacy.getStateDate(), false, false);

        if (candidacy.getRegistration() == null || forceUpdateRegistration) {
            associateRegistrationToCandidacy(candidacy);
        }

        candidacy.setImportDate(candidacy.getStateDate());

    }

    private void associateRegistrationToCandidacy(SasScholarshipCandidacy c) {

        final AbstractFillScholarshipService service = new FillScholarshipFirstYearService();

        AbstractScholarshipStudentBean tempBean = new ScholarshipStudentFirstYearBean();
        tempBean.setStudentNumber(c.getStudentNumber() != null && NumberUtils.isNumber(c.getStudentNumber()) ? Integer.valueOf(
                c.getStudentNumber()) : null);
        tempBean.setFiscalCode(c.getFiscalNumber());
        tempBean.setStudentName(c.getCandidacyName());
        tempBean.setDegreeCode(c.getDegreeCode() != null ? c.getDegreeCode().trim() : null);
        tempBean.setDocumentNumber(c.getDocIdNumber());
        String candidacyDocumentTypeCode = convertCandidacyDocumentType(c.getDocIdType());
        tempBean.setDocumentTypeName(candidacyDocumentTypeCode);

        try {
            final Registration registration =
                    service.getRegistrationByAbstractScholarshipStudentBean(tempBean, c.getExecutionYear());

            if (registration != null) {
                c.setRegistration(registration);
                c.setFirstYear(AbstractFillScholarshipService.isFirstTimeInCycle(registration, c.getExecutionYear()));
            }

        } catch (FillScholarshipException e) {
            c.changeState(SasScholarshipCandidacyState.PROCESSED_ERRORS);
        }

        writeCandidacyLogs(c, service, tempBean);
    }

    private void writeCandidacyLogs(SasScholarshipCandidacy c, final AbstractFillScholarshipService service,
            AbstractScholarshipStudentBean tempBean) {

        final String publicMessages = service.getMessages(tempBean, true);
        if (StringUtils.isNotEmpty(publicMessages)) {
            createLogEntry(c, publicMessages, c.getStateDate().plus(1), true, false);
        }

        final String internalMessages = service.getMessages(tempBean, false);
        if (StringUtils.isNotEmpty(internalMessages)) {
            createLogEntry(c, internalMessages, c.getStateDate().plus(1), false, false);
        }
    }

    protected TipoRegime convertRegimeCandidacy(String regime) {

        if (regime.equals(AbstractFillScholarshipService.REGIME_FULL_TIME)) {
            return TipoRegime.TEMPO_INTEGRAL;
        } else if (regime.equals(AbstractFillScholarshipService.REGIME_FULL_TIME_WORKING_STUDENT)) {
            return TipoRegime.TRABALHADOR_ESTUDANTE_TEMPO_INTEGRAL;
        } else if (regime.equals(AbstractFillScholarshipService.REGIME_PARTIAL_TIME)) {
            return TipoRegime.TEMPO_PARCIAL;
        } else if (regime.equals(AbstractFillScholarshipService.REGIME_PROFESSIONAL_INTERNSHIP)) {
            return TipoRegime.ESTAGIO_PROFISSIONAL;
        } else if (regime.equals(AbstractFillScholarshipService.REGIME_PARTIAL_TIME_WORKING_STUDENT)) {
            return TipoRegime.TRABALHADOR_ESTUDANTE_TEMPO_PARCIAL;
        }

        return null;
    }

    protected String convertCandidacyDocumentType(String idDocumentType) {
        if (idDocumentType.equalsIgnoreCase(TipoDocumentoIdentificacao.BI.name())) {
            return IdentificationDocumentType.IDENTITY_CARD_CODE;
        } else if (idDocumentType.equalsIgnoreCase(TipoDocumentoIdentificacao.PASSAPORTE.name())) {
            return IdentificationDocumentType.PASSPORT_CODE;
        } else if (idDocumentType.equalsIgnoreCase(TipoDocumentoIdentificacao.AUTORIZACAO_RESIDENCIA.name())) {
            return IdentificationDocumentType.RESIDENCE_AUTHORIZATION_CODE;
        } else if (idDocumentType.equalsIgnoreCase(TipoDocumentoIdentificacao.BI_NAO_NACIONAL.name())) {
            return IdentificationDocumentType.NATIVE_COUNTRY_IDENTITY_CARD_CODE;
        } else if (idDocumentType.equalsIgnoreCase(TipoDocumentoIdentificacao.OUTROS.name())) {
            return IdentificationDocumentType.OTHER_CODE;
        }

        return null;
    }

    protected CandidacyState convertCandidacyState(ResultadoEstadoCandidatura candidacyState) {
        if (candidacyState == ResultadoEstadoCandidatura.INDEFERIDA) {
            return CandidacyState.DISMISSED;
        } else if (candidacyState == ResultadoEstadoCandidatura.DEFERIDA) {
            return CandidacyState.DEFERRED;
        } else if (candidacyState == ResultadoEstadoCandidatura.NAO_DETERMINADO) {
            return CandidacyState.UNDEFINED;
        }

        return null;
    }

    @Atomic
    public void processAllSasScholarshipCandidacies(ExecutionYear executionYear) {

        SasScholarshipCandidacy.findAll().stream().filter(c -> c.getExecutionYear() == executionYear)
                .forEach(c -> fillCandidacyData(c));
    }

    @Atomic
    public void processAllSasScholarshipCandidacies(final ExecutionYear executionYear, final Collection<Degree> allowedDegrees) {
        executionYear.getSasScholarshipCandidaciesSet().stream()
                .filter(c -> allowedDegrees.stream().anyMatch(d -> d.getMinistryCode().equals(c.getDegreeCode())))
                .forEach(c -> fillCandidacyData(c));
    }

    @Atomic
    public void processSasScholarshipCandidacies(List<SasScholarshipCandidacy> list2Process) {
        list2Process.stream().forEach(c -> fillCandidacyData(c));
    }

    private void fillCandidacyData(SasScholarshipCandidacy c) {
        final AbstractFillScholarshipService service;

        final AbstractScholarshipStudentBean bean;

        if (c.getFirstYear() == null) {
            c.changeState(SasScholarshipCandidacyState.PROCESSED_ERRORS);
            return;
        }

        if (c.getFirstYear()) {
            bean = new ScholarshipStudentFirstYearBean();
            service = new FillScholarshipFirstYearService();
        } else {
            bean = new ScholarshipStudentOtherYearBean();
            service = new FillScholarshipServiceOtherYearService();
        }

        if (c.getRegistration() == null) {
            return;
        }

        // commons bean fields
        bean.setStudentNumber(c.getStudentNumber() != null && NumberUtils.isNumber(c.getStudentNumber()) ? Integer.valueOf(
                c.getStudentNumber()) : null);
        bean.setGratuityAmount(c.getGratuityAmount());
        bean.setCetQualificationOwner(c.getCetQualificationOwner());
        bean.setCtspQualificationOwner(c.getCetQualificationOwner());
        bean.setDegreeQualificationOwner(c.getDegreeQualificationOwner());
        bean.setMasterQualificationOwner(c.getMasterQualificationOwner());
        bean.setPhdQualificationOwner(c.getPhdQualificationOwner());
        bean.setFiscalCode(c.getFiscalNumber());
        bean.setInstitutionCode(c.getInstitutionCode() != null ? String.valueOf(c.getInstitutionCode()) : null);
        bean.setInstitutionName(c.getInstitutionName());
        bean.setCandidacyNumber(c.getCandidacyNumber() != null ? String.valueOf(c.getCandidacyNumber()) : null);
        bean.setStudentName(c.getCandidacyName());
        bean.setDegreeCode(c.getDegreeCode());
        bean.setDegreeName(c.getDegreeName());
        bean.setDocumentNumber(c.getDocIdNumber());
        String candidacyDocumentTypeCode = convertCandidacyDocumentType(c.getDocIdType());
        bean.setDocumentTypeName(candidacyDocumentTypeCode);

        service.fillBeanWithAcademicInfos(bean, c.getRegistration(), c.getExecutionYear(), c.getFirstYear());

        if (c.getSasScholarshipData() == null || dataHasChanged(c.getSasScholarshipData(), bean, service)) {

            // check if registration has changed state to canceled or inactive
            if (c.getSasScholarshipData() != null) {
                service.addWarningIfRegistrationChangedToInactive(bean, c);

                writeCandidacyLogs(c, service, bean);

            }

            updateSasSchoolarshipCandidacyData(bean, c);
        }

    }

    private boolean dataHasChanged(SasScholarshipData sasScholarshipData, AbstractScholarshipStudentBean bean,
            AbstractFillScholarshipService service) {

        boolean value = !service.equal(bean, bean.getCetQualificationOwner(), sasScholarshipData.getCetQualificationOwner(),
                "cetQualificationOwner")

                || !service.equal(bean, bean.getCtspQualificationOwner(), sasScholarshipData.getCtspQualificationOwner(),
                "ctspQualificationOwner")

                || !service.equal(bean, bean.getPhdQualificationOwner(), sasScholarshipData.getPhdQualificationOwner(),
                "phdQualificationOwner")

                || !service.equal(bean, bean.getDegreeQualificationOwner(), sasScholarshipData.getDegreeQualificationOwner(),
                "degreeQualificationOwner")

                || !service.equal(bean, bean.getMasterQualificationOwner(), sasScholarshipData.getMasterQualificationOwner(),
                "masterQualificationOwner")

                || !service.equal(bean, bean.getFirstMonthExecutionYear(), sasScholarshipData.getFirstMonthExecutionYear(),
                "firstMonthExecutionYear")

                || !service.equal(bean, bean.getGratuityAmount(), sasScholarshipData.getGratuityAmount(), "gratuityAmount")

                || !service.equal(bean, bean.getCycleNumberOfEnrolmentsYears(), sasScholarshipData.getNumberOfEnrolmentsYears(),
                "numberOfEnrolmentsYears")

                || !service.equal(bean, bean.getNumberOfEnrolledECTS(), sasScholarshipData.getNumberOfEnrolledECTS(),
                "numberOfEnrolledECTS")

                || !service.equal(bean, bean.getNumberOfMonthsExecutionYear(),
                sasScholarshipData.getNumberOfMonthsExecutionYear(), "numberOfMonthsExecutionYear")

                || !service.equal(bean, bean.getCurricularYear(), sasScholarshipData.getCurricularYear(), "curricularYear")

                || !service.equal(bean, bean.getRegime(), sasScholarshipData.getRegime(), "regime")

                || !service.equal(bean, bean.getEnroled(), sasScholarshipData.getEnroled(), "enroled")

                || !service.equal(bean, bean.getNumberOfDegreeCurricularYears(),
                sasScholarshipData.getNumberOfDegreeCurricularYears(), "numberOfDegreeCurricularYears")

                || !service.equal(bean, bean.getEnrolmentDate(), sasScholarshipData.getEnrolmentDate(), "enrolmentDate")

                || !service.equal(bean, bean.getIngressionRegimeCode(), sasScholarshipData.getIngressionRegime(),
                "ingressionRegime");

        if (bean instanceof ScholarshipStudentOtherYearBean) {

            final ScholarshipStudentOtherYearBean otherYearBean = (ScholarshipStudentOtherYearBean) bean;

            value |= !service.equal(otherYearBean, otherYearBean.getNumberOfApprovedEcts(),
                    sasScholarshipData.getNumberOfApprovedEcts(), "numberOfApprovedEcts")

                    || !service.equal(otherYearBean, otherYearBean.getNumberOfApprovedEctsLastYear(),
                    sasScholarshipData.getNumberOfApprovedEctsLastYear(), "numberOfApprovedEctsLastYear")

                    || !service.equal(otherYearBean, otherYearBean.getNumberOfEnrolledEctsLastYear(),
                    sasScholarshipData.getNumberOfEnrolledEctsLastYear(), "numberOfEnrolledEctsLastYear")

                    || !service.equal(otherYearBean, otherYearBean.getNumberOfDegreeChanges(),
                    sasScholarshipData.getNumberOfDegreeChanges(), "numberOfDegreeChanges")

                    || !service.equal(otherYearBean, otherYearBean.getHasMadeDegreeChangeOnCurrentYear(),
                    sasScholarshipData.getHasMadeDegreeChangeOnCurrentYear(), "hasMadeDegreeChangeOnCurrentYear")

                    || !service.equal(otherYearBean,
                    otherYearBean.getLastEnrolmentYear() != null ? String.valueOf(otherYearBean.getLastEnrolmentYear()) : "",
                    sasScholarshipData.getLastEnrolmentYear(), "lastEnrolmentYear")

                    || !service.equal(otherYearBean, otherYearBean.getLastAcademicActDateLastYear(),
                    sasScholarshipData.getLastAcademicActDateLastYear(), "lastAcademicActDateLastYear")

                    || !service.equal(otherYearBean, otherYearBean.getCycleNumberOfEnrolmentsYearsInIntegralRegime(),
                    sasScholarshipData.getCycleNumberOfEnrolmentsYearsInIntegralRegime(),
                    "cycleNumberOfEnrolmentsYearsInIntegralRegime")

                    || !service.equal(otherYearBean, bean.getCycleIngressionYear(), sasScholarshipData.getCycleIngressionYear(),
                    "cycleIngressionYear");
        }

        return value;
    }

    private void updateSasSchoolarshipCandidacyData(AbstractScholarshipStudentBean bean, SasScholarshipCandidacy candidacy) {

        boolean isNewData = false;

        SasScholarshipData data;
        if (candidacy.getSasScholarshipData() == null) {
            data = new SasScholarshipData();
            candidacy.setSasScholarshipData(data);

            isNewData = true;
        } else {
            data = candidacy.getSasScholarshipData();
        }

        candidacy.setStudentNumber(bean.getStudentNumber() != null ? String.valueOf(bean.getStudentNumber()) : "");

        data.setGratuityAmount(bean.getGratuityAmount());
        data.setNumberOfMonthsExecutionYear(bean.getNumberOfMonthsExecutionYear());
        data.setFirstMonthExecutionYear(bean.getFirstMonthExecutionYear());
        data.setIngressionRegime(bean.getIngressionRegimeCode());
        data.setRegime(bean.getRegime());
        data.setEnroled(bean.getEnroled());
        data.setEnrolmentDate(bean.getEnrolmentDate());
        data.setNumberOfEnrolledECTS(bean.getNumberOfEnrolledECTS());

        data.setCetQualificationOwner(bean.getCetQualificationOwner());
        data.setCtspQualificationOwner(bean.getCtspQualificationOwner());
        data.setDegreeQualificationOwner(bean.getDegreeQualificationOwner());
        data.setMasterQualificationOwner(bean.getMasterQualificationOwner());
        data.setPhdQualificationOwner(bean.getPhdQualificationOwner());

        data.setCycleIngressionYear(bean.getCycleIngressionYear());
        data.setNumberOfEnrolmentsYears(bean.getCycleNumberOfEnrolmentsYears());
        data.setNumberOfDegreeCurricularYears(bean.getNumberOfDegreeCurricularYears());
        data.setObservations(bean.getObservations());
        data.setCurricularYear(bean.getCurricularYear());

        if (bean instanceof ScholarshipStudentOtherYearBean) {

            final ScholarshipStudentOtherYearBean otherYearBean = (ScholarshipStudentOtherYearBean) bean;

            data.setNumberOfDegreeChanges(otherYearBean.getNumberOfDegreeChanges());
            data.setHasMadeDegreeChangeOnCurrentYear(otherYearBean.getHasMadeDegreeChangeOnCurrentYear());

            data.setNumberOfEnrolledEctsLastYear(otherYearBean.getNumberOfEnrolledEctsLastYear());
            data.setNumberOfApprovedEctsLastYear(otherYearBean.getNumberOfApprovedEctsLastYear());
            data.setLastEnrolmentYear(String.valueOf(otherYearBean.getLastEnrolmentYear()));
            data.setLastAcademicActDateLastYear(otherYearBean.getLastAcademicActDateLastYear());
            data.setCycleNumberOfEnrolmentsYearsInIntegralRegime(otherYearBean.getCycleNumberOfEnrolmentsYearsInIntegralRegime());
            data.setNumberOfApprovedEcts(otherYearBean.getNumberOfApprovedEcts());

        }

        if (data.getObservations().contains(AbstractFillScholarshipService.ERROR_OBSERVATION)) {
            candidacy.changeState(SasScholarshipCandidacyState.PROCESSED_ERRORS);
        } else if (data.getObservations().contains(AbstractFillScholarshipService.WARNING_OBSERVATION)) {
            candidacy.changeState(SasScholarshipCandidacyState.PROCESSED_WARNINGS);
        } else {
            candidacy.changeState(SasScholarshipCandidacyState.PROCESSED);
        }

        createLogEntry(candidacy,
                StringUtils.isNotEmpty(data.getObservations()) ? data.getObservations() : isNewData ? BundleUtil.getString(
                        AbstractFillScholarshipService.SAS_BUNDLE, "message.updateSasSchoolarshipCandidacyData.new") :
                        BundleUtil.getString(AbstractFillScholarshipService.SAS_BUNDLE,
                                "message.updateSasSchoolarshipCandidacyData.update") + (StringUtils.isNotEmpty(
                                data.getObservations()) ? data.getObservations() : ""), candidacy.getStateDate(), false, false);
    }

    public void sendSasScholarshipsCandidaciesToSicabe(Collection<SasScholarshipCandidacy> list2Process) {
        sendSasScholarshipsCandidaciesToSicabe(list2Process, false);
    }

    public void sendSasScholarshipsCandidaciesToSicabe(Collection<SasScholarshipCandidacy> list2Process, boolean forceSend) {

        boolean errors = false;
        for (final SasScholarshipCandidacy candidacy : list2Process) {
            try {
                sendCandidacyToSicabe(candidacy, forceSend);
            } catch (Throwable e) {
                errors = true;
                createLogEntry(candidacy, e.getMessage(), new DateTime(), false, false);
            }
        }

        if (errors) {
            throw new RuntimeException(SasPTUtil.bundle("label.error.send"));
        }
    }

    @Atomic
    private void sendCandidacyToSicabe(SasScholarshipCandidacy c, boolean forceSend) {

        if (!forceSend && !stateAllowToSendCandicacy(c.getState())) {
            throw new RuntimeException(
                    BundleUtil.getString(AbstractFillScholarshipService.SAS_BUNDLE, "message.error.sendCandidacy2Sicabe",
                            c.getState() != null ? c.getState().getLocalizedName() : "-"));
        }

        try {
            if (c.getFirstYear()) {
                sendFirstTimeAcademicData(c);
            } else {
                sendOtherAcademicData(c);
            }

            c.changeState(SasScholarshipCandidacyState.SENT);
            final DateTime exportDate = new DateTime();
            c.setExportDate(exportDate);
            createLogEntry(c,
                    BundleUtil.getString(AbstractFillScholarshipService.SAS_BUNDLE, "message.success.sendCandidacy2Sicabe"),
                    exportDate, false, true);
        } catch (DadosAcademicosAlterarDadosAcademicosPrimeiraVezSicabeBusinessMessageFaultFaultMessage |
                DadosAcademicosAlterarDadosAcademicosPrimeiraVezSicabeErrorMessageFaultFaultMessage |
                DadosAcademicosAlterarDadosAcademicosPrimeiraVezSicabeValidationMessageFaultFaultMessage |
                DadosAcademicosAlterarDadosAcademicosRestantesCasosSicabeBusinessMessageFaultFaultMessage |
                DadosAcademicosAlterarDadosAcademicosRestantesCasosSicabeErrorMessageFaultFaultMessage |
                DadosAcademicosAlterarDadosAcademicosRestantesCasosSicabeValidationMessageFaultFaultMessage e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean stateAllowToSendCandicacy(SasScholarshipCandidacyState state) {
        return state == SasScholarshipCandidacyState.PROCESSED || state == SasScholarshipCandidacyState.PROCESSED_WARNINGS;
    }

    @Atomic
    public boolean removeAllSasScholarshipsCandidacies(ExecutionYear executionYear) {
        final List<SasScholarshipCandidacy> candidacies2Remove =
                SasScholarshipCandidacy.findAll().stream().filter(c -> c.getExecutionYear() == executionYear)
                        .collect(Collectors.toList());

        boolean warningMessage = false;
        for (SasScholarshipCandidacy current : candidacies2Remove) {
            try {
                removeSasScholarshipsCandidacy(current);
            } catch (FillScholarshipException e) {
                warningMessage = true;
            }
        }

        return warningMessage;
    }

    @Atomic
    public void removeSasScholarshipsCandidacy(SasScholarshipCandidacy c) {
        if (c.getExportDate() == null) {
            c.delete();
        } else {
            throw new FillScholarshipException("label.error.delete.already.sent");
        }
    }

    private void sendFirstTimeAcademicData(SasScholarshipCandidacy candidacy)
            throws DadosAcademicosAlterarDadosAcademicosPrimeiraVezSicabeBusinessMessageFaultFaultMessage,
            DadosAcademicosAlterarDadosAcademicosPrimeiraVezSicabeErrorMessageFaultFaultMessage,
            DadosAcademicosAlterarDadosAcademicosPrimeiraVezSicabeValidationMessageFaultFaultMessage {

        final SasScholarshipData data = candidacy.getSasScholarshipData();

        AlterarDadosAcademicosPrimeiraVezRequest request = new AlterarDadosAcademicosPrimeiraVezRequest();

        request.setAnoInscricaoCurso(data.getCycleIngressionYear());
        request.setCodigoCurso(data.getSasScholarshipCandidacy().getDegreeCode());
        request.setCodigoInstituicaoEnsino(candidacy.getInstitutionCode());

        request.setDataInscricaoAnoLectivo(createXMLGregorianCalendar(data.getEnrolmentDate()));

        IdentificadorCandidatura idCandidatura =
                createIdentificadorCandidaturaData(candidacy.getExecutionYear().getAcademicInterval().getStart().getYear(),
                        candidacy.getDocIdNumber(), candidacy.getDocIdType(), candidacy.getFiscalNumber());
        request.setIdentificadorCandidatura(idCandidatura);

        request.setIInscritoAnoLectivoActual(data.getEnroled());
        request.setMesPrimeiroPagamento(data.getFirstMonthExecutionYear());

        request.setNumeroAluno(candidacy.getStudentNumber());
        request.setNumeroAnosCurso(data.getNumberOfDegreeCurricularYears());
        request.setNumeroECTSActualInscrito(data.getNumberOfEnrolledECTS());
        request.setNumeroMatriculas(1);
        request.setNumeroMesesPropina(data.getNumberOfMonthsExecutionYear());

        final String observationToSend = getObservationToSend(candidacy);
        request.setObservacoes(
                observationToSend != null ? new ObjectFactory().createAlterarDadosAcademicosPrimeiraVezRequestObservacoes(
                        observationToSend) : null);

        request.setRegime(convertRegimeCandidacy(data.getRegime()));
        request.setCodRegimeIngresso(Integer.valueOf(data.getIngressionRegime()));
        request.setTitularCET(data.getCetQualificationOwner());
        request.setTitularCSTP(data.getCtspQualificationOwner());
        request.setTitularDoutoramento(data.getPhdQualificationOwner());
        request.setTitularLicenciatura(data.getDegreeQualificationOwner());
        request.setTitularMestrado(data.getMasterQualificationOwner());

        request.setValorPropina(data.getGratuityAmount());

        getClient().alterarDadosAcademicosPrimeiraVez(request);

    }

    private String getObservationToSend(SasScholarshipCandidacy candidacy) {

        return candidacy.getLogsAfter(candidacy.getSubmissionDate(), true).stream()
                .sorted(SasScholarshipDataChangeLog.COMPARATOR_BY_DATE.reversed()).map(l -> l.getDescription()).distinct()
                .collect(Collectors.joining("\n"));
    }

    protected XMLGregorianCalendar createXMLGregorianCalendar(LocalDate localDate) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(localDate.toDate().getTime());

        DatatypeFactory datatypeFactory;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar newXMLGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
            return newXMLGregorianCalendar;
        } catch (DatatypeConfigurationException e) {
            return null;
        }

    }

    protected IdentificadorCandidatura createIdentificadorCandidaturaData(Integer beginExecutionYear, String docIdNumber,
            String docIdType, String fiscalNumber) {
        ObjectFactory factory = new ObjectFactory();

        IdentificadorCandidatura idCandidatura = new IdentificadorCandidatura();
        idCandidatura.setAnoLectivo(beginExecutionYear);
        idCandidatura.setDocumentoIdentificacao(factory.createIdentificadorCandidaturaDocumentoIdentificacao(docIdNumber));

        idCandidatura.setTipoDocumentoIdentificacao(TipoDocumentoIdentificacao.valueOf(docIdType));
        idCandidatura.setNif(factory.createIdentificadorCandidaturaNif(fiscalNumber));
        return idCandidatura;
    }

    private void sendOtherAcademicData(SasScholarshipCandidacy candidacy)
            throws DadosAcademicosAlterarDadosAcademicosRestantesCasosSicabeBusinessMessageFaultFaultMessage,
            DadosAcademicosAlterarDadosAcademicosRestantesCasosSicabeErrorMessageFaultFaultMessage,
            DadosAcademicosAlterarDadosAcademicosRestantesCasosSicabeValidationMessageFaultFaultMessage {

        final SasScholarshipData data = candidacy.getSasScholarshipData();

        AlterarDadosAcademicosRestantesCasosRequest request = new AlterarDadosAcademicosRestantesCasosRequest();

        request.setAnoInscricaoCurso(data.getCycleIngressionYear());
        request.setAnoLectivoActual(data.getCurricularYear());
        request.setCodigoCurso(candidacy.getDegreeCode());
        request.setCodigoInstituicaoEnsino(candidacy.getInstitutionCode());
        request.setDataConclusaoAtosAcademicosUltimoAnoLectivoInscrito(
                createXMLGregorianCalendar(data.getLastAcademicActDateLastYear()));
        request.setDataInscricaoAnoLectivo(createXMLGregorianCalendar(data.getEnrolmentDate()));

        IdentificadorCandidatura idCandidatura =
                createIdentificadorCandidaturaData(candidacy.getExecutionYear().getAcademicInterval().getStart().getYear(),
                        candidacy.getDocIdNumber(), candidacy.getDocIdType(), candidacy.getFiscalNumber());
        request.setIdentificadorCandidatura(idCandidatura);
        request.setIInscritoAnoLectivoActual(data.getEnroled());
        request.setMesPrimeiroPagamento(data.getFirstMonthExecutionYear());

        request.setNumeroAluno(candidacy.getStudentNumber());
        request.setNumeroAnosCurso(data.getNumberOfDegreeCurricularYears());
        request.setNumeroECTSActualmenteInscrito(data.getNumberOfEnrolledECTS());
        request.setNumeroECTSObtidosUltimoAnoInscrito(data.getNumberOfApprovedEctsLastYear());
        request.setNumeroECTSUltimoAnoInscrito(data.getNumberOfEnrolledEctsLastYear());
        request.setNumeroInscricoesCicloEstudosTempoIntegral(data.getCycleNumberOfEnrolmentsYearsInIntegralRegime());
        request.setNumeroMatriculas(data.getNumberOfEnrolmentsYears());

        request.setNumeroMesesPropina(data.getNumberOfMonthsExecutionYear());
        request.setNumeroOcorrenciasMudancaCurso(data.getNumberOfDegreeChanges());

        final String observationToSend = getObservationToSend(candidacy);
        request.setObservacoes(
                observationToSend != null ? new ObjectFactory().createAlterarDadosAcademicosRestantesCasosRequestObservacoes(
                        observationToSend) : null);

        request.setPresenteAnoMudouDeCurso(data.getHasMadeDegreeChangeOnCurrentYear());
        request.setRegime(convertRegimeCandidacy(data.getRegime()));
        request.setCodRegimeIngresso(Integer.valueOf(data.getIngressionRegime()));
        request.setTitularCET(data.getCetQualificationOwner());
        request.setTitularCSTP(data.getCtspQualificationOwner());
        request.setTitularDoutoramento(data.getPhdQualificationOwner());
        request.setTitularLicenciatura(data.getDegreeQualificationOwner());
        request.setTitularMestrado(data.getMasterQualificationOwner());
        request.setTotalECTScursoAtingirGrau(data.getNumberOfApprovedEcts());
        request.setUltimoAnoInscrito(Integer.valueOf(data.getLastEnrolmentYear()));
        request.setValorPropina(data.getGratuityAmount());

        getClient().alterarDadosAcademicosRestantesCasos(request);

    }

    /** TODO: to be done **/
    private void sendRegistrationStudent(SasScholarshipCandidacy candidacy) {

        RegistarMatriculaAlunoRequest request = new RegistarMatriculaAlunoRequest();

        request.setCodigoCurso(candidacy.getRegistration().getDegree().getMinistryCode());
        request.setCodigoInstituicaoEnsino(-1); // TODO
        request.setDataMatricula(null); // TODO

        IdentificadorCandidatura idCandidatura =
                createIdentificadorCandidaturaData(null /*TODO*/, candidacy.getDocIdNumber(), candidacy.getDocIdType(),
                        candidacy.getFiscalNumber());
        request.setIdentificadorCandidatura(idCandidatura);

    }

    private void updateCandidacyState(SasScholarshipCandidacy candidacy) {
        //TODO

        ObterEstadoCandidaturaRequest request = new ObterEstadoCandidaturaRequest();

        IdentificadorCandidatura idCandidatura =
                createIdentificadorCandidaturaData(null /*TODO*/, candidacy.getDocIdNumber(), candidacy.getDocIdType(),
                        candidacy.getFiscalNumber());
        request.setIdentificadorCandidatura(idCandidatura);

    }

    private void getSubmittedCandidacies(SasScholarshipCandidacy candidacy) {
        //TODO

        ObterEstadoCandidaturaRequest request = new ObterEstadoCandidaturaRequest();

        IdentificadorCandidatura idCandidatura =
                createIdentificadorCandidaturaData(null /*TODO*/, candidacy.getDocIdNumber(), candidacy.getDocIdType(),
                        candidacy.getFiscalNumber());
        request.setIdentificadorCandidatura(idCandidatura);

    }

    private void changeInstitutionDegree(SasScholarshipCandidacy candidacy) {
        //TODO
        ObjectFactory factory = new ObjectFactory();
        AlterarCursoInsituicaoRequest request = new AlterarCursoInsituicaoRequest();

        IdentificadorCandidatura idCandidatura =
                createIdentificadorCandidaturaData(null /*TODO*/, candidacy.getDocIdNumber(), candidacy.getDocIdType(),
                        candidacy.getFiscalNumber());
        request.setIdentificadorCandidatura(idCandidatura);

        request.setIdentificadorCandidatura(idCandidatura);
        request.setCodigoCurso(candidacy.getRegistration().getDegree().getMinistryCode());
        request.setCodigoInstituicaoEnsino(-1);
        request.setDataMudanca(null);

    }

    public static byte[] export(ExecutionYear executionYear) {

        final SpreadsheetBuilderForXLSX builder = new SpreadsheetBuilderForXLSX();

        builder.addSheet("SAS", new SheetDataExtension(executionYear.getSasScholarshipCandidaciesSet()));

        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            builder.build(result);
            return result.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static final class SheetDataExtension extends SheetData<SasScholarshipCandidacy> {
        private SheetDataExtension(Iterable<SasScholarshipCandidacy> items) {
            super(items);
        }

        @Override
        protected void makeLine(final SasScholarshipCandidacy candidacy) {

            final SasScholarshipData data = candidacy.getSasScholarshipData();

            addData("SasScholarshipCandidacy.candidacyName", candidacy.getCandidacyName());
            addData("SasScholarshipCandidacy.docIdNumber", candidacy.getDocIdNumber());
            addData("SasScholarshipCandidacy.docIdType", candidacy.getDocIdType());
            addData("SasScholarshipCandidacy.fiscalNumber", candidacy.getFiscalNumber());
            addData("SasScholarshipCandidacy.stateDate", format(candidacy.getStateDate()));
            addData("SasScholarshipCandidacy.exportDate", format(candidacy.getExportDate()));

            addData("SasScholarshipCandidacy.assignmentDate", format(candidacy.getAssignmentDate()));
            addData("SasScholarshipCandidacy.candidacyState", candidacy.getCandidacyState().getLocalizedName());
            addData("SasScholarshipCandidacy.description", candidacy.getDescription());

            final Registration registration = candidacy.getRegistration();

            if (registration != null) {
                addData("SasScholarshipCandidacy.studentNumber",
                        registration.getNumber() != null ? registration.getNumber().toString() : "-");

                addData("SasScholarshipCandidacy.degreeCode",
                        registration.getDegree() != null ? registration.getDegree().getCode() : "");

                addData("SasScholarshipCandidacy.degreeName",
                        registration.getDegree() != null ? registration.getDegree().getPresentationName() : "");
            } else {
                addData("SasScholarshipCandidacy.studentNumber", candidacy.getStudentNumber());
                addData("SasScholarshipCandidacy.degreeCode", candidacy.getDegreeCode());
                addData("SasScholarshipCandidacy.degreeName", candidacy.getDegreeName());
            }

            addData("SasScholarshipData.state", candidacy.getState().getLocalizedName() + (candidacy.isModified() ? " *" : ""));

            final List<SasScholarshipDataChangeLog> logs =
                    candidacy.getSasScholarshipDataChangeLogsSet().stream().sorted((x, y) -> -x.getDate().compareTo(y.getDate()))
                            .collect(Collectors.toList());

            final List<SasScholarshipDataChangeLog> sentLogs =
                    logs.stream().filter(l -> l.getSentLog()).collect(Collectors.toList());

            final List<SasScholarshipDataChangeLog> logsToExcel =
                    logs.size() == 0 ? new ArrayList<SasScholarshipDataChangeLog>() : logs.subList(0,
                            sentLogs.size() < 2 ? logs.size() - 1 : logs.indexOf(sentLogs.get(1)));

            addData("event.logs",
                    logsToExcel.stream().map(SasScholarshipDataChangeLog::getDescription).collect(Collectors.joining("\n")));

            if (data != null) {
                addData("SasScholarshipCandidacy.firstYear",
                        candidacy.getFirstYear() ? bundle("label.true") : bundle("label.false"));
                addData("SasScholarshipData.cycleIngressionYear",
                        data.getCycleIngressionYear() != null ? data.getCycleIngressionYear().toString() : "");
                addData("SasScholarshipData.curricularYear", data.getCurricularYear());
                addData("SasScholarshipData.lastAcademicActDateLastYear", format(data.getLastAcademicActDateLastYear()));
                addData("SasScholarshipData.enrolmentDate", format(data.getEnrolmentDate()));
                addData("SasScholarshipData.firstMonthExecutionYear", data.getFirstMonthExecutionYear());
                addData("SasScholarshipData.numberOfDegreeCurricularYears", data.getNumberOfDegreeCurricularYears());
                addData("SasScholarshipData.numberOfEnrolledECTS", data.getNumberOfEnrolledECTS());
                addData("SasScholarshipData.numberOfApprovedEctsLastYear", data.getNumberOfApprovedEctsLastYear());
                addData("SasScholarshipData.numberOfEnrolledEctsLastYear", data.getNumberOfEnrolledEctsLastYear());
                addData("SasScholarshipData.numberOfEnrolmentsYears", data.getNumberOfEnrolmentsYears());
                addData("SasScholarshipData.cycleNumberOfEnrolmentsYearsInIntegralRegime",
                        data.getCycleNumberOfEnrolmentsYearsInIntegralRegime());
                addData("SasScholarshipData.numberOfMonthsExecutionYear", data.getNumberOfMonthsExecutionYear());
                addData("SasScholarshipData.numberOfDegreeChanges", data.getNumberOfDegreeChanges());
                addData("SasScholarshipData.observations", data.getObservations());
                addData("SasScholarshipData.hasMadeDegreeChangeOnCurrentYear",
                        format(data.getHasMadeDegreeChangeOnCurrentYear()));
                addData("SasScholarshipData.regime", data.getRegime());
                addData("SasScholarshipData.cetQualificationOwner", format(data.getCetQualificationOwner()));
                addData("SasScholarshipData.ctspQualificationOwner", format(data.getCtspQualificationOwner()));
                addData("SasScholarshipData.phdQualificationOwner", format(data.getPhdQualificationOwner()));
                addData("SasScholarshipData.degreeQualificationOwner", format(data.getDegreeQualificationOwner()));
                addData("SasScholarshipData.masterQualificationOwner", format(data.getMasterQualificationOwner()));
                addData("SasScholarshipData.lastEnrolmentYear", data.getLastEnrolmentYear());
                addData("SasScholarshipData.gratuityAmount", data.getGratuityAmount());
                addData("SasScholarshipData.enroled", format(data.getEnroled()));
                addData("SasScholarshipData.numberOfApprovedEcts", data.getNumberOfApprovedEcts());
                addData("SasScholarshipData.ingressionRegime", data.getIngressionRegime());
            } else {

                final String[] fields = { "SasScholarshipCandidacy.firstYear", "SasScholarshipData.cycleIngressionYear",
                        "SasScholarshipData.curricularYear", "SasScholarshipData.lastAcademicActDateLastYear",
                        "SasScholarshipData.enrolmentDate", "SasScholarshipData.firstMonthExecutionYear",
                        "SasScholarshipData.numberOfDegreeCurricularYears", "SasScholarshipData.numberOfEnrolledECTS",
                        "SasScholarshipData.numberOfApprovedEctsLastYear", "SasScholarshipData.numberOfEnrolledEctsLastYear",
                        "SasScholarshipData.numberOfEnrolmentsYears",
                        "SasScholarshipData.cycleNumberOfEnrolmentsYearsInIntegralRegime",
                        "SasScholarshipData.numberOfMonthsExecutionYear", "SasScholarshipData.numberOfDegreeChanges",
                        "SasScholarshipData.observations", "SasScholarshipData.hasMadeDegreeChangeOnCurrentYear",
                        "SasScholarshipData.regime", "SasScholarshipData.cetQualificationOwner",
                        "SasScholarshipData.ctspQualificationOwner", "SasScholarshipData.phdQualificationOwner",
                        "SasScholarshipData.degreeQualificationOwner", "SasScholarshipData.masterQualificationOwner",
                        "SasScholarshipData.lastEnrolmentYear", "SasScholarshipData.gratuityAmount", "SasScholarshipData.enroled",
                        "SasScholarshipData.numberOfApprovedEcts", "SasScholarshipData.ingressionRegime" };

                Arrays.stream(fields).forEach(f -> addData(f, "-"));
            }

        }

        private String format(Boolean value) {
            return value == null ? null : (value.booleanValue() ? bundle("label.true") : bundle("label.false"));
        }

        private LocalDate format(LocalDate value) {
            return value;
        }

        private LocalDate format(DateTime value) {
            return value != null ? value.toLocalDate() : null;
        }

        private void addData(final String key, final Object value) {
            addCell(bundle("label." + key), value == null ? "" : value);
        }
    }

    public boolean isAllowedToSend(final SasScholarshipCandidacy candidacy) {
        return stateAllowToSendCandicacy(candidacy.getState());
    }

    @Atomic
    private void createLogEntry(SasScholarshipCandidacy candidacy, String message, DateTime date, boolean publicLog,
            boolean sentLog) {
        new SasScholarshipDataChangeLog(candidacy, date, message, publicLog, sentLog);
    }

    static private String bundle(final String key) {
        return SasPTUtil.bundle(key);
    }
}
