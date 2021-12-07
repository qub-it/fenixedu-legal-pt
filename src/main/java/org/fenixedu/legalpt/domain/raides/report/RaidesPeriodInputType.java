package org.fenixedu.legalpt.domain.raides.report;

import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.legalpt.util.LegalPTUtil;

public enum RaidesPeriodInputType {
    ENROLLED, GRADUATED, INTERNATIONAL_MOBILITY;

    public boolean isForEnrolled() {
        return this == ENROLLED;
    }

    public boolean isForGraduated() {
        return this == GRADUATED;
    }

    public boolean isForInternationalMobility() {
        return this == INTERNATIONAL_MOBILITY;
    }

    public String getQualifiedName() {
        return RaidesPeriodInputType.class.getSimpleName() + "." + name();
    }

    public String getLocalizedName() {
        return BundleUtil.getString(LegalPTUtil.BUNDLE, getQualifiedName());
    }

}
