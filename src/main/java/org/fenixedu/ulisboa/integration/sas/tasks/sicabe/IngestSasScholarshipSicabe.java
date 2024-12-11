package org.fenixedu.ulisboa.integration.sas.tasks.sicabe;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.academic.domain.student.StudentStatute;
import org.fenixedu.academic.domain.student.services.StatuteServices;
import org.fenixedu.academic.domain.util.email.Message;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.ulisboa.integration.sas.domain.CandidacyState;
import org.fenixedu.ulisboa.integration.sas.domain.SasScholarshipCandidacy;
import org.fenixedu.ulisboa.integration.sas.domain.SocialServicesConfiguration;
import org.fenixedu.ulisboa.integration.sas.service.process.AbstractFillScholarshipService;
import org.fenixedu.ulisboa.integration.sas.service.sicabe.SicabeExternalService;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

//Force task to be read only and process each report on its own transaction to avoid errors in a report affecting other reports
@Task(englishTitle = "Ingest SAS Scholarships from SICABE", readOnly = true)
public class IngestSasScholarshipSicabe extends CronTask {

    @Override
    public void runTask() throws Exception {

        try {
            final int beforeSasCandidacies = Bennu.getInstance().getSasScholarshipCandidaciesSet().size();
            final long beforeWithStateModified = getNumberOfCandidaciesWithModifiedState();

            final SicabeExternalService sicabe = new SicabeExternalService();
            final ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();
            sicabe.removeAllCandidaciesWithoutRegistrationAndLoadAllSasCandidacies(currentExecutionYear);

            sicabe.processAllSasScholarshipCandidacies(currentExecutionYear);

            final int afterSasCandidacies = Bennu.getInstance().getSasScholarshipCandidaciesSet().size();
            final long afterWithStateModified = getNumberOfCandidaciesWithModifiedState();

            HashSet<Registration> listOfWarningToReport = new HashSet<Registration>();
            updatePersonalIngressionDataAndUpdateStatuteType(currentExecutionYear, listOfWarningToReport);

            boolean newCandidaciesProcesses =
                    beforeSasCandidacies != afterSasCandidacies || beforeWithStateModified != afterWithStateModified;

            if (newCandidaciesProcesses || listOfWarningToReport.size() > 0) {
                sendEmailForUser(
                        BundleUtil.getString(AbstractFillScholarshipService.SAS_BUNDLE,
                                "sasScholarship.ingestion.task.message.notification.subject"),

                        (newCandidaciesProcesses ? BundleUtil.getString(AbstractFillScholarshipService.SAS_BUNDLE,
                                "sasScholarship.ingestion.task.message.notification.body",
                                String.valueOf(afterSasCandidacies - beforeSasCandidacies),
                                String.valueOf(afterWithStateModified - beforeWithStateModified)) : "")

                                +

                                (listOfWarningToReport.size() > 0 ? ("\n"
                                        + BundleUtil.getString(AbstractFillScholarshipService.SAS_BUNDLE,
                                                "sasScholarship.ingestion.task.message.notification.body.warnings")
                                        + "\n" + printRegistrationList(listOfWarningToReport)) : "")

                );
            }

        } catch (Throwable e) {
            sendEmailForUser(
                    BundleUtil.getString(AbstractFillScholarshipService.SAS_BUNDLE,
                            "sasScholarship.ingestion.task.message.notification.subject.error"),
                    BundleUtil.getString(AbstractFillScholarshipService.SAS_BUNDLE,
                            "sasScholarship.ingestion.task.message.notification.body.error", ExceptionUtils.getStackTrace(e)));

            throw e;
        }

    }

    private String printRegistrationList(HashSet<Registration> listOfWarningToReport) {

        return listOfWarningToReport.stream().map(r -> r.getNumber() + " - " + r.getStudent().getName() + " - " + " ["
                + r.getDegree().getCode() + " - " + r.getDegree().getPresentationName() + "]").collect(Collectors.joining("\n"));
    }

