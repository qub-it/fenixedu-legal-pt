package org.fenixedu.legalpt.domain.a3es.mapping;

import java.util.Set;

import org.fenixedu.academic.domain.TeacherCategory;
import org.fenixedu.academic.domain.person.qualifications.QualificationLevel;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.DomainObjectLegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.ILegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;

import pt.ist.fenixframework.FenixFramework;

public enum A3esMappingType implements ILegalMappingType {

    CONTRACT_CATEGORY {

        @Override
        public Set<?> getValues() {
            return Bennu.getInstance().getTeacherCategorySet();
        }

        @Override
        public LegalMapping createMapping(final LegalReport report) {
            return new DomainObjectLegalMapping(report, this);
        }

        @Override
        public LocalizedString getLocalizedNameKey(final String key) {
            return ((TeacherCategory) FenixFramework.getDomainObject(key)).getName();
        }
    },

    SCHOOL_LEVEL {

        @Override
        public Set<?> getValues() {
            return Bennu.getInstance().getQualificationLevelsSet();
        }

        @Override
        public LegalMapping createMapping(final LegalReport report) {
            return new DomainObjectLegalMapping(report, this);
        }

        @Override
        public LocalizedString getLocalizedNameKey(final String key) {
            return ((QualificationLevel) FenixFramework.getDomainObject(key)).getName();
        }
    }

    ;

    abstract public Set<?> getValues();

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public LocalizedString getName() {
        return LegalPTUtil.bundleI18N(getQualifiedNameKey());
    }

    @Override
    public LocalizedString getDescription() {
        return LegalPTUtil.bundleI18N(getQualifiedDescriptionKey());
    }

    protected String getQualifiedDescriptionKey() {
        return A3esMappingType.class.getName() + "." + name() + ".description";
    }

    protected String getQualifiedNameKey() {
        return A3esMappingType.class.getName() + "." + name() + ".name";
    }

    @Override
    abstract public LegalMapping createMapping(final LegalReport report);

    @Override
    abstract public LocalizedString getLocalizedNameKey(final String key);

}
