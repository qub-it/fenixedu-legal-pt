package org.fenixedu.ulisboa.specifications.domain.legal.dto.mapping;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;

@SuppressWarnings("serial")
public class LegalMappingEntryKeyBean implements java.io.Serializable {
    
    protected LegalMapping mapping;
    protected Object key;
    
    public LegalMappingEntryKeyBean(final LegalMapping mapping, final Object key) {
        setMapping(mapping);
        setKey(key);
    }

    public LocalizedString getLocalizedName() {
        return getMapping().getLocalizedNameEntryKeyI18NForObject(getKey());
    }
    
    @Override
    public int hashCode() {
        return key.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        
        if(obj.getClass() != this.getClass()) {
            return false;
        }
        
        return ((LegalMappingEntryKeyBean) obj).key.equals(this.key);
    }
    
    /*
     * GETTERS & SETTERS
     */
    
    public LegalMapping getMapping() {
        return mapping;
    }

    public void setMapping(LegalMapping mapping) {
        this.mapping = mapping;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }
    
}
