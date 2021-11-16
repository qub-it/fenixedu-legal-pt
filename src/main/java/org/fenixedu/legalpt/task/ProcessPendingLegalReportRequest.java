package org.fenixedu.legalpt.task;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.legalpt.domain.report.LegalReportRequest;

import pt.ist.fenixframework.FenixFramework;

@Task(englishTitle = "Process pending legal report request", readOnly = false)
public class ProcessPendingLegalReportRequest extends CronTask {

    private String pendingRequestId;
    
    public ProcessPendingLegalReportRequest() {
    }
    
    public ProcessPendingLegalReportRequest(final String pendingRequestId) {
        this.pendingRequestId = pendingRequestId;
    }

    @Override
    public void runTask() throws Exception {
        if(pendingRequestId != null) {
            LegalReportRequest request = FenixFramework.getDomainObject(pendingRequestId);
            getLogger().info("Process report: " + pendingRequestId);
            
            request.process();
            getLogger().info("Finished report: " + pendingRequestId);
        } else if(!Bennu.getInstance().getPendingLegalReportRequestsSet().isEmpty()) {
            final LegalReportRequest request = Bennu.getInstance().getPendingLegalReportRequestsSet().iterator().next();
            getLogger().info("Process report: " + request.getExternalId());
            
            request.process();
            getLogger().info("Finished report: " + request.getExternalId());
        }
    }

}
