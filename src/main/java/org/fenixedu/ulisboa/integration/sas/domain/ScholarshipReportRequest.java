package org.fenixedu.ulisboa.integration.sas.domain;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class ScholarshipReportRequest extends ScholarshipReportRequest_Base {

    public ScholarshipReportRequest(ExecutionYear executionYear, boolean firstYearOfCycle, boolean contractualisation,
            String fileName, byte[] content) {
        super();
        setExecutionYear(executionYear);
        setFirstYearOfCycle(firstYearOfCycle);
        setContractualisation(contractualisation);
        ScholarshipReportFile scholarshipReportFile = new ScholarshipReportFile(fileName, content);
        setParameterFile(scholarshipReportFile);
        Bennu bennu = Bennu.getInstance();
        setBennu(bennu);
        setBennuForWhichIsPending(bennu);
        setWhenRequested(new DateTime());
    }

    @Atomic(mode = TxMode.WRITE)
    public ScholarshipReportFile createResultFile(final String fileName, final byte[] content) {
        if (getResultFile() != null) {
            throw new DomainException("label.error.scholarship.requestAlreadyHasResult");
        }

        final ScholarshipReportFile result = new ScholarshipReportFile(fileName, content);
        setResultFile(result);

        setBennuForWhichIsPending(null);
        setWhenProcessed(new DateTime());
        return result;
    }

    @Atomic(mode = TxMode.WRITE)
    public void removeReport(final LocalizedString reason) {
        setError(reason);

        setBennuForWhichIsPending(null);
        setWhenProcessed(new DateTime());
    }

    //Overrides to change visibility
    @Override
    public boolean getFirstYearOfCycle() {
        return super.getFirstYearOfCycle();
    }

    @Override
    public ScholarshipReportFile getParameterFile() {
        return super.getParameterFile();
    }

    @Override
    public ScholarshipReportFile getResultFile() {
        return super.getResultFile();
    }

}
