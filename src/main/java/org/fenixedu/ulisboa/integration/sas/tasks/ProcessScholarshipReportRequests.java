package org.fenixedu.ulisboa.integration.sas.tasks;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.ulisboa.integration.sas.domain.ScholarshipReportRequest;
import org.fenixedu.ulisboa.integration.sas.service.process.ScholarshipService;
import org.fenixedu.ulisboa.integration.sas.util.SASDomainException;

//Force task to be read only and process each report on its own transaction to avoid errors in a report affecting other reports
@Task(englishTitle = "Process Scholarship Report Requests", readOnly = true)
public class ProcessScholarshipReportRequests extends CronTask {

    @Override
    public void runTask() throws Exception {

        for (final ScholarshipReportRequest request : Bennu.getInstance().getPendingScholarshipReportRequestsSet()) {

            try {
                ScholarshipService.processScholarshipFile(request);

            } catch (final SASDomainException e) {
                taskLog("Error processing scholarship request with oid " + request.getExternalId());
                getLogger().error(
                        e.getMessage() + " - " + "Error processing scholarship request with oid " + request.getExternalId(), e);
                request.removeReport(e.getLocalizedString());
            } catch (final Throwable t) {
                taskLog("Error processing scholarship request with oid " + request.getExternalId());
                getLogger().error(
                        t.getMessage() + " - " + "Error processing scholarship request with oid " + request.getExternalId(), t);
                request.removeReport(null);
            }
        }
    }

}
