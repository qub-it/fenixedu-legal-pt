package org.fenixedu.legalpt.domain.mapping;

import org.fenixedu.legalpt.domain.report.LegalReport;
import org.fenixedu.legalpt.dto.mapping.LegalMappingEntryBean;

import pt.ist.fenixframework.Atomic;

public class StringLegalMapping extends StringLegalMapping_Base {

    protected StringLegalMapping() {
        super();
    }

    public StringLegalMapping(final LegalReport report, final ILegalMappingType type) {
        this();
        init(report, type);
    }

    @Override
    public void addEntry(String key, String value) {
        super.addEntry(key, value);
    }

    @Override
    @Atomic
    public void addEntry(final LegalMappingEntryBean bean) {
        for (LegalMappingEntry entry : this.getLegalMappingEntriesSet()) {
            if (entry.getMappingKey().equalsIgnoreCase(bean.getKeyAsString())) {
                throw new IllegalArgumentException("error.mapping.key.already.exists");
            }
        }
        addEntry(bean.getKeyAsString(), bean.getValue());
    }

    @Override
    public String keyForObject(final Object key) {
        return key.toString();
    }

}
