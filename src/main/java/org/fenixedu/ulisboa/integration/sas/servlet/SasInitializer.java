package org.fenixedu.ulisboa.integration.sas.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academic.domain.person.identificationDocument.IdentificationDocumentType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.domain.mapping.DomainObjectLegalMapping;
import org.fenixedu.legalpt.domain.mapping.EnumerationLegalMapping;
import org.fenixedu.legalpt.domain.raides.mapping.LegalMappingType;
import org.fenixedu.ulisboa.integration.sas.domain.EducationLevelTypeMapping;
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

        EducationLevelTypeMapping.registerEvents();

        SicabeExternalService.init();
        migrateIDDocumentTypeEnumerationMappingToIdentificationDocumentTypeDomainMapping();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @Atomic(mode = TxMode.WRITE)
    private void migrateIDDocumentTypeEnumerationMappingToIdentificationDocumentTypeDomainMapping() {
        String sourceType = "ID_DOCUMENT_TYPE";
        LegalMappingType targetType = LegalMappingType.IDENTIFICATION_DOCUMENT_TYPE;

        EnumerationLegalMapping sourceMapping =
                Bennu.getInstance().getLegalMappingsSet().stream().filter(EnumerationLegalMapping.class::isInstance)
                        .map(EnumerationLegalMapping.class::cast).filter(mapping -> mapping.getType().equals(sourceType))
                        .findFirst().orElse(null);

        if (sourceMapping == null) {
            Log.warn("Source EnumerationLegalMapping not found for type: " + sourceType);
            return;
        }

        if (DomainObjectLegalMapping.find(sourceMapping.getLegalReport(), targetType) == null) {
            Log.warn("Creating new DomainObjectLegalMapping for type: " + targetType);
            DomainObjectLegalMapping targetMapping = new DomainObjectLegalMapping(sourceMapping.getLegalReport(), targetType);
            sourceMapping.getLegalMappingEntriesSet().forEach(e -> {
                DomainObject key = IdentificationDocumentType.findByCode(e.getMappingKey()).orElseThrow(
                        () -> new IllegalArgumentException("Invalid Identification Document Type Code: " + e.getMappingKey()));

                targetMapping.addEntry(key, e.getMappingValue());
            });
            Log.warn("New DomainObjectLegalMapping created for type: " + targetType);
        }
    }
}