package org.fenixedu.ulisboa.integration.sas.service;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academicextensions.domain.person.dataShare.DataShareAuthorization;
import org.fenixedu.academicextensions.domain.person.dataShare.DataShareAuthorizationType;

public class SasDataShareAuthorizationServices {

    private static final String SAS_AUTHORIZATION_CODE = "SAS";

    static public boolean isAuthorizationTypeActive() {
        return getAuthorizationType() != null && getAuthorizationType().isActive();
    }

    static public boolean isAnswered(Person person) {
        final DataShareAuthorizationType authorizationType = getAuthorizationType();
        return authorizationType == null ? false : DataShareAuthorization.findLatest(person, authorizationType) != null;
    }

    static public boolean isDataShareAllowed(Person person) {
        final DataShareAuthorizationType authorizationType = getAuthorizationType();
        return authorizationType == null ? false : DataShareAuthorization.isDataShareAllowed(person, authorizationType);
    }

    private static DataShareAuthorizationType getAuthorizationType() {
        return DataShareAuthorizationType.findUnique(SAS_AUTHORIZATION_CODE);
    }

}
