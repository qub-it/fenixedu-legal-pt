package org.fenixedu.legalpt.services.rebides.process;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.rebides.mapping.RebidesMappingType;
import org.fenixedu.legalpt.dto.rebides.IdentificationBean;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class IdentificationService {

    public static class IdDocument {
        public static final String OTHER = "7";
    }

    private final Teacher teacher;
    private final ExecutionYear executionYear;
    private LegalReport report;

    public IdentificationService(final LegalReport report, Teacher teacher, ExecutionYear executionYear) {
        this.report = report;
        this.teacher = teacher;
        this.executionYear = executionYear;
    }

    public IdentificationBean getIdentificationData() {
        final IdentificationBean bean = new IdentificationBean();
        fillTeacherName(bean); // required
        fillTeacherDocumentIdNumber(bean); // required
        fillTeacherDocumentIdType(bean); // required
        fillTeacherOtherIdDocumentType(bean); // required
        fillTeacherDateOfBirth(bean); // required
        fillTeacherGender(bean); // required
        fillTeacherNationalityCountry(bean); // required
        fillTeacherOtherNationalityCountry(bean); // optional

        return bean;
    }

    private void fillTeacherName(IdentificationBean bean) {
        String name = teacher.getPerson().getName();
        if (StringUtils.isNotEmpty(name)) {
            if (RebidesService.validateMaxFieldSize(teacher, "Person.name", name, RebidesService.LIMIT_80CHARS)) {
                bean.setName(name);
            }
        } else {
            LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
                    RebidesService.createMissingFieldMessage("Person.name"));
        }
    }

    private void fillTeacherDocumentIdNumber(IdentificationBean bean) {
        String documentIdNumber = teacher.getPerson().getDocumentIdNumber();
        if (StringUtils.isNotEmpty(documentIdNumber)) {
            if (RebidesService.validateMaxFieldSize(teacher, "Person.documentIdNumber", documentIdNumber,
                    RebidesService.LIMIT_20CHARS)) {
                bean.setDocumentIdNumber(documentIdNumber);
            }
        } else {
            LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
                    RebidesService.createMissingFieldMessage("Person.documentIdNumber"));
        }
    }

    private void fillTeacherDocumentIdType(IdentificationBean bean) {
        if (teacher.getPerson().getIdDocumentType() != null) {
            if (LegalMapping.find(report, RebidesMappingType.ID_DOCUMENT_TYPE)
                    .translate(teacher.getPerson().getIdDocumentType()) != null) {
                bean.setDocumentIdType(LegalMapping.find(report, RebidesMappingType.ID_DOCUMENT_TYPE)
                        .translate(teacher.getPerson().getIdDocumentType()));
            } else {
                LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
                        RebidesService.createMissingMappingMessage("Person.idDocumentType",
                                teacher.getPerson().getIdDocumentType().getLocalizedName()));
            }
        } else {
            LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
                    RebidesService.createMissingFieldMessage("Person.idDocumentType"));
        }
    }

    private void fillTeacherOtherIdDocumentType(IdentificationBean bean) {
        if (IdDocument.OTHER.equals(teacher.getPerson().getIdDocumentType().name())) {
            String otherIdDocumentType = teacher.getPerson().getIdDocumentType().getLocalizedName();
            if (RebidesService.validateMaxFieldSize(teacher, "Person.otherIdDocumentType", otherIdDocumentType,
                    RebidesService.LIMIT_60CHARS)) {
                bean.setOtherIdDocumentType(otherIdDocumentType);
            }
        }
    }

    private void fillTeacherDateOfBirth(IdentificationBean bean) {
        if (teacher.getPerson().getDateOfBirthYearMonthDay() != null) {
            LocalDate dateOfBirth = teacher.getPerson().getDateOfBirthYearMonthDay().toLocalDate();
            if (validateDateOfBirth("label.age", dateOfBirth)) {
                bean.setDateOfBirth(dateOfBirth);
            }
        } else {
            LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
                    RebidesService.createMissingFieldMessage("Person.dateOfBirthYearMonthDay"));
        }
    }

    private void fillTeacherGender(IdentificationBean bean) {
        if (teacher.getPerson().getGender() != null) {
            if (LegalMapping.find(report, RebidesMappingType.GENDER).translate(teacher.getPerson().getGender()) != null) {
                String gender = LegalMapping.find(report, RebidesMappingType.GENDER).translate(teacher.getPerson().getGender());
                bean.setGender(gender);
            } else {
                LegalReportContext.addError(RebidesService.createSubjectForReport(teacher), RebidesService
                        .createMissingMappingMessage("Person.gender", teacher.getPerson().getGender().getLocalizedName()));
            }
        } else {
            LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
                    RebidesService.createMissingFieldMessage("Person.gender"));
        }
    }

    private void fillTeacherNationalityCountry(IdentificationBean bean) {
        if (teacher.getPerson().getCountry() != null) {
            bean.setNationalityCountry(teacher.getPerson().getCountry().getCode());
        } else {
            LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
                    RebidesService.createMissingFieldMessage("Person.nationality"));
        }
    }

    private void fillTeacherOtherNationalityCountry(IdentificationBean bean) {
        bean.setOtherNationalityCountry(null);
    }

    private Boolean validateDateOfBirth(String field, LocalDate dateOfBirth) {
        LocalDate now = new LocalDate(executionYear.getBeginCivilYear(), DateTimeConstants.DECEMBER, 31); // last day of the begin civil year
        Period period = new Period(dateOfBirth, now, PeriodType.yearMonthDay());

        if (period.getYears() < RebidesService.MIN_AGE || period.getYears() > RebidesService.MAX_AGE) {
            final String message = RebidesService.i18n("rebides.invalidIntervalValue", RebidesService.i18n(field),
                    String.valueOf(period.getYears()), String.valueOf(RebidesService.MIN_AGE),
                    String.valueOf(RebidesService.MAX_AGE));
            LegalReportContext.addError(RebidesService.createSubjectForReport(teacher), message);
            return false;
        }
        return true;
    }
}
