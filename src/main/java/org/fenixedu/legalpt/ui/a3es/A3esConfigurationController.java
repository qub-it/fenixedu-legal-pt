package org.fenixedu.legalpt.ui.a3es;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.legalpt.domain.a3es.A3esInstance;
import org.fenixedu.legalpt.dto.a3es.A3esInstanceBean;
import org.fenixedu.legalpt.ui.FenixeduLegalPTBaseController;
import org.fenixedu.legalpt.ui.FenixeduLegalPTController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@SpringFunctionality(app = FenixeduLegalPTController.class, title = "label.title.manageA3esConfiguration",
        accessGroup = "#managers")
@RequestMapping(A3esConfigurationController.CONTROLLER_URL)
public class A3esConfigurationController extends FenixeduLegalPTBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-legal-pt/a3es/manageconfiguration";
    public static final String JSP_PATH = CONTROLLER_URL.substring(1);

    @RequestMapping
    public String home() {
        return "forward:" + READ_URL;
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI, method = RequestMethod.GET)
    public String read(final Model model) {
        model.addAttribute("instance", A3esInstance.getInstance());

        return jspPage(_READ_URI);
    }

    private static final String _EDIT_URI = "/edit";
    public static final String EDIT_URL = CONTROLLER_URL + _EDIT_URI;

    @RequestMapping(value = _EDIT_URI, method = RequestMethod.GET)
    public String edit(final Model model) {
        return _edit(new A3esInstanceBean(A3esInstance.getInstance()), model);
    }

    private String _edit(final A3esInstanceBean bean, final Model model) {
        model.addAttribute("bean", bean);
        model.addAttribute("beanJson", getBeanJson(bean));

        return jspPage(_EDIT_URI);
    }

    @RequestMapping(value = _EDIT_URI, method = RequestMethod.POST)
    public String editpost(@RequestParam("bean") final A3esInstanceBean bean, final Model model) {
        try {
            A3esInstance instance = A3esInstance.getInstance();
            instance.edit(instance.getName(), instance.getGroup(), instance.getSynchronous(), instance.getHasMappings(),
                    bean.getMobilityAgreements(), bean.getGroupCourseProfessorshipByPerson(),
                    bean.getGroupPersonProfessorshipByCourse());

            return "redirect:" + READ_URL;
        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return _edit(bean, model);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

}