    @Atomic
    private void updatePersonalIngressionDataAndUpdateStatuteType(final ExecutionYear currentExecutionYear,
            HashSet<Registration> listOfWarningToReport) {
        currentExecutionYear.getSasScholarshipCandidaciesSet().stream()
                .filter(c -> c.getRegistration() != null && c.getRegistration().getStudent() != null).forEach(c -> {

                    updatePersonalIngressionData(c, listOfWarningToReport);

                    updateStatuteType(c, listOfWarningToReport);

                });

    }

    private void updatePersonalIngressionData(final SasScholarshipCandidacy c, HashSet<Registration> listOfWarningToReport) {

        final PersonalIngressionData pid =
                c.getRegistration().getStudent().getPersonalIngressionDataByExecutionYear(c.getExecutionYear());

        if (pid != null && c.getCandidacyState() == CandidacyState.DEFERRED
                && pid.getGrantOwnerType() != GrantOwnerType.HIGHER_EDUCATION_SAS_GRANT_OWNER) {
            // add SAS grant owner information
            pid.setGrantOwnerType(GrantOwnerType.HIGHER_EDUCATION_SAS_GRANT_OWNER);
            return;
        }

        if (pid != null
                && (c.getCandidacyState() == CandidacyState.UNDEFINED || c.getCandidacyState() == CandidacyState.DISMISSED)
                && pid.getGrantOwnerType() == GrantOwnerType.HIGHER_EDUCATION_SAS_GRANT_OWNER) {
            // remove SAS grant owner information
            //pid.setGrantOwnerType(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP);

            listOfWarningToReport.add(c.getRegistration());

            return;
        }
    }

    private void updateStatuteType(SasScholarshipCandidacy c, HashSet<Registration> listOfWarningToReport) {
        final StatuteType sasStatuteType = SocialServicesConfiguration.getInstance().getStatuteTypeSas();

        if (c.getCandidacyState() == CandidacyState.DEFERRED && sasStatuteType != null
                && !studentHasStatuteType(c, sasStatuteType)) {
            // assign grant owner statute
            new StudentStatute(c.getRegistration().getStudent(), sasStatuteType, c.getExecutionYear().getExecutionSemesterFor(1),
                    c.getExecutionYear().getExecutionSemesterFor(2), null, null, "", c.getRegistration());
        }

        if ((c.getCandidacyState() == CandidacyState.UNDEFINED || c.getCandidacyState() == CandidacyState.DISMISSED)
                && sasStatuteType != null && studentHasStatuteType(c, sasStatuteType)) {

            // remove statute
            /*c.getRegistration().getStudent().getStudentStatutesSet().stream()
            .filter(st -> st.getBeginExecutionInterval() == c.getExecutionYear().getExecutionSemesterFor(1)
                    && st.getEndExecutionInterval() == c.getExecutionYear().getExecutionSemesterFor(2)
                    && st.getType() == sasStatuteType)
            .forEach(st -> st.delete());*/

            // send a warning to user
            listOfWarningToReport.add(c.getRegistration());
        }
    }

    private boolean studentHasStatuteType(final SasScholarshipCandidacy candidacy, final StatuteType statuteType) {
        return StatuteServices.findStatuteTypes(candidacy.getRegistration(), candidacy.getExecutionYear()).stream()
                .anyMatch(st -> st == statuteType);
    }

    private long getNumberOfCandidaciesWithModifiedState() {
        return Bennu.getInstance().getSasScholarshipCandidaciesSet().stream().filter(c -> c.isModified()).count();
    }

    public void sendEmailForUser(final String subject, final String body) {

        Runnable runnable = () -> {
            FenixFramework.atomic(() -> {
                final String emailAddress = Bennu.getInstance().getSocialServicesConfiguration().getEmail();

                new Message(Bennu.getInstance().getSystemSender(), Collections.emptyList(), Collections.emptyList(), subject,
                        body, new HashSet<String>(Arrays.asList(emailAddress.split(","))));
            });
        };

        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
