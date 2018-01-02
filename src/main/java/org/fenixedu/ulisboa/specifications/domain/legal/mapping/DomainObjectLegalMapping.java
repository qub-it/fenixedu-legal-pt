package org.fenixedu.ulisboa.specifications.domain.legal.mapping;

import org.fenixedu.legalpt.dto.mapping.LegalMappingEntryBean;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;

public class DomainObjectLegalMapping extends DomainObjectLegalMapping_Base {
    
    protected DomainObjectLegalMapping() {
        super();
    }
    
    public DomainObjectLegalMapping(final LegalReport report, final ILegalMappingType type) {
        this();
        init(report, type);
    }
    
    @Atomic
    public void addEntry(final DomainObject instance, final String value) {
        for (LegalMappingEntry entry : this.getLegalMappingEntriesSet()) {
            if(entry.getMappingKey().equalsIgnoreCase(instance.getExternalId())){
                throw new ULisboaSpecificationsDomainException("error.mapping.key.already.exists");
            }
        }
        
        super.addEntry(instance.getExternalId(), value);
    }

    @Override
    @Atomic
    public void addEntry(final LegalMappingEntryBean bean) {
        for (LegalMappingEntry entry : this.getLegalMappingEntriesSet()) {
            if(entry.getMappingKey().equalsIgnoreCase(bean.getKeyAsDomainObject().getExternalId())){
                throw new ULisboaSpecificationsDomainException("error.mapping.key.already.exists");
            }
        }
        addEntry(bean.getKeyAsDomainObject(), bean.getValue());
    }
    
    @Override
    public String keyForObject(final Object key) {
        return ((DomainObject) key).getExternalId();
    }
    
}
