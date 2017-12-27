package org.fenixedu.ulisboa.specifications.domain.legal.mapping;

import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.legal.dto.mapping.LegalMappingEntryBean;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;

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
            if(entry.getMappingKey().equalsIgnoreCase(bean.getKeyAsString())){
                throw new ULisboaSpecificationsDomainException("error.mapping.key.already.exists");
            }
        }
        addEntry(bean.getKeyAsString(), bean.getValue());
    }

    @Override
    public String keyForObject(final Object key) {
        return key.toString();
    }
    
}
