package org.fenixedu.ulisboa.specifications.ui.legal.academicinstitutions.importation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ExcelUtils;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Collections;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.AcademicInstitutionsImportation",
        accessGroup = "logged")
@RequestMapping(AcademicInstitutionsImportationController.CONTROLLER_URL)
public class AcademicInstitutionsImportationController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/academic-institutions-importation";
    private static final String JSP_PATH = "fenixedu-ulisboa-specifications/academic-institutions-importation";

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

    @RequestMapping(value = _VIEW_DEGREE_DESIGNATION_DETAIL_URI, method = RequestMethod.GET)
    public String viewdegreedesignationdetail(final DegreeDesignation degreeDesignation, final Model model,
            final RedirectAttributes redirectAttributes) {
        model.addAttribute("degreeDesignation", degreeDesignation);

        return jspPage(_VIEW_DEGREE_DESIGNATION_DETAIL_URI);
    }

    public static final String _UPLOAD_ACADEMIC_UNITS_FILE_URI = "/uploadacademicunitsfile";
    public static final String UPLOAD_ACADEMIC_UNITS_FILE_URL = CONTROLLER_URL + _UPLOAD_ACADEMIC_UNITS_FILE_URI;
    private static final int MAX_COLS = 10;

    @RequestMapping(value = _UPLOAD_ACADEMIC_UNITS_FILE_URI, method = RequestMethod.POST)
    public String uploadacademicunitsfile(
            @RequestParam(value = "officialAcademicUnitsFile", required = true) MultipartFile officialAcademicUnitsFile,
            final Model model, final RedirectAttributes redirectAttributes) {

        try {
            final byte[] byteArray = IOUtils.toByteArray(officialAcademicUnitsFile.getInputStream());
            final List<List<String>> officialList = ExcelUtils.readExcel(new ByteArrayInputStream(byteArray), MAX_COLS);

            final List<OfficialAcademicUnitBean> academicUnitBeanList = Lists.newArrayList();
            final List<OfficialDegreeDesignationBean> degreeDesignationBeanList = Lists.newArrayList();

            OfficialAcademicUnitBean.read(officialList, academicUnitBeanList, degreeDesignationBeanList);

            final OfficialAcademicUnitAndDegreeBeanAggregator aggregatorBean =
                    new OfficialAcademicUnitAndDegreeBeanAggregator(academicUnitBeanList, degreeDesignationBeanList);

            return _uploadacademicunitsfile(aggregatorBean, byteArray, model);

        } catch (IOException e) {
            addErrorMessage(
                    ULisboaSpecificationsUtil.bundle("error.AcademicInstitutionsImportationController.cannot.read.excel.file"),
                    model);
            return _uploadacademicunitsfile(
                    new OfficialAcademicUnitAndDegreeBeanAggregator(Lists.newArrayList(), Lists.newArrayList()), new byte[0],
                    model);
        }

    }

    public String _uploadacademicunitsfile(final OfficialAcademicUnitAndDegreeBeanAggregator aggregatorBean,
            byte[] officialListByteArray, final Model model) {

        model.addAttribute("officialAcademicUnits", aggregatorBean.getAcademicUnitBeanList());
        model.addAttribute("jsonOfficialAcademicUnitsBeans",
                new String(Base64.encodeBase64(officialListByteArray), Charset.forName("ASCII")));

        return jspPage(_UPLOAD_ACADEMIC_UNITS_FILE_URI);
    }

    public static final String _UPDATE_ACADEMIC_UNITS_URI = "/updateacademicunits";
    public static final String UPDATE_ACADEMIC_UNITS_URL = CONTROLLER_URL + _UPDATE_ACADEMIC_UNITS_URI;

    @RequestMapping(value = _UPDATE_ACADEMIC_UNITS_URI, method = RequestMethod.POST)
    public String updateacademicunits(@RequestParam(value = "officialAcademicUnitsFile", required = true) MultipartFile officialAcademicUnitsFile, 
            final Model model, final RedirectAttributes redirectAttributes) {

        try {
            final byte[] byteArray = IOUtils.toByteArray(officialAcademicUnitsFile.getInputStream());
            final List<List<String>> officialList = ExcelUtils.readExcel(new ByteArrayInputStream(byteArray), MAX_COLS);

            final List<OfficialAcademicUnitBean> academicUnitBeanList = Lists.newArrayList();
            final List<OfficialDegreeDesignationBean> degreeDesignationBeanList = Lists.newArrayList();

            OfficialAcademicUnitBean.read(officialList, academicUnitBeanList, degreeDesignationBeanList);
            
            OfficialAcademicUnitBean.updateAcademicUnits(academicUnitBeanList);

            return redirect(UPDATE_ACADEMIC_UNITS_URL, model, redirectAttributes);
            
        } catch (IOException e) {
            addErrorMessage(
                    ULisboaSpecificationsUtil.bundle("error.AcademicInstitutionsImportationController.cannot.read.excel.file"),
                    model);
            return _uploadacademicunitsfile(
                    new OfficialAcademicUnitAndDegreeBeanAggregator(Lists.newArrayList(), Lists.newArrayList()), new byte[0],
                    model);
        }
    }

    public static final String _UPLOAD_DEGREE_DESIGNATIONS_FILE_URI = "/uploaddegreedesignationsfile";
    public static final String UPLOAD_DEGREE_DESIGNATIONS_FILE_URL = CONTROLLER_URL + _UPLOAD_DEGREE_DESIGNATIONS_FILE_URI;

    @RequestMapping(value = _UPLOAD_DEGREE_DESIGNATIONS_FILE_URI, method = RequestMethod.GET)
    public String uploaddegreedesignationsfile(@RequestParam(value = "officialDegreeDesignationsFile", required = true) MultipartFile officialDegreeDesignationsFile,
            final Model model, final RedirectAttributes redirectAttributes) {

        try {
            final List<List<String>> officialList =
                    ExcelUtils.readExcel(officialDegreeDesignationsFile.getInputStream(), MAX_COLS);

            final List<OfficialAcademicUnitBean> academicUnitBeanList = Lists.newArrayList();
            final List<OfficialDegreeDesignationBean> degreeDesignationBeanList = Lists.newArrayList();

            OfficialAcademicUnitBean.read(officialList, academicUnitBeanList, degreeDesignationBeanList);

            model.addAttribute("officialDegreeDesignations", degreeDesignationBeanList);
        } catch (IOException e) {
            addErrorMessage(
                    ULisboaSpecificationsUtil.bundle("error.AcademicInstitutionsImportationController.cannot.read.excel.file"),
                    model);
        }

        return jspPage(_UPLOAD_DEGREE_DESIGNATIONS_FILE_URI);
    }

    @RequestMapping(value = _UPLOAD_DEGREE_DESIGNATIONS_FILE_URI, method = RequestMethod.POST)
    public String uploaddegreedesignationsfile(final String somebean, final Model model,
            final RedirectAttributes redirectAttributes) {

        return jspPage(_UPLOAD_DEGREE_DESIGNATIONS_FILE_URI);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

}
