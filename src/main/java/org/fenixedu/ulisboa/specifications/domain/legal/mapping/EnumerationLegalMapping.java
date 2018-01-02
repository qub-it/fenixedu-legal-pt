package org.fenixedu.ulisboa.specifications.domain.legal.mapping;

import org.fenixedu.legalpt.dto.mapping.LegalMappingEntryBean;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;

import pt.ist.fenixframework.Atomic;

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
            if(entry.getMappingKey().equalsIgnoreCase(bean.getKeyAsEnum().name())){
                throw new ULisboaSpecificationsDomainException("error.mapping.key.already.exists");
            }
        }
        addEntry(bean.getKeyAsEnum(), bean.getValue());
    }

    @Override
    public String keyForObject(final Object key) {
        return ((Enum<?>) key).name();
    }
    
}
