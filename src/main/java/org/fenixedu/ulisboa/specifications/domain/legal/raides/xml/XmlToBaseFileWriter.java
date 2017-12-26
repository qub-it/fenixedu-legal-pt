package org.fenixedu.ulisboa.specifications.domain.legal.raides.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblDiplomado;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblIdentificacao;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblInscrito;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblMobilidadeInternacional;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesRequestParameter;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequest;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFile;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFileType;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.DuracaoPrograma;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.InformacaoAlunos;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.InformacaoAlunos.Alunos;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.InformacaoAlunos.Alunos.Aluno;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.InformacaoAlunos.Alunos.Aluno.Diplomas;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.InformacaoAlunos.Alunos.Aluno.Diplomas.Diploma;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.InformacaoAlunos.Alunos.Aluno.Identificacao;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.InformacaoAlunos.Alunos.Aluno.Inscricoes;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.InformacaoAlunos.Alunos.Aluno.Inscricoes.Inscricao;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.InformacaoAlunos.Alunos.Aluno.Mobilidade;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.InformacaoAlunos.Extracao;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.NumeroID;
import org.fenixedu.ulisboa.specifications.legal.jaxb.raides.ObjectFactory;
import org.joda.time.LocalDate;

import com.google.common.base.Strings;

public class XmlToBaseFileWriter {

