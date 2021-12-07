package org.fenixedu.legalpt.services.rebides.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.legalpt.domain.rebides.report.RebidesRequestParameter;
import org.fenixedu.legalpt.domain.report.LegalReportRequest;
import org.fenixedu.legalpt.domain.report.LegalReportResultFile;
import org.fenixedu.legalpt.domain.report.LegalReportResultFileType;
import org.fenixedu.legalpt.dto.rebides.CareerActivitiesBean;
import org.fenixedu.legalpt.dto.rebides.IdentificationBean;
import org.fenixedu.legalpt.dto.rebides.QualificationBean;
import org.fenixedu.legalpt.dto.rebides.RebidesBean;
import org.fenixedu.legalpt.dto.rebides.TeacherBean;
import org.fenixedu.legalpt.jaxb.rebides.InformacaoDocentes;
import org.fenixedu.legalpt.jaxb.rebides.InformacaoDocentes.Docentes;
import org.fenixedu.legalpt.jaxb.rebides.InformacaoDocentes.Docentes.Docente;
import org.fenixedu.legalpt.jaxb.rebides.InformacaoDocentes.Docentes.Docente.Habilitacoes;
import org.fenixedu.legalpt.jaxb.rebides.InformacaoDocentes.Docentes.Docente.Habilitacoes.Habilitacao;
import org.fenixedu.legalpt.jaxb.rebides.InformacaoDocentes.Docentes.Docente.Identificacao;
import org.fenixedu.legalpt.jaxb.rebides.InformacaoDocentes.Docentes.Docente.SituacaoCarreiraAtividades;
import org.fenixedu.legalpt.jaxb.rebides.InformacaoDocentes.Extracao;
import org.fenixedu.legalpt.jaxb.rebides.Myboolean;
import org.fenixedu.legalpt.jaxb.rebides.NumeroID;
import org.fenixedu.legalpt.jaxb.rebides.ObjectFactory;
import org.fenixedu.legalpt.jaxb.rebides.Sexo;
import org.joda.time.LocalDate;

public class RebidesXmlToBaseFileWriter {

    private static final String ENCODING = "utf-8";

