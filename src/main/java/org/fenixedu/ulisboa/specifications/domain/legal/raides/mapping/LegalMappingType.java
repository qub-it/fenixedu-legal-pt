package org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.SchoolPeriodDuration;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.academic.domain.student.RegistrationRegimeType;
import org.fenixedu.academic.domain.student.mobility.MobilityActivityType;
import org.fenixedu.academic.domain.student.mobility.MobilityProgramType;
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
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.FenixFramework;

public enum LegalMappingType implements ILegalMappingType {

    BOOLEAN, GENDER, ID_DOCUMENT_TYPE, CYCLE_TYPE, REGIME_TYPE, GRANT_OWNER_TYPE, REGISTRATION_INGRESSION_TYPE, MARITAL_STATUS,
    SCHOOL_LEVEL, PROFESSIONAL_SITUATION_CONDITION, PROFESSION_TYPE, HIGH_SCHOOL_TYPE, SCHOOL_PERIOD_DURATION,
    INTERNATIONAL_MOBILITY_PROGRAM, INTERNATIONAL_MOBILITY_ACTIVITY, CURRICULAR_YEAR, REGIME_FREQUENCIA, PRECEDENT_SCHOOL_LEVEL,
    MOBILITY_SCHOOL_LEVEL, INTERNATIONAL_MOBILITY_PROGRAM_AGREEMENT, GRADE, INTEGRATED_MASTER_FIRST_CYCLE_CODES,
    DEGREE_CURRICULAR_PLAN_DEGREE_OFICIAL_CODE;

    private static final String ENUMERATION_RESOURCES = "resources.EnumerationResources";

    public Set<?> getValues() {
        switch (this) {
        case BOOLEAN:
            return Sets.newHashSet(Boolean.TRUE, Boolean.FALSE);
        case GENDER:
            return Sets.newHashSet(Gender.values());
        case ID_DOCUMENT_TYPE:
            return Sets.newHashSet(IDDocumentType.values());
        case CYCLE_TYPE:
            return Sets.newHashSet(CycleType.values());
        case REGIME_TYPE:
            return Sets.newHashSet(RegistrationRegimeType.values());
        case GRANT_OWNER_TYPE:
            return Raides.Bolseiro.VALUES();
        case REGISTRATION_INGRESSION_TYPE:
            return Sets.newHashSet(Bennu.getInstance().getIngressionTypesSet());
        case MARITAL_STATUS:
            return Sets.newHashSet(MaritalStatus.values());
        case SCHOOL_LEVEL:
        case PRECEDENT_SCHOOL_LEVEL:
        case MOBILITY_SCHOOL_LEVEL:
            return Sets.newHashSet(SchoolLevelType.values());
        case PROFESSIONAL_SITUATION_CONDITION:
            return Sets.newHashSet(ProfessionalSituationConditionType.values());
        case PROFESSION_TYPE:
            return Sets.newHashSet(ProfessionType.values());
        case HIGH_SCHOOL_TYPE:
            return Sets.newHashSet(AcademicalInstitutionType.values());
        case SCHOOL_PERIOD_DURATION:
            return Sets.newHashSet(SchoolPeriodDuration.values());
        case CURRICULAR_YEAR:
            return Raides.AnoCurricular.VALUES();
        case REGIME_FREQUENCIA:
            return Raides.RegimeFrequencia.VALUES();
        case INTERNATIONAL_MOBILITY_PROGRAM:
            return MobilityProgramType.findAllActive();
        case INTERNATIONAL_MOBILITY_ACTIVITY:
            return MobilityActivityType.findAllActive();
        case INTERNATIONAL_MOBILITY_PROGRAM_AGREEMENT:
            return Sets.newHashSet(Bennu.getInstance().getRegistrationProtocolsSet());
        case GRADE:
            final Set<String> possibleGrades = Sets.newHashSet();
            /*
            for (final GradeScaleType gradeScaleType : GradeScaleType.readAll()) {
                for (final GradeScaleValue gradeScaleValue : gradeScaleType.getGradeScaleValuesSet()) {
                    possibleGrades.add(gradeScaleValue.getAcronym());
                }
            }
            */

            return possibleGrades;
        case INTEGRATED_MASTER_FIRST_CYCLE_CODES:
            return Sets.newHashSet(Bennu.getInstance().getDegreesSet().stream()
                    .filter(d -> d.getDegreeType().isIntegratedMasterDegree()).collect(Collectors.toSet()));

        case DEGREE_CURRICULAR_PLAN_DEGREE_OFICIAL_CODE:
            return Bennu.getInstance().getDegreesSet().stream().flatMap(d -> d.getDegreeCurricularPlansSet().stream())
                    .collect(Collectors.toSet());
        default:
            return Collections.EMPTY_SET;
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

    @Override
    public LegalMapping createMapping(LegalReport report) {
        switch (this) {
        case REGISTRATION_INGRESSION_TYPE:
        case INTERNATIONAL_MOBILITY_PROGRAM:
        case INTERNATIONAL_MOBILITY_ACTIVITY:
        case INTERNATIONAL_MOBILITY_PROGRAM_AGREEMENT:
        case INTEGRATED_MASTER_FIRST_CYCLE_CODES:
        case DEGREE_CURRICULAR_PLAN_DEGREE_OFICIAL_CODE:
            return new DomainObjectLegalMapping(report, this);
        case BOOLEAN:
        case CURRICULAR_YEAR:
        case REGIME_FREQUENCIA:
        case GRADE:
            return new StringLegalMapping(report, this);
        default:
            return new EnumerationLegalMapping(report, this);
        }

    }

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
        case CYCLE_TYPE:
            final CycleType cycleType = CycleType.valueOf(key);
            return cycleType.getDescriptionI18N();
        case REGIME_TYPE:
            final RegistrationRegimeType registrationRegimeType = RegistrationRegimeType.valueOf(key);
            mls = mls.with(I18N.getLocale(), registrationRegimeType.getLocalizedName());
            return mls;
        case GRANT_OWNER_TYPE:
            return Raides.Bolseiro.LOCALIZED_NAME(key);
        case REGISTRATION_INGRESSION_TYPE:
            return ((IngressionType) FenixFramework.getDomainObject(key)).getDescription();
        case MARITAL_STATUS:
            final MaritalStatus maritalStatus = MaritalStatus.valueOf(key);
            mls = mls.with(I18N.getLocale(), maritalStatus.getLocalizedName());
            return mls;
        case SCHOOL_LEVEL:
        case PRECEDENT_SCHOOL_LEVEL:
        case MOBILITY_SCHOOL_LEVEL:
            final SchoolLevelType schoolLevel = SchoolLevelType.valueOf(key);
            mls = mls.with(I18N.getLocale(), schoolLevel.getLocalizedName());
            return mls;
        case PROFESSIONAL_SITUATION_CONDITION:
            final ProfessionalSituationConditionType condition = ProfessionalSituationConditionType.valueOf(key);
            mls = mls.with(I18N.getLocale(), condition.getLocalizedName());
            return mls;
        case PROFESSION_TYPE:
            final ProfessionType professionType = ProfessionType.valueOf(key);
            mls = mls.with(I18N.getLocale(), professionType.getLocalizedName());
            return mls;
        case HIGH_SCHOOL_TYPE:
            final AcademicalInstitutionType academicalInstitutionType = AcademicalInstitutionType.valueOf(key);
            return localizedName(academicalInstitutionType, I18N.getLocale());
        case SCHOOL_PERIOD_DURATION:
            return LegalPTUtil.bundleI18N("label.SchoolPeriodDuration." + key);
        case CURRICULAR_YEAR:
            return Raides.AnoCurricular.LOCALIZED_NAME(key);
        case REGIME_FREQUENCIA:
            return Raides.RegimeFrequencia.LOCALIZED_NAME(key);
        case INTERNATIONAL_MOBILITY_PROGRAM:
            return ((MobilityProgramType) FenixFramework.getDomainObject(key)).getName();
        case INTERNATIONAL_MOBILITY_ACTIVITY:
            return ((MobilityActivityType) FenixFramework.getDomainObject(key)).getName();
        case INTERNATIONAL_MOBILITY_PROGRAM_AGREEMENT:
            return ((RegistrationProtocol) FenixFramework.getDomainObject(key)).getDescription();
        case GRADE:
            return new LocalizedString(ULisboaConstants.DEFAULT_LOCALE, key);
        case INTEGRATED_MASTER_FIRST_CYCLE_CODES:
            final Degree degree = (Degree) FenixFramework.getDomainObject(key);
            return new LocalizedString(I18N.getLocale(), "[" + degree.getCode() + "] " + degree.getPresentationName());
        case DEGREE_CURRICULAR_PLAN_DEGREE_OFICIAL_CODE:
            final DegreeCurricularPlan degreeCurricularPlan = FenixFramework.getDomainObject(key);
            return new LocalizedString(I18N.getLocale(),
                    "[" + degreeCurricularPlan.getDegree().getCode() + "] " + degreeCurricularPlan.getPresentationName());
        default:
            return new LocalizedString();
        }
    }

