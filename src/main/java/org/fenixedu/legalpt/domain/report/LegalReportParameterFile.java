package org.fenixedu.legalpt.domain.report;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixframework.Atomic;

public class LegalReportParameterFile extends LegalReportParameterFile_Base {
    
    public LegalReportParameterFile() {
        super();
        setBennu(Bennu.getInstance());
    }

    @Atomic
    public static LegalReportParameterFile createReportParameterFile(String filename, byte[] content) {
        final LegalReportParameterFile reportParameterFile = new LegalReportParameterFile();
        reportParameterFile.init(filename, filename, content);
        return reportParameterFile;
    }

    @Override
    public void delete() {
        setBennu(null);
        
        deleteDomainObject();
    }

    @Override
    public boolean isAccessible(final User user) {
        return getLegalReportRequest().getLegalReport().getGroup().isMember(user);
    }
    
}
