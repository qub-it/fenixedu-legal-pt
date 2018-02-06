package org.fenixedu.legalpt.domain.a3es;

import java.util.Set;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.domain.a3es.mapping.A3esMappingType;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.ILegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequest;

import com.google.common.collect.Sets;

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
        return find(A3esInstance.class);
    }

    @Override
    public void executeProcessing(final LegalReportRequest reportRequest) {
    }

}
