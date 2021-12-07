package org.fenixedu.legalpt.dto.raides;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.domain.raides.IntegratedMasterFirstCycleGraduatedReportOption;
import org.fenixedu.legalpt.domain.raides.RaidesInstance;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class RaidesInstanceBean implements IBean {

    private String passwordToZip;
    private Set<RegistrationProtocol> mobilityAgreements;
    private Set<RegistrationProtocol> enrolledAgreements;

    private Set<IngressionType> degreeChangeIngressions;
    private Set<IngressionType> degreeTransferIngressions;
    private Set<IngressionType> ingressionsForGeneralAccessRegime;
    private Set<StatuteType> grantOwnerStatuteTypes;

    private List<TupleDataSourceBean> ingressionTypesDataSource;

    private List<TupleDataSourceBean> registrationProtocolsDataSource;

    private List<TupleDataSourceBean> integratedMasterFirstCycleGraduatedReportOptionsDataSource;

    private List<TupleDataSourceBean> defaultDistrictOfResidenceDataSource;

    private List<TupleDataSourceBean> grantOwnerStatuteTypesDataSource;

    private String institutionCode;

    private String interlocutorPhone;

    private IntegratedMasterFirstCycleGraduatedReportOption integratedMasterFirstCycleGraduatedReportOption;

    private District defaultDistrictOfResidence;

    private boolean reportGraduatedWithoutConclusionProcess;

    public RaidesInstanceBean(final RaidesInstance raidesInstance) {
        setInstitutionCode(raidesInstance.getInstitutionCode());
        setInterlocutorPhone(raidesInstance.getInterlocutorPhone());

        setPasswordToZip(raidesInstance.getPasswordToZip());

        setMobilityAgreements(Sets.newHashSet(raidesInstance.getMobilityAgreementsSet()));
        setEnrolledAgreements(Sets.newHashSet(raidesInstance.getEnrolledAgreementsSet()));

        setDegreeChangeIngressions(Sets.newHashSet(raidesInstance.getDegreeChangeIngressionsSet()));
        setDegreeTransferIngressions(Sets.newHashSet(raidesInstance.getDegreeTransferIngressionsSet()));
        setIngressionsForGeneralAccessRegime(Sets.newHashSet(raidesInstance.getGeneralAccessRegimeIngressionsSet()));

        setIntegratedMasterFirstCycleGraduatedReportOption(raidesInstance.getIntegratedMasterFirstCycleGraduatedReportOption());
        setDefaultDistrictOfResidence(raidesInstance.getDefaultDistrictOfResidence());
        setReportGraduatedWithoutConclusionProcess(raidesInstance.isReportGraduatedWithoutConclusionProcess());
        setGrantOwnerStatuteTypes(Sets.newHashSet(raidesInstance.getGrantOwnerStatuteTypesSet()));

        loadDataSources();
    }

    private void loadDataSources() {
        this.ingressionTypesDataSource = Lists.newArrayList();
        this.ingressionTypesDataSource.add(new TupleDataSourceBean("", "-"));

        this.ingressionTypesDataSource.addAll(Bennu.getInstance().getIngressionTypesSet().stream()
                .map(i -> new TupleDataSourceBean(i.getExternalId(), i.getDescription().getContent()))
                .collect(Collectors.toList()));

        this.registrationProtocolsDataSource = Lists.newArrayList();
        this.registrationProtocolsDataSource.add(new TupleDataSourceBean("", "-"));

        this.registrationProtocolsDataSource.addAll(Bennu.getInstance().getRegistrationProtocolsSet().stream()
                .map(r -> new TupleDataSourceBean(r.getExternalId(), r.getDescription().getContent()))
                .collect(Collectors.toList()));

        List<IntegratedMasterFirstCycleGraduatedReportOption> l =
                Lists.newArrayList(IntegratedMasterFirstCycleGraduatedReportOption.values());

        this.integratedMasterFirstCycleGraduatedReportOptionsDataSource = Lists.newArrayList();
        this.integratedMasterFirstCycleGraduatedReportOptionsDataSource.addAll(l.stream()
                .map(i -> new TupleDataSourceBean(i.name(), i.getLocalizedName().getContent())).collect(Collectors.toSet()));

        this.defaultDistrictOfResidenceDataSource = Lists.newArrayList();
        this.defaultDistrictOfResidenceDataSource.addAll(Bennu.getInstance().getDistrictsSet().stream()
                .map(i -> new TupleDataSourceBean(i.getExternalId(), i.getName())).collect(Collectors.toSet()));

        this.grantOwnerStatuteTypesDataSource = Lists.newArrayList();
        this.grantOwnerStatuteTypesDataSource
                .addAll(Bennu.getInstance().getStatuteTypesSet().stream().sorted(StatuteType.COMPARATOR_BY_NAME)
                        .map(s -> new TupleDataSourceBean(s.getExternalId(), s.getCode() + " - " + s.getName().getContent()))
                        .collect(Collectors.toList()));

    }

    public String getPasswordToZip() {
        return passwordToZip;
    }

    public void setPasswordToZip(String passwordToZip) {
        this.passwordToZip = passwordToZip;
    }

    public Set<RegistrationProtocol> getMobilityAgreements() {
        return mobilityAgreements;
    }

    public void setMobilityAgreements(Set<RegistrationProtocol> mobilityAgreements) {
        this.mobilityAgreements = mobilityAgreements;
    }

    public Set<RegistrationProtocol> getEnrolledAgreements() {
        return enrolledAgreements;
    }

    public void setEnrolledAgreements(Set<RegistrationProtocol> enrolledAgreements) {
        this.enrolledAgreements = enrolledAgreements;
    }

    public Set<IngressionType> getDegreeChangeIngressions() {
        return degreeChangeIngressions;
    }

    public void setDegreeChangeIngressions(Set<IngressionType> degreeChangeIngressions) {
        this.degreeChangeIngressions = degreeChangeIngressions;
    }

    public Set<IngressionType> getDegreeTransferIngressions() {
        return degreeTransferIngressions;
    }

    public void setDegreeTransferIngressions(Set<IngressionType> degreeTransferIngressions) {
        this.degreeTransferIngressions = degreeTransferIngressions;
    }

    public List<TupleDataSourceBean> getIngressionTypesDataSource() {
        return ingressionTypesDataSource;
    }

    public Set<IngressionType> getIngressionsForGeneralAccessRegime() {
        return ingressionsForGeneralAccessRegime;
    }

    public void setIngressionsForGeneralAccessRegime(Set<IngressionType> ingressionsForGeneralAccessRegime) {
        this.ingressionsForGeneralAccessRegime = ingressionsForGeneralAccessRegime;
    }

    public Set<StatuteType> getGrantOwnerStatuteTypes() {
        return grantOwnerStatuteTypes;
    }

    public void setGrantOwnerStatuteTypes(Set<StatuteType> grantOwnerStatuteTypes) {
        this.grantOwnerStatuteTypes = grantOwnerStatuteTypes;
    }

    public List<TupleDataSourceBean> getGrantOwnerStatuteTypesDataSource() {
        return grantOwnerStatuteTypesDataSource;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getInterlocutorPhone() {
        return interlocutorPhone;
    }

    public void setInterlocutorPhone(String interlocutorPhone) {
        this.interlocutorPhone = interlocutorPhone;
    }

    public IntegratedMasterFirstCycleGraduatedReportOption getIntegratedMasterFirstCycleGraduatedReportOption() {
        return integratedMasterFirstCycleGraduatedReportOption;
    }

    public void setIntegratedMasterFirstCycleGraduatedReportOption(
            IntegratedMasterFirstCycleGraduatedReportOption integratedMasterFirstCycleGraduatedReportOption) {
        this.integratedMasterFirstCycleGraduatedReportOption = integratedMasterFirstCycleGraduatedReportOption;
    }

    public District getDefaultDistrictOfResidence() {
        return defaultDistrictOfResidence;
    }

    public void setDefaultDistrictOfResidence(District defaultDistrictOfResidence) {
        this.defaultDistrictOfResidence = defaultDistrictOfResidence;
    }

    public boolean isReportGraduatedWithoutConclusionProcess() {
        return reportGraduatedWithoutConclusionProcess;
    }

    public void setReportGraduatedWithoutConclusionProcess(boolean reportGraduatedWithoutConclusionProcess) {
        this.reportGraduatedWithoutConclusionProcess = reportGraduatedWithoutConclusionProcess;
    }

}
