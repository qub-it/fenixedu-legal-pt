package org.fenixedu.legalpt.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static void addInfo(ReportEntryTarget target, String message) {
        getReport().addEntry(new ReportEntry(ReportEntryType.INFO, target, message, null));
    }

    @Deprecated
    public static void addError(ReportEntryTarget target, String message) {
        addError(target, message, null);
    }
    
    public static void addError(ReportEntryTarget target, String message, String action) {
        getReport().addEntry(new ReportEntry(ReportEntryType.ERROR, target, message, action));
    }

    public static void addWarn(ReportEntryTarget target, String message) {
        getReport().addEntry(new ReportEntry(ReportEntryType.WARN, target, message, null));
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
