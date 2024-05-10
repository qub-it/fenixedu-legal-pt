package org.fenixedu.ulisboa.integration.sas.service.process;

import org.fenixedu.bennu.core.i18n.BundleUtil;

public class FillScholarshipException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public FillScholarshipException(String message, String... args) {
        super(BundleUtil.getString(AbstractFillScholarshipService.SAS_BUNDLE, message, args));
    }

    public FillScholarshipException() {
        super();
    }

}