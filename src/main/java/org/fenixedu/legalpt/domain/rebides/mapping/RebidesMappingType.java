package org.fenixedu.legalpt.domain.rebides.mapping;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.organizationalStructure.PartyTypeEnum;
import org.fenixedu.academic.domain.organizationalStructure.ScientificAreaUnit;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.DomainObjectLegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.EnumerationLegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.ILegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.StringLegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import pt.ist.fenixframework.FenixFramework;

public enum RebidesMappingType implements ILegalMappingType {
    ID_DOCUMENT_TYPE, CONTRACT_CATEGORY, CONTRACT_SERVICE_REGIME, CONTRACT_BINDING_REGIME, CONTRACT_WAGE_LEVEL, BOOLEAN, GENDER,
    SCHOOL_LEVEL, SCHOOL_LEVEL_ORIGIN, SCIENTIFIC_AREA, DEPARTMENT;

    private static final String ENUMERATION_RESOURCES = "resources.EnumerationResources";

    public Set<?> getValues() {
        switch (this) {
        case ID_DOCUMENT_TYPE:
            return Sets.newHashSet(IDDocumentType.values());
        case CONTRACT_CATEGORY:
//            return RootDomainObject.getInstance().getContractCategoriesSet();
            return Collections.emptySet();
        case CONTRACT_SERVICE_REGIME:
//            return RootDomainObject.getInstance().getContractServiceRegimesSet();
            return Collections.emptySet();
        case CONTRACT_BINDING_REGIME:
//            return RootDomainObject.getInstance().getContractBindingRegimesSet();
            return Collections.emptySet();
        case CONTRACT_WAGE_LEVEL:
//            return RootDomainObject.getInstance().getContractWageLevelsSet();
            return Collections.emptySet();
        case SCIENTIFIC_AREA:
//            Unit institutionUnit = RootDomainObject.getInstance().getInstitutionUnit();
//            Collection<Unit> scientificAreas = institutionUnit.getAllSubUnits(PartyTypeEnum.SCIENTIFIC_AREA);
//            return Sets.newHashSet(scientificAreas);
            return Collections.emptySet();
        case BOOLEAN:
            return Sets.newHashSet(Boolean.TRUE, Boolean.FALSE);
        case GENDER:
            return Sets.newHashSet(Gender.values());
        case SCHOOL_LEVEL:
            return Sets.newHashSet(SchoolLevelType.values());
        case SCHOOL_LEVEL_ORIGIN:
//            return RootDomainObject.getInstance().getSchoolLevelsOriginSet();
            return Collections.emptySet();
        case DEPARTMENT:
            Collection<Unit> departments = Collections2.filter(Bennu.getInstance().getInstitutionUnit().getAllSubUnits(),
                    new Predicate<org.fenixedu.academic.domain.organizationalStructure.Unit>() {
                        @Override
                        public boolean apply(final org.fenixedu.academic.domain.organizationalStructure.Unit entry) {
                            return entry.getPartyType().getType() == PartyTypeEnum.DEPARTMENT;
                        }
                    });
            return Sets.newHashSet(departments);
        default:
            return Collections.EMPTY_SET;
        }
    }

    @Override
    public LegalMapping createMapping(LegalReport report) {
        switch (this) {

        case CONTRACT_CATEGORY:
        case CONTRACT_SERVICE_REGIME:
        case CONTRACT_BINDING_REGIME:
        case CONTRACT_WAGE_LEVEL:
        case SCIENTIFIC_AREA:
        case SCHOOL_LEVEL_ORIGIN:
        case DEPARTMENT:
            return new DomainObjectLegalMapping(report, this);
        case BOOLEAN:
            return new StringLegalMapping(report, this);
        default:
            return new EnumerationLegalMapping(report, this);
        }
    }

    @Override
    public LocalizedString getLocalizedNameKey(final String key) {

        LocalizedString mls = new LocalizedString();
        switch (this) {
        case BOOLEAN:
            return LegalPTUtil.bundleI18N("label." + key);

        case GENDER:
            final Gender gender = Gender.valueOf(key);
            mls = mls.with(I18N.getLocale(), gender.toLocalizedString(I18N.getLocale()));
            return mls;

        case ID_DOCUMENT_TYPE:
            final IDDocumentType idDocumentType = IDDocumentType.valueOf(key);
            mls = mls.with(I18N.getLocale(), idDocumentType.getLocalizedName(I18N.getLocale()));
            return mls;

        case CONTRACT_CATEGORY:
            return new LocalizedString(I18N.getLocale(), "TODO_CONTRACT_CATEGORY");// ((ContractCategory) FenixFramework.getDomainObject(key)).getName();

        case CONTRACT_SERVICE_REGIME:
            return new LocalizedString(I18N.getLocale(), "TODO_CONTRACT_SERVICE_REGIME");// ((ContractServiceRegime) FenixFramework.getDomainObject(key)).getName();

        case CONTRACT_BINDING_REGIME:
            return new LocalizedString(I18N.getLocale(), "TODO_CONTRACT_BINDING_REGIME");// ((ContractBindingRegime) FenixFramework.getDomainObject(key)).getName();

        case CONTRACT_WAGE_LEVEL:
            return new LocalizedString(I18N.getLocale(), "TODO_CONTRACT_WAGE_LEVEL");// ((ContractWageLevel) FenixFramework.getDomainObject(key)).getName();

        case SCIENTIFIC_AREA:
            return ((ScientificAreaUnit) FenixFramework.getDomainObject(key)).getPartyName().toLocalizedString();

        case SCHOOL_LEVEL:
            final SchoolLevelType schoolLevel = SchoolLevelType.valueOf(key);
            return localizedName(schoolLevel, I18N.getLocale());

        case SCHOOL_LEVEL_ORIGIN:
            return new LocalizedString(I18N.getLocale(), "TODO_SCHOOL_LEVEL_ORIGIN");// ((SchoolLevelOrigin) FenixFramework.getDomainObject(key)).getName());

        case DEPARTMENT:
            return ((Unit) FenixFramework.getDomainObject(key)).getPartyName().toLocalizedString();

        default:
            return new LocalizedString();
        }
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public LocalizedString getName() {
        return LegalPTUtil.bundleI18N(getQualifiedNameKey());
    }

    @Override
    public LocalizedString getDescription() {
        return LegalPTUtil.bundleI18N(getQualifiedDescriptionKey());
    }

    protected String getQualifiedDescriptionKey() {
        return this.getClass().getName() + "." + name() + ".description";
    }

    protected String getQualifiedNameKey() {
        return this.getClass().getName() + "." + name() + ".name";
    }

    private LocalizedString localizedName(final SchoolLevelType schoolLevel, final Locale... locales) {
        return localizedName(ENUMERATION_RESOURCES, schoolLevel.getQualifiedName(), locales);
    }

    private LocalizedString localizedName(final String bundle, final String key, final Locale... locales) {
        return BundleUtil.getLocalizedString(ENUMERATION_RESOURCES, key);
    }

}
