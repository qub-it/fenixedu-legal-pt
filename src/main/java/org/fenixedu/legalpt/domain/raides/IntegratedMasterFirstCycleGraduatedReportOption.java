package org.fenixedu.legalpt.domain.raides;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.util.LegalPTUtil;

public enum IntegratedMasterFirstCycleGraduatedReportOption {
    
    NONE,
    WITH_CONCLUSION_PROCESS,
    ALL;
    
    public LocalizedString getLocalizedName() {
        return LegalPTUtil.bundleI18N(getClass().getSimpleName() + "." + name());
    }
}
