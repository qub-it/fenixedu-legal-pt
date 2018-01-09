package org.fenixedu.legalpt.ui.rebides;

import static pt.ist.fenixframework.FenixFramework.atomic;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.legalpt.domain.rebides.RebidesInstance;
import org.fenixedu.legalpt.dto.rebides.RebidesInstanceBean;
import org.fenixedu.legalpt.ui.FenixeduLegalPTBaseController;
import org.fenixedu.legalpt.ui.FenixeduLegalPTController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@SpringFunctionality(app = FenixeduLegalPTController.class, title = "label.title.manageRebidesConfiguration",
        accessGroup = "logged")
@RequestMapping(RebidesConfigurationController.CONTROLLER_URL)
public class RebidesConfigurationController extends FenixeduLegalPTBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-legal-pt/rebides/managerebidesconfiguration";
    public static final String JSP_PATH = CONTROLLER_URL.substring(1);

    @RequestMapping
    public String home() {
        return "forward:" + READ_URL;
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI, method = RequestMethod.GET)
    public String read(final Model model) {
        model.addAttribute("rebidesInstance", RebidesInstance.getInstance());

        return jspPage(_READ_URI);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

    private static final String _EDIT_URI = "/edit";
    public static final String EDIT_URL = CONTROLLER_URL + _EDIT_URI;

    @RequestMapping(value = _EDIT_URI, method = RequestMethod.GET)
    public String edit(final Model model) {
        return _edit(new RebidesInstanceBean(RebidesInstance.getInstance()), model);
    }

    private String _edit(final RebidesInstanceBean bean, final Model model) {
        model.addAttribute("bean", bean);
        model.addAttribute("beanJson", getBeanJson(bean));

        return jspPage(_EDIT_URI);
    }

    @RequestMapping(value = _EDIT_URI, method = RequestMethod.POST)
    public String editpost(@RequestParam("bean") final RebidesInstanceBean bean, final Model model) {
        try {
            atomic(() -> {
                final RebidesInstance instance = RebidesInstance.getInstance();
                instance.setInstitutionCode(bean.getInstitutionCode());
                instance.setInterlocutorName(bean.getInterlocutorName());
                instance.setInterlocutorEmail(bean.getInterlocutorEmail());
                instance.setInterlocutorPhone(bean.getInterlocutorPhone());
                instance.setPasswordToZip(bean.getPasswordToZip());
            });

            return "redirect:" + READ_URL;
        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return _edit(bean, model);
    }
}
