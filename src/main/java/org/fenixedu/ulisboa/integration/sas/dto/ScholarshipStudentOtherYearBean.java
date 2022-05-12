package org.fenixedu.ulisboa.integration.sas.dto;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

@SuppressWarnings("serial")
public class ScholarshipStudentOtherYearBean extends AbstractScholarshipStudentBean {

    public static Integer INSTITUTION_CODE = 0;
    public static Integer INSTITUTION_NAME = 1;
    public static Integer CANDIDACY_NUMBER = 2;
    public static Integer STUDENT_NUMBER = 3;
    public static Integer STUDENT_NAME = 4;
    public static Integer DOCUMENT_TYPE_NAME = 5;
    public static Integer DOCUMENT_NUMBER = 6;
    public static Integer DEGREE_CODE = 7;
    public static Integer DEGREE_NAME = 8;
    public static Integer DEGREE_TYPE_NAME = 9;
    public static Integer CODE = 10;
    public static Integer COUNT_NUMBER_OF_DEGREE_CHANGES = 11;
    public static Integer CURRENT_YEAR_HAS_MADE_DEGREE_CHANGE = 12;
    public static Integer REGISTERED = 13;
    public static Integer REGISTRATION_DATE = 14;

    public static Integer REGIME = 15;
    public static Integer CODE1 = 16;//not filled
    public static Integer CYCLE_INGRESSION_YEAR = 17;
    public static Integer CYCLE_NUMBER_OF_ENROLMENT_YEARS = 18;
    public static Integer CYCLE_COUNT_NUMBER_OF_ENROLMENTS_YEARS_IN_INTEGRAL_REGIME = 19;
    public static Integer NUMBER_OF_APPROVED_ECTS = 20;
    public static Integer NUMBER_OF_YEARS_DEGREE = 21;
    public static Integer LAST_ENROLMENT_CURRICULAR_YEAR = 22;
    public static Integer NUMBER_OF_ENROLLED_ECTS_LAST_YEAR = 23;
    public static Integer NUMBER_OF_APPROVED_ECTS_LAST_YEAR = 24;
    public static Integer CURRICULAR_YEAR = 25;
    public static Integer NUMBER_OF_ECTS = 26;
    public static Integer GRATUITY = 27;
    public static Integer NUMBER_OF_MONTHS_EXECUTION_YEAR = 28;
    public static Integer FIRST_MONTH_EXECUTION_YEAR = 29;
    public static Integer OWNER_CET = 30;
    public static Integer OWNER_CSTP = 31;
    public static Integer OWNER_BACHELOR = 32;
    public static Integer OWNER_MASTER = 33;
    public static Integer OWNER_PHD = 34;
    public static Integer OWNER_OF_HIGHER_QUALIFICATION = 35;
    public static Integer OBSERVATIONS = 36;
    public static Integer LAST_ENROLMENT_EXECUTION_YEAR = 37;
    public static Integer FISCAL_CODE = 38;//not filled
    public static Integer LAST_ACADEMIC_ACT_DATE_LAST_YEAR = 39;
    public static Integer DOCUMENT_BI = 40;
    public static Integer INGRESSION_REGIME = 41;

    public static Integer CONTRACTUALISATION_NUMBER_OF_ECTS = 15;
    public static Integer CONTRACTUALISATION_REGIME = 16;
    public static Integer CONTRACTUALISATION_CODE1 = 17;//not filled
    public static Integer CONTRACTUALISATION_GRATUITY = 18;
    public static Integer CONTRACTUALISATION_NUMBER_OF_MONTHS_EXECUTION_YEAR = 19;
    public static Integer CONTRACTUALISATION_FIRST_MONTH_EXECUTION_YEAR = 20;
    public static Integer CONTRACTUALISATION_FISCAL_CODE = 21; //not filled
    public static Integer CONTRACTUALISATION_DOCUMENT_BI = 22;

    private Integer numberOfDegreeChanges;
    private Boolean hasMadeDegreeChangeOnCurrentYear;
    private Integer cycleNumberOfEnrolmentsYearsInIntegralRegime;
    private Integer lastEnrolmentCurricularYear;
    private BigDecimal numberOfEnrolledEctsLastYear;
    private BigDecimal numberOfApprovedEctsLastYear;
    private BigDecimal numberOfApprovedEcts;
    private Integer lastEnrolmentYear;
    private LocalDate lastAcademicActDateLastYear;

    public Integer getNumberOfDegreeChanges() {
        return numberOfDegreeChanges;
    }

    public void setNumberOfDegreeChanges(Integer numberOfDegreeChanges) {
        this.numberOfDegreeChanges = numberOfDegreeChanges;
    }

    public Integer getCycleNumberOfEnrolmentsYearsInIntegralRegime() {
        return cycleNumberOfEnrolmentsYearsInIntegralRegime;
    }

    public void setCycleNumberOfEnrolmentsYearsInIntegralRegime(Integer numberOfEnrolmentsYearsInIntegralRegime) {
        this.cycleNumberOfEnrolmentsYearsInIntegralRegime = numberOfEnrolmentsYearsInIntegralRegime;
    }

    public Boolean getHasMadeDegreeChangeOnCurrentYear() {
        return hasMadeDegreeChangeOnCurrentYear;
    }

    public void setHasMadeDegreeChangeOnCurrentYear(Boolean hasMadeDegreeChangeOnCurrentYear) {
        this.hasMadeDegreeChangeOnCurrentYear = hasMadeDegreeChangeOnCurrentYear;
    }

    public Integer getLastEnrolmentCurricularYear() {
        return lastEnrolmentCurricularYear;
    }

    public void setLastEnrolmentCurricularYear(Integer lastEnrolmentCurricularYear) {
        this.lastEnrolmentCurricularYear = lastEnrolmentCurricularYear;
    }

    public BigDecimal getNumberOfEnrolledEctsLastYear() {
        return numberOfEnrolledEctsLastYear;
    }

    public void setNumberOfEnrolledEctsLastYear(BigDecimal numberOfEnrolledEctsLastYear) {
        this.numberOfEnrolledEctsLastYear = numberOfEnrolledEctsLastYear;
    }

    public BigDecimal getNumberOfApprovedEctsLastYear() {
        return numberOfApprovedEctsLastYear;
    }

    public void setNumberOfApprovedEctsLastYear(BigDecimal numberOfApprovedEctsLastYear) {
        this.numberOfApprovedEctsLastYear = numberOfApprovedEctsLastYear;
    }

    public BigDecimal getNumberOfApprovedEcts() {
        return numberOfApprovedEcts;
    }

    public void setNumberOfApprovedEcts(BigDecimal numberOfApprovedEcts) {
        this.numberOfApprovedEcts = numberOfApprovedEcts;
    }

    public LocalDate getLastAcademicActDateLastYear() {
        return lastAcademicActDateLastYear;
    }

    public void setLastAcademicActDateLastYear(LocalDate lastAcademicActDateLastYear) {
        this.lastAcademicActDateLastYear = lastAcademicActDateLastYear;
    }

    public Integer getLastEnrolmentYear() {
        return lastEnrolmentYear;
    }

    public void setLastEnrolmentYear(Integer lastEnrolmentYear) {
        this.lastEnrolmentYear = lastEnrolmentYear;
    }

}
