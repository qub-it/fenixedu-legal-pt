package org.fenixedu.legalpt.dto.a3es;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.domain.a3es.A3esInstance;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class A3esInstanceBean implements IBean {

    private Set<RegistrationProtocol> mobilityAgreements;

    private List<TupleDataSourceBean> registrationProtocolsDataSource;

    public A3esInstanceBean(final A3esInstance instance) {
        setMobilityAgreements(Sets.newHashSet(instance.getMobilityAgreementsSet()));

        loadDataSources();
    }

    private void loadDataSources() {

        this.registrationProtocolsDataSource = Lists.newArrayList();
        this.registrationProtocolsDataSource.add(ULisboaConstants.SELECT_OPTION);

        this.registrationProtocolsDataSource.addAll(Bennu.getInstance().getRegistrationProtocolsSet().stream()
                .map(r -> new TupleDataSourceBean(r.getExternalId(), r.getDescription().getContent()))
                .collect(Collectors.toList()));
    }

    public Set<RegistrationProtocol> getMobilityAgreements() {
        return mobilityAgreements;
    }

    public void setMobilityAgreements(Set<RegistrationProtocol> mobilityAgreements) {
        this.mobilityAgreements = mobilityAgreements;
    }

}