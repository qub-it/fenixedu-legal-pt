package org.fenixedu.legalpt.services.a3es.process;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.createEmptyMLS;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.i18n;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.legalpt.dto.a3es.A3esStudentsBean;
import org.joda.time.DateTime;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class A3esHarvestStudentsDataService {

    private final DegreeCurricularPlan degreeCurricularPlan;
    private final ExecutionYear year;
    private final A3esStudentsBean data;

    public static int AGE_20YEARS = 20;
    public static int AGE_24YEARS = 24;
    public static int AGE_28YEARS = 28;

    public A3esHarvestStudentsDataService(DegreeCurricularPlan degreeCurricularPlan, ExecutionYear executionYear) {
        this.degreeCurricularPlan = degreeCurricularPlan;
        this.year = executionYear;
        this.data = new A3esStudentsBean();
    }

    public A3esStudentsBean getStudentsData() {
        fillStudentsByGender();
        fillStudentsByAge();
        fillStudentsByCurricularYear();
        fillDemandAmongStudents();
        fillStudentsExtraInformation();
        fillStudentsSupport();
        fillStudentIntegrationMeasures();
        fillFinanceAndEmploymentCounseling();
        fillStudentSurveysImpact();
        fillMobilityStrategy();

        return this.data;
    }

    private void fillStudentsByGender() {
        final BigDecimal malePercentage = getPercentageOfStudentsByGender(Gender.MALE);
        final BigDecimal femalePercentage = getPercentageOfStudentsByGender(Gender.FEMALE);

        TreeMap<String, String> studentsByGender = new TreeMap<String, String>();
        studentsByGender.put(i18n("label.female"), String.valueOf(femalePercentage));
        studentsByGender.put(i18n("label.male"), String.valueOf(malePercentage));
        this.data.setStudentsByGender(studentsByGender);
    }

    private BigDecimal getPercentageOfStudentsByGender(final Gender gender) {
        Collection<Registration> allRegistrations = getAllRegistrations();

        final Integer numberOfStudentsByGender = Collections2.filter(allRegistrations, new Predicate<Registration>() {
            @Override
            public boolean apply(Registration registration) {
                Student student = registration.getStudent();
                return student.getPerson().getGender() == gender;
            }
        }).size();

        if (allRegistrations.size() == 0) {
            return new BigDecimal(0);
        }

        return new BigDecimal(numberOfStudentsByGender).divide(new BigDecimal(allRegistrations.size()), 2, RoundingMode.HALF_EVEN)
                .multiply(new BigDecimal(100));
    }

    private Collection<Registration> getAllRegistrations() {

        final Set<Registration> result = Sets.newHashSet();
        for (final Registration registration : this.degreeCurricularPlan.getStudentCurricularPlansSet().stream()
                .map(scp -> scp.getRegistration()).collect(Collectors.toSet())) {

            if (!registration.getEnrolments(this.year).isEmpty()) {
                result.add(registration);
            }
        }

        return result;
    }

    private void fillStudentsByAge() {
        final BigDecimal rangeUnder20 = getPercentageOfStudentsByAge(null, AGE_20YEARS);
        final BigDecimal range20_23 = getPercentageOfStudentsByAge(AGE_20YEARS, AGE_24YEARS);
        final BigDecimal range24_27 = getPercentageOfStudentsByAge(AGE_24YEARS, AGE_28YEARS);
        final BigDecimal rangeOver28 = getPercentageOfStudentsByAge(AGE_28YEARS, null);

        TreeMap<String, String> studentsByAge = new TreeMap<String, String>();
        studentsByAge.put(i18n("label.rangeUnder20"), String.valueOf(rangeUnder20));
        studentsByAge.put(i18n("label.range20_23"), String.valueOf(range20_23));
        studentsByAge.put(i18n("label.range24_27"), String.valueOf(range24_27));
        studentsByAge.put(i18n("label.rangeOver28"), String.valueOf(rangeOver28));

        this.data.setStudentsByAge(studentsByAge);
    }

    private BigDecimal getPercentageOfStudentsByAge(final Integer minAge, final Integer maxAge) {
        Collection<Registration> allRegistrations = getAllRegistrations();

        final Integer numberOfStudentsByAge = Collections2.filter(allRegistrations, new Predicate<Registration>() {
            @Override
            public boolean apply(Registration registration) {
                final Student student = registration.getStudent();
                return false; // TODO legidio student.getPerson().getCurrentAge() != null && (minAge != null ? student.getPerson().getCurrentAge() >= minAge : true) && (maxAge != null ? student.getPerson().getCurrentAge() < maxAge : true);
            }
        }).size();

        if (allRegistrations.size() == 0) {
            return new BigDecimal(0);
        }

        return new BigDecimal(numberOfStudentsByAge).divide(new BigDecimal(allRegistrations.size()), 2, RoundingMode.HALF_EVEN)
                .multiply(new BigDecimal(100));
    }

    private void fillStudentsByCurricularYear() {

        final Map<String, String> studentsByCurricularYears = new TreeMap<String, String>();
        for (final Map.Entry<Integer, Collection<Registration>> entry : groupStudentsByCurricularYear().entrySet()) {
            studentsByCurricularYears.put(entry.getKey().toString(), String.valueOf(entry.getValue().size()));
        }

        this.data.setStudentsByCurricularYear(studentsByCurricularYears);
    }

    private Map<Integer, Collection<Registration>> groupStudentsByCurricularYear() {
        final Multimap<Integer, Registration> registrationsByYear = ArrayListMultimap.create();

        for (final Registration registration : getAllRegistrations()) {
            final Integer year =
                    registration.getStudentCurricularPlan(this.year).getCurriculum(new DateTime(), this.year).getCurricularYear();
            registrationsByYear.put(year, registration);
        }

        return registrationsByYear.asMap();
    }

    private void fillDemandAmongStudents() {
        Map<ExecutionYear, Map<String, String>> demandAmongStudents = new HashMap<ExecutionYear, Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        demandAmongStudents.put(this.year, map);
        demandAmongStudents.put(this.year.getPreviousExecutionYear(), map);
        demandAmongStudents.put(this.year.getPreviousExecutionYear().getPreviousExecutionYear(), map);
        this.data.setDemandAmongStudents(demandAmongStudents);
    }

    private void fillStudentsExtraInformation() {
        this.data.setStudentsExtraInformation(createEmptyMLS());
    }

    private void fillStudentsSupport() {
        this.data.setStudentsSupport(createEmptyMLS());
    }

    private void fillStudentIntegrationMeasures() {
        this.data.setStudentIntegrationMeasures(createEmptyMLS());
    }

    private void fillFinanceAndEmploymentCounseling() {
        this.data.setFinanceAndEmploymentCounseling(createEmptyMLS());
    }

    private void fillStudentSurveysImpact() {
        this.data.setStudentSurveysImpact(createEmptyMLS());
    }

    private void fillMobilityStrategy() {
        this.data.setMobilityStrategy(createEmptyMLS());
    }

}
