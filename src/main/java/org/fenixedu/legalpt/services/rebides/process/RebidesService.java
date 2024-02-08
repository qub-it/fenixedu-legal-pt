package org.fenixedu.legalpt.services.rebides.process;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.TeacherAuthorization;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.mapping.LegalMapping;
import org.fenixedu.legalpt.domain.rebides.RebidesReportEntryTarget;
import org.fenixedu.legalpt.domain.rebides.mapping.RebidesMappingType;
import org.fenixedu.legalpt.domain.report.LegalReport;
import org.fenixedu.legalpt.domain.report.LegalReportRequest;
import org.fenixedu.legalpt.dto.rebides.RebidesBean;
import org.fenixedu.legalpt.dto.rebides.TeacherBean;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.joda.time.DateTimeConstants;
import org.joda.time.YearMonthDay;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class RebidesService {

    public static int SIZE_4CHARS = 4;
    public static int SIZE_9CHARS = 9;

    public static int LIMIT_1CHARS = 1;
    public static int LIMIT_2CHARS = 2;
    public static int LIMIT_3CHARS = 3;
    public static int LIMIT_20CHARS = 20;
    public static int LIMIT_50CHARS = 50;
    public static int LIMIT_60CHARS = 60;
    public static int LIMIT_80CHARS = 80;
    public static int LIMIT_160CHARS = 160;
    public static int LIMIT_255CHARS = 255;

    public static int MAX_AGE = 100;
    public static int MIN_AGE = 18;

    public static int LIMIT_100PERCENTAGE = 100;
    public static int LIMIT_99PERCENTAGE = 99;
    public static int LIMIT_0PERCENTAGE = 0;

    public static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    private RebidesBean bean = new RebidesBean();

    protected LegalReport report;

    public RebidesService(LegalReportRequest reportRequest) {
        final ExtractionInfoService extractionInfoService = new ExtractionInfoService();
        bean.setExtractionInfo(extractionInfoService.getExtractionInfoData(reportRequest));

        this.report = reportRequest.getLegalReport();
    }

    public RebidesBean process(ExecutionYear executionYear) {

        checkAllMappings();

        final YearMonthDay lastDayOfTheYear =
                new YearMonthDay(executionYear.getAcademicInterval().getStart().getYear(), DateTimeConstants.DECEMBER, 31);

        Bennu.getInstance().getTeachersSet().forEach(t -> {
            //TODO: add teacher authorization filters (isValid on 31 Dec?)

            final TeacherAuthorization teacherAuthorization =
                    t.getLatestTeacherAuthorizationInInterval(lastDayOfTheYear.toInterval()).orElse(null);

            if (teacherAuthorization != null && (teacherAuthorization.getRevokeTime() == null
                    || !teacherAuthorization.getRevokeTime().toLocalDate().isAfter(lastDayOfTheYear.toLocalDate()))) {

                final TeacherBean teacherBean = new TeacherBean();
                final IdentificationService identificationService = new IdentificationService(report, t, executionYear);
                teacherBean.setIdentification(identificationService.getIdentificationData());

                final CareerActivitiesService careerActivitiesService =
                        new CareerActivitiesService(report, t, lastDayOfTheYear, executionYear);
                teacherBean.setCareerActivities(careerActivitiesService.getCareerActivitiesData());

                //TODO: add qualitifications

                bean.getTeachers().add(teacherBean);
            }

        });

        return bean;

//        for (Teacher teacher : RootDomainObject.getInstance().getTeachersSet()) {
//            // check if teacher has contract at 31 of december
//            Collection<EmployeeContract> contracts = (Collection<EmployeeContract>) teacher.getPerson()
//                    .getParentAccountabilities(AccountabilityTypeEnum.WORKING_CONTRACT, EmployeeContract.class);
//
//            for (EmployeeContract contract : contracts) {
//                if (contract.isActive(lastDayOfTheYear)) {
//
//                    final TeacherBean teacherBean = new TeacherBean();
//
//                    final IdentificationService identificationService = new IdentificationService(report, teacher, executionYear);
//                    teacherBean.setIdentification(identificationService.getIdentificationData());
//
//                    final CareerActivitiesService careerActivitiesService =
//                            new CareerActivitiesService(report, teacher, lastDayOfTheYear, executionYear);
//                    teacherBean.setCareerActivities(careerActivitiesService.getCareerActivitiesData());
//
//                    if (teacher.getPerson().getAssociatedQualificationsSet().size() == 0) {
//                        LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
//                                RebidesService.createMissingFieldMessage("label.qualifications"));
//                    } else {
//                        for (Qualification qualification : teacher.getPerson().getAssociatedQualificationsSet()) {
//                            if (qualification.getSchoolLevel() == null) {
//                                continue;
//                            }
//
//                            if (!qualification.getSchoolLevel().isHigherEducation()) {
//                                continue;
//                            }
//
//                            final QualificationService qualificationService =
//                                    new QualificationService(report, teacher, qualification);
//                            teacherBean.getQualifications().add(qualificationService.getQualificationData());
//                        }
//
//                    }
//
//                    bean.getTeachers().add(teacherBean);
//                }
//            }
//    }

    }

    private void checkAllMappings() {
        if (LegalMapping.find(report, RebidesMappingType.CONTRACT_CATEGORY) == null) {
            throw new DomainException("rebides.message.error.mapping.is.empty",
                    i18n("com.qubit.qubEdu.module.legal.domain.rebides.mapping.RebidesMappingType.CONTRACT_CATEGORY.name"));
        }
        if (LegalMapping.find(report, RebidesMappingType.CONTRACT_BINDING_REGIME) == null) {
            throw new DomainException("rebides.message.error.mapping.is.empty",
                    i18n("com.qubit.qubEdu.module.legal.domain.rebides.mapping.RebidesMappingType.CONTRACT_BINDING_REGIME.name"));
        }
        if (LegalMapping.find(report, RebidesMappingType.CONTRACT_SERVICE_REGIME) == null) {
            throw new DomainException("rebides.message.error.mapping.is.empty",
                    i18n("com.qubit.qubEdu.module.legal.domain.rebides.mapping.RebidesMappingType.CONTRACT_SERVICE_REGIME.name"));
        }
        if (LegalMapping.find(report, RebidesMappingType.CONTRACT_WAGE_LEVEL) == null) {
            throw new DomainException("rebides.message.error.mapping.is.empty",
                    i18n("com.qubit.qubEdu.module.legal.domain.rebides.mapping.RebidesMappingType.CONTRACT_WAGE_LEVEL.name"));
        }
        if (LegalMapping.find(report, RebidesMappingType.ID_DOCUMENT_TYPE) == null) {
            throw new DomainException("rebides.message.error.mapping.is.empty",
                    i18n("com.qubit.qubEdu.module.legal.domain.rebides.mapping.RebidesMappingType.ID_DOCUMENT_TYPE.name"));
        }
        if (LegalMapping.find(report, RebidesMappingType.BOOLEAN) == null) {
            throw new DomainException("rebides.message.error.mapping.is.empty",
                    i18n("com.qubit.qubEdu.module.legal.domain.rebides.mapping.RebidesMappingType.BOOLEAN.name"));
        }
        if (LegalMapping.find(report, RebidesMappingType.GENDER) == null) {
            throw new DomainException("rebides.message.error.mapping.is.empty",
                    i18n("com.qubit.qubEdu.module.legal.domain.rebides.mapping.RebidesMappingType.GENDER.name"));
        }
        if (LegalMapping.find(report, RebidesMappingType.SCHOOL_LEVEL) == null) {
            throw new DomainException("rebides.message.error.mapping.is.empty",
                    i18n("com.qubit.qubEdu.module.legal.domain.rebides.mapping.RebidesMappingType.SCHOOL_LEVEL.name"));
        }
        if (LegalMapping.find(report, RebidesMappingType.SCHOOL_LEVEL_ORIGIN) == null) {
            throw new DomainException("rebides.message.error.mapping.is.empty",
                    i18n("com.qubit.qubEdu.module.legal.domain.rebides.mapping.RebidesMappingType.SCHOOL_LEVEL_ORIGIN.name"));
        }
        if (LegalMapping.find(report, RebidesMappingType.SCIENTIFIC_AREA) == null) {
            throw new DomainException("rebides.message.error.mapping.is.empty",
                    i18n("com.qubit.qubEdu.module.legal.domain.rebides.mapping.RebidesMappingType.SCIENTIFIC_AREA.name"));
        }
    }

    public static String i18n(String key, String... arguments) {
        return LegalPTUtil.bundle(key, arguments);
    }

    public static String createMissingMappingMessage(String field, String value) {
        final String fieldName = i18n(field);
        return i18n("rebides.createMissingMappingMessage", fieldName, value);
    }

    public static String createMissingFieldMessage(String field) {
        final String fieldName = i18n(field);
        return i18n("rebides.createMissingFieldMessage", fieldName);
    }

    public static String createInvalidFieldMessage(String field) {
        final String fieldName = i18n(field);
        return i18n("rebides.createInvalidFieldMessage", fieldName);
    }

    public static Boolean validateMaxFieldSize(Teacher teacher, String field, String fieldContent, int maxFieldSize) {
        if (fieldContent.length() > maxFieldSize) {
            final String message = i18n("rebides.fieldSizeOverflow", i18n(field), String.valueOf(fieldContent.length()),
                    String.valueOf(maxFieldSize));
            LegalReportContext.addError(RebidesReportEntryTarget.of(teacher), message);
            return false;
        }
        return true;
    }

    public static Boolean validatePercentageField(Teacher teacher, String field, Integer value, Integer minPercentage,
            Integer maxPercentage) {
        if (value < minPercentage || value > maxPercentage) {
            final String message = i18n("rebides.invalidIntervalValue", i18n(field), String.valueOf(value),
                    String.valueOf(minPercentage), String.valueOf(maxPercentage));
            LegalReportContext.addError(RebidesReportEntryTarget.of(teacher), message);
            return false;
        }
        return true;
    }
}
