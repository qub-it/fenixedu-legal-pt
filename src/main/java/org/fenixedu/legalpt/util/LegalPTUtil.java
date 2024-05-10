package org.fenixedu.legalpt.util;

import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;

public class LegalPTUtil {
    public final static String BUNDLE_NAME = "resources/FenixeduLegalPTResources";
    public static final String BUNDLE = BUNDLE_NAME.replace('/', '.');

    // @formatter: off
    /**********
     * BUNDLE *
     **********/
    // @formatter: on

    public static String bundle(final String key, final String... args) {
        return BundleUtil.getString(LegalPTUtil.BUNDLE, key, args);
    }

    public static LocalizedString bundleI18N(final String key, final String... args) {
        return BundleUtil.getLocalizedString(LegalPTUtil.BUNDLE, key, args);
    }

}
