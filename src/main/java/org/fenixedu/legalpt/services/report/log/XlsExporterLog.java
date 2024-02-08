package org.fenixedu.legalpt.services.report.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.SchoolPeriodDuration;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilderForXLSX;
import org.fenixedu.legalpt.domain.LegalReportContext.LegalReportEntryData;
import org.fenixedu.legalpt.domain.ReportEntry;
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

                addCell(LegalPTUtil.bundle("label.RaidesRequests.header.excel.type"), reportEntry.getType() != null ? LegalPTUtil
                        .bundle("label.ReportEntryType." + reportEntry.getType().name()) : "");

                reportEntry.getTarget().asMap().entrySet().forEach(e -> addCell(e.getKey(), e.getValue()));

                addCell(LegalPTUtil.bundle("label.RaidesRequests.header.excel.message"), reportEntry.getMessage());
                addCell(LegalPTUtil.bundle("label.RaidesRequests.header.excel.action"), reportEntry.getAction());

            }
        };

        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            final SpreadsheetBuilderForXLSX spreadsheetBuilder = new SpreadsheetBuilderForXLSX();

            spreadsheetBuilder.addSheet(LegalPTUtil.bundle("label.RaidesRequests.sheet.name.excel"), compiledSheet);

            spreadsheetBuilder.build(outputStream);
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