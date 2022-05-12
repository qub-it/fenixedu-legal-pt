package org.fenixedu.ulisboa.integration.sas.domain;

import java.util.Locale;

import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixWebFramework.rendererExtensions.util.IPresentableEnum;

public enum SasScholarshipCandidacyState implements IPresentableEnum {

    PENDING,

    PROCESSED,

    PROCESSED_WARNINGS,

    PROCESSED_ERRORS,

    SENT,

    ANNULLED;

    public String getName() {
        return name();
    }

    @Override
    public String getLocalizedName() {
        return getLocalizedNameI18N().getContent();
    }

    public String getLocalizedName(final Locale locale) {
        return getLocalizedNameI18N().getContent(locale);
    }

    public LocalizedString getLocalizedNameI18N() {
        return BundleUtil.getLocalizedString("resources.SasResources",
                SasScholarshipCandidacyState.class.getSimpleName() + "." + name());
    }

}