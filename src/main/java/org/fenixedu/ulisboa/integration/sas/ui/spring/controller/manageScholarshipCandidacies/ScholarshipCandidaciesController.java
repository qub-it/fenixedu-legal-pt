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
package org.fenixedu.ulisboa.integration.sas.ui.spring.controller.manageScholarshipCandidacies;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.integration.sas.domain.SasScholarshipCandidacy;
import org.fenixedu.ulisboa.integration.sas.domain.SasScholarshipDataChangeLog;
import org.fenixedu.ulisboa.integration.sas.service.process.FillScholarshipException;
import org.fenixedu.ulisboa.integration.sas.service.sicabe.SicabeExternalService;
import org.fenixedu.ulisboa.integration.sas.ui.spring.controller.SasBaseController;
import org.fenixedu.ulisboa.integration.sas.ui.spring.controller.SasController;
import org.fenixedu.ulisboa.integration.sas.util.SasPTUtil;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sun.xml.ws.fault.ServerSOAPFaultException;

import pt.dges.schemas.services.sicabe.v1.DadosAcademicosObterCandidaturasSubmetidasSicabeBusinessMessageFaultFaultMessage;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicosObterCandidaturasSubmetidasSicabeErrorMessageFaultFaultMessage;
import pt.dges.schemas.services.sicabe.v1.DadosAcademicosObterCandidaturasSubmetidasSicabeValidationMessageFaultFaultMessage;
import pt.ist.fenixframework.FenixFramework;

@SpringFunctionality(app = SasController.class, title = "label.title.manageScholarships", accessGroup = "#academicAdmOffice")
@RequestMapping(ScholarshipCandidaciesController.CONTROLLER_URL)
public class ScholarshipCandidaciesController extends SasBaseController {

    public static final String CONTROLLER_URL = "/integration/sas/manageScholarshipCandidacies";

    public static final String JSP_PATH = CONTROLLER_URL.substring(1);

    @RequestMapping
    public String home(Model model) {
        final ExecutionYear executionYear = ExecutionYear.readCurrentExecutionYear();
        return "forward:" + CONTROLLER_URL + "/" + executionYear.getExternalId();
    }

    @RequestMapping(value = "/{executionYearId}", method = RequestMethod.GET)
    public String search(Model model, @PathVariable(value = "executionYearId") ExecutionYear executionYearParam) {

        final List<SasScholarshipCandidacy> scholarshipCandidacies =
                SasScholarshipCandidacy.findAll().stream().filter(c -> c.getExecutionYear() == executionYearParam)
                        .sorted((x, y) -> -(x.getSubmissionDate().compareTo(y.getSubmissionDate()))).collect(Collectors.toList());

        model.addAttribute("scholarshipCandidacies", scholarshipCandidacies);
        model.addAttribute("executionYears", ExecutionYear.readNotClosedExecutionYears().stream()
                .sorted(ExecutionYear.COMPARATOR_BY_BEGIN_DATE.reversed()).collect(Collectors.toList()));
        model.addAttribute("executionYear", executionYearParam);

        return jspPath("search");
    }

    private static final String _CHANGE_EXECUTION_YEAR_URI = "/changeExecutionYear";
    public static final String CHANGE_EXECUTION_YEAR_URL = CONTROLLER_URL + _CHANGE_EXECUTION_YEAR_URI;

    @RequestMapping(value = "/changeExecutionYear", method = RequestMethod.POST)
    public String changeExecutionYear(Model model, @RequestParam(value = "executionYearId") ExecutionYear executionYearParam) {
        return search(model, executionYearParam);
    }

    private String jspPath(String page) {
        return JSP_PATH + "/" + page;
    }

    private static final String _READ_SAS_SCHOLARSHIP_CANDIDACY_URI = "/readSasScholarshipCandidacy";
    public static final String READ_SAS_SCHOLARSHIP_CANDIDACY_URL = CONTROLLER_URL + _READ_SAS_SCHOLARSHIP_CANDIDACY_URI;

