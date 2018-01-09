package org.fenixedu.legalpt.dto.rebides;

import java.io.Serializable;

import org.joda.time.LocalDate;

public class IdentificationBean implements Serializable {
    
    static final long serialVersionUID = 1L;

    protected String name;
    protected String documentIdNumber;
    protected String documentIdType;
    protected String otherIdDocumentType;
    protected LocalDate dateOfBirth;
    protected String gender;
    protected String nationalityCountry;
    protected String otherNationalityCountry;

    /*
     * GETTERS & SETTERS
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentIdNumber() {
        return documentIdNumber;
    }

    public void setDocumentIdNumber(String idDocumentNumber) {
        this.documentIdNumber = idDocumentNumber;
    }

    public String getDocumentIdType() {
        return documentIdType;
    }

    public void setDocumentIdType(String idDocumentType) {
        this.documentIdType = idDocumentType;
    }

    public String getOtherIdDocumentType() {
        return otherIdDocumentType;
    }

    public void setOtherIdDocumentType(String otherIdDocumentType) {
        this.otherIdDocumentType = otherIdDocumentType;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationalityCountry() {
        return nationalityCountry;
    }

    public void setNationalityCountry(String nationalityCountry) {
        this.nationalityCountry = nationalityCountry;
    }

    public String getOtherNationalityCountry() {
        return otherNationalityCountry;
    }

    public void setOtherNationalityCountry(String otherNationalityCountry) {
        this.otherNationalityCountry = otherNationalityCountry;
    }

}
