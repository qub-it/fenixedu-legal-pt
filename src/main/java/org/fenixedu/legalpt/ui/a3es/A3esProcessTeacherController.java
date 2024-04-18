package org.fenixedu.legalpt.ui.a3es;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
import org.fenixedu.legalpt.domain.a3es.A3esProcess;
import org.fenixedu.legalpt.domain.a3es.A3esProcessType;
import org.fenixedu.legalpt.dto.a3es.A3esProcessBean;
import org.fenixedu.legalpt.services.a3es.process.A3esExportService;
import org.fenixedu.legalpt.ui.FenixeduLegalPTBaseController;
import org.fenixedu.legalpt.ui.FenixeduLegalPTController;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

@Component("org.fenixedu.legalpt.ui.a3es.process.teacher")
@SpringFunctionality(app = FenixeduLegalPTController.class, title = "label.searchA3esProcess", accessGroup = "#managers")
@RequestMapping(A3esProcessTeacherController.CONTROLLER_URL)
public class A3esProcessTeacherController extends FenixeduLegalPTBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-legal-pt/a3es/process/teacher";

    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    @RequestMapping
    public String home(final Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    private void setBean(final A3esProcessBean bean, final Model model) {
        model.addAttribute("processBeanJson", bean == null ? null : getBeanJson(bean));
        model.addAttribute("processBean", bean);
    }

    private A3esProcess getA3esProcess(final Model model) {
        return (A3esProcess) model.asMap().get("process");
    }

    private void setA3esProcess(final A3esProcess process, final Model model) {
        model.addAttribute("process", process);
    }

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(final Model model) {

        model.addAttribute("searchResults", Sets.newHashSet());

        final A3esProcessBean bean = new A3esProcessBean();
        setBean(bean, model);
        return jspPage("search");
    }

    @RequestMapping(value = _SEARCH_URI, method = RequestMethod.POST)
    public String search(@RequestParam(value = "bean", required = false) final A3esProcessBean bean, final Model model) {

        model.addAttribute("searchResults",
                filterSearch(bean.getType(), bean.getExecutionYear(), bean.getIdentifier(), bean.getDegreeCurricularPlan()));

        setBean(bean, model);
        return jspPage("search");
    }

    static private Set<A3esProcessBean> filterSearch(final A3esProcessType type, final ExecutionYear year,
            final String identifier, final DegreeCurricularPlan plan) {

        final Set<A3esProcess> processes = A3esProcess.find(A3esProcess.getPeriodUnique(year, type), identifier, plan);
        return processes.stream().map(p -> {

            final A3esProcessBean bean = new A3esProcessBean(p, getTeacher());
            bean.updateCoursesData();
            bean.updateTeachersData();

            return bean.getCoursesData().isEmpty() || bean.getTeachersData().isEmpty() ? null : bean;
        }).filter(i -> i != null).collect(Collectors.toSet());
    }

    private static Teacher getTeacher() {
        return Authenticate.getUser().getPerson().getTeacher();
    }

    private static final String _SEARCHPOSTBACK_URI = "/searchpostback/";
    public static final String SEARCHPOSTBACK_URL = CONTROLLER_URL + _SEARCHPOSTBACK_URI;

    @RequestMapping(value = _SEARCHPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> searchpostback(
            @RequestParam(value = "bean", required = false) final A3esProcessBean bean, final Model model) {

        bean.updateDataSources();
        setBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private static final String _SEARCH_TO_VIEW_ACTION_URI = "/search/view/";
    public static final String SEARCH_TO_VIEW_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_VIEW_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_VIEW_ACTION_URI + "{oid}")
    public String processSearchToViewAction(@PathVariable("oid") final A3esProcess process, final Model model,
            final RedirectAttributes redirectAttributes) {

        return redirect(READ_URL + process.getExternalId(), model, redirectAttributes);
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{oid}")
    public String read(@PathVariable("oid") final A3esProcess process, final Model model) {
        setA3esProcess(process, model);
        return jspPage("read");
    }

    private static final String _VIEWCOURSES_URI = "/viewcourses/";

    @RequestMapping(value = _READ_URI + "{oid}" + _VIEWCOURSES_URI)
    public String processReadToViewCoursesData(@PathVariable("oid") final A3esProcess process, final Model model,
            final RedirectAttributes redirectAttributes) {

        setA3esProcess(process, model);
        return redirect(VIEWCOURSES_URL + getA3esProcess(model).getExternalId(), model, redirectAttributes);
    }

    private static final String _VIEWINFO_URI = "/viewinfo/";

    @RequestMapping(value = _READ_URI + "{oid}" + _VIEWINFO_URI)
    public String processReadToViewInfoData(@PathVariable("oid") final A3esProcess process, final Model model,
            final RedirectAttributes redirectAttributes) {

        setA3esProcess(process, model);
        return redirect(VIEWINFO_URL + getA3esProcess(model).getExternalId(), model, redirectAttributes);
    }

    public static final String VIEWINFO_URL = CONTROLLER_URL + _VIEWINFO_URI;

    @RequestMapping(value = _VIEWINFO_URI + "{oid}", method = RequestMethod.GET)
    public String viewinfo(@PathVariable("oid") final A3esProcess process, final Model model) {
        setA3esProcess(process, model);

        final A3esProcessBean bean = new A3esProcessBean(process);
        bean.updateInfoData();

        this.setBean(bean, model);
        return jspPage("viewinfo");
    }

    private static final String _VIEWINFOPOSTBACK_URI = "/viewinfopostback/";
    public static final String VIEWINFOPOSTBACK_URL = CONTROLLER_URL + _VIEWINFOPOSTBACK_URI;

    @RequestMapping(value = _VIEWINFOPOSTBACK_URI + "{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> viewinfopostback(@PathVariable("oid") final A3esProcess process,
            @RequestParam(value = "bean", required = false) final A3esProcessBean bean, final Model model) {

        this.setBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    public static final String VIEWCOURSES_URL = CONTROLLER_URL + _VIEWCOURSES_URI;

    @RequestMapping(value = _VIEWCOURSES_URI + "{oid}", method = RequestMethod.GET)
    public String viewcourses(@PathVariable("oid") final A3esProcess process, final Model model) {
        setA3esProcess(process, model);

        final A3esProcessBean bean = new A3esProcessBean(process, getTeacher());
        bean.updateCoursesData();

        this.setBean(bean, model);
        return jspPage("viewcourses");
    }

    private static final String _VIEWCOURSESPOSTBACK_URI = "/viewcoursespostback/";
    public static final String VIEWCOURSESPOSTBACK_URL = CONTROLLER_URL + _VIEWCOURSESPOSTBACK_URI;

    @RequestMapping(value = _VIEWCOURSESPOSTBACK_URI + "{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> viewcoursespostback(@PathVariable("oid") final A3esProcess process,
            @RequestParam(value = "bean", required = false) final A3esProcessBean bean, final Model model) {

        this.setBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private static final String _COURSESDOWNLOAD_URI = "/coursesdownload/";
    public static final String COURSESDOWNLOAD_URL = CONTROLLER_URL + _COURSESDOWNLOAD_URI;

    @RequestMapping(value = _COURSESDOWNLOAD_URI, method = RequestMethod.POST)
    public void coursesdownload(@RequestParam(value = "bean", required = false) final A3esProcessBean bean, final Model model,
            final RedirectAttributes redirectAttributes, final HttpServletResponse response) throws IOException {

        if (bean != null) {
            final SpreadsheetBuilder builder = new SpreadsheetBuilder();
            final ByteArrayOutputStream result = new ByteArrayOutputStream();
            A3esExportService.coursesDownload(builder, bean);
            builder.build(WorkbookExportFormat.EXCEL, result);
            writeFile(response, getFileName(bean, LegalPTUtil.bundle("label.courseFiles")), "application/vnd.ms-excel",
                    result.toByteArray());
        }
    }

    private static final String _VIEWTEACHERS_URI = "/viewteachers/";

    @RequestMapping(value = _READ_URI + "{oid}" + _VIEWTEACHERS_URI)
    public String processReadToViewTeachersData(@PathVariable("oid") final A3esProcess process, final Model model,
            final RedirectAttributes redirectAttributes) {

        setA3esProcess(process, model);
        return redirect(VIEWTEACHERS_URL + getA3esProcess(model).getExternalId(), model, redirectAttributes);
    }

    public static final String VIEWTEACHERS_URL = CONTROLLER_URL + _VIEWTEACHERS_URI;

    @RequestMapping(value = _VIEWTEACHERS_URI + "{oid}", method = RequestMethod.GET)
    public String viewteachers(@PathVariable("oid") final A3esProcess process, final Model model) {
        setA3esProcess(process, model);

        final A3esProcessBean bean = new A3esProcessBean(process, getTeacher());
        bean.updateTeachersData();

        this.setBean(bean, model);
        return jspPage("viewteachers");
    }

    private static final String _VIEWTEACHERSPOSTBACK_URI = "/viewteacherspostback/";
    public static final String VIEWTEACHERSPOSTBACK_URL = CONTROLLER_URL + _VIEWTEACHERSPOSTBACK_URI;

    @RequestMapping(value = _VIEWTEACHERSPOSTBACK_URI + "{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> viewteacherspostback(@PathVariable("oid") final A3esProcess process,
            @RequestParam(value = "bean", required = false) final A3esProcessBean bean, final Model model) {

        this.setBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private static final String _TEACHERSDOWNLOAD_URI = "/teachersdownload/";
    public static final String TEACHERSDOWNLOAD_URL = CONTROLLER_URL + _TEACHERSDOWNLOAD_URI;

    @RequestMapping(value = _TEACHERSDOWNLOAD_URI, method = RequestMethod.POST)
    public void teachersdownload(@RequestParam(value = "bean", required = false) final A3esProcessBean bean, final Model model,
            final RedirectAttributes redirectAttributes, final HttpServletResponse response) throws IOException {

        if (bean != null) {
            final SpreadsheetBuilder builder = new SpreadsheetBuilder();
            final ByteArrayOutputStream result = new ByteArrayOutputStream();
            A3esExportService.teachersDownload(builder, bean);
            builder.build(WorkbookExportFormat.EXCEL, result);
            writeFile(response, getFileName(bean, LegalPTUtil.bundle("label.teacherFiles")), "application/vnd.ms-excel",
                    result.toByteArray());
        }
    }

    static private String getFileName(final A3esProcessBean input, final String suffix) {
        final org.fenixedu.academic.domain.organizationalStructure.Unit institutionUnit =
                Bennu.getInstance().getInstitutionUnit();
        final String acronym = institutionUnit.getAcronym();
        final String title =
                acronym + "_" + suffix.replace(" ", "-") + "_" + input.getName().replace(" ", "-").replace("/", "-") + "_";
        return title + new DateTime().toString("yyyy-MM-dd_HH-mm-ss") + ".xls";
    }

}
