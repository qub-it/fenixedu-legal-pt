package org.fenixedu.legalpt.services.a3es.process;

import static org.fenixedu.legalpt.dto.a3es.A3esBeanField.CUT;
import static org.fenixedu.legalpt.dto.a3es.A3esBeanField.labelFieldCutInfo;
import static org.fenixedu.legalpt.dto.a3es.A3esBeanField.labelFieldMissing;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.ShiftProfessorship;
import org.fenixedu.academic.domain.ShiftType;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.legalpt.domain.a3es.A3esInstance;
import org.fenixedu.legalpt.domain.a3es.A3esPeriod;
import org.fenixedu.legalpt.domain.a3es.mapping.A3esMappingType;
import org.fenixedu.legalpt.domain.exceptions.LegalPTDomainException;
import org.fenixedu.legalpt.dto.a3es.A3esAbstractBean;
import org.fenixedu.legalpt.dto.a3es.A3esCourseBean;
import org.fenixedu.legalpt.dto.a3es.A3esProcessBean;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.AttainedDegree;
import org.fenixedu.legalpt.dto.a3es.A3esTeacherBean.TeacherActivity;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.settings.LegalSettings;
import org.fenixedu.ulisboa.specifications.domain.services.OccupationPeriodServices;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.collect.Lists;

@SuppressWarnings("deprecation")
abstract public class A3esExportService {

    static final public Locale PT = new Locale("pt");
    static final public Locale EN = Locale.UK;

    static public int _UNSUPPORTED = -2;
    static public int _UNLIMITED = -1;
    static protected int _0 = 0;
    static protected int _3 = 3;
    static protected int _20 = 20;
    static protected int _30 = 30;
    static protected int _100 = 100;
    static protected int _200 = 200;
    static protected int _360 = 360;
    static protected int _500 = 200;
    static protected int _1000 = 1000;
    static protected int _3000 = 3000;
    static protected String SEMICOLON = "; ";
    static protected String BREAKLINE = "\n";
    static protected String SLASH = " / ";
    static protected String PLUS = " + ";

    static final private String API_PROCESS = "api_process";
    static final private String API_ID = "id";
    static final private String API_NAME = "name";
    static final private String API_ATTACH_NAME_SEPARATOR = " - ";
    static final private String REQUIRED_STATE = " em preenchimento";
    static final private String REQUIRED_ACTION = "Submeter ";
    static final private String API_FORM = "api_form";
    static final private String FORM_ID = "formId";
    static final private String FOLDER_ID = "folderId";
    static final protected String API_FOLDER = "api_folder";
    static final protected String API_CVFOLDER = "api_cvfolder";
    static final private String API_ATTACH = "api_annex";

    private String base64Hash;
    private String formId;

    private void initialize(final A3esProcessBean bean) {
        initializeHash(bean);
        initializeFormId(bean);
    }

    private void initializeHash(final A3esProcessBean bean) {
        this.base64Hash = new String(Base64.getEncoder().encode((bean.getUser() + ":" + bean.getPassword()).getBytes()));
    }

