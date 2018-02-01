package org.fenixedu.legalpt.services.raides.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.SchoolPeriodDuration;
import org.fenixedu.academic.domain.candidacy.PersonalInformationBean;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblDiplomado;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblIdentificacao;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblInscrito;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblMobilidadeInternacional;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequest;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFile;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFileType;

import com.google.common.base.Strings;

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
                        addCell("Curso",
                                !Strings.isNullOrEmpty(tblMobilidadeInternacional.getCurso()) ? tblMobilidadeInternacional
                                        .getCurso() : "");
                        addCell("Ramo", !Strings.isNullOrEmpty(tblMobilidadeInternacional.getRamo()) ? tblMobilidadeInternacional
                                .getRamo() : "");
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
                        addCell("OutroNivelCurOrigem", tblMobilidadeInternacional.getOutroNivelCurOrigem());
                        addCell("AreaCientifica", tblMobilidadeInternacional.getAreaCientifica());
                        addCell("NivelCursoDestino", tblMobilidadeInternacional.getNivelCursoDestino());
                        addCell("OutroNivelCurDestino", tblMobilidadeInternacional.getOutroNivelCursoDestino());
                    }
                };

        final SheetData<PersonalInformationBean> precedentDegreeInformationData =
                new SheetData<PersonalInformationBean>(raides.getAllPrecedentDegreeInformations()) {

                    @Override
                    protected void makeLine(final PersonalInformationBean personalInformationBean) {
                        addCell("Nº Aluno", personalInformationBean.getRegistration().getStudent().getNumber());
                        addCell(pdiLabel("executionYear"), personalInformationBean.getRegistration().getStudentCandidacy()
                                .getExecutionYear().getQualifiedName());
                        addCell("Curso", personalInformationBean.getRegistration().getDegree().getPresentationName());
                        addCell("Acordo",
                                personalInformationBean.getRegistration()
                                        .getRegistrationProtocol() != null ? personalInformationBean.getRegistration()
                                                .getRegistrationProtocol().getDescription().getContent() : "");
                        addCell("Ingresso",
                                personalInformationBean.getRegistration().getStudentCandidacy()
                                        .getIngressionType() != null ? personalInformationBean.getRegistration()
                                                .getIngressionType().getDescription().getContent() : "");
                        addCell("Nome", personalInformationBean.getRegistration().getStudent().getName());

                        addCell("Reportar como Inscrito", raides.isInEnrolledData(personalInformationBean.getRegistration()));
                        addCell("Reportar como Diplomado", raides.isInGraduated(personalInformationBean.getRegistration()));
                        addCell("Reportar como Mobilidade",
                                raides.isInInternacionalMobility(personalInformationBean.getRegistration()));

                        final PrecedentDegreeInformation lastCompletedQualification =
                                personalInformationBean.getRegistration().getStudentCandidacy().getPrecedentDegreeInformation();

                        addCell(pdiLabel("schoolLevel"),
                                lastCompletedQualification.getSchoolLevel() != null ? schoolLevelLocalizedName(
                                        lastCompletedQualification.getSchoolLevel()) : "");
                        addCell(pdiLabel("otherSchoolLevel"), lastCompletedQualification.getOtherSchoolLevel());
                        addCell(pdiLabel("country"), lastCompletedQualification.getCountry() != null ? lastCompletedQualification
                                .getCountry().getCode() : "");
                        addCell(pdiLabel("institution"),
                                lastCompletedQualification.getInstitution() != null ? lastCompletedQualification.getInstitution()
                                        .getName() : "");
                        addCell(pdiLabel("degreeDesignation"), lastCompletedQualification.getDegreeDesignation());
                        addCell(pdiLabel("conclusionGrade"), lastCompletedQualification.getConclusionGrade());
                        addCell(pdiLabel("conclusionYear"),
                                lastCompletedQualification.getConclusionYear() != null ? lastCompletedQualification
                                        .getConclusionYear() : "");

                        final PrecedentDegreeInformation previousQualification =
                                personalInformationBean.getRegistration().getStudentCandidacy().getPrecedentDegreeInformation();

                        addCell(pdiLabel("precedentSchoolLevel"),
                                previousQualification != null
                                        && previousQualification.getPrecedentSchoolLevel() != null ? schoolLevelLocalizedName(
                                                previousQualification.getPrecedentSchoolLevel()) : "");
                        addCell(pdiLabel("otherPrecedentSchoolLevel"),
                                previousQualification != null ? previousQualification.getOtherPrecedentSchoolLevel() : "");
                        addCell(pdiLabel("precedentCountry"),
                                previousQualification != null
                                        && previousQualification.getPrecedentCountry() != null ? previousQualification
                                                .getPrecedentCountry().getCode() : "");
                        addCell(pdiLabel("precedentInstitution"),
                                previousQualification != null
                                        && previousQualification.getPrecedentInstitution() != null ? previousQualification
                                                .getPrecedentInstitution().getName() : "");
                        addCell(pdiLabel("precedentDegreeDesignation"),
                                previousQualification != null ? previousQualification.getPrecedentDegreeDesignation() : "");
                        addCell(pdiLabel("numberOfEnrolmentsInPreviousDegrees"), previousQualification != null
                                && previousQualification.getNumberOfEnrolmentsInPreviousDegrees() != null ? previousQualification
                                        .getNumberOfEnrolmentsInPreviousDegrees() : "");

                    }

                };

        final SheetData<PersonalInformationBean> personalIngressionDataSheetData =
                new SheetData<PersonalInformationBean>(raides.getAllPrecedentDegreeInformations()) {

                    @Override
                    protected void makeLine(final PersonalInformationBean personalInformationBean) {

                        Set<PersonalIngressionData> personalIngressionsDataSet =
                                personalInformationBean.getRegistration().getStudent().getPersonalIngressionsDataSet();
                        for (final PersonalIngressionData personalIngressionData : personalIngressionsDataSet) {

                            addCell("Nº Aluno", personalInformationBean.getRegistration().getStudent().getNumber());
                            addCell(pdiLabel("executionYear"), personalIngressionData.getExecutionYear().getQualifiedName());

                            addCell(pidLabel("countryOfResidence"),
                                    personalIngressionData.getCountryOfResidence() != null ? personalIngressionData
                                            .getCountryOfResidence().getName() : "");
                            addCell(pidLabel("districtSubdivisionOfResidence"),
                                    personalIngressionData.getDistrictSubdivisionOfResidence() != null ? personalIngressionData
                                            .getDistrictSubdivisionOfResidence().getName() : "");
                            addCell(pidLabel("dislocatedFromPermanentResidence"),
                                    personalIngressionData.getDislocatedFromPermanentResidence() != null ? personalIngressionData
                                            .getDislocatedFromPermanentResidence() : "");
                            addCell(pidLabel("professionType"),
                                    personalIngressionData.getProfessionType() != null ? professionTypeLocalizedName(
                                            personalIngressionData.getProfessionType()) : "");
                            addCell(pidLabel("professionalCondition"),
                                    personalIngressionData
                                            .getProfessionalCondition() != null ? professionalSituationConditionTypeLocalizedName(
                                                    personalIngressionData.getProfessionalCondition()) : "");
                            addCell(pidLabel("motherSchoolLevel"),
                                    personalIngressionData.getMotherSchoolLevel() != null ? schoolLevelLocalizedName(
                                            personalIngressionData.getMotherSchoolLevel()) : "");
                            addCell(pidLabel("motherProfessionType"),
                                    personalIngressionData.getMotherProfessionType() != null ? professionTypeLocalizedName(
                                            personalIngressionData.getMotherProfessionType()) : "");
                            addCell(pidLabel("motherProfessionalCondition"),
                                    personalIngressionData
                                            .getMotherProfessionalCondition() != null ? professionalSituationConditionTypeLocalizedName(
                                                    personalIngressionData.getMotherProfessionalCondition()) : "");
                            addCell(pidLabel("fatherSchoolLevel"),
                                    personalIngressionData.getFatherSchoolLevel() != null ? schoolLevelLocalizedName(
                                            personalIngressionData.getFatherSchoolLevel()) : "");
                            addCell(pidLabel("fatherProfessionType"),
                                    personalIngressionData.getFatherProfessionType() != null ? professionTypeLocalizedName(
                                            personalIngressionData.getFatherProfessionType()) : "");
                            addCell(pidLabel("fatherProfessionalCondition"),
                                    personalIngressionData
                                            .getFatherProfessionalCondition() != null ? professionalSituationConditionTypeLocalizedName(
                                                    personalIngressionData.getFatherProfessionalCondition()) : "");
                        }

                    }
                };

        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            final SpreadsheetBuilder spreadsheetBuilder = new SpreadsheetBuilder();

            spreadsheetBuilder.addSheet("Informacao Pessoal Complementar", personalIngressionDataSheetData);
            spreadsheetBuilder.addSheet("Graus Precedentes & Informacao Pessoal", precedentDegreeInformationData);
            spreadsheetBuilder.addSheet("Alunos Mobilidade Internacional", mobilidadeInternacionalData);
            spreadsheetBuilder.addSheet("Alunos Diplomados", diplomadoData);
            spreadsheetBuilder.addSheet("Alunos Inscritos", inscritoData);
            spreadsheetBuilder.addSheet("Alunos", identificationData);

            spreadsheetBuilder.build(WorkbookExportFormat.EXCEL, outputStream);
            final byte[] content = outputStream.toByteArray();

            return new LegalReportResultFile(reportRequest, LegalReportResultFileType.XLS, content);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ULisboaSpecificationsDomainException(e, "error.XlsxExporter.spreadsheet.generation.failed");
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ULisboaSpecificationsDomainException(e, "error.XlsxExporter.spreadsheet.generation.failed");
            }
        }

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
