package org.fenixedu.legalpt.ui.a3es;

import java.util.Set;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.legalpt.domain.a3es.A3esPeriod;
import org.fenixedu.legalpt.domain.a3es.A3esProcessType;
import org.fenixedu.legalpt.dto.a3es.A3esPeriodBean;
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

@Component("org.fenixedu.legalpt.ui.a3es.period")
@SpringFunctionality(app = FenixeduLegalPTController.class, title = "label.searchA3esPeriod", accessGroup = "#managers")
@RequestMapping(A3esPeriodController.CONTROLLER_URL)
public class A3esPeriodController extends FenixeduLegalPTBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-legal-pt/a3es/period";

    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    @RequestMapping
    public String home(final Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    private void setBean(final A3esPeriodBean bean, final Model model) {
        model.addAttribute("periodBeanJson", bean == null ? null : getBeanJson(bean));
        model.addAttribute("periodBean", bean);
    }

    private A3esPeriod getA3esPeriod(final Model model) {
        return (A3esPeriod) model.asMap().get("period");
    }

    private void setA3esPeriod(final A3esPeriod period, final Model model) {
        model.addAttribute("period", period);
    }

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(final Model model) {

        model.addAttribute("searchResults", Sets.newHashSet());

        final A3esPeriodBean bean = new A3esPeriodBean();
        setBean(bean, model);
        return jspPage("search");
    }

    @RequestMapping(value = _SEARCH_URI, method = RequestMethod.POST)
    public String search(@RequestParam(value = "bean", required = false) final A3esPeriodBean bean, final Model model) {

        model.addAttribute("searchResults",
                filterSearch(bean.getType(), bean.getExecutionYear(), bean.getFillInDateBegin(), bean.getFillInDateEnd()));

        setBean(bean, model);
        return jspPage("search");
    }

    static private Set<A3esPeriod> filterSearch(final A3esProcessType type, final ExecutionYear year,
            final DateTime fillInDateBegin, final DateTime fillInDateEnd) {

        return A3esPeriod.find(year, type, fillInDateBegin, fillInDateEnd);
    }

    private static final String _SEARCHPOSTBACK_URI = "/searchpostback/";
    public static final String SEARCHPOSTBACK_URL = CONTROLLER_URL + _SEARCHPOSTBACK_URI;

    @RequestMapping(value = _SEARCHPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> searchpostback(
            @RequestParam(value = "bean", required = false) final A3esPeriodBean bean, final Model model) {

        bean.updateDataSources();
        setBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private static final String _SEARCH_TO_VIEW_ACTION_URI = "/search/view/";
    public static final String SEARCH_TO_VIEW_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_VIEW_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_VIEW_ACTION_URI + "{oid}")
    public String periodSearchToViewAction(@PathVariable("oid") final A3esPeriod period, final Model model,
            final RedirectAttributes redirectAttributes) {

        return redirect(READ_URL + period.getExternalId(), model, redirectAttributes);
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{oid}")
    public String read(@PathVariable("oid") final A3esPeriod period, final Model model) {
        setA3esPeriod(period, model);
        return jspPage("read");
    }

    private static final String _DELETE_URI = "/delete/";
    public static final String DELETE_URL = CONTROLLER_URL + _DELETE_URI;

    @RequestMapping(value = _DELETE_URI + "{oid}", method = RequestMethod.POST)
    public String delete(@PathVariable("oid") final A3esPeriod period, final Model model,
            final RedirectAttributes redirectAttributes) {

        setA3esPeriod(period, model);
        try {
            period.delete();

            addInfoMessage(LegalPTUtil.bundle("label.success.delete"), model);
            return redirect(CONTROLLER_URL, model, redirectAttributes);

        } catch (Exception ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return jspPage("read/" + getA3esPeriod(model).getExternalId());
    }

    private static final String _UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") final A3esPeriod period, final Model model) {
        setA3esPeriod(period, model);

        final A3esPeriodBean bean = new A3esPeriodBean(period);
        setBean(bean, model);
        return jspPage("update");
    }

    private static final String _UPDATEPOSTBACK_URI = "/updatepostback/";
    public static final String UPDATEPOSTBACK_URL = CONTROLLER_URL + _UPDATEPOSTBACK_URI;

    @RequestMapping(value = _UPDATEPOSTBACK_URI + "{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> updatepostback(@PathVariable("oid") final A3esPeriod period,
            @RequestParam(value = "bean", required = false) final A3esPeriodBean bean, final Model model) {

        bean.updateDataSources();
        setBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("oid") final A3esPeriod period,
            @RequestParam(value = "bean", required = false) final A3esPeriodBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        setA3esPeriod(period, model);
        try {

            period.edit(bean.getFillInDateBegin(), bean.getFillInDateEnd());

            addInfoMessage(LegalPTUtil.bundle("label.success.update"), model);
            return redirect(READ_URL + getA3esPeriod(model).getExternalId(), model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(de.getLocalizedMessage(), model);
            setA3esPeriod(period, model);
            setBean(bean, model);

            return jspPage("update");
        }
    }

    private static final String _CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(final Model model) {
        final A3esPeriodBean bean = new A3esPeriodBean();
        setBean(bean, model);
        return jspPage("create");
    }

    private static final String _CREATEPOSTBACK_URI = "/createpostback/";
    public static final String CREATEPOSTBACK_URL = CONTROLLER_URL + _CREATEPOSTBACK_URI;

    @RequestMapping(value = _CREATEPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> createpostback(
            @RequestParam(value = "bean", required = false) final A3esPeriodBean bean, final Model model) {

        bean.updateDataSources();
        setBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "bean", required = false) final A3esPeriodBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            final A3esPeriod period = A3esPeriod.create(bean.getExecutionYear(), bean.getType(), bean.getFillInDateBegin(),
                    bean.getFillInDateEnd());

            model.addAttribute("period", period);
            return redirect(READ_URL + getA3esPeriod(model).getExternalId(), model, redirectAttributes);

        } catch (Exception de) {

            addErrorMessage(de.getLocalizedMessage(), model);
            setBean(bean, model);
            return jspPage("create");
        }
    }

}
