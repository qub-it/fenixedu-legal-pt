package org.fenixedu.ulisboa.specifications.domain.legal.report;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.legal.LegalReportContext;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.ILegalMappingType;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public abstract class LegalReport extends LegalReport_Base {

    public static Comparator<LegalReport> COMPARATOR_BY_NAME = new Comparator<LegalReport>() {

        @Override
        public int compare(LegalReport o1, LegalReport o2) {

            final String leftName = o1.getNameI18N().getContent();
            final String rightName = o2.getNameI18N().getContent();

            if (leftName == null && rightName == null) {
                return o1.getExternalId().compareTo(o2.getExternalId());
            }

            if (leftName == null) {
                return 1;
            }

            if (rightName == null) {
                return -1;
            }

            int result = Collator.getInstance().compare(leftName, rightName);

            return result == 0 ? DomainObjectUtil.COMPARATOR_BY_ID.compare(o1, o2) : result;
        }
    };

    protected LegalReport() {
        super();
        setBennu(Bennu.getInstance());
    }

    public String getType() {
        return getClass().getName();
    }

    public final void process(final LegalReportRequest reportRequest) {
        try {
            LegalReportContext.init();
            executeProcessing(reportRequest);
        } finally {
            LegalReportContext.destroy();
        }
    }

    static public Collection<LegalReport> findReportsWithAccessBy(final Person person) {
        final Set<LegalReport> result = Sets.newHashSet();
        for (final LegalReport LegalReport : Bennu.getInstance().getLegalReportsSet()) {
            if (LegalReport.hasAccess(person)) {
                result.add(LegalReport);
            }
        }

        return result;
    }

    private boolean hasAccess(final Person person) {
        if(person.getUser() != null) {
            return getGroup().isMember(person.getUser());
        }
        
        return false;
    }
    
    @Atomic
    static public <T extends LegalReport> T createReport(Class<T> type){
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public abstract void executeProcessing(final LegalReportRequest legalReportRequest);

    public abstract LocalizedString getNameI18N();

    public abstract Set<ILegalMappingType> getMappingTypes();

    public abstract Set<?> getPossibleKeys(final String type);

    public abstract LocalizedString getMappingTypeNameI18N(final String type);

    public abstract LocalizedString getLocalizedNameMappingKey(final String type, final String key);
    
    public abstract void edit(final LocalizedString name, final PersistentGroup group, final Boolean synchronous, final Boolean hasMappings);
    
    public abstract void delete();

}