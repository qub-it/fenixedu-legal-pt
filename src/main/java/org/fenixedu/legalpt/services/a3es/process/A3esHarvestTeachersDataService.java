package org.fenixedu.legalpt.services.a3es.process;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PT;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.SEPARATOR_1;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._100;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._200;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._30;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._500;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNLIMITED;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNSUPPORTED;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getShiftTypeAcronym;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getTeachingHours;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.readExecutionCourses;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.readProfessorships;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Job;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.Qualification;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.TeacherAuthorization;
import org.fenixedu.academic.domain.TeacherCategory;
import org.fenixedu.academic.domain.academicStructure.AcademicArea;
import org.fenixedu.academic.domain.academicStructure.AcademicAreaType;
import org.fenixedu.academic.domain.organizationalStructure.Accountability;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UniversityUnit;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.domain.a3es.A3esInstance;
import org.fenixedu.legalpt.domain.a3es.mapping.A3esMappingType;
import org.fenixedu.legalpt.dto.a3es.A3esProcessBean;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.AttainedDegree;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.TeacherActivity;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.TeachingService;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;

import com.google.common.collect.Sets;

public class A3esHarvestTeachersDataService {

    static private final int _QUALIFICATIONS = 3;
    static private final int _PUBLICATIONS = 5;
    static private final int _ACTIVITIES = 5;
    static private final int _TEACHING_SERVICES = 10;

    private final ExecutionYear year;
    private final DegreeCurricularPlan degreeCurricularPlan;

    public A3esHarvestTeachersDataService(final A3esProcessBean bean) {
        this.year = bean.getExecutionYear();
        this.degreeCurricularPlan = bean.getDegreeCurricularPlan();

        readProfessorships(this.degreeCurricularPlan, this.year).entrySet().stream().map(entry -> {

            final Person person = entry.getKey();

            final A3esTeacherBean data = new A3esTeacherBean();

            fillBasics(data, entry.getValue());
            fillName(data, person);
            fillInstitutionName(data);
            fillSchoolName(data);
            fillAssociatedResearchCentre(data, person);
            fillCategory(data, person.getTeacher());
            fillSpecialty(data, person);
            fillTimeAllocation(data, person);

            fillAttainedDegree(data, person);
            fillOtherAttainedDegrees(data, person);

            fillPrimePublishedWork(data, person);
            fillPrimeProfessionalActivities(data, person);
            fillOtherPublishedWork(data, person);
            fillOtherProfessionalActivities(data, person);

            fillTeachingService(data, entry.getValue());

            return data;
        }).collect(Collectors.toCollection(() -> bean.getTeachersData()));
    }

    private void fillBasics(final A3esTeacherBean data, final Map<CompetenceCourse, Set<Professorship>> map) {
        final ExecutionYear firstTeacherService = map.values().stream().flatMap(i -> i.stream())
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
        Collection<Unit> allResearchUnits = new HashSet<>(); // Bennu.getInstance().getInstitutionUnit().getAllSubUnits(PartyTypeEnum.RESEARCH_UNIT);

        Set<Unit> activeResearchUnits = Sets.newHashSet();

        for (Unit unit : allResearchUnits) {
            for (final Accountability accountability : unit.getChildsSet()) {
                if (!(accountability.getBeginDate().isAfter(this.year.getEndDateYearMonthDay())
                        && accountability.getEndDate().isBefore(this.year.getBeginDateYearMonthDay()))
                        && accountability.getChildParty().isPerson() && ((Person) accountability.getChildParty()) == person) {
                    activeResearchUnits.add(unit);
                }
            }
        }

        final StringBuilder researchUnitsString = new StringBuilder();
        for (final Unit unit : activeResearchUnits) {
            researchUnitsString.append(unit.getName()).append(SEPARATOR_1);
        }

        if (researchUnitsString.toString().endsWith(SEPARATOR_1)) {
            researchUnitsString.delete(researchUnitsString.length() - SEPARATOR_1.length(), researchUnitsString.length());
        }

        final String source = researchUnitsString.toString();
        data.addField("research_center", "researchUnitFiliation", source, _UNSUPPORTED /* _200 */);
    }

    private void fillCategory(final A3esTeacherBean data, final Teacher teacher) {
        final TeacherAuthorization auth = teacher == null ? null : teacher
                .getLatestTeacherAuthorizationInInterval(this.year.getAcademicInterval().toInterval()).orElse(null);
        final TeacherCategory category = auth == null ? null : auth.getTeacherCategory();
        final String source =
                LegalMapping.find(A3esInstance.getInstance(), A3esMappingType.CONTRACT_CATEGORY).translate(category);
        data.addField("cat", "category", source, _UNLIMITED);
    }

    static private void fillSpecialty(final A3esTeacherBean data, final Person person) {
        data.addField("spec", "specialist", (String) null, _UNSUPPORTED);
        data.addField("spec_area", "specialistArea", getSpecializationArea(person), _200);
    }

    static private void fillTimeAllocation(final A3esTeacherBean data, final Person person) {
        data.addField("time", "regime", (String) null, _UNSUPPORTED);
    }

    static private void fillAttainedDegree(final A3esTeacherBean data, final Person person) {
        final Qualification q = findMostRelevantQualification(person);

        final AttainedDegree attainedDegree = new AttainedDegree();
        data.setAttainedDegree(attainedDegree);

        attainedDegree.addField("deg", "degreeType", q == null ? null : q.getDegree(), _200);
        attainedDegree.addField("degarea", "degreeScientificArea", (String) null, _200);
        attainedDegree.addField("ano_grau", "degreeYear", q == null ? null : q.getYear(), _UNLIMITED);
        attainedDegree.addField("instituicao_conferente", "degreeInstitution", q == null ? null : q.getSchool(), _200);
    }

