package org.fenixedu.legalpt.domain.raides;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.legalpt.domain.raides.report.RaidesRequestParameter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Strings;

public class RaidesReportRequestDefaultData implements IRaidesReportRequestDefaultData {

    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");

    public RaidesReportRequestDefaultData() {
    }

    @Override
    public void fill(final RaidesRequestParameter raidesRequestParameter) {
        // AgreementsForEnrolled
        raidesRequestParameter.getAgreementsForEnrolled().addAll(RaidesInstance.getInstance().getEnrolledAgreementsSet());

        // AgreementsForMobility
        raidesRequestParameter.getAgreementsForMobility().addAll(RaidesInstance.getInstance().getMobilityAgreementsSet());

        // IngressionsForDegreeChange
        raidesRequestParameter.getIngressionsForDegreeChange()
                .addAll(RaidesInstance.getInstance().getDegreeChangeIngressionsSet());

        // IngressionsForDegreeTransfer
        raidesRequestParameter.getIngressionsForDegreeTransfer()
                .addAll(RaidesInstance.getInstance().getDegreeTransferIngressionsSet());

        // IngressionsForGeneralAccessRegime
        raidesRequestParameter.getIngressionsForGeneralAccessRegime()
                .addAll(RaidesInstance.getInstance().getGeneralAccessRegimeIngressionsSet());

        raidesRequestParameter.setInstitution(Bennu.getInstance().getInstitutionUnit());
        raidesRequestParameter
                .setInstitutionCode(!Strings.isNullOrEmpty(RaidesInstance.getInstance().getInstitutionCode()) ? RaidesInstance
                        .getInstance().getInstitutionCode() : Bennu.getInstance().getInstitutionUnit().getCode());
        raidesRequestParameter.setMoment("1");
        raidesRequestParameter.setInterlocutorName(Authenticate.getUser().getPerson().getName());
        raidesRequestParameter.setInterlocutorEmail(Authenticate.getUser().getPerson().getDefaultEmailAddressValue());
        raidesRequestParameter.setInterlocutorPhone(RaidesInstance.getInstance().getInterlocutorPhone());
        raidesRequestParameter.setFilterEntriesWithErrors(true);
    }

}
