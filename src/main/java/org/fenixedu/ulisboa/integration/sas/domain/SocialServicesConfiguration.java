package org.fenixedu.ulisboa.integration.sas.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.academic.domain.student.curriculum.CreditsReasonType;
import org.fenixedu.bennu.core.domain.Bennu;

public class SocialServicesConfiguration extends SocialServicesConfiguration_Base {

    public SocialServicesConfiguration() {
        super();
        setBennu(Bennu.getInstance());
    }

    public void edit(int numberOfMonthsOfAcademicYear, String email, String institutionCode,
            Collection<IngressionType> ingressionTypesWhichAreDegreeTransfer,
            Collection<IngressionType> ingressionTypesWithExternalData, Collection<CreditsReasonType> creditsReasonTypes,
            StatuteType statuteType) {
        SocialServicesConfiguration config = Bennu.getInstance().getSocialServicesConfiguration();
        config.setNumberOfMonthsOfAcademicYear(numberOfMonthsOfAcademicYear);
        config.setEmail(email);
        config.setInstitutionCode(institutionCode);

        Set<IngressionType> ingressionTypeWhichAreDegreeTransferSet = config.getIngressionTypeWhichAreDegreeTransferSet();
        ingressionTypeWhichAreDegreeTransferSet.clear();
        ingressionTypeWhichAreDegreeTransferSet.addAll(ingressionTypesWhichAreDegreeTransfer);

        Set<IngressionType> ingressionTypesWithExternalDataSet = config.getIngressionTypesWithExternalDataSet();
        ingressionTypesWithExternalDataSet.clear();
        ingressionTypesWithExternalDataSet.addAll(ingressionTypesWithExternalData);

        Set<CreditsReasonType> creditsReasonType = config.getCreditsReasonTypesSet();
        creditsReasonType.clear();
        creditsReasonType.addAll(creditsReasonTypes);

        config.setStatuteTypeSas(statuteType);
    }

    public static SocialServicesConfiguration getInstance() {
        return Bennu.getInstance().getSocialServicesConfiguration();
    }

    public Collection<String> getInstitutionCodes() {
        return StringUtils.isBlank(getInstitutionCode()) ? Collections.emptySet() : Stream.of(getInstitutionCode().split(","))
                .map(s -> s.trim()).collect(Collectors.toSet());
    }

    public boolean ingressionTypeRequiresExternalData(final Registration registration) {
        return registration.getIngressionType() != null
                && getIngressionTypesWithExternalDataSet().contains(registration.getIngressionType());
    }

}
