package org.fenixedu.legalpt.domain.mapping;

import org.fenixedu.legalpt.domain.report.LegalReport;
import org.fenixedu.legalpt.dto.mapping.LegalMappingEntryBean;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;

public class EnumerationLegalMapping extends EnumerationLegalMapping_Base {

    protected EnumerationLegalMapping() {
        super();
    }

    public EnumerationLegalMapping(final LegalReport report, final ILegalMappingType mappingType) {
        this();

        init(report, mappingType);
    }

    public void addEntry(final Enum<?> key, final String value) {
        super.addEntry(key.name(), value);
    }

    @Override
    @Atomic
    public void addEntry(final LegalMappingEntryBean bean) {
        for (LegalMappingEntry entry : this.getLegalMappingEntriesSet()) {
            if (entry.getMappingKey().equalsIgnoreCase(bean.getKeyAsEnum().name())) {
                throw new IllegalArgumentException("error.mapping.key.already.exists");
            }
        }
        addEntry(bean.getKeyAsEnum(), bean.getValue());
    }

    @Override
    public String keyForObject(final Object key) {
        if (key instanceof DomainObject) { // TEMP HACK!!
            return ((DomainObject) key).getExternalId();
        }
        return ((Enum<?>) key).name();
    }

}
