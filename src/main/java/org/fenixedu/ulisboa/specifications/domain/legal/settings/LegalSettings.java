package org.fenixedu.ulisboa.specifications.domain.legal.settings;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

public class LegalSettings extends LegalSettings_Base {
    
    protected LegalSettings() {
        super();
        setBennu(Bennu.getInstance());
        
        // Properties
        
        setRaidesFormActiveForStudent(false);
    }
    
    @Atomic
    public void edit(final boolean raidesForActiveForStudent) {
        setRaidesFormActiveForStudent(raidesForActiveForStudent);
    }
    
    @Atomic
    public static synchronized LegalSettings getInstance() {
        if(Bennu.getInstance().getLegalSettingsSet().isEmpty()) {
            new LegalSettings();
        }
        
        return Bennu.getInstance().getLegalSettingsSet().iterator().next();
    }
    
}
