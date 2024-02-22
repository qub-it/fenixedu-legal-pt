package org.fenixedu.legalpt.services.rebides.process;

import org.fenixedu.academic.domain.Qualification;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.mapping.LegalMapping;
import org.fenixedu.legalpt.domain.rebides.RebidesReportEntryTarget;
import org.fenixedu.legalpt.domain.rebides.mapping.RebidesMappingType;
import org.fenixedu.legalpt.domain.report.LegalReport;
import org.fenixedu.legalpt.dto.rebides.QualificationBean;

public class QualificationService {

    private final Teacher teacher;
    private final Qualification qualification;
    private LegalReport report;

    public static class Institution {
        public static final String OTHER = "0000";
        public static final String NOT_DEFINED = "9999";
    }

    public static class Degree {
        public static final String OTHER = "0000";
        public static final String NOT_DEFINED = "9999";
    }

    public static class ExpertiseArea {
        public static final String OTHER = "00";
        public static final String NOT_DEFINED = "99";
    }

    public QualificationService(final LegalReport report, Teacher teacher, Qualification qualification) {
        this.report = report;
        this.teacher = teacher;
        this.qualification = qualification;
    }

    public QualificationBean getQualificationData() {
        QualificationBean bean = new QualificationBean();
        fillSchoolLevel(bean); // required
        fillSchoolLevelDegree(bean); // required
        fillInstituition(bean); // required
        fillOtherInstituition(bean); // optional
        fillDegree(bean); // required
        fillOtherDegree(bean); // optional
        fillExpertiseArea(bean); // required
        fillOtherExpertiseArea(bean); // optional
        fillScientificArea(bean); // required

        return bean;
    }

    private void fillSchoolLevel(QualificationBean bean) {
        // TODO add all values defined at REBIDES report
        if (getQualificationSchoolLevel() != null) {
            if (LegalMapping.find(report, RebidesMappingType.SCHOOL_LEVEL).translate(getQualificationSchoolLevel()) != null) {
                bean.setSchoolLevel(
                        LegalMapping.find(report, RebidesMappingType.SCHOOL_LEVEL).translate(getQualificationSchoolLevel()));
            } else {
                LegalReportContext.addError(RebidesReportEntryTarget.of(teacher), RebidesService
                        .createMissingMappingMessage("label.schoolLevel", getQualificationSchoolLevel().getLocalizedName()));
            }
        } else {
            LegalReportContext.addError(RebidesReportEntryTarget.of(teacher),
                    RebidesService.createMissingFieldMessage("label.schoolLevel"));
        }
    }

    private void fillSchoolLevelDegree(QualificationBean bean) {
        if (getQualificationSchoolLevelOrigin() != null) {
            if (LegalMapping.find(report, RebidesMappingType.SCHOOL_LEVEL_ORIGIN)
                    .translate(getQualificationSchoolLevelOrigin()) != null) {
                bean.setSchoolLevelOrigin(LegalMapping.find(report, RebidesMappingType.SCHOOL_LEVEL_ORIGIN)
                        .translate(getQualificationSchoolLevelOrigin()));
            } else {
                LegalReportContext.addError(RebidesReportEntryTarget.of(teacher), RebidesService
                        .createMissingMappingMessage("label.schoolLevelOrigin", getQualificationSchoolLevelOrigin()));
            }
        } else {
            LegalReportContext.addError(RebidesReportEntryTarget.of(teacher),
                    RebidesService.createMissingFieldMessage("label.schoolLevelType"));
        }
    }

    private void fillInstituition(QualificationBean bean) {
        if (getQualificationInstitution() != null) {
            if (getQualificationInstitution().getCode() != null) {
                bean.setInstituition(getQualificationInstitution().getCode());
            } else {
                bean.setInstituition(Institution.OTHER);
            }
        } else {
            LegalReportContext.addError(RebidesReportEntryTarget.of(teacher),
                    RebidesService.createMissingFieldMessage("label.degreeInstitution"));
        }
    }

    private void fillOtherInstituition(QualificationBean bean) {
        if (bean.getInstituition() != null && bean.getInstituition().equals(Institution.OTHER)) {

            String otherInstitution = getQualificationInstitution() != null ? getQualificationInstitution().getName() : "";
            if (RebidesService.validateMaxFieldSize(teacher, "label.otherInstitution", otherInstitution,
                    RebidesService.LIMIT_160CHARS)) {
                bean.setOtherInstituition(otherInstitution);
            }
        }
    }

    private void fillDegree(QualificationBean bean) {
        if (qualification.getDegree() != null) {
            if (qualification.getCountry().isDefaultCountry() && getQualificationDegreeDesignation() != null
                    && getQualificationDegreeDesignation().getCode() != null) {
                bean.setDegree(getQualificationDegreeDesignation().getCode());
            } else {
                bean.setDegree(Degree.OTHER);
            }

        } else {
            LegalReportContext.addError(RebidesReportEntryTarget.of(teacher),
                    RebidesService.createMissingFieldMessage("label.degree"));
        }
    }

    private void fillOtherDegree(QualificationBean bean) {
        if (bean.getDegree() != null && bean.getDegree().equals(Degree.OTHER)) {
            String otherDegree = qualification.getDegree();
            if (RebidesService.validateMaxFieldSize(teacher, "label.degreeOther", otherDegree, RebidesService.LIMIT_160CHARS)) {
                bean.setOtherDegree(otherDegree);
                return;
            }
        }
    }

    private void fillExpertiseArea(QualificationBean bean) {
        if (qualification.getCountry() != null && qualification.getCountry().isDefaultCountry()
                && getQualificationSchoolLevel() != null && getQualificationSchoolLevel().isHigherEducation()) {
            bean.setExpertiseArea(ExpertiseArea.OTHER);
        } else {
            bean.setExpertiseArea(ExpertiseArea.NOT_DEFINED);
        }
    }

    private void fillOtherExpertiseArea(QualificationBean bean) {
        if (bean.getExpertiseArea() != null && bean.getExpertiseArea().equals(ExpertiseArea.OTHER)) {
            bean.setOtherExpertiseArea(RebidesService.i18n("rebides.info.not.applicable"));
        }
    }

    private void fillScientificArea(QualificationBean bean) {
        if (getQualificationScientificAreaUnit() != null) {
            if (LegalMapping.find(report, RebidesMappingType.SCIENTIFIC_AREA)
                    .translate(getQualificationScientificAreaUnit()) != null) {
                bean.setScientificArea(LegalMapping.find(report, RebidesMappingType.SCIENTIFIC_AREA)
                        .translate(getQualificationScientificAreaUnit()));
            } else {
                LegalReportContext.addError(RebidesReportEntryTarget.of(teacher), RebidesService
                        .createMissingMappingMessage("label.scientificArea", getQualificationScientificAreaUnit().getName()));
            }
        } else {
            LegalReportContext.addError(RebidesReportEntryTarget.of(teacher),
                    RebidesService.createMissingFieldMessage("label.scientificArea"));
        }
    }

    private SchoolLevelType getQualificationSchoolLevel() {
        //TODO: finish
        return null;
    }

    private String getQualificationSchoolLevelOrigin() {
        //TODO: finish
        return null;
    }

    private Unit getQualificationInstitution() {
        //TODO: finish
        return null;
    }

    private DegreeDesignation getQualificationDegreeDesignation() {
        //TODO: finish
        return null;
    }

    private Unit getQualificationScientificAreaUnit() {
        //TODO: finish
        return null;
    }
}
