package org.fenixedu.legalpt.dto.a3es;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.domain.a3es.A3esInstance;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class A3esInstanceBean implements IBean {

    private Set<RegistrationProtocol> mobilityAgreements;
    private List<TupleDataSourceBean> registrationProtocolsDataSource;

    private boolean studyCycleByDegree;
    private boolean groupCourseProfessorshipByPerson;
    private boolean groupPersonProfessorshipByCourse;

    public A3esInstanceBean(final A3esInstance instance) {
        setMobilityAgreements(Sets.newHashSet(instance.getMobilityAgreementsSet()));
        setStudyCycleByDegree(instance.getStudyCycleByDegree());
        setGroupCourseProfessorshipByPerson(instance.getGroupCourseProfessorshipByPerson());
        setGroupPersonProfessorshipByCourse(instance.getGroupPersonProfessorshipByCourse());

        loadDataSources();
    }

    private void loadDataSources() {

        this.registrationProtocolsDataSource = Lists.newArrayList();
        this.registrationProtocolsDataSource.add(new TupleDataSourceBean("", "-"));

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

    public boolean getStudyCycleByDegree() {
        return studyCycleByDegree;
    }

    public void setStudyCycleByDegree(final boolean input) {
        this.studyCycleByDegree = input;
    }

    public boolean getGroupCourseProfessorshipByPerson() {
        return groupCourseProfessorshipByPerson;
    }

    public void setGroupCourseProfessorshipByPerson(final boolean input) {
        this.groupCourseProfessorshipByPerson = input;
    }

    public boolean getGroupPersonProfessorshipByCourse() {
        return groupPersonProfessorshipByCourse;
    }

    public void setGroupPersonProfessorshipByCourse(final boolean input) {
        this.groupPersonProfessorshipByCourse = input;
    }

}
