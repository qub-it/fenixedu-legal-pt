package org.fenixedu.legalpt.domain.raides;

import java.util.Set;

import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.domain.mapping.ILegalMappingType;
import org.fenixedu.legalpt.domain.raides.mapping.BranchMappingType;
import org.fenixedu.legalpt.domain.raides.mapping.LegalMappingType;
import org.fenixedu.legalpt.domain.report.LegalReportRequest;
import org.fenixedu.legalpt.util.LegalPTUtil;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class RaidesInstance extends RaidesInstance_Base {

    public RaidesInstance() {
        super();
    }

    @Override
    public Set<ILegalMappingType> getMappingTypes() {
        return Sets.<ILegalMappingType> newHashSet(LegalMappingType.values());
    }

    @Override
    public Set<?> getPossibleKeys(final String type) {
        return LegalMappingType.valueOf(type).getValues();
    }

    @Override
    public LocalizedString getMappingTypeNameI18N(final String type) {
        if (BranchMappingType.isTypeForMapping(type)) {
            return BranchMappingType.getInstance().getName();
        }

        return LegalMappingType.valueOf(type).getName();
    }

    @Override
    public LocalizedString getLocalizedNameMappingKey(final String type, final String key) {
        if (BranchMappingType.isTypeForMapping(type)) {
            return BranchMappingType.getInstance().getLocalizedNameKey(key);
        }

        return LegalMappingType.valueOf(type).getLocalizedNameKey(key);
    }

    @Override
    public LocalizedString getNameI18N() {
        return LegalPTUtil.bundleI18N("title." + RaidesInstance.class.getName());
    }

    public static RaidesInstance getInstance() {
        return find(RaidesInstance.class);
    }

    @Override
    public void executeProcessing(final LegalReportRequest reportRequest) {
        (new Raides()).process(this, reportRequest);
    }

    @Override
    @Atomic
    public void edit(LocalizedString name, PersistentGroup group, Boolean synchronous, Boolean hasMappings) {
        setName(name);
        setGroup(group);
        setSynchronous(synchronous);
        setHasMappings(hasMappings);
    }

    @Atomic
    public void edit(final LocalizedString name, final PersistentGroup group, final Boolean synchronous,
            final Boolean hasMappings, final String passwordToZip, final Set<RegistrationProtocol> enrolledAgreements,
            final Set<RegistrationProtocol> mobilityAgreements, final Set<IngressionType> degreeTransferIngressions,
            final Set<IngressionType> degreeChangeIngressions, final Set<IngressionType> generalAccessRegimeIngressions,
            final String institutionCode, final String interlocutorPhone,
            final IntegratedMasterFirstCycleGraduatedReportOption integratedMasterFirstCycleGraduatedReportOption,
            final District defaultDistrictOfResidence, final boolean reportGraduatedWithoutConclusionProcess,
            final Set<StatuteType> grantOwnerStatuteTypes, final boolean reportGrantOwnerOnlyByStatutes) {
        edit(name, group, synchronous, hasMappings);

        setPasswordToZip(passwordToZip);
        getEnrolledAgreementsSet().clear();
        getEnrolledAgreementsSet().addAll(enrolledAgreements);

        getMobilityAgreementsSet().clear();
        getMobilityAgreementsSet().addAll(mobilityAgreements);

        getDegreeTransferIngressionsSet().clear();
        getDegreeTransferIngressionsSet().addAll(degreeTransferIngressions);

        getDegreeChangeIngressionsSet().clear();
        getDegreeChangeIngressionsSet().addAll(degreeChangeIngressions);

        getGeneralAccessRegimeIngressionsSet().clear();
        getGeneralAccessRegimeIngressionsSet().addAll(generalAccessRegimeIngressions);

        setInstitutionCode(institutionCode);
        setInterlocutorPhone(interlocutorPhone);

        setIntegratedMasterFirstCycleGraduatedReportOption(integratedMasterFirstCycleGraduatedReportOption);
        setDefaultDistrictOfResidence(defaultDistrictOfResidence);
        setReportGraduatedWithoutConclusionProcess(reportGraduatedWithoutConclusionProcess);

        getGrantOwnerStatuteTypesSet().clear();
        getGrantOwnerStatuteTypesSet().addAll(grantOwnerStatuteTypes);

        setReportGrantOwnerOnlyByStatutes(reportGrantOwnerOnlyByStatutes);
    }

    public boolean isToReportAllIntegratedMasterFirstCycleGraduatedStudents() {
        return getIntegratedMasterFirstCycleGraduatedReportOption() == IntegratedMasterFirstCycleGraduatedReportOption.ALL;
    }

    public boolean isToReportIntegratedMasterFirstCycleGraduatedStudentsOnlyWithConclusionProcess() {
        return getIntegratedMasterFirstCycleGraduatedReportOption() == IntegratedMasterFirstCycleGraduatedReportOption.WITH_CONCLUSION_PROCESS;
    }

    public boolean isToNotReportIntegratedMasterFirstCycleGraduatedStudents() {
        return getIntegratedMasterFirstCycleGraduatedReportOption() == null
                || getIntegratedMasterFirstCycleGraduatedReportOption() == IntegratedMasterFirstCycleGraduatedReportOption.NONE;
    }

    public boolean isSumEctsCreditsBetweenPlans() {
        return getSumEctsCreditsBetweenPlans();
    }

    public boolean isReportGraduatedWithoutConclusionProcess() {
        return getReportGraduatedWithoutConclusionProcess();
    }

}
