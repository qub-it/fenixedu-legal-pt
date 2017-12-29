package org.fenixedu.bennu;

import org.fenixedu.bennu.spring.BennuSpringModule;

@BennuSpringModule(basePackages = "org.fenixedu.legalpt", bundles = "FenixeduLegalPTResources")
public class FenixeduLegalPTSpringConfiguration {
    public final static String BUNDLE = "resources/FenixeduLegalPTResources";
}
