package org.fenixedu.legalpt.ui.raides;

import java.util.List;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.legalpt.dto.raides.BranchMappingEntryBean;
import org.fenixedu.legalpt.ui.FenixeduLegalPTBaseController;
import org.fenixedu.legalpt.ui.FenixeduLegalPTController;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMappingEntry;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.BranchMappingType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduLegalPTController.class, title = "label.title.manageBranchMappings", accessGroup = "logged")
@RequestMapping(ManageBranchMappingsController.CONTROLLER_URL)
public class ManageBranchMappingsController extends FenixeduLegalPTBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-legal-pt/raides/managebranchmappings";
    public static final String JSP_PATH = CONTROLLER_URL.substring(1);

    @RequestMapping
    public String home() {
        return "redirect:" + SEARCH_URL;
    }

    private String jspPage(final String page) {
        return JSP_PATH + page;
    }

    private static final String SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + SEARCH_URI;

    @RequestMapping(value = SEARCH_URI)
    public String search(final Model model) {

        final List<DegreeCurricularPlan> dcpList = Lists.newArrayList(Bennu.getInstance().getDegreeCurricularPlansSet());

        java.util.Collections.sort(dcpList,
                DegreeCurricularPlan.DEGREE_CURRICULAR_PLAN_COMPARATOR_BY_DEGREE_TYPE_AND_EXECUTION_DEGREE_AND_DEGREE_CODE);

        model.addAttribute("dcpList", dcpList);

        return jspPage(SEARCH_URI);
    }

    private static final String VIEW_URI = "/view";
    public static final String VIEW_URL = CONTROLLER_URL + VIEW_URI;

    @RequestMapping(value = VIEW_URI + "/{degreeCurricularPlanId}")
    public String view(@PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            final Model model) {
        model.addAttribute("degreeCurricularPlan", degreeCurricularPlan);
        model.addAttribute("mappingEntries", BranchMappingType.getInstance()
                .getMappingEntries(BranchMappingType.readMapping(RaidesInstance.getInstance()), degreeCurricularPlan));

        return jspPage(VIEW_URI);
    }

    private static final String ADD_MAPPING_URI = "/addmapping";
    public static final String ADD_MAPPING_URL = CONTROLLER_URL + ADD_MAPPING_URI;

    @RequestMapping(value = ADD_MAPPING_URI + "/{degreeCurricularPlanId}", method = RequestMethod.GET)
    public String addmapping(@PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            final Model model) {
        return _addmapping(degreeCurricularPlan, model);
    }

    private String _addmapping(final DegreeCurricularPlan degreeCurricularPlan, final Model model) {

        model.addAttribute("degreeCurricularPlan", degreeCurricularPlan);

        final BranchMappingEntryBean bean = new BranchMappingEntryBean(degreeCurricularPlan);
        model.addAttribute("bean", bean);
        model.addAttribute("beanJson", getBeanJson(bean));

        return jspPage(ADD_MAPPING_URI);
    }

    private static final String ADD_MAPPING_POST_URI = "/addmappingpost";
    public static final String ADD_MAPPING_POST_URL = CONTROLLER_URL + ADD_MAPPING_POST_URI;

    @RequestMapping(value = ADD_MAPPING_POST_URI + "/{degreeCurricularPlanId}", method = RequestMethod.POST)
    public String addmappingpost(@PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @RequestParam("bean") final BranchMappingEntryBean bean, final Model model) {
        try {

            addMapping(bean);

            return String.format("redirect:%s/%s", VIEW_URL, degreeCurricularPlan.getExternalId());
        } catch (DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return _addmapping(degreeCurricularPlan, model);
    }

    @Atomic
    private void addMapping(final BranchMappingEntryBean bean) {
        BranchMappingType.readMapping(RaidesInstance.getInstance()).addEntry(
                BranchMappingType.readMapping(RaidesInstance.getInstance()).keyForObject(bean.getBranchKey()), bean.getValue());
    }

    private static final String DELETE_MAPPING_URI = "/deletemapping";
    public static final String DELETE_MAPPING_URL = CONTROLLER_URL + DELETE_MAPPING_URI;

    @RequestMapping(value = DELETE_MAPPING_URI + "/{degreeCurricularPlanId}", method = RequestMethod.POST)
    public String deletemapping(@PathVariable("degreeCurricularPlanId") final DegreeCurricularPlan degreeCurricularPlan,
            @RequestParam("mappingEntryId") final LegalMappingEntry entry, final Model model) {
        try {

            removeMapping(entry);

            return "redirect:" + VIEW_URL + "/" + degreeCurricularPlan.getExternalId();
        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return view(degreeCurricularPlan, model);
    }

    @Atomic
    private void removeMapping(final LegalMappingEntry entry) {
        entry.getLegalMapping().deleteEntry(entry);
    }

}
