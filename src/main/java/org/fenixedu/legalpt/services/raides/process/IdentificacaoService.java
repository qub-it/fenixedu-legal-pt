package org.fenixedu.legalpt.services.raides.process;

import static org.fenixedu.legalpt.domain.raides.Raides.formatArgs;

import java.util.Locale;
import java.util.function.Function;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.mapping.LegalMapping;
import org.fenixedu.legalpt.domain.raides.Raides;
import org.fenixedu.legalpt.domain.raides.Raides.Idade;
import org.fenixedu.legalpt.domain.raides.TblIdentificacao;
import org.fenixedu.legalpt.domain.raides.mapping.LegalMappingType;
import org.fenixedu.legalpt.domain.report.LegalReport;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import com.google.common.base.Strings;

public class IdentificacaoService extends RaidesService {

    public IdentificacaoService(final LegalReport report) {
        super(report);
    }

    public TblIdentificacao create(final Student student, final Registration registration, final ExecutionYear executionYear) {
        TblIdentificacao bean = new TblIdentificacao();

        bean.setIdAluno(registration.getStudent().getNumber());
        bean.setNome(student.getName());

        bean.setNumId(student.getPerson().getDocumentIdNumber());

        if (student.getPerson().getIdDocumentType() != null) {
            bean.setTipoId(LegalMapping.find(report, LegalMappingType.ID_DOCUMENT_TYPE)
                    .translate(student.getPerson().getIdDocumentType()));
        }

        if (Raides.DocumentoIdentificacao.OUTRO.equals(bean.getTipoId())) {
            bean.setTipoIdDescr(student.getPerson().getIdDocumentType().getLocalizedName(Locale.getDefault()));
        }

        if (student.getPerson().getIdDocumentType() == IDDocumentType.IDENTITY_CARD) {
            String digitControlPerson = student.getPerson().getIdentificationDocumentSeriesNumber();
            bean.setCheckDigitId(digitControlPerson);

            if (Strings.isNullOrEmpty(bean.getCheckDigitId())
                    && student.getPerson().getIdDocumentType() == IDDocumentType.IDENTITY_CARD) {
                // Try to generate digitControl from identity card
                try {
                    int digitControl = generatePortugueseIdentityCardControlDigit(student.getPerson().getDocumentIdNumber());
                    bean.setCheckDigitId(String.valueOf(digitControl));

                    LegalReportContext.addWarn("",
                            i18n("warn.Raides.identity.card.digit.control.generated", formatArgs(registration, executionYear)));

                } catch (final NumberFormatException e) {
                    LegalReportContext.addError("", i18n("error.Raides.validation.cannot.generate.digit.control",
                            formatArgs(registration, executionYear)));
                    bean.markAsInvalid();
                }
            }
        }

        if (student.getPerson().getDateOfBirthYearMonthDay() != null) {
            bean.setDataNasc(student.getPerson().getDateOfBirthYearMonthDay().toLocalDate());
        }

        if (student.getPerson().getGender() != null) {
            bean.setSexo(LegalMapping.find(report, LegalMappingType.GENDER).translate(student.getPerson().getGender()));
        }

        preencheNacionalidade(student, bean);

        final Country countryOfResidence = Raides.countryOfResidence(registration, executionYear);
        if (countryOfResidence != null) {
            bean.setResidePais(countryOfResidence.getCode());
        }

        bean.setPaisEnsinoSecundario(countryHighSchool(registration));

        validaPais(bean, student, registration, executionYear);
        validaDocumentoIdentificacao(bean, student, registration, executionYear);
        validaDataNascimento(bean, student, registration, executionYear);

        return bean;
    }

    private static int generatePortugueseIdentityCardControlDigit(final String idDocumentNumber) throws NumberFormatException {

        //force number validation
        Integer.valueOf(idDocumentNumber);

        int mult = 2;
        int controlSum = 0;
        for (int i = 0; i < idDocumentNumber.length(); i++) {
            controlSum += Integer.valueOf(idDocumentNumber.charAt(idDocumentNumber.length() - i - 1)) * mult;

            mult++;
        }

        int result = controlSum % 11;

        int checkDigit;

        if (result < 2) {
            checkDigit = 0;
        } else {
            checkDigit = 11 - result;
        }

        return checkDigit;
    }

