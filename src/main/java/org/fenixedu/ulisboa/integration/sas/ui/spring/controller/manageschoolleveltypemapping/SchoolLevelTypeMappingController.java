/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: nuno.pinheiro@qub-it.com
 *
 * 
 * This file is part of FenixEdu ULisboa SAS Integration.
 *
 * FenixEdu Sas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Sas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Sas.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.integration.sas.ui.spring.controller.manageschoolleveltypemapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.bennu.SasSpringConfiguration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.integration.sas.domain.SchoolLevelTypeMapping;
import org.fenixedu.ulisboa.integration.sas.ui.spring.controller.SasBaseController;
import org.fenixedu.ulisboa.integration.sas.ui.spring.controller.SasController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = SasController.class, title = "label.title.manageSchoolLevelTypeMapping", accessGroup = "#managers")
@RequestMapping(SchoolLevelTypeMappingController.CONTROLLER_URL)
public class SchoolLevelTypeMappingController extends SasBaseController {

    public static final String CONTROLLER_URL = "/integration/sas/manageschoolleveltypemapping/schoolleveltypemapping";

    @RequestMapping
    public String home(Model model) {
        return search(model);
    }

    private SchoolLevelTypeMapping getSchoolLevelTypeMapping(Model model) {
        return (SchoolLevelTypeMapping) model.asMap().get("schoolLevelTypeMapping");
    }

    private void setSchoolLevelTypeMapping(SchoolLevelTypeMapping schoolLevelTypeMapping, Model model) {
        model.addAttribute("schoolLevelTypeMapping", schoolLevelTypeMapping);
    }

    @Atomic
    public void deleteSchoolLevelTypeMapping(SchoolLevelTypeMapping schoolLevelTypeMapping) {
        schoolLevelTypeMapping.delete();
    }

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(Model model) {
        List<SchoolLevelTypeMapping> searchschoolleveltypemappingResultsDataSet = filterSearchSchoolLevelTypeMapping();

        //add the results dataSet to the model
        model.addAttribute("searchschoolleveltypemappingResultsDataSet", searchschoolleveltypemappingResultsDataSet);
        return "integration/sas/manageschoolleveltypemapping/schoolleveltypemapping/search";
    }

    private Stream<SchoolLevelTypeMapping> getSearchUniverseSearchSchoolLevelTypeMappingDataSet() {
        return SchoolLevelTypeMapping.findAll();
    }

    private List<SchoolLevelTypeMapping> filterSearchSchoolLevelTypeMapping() {
        return getSearchUniverseSearchSchoolLevelTypeMappingDataSet().collect(Collectors.toList());
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(Model model) {
        model.addAttribute("schoolLevelValues", SchoolLevelType.values());

        model.addAttribute("SchoolLevelTypeMapping_degreeType_options", getSelectableElements());

        return "integration/sas/manageschoolleveltypemapping/schoolleveltypemapping/create";
    }

    //We are only interested in degree types which have no mapping associated
    private List<DegreeType> getSelectableElements() {
        return Bennu.getInstance().getDegreeTypeSet().stream().filter(dt -> dt.getSchoolLevelTypeMapping() == null)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "schoollevel", required = false) SchoolLevelType schoolLevel, @RequestParam(
            value = "degreetype", required = false) DegreeType degreeType, Model model, RedirectAttributes redirectAttributes) {

        try {

            SchoolLevelTypeMapping schoolLevelTypeMapping = createSchoolLevelTypeMapping(schoolLevel, degreeType);

            return redirect("/integration/sas/manageschoolleveltypemapping/schoolleveltypemapping/", model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(SasSpringConfiguration.BUNDLE, "label.error.create") + de.getLocalizedMessage(),
                    model);
            return create(model);
        }
    }

    private static final String _SEARCH_TO_DELETE_ACTION_URI = "/search/delete/";
    public static final String SEARCH_TO_DELETE_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_DELETE_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_DELETE_ACTION_URI + "{oid}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("oid") SchoolLevelTypeMapping schoolLevelTypeMapping, Model model,
            RedirectAttributes redirectAttributes) {
        setSchoolLevelTypeMapping(schoolLevelTypeMapping, model);
        try {
            deleteSchoolLevelTypeMapping(schoolLevelTypeMapping);

            addInfoMessage("Sucess deleting SchoolLevelTypeMapping ...", model);
            return redirect("/integration/sas/manageschoolleveltypemapping/schoolleveltypemapping/", model, redirectAttributes);
        } catch (Exception ex) {
            addErrorMessage(BundleUtil.getString(SasSpringConfiguration.BUNDLE, "label.error.update") + ex.getLocalizedMessage(),
                    model);
        }

        return "integration/sas/manageschoolleveltypemapping/schoolleveltypemapping/search";
    }

    @Atomic
    public SchoolLevelTypeMapping createSchoolLevelTypeMapping(SchoolLevelType schoolLevel, DegreeType degreeType) {

        return SchoolLevelTypeMapping.create(schoolLevel, degreeType);
    }

    private static final String _UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") SchoolLevelTypeMapping schoolLevelTypeMapping, Model model) {
        model.addAttribute("schoolLevelValues", SchoolLevelType.values());
        List<DegreeType> selectableElements = getSelectableElements();
        selectableElements.add(schoolLevelTypeMapping.getDegreeType());
        model.addAttribute("SchoolLevelTypeMapping_degreeType_options", selectableElements);
        setSchoolLevelTypeMapping(schoolLevelTypeMapping, model);

        return "integration/sas/manageschoolleveltypemapping/schoolleveltypemapping/update";

    }

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("oid") SchoolLevelTypeMapping schoolLevelTypeMapping, @RequestParam(value = "schoollevel",
            required = false) SchoolLevelType schoolLevel,
            @RequestParam(value = "degreetype", required = false) DegreeType degreeType, Model model,
            RedirectAttributes redirectAttributes) {

        setSchoolLevelTypeMapping(schoolLevelTypeMapping, model);

        try {

            updateSchoolLevelTypeMapping(schoolLevel, degreeType, model);

            return redirect("/integration/sas/manageschoolleveltypemapping/schoolleveltypemapping/", model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(SasSpringConfiguration.BUNDLE, "label.error.update") + de.getLocalizedMessage(),
                    model);
            return update(schoolLevelTypeMapping, model);

        }
    }

    @Atomic
    public void updateSchoolLevelTypeMapping(SchoolLevelType schoolLevel, DegreeType degreeType, Model model) {
        getSchoolLevelTypeMapping(model).edit(schoolLevel, degreeType);
    }

}
