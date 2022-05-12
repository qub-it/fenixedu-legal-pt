package org.fenixedu.bennu;

import org.fenixedu.bennu.spring.BennuSpringModule;

@BennuSpringModule(basePackages = "org.fenixedu.ulisboa.integration.sas", bundles = "SasResources")
public class SasSpringConfiguration {
    public static final String BUNDLE = "resources/SasResources";
}
