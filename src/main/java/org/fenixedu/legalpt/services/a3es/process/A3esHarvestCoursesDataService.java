package org.fenixedu.legalpt.services.a3es.process;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.EN;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PLUS;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PT;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.SEMICOLON;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._100;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._1000;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._3000;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNSUPPORTED;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.calculateTeachingHours;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.createMLS;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getApaFormat;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getShiftTypeAcronym;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.label;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.readCourseProfessorships;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.readCourses;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.degreeStructure.BibliographicReferences;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseInformation;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.dml.DynamicField;
import org.fenixedu.academic.util.CurricularPeriodLabelFormatter;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.domain.a3es.A3esInstance;
import org.fenixedu.legalpt.dto.a3es.A3esCourseBean;
import org.fenixedu.legalpt.dto.a3es.A3esProcessBean;

@SuppressWarnings("deprecation")
public class A3esHarvestCoursesDataService {

    private final ExecutionYear year;
    private final ExecutionInterval semester;
    private final DegreeCurricularPlan degreeCurricularPlan;

    public A3esHarvestCoursesDataService(final A3esProcessBean bean) {
        this.year = bean.getExecutionYear();
        this.semester = this.year.getFirstExecutionPeriod();
        this.degreeCurricularPlan = bean.getDegreeCurricularPlan();

        final Set<Person> coordinators =
                this.degreeCurricularPlan.getExecutionDegreesSet().stream().filter(ed -> ed.getExecutionYear() == this.year)
                        .flatMap(ed -> ed.getCoordinatorsListSet().stream()).map(c -> c.getPerson()).collect(Collectors.toSet());

        readCourses(this.degreeCurricularPlan, this.year).stream().map(course -> {
            final Map<Person, Set<Professorship>> courseProfessorships =
                    readCourseProfessorships(this.degreeCurricularPlan, this.year, course);

            final Teacher teacher = bean.getTeacher();
            if (teacher != null && !courseProfessorships.containsKey(teacher.getPerson())
                    && !coordinators.contains(teacher.getPerson())) {
                return null;
            }

            final A3esCourseBean data = new A3esCourseBean();

            final CompetenceCourseInformation info = course.findInformationMostRecentUntil(this.semester);
            fillBasics(data, info);
            fillAllTeachersInfo(data, courseProfessorships);
            fillLearningObjectives(data, course);
            fillCourseProgram(data, course);
            fillCourseProgramJustification(data, info);
            fillTeachingMethodology(data, course);
            fillTeachingMethodologyJustification(data, info);
            fillBibliography(data, course);

            return data;
        }).filter(i -> i != null).collect(Collectors.toCollection(() -> bean.getCoursesData()));
    }

    private void fillBasics(final A3esCourseBean data, final CompetenceCourseInformation info) {
        data.addField("currentInfo", "currentInfo", info.getExecutionInterval().getQualifiedName(), _UNSUPPORTED);

        final CompetenceCourse course = info.getCompetenceCourse();
        data.addField("code", "code", course.getCode(), _UNSUPPORTED);

        final LocalizedString curricularUnitName = course.getNameI18N(this.semester);
        data.addField("curricularUnitPresentationName", "curricularUnitPresentationName", getCourseName(course), _UNSUPPORTED);
        data.addField("1.1", "curricularUnitName", PT, curricularUnitName, _100);
        data.addField("1.1", "curricularUnitName", EN, curricularUnitName, _100);

        final String curricularPeriods = course.getAssociatedCurricularCoursesSet().stream()
                .filter(c -> c.getDegreeCurricularPlan() == degreeCurricularPlan)
                .flatMap(c -> c.getParentContextsByExecutionYear(year).stream())
                .map(ctx -> CurricularPeriodLabelFormatter.getFullLabel(ctx.getCurricularPeriod(), true)).distinct()
                .collect(Collectors.joining("; "));
        data.addField("curricularPeriod", "curricularPeriod", curricularPeriods, _UNSUPPORTED);

        final String source = course.getAssociatedCurricularCoursesSet().stream()
                .filter(c -> c.getDegreeCurricularPlan() == this.degreeCurricularPlan
                        && c.getAssociatedExecutionCoursesSet().stream().anyMatch(ec -> ec.getExecutionYear() == year))
                .flatMap(c -> {
                    final List<String> notes = new ArrayList<>();

                    if (c.isOptionalCurricularCourse()) {
                        notes.add(label("optional"));
                    }

                    collectFullPath(c.getParentContextsByExecutionYear(this.year)).forEach(cg -> {
                        if (cg.isBranchCourseGroup()) {
                            notes.add(cg.getName());
                        }

                        if (cg.isOptionalCourseGroup()) {
                            notes.add(label("optional"));
                        }
                    });

                    return notes.stream();
                }).sorted().distinct().collect(Collectors.joining(SEMICOLON));
        data.addField("notes", "notes", StringUtils.isBlank(source) ? "-" : source, _UNSUPPORTED);

    }

    private List<CourseGroup> collectFullPath(final List<Context> input) {
        final List<CourseGroup> result = new ArrayList<>();

        if (!input.isEmpty()) {
            final List<CourseGroup> parents = input.stream().map(c -> c.getParentCourseGroup()).collect(Collectors.toList());
            result.addAll(parents);
            result.addAll(collectFullPath(parents.stream().flatMap(c -> c.getParentContextsByExecutionYear(this.year).stream())
                    .collect(Collectors.toList())));
        }

        return result;
    }

