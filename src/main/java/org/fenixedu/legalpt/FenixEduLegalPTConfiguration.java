package org.fenixedu.legalpt;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

abstract public class FenixEduLegalPTConfiguration {

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

    @ConfigurationManager(description = "FenixEdu Legal-PT Configuration")
    public interface ConfigurationProperties {

        @ConfigurationProperty(key = "a3es.url", defaultValue = "http://testes.a3es.pt/si/iportal.php")
        public String a3esURL();
    }

}