    public static LegalReportResultFile write(final LegalReportRequest reportRequest, final RebidesBean rebides) {
        try {
            final RebidesRequestParameter requestParameters = reportRequest.getParametersAs(RebidesRequestParameter.class);
            final ObjectFactory factory = new ObjectFactory();
            final DatatypeFactory dataTypeFactory = createDataTypeFactory();

            final InformacaoDocentes informacaoDocentes = factory.createInformacaoDocentes();

            fillExtracao(requestParameters, factory, dataTypeFactory, informacaoDocentes);

            final Docentes docentes = factory.createInformacaoDocentesDocentes();
            informacaoDocentes.setDocentes(docentes);

            for (final TeacherBean teacher : rebides.getTeachers()) {
                final Docente docente = fillDocente(factory, dataTypeFactory, teacher);

                if (docente != null) {
                    docentes.getDocente().add(docente);
                }
            }

            final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            final Marshaller marshaller = context.createMarshaller();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //TODO: check encoding on manual (utf-8 ?)
            final OutputStreamWriter osw = new OutputStreamWriter(baos, Charset.forName(ENCODING));

            try {
                marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(informacaoDocentes, osw);
                byte[] content = baos.toByteArray();

                final String filename = "D0" + requestParameters.getMoment() + requestParameters.getInstitutionCode() + ".xml";
                return new LegalReportResultFile(reportRequest, LegalReportResultFileType.XML, filename, content);

            } finally {
                try {
                    osw.close();
                    baos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (final JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private static DatatypeFactory createDataTypeFactory() {
        try {
            return DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e1) {
            throw new RuntimeException(e1);
        }
    }

    protected static void fillExtracao(final RebidesRequestParameter parameters, ObjectFactory factory,
            DatatypeFactory dataTypeFactory, final InformacaoDocentes informacaoDocentes) {
        final Extracao extracao = factory.createInformacaoDocentesExtracao();
        informacaoDocentes.setExtracao(extracao);

        extracao.setCodigoEstabelecimento(parameters.getInstitutionCode());

        extracao.setDataExtracao(toXMLDate(dataTypeFactory, new LocalDate()));
        extracao.setEmailInterlocutor(parameters.getInterlocutorEmail());
        extracao.setMomento(Integer.valueOf(parameters.getMoment()));
        extracao.setNomeInterlocutor(parameters.getInterlocutorName());
        extracao.setTelefoneInterlocutor(new BigInteger(parameters.getInterlocutorPhone()));
    }

    protected static Docente fillDocente(ObjectFactory factory, DatatypeFactory dataTypeFactory, final TeacherBean teacher) {

        final IdentificationBean identificationBean = teacher.getIdentification();
        final CareerActivitiesBean careerActivitiesBean = teacher.getCareerActivities();
        final Collection<QualificationBean> qualificationsForTeacher = teacher.getQualifications();

        final Docente docente = factory.createInformacaoDocentesDocentesDocente();
        docente.setIdentificacao(fillIdentificacaoDocente(factory, dataTypeFactory, identificationBean));
        docente.setSituacaoCarreiraAtividades(fillSituacaoCarreiraAtividades(factory, careerActivitiesBean));

        if (!qualificationsForTeacher.isEmpty()) {
            final Habilitacoes habilitacoes = factory.createInformacaoDocentesDocentesDocenteHabilitacoes();
            docente.setHabilitacoes(habilitacoes);

            for (final QualificationBean qualificationBean : qualificationsForTeacher) {
                habilitacoes.getHabilitacao().add(fillHabilitacao(factory, qualificationBean));
            }
        }

        return docente;
    }

    private static Identificacao fillIdentificacaoDocente(ObjectFactory factory, DatatypeFactory datatypeFactory,
            IdentificationBean bean) {
        final Identificacao identificacao = factory.createInformacaoDocentesDocentesDocenteIdentificacao();

        identificacao.setNome(bean.getName());

        final NumeroID numeroID = factory.createNumeroID();
        numeroID.setValue(bean.getDocumentIdNumber());
        numeroID.setTipo(bean.getDocumentIdType() != null ? new BigInteger(bean.getDocumentIdType()) : null);
        identificacao.setNumeroID(numeroID);

        identificacao.setOutroTipoID(bean.getOtherIdDocumentType());
        identificacao.setDataNascimento(toXMLDate(datatypeFactory, bean.getDateOfBirth()));
        identificacao.setSexo(bean.getGender() != null ? Sexo.valueOf(bean.getGender()) : null);
        identificacao.setPaisDeNacionalidade(bean.getNationalityCountry());
        identificacao.setOutroPaisDeNacionalidade(bean.getOtherNationalityCountry());

        return identificacao;
    }

    private static SituacaoCarreiraAtividades fillSituacaoCarreiraAtividades(ObjectFactory factory, CareerActivitiesBean bean) {

        final SituacaoCarreiraAtividades situacaoCarreiraAtividades =
                factory.createInformacaoDocentesDocentesDocenteSituacaoCarreiraAtividades();

        situacaoCarreiraAtividades.setDepartamento(bean.getDepartment());
        situacaoCarreiraAtividades.setOutroDepartamento(bean.getOtherDepartment());
        situacaoCarreiraAtividades.setCategoria(bean.getCategory() != null ? Integer.valueOf(bean.getCategory()) : -1);
        situacaoCarreiraAtividades.setConvidado(toXmlBoolean(bean.getGuest()));
        situacaoCarreiraAtividades.setEquiparado(toXmlBoolean(bean.getSchoolStaff()));
        situacaoCarreiraAtividades.setProvasAptidao(bean.getServiceRegime());
        situacaoCarreiraAtividades.setProvasAgregacao(bean.getAggregationTests());
        situacaoCarreiraAtividades.setProvasEspecialista(bean.getExpertTests());
        //TODO:
//        situacaoCarreiraAtividades.setRegimePrestacaoServico(new BigInteger(bean.getServiceRegime()));
//        situacaoCarreiraAtividades.setRegimeParcial(Integer.valueOf(bean.getPartialRegime()));
//        situacaoCarreiraAtividades.setRegimeVinculacao(Integer.valueOf(bean.getBindingRegime()));
        //TODO: check
//        situacaoCarreiraAtividades.setIndice(longValueOf(bean.getContractWageLevel()));
//        situacaoCarreiraAtividades.setPercentagemInvestigacao(bean.getResearchPercentage().intValue());
        situacaoCarreiraAtividades.setDocencia(toXmlBoolean(bean.getTeaching()));
        situacaoCarreiraAtividades.setHorasLetivas(bean.getTeachingHoursPercentage() != null ? bean.getTeachingHoursPercentage()
                .setScale(1, RoundingMode.HALF_EVEN) : null);
        situacaoCarreiraAtividades.setUnidadeHL(bean.getTeachingHoursUnit());
        situacaoCarreiraAtividades.setActReitor(toXmlBoolean(bean.getRootUnitPresident()));
        situacaoCarreiraAtividades.setActViceReitor(toXmlBoolean(bean.getRootUnitVicePresident()));
        situacaoCarreiraAtividades.setActDiretor(toXmlBoolean(bean.getUnitPresident()));
        situacaoCarreiraAtividades.setActCoordena(toXmlBoolean(bean.getUnitCoordinator()));
        situacaoCarreiraAtividades.setActConsGeral(toXmlBoolean(bean.getCoordenatorGeneralCouncil()));
        situacaoCarreiraAtividades.setActConsGest(toXmlBoolean(bean.getManagementCouncilActivities()));
        situacaoCarreiraAtividades.setActConsCient(toXmlBoolean(bean.getScientificCouncilActivities()));
        situacaoCarreiraAtividades.setActConsPedag(toXmlBoolean(bean.getPedagogicCouncilActivities()));
        situacaoCarreiraAtividades.setActFormacao(toXmlBoolean(bean.getCoachingOrResearchProjectActivities()));
        situacaoCarreiraAtividades.setActOutra(toXmlBoolean(bean.getOtherActivity()));
        situacaoCarreiraAtividades.setActOutraDescr(bean.getOtherActivityDescription());

        return situacaoCarreiraAtividades;
    }

    private static Habilitacao fillHabilitacao(ObjectFactory factory, QualificationBean bean) {
        final Habilitacao habitalitacao = factory.createInformacaoDocentesDocentesDocenteHabilitacoesHabilitacao();

        //TODO:
//        habitalitacao.setNivelFormacao(Integer.valueOf(bean.getSchoolLevel()));
//        habitalitacao.setTipoNivelFormacao(Integer.valueOf(bean.getSchoolLevelOrigin()));
//        habitalitacao.setEstabelecimento(bean.getInstituition());
//        habitalitacao.setOutroEstabelecimento(bean.getOtherInstituition());
//        habitalitacao.setCurso(bean.getDegree());
//        habitalitacao.setOutroCurso(bean.getOtherDegree());
//        habitalitacao.setEspecialidade(bean.getExpertiseArea());
//        habitalitacao.setOutraEspecialidade(bean.getOtherExpertiseArea());
        //TODO: areaCNAEF == Area Cientifica?
        //habitalitacao.setAreaCientifica(longValueOf(bean.getScientificArea()));

        return habitalitacao;
    }

    protected static Long longValueOf(final String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        return Long.valueOf(value);
    }

    protected static Long longValueOf(final BigDecimal value) {
        if (value == null) {
            return null;
        }

        return value.longValue();
    }

    private static XMLGregorianCalendar toXMLDate(DatatypeFactory datatypeFactory, LocalDate date) {
        final XMLGregorianCalendar result = toXmlDateTime(datatypeFactory, date);

        if (result == null) {
            return null;
        }

        //Remove Time
        result.setTime(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                DatatypeConstants.FIELD_UNDEFINED);
        //Remove time zone
        result.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        return result;
    }

    private static XMLGregorianCalendar toXmlDateTime(DatatypeFactory datatypeFactory, LocalDate date) {

        if (date == null) {
            return null;
        }

        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(date.toDate().getTime());

        return datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
    }

    private static Myboolean toXmlBoolean(Boolean value) {
        if (value == null) {
            return null;
        }

        return value.booleanValue() ? Myboolean.TRUE : Myboolean.FALSE;

    }

}
