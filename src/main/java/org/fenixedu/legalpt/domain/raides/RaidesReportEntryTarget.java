package org.fenixedu.legalpt.domain.raides;

import java.util.LinkedHashMap;
import java.util.Map;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.legalpt.domain.ReportEntryTarget;

public class RaidesReportEntryTarget implements ReportEntryTarget {

    private Registration registration;

    private ExecutionYear executionYear;

    protected RaidesReportEntryTarget() {

    }

    public RaidesReportEntryTarget(Registration registration, ExecutionYear executionYear) {
        this.registration = registration;
        this.executionYear = executionYear;
    }

    @Override
    public Map<String, String> asMap() {
        final Map<String, String> result = new LinkedHashMap<>();

        result.put("Nº de Aluno", registration.getStudent().getNumber().toString());
        result.put("Nº de Matrícula", registration.getNumber().toString());
        result.put("Código de Curso", registration.getDegree().getCode());
        result.put("Curso", registration.getDegree().getPresentationName());
        result.put("Ano", executionYear.getQualifiedName());

        return result;
    }

    private static final RaidesReportEntryTarget EMPTY = new RaidesReportEntryTarget() {
        @Override
        public Map<String, String> asMap() {
            final Map<String, String> result = new LinkedHashMap<>();
            result.put("Nº de Aluno", "");
            result.put("Nº de Matrícula", "");
            result.put("Código de Curso", "");
            result.put("Curso", "");
            result.put("Ano", "");

            return result;

        }
    };

    public static RaidesReportEntryTarget of(Registration registration, ExecutionYear executionYear) {
        return new RaidesReportEntryTarget(registration, executionYear);
    }

    public static RaidesReportEntryTarget empty() {
        return EMPTY;
    }

}
