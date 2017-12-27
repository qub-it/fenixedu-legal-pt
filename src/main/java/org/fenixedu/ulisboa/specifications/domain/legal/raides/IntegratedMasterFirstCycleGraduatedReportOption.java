package org.fenixedu.ulisboa.specifications.domain.legal.raides;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

public enum IntegratedMasterFirstCycleGraduatedReportOption {
    
    NONE,
    WITH_CONCLUSION_PROCESS,
    ALL;
    
    public LocalizedString getLocalizedName() {
        return ULisboaSpecificationsUtil.bundleI18N(getClass().getSimpleName() + "." + name());
    }
}
