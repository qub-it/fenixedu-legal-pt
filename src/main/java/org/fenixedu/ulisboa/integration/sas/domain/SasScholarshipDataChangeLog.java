package org.fenixedu.ulisboa.integration.sas.domain;

import java.util.Comparator;

import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

public class SasScholarshipDataChangeLog extends SasScholarshipDataChangeLog_Base {

    static public Comparator<SasScholarshipDataChangeLog> COMPARATOR_BY_DATE = (x, y) -> x.getDate().compareTo(y.getDate());

    protected SasScholarshipDataChangeLog() {
        super();
        super.setBennu(Bennu.getInstance());
    }

    public SasScholarshipDataChangeLog(SasScholarshipCandidacy candidacy, DateTime date, String description, boolean publicLog, boolean sentLog) {
        this();
        setSasScholarshipCandidacy(candidacy);
        setDate(date);
        setStudentNumber(candidacy.getStudentNumber());
        setStudentName(candidacy.getCandidacyName());
        setDescription(description);
        setPublicLog(publicLog);
        setSentLog(sentLog);
    }

    @Atomic
    public void delete() {
        setSasScholarshipCandidacy(null);
        setBennu(null);
        deleteDomainObject();
    }

}
