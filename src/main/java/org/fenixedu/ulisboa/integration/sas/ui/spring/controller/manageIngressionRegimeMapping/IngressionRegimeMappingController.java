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
package org.fenixedu.ulisboa.integration.sas.ui.spring.controller.manageIngressionRegimeMapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.bennu.SasSpringConfiguration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.integration.sas.domain.SasIngressionRegimeMapping;
import org.fenixedu.ulisboa.integration.sas.ui.spring.controller.SasBaseController;
import org.fenixedu.ulisboa.integration.sas.ui.spring.controller.SasController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = SasController.class, title = "label.title.manageIngressionRegimeMapping", accessGroup = "#managers")
@RequestMapping(IngressionRegimeMappingController.CONTROLLER_URL)
public class IngressionRegimeMappingController extends SasBaseController {

    public static final String CONTROLLER_URL = "/integration/sas/manageIngressionRegimeMapping";

    @RequestMapping
    public String home(Model model) {
        return search(model);
    }

    private SasIngressionRegimeMapping getIngressionRegimeMapping(Model model) {
        return (SasIngressionRegimeMapping) model.asMap().get("ingressionRegimeMapping");
    }

    private void setIngressionRegimeMapping(SasIngressionRegimeMapping ingressionRegimeMapping, Model model) {
        model.addAttribute("ingressionRegimeMapping", ingressionRegimeMapping);
    }

    @Atomic
    public void deleteIngressionRegimeMapping(SasIngressionRegimeMapping ingressionRegimeMapping) {
        ingressionRegimeMapping.delete();
    }

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(Model model) {
        List<SasIngressionRegimeMapping> searchIngressionRegimeMappingResultsDataSet = filterSearchIngressionRegimeMapping();

        //add the results dataSet to the model
        model.addAttribute("searchsIngressionRegimeMappingResultsDataSet", searchIngressionRegimeMappingResultsDataSet);
        return "integration/sas/manageIngressionRegimeMapping/search";
    }

    private Stream<SasIngressionRegimeMapping> getSearchUniverseSearchIngressionRegimeMappingDataSet() {
        return SasIngressionRegimeMapping.findAll();
    }

    private List<SasIngressionRegimeMapping> filterSearchIngressionRegimeMapping() {
        return getSearchUniverseSearchIngressionRegimeMappingDataSet().collect(Collectors.toList());
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(Model model) {
        model.addAttribute("ingressionValues", Bennu.getInstance().getIngressionTypesSet().stream().sorted((x, y) -> x.getDescription().compareTo(y.getDescription())).collect(Collectors.toList()));
        
        return "integration/sas/manageIngressionRegimeMapping/create";
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "ingression", required = false) IngressionType ingressionType,
            @RequestParam(value = "regimeCode", required = false) String regimeCode, 
            @RequestParam(value = "regimeCodeWithDescription", required = false) String regimeCodeWithDescription,
            Model model, RedirectAttributes redirectAttributes) {

        try {
            createIngressionRegimeMapping(ingressionType, regimeCode, regimeCodeWithDescription);
            return redirect("/integration/sas/manageIngressionRegimeMapping/", model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(SasSpringConfiguration.BUNDLE, "label.error.create", de.getLocalizedMessage()),
                    model);
            return create(model);
        }
    }

    private static final String _SEARCH_TO_DELETE_ACTION_URI = "/search/delete/";
    public static final String SEARCH_TO_DELETE_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_DELETE_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_DELETE_ACTION_URI + "{oid}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("oid") SasIngressionRegimeMapping ingressionRegimeMapping, Model model,
            RedirectAttributes redirectAttributes) {
        
        setIngressionRegimeMapping(ingressionRegimeMapping, model);
        try {
            deleteIngressionRegimeMapping(ingressionRegimeMapping);

            addInfoMessage(BundleUtil.getString(SasSpringConfiguration.BUNDLE, "label.manageIngressionRegimeMapping.success.delete"), model);
            return redirect("/integration/sas/manageIngressionRegimeMapping/", model, redirectAttributes);
        } catch (Exception ex) {
            addErrorMessage(BundleUtil.getString(SasSpringConfiguration.BUNDLE, "label.error.update", ex.getLocalizedMessage()),
                    model);
        }

        return "integration/sas/manageIngressionRegimeMapping/search";
    }

    @Atomic
    public SasIngressionRegimeMapping createIngressionRegimeMapping(IngressionType ingressionType, String regimeCode, String regimeCodeWithDescription) {

        return SasIngressionRegimeMapping.create(ingressionType, regimeCode, regimeCodeWithDescription);
    }

    private static final String _UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") SasIngressionRegimeMapping ingressionRegimeMapping, Model model) {
        
        model.addAttribute("ingressionValues", Bennu.getInstance().getIngressionTypesSet());
        setIngressionRegimeMapping(ingressionRegimeMapping, model);
        
        return "integration/sas/manageIngressionRegimeMapping/update";

    }

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("oid") SasIngressionRegimeMapping ingressionRegimeMapping, @RequestParam(value = "ingression",
            required = false) IngressionType ingressionType,
            @RequestParam(value = "regimeCode", required = false) String regimeCode, 
            @RequestParam(value = "regimeCodeWithDescription", required = false) String regimeCodeWithDescription, Model model,
            RedirectAttributes redirectAttributes) {

        setIngressionRegimeMapping(ingressionRegimeMapping, model);

        try {

            updateIngressionRegimeMapping(ingressionType, regimeCode, regimeCodeWithDescription, model);

            return redirect("/integration/sas/manageIngressionRegimeMapping/", model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(SasSpringConfiguration.BUNDLE, "label.error.update") + de.getLocalizedMessage(),
                    model);
            return update(ingressionRegimeMapping, model);

        }
    }

    @Atomic
    public void updateIngressionRegimeMapping(IngressionType ingression, String regimeCode, String regimeCodeWithDescription, Model model) {
        getIngressionRegimeMapping(model).edit(ingression, regimeCode, regimeCodeWithDescription);
    }

}
