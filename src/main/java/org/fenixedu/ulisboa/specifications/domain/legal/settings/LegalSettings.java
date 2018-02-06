package org.fenixedu.ulisboa.specifications.domain.legal.settings;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.FenixEduLegalPTConfiguration;

import pt.ist.fenixframework.Atomic;

public class LegalSettings extends LegalSettings_Base {

    protected LegalSettings() {
        super();
        setBennu(Bennu.getInstance());
    }

    @Atomic
    public static synchronized LegalSettings getInstance() {
        if (Bennu.getInstance().getLegalSettingsSet().isEmpty()) {
            new LegalSettings();
        }

        return Bennu.getInstance().getLegalSettingsSet().iterator().next();
    }

    @Override
    public String getA3esURL() {
        final String saved = super.getA3esURL();
        return StringUtils.isBlank(saved) ? FenixEduLegalPTConfiguration.getConfiguration().a3esURL() : saved;
    }

}
