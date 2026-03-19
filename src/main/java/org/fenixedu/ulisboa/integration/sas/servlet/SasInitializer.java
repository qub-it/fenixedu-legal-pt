package org.fenixedu.ulisboa.integration.sas.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.domain.mapping.EnumerationLegalMapping;
import org.fenixedu.ulisboa.integration.sas.domain.EducationLevelTypeMapping;
import org.fenixedu.ulisboa.integration.sas.domain.SocialServicesConfiguration;
import org.fenixedu.ulisboa.integration.sas.service.sicabe.SicabeExternalService;

import com.qubit.terra.framework.services.logging.Log;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

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
        deleteIDDocumentTypeEnumerationMappings();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @Atomic(mode = TxMode.WRITE)
    private void deleteIDDocumentTypeEnumerationMappings() {
        String sourceType = "ID_DOCUMENT_TYPE";

        EnumerationLegalMapping sourceMapping =
                Bennu.getInstance().getLegalMappingsSet().stream().filter(EnumerationLegalMapping.class::isInstance)
                        .map(EnumerationLegalMapping.class::cast).filter(mapping -> mapping.getType().equals(sourceType))
                        .findFirst().orElse(null);

        if (sourceMapping == null) {
            Log.warn("Source EnumerationLegalMapping not found for type: " + sourceType);
            return;
        }

        sourceMapping.getLegalMappingEntriesSet().forEach(sourceMapping::deleteEntry);
        sourceMapping.delete();
        Log.warn("Source ID_DOCUMENT_TYPE EnumerationLegalMapping entries and mapping deleted");
    }
}