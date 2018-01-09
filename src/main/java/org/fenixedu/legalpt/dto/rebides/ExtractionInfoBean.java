package org.fenixedu.legalpt.dto.rebides;

import java.io.Serializable;

import org.joda.time.LocalDate;


public class ExtractionInfoBean implements Serializable {
    
    static final long serialVersionUID = 1L;
    
    protected String institutionCode;
    protected String moment;
    protected LocalDate extractionDate;
    protected String interlocutorName;
    protected String interlocutorEmail;
    protected String interlocutorPhone;

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

    public LocalDate getExtractionDate() {
        return extractionDate;
    }

    public void setExtractionDate(LocalDate extractionDate) {
        this.extractionDate = extractionDate;
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

}
