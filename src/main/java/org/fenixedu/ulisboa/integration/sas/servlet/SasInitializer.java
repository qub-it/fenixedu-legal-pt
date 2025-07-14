package org.fenixedu.ulisboa.integration.sas.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academic.domain.student.personaldata.EducationLevelType;
import org.fenixedu.academic.domain.student.personaldata.ProfessionCategoryType;
import org.fenixedu.academic.domain.student.personaldata.ProfessionalStatusType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.domain.mapping.DomainObjectLegalMapping;
import org.fenixedu.legalpt.domain.mapping.EnumerationLegalMapping;
import org.fenixedu.legalpt.domain.raides.mapping.LegalMappingType;
import org.fenixedu.ulisboa.integration.sas.domain.SchoolLevelTypeMapping;
import org.fenixedu.ulisboa.integration.sas.domain.SocialServicesConfiguration;
import org.fenixedu.ulisboa.integration.sas.service.sicabe.SicabeExternalService;

import com.qubit.terra.framework.services.logging.Log;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.DomainObject;

@WebListener
public class SasInitializer implements ServletContextListener {

    @Override
    @Atomic(mode = TxMode.SPECULATIVE_READ)
    public void contextInitialized(ServletContextEvent event) {
        //Bootstrap SocialServicesConfiguration Singleton
        if (Bennu.getInstance().getSocialServicesConfiguration() == null) {
            SocialServicesConfiguration socialServicesConfiguration = new SocialServicesConfiguration();
        }

        SchoolLevelTypeMapping.registerEvents();
        
        SicabeExternalService.init();
        createNewDomainMappingsFromEnumerationMappings();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @Atomic(mode = TxMode.WRITE)
    private void createNewDomainMappingsFromEnumerationMappings() {
        migrateMapping("SCHOOL_LEVEL", LegalMappingType.EDUCATION_LEVEL);
        migrateMapping("PROFESSION_TYPE", LegalMappingType.PROFESSION_CATEGORY);
        migrateMapping("PROFESSIONAL_SITUATION_CONDITION", LegalMappingType.PROFESSIONAL_STATUS);
        migrateMapping("PRECEDENT_SCHOOL_LEVEL", LegalMappingType.PRECEDENT_EDUCATION_LEVEL);
    }

    private void migrateMapping(String sourceType, LegalMappingType targetType) {
        EnumerationLegalMapping sourceMapping =
                Bennu.getInstance().getLegalMappingsSet().stream().filter(EnumerationLegalMapping.class::isInstance)
                        .map(EnumerationLegalMapping.class::cast).filter(mapping -> mapping.getType().equals(sourceType))
                        .findFirst().orElse(null);

        if (sourceMapping == null) {
            Log.warn("No " + sourceType + " enumeration mapping found");
            return;
        }

        if (DomainObjectLegalMapping.find(sourceMapping.getLegalReport(), targetType) == null) {
            Log.warn("Creating new DomainObjectLegalMapping for type: " + targetType);
            DomainObjectLegalMapping targetMapping = new DomainObjectLegalMapping(sourceMapping.getLegalReport(), targetType);
            addEntriesToTargetMapping(targetType, sourceMapping, targetMapping);
            Log.warn("New DomainObjectLegalMapping created for type: " + targetType);
        }
    }

    private static void addEntriesToTargetMapping(LegalMappingType targetType, EnumerationLegalMapping sourceMapping,
            DomainObjectLegalMapping targetMapping) {

        sourceMapping.getLegalMappingEntriesSet().forEach(e -> {
            DomainObject key = null;

            switch (targetType) {
            case EDUCATION_LEVEL:
            case PRECEDENT_EDUCATION_LEVEL:
                key = EducationLevelType.findByCode(e.getMappingKey())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Education Level Code: " + e.getMappingKey()));
                break;
            case PROFESSION_CATEGORY:
                key = ProfessionCategoryType.findByCode(e.getMappingKey()).orElseThrow(
                        () -> new IllegalArgumentException("Invalid Profession Category Code: " + e.getMappingKey()));
                break;
            case PROFESSIONAL_STATUS:
                key = ProfessionalStatusType.findByCode(e.getMappingKey()).orElseThrow(
                        () -> new IllegalArgumentException("Invalid Professional Status Code: " + e.getMappingKey()));
                break;
            default:
                Log.warn("Unsupported targetType: " + targetType);
            }

            if (key != null) {
                targetMapping.addEntry(key, e.getMappingValue());
            }
        });
    }
}