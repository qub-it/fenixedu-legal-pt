package org.fenixedu.legalpt.ui.settings;

import static pt.ist.fenixframework.FenixFramework.atomic;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.legalpt.dto.LegalSettingsBean;
import org.fenixedu.legalpt.ui.FenixeduLegalPTBaseController;
import org.fenixedu.legalpt.ui.FenixeduLegalPTController;
import org.fenixedu.ulisboa.specifications.domain.legal.settings.LegalSettings;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@SpringFunctionality(app = FenixeduLegalPTController.class, title = "label.title.manageLegalSettings", accessGroup = "#managers")
@RequestMapping(LegalSettingsController.CONTROLLER_URL)
public class LegalSettingsController extends FenixeduLegalPTBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-legal-pt/settings/managelegalsettings";
    public static final String JSP_PATH = CONTROLLER_URL.substring(1);

    @RequestMapping
    public String home() {
        return "forward:" + READ_URL;
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI, method = RequestMethod.GET)
    public String read(final Model model) {
        model.addAttribute("instance", LegalSettings.getInstance());

        return jspPage(_READ_URI);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

    private static final String _EDIT_URI = "/edit";
    public static final String EDIT_URL = CONTROLLER_URL + _EDIT_URI;

    @RequestMapping(value = _EDIT_URI, method = RequestMethod.GET)
    public String edit(final Model model) {
        return _edit(new LegalSettingsBean(LegalSettings.getInstance()), model);
    }

    private String _edit(final LegalSettingsBean bean, final Model model) {
        model.addAttribute("bean", bean);
        model.addAttribute("beanJson", getBeanJson(bean));

        return jspPage(_EDIT_URI);
    }

    @RequestMapping(value = _EDIT_URI, method = RequestMethod.POST)
    public String editpost(@RequestParam("bean") final LegalSettingsBean bean, final Model model) {
        try {
            atomic(() -> {
                final LegalSettings instance = LegalSettings.getInstance();
                instance.setNumberOfLessonWeeks(bean.getNumberOfLessonWeeks());
                instance.setA3esURL(bean.getA3esURL());
            });

            return "redirect:" + READ_URL;
        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return _edit(bean, model);
    }

}
