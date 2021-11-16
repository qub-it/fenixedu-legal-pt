package org.fenixedu.legalpt.domain.mapping;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.domain.report.LegalReport;

public interface ILegalMappingType {
    
    public String getCode();
    public LocalizedString getName();
    public LocalizedString getDescription();
    public LocalizedString getLocalizedNameKey(final String key);
    public LegalMapping createMapping(final LegalReport report);
    
}
