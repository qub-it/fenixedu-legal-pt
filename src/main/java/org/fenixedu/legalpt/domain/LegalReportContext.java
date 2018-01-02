package org.fenixedu.legalpt.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

public class LegalReportContext {

    public static String SEPARATOR_SUBJECT = " | ";

    public static enum ReportEntryType {
        INFO, ERROR, WARN;
    }

    private static final InheritableThreadLocal<LegalReportEntryData> reportHolder =
            new InheritableThreadLocal<LegalReportEntryData>();

    public static void init() {
        reportHolder.set(new LegalReportEntryData());
    }

    public static void destroy() {
        reportHolder.set(null);
    }

    public static void addInfo(Object target, String message, String... args) {
        getReport().addEntry(new ReportEntry(ReportEntryType.INFO, target, message, args));
    }

    public static void addError(Object target, String message, String... args) {
        getReport().addEntry(new ReportEntry(ReportEntryType.ERROR, target, message, args));
    }

    public static void addWarn(Object target, String message, String... args) {
        getReport().addEntry(new ReportEntry(ReportEntryType.WARN, target, message, args));
    }

    public static LegalReportEntryData getReport() {
        ensureContext();
        return reportHolder.get();
    }

    private static void ensureContext() {
        if (reportHolder.get() == null) {
            throw new RuntimeException(
                    "Report context is not available. Make sure you are running inside a Legal Report context.");
        }
    }

    public static class ReportEntry {

        private final ReportEntryType type;
        private final Object target;
        private final DateTime reportDate;
        private final String message;
        private final String[] messageArgs;

        public ReportEntry(ReportEntryType type, Object target, String message, String... args) {
            this.type = type;
            this.target = target;
            this.reportDate = new DateTime();
            this.message = message;
            this.messageArgs = args;
        }

        public ReportEntryType getType() {
            return type;
        }

        public Object getTarget() {
            return target;
        }

        public DateTime getReportDate() {
            return reportDate;
        }

        public String getMessage() {
            return message;
        }

        public String[] getMessageArgs() {
            return messageArgs;
        }
    }

    public static class LegalReportEntryData {

        private final List<ReportEntry> entries = new ArrayList<ReportEntry>();

        public void addEntry(ReportEntry entry) {
            this.entries.add(entry);
        }

        public List<ReportEntry> getEntries() {
            return entries;
        }

        public List<ReportEntry> getErrorEntries() {
            return entries.stream().filter(e -> e.getType() == ReportEntryType.ERROR).collect(Collectors.toList());
        }

        public List<ReportEntry> getWarnEntries() {
            return entries.stream().filter(e -> e.getType() == ReportEntryType.WARN).collect(Collectors.toList());
        }

        public List<ReportEntry> getInfoEntries() {
            return entries.stream().filter(e -> e.getType() == ReportEntryType.INFO).collect(Collectors.toList());
        }

        public List<ReportEntry> getEntries(Object target) {
            return entries.stream().filter(e -> e.getTarget() == target).collect(Collectors.toList());
        }

        public List<ReportEntry> getErrorEntries(Object target) {
            return entries.stream().filter(e -> e.getTarget() == target && e.getType() == ReportEntryType.ERROR)
                    .collect(Collectors.toList());
        }

        public List<ReportEntry> getWarnEntries(Object target) {
            return entries.stream().filter(e -> e.getTarget() == target && e.getType() == ReportEntryType.WARN)
                    .collect(Collectors.toList());
        }

        public List<ReportEntry> getInfoEntries(Object target) {
            return entries.stream().filter(e -> e.getTarget() == target && e.getType() == ReportEntryType.INFO)
                    .collect(Collectors.toList());
        }

        public void clear() {
            this.entries.clear();
        }
    }

}