    @RequestMapping(value = _READ_SAS_SCHOLARSHIP_CANDIDACY_URI + "/{oid}", method = RequestMethod.GET)
    public String readResumeSasScholarshipCandidacy(@PathVariable("oid") SasScholarshipCandidacy sasScholarshipCandidacy,
            Model model) {
        model.addAttribute("sasScholarshipCandidacy", sasScholarshipCandidacy);

        return jspPath("resume");
    }

    private static final String _SYNC_ALL_ENTRIES_URI = "/syncAll";
    public static final String SYNC_ALL_ENTRIES_URL = CONTROLLER_URL + _SYNC_ALL_ENTRIES_URI;

    @RequestMapping(value = _SYNC_ALL_ENTRIES_URI + "/{executionYearId}", method = RequestMethod.GET)
    public String syncAllEntries(@PathVariable(value = "executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {

        SicabeExternalService sicabe = new SicabeExternalService();
        try {
            sicabe.removeAllCandidaciesWithoutRegistrationAndLoadAllSasCandidacies(executionYear);
            addInfoMessage(SasPTUtil.bundle("label.info.syncAll"), model);
        } catch (DadosAcademicosObterCandidaturasSubmetidasSicabeBusinessMessageFaultFaultMessage
                | DadosAcademicosObterCandidaturasSubmetidasSicabeErrorMessageFaultFaultMessage
                | DadosAcademicosObterCandidaturasSubmetidasSicabeValidationMessageFaultFaultMessage e) {
            addErrorMessage(SasPTUtil.bundle("label.error.syncAll"), model);
        } catch (ServerSOAPFaultException e) {
            addErrorMessage(SasPTUtil.bundle("label.error.connection"), model);
        }

        return redirect(CONTROLLER_URL + "/" + executionYear.getExternalId(), model, redirectAttributes);
    }

    private static final String _PROCESS_ENTRY_URI = "/process";
    public static final String PROCESS_ENTRY_URL = CONTROLLER_URL + _PROCESS_ENTRY_URI;

    @RequestMapping(value = _PROCESS_ENTRY_URI + "/{sasScholarshipCandidacyId}", method = RequestMethod.GET)
    public String processSearchEntries(@PathVariable("sasScholarshipCandidacyId") SasScholarshipCandidacy sasScholarshipCandidacy,
            Model model, RedirectAttributes redirectAttributes) {

        SicabeExternalService sicabe = new SicabeExternalService();
        sicabe.processSasScholarshipCandidacies(Collections.singletonList(sasScholarshipCandidacy));

        addInfoMessage(SasPTUtil.bundle("label.info.process"), model);

        return readResumeSasScholarshipCandidacy(sasScholarshipCandidacy, model);

    }


    private static final String _PROCESS_ALL_ENTRIES_URI = "/processAll";
    public static final String PROCESS_ALL_ENTRIES_URL = CONTROLLER_URL + _PROCESS_ALL_ENTRIES_URI;

    @RequestMapping(value = _PROCESS_ALL_ENTRIES_URI + "/{executionYearId}", method = RequestMethod.GET)
    public String processAll(@PathVariable(value = "executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {

        //process all entries
        SicabeExternalService sicabe = new SicabeExternalService();
        sicabe.processAllSasScholarshipCandidacies(executionYear);

        addInfoMessage(SasPTUtil.bundle("label.info.processAll"), model);

        return redirect(CONTROLLER_URL + "/" + executionYear.getExternalId(), model, redirectAttributes);
    }

    private static final String _SEND_ENTRY_URI = "/send";
    public static final String SEND_ENTRY_URL = CONTROLLER_URL + _SEND_ENTRY_URI;

    @RequestMapping(value = _SEND_ENTRY_URI + "/{sasScholarshipCandidacyId}", method = RequestMethod.GET)
    public String sendSearchEntries(
            @PathVariable(value = "sasScholarshipCandidacyId") SasScholarshipCandidacy sasScholarshipCandidacy, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            SicabeExternalService sicabe = new SicabeExternalService();
            sicabe.sendSasScholarshipsCandidaciesToSicabe(Collections.singletonList(sasScholarshipCandidacy));
            addInfoMessage(SasPTUtil.bundle("label.info.send"), model);
        } catch (RuntimeException e) {
            addErrorMessage(SasPTUtil.bundle("label.error.send"), model);
        }

        model.addAttribute("sasScholarshipCandidacy", sasScholarshipCandidacy);

        return jspPath("resume");
    }

    private static final String _SEND_ALL_ENTRIES_URI = "/sendAll";
    public static final String SEND_ALL_ENTRIES_URL = CONTROLLER_URL + _SEND_ALL_ENTRIES_URI;

    @RequestMapping(value = _SEND_ALL_ENTRIES_URI + "/{executionYearId}", method = RequestMethod.GET)
    public String sendSearchEntries(@PathVariable(value = "executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            SicabeExternalService sicabe = new SicabeExternalService();
            sicabe.sendAllSasScholarshipCandidaciesToSicabe(executionYear);

            addInfoMessage(SasPTUtil.bundle("label.info.sendAll"), model);
        } catch (RuntimeException e) {
            addErrorMessage(SasPTUtil.bundle("label.error.sendAll"), model);
        }

        return redirect(CONTROLLER_URL + "/" + executionYear.getExternalId(), model, redirectAttributes);

    }

    private static final String _SEND_SELECTED_ENTRIES_URI = "/sendSelected";
    public static final String SEND_SELECTED_ENTRIES_URL = CONTROLLER_URL + _SEND_SELECTED_ENTRIES_URI;

    @RequestMapping(value = _SEND_SELECTED_ENTRIES_URI + "/{executionYearId}", method = RequestMethod.POST)
    public String sendSelectedEntries(@RequestParam(value = "selectedCandidacyIds", required = false) String selectedCandidacyIds,
            @PathVariable(value = "executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            SicabeExternalService sicabe = new SicabeExternalService();
            final Collection<SasScholarshipCandidacy> candidacies = selectedCandidacyIds == null ? Collections.emptySet() : Arrays
                    .stream(selectedCandidacyIds.split(",")).map(s -> s.trim()).filter(s -> !s.isEmpty())
                    .map(s -> (SasScholarshipCandidacy) FenixFramework.getDomainObject(s)).collect(Collectors.toSet());

            if (!candidacies.isEmpty()) {
                sicabe.sendSasScholarshipsCandidaciesToSicabe(candidacies);

                addInfoMessage(SasPTUtil.bundle("label.info.sendSelected"), model);

            } else {
                addErrorMessage(SasPTUtil.bundle("label.error.sendSelected.empty.list"), model);
                            }
        } catch (RuntimeException e) {
            addErrorMessage(SasPTUtil.bundle("label.error.sendSelected"), model);
        }

        return redirect(CONTROLLER_URL + "/" + executionYear.getExternalId(), model, redirectAttributes);

    }

    private static final String _DELETE_ENTRY_URI = "/delete";
    public static final String DELETE_ENTRY_URL = CONTROLLER_URL + _DELETE_ENTRY_URI;

    @RequestMapping(value = _DELETE_ENTRY_URI + "/{sasScholarshipCandidacyId}", method = RequestMethod.GET)
    public String delete(@PathVariable(value = "sasScholarshipCandidacyId") SasScholarshipCandidacy sasScholarshipCandidacy,
            Model model, RedirectAttributes redirectAttributes) {

        String studentName = sasScholarshipCandidacy.getCandidacyName();
        final ExecutionYear executionYear = sasScholarshipCandidacy.getExecutionYear();

        try {
            SicabeExternalService sicabe = new SicabeExternalService();
            sicabe.removeSasScholarshipsCandidacy(sasScholarshipCandidacy);

            addInfoMessage(SasPTUtil.bundle("label.info.delete", studentName), model);

            return redirect(CONTROLLER_URL + "/" + executionYear.getExternalId(), model, redirectAttributes);

        } catch (FillScholarshipException e) {
            addErrorMessage(e.getMessage(), model);

            model.addAttribute("sasScholarshipCandidacy", sasScholarshipCandidacy);

            return jspPath("resume");
        }

    }

    private static final String _DELETE_ALL_ENTRIES_URI = "/deleteAll";
    public static final String DELETE_ALL_ENTRIES_URL = CONTROLLER_URL + _DELETE_ALL_ENTRIES_URI;

    @RequestMapping(value = _DELETE_ALL_ENTRIES_URI + "/{executionYearId}", method = RequestMethod.GET)
    public String deleteAll(@PathVariable(value = "executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {

        SicabeExternalService sicabe = new SicabeExternalService();

        boolean showWarningMessage = sicabe.removeAllSasScholarshipsCandidacies(executionYear);

        if (showWarningMessage) {
            addWarningMessage(SasPTUtil.bundle("label.error.delete.already.sent"), model);
        } else {
            addInfoMessage(SasPTUtil.bundle("label.info.deleteAll"), model);
        }

        return redirect(CONTROLLER_URL + "/" + executionYear.getExternalId(), model, redirectAttributes);

    }

    private static final String _VIEW_LOGS_URI = "/logs";
    public static final String VIEW_LOGS_ENTRIES_URL = CONTROLLER_URL + _VIEW_LOGS_URI;

    @RequestMapping(value = _VIEW_LOGS_URI + "/{executionYearId}", method = RequestMethod.GET)
    public String viewLogs(@PathVariable(value = "executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {

        List<SasScholarshipDataChangeLog> logs = Bennu.getInstance().getSasScholarshipDataChangeLogsSet().stream()
                .filter(l -> l.getSasScholarshipCandidacy().getExecutionYear() == executionYear)
                .sorted((x, y) -> -(x.getDate().compareTo(y.getDate()))).collect(Collectors.toList());

        model.addAttribute("sasScholarshipDataChangeLogs", logs);
        return jspPath("viewLogs");
    }

    private static final String _VIEW_LOG_URI = "/log";
    public static final String VIEW_LOG_URL = CONTROLLER_URL + _VIEW_LOG_URI;

    @RequestMapping(value = _VIEW_LOG_URI + "/{sasScholarshipCandidacyId}", method = RequestMethod.GET)
    public String viewLogs(@PathVariable("sasScholarshipCandidacyId") SasScholarshipCandidacy sasScholarshipCandidacy,
            Model model, RedirectAttributes redirectAttributes) {

        List<SasScholarshipDataChangeLog> logs = Bennu.getInstance().getSasScholarshipDataChangeLogsSet().stream()
                .filter(l -> l.getSasScholarshipCandidacy() == sasScholarshipCandidacy)
                .sorted((x, y) -> -(x.getDate().compareTo(y.getDate()))).collect(Collectors.toList());

        model.addAttribute("sasScholarshipDataChangeLogs", logs);
        model.addAttribute("sasScholarshipCandidacy", sasScholarshipCandidacy);
        return jspPath("viewLogs");

    }

    private static final String _EXPORT_TO_XLS_URI = "/exportToXls";
    public static final String EXPORT_TO_XLS_URL = CONTROLLER_URL + _EXPORT_TO_XLS_URI;

    @RequestMapping(value = _EXPORT_TO_XLS_URI + "/{executionYearId}", method = RequestMethod.GET)
    public void exportToXls(@PathVariable(value = "executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes, final HttpServletResponse response) throws IOException {
        writeFile(response, "SAS_Report.xlsx", "application/vnd.ms-excel", SicabeExternalService.export(executionYear));

    }

}
