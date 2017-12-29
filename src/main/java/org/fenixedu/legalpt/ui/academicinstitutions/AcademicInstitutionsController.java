package org.fenixedu.legalpt.ui.academicinstitutions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.legalpt.ui.FenixeduLegalPTBaseController;
import org.fenixedu.legalpt.ui.FenixeduLegalPTController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@SpringFunctionality(app = FenixeduLegalPTController.class, title = "label.title.AcademicInstitutions", accessGroup = "logged")
@RequestMapping(AcademicInstitutionsController.CONTROLLER_URL)
public class AcademicInstitutionsController extends FenixeduLegalPTBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-legal-pt/academicinstitutions";
    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    @RequestMapping
    public String home(Model model) {
        return "forward:" + VIEW_ACADEMIC_UNITS_URL;
    }

    public static final String _VIEW_ACADEMIC_UNITS_URI = "/viewacademicunits";
    public static final String VIEW_ACADEMIC_UNITS_URL = CONTROLLER_URL + _VIEW_ACADEMIC_UNITS_URI;

    @RequestMapping(value = _VIEW_ACADEMIC_UNITS_URI, method = RequestMethod.GET)
    public String viewacademicunits(final Model model, final RedirectAttributes redirectAttributes) {

        model.addAttribute("officialAcademicUnits", readAllOfficialAcademicUnits());
        model.addAttribute("unofficialAcademicUnits", readAllUnofficialAcademicUnits());

        return jspPage(_VIEW_ACADEMIC_UNITS_URI);
    }

    private List<Unit> readAllUnofficialAcademicUnits() {
        return Unit.readAllUnits().stream().filter(u -> u.isAcademicalUnit() && Strings.isNullOrEmpty(u.getCode()))
                .sorted(Unit.COMPARATOR_BY_NAME_AND_ID).collect(Collectors.toList());
    }

    private List<Unit> readAllOfficialAcademicUnits() {
        return Unit.readAllUnits().stream().filter(u -> u.isAcademicalUnit() && !Strings.isNullOrEmpty(u.getCode()))
                .sorted(Unit.COMPARATOR_BY_NAME_AND_ID).collect(Collectors.toList());
    }

    public static final String _VIEW_ACADEMIC_UNIT_DETAIL_URI = "/viewacademicunitdetail";
    public static final String VIEW_ACADEMIC_UNIT_DETAIL_URL = CONTROLLER_URL + _VIEW_ACADEMIC_UNIT_DETAIL_URI;

    @RequestMapping(value = _VIEW_ACADEMIC_UNIT_DETAIL_URI + "/{unitId}", method = RequestMethod.GET)
    public String viewacademicunitdetail(@PathVariable("unitId") final Unit unit, final Model model,
            final RedirectAttributes redirectAttributes) {

        model.addAttribute("academicUnit", unit);

        return jspPage(_VIEW_ACADEMIC_UNIT_DETAIL_URI);
    }

    public static final String _VIEW_DEGREE_DESIGNATIONS_URI = "/viewdegreedesignations";
    public static final String VIEW_DEGREE_DESIGNATIONS_URL = CONTROLLER_URL + _VIEW_DEGREE_DESIGNATIONS_URI;

    @RequestMapping(value = _VIEW_DEGREE_DESIGNATIONS_URI, method = RequestMethod.GET)
    public String viewdegreedesignations(final Model model, final RedirectAttributes redirectAttributes) {

        model.addAttribute("degreeDesignations", readDegreeDesignations());

        return jspPage(_VIEW_DEGREE_DESIGNATIONS_URI);
    }

    private List<DegreeDesignation> readDegreeDesignations() {
        final List<DegreeDesignation> result = Lists.newArrayList(Bennu.getInstance().getDegreeDesignationsSet());

        Collections.sort(result, new Comparator<DegreeDesignation>() {

            @Override
            public int compare(final DegreeDesignation o1, final DegreeDesignation o2) {
                int c = o1.getDescription().compareTo(o2.getDescription());

                return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
            }

        });

        return result;
    }

    public static final String _VIEW_DEGREE_DESIGNATION_DETAIL_URI = "/viewdegreedesignationdetail";
    public static final String VIEW_DEGREE_DESIGNATION_DETAIL_URL = CONTROLLER_URL + _VIEW_DEGREE_DESIGNATION_DETAIL_URI;

    @RequestMapping(value = _VIEW_DEGREE_DESIGNATION_DETAIL_URI + "/{degreeDesignationId}", method = RequestMethod.GET)
    public String viewdegreedesignationdetail(@PathVariable("degreeDesignationId") final DegreeDesignation degreeDesignation,
            final Model model, final RedirectAttributes redirectAttributes) {
        model.addAttribute("degreeDesignation", degreeDesignation);

        return jspPage(_VIEW_DEGREE_DESIGNATION_DETAIL_URI);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

}
