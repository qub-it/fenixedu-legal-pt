package org.fenixedu.legalpt.services.a3es.process;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNLIMITED;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.legalpt.domain.a3es.A3esInstance;
import org.fenixedu.legalpt.dto.a3es.A3esProcessBean;
import org.fenixedu.legalpt.dto.a3es.A3esStudentsBean;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.domain.services.student.RegistrationDataServices;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class A3esHarvestStudentsDataService {

    private final ExecutionYear year;
    private final DegreeCurricularPlan degreeCurricularPlan;

    public A3esHarvestStudentsDataService(final A3esProcessBean bean) {
        this.year = bean.getExecutionYear();
        this.degreeCurricularPlan = bean.getDegreeCurricularPlan();

        final Collection<Registration> registrations = getAllRegistrations();

        final A3esStudentsBean data = bean.getStudentsData();

        fillStudentsEnroled(data, registrations);
        fillStudentsByGender(data, registrations);
        fillStudentsByCurricularYear(data, registrations);
//        fillDemandAmongStudents(data);
//        fillStudentsExtraInformation(data);
//        fillStudentsSupport(data);
//        fillStudentIntegrationMeasures(data);
//        fillFinanceAndEmploymentCounseling(data);
//        fillStudentSurveysImpact(data);
//        fillMobilityStrategy(data);
    }

    private Collection<Registration> getAllRegistrations() {
        return this.year.getExecutionPeriodsSet().stream()
                .flatMap(semester -> semester.getEnrolmentsSet().stream().filter(enrolment -> !enrolment.isAnnulled())
                        .map(enrolment -> enrolment.getRegistration())
                        .filter(registration -> registration.getDegree() == this.degreeCurricularPlan.getDegree())
                        .filter(registration -> !isAgreementPartOfMobilityReport(registration))
                        .filter(registration -> RegistrationDataServices.getRegistrationData(registration, this.year) != null))
                .collect(Collectors.toSet());
    }

    static private boolean isAgreementPartOfMobilityReport(final Registration registration) {
        return A3esInstance.getInstance().getMobilityAgreementsSet().contains(registration.getRegistrationProtocol());
    }

    static private void fillStudentsEnroled(final A3esStudentsBean data, final Collection<Registration> registrations) {
        data.addField("q-II.5.1.1", "studentsEnroled", String.valueOf(registrations.size()), _UNLIMITED);
    }

    static private void fillStudentsByGender(final A3esStudentsBean data, final Collection<Registration> registrations) {
        data.addField("q-II.5.1.2.a", "studentsMale", getPercentOfStudentsByGender(registrations, Gender.MALE), _UNLIMITED);
        data.addField("q-II.5.1.2.b", "studentsFemale", getPercentOfStudentsByGender(registrations, Gender.FEMALE), _UNLIMITED);
    }

    static private String getPercentOfStudentsByGender(final Collection<Registration> registrations, final Gender gender) {
        final int total = registrations.size();
        if (total == 0) {
            return "0 %";
        }

        final long filtered = registrations.stream().filter(r -> r.getPerson().getGender() == gender).count();
        return new BigDecimal(filtered).divide(new BigDecimal(total), 2, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100))
                .stripTrailingZeros().toPlainString() + " %";
    }

    private void fillStudentsByCurricularYear(final A3esStudentsBean data, final Collection<Registration> registrations) {
        for (final Map.Entry<Integer, Collection<Registration>> entry : groupStudentsByCurricularYear(registrations).entrySet()) {
            final Integer curricularYear = entry.getKey();
            final int number = entry.getValue().size();

            // TODO legidio, make this more generic
            final CycleType cycleType = this.degreeCurricularPlan.getDegreeType().getFirstOrderedCycleType();
            final int cycle = cycleType == null ? 1 : cycleType.getWeight();

            data.addField("q-II.5.1.3." + curricularYear, "studentsCurricularYear" + curricularYear + cycle,
                    String.valueOf(number), _UNLIMITED);
        }
    }

    private Map<Integer, Collection<Registration>> groupStudentsByCurricularYear(final Collection<Registration> registrations) {
        final Multimap<Integer, Registration> registrationsByYear = ArrayListMultimap.create();

        for (final Registration registration : registrations) {
            final int curricularYear = RegistrationServices.getCurricularYear(registration, this.year).getResult();
            registrationsByYear.put(curricularYear, registration);
        }

        return registrationsByYear.asMap();
    }

}
