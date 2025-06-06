package org.fenixedu.legalpt.services.raides.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.SchoolPeriodDuration;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilderForXLSX;
import org.fenixedu.legalpt.domain.raides.Raides;
import org.fenixedu.legalpt.domain.raides.TblDiplomado;
import org.fenixedu.legalpt.domain.raides.TblIdentificacao;
import org.fenixedu.legalpt.domain.raides.TblInscrito;
import org.fenixedu.legalpt.domain.raides.TblMobilidadeInternacional;
import org.fenixedu.legalpt.domain.report.LegalReportRequest;
import org.fenixedu.legalpt.domain.report.LegalReportResultFile;
import org.fenixedu.legalpt.domain.report.LegalReportResultFileType;
import org.fenixedu.legalpt.util.LegalPTUtil;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class XlsxExporter {

    public static LegalReportResultFile write(final LegalReportRequest reportRequest, final Raides raides) {

        final SheetData<TblIdentificacao> identificationData = new SheetData<TblIdentificacao>(raides.getAllIdentifications()) {

            @Override
            protected void makeLine(final TblIdentificacao tblIdentificacao) {
                addCell("DataNascimento", tblIdentificacao.getDataNasc());
                addCell("DigitosControlo", tblIdentificacao.getCheckDigitId());
                addCell("Nome", tblIdentificacao.getNome());
                addCell("NumeroAluno", tblIdentificacao.getIdAluno());
                addCell("NumeroId", tblIdentificacao.getNumId());
                addCell("NumeroIdTipo", tblIdentificacao.getTipoId());
                addCell("OutroPaisDeNacionalidade", "");
                addCell("OutroTipoID", tblIdentificacao.getTipoIdDescr());
                addCell("PaisDeNacionalidade", tblIdentificacao.getNacionalidade());
                addCell("OutroPaisDeNacionalidade", tblIdentificacao.getOutroPaisDeNacionalidade());
                addCell("PaisEnsinoSec", tblIdentificacao.getPaisEnsinoSecundario());
                addCell("PaisResidencia", tblIdentificacao.getResidePais());
                addCell("Sexo", tblIdentificacao.getSexo());
            }
        };

        final SheetData<TblInscrito> inscritoData = new SheetData<TblInscrito>(raides.getAllInscritos()) {

            @Override
            protected void makeLine(final TblInscrito tblInscrito) {
                addCell("IdAluno", tblInscrito.getIdAluno());
                addCell("Curso", tblInscrito.getCurso());
                addCell("Ramo", tblInscrito.getRamo());
                addCell("AnoLetivo", tblInscrito.getAnoLectivo());
                addCell("AnoCurricular", tblInscrito.getAnoCurricular());
                addCell("PrimeiraVez", tblInscrito.getPrimeiraVez());
                addCell("RegimeFrequencia", tblInscrito.getRegimeFrequencia());
                addCell("NumInscNesteCurso",
                        tblInscrito.getNumInscNesteCurso() != null ? (int) tblInscrito.getNumInscNesteCurso() : "");
                addCell("ECTSInscricao", tblInscrito.getEctsInscricao() != null ? tblInscrito.getEctsInscricao().toString() : "");
                addCell("ECTSAcumulados",
                        tblInscrito.getEctsAcumulados() != null ? tblInscrito.getEctsAcumulados().toString() : "");
                addCell("TempoParcial", tblInscrito.getTempoParcial());
                addCell("Bolseiro", tblInscrito.getBolseiro());
                addCell("FormaIngresso", tblInscrito.getFormaIngresso());
                addCell("EstabInscricaoAnt", tblInscrito.getEstabInscricaoAnt());
                addCell("OutroEstabInscAnt", tblInscrito.getOutroEstabInscAnt());
                addCell("NotaIngresso", tblInscrito.getNotaIngresso());
                addCell("OpcaoIngresso", tblInscrito.getOpcaoIngresso());
                addCell("NumInscCursosAnt", tblInscrito.getNumInscCursosAnt());
                addCell("AnoUltimaInscricao", tblInscrito.getAnoUltimaInscricao());
                addCell("EstadoCivil", tblInscrito.getEstadoCivil());
                addCell("TrabalhadorEstudante", tblInscrito.getEstudanteTrabalhador());
                addCell("AlunoDeslocado", tblInscrito.getAlunoDeslocado());
                addCell("Concelho", tblInscrito.getResideConcelho());
                addCell("NivelEscolarPai", tblInscrito.getNivelEscolarPai());
                addCell("NivelEscolarMae", tblInscrito.getNivelEscolarMae());
                addCell("SituacaoProfPai", tblInscrito.getSituacaoProfPai());
                addCell("SituacaoProfMae", tblInscrito.getSituacaoProfMae());
                addCell("SituacaoProfAluno", tblInscrito.getSituacaoProfAluno());
                addCell("ProfissaoPai", tblInscrito.getProfissaoPai());
                addCell("ProfissaoMae", tblInscrito.getProfissaoMae());
                addCell("ProfissaoAluno", tblInscrito.getProfissaoAluno());
                addCell("EscolaridadeAnterior", tblInscrito.getEscolaridadeAnterior());
                addCell("OutroEscolaridadeAnterior", tblInscrito.getOutroEscolaridadeAnterior());
                addCell("PaisEscolaridadeAnt", tblInscrito.getPaisEscolaridadeAnt());
                addCell("AnoEscolaridadeAnt", tblInscrito.getAnoEscolaridadeAnt());
                addCell("EstabEscolaridadeAnt", tblInscrito.getEstabEscolaridadeAnt());
                addCell("OutroEstabEscolarAnt", tblInscrito.getOutroEstabEscolarAnt());
                addCell("CursoEscolarAnt", tblInscrito.getCursoEscolarAnt());
                addCell("OutroCursoEscolarAnt", tblInscrito.getOutroCursoEscolarAnt());
                addCell("TipoEstabSec", tblInscrito.getTipoEstabSec());
            }

        };

        final SheetData<TblDiplomado> diplomadoData = new SheetData<TblDiplomado>(raides.getAllDiplomados()) {

            @Override
            protected void makeLine(final TblDiplomado tblDiplomado) {
                addCell("IdAluno", tblDiplomado.getIdAluno());
                addCell("Curso", tblDiplomado.getCurso());
                addCell("Ramo", tblDiplomado.getRamo());
                addCell("AnoLetivo", tblDiplomado.getAnoLectivo());
                addCell("AreaInvestigacao", tblDiplomado.getAreaInvestigacao());
                addCell("ConcluiGrau", tblDiplomado.getConcluiGrau());
                addCell("NumInscConclusao", tblDiplomado.getNumInscConclusao());
                addCell("ClassificacaoFinal", tblDiplomado.getClassificacaoFinal());
                addCell("DataDiploma",
                        tblDiplomado.getDataDiploma() != null ? tblDiplomado.getDataDiploma().toString(Raides.DATE_FORMAT) : "");
                addCell("ConclusaoMD", tblDiplomado.getConclusaoMd());
                addCell("ClassificacaoFinalMD", tblDiplomado.getClassificacaoFinalMd());
                addCell("MobilidadeCredito", tblDiplomado.getMobilidadeCredito());
                addCell("TipoMobilidadeCredito", tblDiplomado.getTipoMobilidadeCredito());
                addCell("ProgMobilidadeCredito", tblDiplomado.getProgMobilidadeCredito());
                addCell("OutroProgMobCredito", tblDiplomado.getOutroProgMobCredito());
                addCell("PaisMobilidadeCredito", tblDiplomado.getPaisMobilidadeCredito());
                addCell("EscolaridadeAnterior", tblDiplomado.getEscolaridadeAnterior());
                addCell("OutroEscolaridadeAnterior", tblDiplomado.getOutroEscolaridadeAnterior());
                addCell("PaisEscolaridadeAnt", tblDiplomado.getPaisEscolaridadeAnt());
                addCell("AnoEscolaridadeAnt", tblDiplomado.getAnoEscolaridadeAnt());
                addCell("EstabEscolaridadeAnt", tblDiplomado.getEstabEscolaridadeAnt());
                addCell("OutroEstabEscolarAnt", tblDiplomado.getOutroEstabEscolarAnt());
                addCell("CursoEscolarAnt", tblDiplomado.getCursoEscolarAnt());
                addCell("OutroCursoEscolarAnt", tblDiplomado.getOutroCursoEscolarAnt());
            }

        };

        final SheetData<TblMobilidadeInternacional> mobilidadeInternacionalData =
                new SheetData<TblMobilidadeInternacional>(raides.getAllMobilidadeInternacional()) {

                    @Override
                    protected void makeLine(final TblMobilidadeInternacional tblMobilidadeInternacional) {
                        addCell("IdAluno", tblMobilidadeInternacional.getIdAluno());
                        addCell("Curso", !Strings.isNullOrEmpty(
                                tblMobilidadeInternacional.getCurso()) ? tblMobilidadeInternacional.getCurso() : "");
                        addCell("Ramo", !Strings.isNullOrEmpty(
                                tblMobilidadeInternacional.getRamo()) ? tblMobilidadeInternacional.getRamo() : "");
                        addCell("AnoLetivo", tblMobilidadeInternacional.getAnoLectivo());
                        addCell("AreaCientifica", tblMobilidadeInternacional.getAreaCientifica());
                        addCell("AnoCurricular", tblMobilidadeInternacional.getAnoCurricular());
                        addCell("RegimeFrequencia", tblMobilidadeInternacional.getRegimeFrequencia());
                        addCell("ECTSInscricao", tblMobilidadeInternacional.getEctsInscrito());
                        addCell("ProgMobilidade", tblMobilidadeInternacional.getProgMobilidade());
                        addCell("OutroPrograma", tblMobilidadeInternacional.getOutroPrograma());
                        addCell("TipoProgMobilidade", tblMobilidadeInternacional.getTipoProgMobilidade());
                        addCell("DuracaoPrograma", tblMobilidadeInternacional.getDuracaoPrograma());
                        addCell("NivelCursoOrigem", tblMobilidadeInternacional.getNivelCursoOrigem());
                        addCell("PaisOrigemMobilidadeCredito", tblMobilidadeInternacional.getPaisOrigemMobilidadeCredito());
                        addCell("OutroNivelCurOrigem", tblMobilidadeInternacional.getOutroNivelCurOrigem());
                        addCell("AreaCientifica", tblMobilidadeInternacional.getAreaCientifica());
                        addCell("NivelCursoDestino", tblMobilidadeInternacional.getNivelCursoDestino());
                        addCell("OutroNivelCurDestino", tblMobilidadeInternacional.getOutroNivelCursoDestino());
                    }
                };

        final SheetData<Registration> precedentDegreeInformationData = new SheetData<Registration>(raides.getAllRegistrations()) {

            @Override
            protected void makeLine(final Registration registration) {
                addCell("Nº Aluno", registration.getStudent().getNumber());
                addCell(pdiLabel("executionYear"), registration.getRegistrationYear().getQualifiedName());
                addCell("Curso", registration.getDegree().getPresentationName());
                addCell("Acordo",
                        registration.getRegistrationProtocol() != null ? registration.getRegistrationProtocol().getDescription()
                                .getContent() : "");
                addCell("Ingresso",
                        registration.getStudentCandidacy().getIngressionType() != null ? registration.getIngressionType()
                                .getDescription().getContent() : "");
                addCell("Nome", registration.getStudent().getName());

                addCell("Reportar como Inscrito", raides.isInEnrolledData(registration));
                addCell("Reportar como Diplomado", raides.isInGraduated(registration));
                addCell("Reportar como Mobilidade", raides.isInInternacionalMobility(registration));

                final PrecedentDegreeInformation lastCompletedQualification = registration.getCompletedDegreeInformation();

                addCell(pdiLabel("schoolLevel"), lastCompletedQualification.getSchoolLevel() != null ? schoolLevelLocalizedName(
                        lastCompletedQualification.getSchoolLevel()) : "");
                addCell(pdiLabel("otherSchoolLevel"), lastCompletedQualification.getOtherSchoolLevel());
                addCell(pdiLabel("country"),
                        lastCompletedQualification.getCountry() != null ? lastCompletedQualification.getCountry().getCode() : "");
                addCell(pdiLabel("institution"),
                        lastCompletedQualification.getInstitution() != null ? lastCompletedQualification.getInstitution()
                                .getName() : "");
                addCell(pdiLabel("degreeDesignation"), lastCompletedQualification.getDegreeDesignation());
                addCell(pdiLabel("conclusionGrade"), lastCompletedQualification.getConclusionGrade());
                addCell(pdiLabel("conclusionYear"), lastCompletedQualification.getConclusionYear()
                        != null ? lastCompletedQualification.getConclusionYear() : "");

                final PrecedentDegreeInformation previousQualification = registration.getPreviousDegreeInformation();

                addCell(pdiLabel("precedentSchoolLevel"), previousQualification != null
                        && previousQualification.getSchoolLevel() != null ? schoolLevelLocalizedName(
                        previousQualification.getSchoolLevel()) : "");
                addCell(pdiLabel("otherPrecedentSchoolLevel"),
                        previousQualification != null ? previousQualification.getOtherSchoolLevel() : "");
                addCell(pdiLabel("precedentCountry"), previousQualification != null
                        && previousQualification.getCountry() != null ? previousQualification.getCountry().getCode() : "");
                addCell(pdiLabel("precedentInstitution"), previousQualification != null
                        && previousQualification.getInstitution() != null ? previousQualification.getInstitution()
                        .getName() : "");
                addCell(pdiLabel("precedentDegreeDesignation"),
                        previousQualification != null ? previousQualification.getDegreeDesignation() : "");
                addCell(pdiLabel("numberOfEnrolmentsInPreviousDegrees"), previousQualification != null
                        && previousQualification.getNumberOfEnrolmentsInPreviousDegrees()
                        != null ? previousQualification.getNumberOfEnrolmentsInPreviousDegrees() : "");

            }

        };

        final SheetData<Registration> personalIngressionDataSheetData =
                new SheetData<Registration>(raides.getAllRegistrations()) {

                    @Override
                    protected void makeLine(final Registration registration) {

                        final ExecutionYear currentYear = ExecutionYear.findCurrent(registration.getDegree().getCalendar());
                        registration.getStudent().getPersonalIngressionsDataSet().stream()
                                .filter(pid -> pid.getExecutionYear() == currentYear).forEach(pid -> {
                                    addCell("Nº Aluno", registration.getStudent().getNumber());
                                    addCell(pdiLabel("executionYear"), pid.getExecutionYear().getQualifiedName());

                                    addCell(pidLabel("countryOfResidence"),
                                            pid.getCountryOfResidence() != null ? pid.getCountryOfResidence().getName() : "");
                                    addCell(pidLabel("districtSubdivisionOfResidence"),
                                            pid.getDistrictSubdivisionOfResidence() != null ? pid.getDistrictSubdivisionOfResidence()
                                                    .getName() : "");
                                    addCell(pidLabel("dislocatedFromPermanentResidence"), pid.getDislocatedFromPermanentResidence()
                                            != null ? pid.getDislocatedFromPermanentResidence() : "");
                                    addCell(pidLabel("professionType"),
                                            pid.getProfessionType() != null ? professionTypeLocalizedName(pid.getProfessionType()) : "");
                                    addCell(pidLabel("professionalCondition"),
                                            pid.getProfessionalCondition() != null ? professionalSituationConditionTypeLocalizedName(
                                                    pid.getProfessionalCondition()) : "");
                                    addCell(pidLabel("motherSchoolLevel"), pid.getMotherSchoolLevel() != null ? schoolLevelLocalizedName(
                                            pid.getMotherSchoolLevel()) : "");
                                    addCell(pidLabel("motherProfessionType"),
                                            pid.getMotherProfessionType() != null ? professionTypeLocalizedName(
                                                    pid.getMotherProfessionType()) : "");
                                    addCell(pidLabel("motherProfessionalCondition"), pid.getMotherProfessionalCondition()
                                            != null ? professionalSituationConditionTypeLocalizedName(
                                            pid.getMotherProfessionalCondition()) : "");
                                    addCell(pidLabel("fatherSchoolLevel"), pid.getFatherSchoolLevel() != null ? schoolLevelLocalizedName(
                                            pid.getFatherSchoolLevel()) : "");
                                    addCell(pidLabel("fatherProfessionType"),
                                            pid.getFatherProfessionType() != null ? professionTypeLocalizedName(
                                                    pid.getFatherProfessionType()) : "");
                                    addCell(pidLabel("fatherProfessionalCondition"), pid.getFatherProfessionalCondition()
                                            != null ? professionalSituationConditionTypeLocalizedName(
                                            pid.getFatherProfessionalCondition()) : "");
                                });
                    }
                };

        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            final SpreadsheetBuilderForXLSX spreadsheetBuilder = new SpreadsheetBuilderForXLSX();

            spreadsheetBuilder.addSheet("Informacao Pessoal Complementar", personalIngressionDataSheetData);
            spreadsheetBuilder.addSheet("Graus Precedentes & Informacao Pessoal", precedentDegreeInformationData);
            spreadsheetBuilder.addSheet("Alunos Mobilidade Internacional", mobilidadeInternacionalData);
            spreadsheetBuilder.addSheet("Alunos Diplomados", diplomadoData);
            spreadsheetBuilder.addSheet("Alunos Inscritos", inscritoData);
            spreadsheetBuilder.addSheet("Alunos", identificationData);

            spreadsheetBuilder.build(outputStream);
            final byte[] content = outputStream.toByteArray();

            return writeFile(reportRequest, content);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("error.XlsxExporter.spreadsheet.generation.failed", e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("error.XlsxExporter.spreadsheet.generation.failed", e);
            }
        }

    }

    @Atomic(mode = TxMode.WRITE)
    private static LegalReportResultFile writeFile(final LegalReportRequest reportRequest, final byte[] content) {
        return new LegalReportResultFile(reportRequest, LegalReportResultFileType.XLSX, content);
    }

    protected static String pdiLabel(final String key) {
        return LegalPTUtil.bundle("org.fenixedu.academic.domain.student.PrecedentDegreeInformation." + key);
    }

    protected static String pidLabel(final String key) {
        return LegalPTUtil.bundle("label.org.fenixedu.academic.domain.student.PersonalIngressionData." + key);
    }

    protected static String schoolLevelLocalizedName(final SchoolLevelType schoolLevel) {
        return schoolLevel.getLocalizedName();
    }

    protected static String professionTypeLocalizedName(final ProfessionType profession) {
        return profession.getLocalizedName();
    }

    protected static String professionalSituationConditionTypeLocalizedName(
            final ProfessionalSituationConditionType conditionType) {
        return conditionType.getLocalizedName();
    }

    protected static String schoolPeriodDurationLocalizedName(final SchoolPeriodDuration duration) {
        return LegalPTUtil.bundle("label.SchoolPeriodDuration." + duration.name());
    }
}
