package org.fenixedu.legalpt.domain.a3es.mapping;

import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.TeacherCategory;
import org.fenixedu.academic.domain.degreeStructure.CourseLoadType;
import org.fenixedu.academic.domain.person.qualifications.QualificationLevel;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.domain.mapping.DomainObjectLegalMapping;
import org.fenixedu.legalpt.domain.mapping.EnumerationLegalMapping;
import org.fenixedu.legalpt.domain.mapping.ILegalMappingType;
import org.fenixedu.legalpt.domain.mapping.LegalMapping;
import org.fenixedu.legalpt.domain.report.LegalReport;
import org.fenixedu.legalpt.domain.teacher.SpecialistTitle;
import org.fenixedu.legalpt.util.LegalPTUtil;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.FenixFramework;

public enum A3esMappingType implements ILegalMappingType {

    CONTRACT_CATEGORY {

        @Override
        public Set<?> getValues() {
            return Bennu.getInstance().getTeacherCategorySet();
        }

        @Override
        public LegalMapping createMapping(final LegalReport report) {
            return new DomainObjectLegalMapping(report, this);
        }

        @Override
        public LocalizedString getLocalizedNameKey(final String key) {
            final TeacherCategory object = (TeacherCategory) FenixFramework.getDomainObject(key);
            return FenixFramework.isDomainObjectValid(object) ? new LocalizedString(I18N.getLocale(),
                    "[" + object.getCode() + "] ").append(object.getName()) : new LocalizedString(I18N.getLocale(), "-----");
        }
    },

    SCHOOL_LEVEL {

        @Override
        public Set<?> getValues() {
            return Bennu.getInstance().getQualificationLevelsSet();
        }

        @Override
        public LegalMapping createMapping(final LegalReport report) {
            return new DomainObjectLegalMapping(report, this);
        }

        @Override
        public LocalizedString getLocalizedNameKey(final String key) {
            final QualificationLevel object = (QualificationLevel) FenixFramework.getDomainObject(key);
            return FenixFramework.isDomainObjectValid(object) ? new LocalizedString(I18N.getLocale(),
                    "[" + object.getCode() + "] ").append(object.getName()) : new LocalizedString(I18N.getLocale(), "-----");
        }
    },

    SHIFT_TYPE {

        @Override
        public Set<?> getValues() {
            return CourseLoadType.findAll().collect(Collectors.toSet());
        }

        @Override
        public LegalMapping createMapping(final LegalReport report) {
            return new EnumerationLegalMapping(report, this);
        }

        @Override
        public LocalizedString getLocalizedNameKey(final String key) {
            final CourseLoadType object = CourseLoadType.of(key);
            return new LocalizedString(I18N.getLocale(), "[" + object.getInitials().getContent() + "] ")
                    .append(object.getName().getContent());
        }
    },

    SPECIALIST_TITLE {

        @Override
        public Set<?> getValues() {
            return Sets.newHashSet(SpecialistTitle.values());
        }

        @Override
        public LegalMapping createMapping(final LegalReport report) {
            return new EnumerationLegalMapping(report, this);
        }

        @Override
        public LocalizedString getLocalizedNameKey(final String key) {
            final SpecialistTitle title = SpecialistTitle.valueOf(key);
            return new LocalizedString(I18N.getLocale(), title.getPresentationName());
        }
    }

    ;

    abstract public Set<?> getValues();

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
        return A3esMappingType.class.getName() + "." + name() + ".description";
    }

    protected String getQualifiedNameKey() {
        return A3esMappingType.class.getName() + "." + name() + ".name";
    }

    @Override
    abstract public LegalMapping createMapping(final LegalReport report);

    @Override
    abstract public LocalizedString getLocalizedNameKey(final String key);

}
