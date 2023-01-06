package org.fenixedu.legalpt.domain.report;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class LegalReportRequest extends LegalReportRequest_Base {
    
    protected static final Comparator<LegalReportRequest> COMPARE_BY_REQUEST_DATE = new Comparator<LegalReportRequest>() {

        @Override
        public int compare(final LegalReportRequest o1, final LegalReportRequest o2) {
            int c = o1.getWhenRequested().compareTo(o2.getWhenRequested());
            
            return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
        }
    };

    protected LegalReportRequest() {
        super();
        setBennu(Bennu.getInstance());
        setBennuPending(Bennu.getInstance());
    }

    protected void init(final LegalReport report) {
        setWhenRequested(new DateTime());
        setRequestor(Authenticate.getUser().getPerson());
        setLegalReport(report);

        checkRules();
    }

    private void checkRules() {
        if(getRequestor() == null) { throw new IllegalArgumentException("error.ReportRequest.requestor.required"); }
        
        if(getWhenRequested() == null) { throw new IllegalArgumentException("error.ReportRequest.whenRequested.required"); }
        
        if(getLegalReport() == null) { throw new IllegalArgumentException("error.ReportRequest.report.required"); }
    }

    public boolean isPending() {
        return getBennuPending() != null;
    }

    public Boolean getPending() {
        return getBennuPending() != null;
    }

    public boolean isCancelable() {
        return isPending();
    }

    public Boolean getCancelable() {
        return getBennuPending() != null;
    }

    public void process() {
        getLegalReport().process(this);
    }

    @Atomic
    public void cancelRequest() {
        setBennuPending(null);
    }

    public static final LegalReportRequest findFirstAsynchronousPendingRequest() {
        final Collection<LegalReportRequest> requests =
                Collections2.filter(Bennu.getInstance().getPendingLegalReportRequestsSet(),
                        new Predicate<LegalReportRequest>() {

                            @Override
                            public boolean apply(LegalReportRequest request) {
                                return !request.getLegalReport().getSynchronous();
                            }
                        });

        if (requests.isEmpty()) {
            return null;
        }

        return Collections.min(requests, COMPARE_BY_REQUEST_DATE);
    }

    public String getParameters() {
        throw new IllegalArgumentException("error.ReportRequest.report.use.method.getParametersAs");
    }

    public <T extends LegalReportRequestParameters> T getParametersAs(Class<T> type) {
        return LegalReportRequestParameters.fromJson(type, super.getParameters());
    }

    public void setParameters(LegalReportRequestParameters reportRequestParameter) {
        super.setParameters(reportRequestParameter != null ? reportRequestParameter.toJson() : null);
    }

    @Atomic
    public static LegalReportRequest createRequest(final LegalReport report, final LegalReportRequestParameters parameters) {
        final LegalReportRequest result = new LegalReportRequest();
        result.init(report);
        result.setParameters(parameters);

        if (report.getSynchronous()) {
            result.process();
        }

        return result;
    }

    @Atomic(mode = TxMode.WRITE)
    public void markAsProcessed() {
        setWhenProcessed(new DateTime());
        setBennuPending(null);
    }
    
    @Atomic
    public void delete() {
        setBennu(null);
        setBennuPending(null);
        getLegalResultDataFileSet().forEach(dataFile->dataFile.delete());
        getLegalReportParametersFilesSet().forEach(parameterFile->parameterFile.delete());
        setRequestor(null);
        setLegalReport(null);
        deleteDomainObject();
    }
}
