package org.fenixedu.legalpt.services.report.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.SchoolPeriodDuration;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilderForXLSX;
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
import org.fenixedu.legalpt.domain.LegalReportContext.LegalReportEntryData;
import org.fenixedu.legalpt.domain.LegalReportContext.ReportEntry;
import org.fenixedu.legalpt.domain.LegalReportContext.ReportEntryType;
import org.fenixedu.legalpt.domain.report.LegalReportRequest;
import org.fenixedu.legalpt.domain.report.LegalReportResultFile;
import org.fenixedu.legalpt.domain.report.LegalReportResultFileType;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class XlsExporterLog {

    public static LegalReportResultFile write(final LegalReportRequest reportRequest,
            final LegalReportEntryData legalReportEntryData) {
        List<ReportEntry> compiledData = legalReportEntryData.getErrorEntries();
        compiledData.addAll(legalReportEntryData.getWarnEntries());
        compiledData.addAll(legalReportEntryData.getInfoEntries());

        final SheetData<ReportEntry> compiledSheet = new SheetData<ReportEntry>((Iterable<ReportEntry>) compiledData) {
            @Override
            protected void makeLine(final ReportEntry reportEntry) {

                String type = reportEntry.getType() == ReportEntryType.ERROR ? "Erro" : reportEntry
                        .getType() == ReportEntryType.WARN ? "Aviso" : reportEntry
                                .getType() == ReportEntryType.INFO ? "Informação" : "";

                addCell("Tipo", type);
                addCell("Assunto", reportEntry.getTarget());

                final String[] fields = reportEntry.getMessage().split(";");

                for (int i = 0; i < fields.length; i++) {
                    if (i == 1) {
                        addCell("Nº de Aluno", fields[i]);
                    } else if (i == 3) {
                        addCell("Nº de Matrícula", fields[i]);
                    } else if (i == 5) {
                        addCell("Código de Curso", fields[i]);
                    } else if (i == 7) {
                        addCell("Curso", fields[i]);
                    } else if (i == 9) {
                        addCell("Ano", fields[i]);
                    } else if (i == 10) {
                        addCell("Mensagem", fields[i]);
                    } else if (i > 10) {
                        addCell(String.format("[%d]", i), fields[i]);
                    }
                }

            }
        };

        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            final SpreadsheetBuilderForXLSX spreadsheetBuilder = new SpreadsheetBuilderForXLSX();

            spreadsheetBuilder.addSheet("Erros | Avisos | Informações", compiledSheet);

            spreadsheetBuilder.build(WorkbookExportFormat.EXCEL, outputStream);
            final byte[] content = outputStream.toByteArray();

            final String fileName = "Logs_" + reportRequest.getLegalReport().getName().getContent() + "_"
                    + new DateTime().toString("dd-MM-yyyy-HH-mm") + "." + LegalReportResultFileType.XLSX.toString().toLowerCase();

            return writeToFile(reportRequest, content, fileName);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error.XlsxExporter.spreadsheet.generation.failed", e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("error.XlsxExporter.spreadsheet.generation.failed", e);
            }
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private static LegalReportResultFile writeToFile(final LegalReportRequest reportRequest, final byte[] content,
            final String fileName) {
        return new LegalReportResultFile(reportRequest, LegalReportResultFileType.XLSX, fileName, content);
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