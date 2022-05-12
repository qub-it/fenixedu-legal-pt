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
package org.fenixedu.ulisboa.integration.sas.ui.spring.controller.manageScholarshipReportRequests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.integration.sas.domain.ScholarshipReportFile;
import org.fenixedu.ulisboa.integration.sas.domain.ScholarshipReportRequest;
import org.fenixedu.ulisboa.integration.sas.ui.spring.controller.SasBaseController;
import org.fenixedu.ulisboa.integration.sas.ui.spring.controller.SasController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = SasController.class, title = "label.title.manageScholarshipReportRequests",
        accessGroup = "#academicAdmOffice")
@RequestMapping("/integration/sas/managescholarshipreportrequests/scholarshipreportrequest")
public class ScholarshipReportRequestController extends SasBaseController {

    @RequestMapping
    public String home(Model model) {
        return search(model);
    }

    private ScholarshipReportRequest getScholarshipReportRequest(Model model) {
        return (ScholarshipReportRequest) model.asMap().get("scholarshipReportRequest");
    }

    @RequestMapping(value = "/")
    public String search(Model model) {
        List<ScholarshipReportRequest> searchscholarshipreportrequestResultsDataSet =
                getSearchUniverseSearchScholarshipReportRequestDataSet().stream().collect(Collectors.toList());
        Comparator<? super ScholarshipReportRequest> byMoreRecentFirst =
                (x, y) -> x.getWhenRequested().isAfter(y.getWhenRequested()) ? 1 : -1;
        searchscholarshipreportrequestResultsDataSet.sort(byMoreRecentFirst);

        model.addAttribute("searchscholarshipreportrequestResultsDataSet", searchscholarshipreportrequestResultsDataSet);
        return "integration/sas/managescholarshipreportrequests/scholarshipreportrequest/search";
    }

    private List<ScholarshipReportRequest> getSearchUniverseSearchScholarshipReportRequestDataSet() {
        return Bennu.getInstance().getScholarshipReportRequestsSet().stream().collect(Collectors.toList());
    }

    @RequestMapping(value = "/createscholarshipreportrequeststep1", method = RequestMethod.GET)
    public String createscholarshipreportrequeststep1(Model model) {
        List<ExecutionYear> sortedExecutionYears =
                Bennu.getInstance().getExecutionYearsSet().stream().collect(Collectors.toList());
        sortedExecutionYears.sort(ExecutionYear.COMPARATOR_BY_BEGIN_DATE.reversed());
        model.addAttribute("ScholarshipReportRequest_executionYear_options", sortedExecutionYears);
        return "integration/sas/managescholarshipreportrequests/scholarshipreportrequest/createscholarshipreportrequeststep1";
    }

//				
    @RequestMapping(value = "/createscholarshipreportrequeststep1", method = RequestMethod.POST)
    public String createscholarshipreportrequeststep1(
            @RequestParam(value = "executionyear", required = false) ExecutionYear executionYear,
            @RequestParam(value = "firstyearofcycle", required = false) boolean firstYearOfCycle,
            @RequestParam(value = "contractualisation", required = false) boolean contractualisation, Model model,
            RedirectAttributes redirectAttributes) {

        if (executionYear != null) {
            redirectAttributes.addAttribute("executionyear", executionYear.getExternalId());
            redirectAttributes.addAttribute("firstyearofcycle", firstYearOfCycle);
            redirectAttributes.addAttribute("contractualisation", contractualisation);
            return redirect(
                    "/integration/sas/managescholarshipreportrequests/scholarshipreportrequest/createscholarshipreportrequeststep2/",
                    model, redirectAttributes);
        } else {
            addErrorMessage("An execution year must be selected.", model);
            return createscholarshipreportrequeststep1(model);
        }

    }

    @RequestMapping(value = "/createscholarshipreportrequeststep2", method = RequestMethod.GET)
    public String createscholarshipreportrequeststep2(Model model,
            @RequestParam(value = "executionyear", required = true) ExecutionYear executionYear,
            @RequestParam(value = "firstyearofcycle", required = true) boolean firstYearOfCycle,
            @RequestParam(value = "contractualisation", required = true) boolean contractualisation) {
        model.addAttribute("executionyear", executionYear);
        model.addAttribute("firstyearofcycle", firstYearOfCycle);
        model.addAttribute("contractualisation", contractualisation);
        return "integration/sas/managescholarshipreportrequests/scholarshipreportrequest/createscholarshipreportrequeststep2";
    }

    @RequestMapping(value = "/createscholarshipreportrequeststep2", method = RequestMethod.POST)
    public String createscholarshipreportrequeststep2(
            @RequestParam(value = "executionyear", required = false) ExecutionYear executionYear,
            @RequestParam(value = "firstyearofcycle", required = false) boolean firstYearOfCycle,
            @RequestParam(value = "contractualisation", required = false) boolean contractualisation,
            @RequestParam(value = "file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {

        try {

            ScholarshipReportRequest scholarshipReportRequest =
                    createScholarshipReportRequest(executionYear, firstYearOfCycle,contractualisation, file.getOriginalFilename(), file.getBytes());

            model.addAttribute("scholarshipReportRequest", scholarshipReportRequest);
            return redirect("/integration/sas/managescholarshipreportrequests/scholarshipreportrequest/", model,
                    redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(" Error creating due to " + de.getLocalizedMessage(), model);
            return createscholarshipreportrequeststep2(model, executionYear, firstYearOfCycle, contractualisation);
        } catch (IOException e) {
            //TODO what to do here?
            addErrorMessage(" Error reading submitted file", model);
            return createscholarshipreportrequeststep2(model, executionYear, firstYearOfCycle, contractualisation);
        }
    }

    @Atomic
    public ScholarshipReportRequest createScholarshipReportRequest(ExecutionYear executionYear, boolean firstYearOfCycle,
            boolean contractualisation, String name, byte[] file) {
        ScholarshipReportRequest scholarshipReportRequest =
                new ScholarshipReportRequest(executionYear, firstYearOfCycle, contractualisation, name, file);
        return scholarshipReportRequest;
    }

    @RequestMapping(value = "/createscholarshipreportrequeststep2/backtostep1")
    public String processCreatescholarshipreportrequeststep2ToBackToStep1(Model model,
            @RequestParam(value = "executionyear", required = false) ExecutionYear executionYear,
            @RequestParam(value = "firstyearofcycle", required = false) boolean firstYearOfCycle,
            @RequestParam(value = "contractualisation", required = false) boolean contractualisation,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("executionyear", executionYear.getExternalId());
        redirectAttributes.addAttribute("firstyearofcycle", firstYearOfCycle);
        redirectAttributes.addAttribute("contractualisation", contractualisation);
        return redirect(
                "/integration/sas/managescholarshipreportrequests/scholarshipreportrequest/createscholarshipreportrequeststep1",
                model, redirectAttributes);
    }

    @RequestMapping(value = "/downloadFile/{oid}", method = RequestMethod.GET)
    public void getFile(@PathVariable("oid") ScholarshipReportFile file, HttpServletResponse response) {
        try {
            response.setContentType(file.getContentType());
            response.setHeader("Content-disposition", "attachment; filename=" + file.getFilename());
            InputStream is = new ByteArrayInputStream(file.getContent());
            IOUtils.copy(is, response.getOutputStream());
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }

    }
}
