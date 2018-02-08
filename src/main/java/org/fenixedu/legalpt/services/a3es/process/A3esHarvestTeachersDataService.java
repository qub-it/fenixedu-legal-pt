package org.fenixedu.legalpt.services.a3es.process;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PT;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.SEMICOLON;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._100;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._200;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._30;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._500;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNLIMITED;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNSUPPORTED;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getApaFormat;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getShiftTypeAcronym;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.getTeachingHours;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.label;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.readProfessorships;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Job;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.Qualification;
import org.fenixedu.academic.domain.ShiftType;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.TeacherAuthorization;
import org.fenixedu.academic.domain.TeacherCategory;
import org.fenixedu.academic.domain.academicStructure.AcademicArea;
import org.fenixedu.academic.domain.academicStructure.AcademicAreaType;
import org.fenixedu.academic.domain.organizationalStructure.Accountability;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UniversityUnit;
import org.fenixedu.academic.domain.person.JobType;
import org.fenixedu.academic.domain.researchPublication.ResearchPublication;
import org.fenixedu.academic.domain.researchPublication.ResearchPublicationType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.domain.a3es.A3esInstance;
import org.fenixedu.legalpt.domain.a3es.mapping.A3esMappingType;
import org.fenixedu.legalpt.dto.a3es.A3esProcessBean;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.AttainedDegree;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.TeacherActivity;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.TeachingService;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.joda.time.LocalDate;

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
            final Map<CompetenceCourse, Set<Professorship>> personProfessorships = entry.getValue();

            final A3esTeacherBean data = new A3esTeacherBean();

            fillBasics(data, person);
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

            fillTeachingService(data, personProfessorships);

            return data;
        }).collect(Collectors.toCollection(() -> bean.getTeachersData()));
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
            researchUnitsString.append(unit.getName()).append(SEMICOLON);
        }

        if (researchUnitsString.toString().endsWith(SEMICOLON)) {
            researchUnitsString.delete(researchUnitsString.length() - SEMICOLON.length(), researchUnitsString.length());
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
        data.addField("spec_area", "specialistArea", findSpecializationArea(person), _200);
    }

    static private void fillTimeAllocation(final A3esTeacherBean data, final Person person) {
        data.addField("time", "regime", (String) null, _UNSUPPORTED);
    }

    static private void fillAttainedDegree(final A3esTeacherBean data, final Person person) {
        final Qualification q = findMostRelevantQualification(person);

        final AttainedDegree attainedDegree = new AttainedDegree();
        data.setAttainedDegree(attainedDegree);

        attainedDegree.addField("deg", "degreeType",
                q == null ? null : q.getDegreeUnit() != null ? q.getDegreeUnit().getName() : q.getDegree(), _200);
        attainedDegree.addField("degarea", "degreeScientificArea", q == null ? null : q.getSpecializationArea(), _200);
        attainedDegree.addField("ano_grau", "degreeYear", q == null ? null : q.getYear(), _UNLIMITED);
        attainedDegree.addField("instituicao_conferente", "degreeInstitution",
                q == null ? null : q.getInstitutionUnit() != null ? q.getInstitutionUnit().getName() : q.getSchool(), _200);
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
            attainedDegree.addField("degree", "degreeTypeOrTitle",
                    q.getDegreeUnit() != null ? q.getDegreeUnit().getName() : q.getDegree(), _30);
            attainedDegree.addField("area", "area", q.getSpecializationArea(), _100);
            attainedDegree.addField("ies", "institution",
                    q.getInstitutionUnit() != null ? q.getInstitutionUnit().getName() : q.getSchool(), _100);
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

        for (final String source : findPublications(person, ResearchPublicationType.findByCode("AC"))) {
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

        for (final String source : findJobs(person, JobType.findByCode("ADNP"))) {
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

        for (final String source : findPublications(person, ResearchPublicationType.findByCode("PP"))) {
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

        for (final String source : findJobs(person, JobType.findByCode("EP"))) {
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

                    competenceProfessorships.stream().flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream())
                            .forEach(sp -> {

                                final List<ShiftType> types = sp.getShift().getTypes();
                                if (types.size() != 1) {
                                    return;
                                }
                                final ShiftType type = types.iterator().next();

                                final ExecutionCourse execution = sp.getProfessorship().getExecutionCourse();

                                final TeachingService service = new TeachingService();
                                service.addField("curricularUnit", "curricularUnit", competence.getName(), _100);
                                service.addField("studyCycle", "studyCycle",
                                        execution.getAssociatedCurricularCoursesSet().stream()
                                                .flatMap(c -> c.getDegree().getCycleTypes().stream())
                                                .map(ct -> ct.getDescriptionI18N().getContent()).distinct().sorted()
                                                .collect(Collectors.joining(SEMICOLON)),
                                        _200);
                                service.addField("type", "type", getShiftTypeAcronym(type), _30);
                                service.addField("hoursPerWeek", "totalContactHours", getTeachingHours(sp), _UNLIMITED);

                                teachingServices.add(service);

                                if (teachingServices.size() == _TEACHING_SERVICES) {
                                    return;
                                }
                            });
                });

        data.setTeachingServices(teachingServices);
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

    static private Set<String> findPublications(final Person person, final Optional<ResearchPublicationType> type) {
        final Set<String> result = new HashSet<String>();

        ResearchPublication.findPublicationsSortedByRelevance(person, type.orElse(null)).forEach(r -> {
            result.add(getApaFormat(r.getAuthors(), r.getYear() == null ? null : r.getYear().toString(),
                    r.getTitle() == null ? null : r.getTitle().toString(), r.getPublicationData()));
        });

        return result;
    }

    static private Set<String> findJobs(final Person person, final Optional<JobType> type) {
        final Set<String> result = new HashSet<String>();

        findJobsSortedByBeginDate(person, type.orElse(null)).forEach(r -> {
            result.add(getJobFormat(r));
        });

        return result;
    }

    static public SortedSet<Job> findJobsSortedByBeginDate(final Person person, final JobType type) {
        final SortedSet<Job> result = new TreeSet<>(Comparator.comparing(Job::getBeginDate).reversed());
        if (person != null && type != null) {
            result.addAll(person.getJobsSet().stream().filter(p -> type.equals(p.getType())).collect(Collectors.toSet()));
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

        return result;
    }

}
