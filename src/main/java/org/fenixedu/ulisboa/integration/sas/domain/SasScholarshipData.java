package org.fenixedu.ulisboa.integration.sas.domain;

import java.util.Collection;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

public class SasScholarshipData extends SasScholarshipData_Base {

    public SasScholarshipData() {
        super();
        super.setBennu(Bennu.getInstance());;
    }

    @Atomic
    public void delete() {
        setSasScholarshipCandidacy(null);
        setBennu(null);
        deleteDomainObject();
    }

    static public Collection<SasScholarshipData> findAll() {
        return Bennu.getInstance().getSasScholarshipDataSet();
    }
}
