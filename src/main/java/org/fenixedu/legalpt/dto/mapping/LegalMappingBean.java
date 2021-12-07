package org.fenixedu.legalpt.dto.mapping;

import java.util.Set;

import org.fenixedu.legalpt.domain.mapping.ILegalMappingType;
import org.fenixedu.legalpt.domain.report.LegalReport;

@SuppressWarnings("serial")
public class LegalMappingBean implements java.io.Serializable {

    protected LegalReport report;
    protected ILegalMappingType mappingType;
    
    public LegalMappingBean(final LegalReport report) {
        setReport(report);
    }
    
    public Set<ILegalMappingType> getMappingTypesProvider() {
        return report.getMappingTypes();
    }

    /*
     * GETTERS & SETTERS
     */

    public LegalReport getReport() {
        return report;
    }

    public void setReport(LegalReport report) {
        this.report = report;
    }

    public ILegalMappingType getMappingType() {
        return mappingType;
    }

    public void setMappingType(ILegalMappingType mappingType) {
        this.mappingType = mappingType;
    }
    
}
