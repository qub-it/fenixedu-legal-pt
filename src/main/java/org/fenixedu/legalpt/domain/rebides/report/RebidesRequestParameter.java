package org.fenixedu.legalpt.domain.rebides.report;

import java.io.Serializable;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.IBean;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequestParameters;

public class RebidesRequestParameter extends LegalReportRequestParameters implements Serializable, IBean {

    protected String institutionCode;
    protected String moment;
    protected ExecutionYear executionYear;
    protected String interlocutorName;
    protected String interlocutorEmail;
    protected String interlocutorPhone;
    protected boolean filterEntriesWithErrors;

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getMoment() {
        return moment;
    }

    public void setMoment(String moment) {
        this.moment = moment;
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public String getInterlocutorName() {
        return interlocutorName;
    }

    public void setInterlocutorName(String interlocutorName) {
        this.interlocutorName = interlocutorName;
    }

    public String getInterlocutorEmail() {
        return interlocutorEmail;
    }

    public void setInterlocutorEmail(String interlocutorEmail) {
        this.interlocutorEmail = interlocutorEmail;
    }

    public String getInterlocutorPhone() {
        return interlocutorPhone;
    }

    public void setInterlocutorPhone(String interlocutorPhone) {
        this.interlocutorPhone = interlocutorPhone;
    }

    public boolean isFilterEntriesWithErrors() {
        return filterEntriesWithErrors;
    }

    public void setFilterEntriesWithErrors(boolean filterEntriesWithErrors) {
        this.filterEntriesWithErrors = filterEntriesWithErrors;
    }

}
