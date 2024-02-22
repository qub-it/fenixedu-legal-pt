package org.fenixedu.legalpt.domain;

import org.fenixedu.legalpt.domain.LegalReportContext.ReportEntryType;
import org.joda.time.DateTime;

public class ReportEntry {

    private final ReportEntryType type;
    private final ReportEntryTarget target;
    private final DateTime reportDate;
    private final String message;
    private final String action;

    public ReportEntry(ReportEntryType type, ReportEntryTarget target, String message, String action) {
        this.type = type;
        this.target = target;
        this.reportDate = new DateTime();
        this.message = message;
        this.action = action;
    }

    public ReportEntryType getType() {
        return type;
    }

    public ReportEntryTarget getTarget() {
        return target;
    }

    public DateTime getReportDate() {
        return reportDate;
    }

    public String getMessage() {
        return message;
    }

    public String getAction() {
        return action;
    }

}