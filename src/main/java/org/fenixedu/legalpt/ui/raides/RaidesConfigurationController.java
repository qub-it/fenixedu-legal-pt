package org.fenixedu.legalpt.ui.raides;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.legalpt.domain.raides.RaidesInstance;
import org.fenixedu.legalpt.dto.raides.RaidesInstanceBean;
import org.fenixedu.legalpt.ui.FenixeduLegalPTBaseController;
import org.fenixedu.legalpt.ui.FenixeduLegalPTController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@SpringFunctionality(app = FenixeduLegalPTController.class, title = "label.title.manageRaidesConfiguration",
        accessGroup = "logged")
@RequestMapping(RaidesConfigurationController.CONTROLLER_URL)
public class RaidesConfigurationController extends FenixeduLegalPTBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-legal-pt/raides/manageraidesconfiguration";
    public static final String JSP_PATH = CONTROLLER_URL.substring(1);

    @RequestMapping
    public String home() {
        return "forward:" + READ_URL;
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI, method = RequestMethod.GET)
    public String read(final Model model) {
        model.addAttribute("raidesInstance", RaidesInstance.getInstance());

        return jspPage(_READ_URI);
    }

    private static final String _EDIT_URI = "/edit";
    public static final String EDIT_URL = CONTROLLER_URL + _EDIT_URI;

    @RequestMapping(value = _EDIT_URI, method = RequestMethod.GET)
    public String edit(final Model model) {
        return _edit(new RaidesInstanceBean(RaidesInstance.getInstance()), model);
    }

    private String _edit(final RaidesInstanceBean bean, final Model model) {
        model.addAttribute("bean", bean);
        model.addAttribute("beanJson", getBeanJson(bean));

        return jspPage(_EDIT_URI);
    }

    @RequestMapping(value = _EDIT_URI, method = RequestMethod.POST)
    public String editpost(@RequestParam("bean") final RaidesInstanceBean bean, final Model model) {
        try {
            RaidesInstance raidesInstance = RaidesInstance.getInstance();
            raidesInstance.edit(raidesInstance.getName(), raidesInstance.getGroup(), raidesInstance.getSynchronous(),
                    raidesInstance.getHasMappings(), bean.getPasswordToZip(), bean.getEnrolledAgreements(),
                    bean.getMobilityAgreements(), bean.getDegreeTransferIngressions(), bean.getDegreeChangeIngressions(),
                    bean.getIngressionsForGeneralAccessRegime(), bean.getInstitutionCode(), bean.getInterlocutorPhone(),
                    bean.getIntegratedMasterFirstCycleGraduatedReportOption(), bean.getDefaultDistrictOfResidence(),
                    bean.isReportGraduatedWithoutConclusionProcess(), bean.getGrantOwnerStatuteTypes());

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
