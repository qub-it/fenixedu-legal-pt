package org.fenixedu.legalpt.ui.rebides;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.scheduler.TaskRunner;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.legalpt.domain.rebides.RebidesInstance;
import org.fenixedu.legalpt.domain.rebides.report.RebidesRequestParameter;
import org.fenixedu.legalpt.domain.report.LegalReportRequest;
import org.fenixedu.legalpt.domain.report.LegalReportResultFile;
import org.fenixedu.legalpt.task.ProcessPendingLegalReportRequest;
import org.fenixedu.legalpt.ui.FenixeduLegalPTBaseController;
import org.fenixedu.legalpt.ui.FenixeduLegalPTController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;

@SpringFunctionality(app = FenixeduLegalPTController.class, title = "label.title.manageRebidesRequests", accessGroup = "logged")
@RequestMapping(RebidesRequestsController.CONTROLLER_URL)
public class RebidesRequestsController extends FenixeduLegalPTBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-legal-pt/rebides/managerebidesrequests";
    public static final String JSP_PATH = CONTROLLER_URL.substring(1);

    @RequestMapping
    public String home() {
        return "forward:" + SEARCH_URL;
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(_SEARCH_URI)
    public String search(final Model model) {
        model.addAttribute("reportRequests", RebidesInstance.getInstance().getLegalRequestsSet());

        return jspPage(_SEARCH_URI);
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(_READ_URI + "/{requestId}")
    public String read(@PathVariable("requestId") final LegalReportRequest request, final Model model) {
        model.addAttribute("reportRequest", request);
        return jspPage(_READ_URI);
    }

    private static final String _DOWNLOAD_RESULT_FILE_URI = "/downloadresultfile";
    public static final String DOWNLOAD_RESULT_FILE_URL = CONTROLLER_URL + _DOWNLOAD_RESULT_FILE_URI;

    @RequestMapping(_DOWNLOAD_RESULT_FILE_URI + "/{resultFileId}")
    public void downloadresultfile(@PathVariable("resultFileId") final LegalReportResultFile resultFile, final Model model,
            final HttpServletResponse response) {
        try {
            response.setContentType(resultFile.getContentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + resultFile.getFilename());
            response.getOutputStream().write(resultFile.getContent());

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(final Model model) {
        final RebidesRequestParameter rebidesParameter = new RebidesRequestParameter();

        final RebidesInstance rebidesInstance = RebidesInstance.getInstance();

        rebidesParameter.setInstitutionCode(rebidesInstance.getInstitutionCode());
        rebidesParameter.setInterlocutorName(rebidesInstance.getInterlocutorName());
        rebidesParameter.setInterlocutorEmail(rebidesInstance.getInterlocutorEmail());
        rebidesParameter.setInterlocutorPhone(rebidesInstance.getInterlocutorPhone());
        rebidesParameter.setMoment("1");
        rebidesParameter.setExecutionYear(ExecutionYear.readCurrentExecutionYear());
        rebidesParameter.setFilterEntriesWithErrors(true);

        model.addAttribute("bean", rebidesParameter);
        model.addAttribute("beanJson", getBeanJson(rebidesParameter));
        model.addAttribute("executionYearDataSource", getNotClosedExecutionYearsJson());

        return jspPage(_CREATE_URI);

    }

    private String getNotClosedExecutionYearsJson() {
        List<TupleDataSourceBean> list =
                ExecutionYear.readNotClosedExecutionYears().stream().sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR).map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());
                    tuple.setText(x.getName());
                    return tuple;
                }).collect(Collectors.toList());
        return new Gson().toJson(list);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam("bean") final RebidesRequestParameter rebidesRequestParameter, final Model model) {

        try {
            final LegalReportRequest request =
                    LegalReportRequest.createRequest(RebidesInstance.getInstance(), rebidesRequestParameter);

            if (request.isPending()) {
                SchedulerSystem.queue(new TaskRunner(new ProcessPendingLegalReportRequest(request.getExternalId())));
            }

            return "redirect:" + SEARCH_URL;
        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        model.addAttribute("bean", rebidesRequestParameter);
        model.addAttribute("beanJson", getBeanJson(rebidesRequestParameter));
        model.addAttribute("executionYearDataSource", getNotClosedExecutionYearsJson());

        return jspPage(_CREATE_URI);

    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

}
