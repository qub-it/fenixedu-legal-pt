package org.fenixedu.legalpt.dto.a3es;

import java.util.Map;
import java.util.TreeMap;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.util.MultiLanguageString;

@SuppressWarnings({ "serial", "deprecation" })
public class A3esStudentsBean extends A3esAbstractBean {

    private TreeMap<String, String> studentsByGender;
    private TreeMap<String, String> studentsByAge;
    private Map<String, String> studentsByCurricularYear;
    private Map<ExecutionYear, Map<String, String>> demandAmongStudents;
    private MultiLanguageString studentsExtraInformation;
    private MultiLanguageString studentsSupport;
    private MultiLanguageString studentIntegrationMeasures;
    private MultiLanguageString financeAndEmploymentCounseling;
    private MultiLanguageString studentSurveysImpact;
    private MultiLanguageString mobilityStrategy;

    public TreeMap<String, String> getStudentsByGender() {
        return studentsByGender;
    }

    public void setStudentsByGender(TreeMap<String, String> studentsByGender) {
        this.studentsByGender = studentsByGender;
    }

    public TreeMap<String, String> getStudentsByAge() {
        return studentsByAge;
    }

    public void setStudentsByAge(TreeMap<String, String> studentsByAge) {
        this.studentsByAge = studentsByAge;
    }

    public Map<String, String> getStudentsByCurricularYear() {
        return studentsByCurricularYear;
    }

    public void setStudentsByCurricularYear(Map<String, String> studentsByCurricularYear) {
        this.studentsByCurricularYear = studentsByCurricularYear;
    }

    public Map<ExecutionYear, Map<String, String>> getDemandAmongStudents() {
        return demandAmongStudents;
    }

    public void setDemandAmongStudents(Map<ExecutionYear, Map<String, String>> demandAmongStudents) {
        this.demandAmongStudents = demandAmongStudents;
    }

    public MultiLanguageString getStudentsExtraInformation() {
        return studentsExtraInformation;
    }

    public void setStudentsExtraInformation(MultiLanguageString studentsExtraInformation) {
        this.studentsExtraInformation = studentsExtraInformation;
    }

    public MultiLanguageString getStudentsSupport() {
        return studentsSupport;
    }

    public void setStudentsSupport(MultiLanguageString studentsSupport) {
        this.studentsSupport = studentsSupport;
    }

    public MultiLanguageString getStudentIntegrationMeasures() {
        return studentIntegrationMeasures;
    }

    public void setStudentIntegrationMeasures(MultiLanguageString studentIntegrationMeasures) {
        this.studentIntegrationMeasures = studentIntegrationMeasures;
    }

    public MultiLanguageString getFinanceAndEmploymentCounseling() {
        return financeAndEmploymentCounseling;
    }

    public void setFinanceAndEmploymentCounseling(MultiLanguageString financeAndEmploymentCounseling) {
        this.financeAndEmploymentCounseling = financeAndEmploymentCounseling;
    }

    public MultiLanguageString getStudentSurveysImpact() {
        return studentSurveysImpact;
    }

    public void setStudentSurveysImpact(MultiLanguageString studentSurveysImpact) {
        this.studentSurveysImpact = studentSurveysImpact;
    }

    public MultiLanguageString getMobilityStrategy() {
        return mobilityStrategy;
    }

    public void setMobilityStrategy(MultiLanguageString mobilityStrategy) {
        this.mobilityStrategy = mobilityStrategy;
    }
}
