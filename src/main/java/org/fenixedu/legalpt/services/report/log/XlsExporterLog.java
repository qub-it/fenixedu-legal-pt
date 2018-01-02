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
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
import org.fenixedu.legalpt.domain.LegalReportContext.LegalReportEntryData;
import org.fenixedu.legalpt.domain.LegalReportContext.ReportEntry;
import org.fenixedu.legalpt.domain.LegalReportContext.ReportEntryType;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequest;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFile;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFileType;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.DateTime;

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
            final SpreadsheetBuilder spreadsheetBuilder = new SpreadsheetBuilder();

            spreadsheetBuilder.addSheet("Erros | Avisos | Informações", compiledSheet);

            spreadsheetBuilder.build(WorkbookExportFormat.EXCEL, outputStream);
            final byte[] content = outputStream.toByteArray();

            final String fileName = "Logs_" + reportRequest.getLegalReport().getName().getContent() + "_"
                    + new DateTime().toString("dd-MM-yyyy-HH-mm") + "." + LegalReportResultFileType.XLS.toString().toLowerCase();

            return new LegalReportResultFile(reportRequest, LegalReportResultFileType.XLS, fileName, content);
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
        return ULisboaSpecificationsUtil.bundle("org.fenixedu.academic.domain.student.PrecedentDegreeInformation." + key);
    }

    protected static String pidLabel(final String key) {
        return ULisboaSpecificationsUtil.bundle("label.org.fenixedu.academic.domain.student.PersonalIngressionData." + key);
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
        return ULisboaSpecificationsUtil.bundle("label.SchoolPeriodDuration." + duration.name());
    }

}