    private String getCourseName(final CompetenceCourse course) {
        final LocalizedString i18n = course.getNameI18N(this.semester);

        String result = i18n.getContent(PT);
        if (i18n.getContent(EN) != null) {
            result += " | " + i18n.getContent(EN);
        }

        return result;
    }

    private void fillAllTeachersInfo(final A3esCourseBean data, final Map<Person, Set<Professorship>> courseProfessorships) {
        fillTeachersInfo(data, courseProfessorships);
        fillAssistantTeachersInfo(data, courseProfessorships);
    }

    static private void fillTeachersInfo(final A3esCourseBean data, final Map<Person, Set<Professorship>> courseProfessorships) {
        final String source = getTeachersAndTeachingHours(courseProfessorships, p -> p.isResponsibleFor());
        data.addField("2", "responsibleTeacherAndTeachingHours", source, _1000);
    }

    static private void fillAssistantTeachersInfo(final A3esCourseBean data,
            final Map<Person, Set<Professorship>> courseProfessorships) {

        final String source = getTeachersAndTeachingHours(courseProfessorships, p -> !p.isResponsibleFor());
        data.addField("3", "otherTeachersAndTeachingHours", source, _1000);
    }

    static private String getTeachersAndTeachingHours(final Map<Person, Set<Professorship>> courseProfessorships,
            final Predicate<Professorship> p) {

        return courseProfessorships.entrySet().stream().map(entry -> {

            final Person person = entry.getKey();
            final Set<Professorship> personProfessorships = entry.getValue();
            if (personProfessorships.stream().noneMatch(p)) {
                return "";
            }

            return person.getName() + " (" + getTeachingHours(personProfessorships, p) + ")";

        }).sorted().filter(i -> !StringUtils.isBlank(i)).collect(Collectors.joining(PLUS));
    }

    static private String getTeachingHours(final Set<Professorship> personProfessorships, final Predicate<Professorship> p) {
        final Stream<Professorship> filtered = personProfessorships.stream().filter(p);

        if (A3esInstance.getInstance().getGroupCourseProfessorshipByPerson()) {
            return getTeachingHoursByPerson(filtered);

        } else {
            return getTeachingHoursByShiftType(filtered);
        }
    }

    static private String getTeachingHoursByPerson(final Stream<Professorship> professorships) {
        return professorships.flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream()).map(sp -> calculateTeachingHours(sp))
                .reduce(BigDecimal.ZERO, BigDecimal::add).toPlainString() + "h";
    }

    static private String getTeachingHoursByShiftType(final Stream<Professorship> professorships) {
        return professorships.flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream())
                .map(sp -> getShiftTypeAcronym(sp.getShift().getTypes().iterator().next()) + " - "
                        + calculateTeachingHours(sp).toPlainString() + "h")
                .sorted().collect(Collectors.joining(SEMICOLON));
    }

    private void fillLearningObjectives(final A3esCourseBean data, final CompetenceCourse course) {
        final LocalizedString source = course.getObjectivesI18N(this.semester);
        data.addField("4", "learningOutcomes", PT, source, _1000);
        data.addField("4", "learningOutcomes", EN, source, _1000);
    }

    private void fillCourseProgram(final A3esCourseBean data, final CompetenceCourse course) {
        final LocalizedString source = course.getProgramI18N(this.semester);
        data.addField("5", "program", PT, source, _1000);
        data.addField("5", "program", EN, source, _1000);
    }

    private void fillCourseProgramJustification(final A3esCourseBean data, final CompetenceCourseInformation info) {
        final String code = "programDemonstration";
        final DynamicField field = DynamicField.findField(info, code);
        final LocalizedString source = field == null ? null : field.getValue(LocalizedString.class);
        data.addField("6", code, PT, source, _1000);
        data.addField("6", code, EN, source, _1000);
    }

    private void fillTeachingMethodology(final A3esCourseBean data, final CompetenceCourse course) {
        final LocalizedString source =
                createMLS(course.getEvaluationMethod(this.semester), course.getEvaluationMethodEn(this.semester));
        data.addField("7", "evaluationMethod", PT, source, _1000);
        data.addField("7", "evaluationMethod", EN, source, _1000);
    }

    private void fillTeachingMethodologyJustification(final A3esCourseBean data, final CompetenceCourseInformation info) {
        final String code = "evaluationMethodDemonstration";
        final DynamicField field = DynamicField.findField(info, code);
        final LocalizedString source = field == null ? null : field.getValue(LocalizedString.class);
        data.addField("8", code, PT, source, _3000);
        data.addField("8", code, EN, source, _3000);
    }

    private void fillBibliography(final A3esCourseBean data, final CompetenceCourse course) {
        data.addField("9", "bibliographicReferences", getBibliography(course), _1000);
    }

    private String getBibliography(final CompetenceCourse course) {
        final Set<String> result = new LinkedHashSet<String>();

        final BibliographicReferences data = course.getBibliographicReferences(this.semester);
        if (data != null) {
            data.getBibliographicReferencesSortedByOrder().stream().forEach(r -> {
                result.add(getApaFormat(removeNonPrintableChars(r.getAuthors()), removeNonPrintableChars(r.getYear()),
                        removeNonPrintableChars(r.getTitle()), removeNonPrintableChars(r.getReference())));
            });
        }

        return result.stream().collect(Collectors.joining(PLUS));
    }

    private String removeNonPrintableChars(String value) {
        return value /* == null ? null : value.replaceAll("\\P{Print}", "")*/ ;
    }

}
