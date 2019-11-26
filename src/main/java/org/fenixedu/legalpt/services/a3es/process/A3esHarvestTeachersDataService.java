package org.fenixedu.legalpt.services.a3es.process;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.EN;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PT;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.SEMICOLON;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._100;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._200;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._30;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._500;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNLIMITED;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNSUPPORTED;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.calculateTeachingHours;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getApaFormat;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getShiftTypeAcronym;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.label;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.readPersonProfessorships;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Job;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.Qualification;
import org.fenixedu.academic.domain.ShiftProfessorship;
import org.fenixedu.academic.domain.ShiftType;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.TeacherAuthorization;
import org.fenixedu.academic.domain.TeacherCategory;
import org.fenixedu.academic.domain.academicStructure.AcademicArea;
import org.fenixedu.academic.domain.academicStructure.AcademicAreaType;
import org.fenixedu.academic.domain.dml.DynamicField;
import org.fenixedu.academic.domain.organizationalStructure.UniversityUnit;
import org.fenixedu.academic.domain.person.JobType;
import org.fenixedu.academic.domain.person.qualifications.QualificationLevel;
import org.fenixedu.academic.domain.researchPublication.ResearchPublication;
import org.fenixedu.academic.domain.researchPublication.ResearchPublicationType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.domain.a3es.A3esInstance;
import org.fenixedu.legalpt.domain.a3es.A3esProcessType;
import org.fenixedu.legalpt.domain.a3es.mapping.A3esMappingType;
import org.fenixedu.legalpt.domain.teacher.SpecialistTitle;
import org.fenixedu.legalpt.dto.a3es.A3esProcessBean;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.AttainedDegree;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.TeacherActivity;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.TeachingService;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.OtherTeachingService;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class A3esHarvestTeachersDataService {

    private static final int _PRIME_PUBLICATIONS_MIN_YEAR = 5;
    static private final int _QUALIFICATIONS = 3;
    static private final int _PUBLICATIONS = 5;
    static private final int _ACTIVITIES = 5;
    static private final int _TEACHING_SERVICES = 10;
    static private final int _OTHER_TEACHING_SERVICES = 10;

    private final ExecutionYear year;
    private final ExecutionSemester semester;
    private final DegreeCurricularPlan degreeCurricularPlan;
    private final String processStudyCycle;

    public A3esHarvestTeachersDataService(final A3esProcessBean bean) {
        this.year = bean.getExecutionYear();
        this.semester = this.year.getFirstExecutionPeriod();
        this.degreeCurricularPlan = bean.getDegreeCurricularPlan();

        this.processStudyCycle = degreeCurricularPlan.getDegree().getNameFor(this.year).getContent() + " ("
                + degreeCurricularPlan.getDegree().getDegreeType().getName().getContent().substring(0, 1) + ")" + SEMICOLON;

        final Set<Person> coordinators =
                this.degreeCurricularPlan.getExecutionDegreesSet().stream().filter(ed -> ed.getExecutionYear() == this.year)
                        .flatMap(ed -> ed.getCoordinatorsListSet().stream()).map(c -> c.getPerson()).collect(Collectors.toSet());

        readPersonProfessorships(this.degreeCurricularPlan, this.year).entrySet().stream().map(entry -> {
            final Map<CompetenceCourse, Set<Professorship>> personProfessorships = entry.getValue();
            final Person person = entry.getKey();

            final Teacher teacher = bean.getTeacher();
            if (teacher != null && person != teacher.getPerson() && !coordinators.contains(teacher.getPerson())) {
                return null;
            }

            final A3esTeacherBean data = new A3esTeacherBean();

            final TeacherAuthorization auth = getTeacherAuthorization(person);
//            fillBasics(data, person);
            fillName(data, person);
            fillInstitutionName(data);
            fillSchoolName(data);
            fillAssociatedResearchCentre(data, person);
            fillCategory(data, auth);
            fillSpecialty(data, person);
            fillTimeAllocation(data, auth);

            fillAttainedDegree(data, person);
            fillOtherAttainedDegrees(data, person);

            fillPrimePublishedWork(data, person);
            fillPrimeProfessionalActivities(data, person);
            fillOtherPublishedWork(data, person);
            fillOtherProfessionalActivities(data, person);

            if (bean.getProcess().getType().equals(A3esProcessType.EVALUATION_OF_NEW_PROGRAM)) {
                fillTeachingService(data, personProfessorships);
                fillOtherTeachingService(data, personProfessorships);
            } else {
                fillTeachingServiceOriginal(data, personProfessorships);
            }

            return data;
        }).filter(i -> i != null).collect(Collectors.toCollection(() -> bean.getTeachersData()));
    }

    private void fillBasics(final A3esTeacherBean data, final Person person) {
        final ExecutionYear firstTeacherService = person.getProfessorshipsSet().stream()
                .filter(p -> p.getExecutionCourse().getAssociatedCurricularCoursesSet().stream()
                        .anyMatch(cc -> cc.getDegreeCurricularPlan() == this.degreeCurricularPlan))
                .map(p -> p.getExecutionCourse().getExecutionYear()).distinct().min(Comparator.naturalOrder()).orElse(null);

        data.addField("firstTeacherService", "firstTeacherService", firstTeacherService.getQualifiedName(), _UNSUPPORTED);
    }

    static private void fillName(final A3esTeacherBean data, final Person person) {
        data.addField("name", "name", person.getName(), _200);
    }

    static private void fillInstitutionName(final A3esTeacherBean data) {
        data.addField("ies", "higherEducationInstitution", UniversityUnit.getInstitutionsUniversityUnit().getName(), _200);
    }

    static private void fillSchoolName(final A3esTeacherBean data) {
        data.addField("uo", "organicUnit", Bennu.getInstance().getInstitutionUnit().getName(), _200);
    }

    private void fillAssociatedResearchCentre(final A3esTeacherBean data, final Person person) {
        final String code = "teacherResearchCenterMembership";
        final DynamicField field = DynamicField.findField(person, code);
        data.addField("research_center", "researchUnitFiliation", field == null ? null : field.getValue(String.class), _200);
    }

    private void fillCategory(final A3esTeacherBean data, final TeacherAuthorization auth) {
        final TeacherCategory category = auth == null ? null : auth.getTeacherCategory();
        final String source =
                LegalMapping.find(A3esInstance.getInstance(), A3esMappingType.CONTRACT_CATEGORY).translate(category);
        data.addField("cat", "category", source, _UNLIMITED);
    }

    private TeacherAuthorization getTeacherAuthorization(final Person person) {
        final Teacher teacher = person == null ? null : person.getTeacher();
        return teacher == null ? null : teacher
                .getLatestTeacherAuthorizationInInterval(this.year.getAcademicInterval().toInterval()).orElse(null);
    }

    static private void fillSpecialty(final A3esTeacherBean data, final Person person) {
        final String code = "teacherSpecialistTitle";
        final DynamicField field = DynamicField.findField(person, code);
        data.addField("spec", "specialist",
                field == null || field.getValue(String.class) == null ? null : LegalMapping
                        .find(A3esInstance.getInstance(), A3esMappingType.SPECIALIST_TITLE)
                        .translate(SpecialistTitle.valueOf(field.getValue(String.class))),
                _100);

        final DynamicField specialistAreaField = DynamicField.findField(person, "teacherSpecialistArea");
        data.addField("spec_area", "specialistArea",
                specialistAreaField == null ? null : specialistAreaField.getValue(String.class), _200);

//        data.addField("spec_area", "specialistArea", findSpecializationArea(person), _200);
    }

    static private void fillTimeAllocation(final A3esTeacherBean data, final TeacherAuthorization auth) {
        final Double allocation = auth == null ? null : auth.getWorkPercentageInInstitution();
        final String source = allocation == null ? null : BigDecimal.valueOf(allocation).stripTrailingZeros().toPlainString();
        data.addField("time", "regime", source, _30);
    }

    static private void fillAttainedDegree(final A3esTeacherBean data, final Person person) {
        final Qualification q = findMostRelevantQualification(person);

        final AttainedDegree attainedDegree = new AttainedDegree();
        data.setAttainedDegree(attainedDegree);

        final String year = q == null ? null : q.getYear();
        final String level = getDegreeLevel(q);
        final String area = getDegreeArea(q);
        final String institution = getDegreeInstitution(q);

        attainedDegree.addField("deg", "degreeType", level, _200);
        attainedDegree.addField("degarea", "degreeScientificArea", area, _200);
        attainedDegree.addField("ano_grau", "degreeYear", year, _UNLIMITED);
        attainedDegree.addField("instituicao_conferente", "degreeInstitution", institution, _200);
    }

    static private String getDegreeLevel(final Qualification q) {
        final QualificationLevel level = q == null ? null : q.getLevel();
        return level == null ? null : LegalMapping.find(A3esInstance.getInstance(), A3esMappingType.SCHOOL_LEVEL)
                .translate(level);
    }

    static private String getDegreeArea(final Qualification q) {
        if (q != null) {

            if (!StringUtils.isBlank(q.getSpecializationArea())) {
                return q.getSpecializationArea().trim();
            }

            if (q.getDegreeUnit() != null) {
                return q.getDegreeUnit().getName();
            }

            if (!StringUtils.isBlank(q.getDegree())) {
                return q.getDegree().trim();
            }
        }

        return q == null ? null : q.getSpecializationArea();
    }

    private static String getDegreeInstitution(final Qualification q) {
        return q == null ? null : q.getInstitutionUnit() != null ? q.getInstitutionUnit().getName() : q.getSchool();
    }

    static private void fillOtherAttainedDegrees(final A3esTeacherBean data, final Person person) {
        final Set<AttainedDegree> otherAttainedDegrees = new LinkedHashSet<AttainedDegree>();

        final Qualification main = findMostRelevantQualification(person);
        person.getAssociatedQualificationsSet().stream().filter(q -> q != main)
                .sorted(Comparator.comparing(Qualification::getYear).reversed()).forEach(q -> {
                    if (otherAttainedDegrees.size() == _QUALIFICATIONS) {
                        return;
                    }

                    final AttainedDegree attainedDegree = new AttainedDegree();
                    otherAttainedDegrees.add(attainedDegree);

                    final String year = q == null ? null : q.getYear();
                    final String level = StringUtils.isNotBlank(q.getOtherLevel()) ? q.getOtherLevel() : getDegreeLevel(q);
                    final String area = getDegreeArea(q);
                    final String institution = getDegreeInstitution(q);
                    final String classification = q.getMark();

                    attainedDegree.addField("year", "year", year, _UNLIMITED);
                    attainedDegree.addField("degree", "degreeTypeOrTitle", level, _30);
                    attainedDegree.addField("area", "area", area, _100);
                    attainedDegree.addField("ies", "institution", institution, _100);
                    attainedDegree.addField("rank", "classification", classification, _30);
                });

        data.setOtherAttainedDegrees(otherAttainedDegrees);
    }

    static private void fillPrimePublishedWork(final A3esTeacherBean data, final Person person) {
        final TeacherActivity result = new TeacherActivity();
        final String id = "investigation";

        for (final String source : findPublications(person, ResearchPublicationType.findByCode("AC"),
                new DateTime().getYear() - _PRIME_PUBLICATIONS_MIN_YEAR)) {
            if (result.getField(id).size() == _PUBLICATIONS) {
                break;
            }

            result.addField(id, id, source, _500);
        }

        data.setPrimePublishedWork(result);
    }

    static private void fillPrimeProfessionalActivities(final A3esTeacherBean data, final Person person) {
        final TeacherActivity result = new TeacherActivity();
        String id = "highlevelactivities";

        for (final String source : findJobs(person, JobType.findByCode("ADNP"))) {
            if (result.getField(id).size() == _ACTIVITIES) {
                break;
            }

            result.addField(id, id, source, _200);
        }

        data.setPrimeProfessionalActivities(result);
    }

    static private void fillOtherPublishedWork(final A3esTeacherBean data, final Person person) {
        final TeacherActivity result = new TeacherActivity();
        final String id = "otherpublications";

        for (final String source : findPublications(person, ResearchPublicationType.findByCode("PP"), null)) {
            if (result.getField(id).size() == _PUBLICATIONS) {
                break;
            }

            result.addField(id, id, source, _500);
        }

        data.setOtherPublishedWork(result);
    }

    static private void fillOtherProfessionalActivities(final A3esTeacherBean data, final Person person) {
        final TeacherActivity result = new TeacherActivity();
        final String id = "profession";

        for (final String source : findJobs(person, JobType.findByCode("EP"))) {
            if (result.getField(id).size() == _ACTIVITIES) {
                break;
            }

            result.addField(id, id, source, _200);
        }

        data.setOtherProfessionalActivities(result);
    }

    private void fillTeachingServiceOriginal(final A3esTeacherBean data,
            final Map<CompetenceCourse, Set<Professorship>> personProfessorships) {

        final Set<TeachingService> teachingServices = new LinkedHashSet<>();

        final Stream<Entry<CompetenceCourse, Set<Professorship>>> sorted = personProfessorships.entrySet().stream()
                .sorted((x, y) -> Collator.getInstance().compare(x.getKey().getName(), y.getKey().getName()));

        if (A3esInstance.getInstance().getGroupPersonProfessorshipByCourse()) {
            fillTeachingServiceByCourseOriginal(teachingServices, sorted);

        } else {
            fillTeachingServiceByShiftTypeOriginal(teachingServices, sorted);
        }

        data.setTeachingServices(teachingServices);
    }

    private void fillTeachingServiceByShiftTypeOriginal(final Set<TeachingService> teachingServices,
            final Stream<Entry<CompetenceCourse, Set<Professorship>>> personProfessorships) {

        personProfessorships.forEach(entry -> {

            final CompetenceCourse competence = entry.getKey();
            final Set<Professorship> competenceProfessorships = entry.getValue();

            competenceProfessorships.stream().flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream()).forEach(sp -> {

                if (teachingServices.size() == _TEACHING_SERVICES) {
                    return;
                }

                final ShiftType type = getShiftType(sp);
                if (type == null) {
                    return;
                }

                final TeachingService service = new TeachingService();
                teachingServices.add(service);
                service.addField("curricularUnit", "curricularUnit", getCourseName(competence), _100);

                final Stream<CurricularCourse> courses =
                        sp.getProfessorship().getExecutionCourse().getAssociatedCurricularCoursesSet().stream();
                final String studyCycle = getStudyCycles(courses);
                service.addField("studyCycle", "studyCycle", studyCycle, _200);

                final String shiftType = getShiftTypeAcronym(type);
                service.addField("type", "type", shiftType, _30);

                final BigDecimal hours = calculateTeachingHours(sp);
                service.addField("hoursPerWeek", "totalContactHours", hours.toPlainString(), _UNLIMITED);
            });

        });
    }

    private void fillTeachingServiceByCourseOriginal(final Set<TeachingService> teachingServices,
            final Stream<Entry<CompetenceCourse, Set<Professorship>>> personProfessorships) {

        personProfessorships.forEach(entry -> {

            final CompetenceCourse competence = entry.getKey();
            final Set<Professorship> competenceProfessorships = entry.getValue();

            if (teachingServices.size() == _TEACHING_SERVICES) {
                return;
            }

            final Set<ShiftProfessorship> shiftProfessorships =
                    competenceProfessorships.stream().flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream())
                            .filter(sp -> getShiftType(sp) != null).collect(Collectors.toSet());
            if (shiftProfessorships.isEmpty()) {
                return;
            }

            final TeachingService service = new TeachingService();
            teachingServices.add(service);
            service.addField("curricularUnit", "curricularUnit", getCourseName(competence), _100);

            final Stream<CurricularCourse> courses = shiftProfessorships.stream()
                    .flatMap(sp -> sp.getProfessorship().getExecutionCourse().getAssociatedCurricularCoursesSet().stream());
            final String studyCycle = getStudyCycles(courses);
            service.addField("studyCycle", "studyCycle", studyCycle, _200);

            final String shiftType = shiftProfessorships.stream().map(sp -> getShiftTypeAcronym(getShiftType(sp))).distinct()
                    .sorted().collect(Collectors.joining(","));
            service.addField("type", "type", shiftType, _30);

            final BigDecimal hours =
                    shiftProfessorships.stream().map(sp -> calculateTeachingHours(sp)).reduce(BigDecimal.ZERO, BigDecimal::add);
            service.addField("hoursPerWeek", "totalContactHours", hours.toPlainString(), _UNLIMITED);
        });
    }

    private void fillTeachingService(final A3esTeacherBean data,
            final Map<CompetenceCourse, Set<Professorship>> personProfessorships) {

        final Set<TeachingService> teachingServices = new LinkedHashSet<>();

        final Stream<Entry<CompetenceCourse, Set<Professorship>>> sorted = personProfessorships.entrySet().stream()
                .sorted((x, y) -> Collator.getInstance().compare(x.getKey().getName(), y.getKey().getName()));

        if (A3esInstance.getInstance().getGroupPersonProfessorshipByCourse()) {
            fillTeachingServiceByCourse(teachingServices, sorted);

        } else {
            fillTeachingServiceByShiftType(teachingServices, sorted);
        }

        data.setTeachingServices(teachingServices);
    }

    private void fillTeachingServiceByShiftType(final Set<TeachingService> teachingServices,
            final Stream<Entry<CompetenceCourse, Set<Professorship>>> personProfessorships) {

        personProfessorships.forEach(entry -> {

            final CompetenceCourse competence = entry.getKey();
            final Set<Professorship> competenceProfessorships = entry.getValue();

            competenceProfessorships.stream().flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream()).forEach(sp -> {

                if (teachingServices.size() == _TEACHING_SERVICES) {
                    return;
                }

                final ShiftType type = getShiftType(sp);
                if (type == null) {
                    return;
                }

                final Stream<CurricularCourse> courses =
                        sp.getProfessorship().getExecutionCourse().getAssociatedCurricularCoursesSet().stream();
                final String studyCycle = getStudyCycles(courses);

                if (studyCycle.contains(processStudyCycle)) {
                    final TeachingService service = new TeachingService();
                    teachingServices.add(service);
                    service.addField("curricularUnit", "curricularUnit", getCourseName(competence), _100);

                    final String shiftType = getShiftTypeAcronym(type);
                    service.addField("type", "type", shiftType, _30);

                    final BigDecimal hours = calculateTeachingHours(sp);
                    service.addField("hoursPerWeek", "totalContactHours", hours.toPlainString(), _UNLIMITED);
                }

            });

        });
    }

    private void fillTeachingServiceByCourse(final Set<TeachingService> teachingServices,
            final Stream<Entry<CompetenceCourse, Set<Professorship>>> personProfessorships) {

        personProfessorships.forEach(entry -> {

            final CompetenceCourse competence = entry.getKey();
            final Set<Professorship> competenceProfessorships = entry.getValue();

            if (teachingServices.size() == _TEACHING_SERVICES) {
                return;
            }

            final Set<ShiftProfessorship> shiftProfessorships =
                    competenceProfessorships.stream().flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream())
                            .filter(sp -> getShiftType(sp) != null).collect(Collectors.toSet());
            if (shiftProfessorships.isEmpty()) {
                return;
            }

            final Stream<CurricularCourse> courses = shiftProfessorships.stream()
                    .flatMap(sp -> sp.getProfessorship().getExecutionCourse().getAssociatedCurricularCoursesSet().stream());
            final String studyCycle = getStudyCycles(courses);

            if (studyCycle.contains(processStudyCycle)) {
                final TeachingService service = new TeachingService();
                teachingServices.add(service);
                service.addField("curricularUnit", "curricularUnit", getCourseName(competence), _100);

                final String shiftType = shiftProfessorships.stream().map(sp -> getShiftTypeAcronym(getShiftType(sp))).distinct()
                        .sorted().collect(Collectors.joining(","));
                service.addField("type", "type", shiftType, _30);

                final BigDecimal hours = shiftProfessorships.stream().map(sp -> calculateTeachingHours(sp))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                service.addField("hoursPerWeek", "totalContactHours", hours.toPlainString(), _UNLIMITED);
            }

        });
    }

    private void fillOtherTeachingService(final A3esTeacherBean data,
            final Map<CompetenceCourse, Set<Professorship>> personProfessorships) {

        final Set<OtherTeachingService> otherTeachingServices = new LinkedHashSet<>();

        final Stream<Entry<CompetenceCourse, Set<Professorship>>> sorted = personProfessorships.entrySet().stream()
                .sorted((x, y) -> Collator.getInstance().compare(x.getKey().getName(), y.getKey().getName()));

        if (A3esInstance.getInstance().getGroupPersonProfessorshipByCourse()) {
            fillOtherTeachingServiceByCourse(otherTeachingServices, sorted);

        } else {
            fillOtherTeachingServiceByShiftType(otherTeachingServices, sorted);
        }

        data.setOtherTeachingServices(otherTeachingServices);
    }

    private void fillOtherTeachingServiceByShiftType(final Set<OtherTeachingService> otherTeachingServices,
            final Stream<Entry<CompetenceCourse, Set<Professorship>>> personProfessorships) {

        personProfessorships.forEach(entry -> {

            final CompetenceCourse competence = entry.getKey();
            final Set<Professorship> competenceProfessorships = entry.getValue();

            competenceProfessorships.stream().flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream()).forEach(sp -> {

                if (otherTeachingServices.size() == _OTHER_TEACHING_SERVICES) {
                    return;
                }

                final ShiftType type = getShiftType(sp);
                if (type == null) {
                    return;
                }

                final Stream<CurricularCourse> courses =
                        sp.getProfessorship().getExecutionCourse().getAssociatedCurricularCoursesSet().stream();
                final String studyCycle = getStudyCycles(courses);

                if (!studyCycle.contains(processStudyCycle)) {
                    final OtherTeachingService service = new OtherTeachingService();
                    otherTeachingServices.add(service);
                    service.addField("otherCurricularUnit", "otherCurricularUnit", getCourseName(competence), _100);

                    service.addField("studyCycle", "studyCycle", studyCycle, _200);

                    final BigDecimal hours = calculateTeachingHours(sp);
                    service.addField("contactHours", "totalContactHours", hours.toPlainString(), _UNLIMITED);
                }

            });

        });
    }

    private void fillOtherTeachingServiceByCourse(final Set<OtherTeachingService> otherTeachingServices,
            final Stream<Entry<CompetenceCourse, Set<Professorship>>> personProfessorships) {

        personProfessorships.forEach(entry -> {

            final CompetenceCourse competence = entry.getKey();
            final Set<Professorship> competenceProfessorships = entry.getValue();

            if (otherTeachingServices.size() == _OTHER_TEACHING_SERVICES) {
                return;
            }

            final Set<ShiftProfessorship> shiftProfessorships =
                    competenceProfessorships.stream().flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream())
                            .filter(sp -> getShiftType(sp) != null).collect(Collectors.toSet());
            if (shiftProfessorships.isEmpty()) {
                return;
            }

            final Stream<CurricularCourse> courses = shiftProfessorships.stream()
                    .flatMap(sp -> sp.getProfessorship().getExecutionCourse().getAssociatedCurricularCoursesSet().stream());
            final String studyCycle = getStudyCycles(courses);

            if (!studyCycle.contains(processStudyCycle)) {
                final OtherTeachingService service = new OtherTeachingService();
                otherTeachingServices.add(service);
                service.addField("otherCurricularUnit", "otherCurricularUnit", getCourseName(competence), _100);

                service.addField("studyCycle", "studyCycle", studyCycle, _200);

                final BigDecimal hours = shiftProfessorships.stream().map(sp -> calculateTeachingHours(sp))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                service.addField("contactHours", "totalContactHours", hours.toPlainString(), _UNLIMITED);
            }

        });
    }

    private String getCourseName(final CompetenceCourse course) {
        final LocalizedString i18n = course.getNameI18N(this.semester);

        String result = i18n.getContent(PT);
        if (i18n.getContent(EN) != null) {
            result += " | " + i18n.getContent(EN);
        }

        return result;
    }

    static private ShiftType getShiftType(final ShiftProfessorship sp) {
        final List<ShiftType> types = sp.getShift().getTypes();
        if (types.size() != 1) {
            return null;
        }

        return types.iterator().next();
    }

    private String getStudyCycles(final Stream<CurricularCourse> courses) {
        if (A3esInstance.getInstance().getStudyCycleByDegree()) {
            return getStudyCycleByDegree(courses);

        } else {
            return getStudyCycleByName(courses);
        }
    }

    private String getStudyCycleByName(final Stream<CurricularCourse> courses) {
        final String studyCycle = courses.flatMap(c -> c.getDegree().getCycleTypes().stream())
                .map(ct -> ct.getDescriptionI18N().getContent()).distinct().sorted().collect(Collectors.joining(SEMICOLON));
        return studyCycle;
    }

    private String getStudyCycleByDegree(final Stream<CurricularCourse> coursesStream) {

        final Set<CurricularCourse> courses = coursesStream.collect(Collectors.toSet());

        boolean containsDcp = courses.stream().anyMatch(c -> c.getDegreeCurricularPlan() == degreeCurricularPlan);

        final Set<Degree> otherDegrees = courses.stream().map(c -> c.getDegree())
                .filter(d -> d != degreeCurricularPlan.getDegree()).collect(Collectors.toSet());
        final Function<Degree, String> degreeFormatter =
                d -> d.getNameFor(this.year).getContent() + " (" + d.getDegreeType().getName().getContent().substring(0, 1) + ")";

        return (containsDcp ? degreeFormatter.apply(degreeCurricularPlan.getDegree()) + SEMICOLON : "")
                + otherDegrees.stream().map(degreeFormatter).distinct().sorted().collect(Collectors.joining(SEMICOLON));
    }

    static private String findSpecializationArea(final Person person) {
        String result = null;

        final Qualification qualification = findMostRelevantQualification(person);
        if (qualification != null) {

            result = qualification.getSpecializationArea();

            if (StringUtils.isBlank(result)) {
                final AcademicArea area = qualification.getAcademicAreasSet().stream()
                        .filter(a -> a.getType() == AcademicAreaType.findByCode("DEGREE_SPECIALIZATION")).findFirst()
                        .orElse(null);
                result = area == null ? null : area.getName().getContent(PT);
            }
        }

        return result;
    }

    static private Qualification findMostRelevantQualification(final Person person) {
        return person.getAssociatedQualificationsSet().stream().filter(q -> q.getMainQualification()).findAny().orElse(null);
    }

    static private Set<String> findPublications(final Person person, final Optional<ResearchPublicationType> type,
            final Integer minYear) {
        final Set<String> result = new LinkedHashSet<String>();

        ResearchPublication.findPublicationsSortedByRelevance(person, type.orElse(null)).stream()
                .filter(r -> minYear == null || (r.getYear() != null && r.getYear().intValue() >= minYear.intValue()))
                .forEach(r -> {
                    result.add(getApaFormat(r.getAuthors(), r.getYear() == null ? null : r.getYear().toString(),
                            r.getTitle() == null ? null : r.getTitle().toString(), r.getPublicationData()));
                });

        return result;
    }

    static private Set<String> findJobs(final Person person, final Optional<JobType> type) {
        final Set<String> result = new LinkedHashSet<String>();

        findJobsSortedByBeginDate(person, type.orElse(null)).forEach(r -> {
            result.add(getJobFormat(r));
        });

        return result;
    }

    static public SortedSet<Job> findJobsSortedByBeginDate(final Person person, final JobType type) {
        final SortedSet<Job> result = new TreeSet<>(Comparator.comparing(Job::getBeginDate).reversed());
        if (person != null && type != null) {
            result.addAll(person.getJobsSet().stream().filter(j -> j.getBeginDate() != null && type.equals(j.getType()))
                    .collect(Collectors.toSet()));
        }
        return result;
    }

    static private String getJobFormat(final Job job) {
        String result = "";

        final LocalDate end = job.getEndDate();
        if (end == null) {
            result += label("since") + " ";
        }

        result += job.getBeginDate().toString("yyyy") + " ";

        if (end != null) {
            result += "- " + end.toString("yyyy") + " ";
        }

        if (!StringUtils.isBlank(job.getPosition())) {
            result += job.getPosition() + " ";
        }

        if (!StringUtils.isBlank(job.getEmployerName())) {
            result += "- " + job.getEmployerName() + " ";
        }

        return result.trim();
    }

}
