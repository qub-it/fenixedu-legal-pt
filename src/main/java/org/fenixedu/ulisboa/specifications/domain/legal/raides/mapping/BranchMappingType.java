package org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping;

import java.util.Set;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.dto.mapping.LegalMappingBean;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.DomainObjectLegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.ILegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMappingEntry;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;


public class BranchMappingType implements ILegalMappingType {

    protected static final String CODE = "BRANCH_MAPPING";
    protected static BranchMappingType _instance = null;
    
    protected BranchMappingType() {
    }

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public LocalizedString getName() {
        return LegalPTUtil.bundleI18N(getQualifiedNameKey());
    }

    @Override
    public LocalizedString getDescription() {
        return LegalPTUtil.bundleI18N(getQualifiedDescriptionKey());
    }

    @Override
    public LocalizedString getLocalizedNameKey(final String key) {
        final CourseGroup branch = getCourseGroup(key);
        return new LocalizedString(I18N.getLocale(), branch.getOneFullName());
    }

    public Set<LegalMappingEntry> getMappingEntries(final LegalMapping mapping, final DegreeCurricularPlan degreeCurricularPlan) {
        final Set<LegalMappingEntry> entriesSet = mapping.getLegalMappingEntriesSet();
        
        
        final Set<LegalMappingEntry> result =
                Sets.newHashSet();
        for (final LegalMappingEntry LegalMappingEntry : entriesSet) {
            final CourseGroup courseGroup = getCourseGroup(LegalMappingEntry.getMappingKey());
            
            if(courseGroup.getParentDegreeCurricularPlan() == degreeCurricularPlan) {
                result.add(LegalMappingEntry);
            }
        }
        
        return result;
    }
    
    public Set<?> getValues(final DegreeCurricularPlan degreeCurricularPlan) {
        return degreeCurricularPlan.getAllBranches();
    }
    
    public static final BranchMappingType getInstance() {
        if(_instance == null) {
            _instance = new BranchMappingType();
        }
        
        return _instance;
    }
    
    @Atomic
    public synchronized static LegalMapping readMapping(final LegalReport report) {
        LegalMapping mapping = LegalMapping.find(report, getInstance());
        
        if(mapping == null) {
            final LegalMappingBean bean = new LegalMappingBean(report);
            mapping = getInstance().createMapping(bean.getReport());
        }
        
        return mapping;
    }
    
    public static final boolean isTypeForMapping(final String type) {
        return CODE.equals(type);
    }
    
    @Override
    public LegalMapping createMapping(final LegalReport report) {
        return new DomainObjectLegalMapping(report, this);
    }

    protected CourseGroup getCourseGroup(final String key) {
        return FenixFramework.getDomainObject(key);
    }

    protected String getQualifiedNameKey() {
        return this.getClass().getName() + ".name";
    }

    protected String getQualifiedDescriptionKey() {
        return this.getClass().getName() + ".description";
    }

}
