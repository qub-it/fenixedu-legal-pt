package org.fenixedu.ulisboa.specifications.domain.legal.raides;

import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.student.RegistrationProtocol;

public class RaidesConfiguration {

    public boolean isProtocolCandidateForEnrolledStudentsReport(final RegistrationProtocol agreement) {
        return false;
    }
    
    public boolean isProtocolCandidateForInternationalMobilityReport(final RegistrationProtocol agreeemnt) {
        return false;
    }
    
    public boolean isIngressionDegreeTransfer(final IngressionType ingression) {
        return false;
    }
    
    public boolean isIngressionDegreeChange(final IngressionType ingression) {
        return false;
    }
    
}