    private void initializeFormId(final A3esProcessBean bean) {
        this.formId = bean.getFormId();

        if (StringUtils.isBlank(bean.getFormId())) {

            final A3esPeriod period = bean.getPeriod();
            if (!period.isInFillingPeriod()) {
                throw new LegalPTDomainException("error.A3es.outside.period",
                        OccupationPeriodServices.getIntervalsDescription(Lists.newArrayList(period.getFillInDateInterval())));
            }

            JSONArray processes = null;
            try {
                processes = invokeToArray(getClientTarget().path(API_PROCESS));
            } catch (final NotAuthorizedException e) {
                throw new LegalPTDomainException("error.A3es.wrong.credentials", bean.getUser());
            }

            if (processes.size() != 1) {
                throw new LegalPTDomainException("error.A3es.process.not.unique", String.valueOf(processes.size()));
            }

            final JSONObject process = (JSONObject) processes.iterator().next();

            final String processName = (String) process.get(API_NAME);
            if (!bean.getName().equals(processName)) {
                throw new LegalPTDomainException("error.A3es.process.unauthorized", processName);
            }

            final String processPlan = (String) process.get("study_cycle");
            if (!bean.getDegreeCurricularPlan().getPresentationName().contains(processPlan)) {
                // TODO legidio remove comment
                // throw new LegalPTDomainException("error.A3es.process.different.plan", processPlan);
            }

            final String processState = (String) process.get("state");
            final String processActions = (String) process.get("actions");
            if (!processState.contains(REQUIRED_STATE) || !processActions.contains(REQUIRED_ACTION)) {
                throw new LegalPTDomainException("error.A3es.process.unavailable", processState, processActions);
            }

            final String processId = (String) process.get(API_ID);
            for (final Object formObj : invokeToArray(getClientTarget().path(API_FORM).queryParam("processId", processId))) {
                final JSONObject form = (JSONObject) formObj;

                if (getFormName().equals(form.get(API_NAME))) {
                    this.formId = (String) form.get(API_ID);
                    bean.setFormId(this.formId);
                    return;
                }
            }

            throw new LegalPTDomainException("error.A3es.process.without.form", getFormName());
        }
    }

    protected WebTarget getClientTarget() {
        final Client client = ClientBuilder.newClient();
        return client.target(LegalSettings.getInstance().getA3esURL());
    }

    private Builder getClientBuilder(final WebTarget target) {
        return target.request(MediaType.APPLICATION_JSON).header("Authorization", "Basic " + this.base64Hash);
    }

    protected JSONObject invoke(final WebTarget target) {
        return (JSONObject) JSONValue.parse(getClientBuilder(target).get(String.class));
    }

    protected JSONArray invokeToArray(final WebTarget target) {
        return (JSONArray) ((JSONObject) JSONValue.parse(getClientBuilder(target).get(String.class))).get("list");
    }

    private String create(final WebTarget target, final JSONObject json, final String name) {
        final Response response = getClientBuilder(target).buildPost(Entity.text(json.toJSONString())).invoke();
        return buildResponseText("create", response, name);
    }

    private String delete(final WebTarget target, final String name) {
        final Response response = getClientBuilder(target).buildDelete().invoke();
        return buildResponseText("delete", response, name);
    }

    private String buildResponseText(final String action, final Response response, final String name) {
        return "[" + new DateTime().toString("yyyy-dd-MM HH:mm:ss") + SLASH + label(action) + SLASH + label("response") + ": "
                + parseResponse(response) + "] " + name;
    }

    private String parseResponse(final Response response) {
        if (response == null) {
            return "-";
        }

        final int status = response.getStatus();
        if (status == HttpStatus.SC_NO_CONTENT) {
            return "-";
        }

        final String result = label(String.valueOf(status));
        return result.contains("!") ? HttpStatus.getStatusText(status) : result;
    }

    abstract protected String getFormName();

    abstract protected String getCoursesFolderIndex();

    abstract protected String getCoursesFolderName();

    private String getCoursesFieldKey(final String keyIndex) {
        return "q-II." + getCoursesFolderIndex() + keyIndex;
    }

    private String getCourseId() {
        return API_ID;
    }

    private boolean isCourseFolder(final JSONObject folder) {
        return getCoursesFolderName().equals(folder.get(API_NAME));
    }

    private JSONArray getCourseFolders(final WebTarget target) {
        return (JSONArray) invokeToArray(target.path(API_FOLDER).queryParam(FORM_ID, this.formId));
    }

    abstract protected String getTeachersFolderSectionName();

    abstract protected String getTeachersFolderIndex();

    abstract protected String getTeachersFolderName();

    abstract protected String getTeacherId();

    private boolean isTeacherFolder(final JSONObject input) {
        return getTeachersFolderName().equals(input.get("name")) && getTeachersFolderSectionName().equals(input.get("uo"));
    }

