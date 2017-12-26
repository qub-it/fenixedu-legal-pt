package org.fenixedu.ulisboa.specifications.domain.legal.report;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.joda.time.DateTime;

public class LegalReportResultFile extends LegalReportResultFile_Base {
    
    public LegalReportResultFile() {
        super();
        setBennu(Bennu.getInstance());
    }

    public LegalReportResultFile(final LegalReportRequest reportRequest, final LegalReportResultFileType type, final byte[] content) {
        this(reportRequest, type, reportRequest.getLegalReport().getName().getContent() + "_" + new DateTime().toString("dd-MM-yyyy-HH-mm") + "." + 
                type.getFileExtension(), content);
    }

    public LegalReportResultFile(final LegalReportRequest reportRequest, final LegalReportResultFileType type, final String filename,
            final byte[] content) {
        this();
        setType(type);
        setLegalReportRequest(reportRequest);

        final String displayName = reportRequest.getLegalReport().getName().getContent() + "_" + new DateTime().toString("dd-MM-yyyy-HH-mm");

        init(displayName, filename, content);
    }

    @Override
    public boolean isAccessible(final User user) {
        return getLegalReportRequest().getLegalReport().getGroup().isMember(user);
    }
    
}
