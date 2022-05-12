package org.fenixedu.ulisboa.integration.sas.util;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;

public class SASDomainException extends DomainException {

    private static final String BUNDLE = "resources/SasResources";

    public SASDomainException(String key, String[] args) {
        super(BUNDLE, key, args);
    }

    public SASDomainException(String key) {
        this(key, new String[0]);
    }

    public LocalizedString getLocalizedString() {
        return BundleUtil.getLocalizedString(BUNDLE, getKey(), getArgs());
    }

}
