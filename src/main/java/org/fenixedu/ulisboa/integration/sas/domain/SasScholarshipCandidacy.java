package org.fenixedu.ulisboa.integration.sas.domain;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

public class SasScholarshipCandidacy extends SasScholarshipCandidacy_Base {

    public SasScholarshipCandidacy() {
        super();
        super.setBennu(Bennu.getInstance());
    }

    @Atomic
    public void delete() {
        setRegistration(null);
        setExecutionYear(null);

        if (getSasScholarshipData() != null) {
            getSasScholarshipData().delete();

        }

        for (final Iterator<SasScholarshipDataChangeLog> iterator = getSasScholarshipDataChangeLogsSet().iterator(); iterator
                .hasNext();) {

            final SasScholarshipDataChangeLog log = iterator.next();
            iterator.remove();
            log.delete();
        }

        setBennu(null);
        deleteDomainObject();
    }

    static public Collection<SasScholarshipCandidacy> findAll() {
        return Bennu.getInstance().getSasScholarshipCandidaciesSet();
    }

    public boolean isModified() {
        return getExportDate() == null ? false : getStateDate().isAfter(getExportDate());
    }

    public void changeState(SasScholarshipCandidacyState state) {
        super.setState(state);
        super.setStateDate(new DateTime());
    }

    public Collection<SasScholarshipDataChangeLog> getLogsAfter(final DateTime date, boolean publicLogs) {
        return getSasScholarshipDataChangeLogsSet().stream()
                .filter(l -> (date == null || !l.getDate().isBefore(date)) && l.getPublicLog() == publicLogs)
                .collect(Collectors.toSet());
    }

}