    private JSONArray getTeacherFolders(final WebTarget target) {
        return (JSONArray) invoke(target.path(API_CVFOLDER)).get("folders");
    }

    static private String reportLabel(final String label) {
        return label("note") + ": " + label;
    }

    public List<String> coursesUpload(final A3esProcessBean bean) {
        final List<String> result = new ArrayList<String>();

        initialize(bean);
        final WebTarget clientTarget = getClientTarget();

        for (final Object folderObj : getCourseFolders(clientTarget)) {
            final JSONObject folder = (JSONObject) folderObj;

            if (isCourseFolder(folder)) {
                final String folderId = (String) folder.get(getCourseId());
                final WebTarget attachPath = clientTarget.path(API_ATTACH);
                final WebTarget attachTarget = attachPath.queryParam(FORM_ID, this.formId).queryParam(FOLDER_ID, folderId);

                final Map<String, String> attachExisting = existingAttachments(attachTarget);

                for (final JSONObject json : buildCoursesJson(bean)) {
                    final String name = (String) json.get(getCoursesFieldKey("1"));

                    result.addAll(deleteAttachmentsWithSameName(attachPath, folderId, attachExisting, name));

                    // try to create
                    result.add(create(attachTarget, json, name));
                }

                result.add(deleteAttachmentsWithInvalidName(attachTarget, attachPath, folderId));

                // should be only one relevant folder
                break;
            }
        }

        return result.stream().filter(i -> !StringUtils.isBlank(i)).sorted(uploadResultsComparator())
                .collect(Collectors.toList());
    }

