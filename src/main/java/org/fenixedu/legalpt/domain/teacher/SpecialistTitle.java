package org.fenixedu.legalpt.domain.teacher;

import org.fenixedu.legalpt.util.LegalPTUtil;

public enum SpecialistTitle {

    CTC_RECOGNITION_BY_INSTITUTION,

    TITLE_OBTAINED_IN_PUBLIC_EVALUATION;

    public String getPresentationName() {
        return LegalPTUtil.bundle(getClass().getName() + "." + name());
    }

}
