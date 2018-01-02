package org.fenixedu.ulisboa.specifications.domain.legal.mapping;

import java.text.Collator;
import java.util.Comparator;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.dto.mapping.LegalMappingBean;
import org.fenixedu.legalpt.dto.mapping.LegalMappingEntryBean;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;

public abstract class LegalMapping extends LegalMapping_Base {

    public static Comparator<LegalMapping> COMPARATOR_BY_NAME = new Comparator<LegalMapping>() {

        @Override
        public int compare(final LegalMapping o1, final LegalMapping o2) {

            final String leftName = o1.getNameI18N().getContent() == null ? "" : o1.getNameI18N().getContent();
            final String rightName = o2.getNameI18N().getContent() == null ? "" : o2.getNameI18N().getContent();

            final int result = Collator.getInstance().compare(leftName, rightName);
            return result == 0 ? o1.getExternalId().compareTo(o2.getExternalId()) : result;
        }
    };

    protected LegalMapping() {
        setBennu(Bennu.getInstance());
    }

    protected void init(final LegalReport report, final ILegalMappingType type) {
        setLegalReport(report);
        setType(type.getCode());
        checkRules();
    }

    public abstract void addEntry(final LegalMappingEntryBean bean);

    public abstract String keyForObject(final Object key);

    public String translate(final Enum<?> enumValue) {
        return translateObject(enumValue);
    }

    public String translate(final Boolean booleanValue) {
        return booleanValue != null ? translateObject(booleanValue.toString()) : translateObject(Boolean.FALSE.toString());
    }

    public String translate(final DomainObject domainObjectInstance) {
        return translateObject(domainObjectInstance);
    }

    public String translate(final String key) {
        return translateObject(key);
    }

    protected String translateObject(final Object key) {
        if (isKeyDefined(key)) {
            return findMappingEntryForKey(keyForObject(key)).getMappingValue();
        }

        return null;
    }

    public Set<?> getPossibleKeys() {
        return getLegalReport().getPossibleKeys(getType());
    }

    public LocalizedString getNameI18N() {
        return getLegalReport().getMappingTypeNameI18N(getType());
    }

    public boolean isKeyDefined(final Object object) {
        final String key = keyForObject(object);
        return findMappingEntryForKey(key) != null;
    }

    @Atomic
    public void deleteEntry(final LegalMappingEntry entry) {
        entry.delete();
    }

    public LocalizedString getLocalizedNameEntryKeyI18N(final String key) {
        return getLegalReport().getLocalizedNameMappingKey(getType(), key);
    }

    public LocalizedString getLocalizedNameEntryKeyI18NForObject(final Object key) {
        return getLocalizedNameEntryKeyI18N(keyForObject(key));
    }

    public static LegalMapping find(final LegalReport report, final ILegalMappingType type) {
        return find(report, type.getCode());
    }

    protected static LegalMapping find(final LegalReport report, final String type) {
        LegalMapping result = null;
        for (final LegalMapping mapping : readAll()) {
            if (mapping.getType().equals(type) && mapping.getLegalReport() == report) {

                if (result != null) {
                    throw new ULisboaSpecificationsDomainException("error.Mapping.found.more.than.one.in.report");
                }

                result = mapping;
            }
        }

        return result;
    }

    @Atomic
    public void delete() {
        if (this.getLegalMappingEntriesSet().size() > 0) {
            throw new ULisboaSpecificationsDomainException("error.mapping.delete.not.empty.entries");
        }
        super.setLegalReport(null);
        super.setBennu(null);
        super.deleteDomainObject();

    }

    @Atomic
    public static LegalMapping create(final LegalMappingBean bean) {
        return bean.getMappingType().createMapping(bean.getReport());
    }

    @Atomic
    public static LegalMapping create(final ILegalMappingType mappingType, final LegalReport report) {
        return mappingType.createMapping(report);
    }

    /*
     * OTHER METHODS
     */

    public void addEntry(final String key, final String value) {
        new LegalMappingEntry(this, key, value);
    }

    protected static Set<LegalMapping> readAll() {
        return Bennu.getInstance().getLegalMappingsSet();
    }

    private void checkRules() {
        if (Strings.isNullOrEmpty(getType())) {
            throw new ULisboaSpecificationsDomainException("error.Mapping.type.required");
        }

        if (getLegalReport() == null) {
            throw new ULisboaSpecificationsDomainException("error.Mapping.report.required");
        }

        find(getLegalReport(), getType());
    }

    protected LegalMappingEntry findMappingEntryForKey(final String key) {
        for (final LegalMappingEntry entry : getLegalMappingEntriesSet()) {
            if (entry.getMappingKey().equals(key)) {
                return entry;
            }
        }

        return null;
    }

}
