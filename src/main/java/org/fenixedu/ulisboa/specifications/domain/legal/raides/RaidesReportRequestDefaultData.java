package org.fenixedu.ulisboa.specifications.domain.legal.raides;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesPeriodInputType;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesRequestParameter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Strings;

public class RaidesReportRequestDefaultData implements IRaidesReportRequestDefaultData {

    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");

    public RaidesReportRequestDefaultData() {
    }

    @Override
    public void fill(final RaidesRequestParameter raidesRequestParameter) {

        // Enrolment periods

        raidesRequestParameter.addPeriod(RaidesPeriodInputType.ENROLLED, ExecutionYear.readExecutionYearByName("2015/2016"),
                DATETIME_FORMATTER.parseDateTime("01/04/2015").toLocalDate(),
                DATETIME_FORMATTER.parseDateTime("31/03/2016").toLocalDate(), true, false, BigDecimal.ZERO, BigDecimal.ZERO,
                false, 0, 0);

        // Graduation periods

        // International Mobility
        raidesRequestParameter.addPeriod(RaidesPeriodInputType.INTERNATIONAL_MOBILITY,
                ExecutionYear.readExecutionYearByName("2015/2016"), DATETIME_FORMATTER.parseDateTime("01/09/2015").toLocalDate(),
                DATETIME_FORMATTER.parseDateTime("31/03/2016").toLocalDate(), true, false, BigDecimal.ZERO, BigDecimal.ZERO,
                false, 0, 0);

        // Degrees
        raidesRequestParameter.getDegrees().addAll(RaidesInstance.getInstance().getDegreesToReportSet());

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
        raidesRequestParameter.setMoment("2");
        raidesRequestParameter.setInterlocutorName(Authenticate.getUser().getPerson().getName());
        raidesRequestParameter.setInterlocutorEmail(Authenticate.getUser().getPerson().getDefaultEmailAddressValue());
        raidesRequestParameter.setInterlocutorPhone(RaidesInstance.getInstance().getInterlocutorPhone());
        raidesRequestParameter.setFilterEntriesWithErrors(true);
    }

}
