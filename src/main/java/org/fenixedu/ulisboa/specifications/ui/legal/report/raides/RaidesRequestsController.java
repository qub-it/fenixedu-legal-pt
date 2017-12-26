package org.fenixedu.ulisboa.specifications.ui.legal.report.raides;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.scheduler.TaskRunner;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesRequestParameter;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequest;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFile;
import org.fenixedu.ulisboa.specifications.domain.legal.task.ProcessPendingLegalReportRequest;
import org.fenixedu.ulisboa.specifications.dto.DegreeTypeBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.manageRaidesRequests",
        accessGroup = "logged")
@RequestMapping(RaidesRequestsController.CONTROLLER_URL)
public class RaidesRequestsController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/manageraidesrequests";
    public static final String JSP_PATH = "fenixedu-ulisboa-specifications/manageraidesrequests";

    @RequestMapping
    public String home() {
        return "forward:" + SEARCH_URL;
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(_SEARCH_URI)
    public String search(final Model model) {
        model.addAttribute("reportRequests", RaidesInstance.getInstance().getLegalRequestsSet());

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
        final RaidesRequestParameter raidesRequestParameter = new RaidesRequestParameter();

        Raides.fillRaidesRequestDefaultData(raidesRequestParameter);

        return _create(raidesRequestParameter, model);
    }

    private static final String _CREATE_BY_COPY_URI = "/createByCopy";
    public static final String CREATE_BY_COPY_URL = CONTROLLER_URL + _CREATE_BY_COPY_URI;

    @RequestMapping(value = _CREATE_BY_COPY_URI + "/{legalReportRequestId}", method = RequestMethod.GET)
    public String createByCopy(@PathVariable("legalReportRequestId") final LegalReportRequest legalReportRequest,
            final Model model) {
        final RaidesRequestParameter previousRequestParameters = legalReportRequest.getParametersAs(RaidesRequestParameter.class);
        return _create(previousRequestParameters.copy(), model);
    }

    private String _create(final RaidesRequestParameter raidesRequestParameter, final Model model) {
        model.addAttribute("bean", raidesRequestParameter);
        model.addAttribute("beanJson", getBeanJson(raidesRequestParameter));
        //Bean is serialized and saved in database
        //The below attributes don't need to be serialized.
        model.addAttribute("executionYearDataSource", getNotClosedExecutionYearsJson());
        model.addAttribute("degreeTypeDataSource", getDegreeTypesJson());

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

    private String getDegreeTypesJson() {
        List<DegreeTypeBean> list = Bennu.getInstance().getDegreeTypeSet().stream().map(type -> new DegreeTypeBean(type))
                .sorted(DegreeTypeBean.COMPARE_BY_TEXT).collect(Collectors.toList());
        return new Gson().toJson(list);
    }

    private static final String _CREATE_POSTBACK_URI = "/createpostback";
    public static final String CREATE_POSTBACK_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _CREATE_POSTBACK_URI, method = RequestMethod.GET)
    public String createpostback(final Model model) {

        return _create(null, model);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String createpost(@RequestParam("bean") final RaidesRequestParameter raidesRequestParameter, final Model model) {

        try {
            final LegalReportRequest request =
                    LegalReportRequest.createRequest(RaidesInstance.getInstance(), raidesRequestParameter);

            if (request.isPending()) {
                SchedulerSystem.queue(new TaskRunner(new ProcessPendingLegalReportRequest(request.getExternalId())));
            }

            return "redirect:" + SEARCH_URL;
        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return _create(raidesRequestParameter, model);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

}