    private LocalizedString localizedName(final SchoolLevelType schoolLevel, final Locale... locales) {
        return localizedName(ENUMERATION_RESOURCES, schoolLevel.getQualifiedName(), locales);
    }

    private LocalizedString localizedName(final SchoolPeriodDuration schoolPeriodDuration, final Locale... locales) {
        return localizedName(ENUMERATION_RESOURCES,
                schoolPeriodDuration.getClass().getSimpleName() + "." + schoolPeriodDuration.name(), locales);
    }

    private LocalizedString localizedName(final AcademicalInstitutionType academicalInstitutionType, final Locale... locales) {
        return localizedName(ENUMERATION_RESOURCES,
                academicalInstitutionType.getClass().getSimpleName() + "." + academicalInstitutionType.name(), locales);
    }

    private LocalizedString localizedName(final ProfessionType professionType, final Locale... locales) {
        return localizedName(ENUMERATION_RESOURCES, professionType.getQualifiedName(), locales);
    }

    private LocalizedString localizedName(final ProfessionalSituationConditionType condition, final Locale... locales) {
        return localizedName(ENUMERATION_RESOURCES, condition.getQualifiedName(), locales);
    }

    private LocalizedString localizedName(final MaritalStatus maritalStatus, final Locale... locales) {
        return localizedName(ENUMERATION_RESOURCES, maritalStatus.getClass().getName() + "." + maritalStatus.name(), locales);
    }

    private LocalizedString localizedName(final RegistrationRegimeType registrationRegimeType, final Locale... locales) {
        return localizedName(ENUMERATION_RESOURCES, registrationRegimeType.getQualifiedName(), locales);
    }

    private LocalizedString localizedName(final String bundle, final String key, final Locale... locales) {
        return BundleUtil.getLocalizedString(ENUMERATION_RESOURCES, key);
    }

}
