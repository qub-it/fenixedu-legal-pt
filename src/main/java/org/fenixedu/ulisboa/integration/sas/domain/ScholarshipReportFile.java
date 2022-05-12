package org.fenixedu.ulisboa.integration.sas.domain;

import org.fenixedu.bennu.core.domain.User;

public class ScholarshipReportFile extends ScholarshipReportFile_Base {

    protected ScholarshipReportFile(String fileName, byte[] content) {
        this.init(fileName, fileName, content);
    }

    @Override
    public boolean isAccessible(User arg0) {
        return false;
    }

}
