package org.fenixedu.ulisboa.integration.sas.service.process;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.FenixEduAcademicConfiguration;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationRegimeType;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.student.curriculum.CreditsReasonType;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.services.StatuteServices;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.bennu.SasSpringConfiguration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.integration.sas.domain.SasIngressionRegimeMapping;
import org.fenixedu.ulisboa.integration.sas.domain.SasScholarshipCandidacy;
import org.fenixedu.ulisboa.integration.sas.domain.SchoolLevelTypeMapping;
import org.fenixedu.ulisboa.integration.sas.domain.SocialServicesConfiguration;
import org.fenixedu.ulisboa.integration.sas.dto.AbstractScholarshipStudentBean;
import org.fenixedu.ulisboa.integration.sas.service.SasDataShareAuthorizationServices;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class AbstractFillScholarshipService {

    private static final String REGISTRATION_TYPE_CANCELED = "CANCELED";

    public static class MessageEntry {

        private boolean publicMessage;

        private String message;

        public MessageEntry(boolean publicMessage, String message) {
            this.publicMessage = publicMessage;
            this.message = message;
        }

        public boolean isPublicMessage() {
            return publicMessage;
        }

        public String getMessage() {
            return message;
        }

        public void setPublicMessage(boolean publicMessage) {
            this.publicMessage = publicMessage;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    private final Multimap<AbstractScholarshipStudentBean, MessageEntry> messages = ArrayListMultimap.create();

    protected static final Map<String, IDDocumentType> ID_DOCUMENT_TYPE_MAPPING = Maps.newHashMap();

    public static final String REGIME_FULL_TIME = "Tempo integral";

    public static final String REGIME_FULL_TIME_WORKING_STUDENT = "Trabalhador estudante tempo integral";

    public static final String REGIME_PARTIAL_TIME = "Tempo parcial";

    public static final String REGIME_PARTIAL_TIME_WORKING_STUDENT = "Trabalhador estudante tempo parcial";

    public static final String REGIME_PROFESSIONAL_INTERNSHIP = "Estágio Profissional";

    public static final String ERROR_OBSERVATION = "ERRO";

    public static final String WARNING_OBSERVATION = "AVISO";

    static {

        // id document types mapping
        ID_DOCUMENT_TYPE_MAPPING.put("BI / N.º ID CIVIL", IDDocumentType.IDENTITY_CARD);
        //ID_DOCUMENT_TYPE_MAPPING.put("BiNaoNacional", IDDocumentType.FOREIGNER_IDENTITY_CARD);
        ID_DOCUMENT_TYPE_MAPPING.put("Autorização de residência", IDDocumentType.RESIDENCE_AUTHORIZATION);
        ID_DOCUMENT_TYPE_MAPPING.put("Passaporte", IDDocumentType.PASSPORT);
        ID_DOCUMENT_TYPE_MAPPING.put("NIF", IDDocumentType.OTHER);
        ID_DOCUMENT_TYPE_MAPPING.put("Outros", IDDocumentType.OTHER);

    }

    public void fillAcademicInfos(Collection<AbstractScholarshipStudentBean> scholarshipStudentBeans, ExecutionYear requestYear,
            boolean firstYearOfCycle) {

        messages.clear();

        for (final AbstractScholarshipStudentBean bean : scholarshipStudentBeans) {
            try {
                fillBeanWithAcademicInfos(bean, getRegistrationByAbstractScholarshipStudentBean(bean, requestYear), requestYear,
                        firstYearOfCycle);
            } catch (FillScholarshipException e) {
                addError(bean, false, e.getMessage());
                bean.setObservations(formatObservations(bean));
            }
        }
    }

    public Registration getRegistrationByAbstractScholarshipStudentBean(AbstractScholarshipStudentBean bean,
            ExecutionYear requestYear) {
        return findRegistration(findStudent(bean, requestYear), bean, requestYear);
    }

    private Registration findRegistration(Student student, AbstractScholarshipStudentBean bean, ExecutionYear requestYear) {

        final Collection<Degree> degrees = findDegree(bean);

        final Predicate<Registration> isEnroled = r -> !getEnroledCurriculumLines(r, requestYear).isEmpty();

        final Set<Registration> registrations = Sets.newHashSet();

        for (final Degree degree : degrees) {
            registrations.addAll(student.getRegistrationsFor(degree).stream().filter(isEnroled).collect(Collectors.toSet()));
        }

        if (registrations.size() == 1) {

            return registrations.iterator().next();

        } else if (registrations.size() > 1) {

            addError(bean, false, "message.error.multiple.registrations");
            bean.setObservations(formatObservations(bean));
            throw new FillScholarshipException("message.error.multiple.registrations");

        } else {

            final Collection<DegreeType> possibleDegreeTypes =
                    degrees.stream().map(d -> d.getDegreeType()).collect(Collectors.toSet());

            final Predicate<Registration> degreeTypePredicate = r -> possibleDegreeTypes.contains(r.getDegreeType());

            final Predicate<Registration> precedentDegreePredicate = r -> r.getDegree().getPrecedentDegreesSet().stream()
                    .anyMatch(pd -> Objects.equal(pd.getMinistryCode(), bean.getDegreeCode()));

            final Collection<Registration> registrationsWithActiveEnrolments = student.getRegistrationsSet().stream()
                    .filter(isEnroled.and(degreeTypePredicate).and(precedentDegreePredicate)).collect(Collectors.toSet());

            if (registrationsWithActiveEnrolments.size() == 1) {
                final Registration registration = registrationsWithActiveEnrolments.iterator().next();
                addWarning(bean, true, "message.warning.input.degree.code.not.equals.to.active.degree.code",
                        registration.getDegree().getCode());
                bean.setObservations(formatObservations(bean));
                return registration;
            } else if (registrationsWithActiveEnrolments.size() > 1) {
                addError(bean, false, "message.error.input.registration.not.found.and.multiple.active.registrations");
                bean.setObservations(formatObservations(bean));
                throw new FillScholarshipException(
                        "message.error.input.registration.not.found.and.multiple.active.registrations");
            } else {
                addError(bean, false, "message.error.input.registration.not.found.and.no.active.registrations");
                bean.setObservations(formatObservations(bean));
                throw new FillScholarshipException("message.error.input.registration.not.found.and.no.active.registrations");
            }

        }

    }

    private Collection<Degree> findDegree(AbstractScholarshipStudentBean bean) {
        final Collection<Degree> degrees = Bennu.getInstance().getDegreesSet().stream().filter(
                d -> Objects.equal(d.getMinistryCode(), bean.getDegreeCode()) || Objects.equal(d.getCode(), bean.getDegreeCode()))
                .collect(Collectors.toSet());

        if (degrees.isEmpty()) {
            addError(bean, false, "message.error.degree.not.found");
            bean.setObservations(formatObservations(bean));
            throw new FillScholarshipException("message.error.degree.not.found");
        }

        return degrees;
    }

    private Student findStudent(AbstractScholarshipStudentBean bean, ExecutionYear requestYear) {

        final Person person = findPerson(bean, requestYear);
        if (person == null) {
            addError(bean, false, "message.error.person.not.found");
            throw new FillScholarshipException("message.error.person.not.found");
        }

        if (person.getStudent() == null) {
            addError(bean, false, "message.error.degree.not.found");
            throw new FillScholarshipException("message.error.person.is.not.a.student");
        }

        if (bean.getStudentNumber() == null) {
            addWarning(bean, false, "message.warning.input.student.number.is.empty");
        }

        return person.getStudent();

    }

    protected Person findPerson(AbstractScholarshipStudentBean bean, ExecutionYear requestYear) {

        final Collection<Person> withDocumentId = Person.readByDocumentIdNumber(bean.getDocumentNumber());

        if (withDocumentId.size() == 1) {
            return ensureDocumentIdType(withDocumentId.iterator().next(), bean);

        } else if (withDocumentId.size() > 1) {
            addWarning(bean, false, "message.warning.multiple.people.found.with.same.document.id");
            return findPersonByName(withDocumentId, bean);
        } else {
            // try partial id document number and with the student number and name

            if (bean.getDocumentNumber().length() != 0) {

                // try document id without check digit
                final String documentIdWithoutCheckDigit =
                        bean.getDocumentNumber().substring(0, bean.getDocumentNumber().length() - 1);

                final Collection<Person> withPartialDocumentId = Person.readByDocumentIdNumber(documentIdWithoutCheckDigit);

                if (withPartialDocumentId.size() == 1) {
                    if ((bean.getDocumentBINumber() == null || !bean.getDocumentBINumber().equals(documentIdWithoutCheckDigit))
                            && !(hasSameCheckDigitValue(bean.getDocumentNumber(), withPartialDocumentId.iterator().next()))) {
                        addWarning(bean, false, "message.warning.input.document.id.not.equals.without.control.digit");
                    }

                    return ensureDocumentIdType(withPartialDocumentId.iterator().next(), bean);
                }

                if (withPartialDocumentId.size() > 1) {
                    if (bean.getDocumentBINumber() == null || !bean.getDocumentBINumber().equals(documentIdWithoutCheckDigit)) {
                        addWarning(bean, false, "message.warning.input.document.id.not.equals.without.control.digit");
                    }

                    addWarning(bean, false, "message.warning.multiple.people.found.with.same.document.id.without.control.digit");

                    return findPersonByName(withPartialDocumentId, bean);
                }

                // try document id without check digit and without citizen card serial
                final String documentIdWithoutCitizenCardSerial =
                        bean.getDocumentNumber().substring(0, bean.getDocumentNumber().length() - 4);

                final Collection<Person> withPartialDocumentIdWithoutCCSerial =
                        Person.readByDocumentIdNumber(documentIdWithoutCitizenCardSerial);

                if (withPartialDocumentIdWithoutCCSerial.size() == 1) {
                    if (bean.getDocumentBINumber() == null
                            || !bean.getDocumentBINumber().equals(documentIdWithoutCitizenCardSerial)) {
                        addWarning(bean, false, "message.warning.input.document.id.not.equals.without.control.digit.and.serial");
                    }
                    return ensureDocumentIdType(withPartialDocumentIdWithoutCCSerial.iterator().next(), bean);
                }

                if (withPartialDocumentIdWithoutCCSerial.size() > 1) {
                    if (bean.getDocumentBINumber() == null
                            || !bean.getDocumentBINumber().equals(documentIdWithoutCitizenCardSerial)) {
                        addWarning(bean, false, "message.warning.input.document.id.not.equals.without.control.digit.and.serial");
                    }
                    addWarning(bean, false,
                            "message.warning.multiple.people.found.with.same.document.id.without.control.digit.and.serial");
                    return findPersonByName(withPartialDocumentIdWithoutCCSerial, bean);
                }

                // try with student name and student number
                final Collection<Person> studentsWithSameName = Person.findPerson(bean.getStudentName()).stream()
                        .filter(p -> p.getStudent() != null).collect(Collectors.toSet());
                for (Person person : studentsWithSameName) {
                    Registration findRegistration = findRegistration(person.getStudent(), bean, requestYear);
                    if (findRegistration.getNumber().equals(bean.getStudentNumber()) || (bean.getStudentNumber() != null
                            && findRegistration.getStudent().getNumber().intValue() == bean.getStudentNumber().intValue())) {
                        addWarning(bean, false, "message.warning.student.not.found.with.id.but.name.and.number.match");
                        return person;
                    }
                }
            }

            // try with fiscal code and name
            if (StringUtils.isNotBlank(bean.getFiscalCode()) && !StringUtils.equals(bean.getFiscalCode(),
                    FenixEduAcademicConfiguration.getConfiguration().getDefaultSocialSecurityNumber())) {
                final Party party = Person.readByContributorNumber(bean.getFiscalCode());

                if (party != null) {
                    Person person = (Person) party;
                    if (person.getName().equalsIgnoreCase(bean.getStudentName())) {
                        addWarning(bean, false, "message.warning.student.not.found.with.id.but.name.and.social.number.match");
                        return person;
                    }
                }

            }

            return null;
        }

    }

    private boolean hasSameCheckDigitValue(String candidacyIdDocumentNumber, Person person) {
        final String inputDocumentIdCheckDigit = candidacyIdDocumentNumber.substring(candidacyIdDocumentNumber.length() - 1);

        final String personDocumentIdCheckDigit = (person.getIdentificationDocumentSeriesNumber() != null
                && person.getIdentificationDocumentSeriesNumber().length() > 0) ? person.getIdentificationDocumentSeriesNumber()
                        .substring(0, 1) : "";

        return inputDocumentIdCheckDigit.equals(personDocumentIdCheckDigit);
    }

    private Person findPersonByName(Collection<Person> toCheck, AbstractScholarshipStudentBean bean) {

        for (final Person person : toCheck) {
            if (person.getName().equalsIgnoreCase(bean.getStudentName())) {
                return ensureDocumentIdType(person, bean);
            }
        }

        return null;

    }

    private Person ensureDocumentIdType(final Person person, final AbstractScholarshipStudentBean bean) {

        if (person.getIdDocumentType() != ID_DOCUMENT_TYPE_MAPPING.get(bean.getDocumentTypeName())
                && !person.getIdDocumentType().name().equalsIgnoreCase(bean.getDocumentTypeName())) {
            addError(bean, false, "message.error.identity.document.type");
            throw new FillScholarshipException("message.error.identity.document.type");
        }

        return person;

    }

    public void fillBeanWithAcademicInfos(final AbstractScholarshipStudentBean bean, Registration registration,
            ExecutionYear requestYear, boolean firstYearOfCycle) {

        try {

            validateStudentNumber(bean, registration);
            checkPreconditions(bean, registration, requestYear, firstYearOfCycle);

            fillSpecificInfo(bean, registration, requestYear);
            fillCommonInfo(bean, registration, requestYear);

        } catch (FillScholarshipException e) {

        } finally {
            bean.setObservations(formatObservations(bean));
        }
    }

    private void validateStudentNumber(final AbstractScholarshipStudentBean bean, final Registration registration) {
        if (bean.getStudentNumber() != null && registration.getNumber().intValue() != bean.getStudentNumber().intValue()) {
            addWarning(bean, false, "message.warning.input.student.number.does.not.match.with.fenix");
        }
    }

    private void checkPreconditions(AbstractScholarshipStudentBean bean, Registration registration, ExecutionYear requestYear,
            boolean firstYearOfCycle) {

        final RegistrationState lastRegistrationState = registration.getLastRegistrationState(requestYear);
        if (lastRegistrationState != null && !lastRegistrationState.isActive()) {

            //TODO: remove specific test to CANCELED
            if (lastRegistrationState.getType().getCode().equals(REGISTRATION_TYPE_CANCELED)) {
                addWarning(bean, false, "message.warning.registration.is.canceled",
                        lastRegistrationState.getStateDate().toLocalDate().toString("yyyy-MM-dd"));
            } else {
                addWarning(bean, false, "message.warning.registration.is.not.active", requestYear.getQualifiedName());
            }
        }

        if (firstYearOfCycle && !isFirstTimeInCycle(registration, requestYear)) {
            addWarning(bean, false, "message.warning.student.is.not.first.time");
        }

        if (getCycleEnrolmentYears(registration, requestYear).isEmpty()) {
            addWarning(bean, false, "message.warning.registration.without.enrolments");
        }

        if (registration.hasReingression(requestYear)) {
            addWarning(bean, true, "message.warning.found.reingression.for.year");
        }

        if (!SasDataShareAuthorizationServices.isAuthorizationTypeActive()) {
            return;
        }

        if (!SasDataShareAuthorizationServices.isAnswered(registration.getPerson())) {
            addError(bean, false, "message.error.student.has.not.answer.data.sharing.survey");
            throw new FillScholarshipException("message.error.student.has.not.answer.data.sharing.survey");
        } else if (!SasDataShareAuthorizationServices.isDataShareAllowed(registration.getPerson())) {
            addError(bean, false, "message.error.student.does.not.allow.data.sharing");
            throw new FillScholarshipException("message.error.student.does.not.allow.data.sharing");
        }

    }

    static public boolean isFirstTimeInCycle(Registration registration, ExecutionYear requestYear) {
        final List<ExecutionYear> cycleEnrolmentYears = getCycleEnrolmentYears(registration, requestYear);

        return cycleEnrolmentYears.size() > 1 ? false : cycleEnrolmentYears.isEmpty()
                || cycleEnrolmentYears.size() == 1 && cycleEnrolmentYears.iterator().next() == requestYear;
    }

    private void fillCommonInfo(AbstractScholarshipStudentBean bean, Registration registration, ExecutionYear requestYear) {

        // bypass cache
        bean.setCurricularYear(registration.getCurriculum(requestYear).getCurricularYear());

        bean.setGratuityAmount(getTuitionAmount(registration, requestYear));
        bean.setNumberOfMonthsExecutionYear(SocialServicesConfiguration.getInstance().getNumberOfMonthsOfAcademicYear());
        bean.setFirstMonthExecutionYear(getFirstMonthOfExecutionYear(registration, requestYear));

        // the student number (provided by input file) is replaced by the system value
        bean.setStudentNumber(registration.getNumber());

        bean.setRegime(getRegime(bean, registration, requestYear));

        bean.setEnroled(isEnroled(registration, requestYear));

        final LocalDate enrolmentDate = RegistrationServices.getEnrolmentDate(registration, requestYear);
        bean.setEnrolmentDate(enrolmentDate != null ? enrolmentDate : requestYear.getBeginLocalDate());

        bean.setNumberOfEnrolledECTS(getEnroledCredits(registration, requestYear));

        bean.setCetQualificationOwner(isCETQualificationOwner(registration));
        bean.setCtspQualificationOwner(isCTSPQualificationOwner(registration));
        bean.setDegreeQualificationOwner(isDegreeQualificationOwner(registration));
        bean.setMasterQualificationOwner(isMasterQualificationOwner(registration));
        bean.setPhdQualificationOwner(isPhdQualificationOwner(registration));

        // add warning if person is enrolled in a degree that already exists as a completed qualification
        checkIfRegistrationDegreeIsCompleted(bean, registration);

        bean.setCycleIngressionYear(getCycleIngressionYear(bean, registration));
        bean.setCycleNumberOfEnrolmentsYears(getCycleEnrolmentYears(registration, requestYear).size());

        bean.setNumberOfDegreeCurricularYears(getNumberOfDegreeCurricularYears(registration, requestYear));

        bean.setIngressionRegimeCodeWithDescription(Bennu.getInstance().getSasIngressionRegimeMappingsSet().stream()
                .filter(ir -> ir.getIngressionType() == registration.getIngressionType())
                .map(SasIngressionRegimeMapping::getRegimeCodeWithDescription).findFirst().orElse(null));

        bean.setIngressionRegimeCode(Bennu.getInstance().getSasIngressionRegimeMappingsSet().stream()
                .filter(ir -> ir.getIngressionType() == registration.getIngressionType())
                .map(SasIngressionRegimeMapping::getRegimeCode).findFirst().orElse(null));

        if (bean.getIngressionRegimeCode() == null || bean.getIngressionRegimeCodeWithDescription() == null) {
            addError(bean, false, "message.error.ingression.regime.mapping.is.missing",
                    registration.getIngressionType() != null ? registration.getIngressionType()
                            .getLocalizedName() : "empty.ingression.regime");
        }

        if (SocialServicesConfiguration.getInstance().ingressionTypeRequiresExternalData(registration)) {
            addWarning(bean, true, "message.warning.ingression.type.requires.external.data",
                    registration.getIngressionType().getLocalizedName(),
                    RegistrationServices.getCurriculum(registration, requestYear).getSumEctsCredits().toString());
        }

    }

    private Boolean isEnroled(Registration registration, ExecutionYear requestYear) {
        final RegistrationState stateInDate = registration.getStateInDate(new DateTime());
        return stateInDate != null && stateInDate.isActive() && !getEnroledCurriculumLines(registration, requestYear).isEmpty();
    }

    private Integer getNumberOfDegreeCurricularYears(Registration registration, ExecutionYear requestYear) {
        return getStudentCurricularPlan(registration, requestYear).getDegreeCurricularPlan().getDurationInYears();
    }

    private Collection<SchoolLevelType> getPersonSchoolLevelTypes(final Person person) {
        final Set<SchoolLevelType> result = Sets.newHashSet();
        result.addAll(getCompletedQualificationsSchoolLevelTypes(person));
        result.addAll(getCompletedRegistrationSchoolLevelTypes(person.getStudent()));

        return result;

    }

    private Collection<SchoolLevelType> getCompletedRegistrationSchoolLevelTypes(final Student student) {
        final Set<SchoolLevelType> result = Sets.newHashSet();
        for (final Registration registration : student.getRegistrationsSet()) {

            //TODO: find cleaner solution
            if (registration.getDegreeType().isIntegratedMasterDegree()
                    && registration.hasConcludedCycle(CycleType.FIRST_CYCLE)) {
                result.add(SchoolLevelType.DEGREE);
            }

            if (registration.isConcluded() || registration.hasConcluded()) {
                final SchoolLevelTypeMapping schoolLevelTypeMapping = registration.getDegreeType().getSchoolLevelTypeMapping();
                if (schoolLevelTypeMapping != null) {
                    result.add(schoolLevelTypeMapping.getSchoolLevel());
                }
            }

        }

        return result;
    }

    private Collection<SchoolLevelType> getCompletedQualificationsSchoolLevelTypes(final Person person) {
        return person.getStudent().getRegistrationsSet().stream().map(r -> r.getStudentCandidacy()).filter(o -> o != null)
                .map(sc -> sc.getCompletedDegreeInformation().getSchoolLevel()).filter(o -> o != null)
                .collect(Collectors.toSet());
    }

    private BigDecimal getTuitionAmount(Registration registration, ExecutionYear requestYear) {

        final AcademicTreasuryEvent treasuryEvent = (AcademicTreasuryEvent) TreasuryBridgeAPIFactory.implementation()
                .getTuitionForRegistrationTreasuryEvent(registration, requestYear);

        if (treasuryEvent == null) {
            return BigDecimal.ZERO;
        }

        return treasuryEvent.getRegistrationTuitionAmountToPayIncludingOtherTuitionRelatedEmolumentsAndExcludingInterests();
    }

    private Integer getFirstMonthOfExecutionYear(Registration registration, ExecutionYear requestYear) {

        final LocalDate enrolmentDate = RegistrationServices.getEnrolmentDate(registration, requestYear);

        if (enrolmentDate == null || requestYear.getBeginLocalDate().compareTo(enrolmentDate) > 0) {
            return requestYear.getBeginLocalDate().getMonthOfYear();
        }

        return enrolmentDate.getMonthOfYear();

    }

    private String getRegime(AbstractScholarshipStudentBean bean, Registration registration, ExecutionYear requestYear) {

        final boolean partialRegime = registration.getRegimeType(requestYear) == RegistrationRegimeType.PARTIAL_TIME;

        final boolean workingStudent =
                StatuteServices.findStatuteTypes(registration, requestYear).stream().anyMatch(s -> s.isWorkingStudentStatute());

        //TODO: not supported
        final boolean professionalInternship = false;

        if (professionalInternship) {
            return REGIME_PROFESSIONAL_INTERNSHIP;
        }

        if (partialRegime) {

            if (workingStudent) {
                return REGIME_PARTIAL_TIME_WORKING_STUDENT;
            }

            return REGIME_PARTIAL_TIME;
        }

        if (workingStudent) {
            return REGIME_FULL_TIME_WORKING_STUDENT;
        }

        return REGIME_FULL_TIME;

    }

    public Integer getCycleIngressionYear(AbstractScholarshipStudentBean bean, Registration registration) {

        final Registration cycleFirstRegistration = getCycleRegistrations(registration).iterator().next();
        final Integer cycleIngressionYear = cycleFirstRegistration.getStartExecutionYear().getBeginDateYearMonthDay().getYear();

        if (bean.getCycleIngressionYear() != null && !bean.getCycleIngressionYear().equals(cycleIngressionYear)) {
            addWarning(bean, false, "message.warning.input.ingression.date.does.not.match.with.fenix",
                    String.valueOf(bean.getCycleIngressionYear()), String.valueOf(cycleIngressionYear));
        }

        return cycleIngressionYear;
    }

    static protected List<ExecutionYear> getCycleEnrolmentYears(Registration registration, ExecutionYear requestYear) {
        return getCycleRegistrations(registration).stream().flatMap(r -> RegistrationServices.getEnrolmentYears(r).stream())
                .distinct().filter(ey -> ey.isBeforeOrEquals(requestYear)).sorted(ExecutionYear.COMPARATOR_BY_BEGIN_DATE)
                .collect(Collectors.toList());
    }

    static protected List<Registration> getCycleRegistrations(final Registration registration) {
        final Collection<DegreeType> degreeTypesToCheck = DegreeType.all()
                .filter(dt -> dt.getFirstOrderedCycleType() == registration.getDegreeType().getFirstOrderedCycleType())
                .collect(Collectors.toSet());
        final Collection<Registration> degreesToProcess = registration.getStudent().getRegistrationsSet().stream()
                .filter(r -> degreeTypesToCheck.contains(r.getDegreeType())).collect(Collectors.toSet());
        return degreesToProcess.stream()
                .filter(r -> r.getStartExecutionYear().isBeforeOrEquals(registration.getStartExecutionYear()))
                .flatMap(r -> Stream.concat(Stream.of(r), Stream.of(RegistrationServices.getRootRegistration(r)))).distinct()
                .sorted(Registration.COMPARATOR_BY_START_DATE).collect(Collectors.toList());
    }

    public String getMessages(final AbstractScholarshipStudentBean bean, boolean publicMessage) {
        return messages.containsKey(bean) ? messages.get(bean).stream().filter(m -> m.isPublicMessage() == publicMessage)
                .map(m -> m.getMessage()).collect(Collectors.joining("\n")) : null;
    }

    public String formatObservations(final AbstractScholarshipStudentBean bean) {
        if (!messages.containsKey(bean)) {
            return "";
        }

        return messages.get(bean).stream().map(m -> m.getMessage()).collect(Collectors.joining("\n"));
    }

    protected void addError(AbstractScholarshipStudentBean bean, boolean publicMessage, String message, String... args) {
        messages.put(bean, new MessageEntry(publicMessage,
                ERROR_OBSERVATION + ": " + BundleUtil.getString(SasSpringConfiguration.BUNDLE, message, args)));
    }

    protected void addWarning(AbstractScholarshipStudentBean bean, boolean publicMessage, String message, String... args) {
        messages.put(bean, new MessageEntry(publicMessage,
                WARNING_OBSERVATION + ": " + BundleUtil.getString(SasSpringConfiguration.BUNDLE, message, args)));
    }

    private StudentCurricularPlan getStudentCurricularPlan(Registration registration, ExecutionYear executionYear) {
        final StudentCurricularPlan studentCurricularPlan =
                RegistrationServices.getStudentCurricularPlan(registration, executionYear);
        if (studentCurricularPlan == null) {
            throw new FillScholarshipException("message.error.curricular.plan.not.found");
        }

        return studentCurricularPlan;
    }

    protected BigDecimal getEnroledCredits(Registration registration, ExecutionYear executionYear) {
        return sumCredits(getEnroledCurriculumLines(registration, executionYear).stream());
    }

    protected BigDecimal getApprovedCredits(Registration registration, ExecutionYear executionYear) {
        return sumCredits(getEnroledCurriculumLines(registration, executionYear).stream().filter(l -> l.isApproved()));
    }

    private Set<CurriculumLine> getEnroledCurriculumLines(Registration registration, ExecutionYear executionYear) {
        return RegistrationServices.getNormalEnroledCurriculumLines(registration, executionYear, true,
                getDismissalTypesToConsider());
    }

    private Set<CreditsReasonType> getDismissalTypesToConsider() {
        return Bennu.getInstance().getSocialServicesConfiguration().getCreditsReasonTypesSet();
    }

    private BigDecimal sumCredits(final Stream<CurriculumLine> linesStream) {
        return linesStream.map(line -> line.getEctsCreditsForCurriculum()).reduce((x, y) -> x.add(y)).orElse(BigDecimal.ZERO);
    }

    private Boolean isCETQualificationOwner(Registration registration) {
        return getPersonSchoolLevelTypes(registration.getPerson()).stream().filter(SchoolLevelTypeMapping::isCET).findAny()
                .isPresent();
    }

    private Boolean isCTSPQualificationOwner(Registration registration) {
        return getPersonSchoolLevelTypes(registration.getPerson()).stream().filter(SchoolLevelTypeMapping::isCTSP).findAny()
                .isPresent();
    }

    private Boolean isDegreeQualificationOwner(Registration registration) {
        return getPersonSchoolLevelTypes(registration.getPerson()).stream().filter(SchoolLevelTypeMapping::isDegree).findAny()
                .isPresent();
    }

    private Boolean isMasterQualificationOwner(Registration registration) {
        return getPersonSchoolLevelTypes(registration.getPerson()).stream().filter(SchoolLevelTypeMapping::isMasterDegree)
                .findAny().isPresent();
    }

    private Boolean isPhdQualificationOwner(Registration registration) {
        return getPersonSchoolLevelTypes(registration.getPerson()).stream().filter(SchoolLevelTypeMapping::isPhd).findAny()
                .isPresent();
    }

    private void checkIfRegistrationDegreeIsCompleted(AbstractScholarshipStudentBean bean, Registration registration) {
        SchoolLevelTypeMapping schoolLevelTypeMapping = registration.getDegreeType().getSchoolLevelTypeMapping();
        SchoolLevelType schoolLevelType = schoolLevelTypeMapping == null ? null : schoolLevelTypeMapping.getSchoolLevel();
        if (bean.getCetQualificationOwner() && SchoolLevelTypeMapping.isCET(schoolLevelType)) {
            addWarning(bean, false, "message.warning.cet.level");
        }

        if (bean.getCtspQualificationOwner() && SchoolLevelTypeMapping.isCTSP(schoolLevelType)) {
            // check if current registration degree is the same of completed qualification
            addWarning(bean, false, "message.warning.ctsp.level");
        }

        if (bean.getDegreeQualificationOwner() && SchoolLevelTypeMapping.isDegree(schoolLevelType)) {
            // check if current registration degree is the same of completed qualification
            addWarning(bean, false, "message.warning.degree.level");
        }

        if (bean.getMasterQualificationOwner() && SchoolLevelTypeMapping.isMasterDegree(schoolLevelType)) {
            // check if current registration degree is the same of completed qualification
            addWarning(bean, false, "message.warning.master.level");
        }

        if (bean.getPhdQualificationOwner() && SchoolLevelTypeMapping.isPhd(schoolLevelType)) {
            // check if current registration degree is the same of completed qualification
            addWarning(bean, false, "message.warning.phd.level");
        }
    }

    protected void fillSpecificInfo(AbstractScholarshipStudentBean bean, Registration registration, ExecutionYear requestYear) {
        //nothing to be done
    }

    public void addWarningIfRegistrationChangedToInactive(AbstractScholarshipStudentBean bean,
            SasScholarshipCandidacy candidacy) {

        final BigDecimal beforeNumberOfEnrolledECTS = candidacy.getSasScholarshipData().getNumberOfEnrolledECTS();
        final Registration registration = candidacy.getRegistration();

        if (bean.getNumberOfEnrolledECTS().compareTo(beforeNumberOfEnrolledECTS) != 0
                && bean.getNumberOfEnrolledECTS().compareTo(BigDecimal.ZERO) == 0
                && !registration.getActiveStateType().getActive()) {
            addWarning(bean, true, "message.warning.student.registration.state.inactive",
                    registration.getActiveState().getStateDate().toLocalDate().toString("yyyy-MM-dd"));
        }

    }

    public boolean equal(AbstractScholarshipStudentBean bean, Object left, Object right, String fieldName) {
        if (Objects.equal(left, right)) {
            return true;
        }

        addWarning(bean, false, "message.warning.data.has.changed",
                BundleUtil.getString(SasSpringConfiguration.BUNDLE, "label.SasScholarshipData." + fieldName));
        return false;
    }

}