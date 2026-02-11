package org.fenixedu.ulisboa.integration.sas.domain;

import org.fenixedu.bennu.core.domain.Bennu;

public class SasDegreeMapping extends SasDegreeMapping_Base {

    protected SasDegreeMapping() {
        super();
        super.setBennu(Bennu.getInstance());
    }

    public static SasDegreeMapping create(final String sourceDegreeCode, final String targetDegreeCode) {
        SasDegreeMapping sasDegreeMapping = new SasDegreeMapping();
        sasDegreeMapping.setSourceDegreeCode(sourceDegreeCode);
        sasDegreeMapping.setTargetDegreeCode(targetDegreeCode);
        return sasDegreeMapping;
    }

    public void edit(final String sourceDegreeCode, final String targetDegreeCode) {
        setSourceDegreeCode(sourceDegreeCode);
        setTargetDegreeCode(targetDegreeCode);
    }

    public void delete() {
        setBennu(null);
        super.deleteDomainObject();
    }

}
