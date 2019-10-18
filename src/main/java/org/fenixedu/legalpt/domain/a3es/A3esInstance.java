package org.fenixedu.legalpt.domain.a3es;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.domain.a3es.mapping.A3esMappingType;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.ILegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequest;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class A3esInstance extends A3esInstance_Base {

    public A3esInstance() {
        super();
    }

    @Override
    public LocalizedString getNameI18N() {
        return LegalPTUtil.bundleI18N("title." + A3esInstance.class.getName());
    }

    @Override
    public Set<ILegalMappingType> getMappingTypes() {
        return Sets.<ILegalMappingType> newHashSet(A3esMappingType.values());
    }

    @Override
    public Set<?> getPossibleKeys(String type) {
        return A3esMappingType.valueOf(type).getValues();
    }

    @Override
    public LocalizedString getMappingTypeNameI18N(String type) {
        return A3esMappingType.valueOf(type).getName();
    }

    @Override
    public LocalizedString getLocalizedNameMappingKey(String type, String key) {
        return A3esMappingType.valueOf(type).getLocalizedNameKey(key);
    }

    public static A3esInstance getInstance() {
        return Optional.of(find(A3esInstance.class)).orElseGet(() -> createInstance());
    }

    private static synchronized A3esInstance createInstance() {
        A3esInstance instance = find(A3esInstance.class);
        if (instance == null) {
            instance = LegalReport.createReport(A3esInstance.class);
            instance.edit(new LocalizedString(Locale.getDefault(), "A3ES"), Group.parse("#academicAdmOffice").toPersistentGroup(),
                    /* synchronous */ true, /* hasMappings */ true);
        }

        return instance;
    }

    @Override
    public void executeProcessing(final LegalReportRequest reportRequest) {
    }

    @Atomic
    public void edit(final LocalizedString name, final PersistentGroup group, final Boolean synchronous,
            final Boolean hasMappings, final String a3esUrl, final Set<RegistrationProtocol> mobilityAgreements,
            final boolean studyCycleByDegree, final boolean groupCourseProfessorshipByPerson,
            final boolean groupPersonProfessorshipByCourse) {

        super.edit(name, group, synchronous, hasMappings);

        setA3esUrl(a3esUrl);

        getMobilityAgreementsSet().clear();
        getMobilityAgreementsSet().addAll(mobilityAgreements);

        setStudyCycleByDegree(studyCycleByDegree);
        setGroupCourseProfessorshipByPerson(groupCourseProfessorshipByPerson);
        setGroupPersonProfessorshipByCourse(groupPersonProfessorshipByCourse);
    }

}