    static private void fillOtherAttainedDegrees(final A3esTeacherBean data, final Person person) {
        final Set<Qualification> otherQual = new HashSet<Qualification>();
        otherQual.addAll(person.getAssociatedQualificationsSet());
        final Qualification major = findMostRelevantQualification(person);
        if (major != null) {
            otherQual.remove(major);
        }

        final Set<AttainedDegree> otherAttainedDegrees = new HashSet<AttainedDegree>();

        for (final Qualification q : otherQual) {
            final AttainedDegree attainedDegree = new AttainedDegree();
            otherAttainedDegrees.add(attainedDegree);

            attainedDegree.addField("year", "year", q.getYear(), _UNLIMITED);
            attainedDegree.addField("degree", "degreeTypeOrTitle", q.getDegree(), _30);
            attainedDegree.addField("area", "area", (String) null, _100);
            attainedDegree.addField("ies", "institution", (String) null, _100);
            attainedDegree.addField("rank", "classification", q.getMark(), _30);

            if (otherAttainedDegrees.size() == _QUALIFICATIONS) {
                break;
            }
        }

        data.setOtherAttainedDegrees(otherAttainedDegrees);
    }

    static private void fillPrimePublishedWork(final A3esTeacherBean data, final Person person) {
        final TeacherActivity result = new TeacherActivity();
        final String id = "investigation";

        for (final String source : findMostRecentPublications(person)) {
            result.addField(id, id, source, _500);

            if (result.getField(id).size() == _PUBLICATIONS) {
                break;
            }
        }

        data.setPrimePublishedWork(result);
    }

    static private void fillPrimeProfessionalActivities(final A3esTeacherBean data, final Person person) {
        final TeacherActivity result = new TeacherActivity();
        String id = "highlevelactivities";

        // TODO legidio
        for (final String source : new HashSet<String>()) {
            result.addField(id, id, source, _200);

            if (result.getField(id).size() == _ACTIVITIES) {
                break;
            }
        }

        data.setPrimeProfessionalActivities(result);
    }

    static private void fillOtherPublishedWork(final A3esTeacherBean data, final Person person) {
        final TeacherActivity result = new TeacherActivity();
        final String id = "otherpublications";

        // TODO legidio
        for (final String source : new HashSet<String>()) {
            result.addField(id, id, source, _500);

            if (result.getField(id).size() == _PUBLICATIONS) {
                break;
            }
        }

        data.setOtherPublishedWork(result);
    }

    static private void fillOtherProfessionalActivities(final A3esTeacherBean data, final Person person) {
        final TeacherActivity result = new TeacherActivity();
        final String id = "profession";

        final Set<String> sources = new HashSet<String>();
        for (final Job job : person.getJobsSet()) {
            final String jobString = job.getEmployerName() + SEPARATOR_1 + job.getPosition() + SEPARATOR_1
                    + job.getBeginDate().toString("dd/MM/yyyy") + " - "
                    + (job.getEndDate() != null ? job.getEndDate().toString("dd/MM/yyyy") : null);

            sources.add(jobString);
        }

        for (final String source : sources) {
            result.addField(id, id, source, _200);

            if (result.getField(id).size() == _ACTIVITIES) {
                break;
            }
        }

        data.setOtherProfessionalActivities(result);
    }

    private void fillTeachingService(final A3esTeacherBean data,
            final Map<CompetenceCourse, Set<Professorship>> personProfessorships) {

        final Set<TeachingService> teachingServices = new HashSet<>();

        personProfessorships.entrySet().stream()
                .sorted((x, y) -> Collator.getInstance().compare(x.getKey().getName(), y.getKey().getName())).forEach(entry -> {

                    final CompetenceCourse competence = entry.getKey();
                    final Set<Professorship> competenceProfessorships = entry.getValue();

                    final TeachingService service = new TeachingService();
                    service.addField("curricularUnit", "curricularUnit", competence.getName(), _100);
                    service.addField("studyCycle", "studyCycle",
                            readExecutionCourses(this.degreeCurricularPlan, this.year, competence)
                                    .flatMap(ec -> ec.getAssociatedCurricularCoursesSet().stream()
                                            .flatMap(c -> c.getDegree().getCycleTypes().stream()))
                                    .map(ct -> ct.getDescriptionI18N().getContent()).distinct().sorted()
                                    .collect(Collectors.joining(SEPARATOR_1)),
                            _200);
                    service.addField("type", "type",
                            competenceProfessorships.stream().flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream())
                                    .map(sp -> sp.getShift()).flatMap(s -> s.getSortedTypes().stream())
                                    .map(t -> getShiftTypeAcronym(t)).filter(i -> i != null).distinct().sorted()
                                    .collect(Collectors.joining(" + ")),
                            _30);
                    service.addField("hoursPerWeek", "totalContactHours", getTeachingHours(competenceProfessorships), _UNLIMITED);

                    teachingServices.add(service);

                    if (teachingServices.size() == _TEACHING_SERVICES) {
                        return;
                    }
                });

        data.setTeachingServices(teachingServices);
    }

    static private String getSpecializationArea(final Person person) {
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
        // TODO legidio
        return person.getAssociatedQualificationsSet().stream().max(Qualification.COMPARATOR_BY_YEAR).orElse(null);
    }

    static private Set<String> findMostRecentPublications(final Person person) {
        // TODO legidio
        return new HashSet<>();
    }

}
