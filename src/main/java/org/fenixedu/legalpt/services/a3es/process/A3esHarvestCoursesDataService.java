package org.fenixedu.legalpt.services.a3es.process;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.EN;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PT;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.SEPARATOR_2;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.SEPARATOR_3;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._100;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._1000;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._3000;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNLIMITED;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.createEmptyMLS;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.createMLS;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getTeachingHoursByShiftType;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.readCourseProfessorships;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.readCourses;

import java.util.ArrayList;
import java.util.List;
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
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.legalpt.dto.a3es.A3esCourseBean;
import org.fenixedu.legalpt.dto.a3es.A3esProcessBean;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

@SuppressWarnings("deprecation")
public class A3esHarvestCoursesDataService {

    private final ExecutionYear year;
    private final ExecutionSemester semester;
    private final DegreeCurricularPlan degreeCurricularPlan;

    public A3esHarvestCoursesDataService(final A3esProcessBean bean) {
        this.year = bean.getExecutionYear();
        this.semester = this.year.getFirstExecutionPeriod();
        this.degreeCurricularPlan = bean.getDegreeCurricularPlan();

        readCourses(this.degreeCurricularPlan, this.year).stream().map(competence -> {

            final A3esCourseBean data = new A3esCourseBean();

            fillBasics(data, competence);
            fillCourseName(data, competence);
            fillAllTeachersInfo(data, competence);
            fillLearningObjectives(data, competence);
            fillCourseProgram(data, competence);
            fillCourseProgramJustification(data, competence);
            fillTeachingMethodology(data, competence);
            fillTeachingMethodologyJustification(data, competence);
            fillBibliography(data, competence);

            return data;
        }).collect(Collectors.toCollection(() -> bean.getCoursesData()));
    }

    private void fillBasics(final A3esCourseBean data, final CompetenceCourse competence) {
        data.addField("currentInfo", "currentInfo", competence.findCompetenceCourseInformationForExecutionPeriod(this.semester)
                .getExecutionInterval().getQualifiedName(), _UNLIMITED);
        data.addField("code", "code", competence.getCode(), _UNLIMITED);
    }

    private void fillCourseName(final A3esCourseBean data, final CompetenceCourse competence) {
        data.addField("1", "curricularUnitName", competence.getNameI18N(this.semester), _100);
    }

    private void fillAllTeachersInfo(final A3esCourseBean data, final CompetenceCourse competence) {
        final Map<Person, Set<Professorship>> professorships =
                readCourseProfessorships(this.degreeCurricularPlan, this.year, competence);

        fillTeachersInfo(data, professorships);
        fillAssistantTeachersInfo(data, professorships);
    }

    static private void fillTeachersInfo(final A3esCourseBean data, final Map<Person, Set<Professorship>> professorships) {
        final String source = getTeachersAndTeachingHours(professorships, p -> p.isResponsibleFor());
        data.addField("2", "responsibleTeacherAndTeachingHours", source, _1000);
    }

    static private void fillAssistantTeachersInfo(final A3esCourseBean data,
            final Map<Person, Set<Professorship>> professorships) {
        final String source = getTeachersAndTeachingHours(professorships, p -> !p.isResponsibleFor());
        data.addField("3", "otherTeachersAndTeachingHours", source, _1000);
    }

    static private String getTeachersAndTeachingHours(final Map<Person, Set<Professorship>> input,
            final Predicate<Professorship> p) {

        return Joiner.on(SEPARATOR_3)
                .join(input.entrySet().stream()
                        .map(entry -> entry.getKey().getName() + " - "
                                + getTeachingHoursByShiftType(entry.getValue().stream().filter(p).collect(Collectors.toSet())))
                        .collect(Collectors.toSet()));
    }

    private void fillLearningObjectives(final A3esCourseBean data, final CompetenceCourse competence) {
        final MultiLanguageString source = competence.getObjectivesI18N(this.semester);
        data.addField("4", "learningOutcomes", PT, source, _1000);
        data.addField("4", "learningOutcomes", EN, source, _1000);
    }

    private void fillCourseProgram(final A3esCourseBean data, final CompetenceCourse competence) {
        final MultiLanguageString source = competence.getProgramI18N(this.semester);
        data.addField("5", "syllabus", PT, source, _1000);
        data.addField("5", "syllabus", EN, source, _1000);
    }

    private void fillCourseProgramJustification(final A3esCourseBean data, final CompetenceCourse competence) {
        // TODO legidio
        final MultiLanguageString source = createEmptyMLS(); // competence.getProgramI18N(this.semester);
        data.addField("6", "syllabusDemonstration", PT, source, _1000);
        data.addField("6", "syllabusDemonstration", EN, source, _1000);
    }

    private void fillTeachingMethodology(final A3esCourseBean data, final CompetenceCourse competence) {
        final MultiLanguageString source =
                createMLS(competence.getEvaluationMethod(this.semester), competence.getEvaluationMethodEn(this.semester));
        data.addField("7", "teachingMethodologies", PT, source, _1000);
        data.addField("7", "teachingMethodologies", EN, source, _1000);
    }

    private void fillTeachingMethodologyJustification(final A3esCourseBean data, final CompetenceCourse competence) {
        // TODO legidio
        final MultiLanguageString source = createEmptyMLS(); // competence.getProgramI18N(this.semester);
        data.addField("8", "teachingMethodologiesDemonstration", PT, source, _3000);
        data.addField("8", "teachingMethodologiesDemonstration", EN, source, _3000);
    }

    private void fillBibliography(final A3esCourseBean data, final CompetenceCourse competence) {
        data.addField("9", "bibliographicReferences", getBibliography(competence), _1000);
    }

    private String getBibliography(final CompetenceCourse competence) {
        final List<String> references = new ArrayList<String>();
        final BibliographicReferences data = competence.getBibliographicReferences(this.semester);
        if (data != null) {
            data.getMainBibliographicReferences().stream().forEach(r -> {
                final List<String> info = Lists.newArrayList(r.getTitle(), r.getAuthors(), r.getYear(), r.getReference());
                references.add(Joiner.on(SEPARATOR_3)
                        .join(info.stream().filter(i -> !StringUtils.isBlank(i)).collect(Collectors.toList())));
            });
        }

        return Joiner.on(SEPARATOR_2).join(references);
    }

}