    public List<String> teachersUpload(final A3esProcessBean bean) {
        final List<String> result = new ArrayList<String>();

        initialize(bean);
        final WebTarget clientTarget = getClientTarget();

        for (final Object folderObj : getTeacherFolders(clientTarget)) {
            final JSONObject folder = (JSONObject) folderObj;

            if (isTeacherFolder(folder)) {
                final String folderId = (String) folder.get(getTeacherId());
                final WebTarget attachPath = clientTarget.path(API_ATTACH);
                final WebTarget attachTarget = attachPath.queryParam(FORM_ID, this.formId).queryParam(FOLDER_ID, folderId);

                final Map<String, String> attachExisting = existingAttachments(attachTarget);

                for (final JSONObject json : buildTeachersJson(bean)) {
                    final String name = (String) json.get("q-cf-name");

                    result.addAll(deleteAttachmentsWithSameName(attachPath, folderId, attachExisting, name));

                    // try to create
                    result.add(create(attachTarget, json, name));
                }

                result.add(deleteAttachmentsWithInvalidName(attachTarget, attachPath, folderId));

                // should be only one relevant folder
                break;
            }
        }

        return result.stream().filter(i -> !StringUtils.isBlank(i)).sorted(uploadResultsComparator())
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> existingAttachments(final WebTarget attachTarget) {
        final Map<String, String> result = new HashMap<>();

        invokeToArray(attachTarget).stream().forEach(i -> {
            final JSONObject attach = (JSONObject) i;
            result.put((String) attach.get(API_ID), (String) attach.get(API_NAME));
        });

        return result;
    }

    private List<String> deleteAttachmentsWithSameName(final WebTarget attachPath, final String folderId,
            final Map<String, String> attachExisting, final String attachName) {

        final List<String> result = new LinkedList<>();

        attachExisting.entrySet().stream().filter(i -> i.getValue().contains(attachName)).forEach(i -> {
            final String attachId = i.getKey();
            result.add(delete(attachPath.path(attachId).queryParam(FORM_ID, this.formId).queryParam(FOLDER_ID, folderId),
                    attachName));
        });

        return result;
    }

    private String deleteAttachmentsWithInvalidName(final WebTarget attachTarget, final WebTarget attachPath,
            final String folderId) {

        int deleted = 0;

        for (final Object i : invokeToArray(attachTarget)) {
            final JSONObject attach = (JSONObject) i;
            final String attachId = (String) attach.get(API_ID);
            final String attachName = (String) attach.get(API_NAME);

            final boolean attachNameInvalid =
                    !attachName.contains(API_ATTACH_NAME_SEPARATOR) || attachName.endsWith(API_ATTACH_NAME_SEPARATOR);
            if (attachNameInvalid) {
                delete(attachPath.path(attachId).queryParam(FORM_ID, this.formId).queryParam(FOLDER_ID, folderId), attachName);
                deleted++;
            }
        }

        return deleted == 0 ? "" : i18n("info.A3es.process.deleted.invalid.attachment", String.valueOf(deleted));
    }

    static private Comparator<String> uploadResultsComparator() {
        return (x, y) -> {

            final String marker = "] ";
            final int xMark = x.indexOf(marker);
            final int yMark = y.indexOf(marker);

            if (xMark > 0 && yMark > 0) {
                return x.substring(xMark).compareTo(y.substring(yMark));
            }

            if (xMark < 0) {
                return -1;
            }

            if (yMark < 0) {
                return 1;
            }

            return x.compareTo(y);
        };
    }

    @SuppressWarnings("unchecked")
    private Set<JSONObject> buildCoursesJson(final A3esProcessBean bean) {
        final Set<JSONObject> result = new LinkedHashSet<>();

        if (bean.getCoursesData().isEmpty()) {
            bean.updateCoursesData();
        }

        bean.getCoursesData().forEach(data -> {

            if (!bean.getSelectedIds().isEmpty() && !bean.getSelectedIds().contains(data.getFormattedId())) {
                return;
            }

            final JSONObject root = new JSONObject();

            root.put(getCoursesFieldKey("1"), data.getFieldUnique("1").getValue());
            root.put(getCoursesFieldKey("2"), data.getFieldUnique("2").getValue());
            root.put(getCoursesFieldKey("3"), data.getFieldUnique("3").getValue());

            final JSONObject q4 = new JSONObject();
            data.getField("4").stream().forEach(i -> q4.put(i.getLanguage(), i.getValue()));
            root.put(getCoursesFieldKey("4"), q4);

            final JSONObject q5 = new JSONObject();
            data.getField("5").stream().forEach(i -> q5.put(i.getLanguage(), i.getValue()));
            root.put(getCoursesFieldKey("5"), q5);

            final JSONObject q6 = new JSONObject();
            data.getField("6").stream().forEach(i -> q6.put(i.getLanguage(), i.getValue()));
            root.put(getCoursesFieldKey("6"), q6);

            final JSONObject q7 = new JSONObject();
            data.getField("7").stream().forEach(i -> q7.put(i.getLanguage(), i.getValue()));
            root.put(getCoursesFieldKey("7"), q7);

            final JSONObject q8 = new JSONObject();
            data.getField("8").stream().forEach(i -> q8.put(i.getLanguage(), i.getValue()));
            root.put(getCoursesFieldKey("8"), q8);

            root.put(getCoursesFieldKey("9"), data.getFieldUnique("9").getValue());
            result.add(root);
        });

        return result;
    }

    @SuppressWarnings("unchecked")
    private Set<JSONObject> buildTeachersJson(final A3esProcessBean bean) {
        final Set<JSONObject> result = new LinkedHashSet<>();

        if (bean.getTeachersData().isEmpty()) {
            bean.updateTeachersData();
        }

        bean.getTeachersData().forEach(data -> {

            if (!bean.getSelectedIds().isEmpty() && !bean.getSelectedIds().contains(data.getFormattedId())) {
                return;
            }

            final JSONObject root = new JSONObject();

            root.put("q-cf-name", data.getFieldUnique(API_NAME).getValue());
            root.put("q-cf-ies", data.getFieldUnique("ies").getValue());
            root.put("q-cf-uo", data.getFieldUnique("uo").getValue());
            root.put("q-cf-cat", data.getFieldUnique("cat").getValue());
            root.put("q-cf-time", data.getFieldUnique("time").getValue());

            final JSONObject file = new JSONObject();
            file.put(API_NAME, data.getFieldUnique(API_NAME).getValue());
            file.put("ies", data.getFieldUnique("ies").getValue());
            file.put("uo", data.getFieldUnique("uo").getValue());
            file.put("research_center", data.getFieldUnique("research_center").getValue());
            file.put("cat", data.getFieldUnique("cat").getValue());
            file.put("spec", data.getFieldUnique("spec").getValue());
            file.put("spec_area", data.getFieldUnique("spec_area").getValue());

            final AttainedDegree attainedDegree = data.getAttainedDegree();
            file.put("deg", attainedDegree.getFieldUnique("deg").getValue());
            file.put("degarea", attainedDegree.getFieldUnique("degarea").getValue());
            file.put("ano_grau", attainedDegree.getFieldUnique("ano_grau").getValue());
            file.put("instituicao_conferente", attainedDegree.getFieldUnique("instituicao_conferente").getValue());

            file.put("regime", data.getFieldUnique("time").getValue());

            final JSONArray academicArray = new JSONArray();
            data.getOtherAttainedDegrees().forEach(x -> {

                final JSONObject academic = new JSONObject();

                academic.put("year", x.getFieldUnique("year").getValue());
                academic.put("degree", x.getFieldUnique("degree").getValue());
                academic.put("area", x.getFieldUnique("area").getValue());
                academic.put("ies", x.getFieldUnique("ies").getValue());
                academic.put("rank", x.getFieldUnique("rank").getValue());

                academicArray.add(academic);
            });
            file.put("form-academic", academicArray);

            file.put("form-investigation", getJsonActivitiesArray(data.getPrimePublishedWork(), "investigation"));
            file.put("form-highlevelactivities",
                    getJsonActivitiesArray(data.getPrimeProfessionalActivities(), "highlevelactivities"));
            file.put("form-otherpublications", getJsonActivitiesArray(data.getOtherPublishedWork(), "otherpublications"));
            file.put("form-professional", getJsonActivitiesArray(data.getOtherProfessionalActivities(), "profession"));

            final JSONArray insideLectures = new JSONArray();
            data.getTeachingServices().forEach(x -> {

                final JSONObject lecture = new JSONObject();

                lecture.put("curricularUnit", x.getFieldUnique("curricularUnit").getValue());
                lecture.put("studyCycle", x.getFieldUnique("studyCycle").getValue());
                lecture.put("type", x.getFieldUnique("type").getValue());
                lecture.put("hoursPerWeek", x.getFieldUnique("hoursPerWeek").getValue());

                insideLectures.add(lecture);
            });
            file.put("form-unit", insideLectures);
            root.put("q-cf-cfile", file);

            result.add(root);
        });

        return result;
    }

    @SuppressWarnings("unchecked")
    static private JSONArray getJsonActivitiesArray(final TeacherActivity teacherActivity, final String jsonObject) {
        final JSONArray result = new JSONArray();

        teacherActivity.getField(jsonObject).forEach(activity -> {
            final JSONObject current = new JSONObject();
            current.put(jsonObject, activity.getValue());
            result.add(current);
        });

        return result;
    }

    static public void coursesDownload(final SpreadsheetBuilder builder, final A3esProcessBean bean) throws IOException {
        final Set<A3esCourseBean> datas = bean.getCoursesData();

        builder.addSheet(label("courseFiles").replaceAll(" ", "_"), new SheetData<A3esCourseBean>(datas) {
            @Override
            protected void makeLine(final A3esCourseBean data) {

                addCell(label("degreeCode"), bean.getDegreeCode());
                addCell(label("degree"), bean.getDegreeCurricularPlan().getPresentationName());

                data.getFields().forEach(i -> {
                    addCell(i.getLabel(), i.getValue());

                    // avoid report on basic fields
                    final String report = i.getReport();
                    if (!StringUtils.isBlank(report)) {
                        addCell(reportLabel(i.getLabel()), report);
                    }
                });
            }
        });
    }

    static public void teachersDownload(final SpreadsheetBuilder builder, final A3esProcessBean bean) throws IOException {
        final Set<A3esTeacherBean> datas = bean.getTeachersData();

        builder.addSheet(label("teacherFiles").replaceAll(" ", "_"), new SheetData<A3esTeacherBean>(datas) {
            @Override
            protected void makeLine(final A3esTeacherBean data) {

                data.getFields().forEach(i -> {
                    addCell(i.getLabel(), i.getValue());

                    // avoid report on basic fields
                    final String report = i.getReport();
                    if (!StringUtils.isBlank(report)) {
                        addCell(reportLabel(i.getLabel()), report);
                    }
                });

                data.getAttainedDegree().getFields().forEach(i -> {
                    addCell(i.getLabel(), i.getValue());
                    addCell(reportLabel(i.getLabel()), i.getReport());
                });

                String label = label("otherAcademicDegreesOrTitle");
                String value =
                        data.getOtherAttainedDegrees().stream().map(i -> concat(i, SLASH)).collect(Collectors.joining(BREAKLINE));
                addCell(label, value);
                addCell(reportLabel(label), concatReport(value));

                label = label("scientificActivity");
                value = concat(data.getPrimePublishedWork());
                addCell(label, value);
                addCell(reportLabel(label), concatReport(value));

                label = label("developmentActivity");
                value = concat(data.getPrimeProfessionalActivities());
                addCell(label, value);
                addCell(reportLabel(label), concatReport(value));

                label = label("otherPublicationActivity");
                value = concat(data.getOtherPublishedWork());
                addCell(label, value);
                addCell(reportLabel(label), concatReport(value));

                label = label("otherProfessionalActivity");
                value = concat(data.getOtherProfessionalActivities());
                addCell(label, value);
                addCell(reportLabel(label), concatReport(value));

                label = label("teachingServiceAllocation");
                value = data.getTeachingServices().stream().map(i -> concat(i, SLASH)).collect(Collectors.joining(BREAKLINE));
                addCell(label, value);
                addCell(reportLabel(label), concatReport(value));
            }

            private String concat(final A3esAbstractBean input) {
                return concat(input, BREAKLINE);
            }

            private String concat(final A3esAbstractBean input, final String separator) {
                return input.getFields().stream().map(i -> i.getValue()).filter(i -> !StringUtils.isBlank(i))
                        .collect(Collectors.joining(separator));
            }

            private Object concatReport(final String value) {
                return StringUtils.isBlank(value) ? labelFieldMissing() : value.contains(CUT) ? labelFieldCutInfo() : null;
            }
        });
    }

    static public String label(final String input) {
        return i18n("label." + input);
    }

    static public String i18n(final String code, String... args) {
        return LegalPTUtil.bundleI18N(code, args).getContent(PT);
    }

    static protected MultiLanguageString createEmptyMLS() {
        return createMLS(null, null);
    }

    static public MultiLanguageString createMLS(final String pt, final String en) {
        MultiLanguageString result = new MultiLanguageString();

        if (!StringUtils.isBlank(pt)) {
            result = result.with(PT, pt);
        }
        if (!StringUtils.isBlank(en)) {
            result = result.with(EN, en);
        }

        return result;
    }

    static public Map<Person, Map<CompetenceCourse, Set<Professorship>>> readProfessorships(final DegreeCurricularPlan plan,
            final ExecutionYear year) {

        final Map<Person, Map<CompetenceCourse, Set<Professorship>>> result =
                new TreeMap<>((x, y) -> Collator.getInstance().compare(x.getName(), y.getName()));

        readCourses(plan, year).stream().forEach(course -> {
            readCourseProfessorships(plan, year, course).entrySet().stream().forEach(entry -> {

                final Person person = entry.getKey();
                final Set<Professorship> courseProfessorships = entry.getValue();

                Map<CompetenceCourse, Set<Professorship>> tempMap = result.get(person);
                tempMap = tempMap != null ? tempMap : new HashMap<>();

                Set<Professorship> tempSet = tempMap.get(course);
                tempSet = tempSet != null ? tempSet : new HashSet<>();

                tempSet.addAll(courseProfessorships.stream().filter(p -> p.getPerson() == person).collect(Collectors.toSet()));
                tempMap.put(course, tempSet);
                result.put(person, tempMap);
            });
        });

        return result;
    }

    static public Set<CompetenceCourse> readCourses(final DegreeCurricularPlan plan, final ExecutionYear year) {
        final Set<CompetenceCourse> result = new TreeSet<>((x, y) -> Collator.getInstance().compare(x.getName(), y.getName()));

        year.getExecutionPeriodsSet().stream().forEach(semester -> {
            plan.getRoot().getAllCurricularCourses(semester).stream().filter(c -> c.getCompetenceCourse() != null)
                    .map(c -> c.getCompetenceCourse()).distinct().collect(Collectors.toCollection(() -> result));
        });

        return result;
    }

    static public Map<Person, Set<Professorship>> readCourseProfessorships(final DegreeCurricularPlan plan,
            final ExecutionYear year, final CompetenceCourse course) {

        final Map<Person, Set<Professorship>> result =
                new TreeMap<>((x, y) -> Collator.getInstance().compare(x.getName(), y.getName()));

        result.putAll(readExecutionCourses(plan, year, course).flatMap(ec -> ec.getProfessorshipsSet().stream())
                .collect(Collectors.groupingBy(Professorship::getPerson, Collectors.toSet())));

        return result;
    }

    static public Stream<ExecutionCourse> readExecutionCourses(final DegreeCurricularPlan plan, final ExecutionYear year,
            final CompetenceCourse competence) {

        return competence.getAssociatedCurricularCoursesSet().stream().filter(c -> c.getDegreeCurricularPlan() == plan)
                .flatMap(c -> c.getAssociatedExecutionCoursesSet().stream()).filter(ec -> ec.getExecutionYear() == year);
    }

    static public String getShiftTypeAcronym(final ShiftType t) {
        return LegalMapping.find(A3esInstance.getInstance(), A3esMappingType.SHIFT_TYPE).translate(t);
    }

    static public String getTeachingHours(final ShiftProfessorship sp) {
        final BigDecimal shiftTotalHours =
                sp.getShift().getUnitHours().multiply(new BigDecimal(LegalSettings.getInstance().getNumberOfLessonWeeks()));
        final BigDecimal result = sp.getPercentage() != null ? shiftTotalHours.multiply(new BigDecimal(sp.getPercentage()))
                .divide(BigDecimal.valueOf(100d)).setScale(2, RoundingMode.DOWN) : shiftTotalHours;
        return result.toString();
    }

    static public String getTeachingHoursByShiftType(final Set<Professorship> professorships) {
        return professorships.stream().flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream())
                .map(sp -> getShiftTypeAcronym(sp.getShift().getTypes().iterator().next()) + " - " + getTeachingHours(sp))
                .sorted().collect(Collectors.joining(SEMICOLON));
    }

    static public String getApaFormat(final String authors, final String date, final String title, final String aditionalInfo) {
        final List<String> info = new ArrayList<>();

        if (!StringUtils.isBlank(authors)) {
            info.add(authors.trim());
        }
        if (!StringUtils.isBlank(date)) {
            info.add("(" + date.trim() + ")");
        }

        if (!StringUtils.isBlank(title)) {
            info.add(title.trim());
        }

        if (!StringUtils.isBlank(aditionalInfo)) {
            info.add(aditionalInfo.trim());
        }

        return info.stream().collect(Collectors.joining(". ")).trim();
    }

}
