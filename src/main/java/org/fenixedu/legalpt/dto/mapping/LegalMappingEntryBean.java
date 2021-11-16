package org.fenixedu.legalpt.dto.mapping;

import java.io.Serializable;
import java.util.Set;

import org.fenixedu.legalpt.domain.mapping.LegalMapping;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.DomainObject;

@SuppressWarnings("serial")
public class LegalMappingEntryBean implements Serializable {
    protected LegalMapping mapping;
    protected LegalMappingEntryKeyBean key;
    protected String value;

    protected Set<LegalMappingEntryKeyBean> keys = Sets.newHashSet();

    protected LegalMappingEntryBean() {
        
    }
    
    public LegalMappingEntryBean(final LegalMapping mapping) {
        setLegalMapping(mapping);

        for (final Object o : mapping.getPossibleKeys()) {
            keys.add(new LegalMappingEntryKeyBean(mapping, o));
        }
    }

    public Set<LegalMappingEntryKeyBean> getKeysProvider() {
        return keys;
    }

    public Enum<?> getKeyAsEnum() {
        return (Enum<?>) getKey().getKey();
    }

    public String getKeyAsString() {
        return getKey().getKey().toString();
    }

    public DomainObject getKeyAsDomainObject() {
        return (DomainObject) getKey().getKey();
    }

    /*
     * GETTERS & SETTERS
     */

    public LegalMapping getLegalMapping() {
        return mapping;
    }

    public void setLegalMapping(LegalMapping mapping) {
        this.mapping = mapping;
    }

    public LegalMappingEntryKeyBean getKey() {
        return key;
    }

    public void setKey(final LegalMappingEntryKeyBean key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