    private void preencheNacionalidade(final Student student, final TblIdentificacao bean) {
        final Country firstNationality = student.getPerson().getCountry();
        Country secondNationality = student.getPerson().getSecondNationality();

        if (firstNationality == null && secondNationality == null) {
            return;
        }

        if (firstNationality != null && secondNationality == null) {
            bean.setNacionalidade(firstNationality.getCode());
            bean.setOutroPaisDeNacionalidade(null);
            return;
        }

        if (firstNationality == null && secondNationality != null) {
            bean.setNacionalidade(secondNationality.getCode());
            bean.setOutroPaisDeNacionalidade(null);
            return;
        }

        // The two nationalities are not null

        if (firstNationality != null && firstNationality == secondNationality) {
            bean.setNacionalidade(firstNationality.getCode());
            bean.setOutroPaisDeNacionalidade(null);
            return;
        }

        if (secondNationality.isDefaultCountry()) {
            bean.setNacionalidade(secondNationality.getCode());
            bean.setOutroPaisDeNacionalidade(firstNationality.getCode());
        } else {
            bean.setNacionalidade(firstNationality.getCode());
            bean.setOutroPaisDeNacionalidade(secondNationality.getCode());
        }
    }

    public static Function<Registration, String> COUNTRY_OF_HIGH_SCHOOL_PROVIDER = registration -> {

        final PrecedentDegreeInformation pid = registration.getStudentCandidacy().getCompletedDegreeInformation();

        if (pid != null && pid.getSchoolLevel() == SchoolLevelType.HIGH_SCHOOL_OR_EQUIVALENT && pid.getCountry() != null) {
            return pid.getCountry().getCode();
        }

        if (pid.getCountryHighSchool() != null) {
            return pid.getCountryHighSchool().getCode();
        }

        if (registration.getPerson().getCountry() != null) {
            return registration.getPerson().getCountry().getCode();
        }

        return null;
    };

    static public void setCountryOfHighSchoolProvider(final Function<Registration, String> provider) {
        COUNTRY_OF_HIGH_SCHOOL_PROVIDER = provider;
    }

    protected String countryHighSchool(final Registration registration) {
        return COUNTRY_OF_HIGH_SCHOOL_PROVIDER.apply(registration);
    }

    protected void validaDataNascimento(final TblIdentificacao bean, final Student student, final Registration registration,
            final ExecutionYear executionYear) {
        if (bean.getDataNasc() == null) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.birth.date.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (bean.getDataNasc() != null) {

            LocalDate december31BeginExecYear =
                    new LocalDate(executionYear.getAcademicInterval().getStart().getYear(), DateTimeConstants.DECEMBER, 31);
            long age = Years.yearsBetween(bean.getDataNasc(), december31BeginExecYear).getYears();

            if (age < Idade.MIN || age > Idade.MAX) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.birth.date.invalid", formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            }
        }
    }

    protected void validaDocumentoIdentificacao(final TblIdentificacao bean, final Student student,
            final Registration registration, final ExecutionYear executionYear) {

        if (Strings.isNullOrEmpty(bean.getNumId())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.missing.document.id", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        } else if (bean.getNumId().matches(".*\\s.*")) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.document.id.contains.spaces", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        } else if (IDDocumentType.IDENTITY_CARD == registration.getPerson().getIdDocumentType()
                || IDDocumentType.CITIZEN_CARD == registration.getPerson().getIdDocumentType()) {
            if (!bean.getNumId().matches("\\d+")) {
                LegalReportContext.addError("", i18n("error.Raides.validation.national.document.id.contains.other.than.spaces",
                        formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            }

            if (student.getPerson().getIdDocumentType() == IDDocumentType.IDENTITY_CARD
                    && student.getPerson().getDocumentIdNumber().length() != 8) {

                LegalReportContext.addError("",
                        i18n("error.Raides.validation.document.id.invalid", formatArgs(registration, executionYear)));

                bean.markAsInvalid();

            }
        }

        if (!Strings.isNullOrEmpty(bean.getTipoIdDescr())
                && bean.getTipoIdDescr().equalsIgnoreCase(IDDocumentType.OTHER.getLocalizedName(Locale.getDefault()))) {
            
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.document.id.invalid.other", formatArgs(registration, executionYear)));
            
            bean.markAsInvalid();
        }
    }

    protected void validaPais(final TblIdentificacao bean, final Student student, final Registration registration,
            final ExecutionYear executionYear) {
        if (Strings.isNullOrEmpty(bean.getResidePais())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.country.of.residence.incomplete", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getNacionalidade())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.nationality.incomplete", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getPaisEnsinoSecundario())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.high.school.country.missing", formatArgs(registration, executionYear)));
        }
    }
}
