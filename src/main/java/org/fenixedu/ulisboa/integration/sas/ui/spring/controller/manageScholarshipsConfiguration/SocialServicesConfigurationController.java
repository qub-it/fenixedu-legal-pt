/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and ServiÃ§os Partilhados da
 * Universidade de Lisboa:
 *  - Copyright Â© 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright Â© 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: xpto@qub-it.com
 *
 * 
 * This file is part of FenixEdu Sas.
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
package org.fenixedu.ulisboa.integration.sas.ui.spring.controller.manageScholarshipsConfiguration;

import java.util.Collections;
import java.util.List;

import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.academic.domain.student.curriculum.CreditsReasonType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.integration.sas.domain.SocialServicesConfiguration;
import org.fenixedu.ulisboa.integration.sas.ui.spring.controller.SasBaseController;
import org.fenixedu.ulisboa.integration.sas.ui.spring.controller.SasController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = SasController.class, title = "label.title.manageScholarshipsConfiguration", accessGroup = "#managers")
@RequestMapping("/integration/sas/managescholarshipsconfiguration/socialservicesconfiguration")
public class SocialServicesConfigurationController extends SasBaseController {

    @RequestMapping
    public String home(Model model) {
        return read(model);
    }

    private SocialServicesConfiguration getSocialServicesConfiguration(Model model) {
        return (SocialServicesConfiguration) model.asMap().get("socialServicesConfiguration");
    }

    @RequestMapping(value = "/read/")
    public String read(Model model) {
        return "integration/sas/managescholarshipsconfiguration/socialservicesconfiguration/read";
    }

    @RequestMapping(value = "/update/", method = RequestMethod.GET)
    public String update(Model model) {
        model.addAttribute("SocialServicesConfiguration_ingressionTypeWhichAreDegreeTransfer_options",
                Bennu.getInstance().getIngressionTypesSet());
        model.addAttribute("SocialServicesConfiguration_ingressionTypesWithExternalData_options",
                Bennu.getInstance().getIngressionTypesSet());
        model.addAttribute("SocialServicesConfiguration_creditsReasonType_options",
                Bennu.getInstance().getCreditsReasonTypesSet());
        model.addAttribute("SocialServicesConfiguration_statuteType_options",
                Bennu.getInstance().getStatuteTypesSet());
        return "integration/sas/managescholarshipsconfiguration/socialservicesconfiguration/update";
    }

    @RequestMapping(value = "/update/", method = RequestMethod.POST)
    public String update(@RequestParam(value = "numberofmonthsofacademicyear", required = false) int numberOfMonthsOfAcademicYear,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "institutionCode", required = false) String institutionCode,
            @RequestParam(value = "ingressiontypewhicharedegreetransfer",
                    required = false) List<IngressionType> ingressionTypeWhichAreDegreeTransfer,
            @RequestParam(value = "ingressiontypeswithexternaldata",
                    required = false) List<IngressionType> ingressionTypesWithExternalData,
            @RequestParam(value = "creditsReasonTypes", required = false) List<CreditsReasonType> creditsReasonTypes,
            @RequestParam(value = "statuteTypeSas", required = false) StatuteType statuteTypeSas,
            Model model,
            RedirectAttributes redirectAttributes) {

        ingressionTypeWhichAreDegreeTransfer =
                ingressionTypeWhichAreDegreeTransfer != null ? ingressionTypeWhichAreDegreeTransfer : Collections
                        .<IngressionType> emptyList();

        ingressionTypesWithExternalData = ingressionTypesWithExternalData != null ? ingressionTypesWithExternalData : Collections
                .<IngressionType> emptyList();

        creditsReasonTypes = creditsReasonTypes != null ? creditsReasonTypes : Collections.<CreditsReasonType> emptyList();

        try {
            updateSocialServicesConfiguration(numberOfMonthsOfAcademicYear, email, institutionCode,
                    ingressionTypeWhichAreDegreeTransfer, ingressionTypesWithExternalData, creditsReasonTypes, statuteTypeSas, model);

            return redirect("/integration/sas/managescholarshipsconfiguration/socialservicesconfiguration/read/", model,
                    redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(" Error updating due to " + de.getLocalizedMessage(), model);
            return update(model);

        }
    }

    @Atomic
    public void updateSocialServicesConfiguration(int numberOfMonthsOfAcademicYear, String email, String institutionCode,
            List<IngressionType> ingressionTypeWhichAreDegreeTransfer, List<IngressionType> ingressionTypesWithExternalData,
            List<CreditsReasonType> creditsReasonTypes, StatuteType statuteType, Model model) {
        getSocialServicesConfiguration().edit(numberOfMonthsOfAcademicYear, email, institutionCode,
                ingressionTypeWhichAreDegreeTransfer, ingressionTypesWithExternalData, creditsReasonTypes, statuteType);
    }

    @ModelAttribute("socialServicesConfiguration")
    public SocialServicesConfiguration getSocialServicesConfiguration() {
        return SocialServicesConfiguration.getInstance();
    }
}
