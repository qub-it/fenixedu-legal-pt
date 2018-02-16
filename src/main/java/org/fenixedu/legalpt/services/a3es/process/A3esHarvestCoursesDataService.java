package org.fenixedu.legalpt.services.a3es.process;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.EN;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PLUS;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PT;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._100;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._1000;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._3000;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNSUPPORTED;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.createMLS;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getApaFormat;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getTeachingHoursByShiftType;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.readCourseProfessorships;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.readCourses;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.degreeStructure.BibliographicReferences;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseInformation;
import org.fenixedu.academic.domain.dml.DynamicField;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.dto.a3es.A3esCourseBean;
import org.fenixedu.legalpt.dto.a3es.A3esProcessBean;

@SuppressWarnings("deprecation")
public class A3esHarvestCoursesDataService {

    private final ExecutionYear year;
    private final ExecutionSemester semester;
    private final DegreeCurricularPlan degreeCurricularPlan;

    public A3esHarvestCoursesDataService(final A3esProcessBean bean) {
        this.year = bean.getExecutionYear();
        this.semester = this.year.getFirstExecutionPeriod();
        this.degreeCurricularPlan = bean.getDegreeCurricularPlan();

        readCourses(this.degreeCurricularPlan, this.year).stream().map(course -> {

            final CompetenceCourseInformation info = course.findCompetenceCourseInformationForExecutionPeriod(this.semester);

            final A3esCourseBean data = new A3esCourseBean();

            fillBasics(data, info);
            fillCourseName(data, course);
            fillAllTeachersInfo(data, course);
            fillLearningObjectives(data, course);
            fillCourseProgram(data, course);
            fillCourseProgramJustification(data, info);
            fillTeachingMethodology(data, course);
            fillTeachingMethodologyJustification(data, info);
            fillBibliography(data, course);

            return data;
        }).collect(Collectors.toCollection(() -> bean.getCoursesData()));
    }

    private void fillBasics(final A3esCourseBean data, final CompetenceCourseInformation info) {
        data.addField("currentInfo", "currentInfo", info.getExecutionInterval().getQualifiedName(), _UNSUPPORTED);
        data.addField("code", "code", info.getCompetenceCourse().getCode(), _UNSUPPORTED);
    }

    private void fillCourseName(final A3esCourseBean data, final CompetenceCourse course) {
        data.addField("1", "curricularUnitName", course.getNameI18N(this.semester), _100);
    }

    private void fillAllTeachersInfo(final A3esCourseBean data, final CompetenceCourse course) {
        final Map<Person, Set<Professorship>> courseProfessorships =
                readCourseProfessorships(this.degreeCurricularPlan, this.year, course);

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

            return person.getName() + " ("
                    + getTeachingHoursByShiftType(personProfessorships.stream().filter(p).collect(Collectors.toSet())) + ")";

        }).sorted().filter(i -> !StringUtils.isBlank(i)).collect(Collectors.joining(PLUS));
    }

    private void fillLearningObjectives(final A3esCourseBean data, final CompetenceCourse course) {
        final MultiLanguageString source = course.getObjectivesI18N(this.semester);
        data.addField("4", "learningOutcomes", PT, source, _1000);
        data.addField("4", "learningOutcomes", EN, source, _1000);
    }

    private void fillCourseProgram(final A3esCourseBean data, final CompetenceCourse course) {
        final MultiLanguageString source = course.getProgramI18N(this.semester);
        data.addField("5", "program", PT, source, _1000);
        data.addField("5", "program", EN, source, _1000);
    }

    private void fillCourseProgramJustification(final A3esCourseBean data, final CompetenceCourseInformation info) {
        final String code = "programDemonstration";
        final LocalizedString source = DynamicField.findField(info, code).getValue(LocalizedString.class);
        data.addField("6", code, PT, source, _1000);
        data.addField("6", code, EN, source, _1000);
    }

    private void fillTeachingMethodology(final A3esCourseBean data, final CompetenceCourse course) {
        final MultiLanguageString source =
                createMLS(course.getEvaluationMethod(this.semester), course.getEvaluationMethodEn(this.semester));
        data.addField("7", "evaluationMethod", PT, source, _1000);
        data.addField("7", "evaluationMethod", EN, source, _1000);
    }

    private void fillTeachingMethodologyJustification(final A3esCourseBean data, final CompetenceCourseInformation info) {
        final String code = "evaluationMethodDemonstration";
        final LocalizedString source = DynamicField.findField(info, code).getValue(LocalizedString.class);
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
                result.add(getApaFormat(r.getAuthors(), r.getYear(), r.getTitle(), r.getReference()));
            });
        }

        return result.stream().collect(Collectors.joining(PLUS));
    }

}