    public static LegalReportResultFile write(final LegalReportRequest reportRequest,
            final RaidesRequestParameter raidesRequestParameter, final Raides raides) {
        try {
            final ObjectFactory factory = new ObjectFactory();

            final InformacaoAlunos informacaoAlunos = factory.createInformacaoAlunos();

            fillExtracao(raidesRequestParameter, factory, informacaoAlunos);

            final Alunos alunos = factory.createInformacaoAlunosAlunos();
            informacaoAlunos.setAlunos(alunos);

            for (final Student student : raides.studentsToReport()) {
                final Aluno aluno = fillAluno(raidesRequestParameter, raides, factory, student);

                if (aluno != null) {
                    alunos.getAluno().add(aluno);
                }
            }

            final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            final Marshaller marshaller = context.createMarshaller();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final OutputStreamWriter osw = new OutputStreamWriter(baos, Charset.forName("ISO-8859-15"));

            try {
                marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-15");
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(informacaoAlunos, osw);
                byte[] content = baos.toByteArray();

                final String filename =
                        "A0" + raidesRequestParameter.getMoment() + raidesRequestParameter.getInstitutionCode() + ".xml";
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

    protected static Aluno fillAluno(final RaidesRequestParameter raidesRequestParameter, final Raides raides,
            ObjectFactory factory, final Student student) {
        try {
            final TblIdentificacao tblIdentificacao = raides.identificacaoForStudent(student);
            final Collection<TblInscrito> inscricoesForStudent = raides.inscricoesForStudent(student, raidesRequestParameter);
            final Collection<TblDiplomado> diplomadosForStudent = raides.diplomadosForStudent(student, raidesRequestParameter);
            final TblMobilidadeInternacional tblMobilidadeInternacional =
                    raides.mobilidadeInternacionalForStudent(student, raidesRequestParameter);

            if (raidesRequestParameter.isFilterEntriesWithErrors() && !tblIdentificacao.isValid()) {
                return null;
            }

            if (raidesRequestParameter.isFilterEntriesWithErrors() && inscricoesForStudent.isEmpty()
                    && diplomadosForStudent.isEmpty() && tblMobilidadeInternacional == null) {
                return null;
            }

            final Aluno aluno = factory.createInformacaoAlunosAlunosAluno();
            aluno.setIdentificacao(fillIdentificacaoAluno(raides, factory, student));

            if (!inscricoesForStudent.isEmpty()) {
                final Inscricoes inscricoes = factory.createInformacaoAlunosAlunosAlunoInscricoes();
                aluno.setInscricoes(inscricoes);

                for (final TblInscrito tblInscrito : inscricoesForStudent) {
                    inscricoes.getInscricao().add(fillInscricao(raides, factory, student, tblInscrito));
                }
            }

            if (!diplomadosForStudent.isEmpty()) {
                final Diplomas diplomas = factory.createInformacaoAlunosAlunosAlunoDiplomas();
                aluno.setDiplomas(diplomas);

                for (final TblDiplomado tblDiplomado : diplomadosForStudent) {
                    diplomas.getDiploma().add(fillDiploma(raides, factory, student, tblDiplomado));
                }
            }

            if (tblMobilidadeInternacional != null) {
                final Mobilidade mobilidade = fillMobilidadeInternacional(raides, factory, student, tblMobilidadeInternacional);
                aluno.setMobilidade(mobilidade);

            }

            return aluno;

        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Mobilidade fillMobilidadeInternacional(Raides raides, ObjectFactory factory, Student student,
            TblMobilidadeInternacional tblMobilidadeInternacional) {
        final Mobilidade mobilidade = factory.createInformacaoAlunosAlunosAlunoMobilidade();
        mobilidade.setAnoCurricular(longValueOf(tblMobilidadeInternacional.getAnoCurricular()));
        mobilidade.setAnoLetivo(tblMobilidadeInternacional.getAnoLectivo());
        mobilidade.setAreaCientifica(null);
        mobilidade.setCurso(tblMobilidadeInternacional.getCurso());
        mobilidade.setDuracaoPrograma(duracaoProgramaValueOf(tblMobilidadeInternacional));
        mobilidade.setECTSInscricao(bigDecimalValueOf(tblMobilidadeInternacional.getEctsInscrito()));
        mobilidade.setNivelCursoDestino(null);
        mobilidade.setNivelCursoOrigem(longValueOf(tblMobilidadeInternacional.getNivelCursoOrigem()));
        mobilidade.setOutroNivelCurDestino(null);
        mobilidade.setOutroNivelCurOrigem(tblMobilidadeInternacional.getOutroNivelCurOrigem());
        mobilidade.setOutroPrograma(tblMobilidadeInternacional.getOutroPrograma());
        mobilidade.setProgMobilidade(longValueOf(tblMobilidadeInternacional.getProgMobilidade()));

        mobilidade.setAreaCientifica(tblMobilidadeInternacional.getAreaCientifica());
        mobilidade.setNivelCursoDestino(longValueOf(tblMobilidadeInternacional.getNivelCursoDestino()));
        mobilidade.setOutroNivelCurDestino(tblMobilidadeInternacional.getOutroNivelCursoDestino());

        if (!Strings.isNullOrEmpty(tblMobilidadeInternacional.getRamo())) {
            mobilidade.setRamo(tblMobilidadeInternacional.getRamo());
        }

        mobilidade.setRegimeFrequencia(longValueOf(tblMobilidadeInternacional.getRegimeFrequencia()));
        mobilidade.setTipoProgMobilidade(longValueOf(tblMobilidadeInternacional.getTipoProgMobilidade()));

        return mobilidade;
    }

    private static BigDecimal bigDecimalValueOf(final String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }

        return new BigDecimal(value);
    }

    private static Diploma fillDiploma(Raides raides, ObjectFactory factory, Student student, TblDiplomado tblDiplomado) {
        final Diploma diploma = factory.createInformacaoAlunosAlunosAlunoDiplomasDiploma();

        diploma.setAnoEscolaridadeAnt(longValueOf(tblDiplomado.getAnoEscolaridadeAnt()));
        diploma.setAnoLetivo(tblDiplomado.getAnoLectivo());
        diploma.setClassificacaoFinal(longValueOf(tblDiplomado.getClassificacaoFinal()));
        diploma.setConcluiuGrau(booleanValueOf(tblDiplomado.getConcluiGrau()));
        diploma.setConclusaoMD(booleanValueOf(tblDiplomado.getConclusaoMd()));
        diploma.setClassificacaoFinalMD(longValueOf(tblDiplomado.getClassificacaoFinalMd()));
        diploma.setCurso(tblDiplomado.getCurso());
        diploma.setDataDiploma(tblDiplomado.getDataDiploma());
        diploma.setEscolaridadeAnterior(longValueOf(tblDiplomado.getEscolaridadeAnterior()));
        diploma.setOutroEscolaridadeAnterior(tblDiplomado.getOutroEscolaridadeAnterior());
        diploma.setMobilidadeCredito(booleanValueOf(tblDiplomado.getMobilidadeCredito()));
        diploma.setNumInscConclusao(longValueOf(tblDiplomado.getNumInscConclusao()));
        diploma.setEstabEscolaridadeAnt(tblDiplomado.getEstabEscolaridadeAnt());
        diploma.setOutroEstabEscolarAnt(tblDiplomado.getOutroEstabEscolarAnt());
        diploma.setCursoEscolarAnt(tblDiplomado.getCursoEscolarAnt());
        diploma.setOutroCursoEscolarAnt(tblDiplomado.getOutroCursoEscolarAnt());
        diploma.setOutroProgMobCredito(tblDiplomado.getOutroProgMobCredito());
        diploma.setPaisEscolaridadeAnt(tblDiplomado.getPaisEscolaridadeAnt());
        diploma.setPaisMobilidadeCredito(tblDiplomado.getPaisMobilidadeCredito());
        diploma.setProgMobilidadeCredito(longValueOf(tblDiplomado.getProgMobilidadeCredito()));
        diploma.setAreaInvestigacao(integerValueOf(tblDiplomado.getAreaInvestigacao()));

        if (!Strings.isNullOrEmpty(tblDiplomado.getRamo())) {
            diploma.setRamo(tblDiplomado.getRamo());
        } else {
            diploma.setRamo(Raides.Ramo.TRONCO_COMUM);
        }

        diploma.setTipoMobilidadeCredito(longValueOf(tblDiplomado.getTipoMobilidadeCredito()));

        return diploma;
    }

    protected static Inscricao fillInscricao(final Raides raides, final ObjectFactory factory, final Student student,
            final TblInscrito tblInscrito) {
        final Inscricao inscricao = factory.createInformacaoAlunosAlunosAlunoInscricoesInscricao();

        inscricao.setCurso(tblInscrito.getCurso());

        if (!Strings.isNullOrEmpty(tblInscrito.getRamo())) {
            inscricao.setRamo(tblInscrito.getRamo());
        } else {
            inscricao.setRamo(Raides.Ramo.TRONCO_COMUM);
        }

        inscricao.setAnoLetivo(tblInscrito.getAnoLectivo());
        inscricao.setAnoCurricular(longValueOf(tblInscrito.getAnoCurricular()));
        inscricao.setPrimeiraVez(booleanValueOf(tblInscrito.getPrimeiraVez()));
        inscricao.setRegimeFrequencia(longValueOf(tblInscrito.getRegimeFrequencia()));
        inscricao.setNumInscNesteCurso(
                tblInscrito.getNumInscNesteCurso() != null ? Long.valueOf((int) tblInscrito.getNumInscNesteCurso()) : null);
        inscricao.setECTSInscricao(tblInscrito.getEctsInscricao() != null ? tblInscrito.getEctsInscricao().setScale(2,
                RoundingMode.HALF_EVEN) : null);
        inscricao.setECTSAcumulados(tblInscrito.getEctsAcumulados() != null ? tblInscrito.getEctsAcumulados().setScale(2,
                RoundingMode.HALF_EVEN) : null);
        inscricao.setTempoParcial(booleanValueOf(tblInscrito.getTempoParcial()));
        inscricao.setBolseiro(longValueOf(tblInscrito.getBolseiro()));
        inscricao.setFormaIngresso(longValueOf(tblInscrito.getFormaIngresso()));
        inscricao.setEstabInscricaoAnt(tblInscrito.getEstabInscricaoAnt());
        inscricao.setOutroEstabInscAnt(tblInscrito.getOutroEstabInscAnt());
        inscricao.setNotaIngresso(
                !Strings.isNullOrEmpty(tblInscrito.getNotaIngresso()) ? new BigDecimal(tblInscrito.getNotaIngresso()) : null);
        inscricao.setOpcaoIngresso(longValueOf(tblInscrito.getOpcaoIngresso()));
        inscricao.setNumInscCursosAnt(
                tblInscrito.getNumInscCursosAnt() != null ? Long.valueOf(tblInscrito.getNumInscCursosAnt()) : null);
        inscricao.setAnoUltimaInscricao(tblInscrito.getAnoUltimaInscricao());
        inscricao.setEstadoCivil(longValueOf(tblInscrito.getEstadoCivil()));
        inscricao.setTrabalhadorEstudante(booleanValueOf(tblInscrito.getEstudanteTrabalhador()));
        inscricao.setAlunoDeslocado(booleanValueOf(tblInscrito.getAlunoDeslocado()));
        inscricao.setConcelho(tblInscrito.getResideConcelho());

        if (!Strings.isNullOrEmpty(tblInscrito.getNivelEscolarPai())) {
            inscricao.setNivelEscolarPai(longValueOf(tblInscrito.getNivelEscolarPai()));
        } else {
            inscricao.setNivelEscolarPai(longValueOf(Raides.NivelEscolaridade.NAO_DISPONIVEL));
        }

        if (!Strings.isNullOrEmpty(tblInscrito.getNivelEscolarMae())) {
            inscricao.setNivelEscolarMae(longValueOf(tblInscrito.getNivelEscolarMae()));
        } else {
            inscricao.setNivelEscolarMae(longValueOf(Raides.NivelEscolaridade.NAO_DISPONIVEL));
        }

        if (!Strings.isNullOrEmpty(tblInscrito.getSituacaoProfPai())) {
            inscricao.setSituacaoProfPai(longValueOf(tblInscrito.getSituacaoProfPai()));
        } else {
            inscricao.setSituacaoProfPai(longValueOf(Raides.SituacaoProfissional.NAO_DISPONIVEL));
        }

        if (!Strings.isNullOrEmpty(tblInscrito.getSituacaoProfMae())) {
            inscricao.setSituacaoProfMae(longValueOf(tblInscrito.getSituacaoProfMae()));
        } else {
            inscricao.setSituacaoProfMae(longValueOf(Raides.SituacaoProfissional.NAO_DISPONIVEL));
        }

        if (!Strings.isNullOrEmpty(tblInscrito.getSituacaoProfAluno())
                && !Raides.SituacaoProfissional.NAO_DISPONIVEL.equals(tblInscrito.getSituacaoProfAluno())) {
            inscricao.setSituacaoProfAluno(longValueOf(tblInscrito.getSituacaoProfAluno()));
        } else {
            inscricao.setSituacaoProfAluno(longValueOf(Raides.SituacaoProfissional.ALUNO));
        }

        if (!Strings.isNullOrEmpty(tblInscrito.getProfissaoPai())) {
            inscricao.setProfissaoPai(longValueOf(tblInscrito.getProfissaoPai()));
        } else {
            inscricao.setProfissaoPai(longValueOf(Raides.Profissao.NAO_DISPONIVEL));
        }

        if (!Strings.isNullOrEmpty(tblInscrito.getProfissaoMae())) {
            inscricao.setProfissaoMae(longValueOf(tblInscrito.getProfissaoMae()));
        } else {
            inscricao.setProfissaoMae(longValueOf(Raides.Profissao.NAO_DISPONIVEL));
        }

        if (!Strings.isNullOrEmpty(tblInscrito.getProfissaoAluno())
                && !Raides.Profissao.NAO_DISPONIVEL.equals(tblInscrito.getProfissaoAluno())) {
            inscricao.setProfissaoAluno(longValueOf(tblInscrito.getProfissaoAluno()));
        } else {
            inscricao.setProfissaoAluno(longValueOf(Raides.Profissao.OUTRA_SITUACAO));
        }

        inscricao.setEscolaridadeAnterior(longValueOf(tblInscrito.getEscolaridadeAnterior()));
        inscricao.setOutroEscolaridadeAnterior(tblInscrito.getOutroEscolaridadeAnterior());
        inscricao.setPaisEscolaridadeAnt(tblInscrito.getPaisEscolaridadeAnt());
        inscricao.setAnoEscolaridadeAnt(longValueOf(tblInscrito.getAnoEscolaridadeAnt()));
        inscricao.setEstabEscolaridadeAnt(tblInscrito.getEstabEscolaridadeAnt());
        inscricao.setOutroEstabEscolarAnt(tblInscrito.getOutroEstabEscolarAnt());
        inscricao.setCursoEscolarAnt(tblInscrito.getCursoEscolarAnt());
        inscricao.setOutroCursoEscolarAnt(tblInscrito.getOutroCursoEscolarAnt());
        inscricao.setTipoEstabSec(longValueOf(tblInscrito.getTipoEstabSec()));

        return inscricao;
    }

    protected static Identificacao fillIdentificacaoAluno(final Raides raides, ObjectFactory factory, final Student student) {
        final Identificacao identificacao = factory.createInformacaoAlunosAlunosAlunoIdentificacao();
        final TblIdentificacao tblIdentificacao = raides.identificacaoForStudent(student);

        identificacao.setDataNascimento(tblIdentificacao.getDataNasc());
        identificacao.setNome(tblIdentificacao.getNome());
        identificacao.setNumeroAluno(tblIdentificacao.getIdAluno().toString());

        final NumeroID numeroID = factory.createNumeroID();
        numeroID.setValue(tblIdentificacao.getNumId());
        numeroID.setTipo(Long.valueOf(tblIdentificacao.getTipoId()));
        numeroID.setDigitosControlo(tblIdentificacao.getCheckDigitId());

        identificacao.setNumeroID(numeroID);
        identificacao.setOutroTipoID(tblIdentificacao.getTipoIdDescr());
        identificacao.setPaisDeNacionalidade(tblIdentificacao.getNacionalidade());

        if (!Strings.isNullOrEmpty(tblIdentificacao.getOutroPaisDeNacionalidade())) {
            identificacao.setOutroPaisDeNacionalidade(tblIdentificacao.getOutroPaisDeNacionalidade());
        }

        if (!Strings.isNullOrEmpty(tblIdentificacao.getPaisEnsinoSecundario())) {
            identificacao.setPaisEnsinoSec(tblIdentificacao.getPaisEnsinoSecundario());
        } else {
            identificacao.setPaisEnsinoSec(Raides.Pais.OMISSAO);
        }

        identificacao.setPaisResidencia(tblIdentificacao.getResidePais());
        identificacao.setSexo(tblIdentificacao.getSexo());

        return identificacao;
    }

    protected static void fillExtracao(final RaidesRequestParameter raidesRequestParameter, ObjectFactory factory,
            final InformacaoAlunos informacaoAlunos) {
        final Extracao extracao = factory.createInformacaoAlunosExtracao();
        informacaoAlunos.setExtracao(extracao);

        extracao.setCodigoEstabelecimento(raidesRequestParameter.getInstitutionCode());
        extracao.setDataExtracao(new LocalDate());
        extracao.setEmailInterlocutor(raidesRequestParameter.getInterlocutorEmail());
        extracao.setMomento(Integer.valueOf(raidesRequestParameter.getMoment()));
        extracao.setNomeInterlocutor(raidesRequestParameter.getInterlocutorName());
        extracao.setTelefoneInterlocutor(longValueOf(raidesRequestParameter.getInterlocutorPhone()));
        extracao.setEmailInterlocutorAdicional(raidesRequestParameter.getInterlocutorEmail());
    }

    protected static Long longValueOf(final String value) {
        if (Strings.isNullOrEmpty(value)) {
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

    protected static DuracaoPrograma duracaoProgramaValueOf(TblMobilidadeInternacional tblMobilidadeInternacional) {
        if (Strings.isNullOrEmpty(tblMobilidadeInternacional.getDuracaoPrograma())) {
            return null;
        }

        return DuracaoPrograma.fromValue(tblMobilidadeInternacional.getDuracaoPrograma());
    }

    protected static Boolean booleanValueOf(final String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }

        return Boolean.valueOf(value);
    }

    protected static Integer integerValueOf(final String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }

        return Integer.valueOf(value);
    }

}
