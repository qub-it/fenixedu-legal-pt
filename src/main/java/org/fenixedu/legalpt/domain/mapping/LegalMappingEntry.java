package org.fenixedu.legalpt.domain.mapping;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.base.Strings;

public class LegalMappingEntry extends LegalMappingEntry_Base {
    
    protected LegalMappingEntry() {
        super();
        setBennu(Bennu.getInstance());
    }
    
    LegalMappingEntry(final LegalMapping mapping, final String key, final String value) {
        this();
        
        setLegalMapping(mapping);
        setMappingKey(key);
        setMappingValue(value);
        
        checkRules();
    }

    public LocalizedString getLocalizedNameKeyI18N() {
        return getLegalMapping().getLocalizedNameEntryKeyI18N(getMappingKey());
    }

    protected void checkRules() {
        if(Strings.isNullOrEmpty(getMappingKey())) {
            throw new IllegalArgumentException("error.MappingEntry.key.must.not.be.blank.or.null");
        }
        
        if(Strings.isNullOrEmpty(getMappingValue())) {
            throw new IllegalArgumentException("error.MappingEntry.value.must.not.be.blank.or.null");
        }
    }

    void delete() {
        setBennu(null);
        setLegalMapping(null);
        
        deleteDomainObject();
    }
    
}
