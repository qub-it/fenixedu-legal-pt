package org.fenixedu.legalpt.domain.rebides;

import java.util.LinkedHashMap;
import java.util.Map;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.ReportEntryTarget;

public class RebidesReportEntryTarget implements ReportEntryTarget {

    private Teacher teacher;

    protected RebidesReportEntryTarget() {

    }

    public RebidesReportEntryTarget(final Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public Map<String, String> asMap() {
        final Map<String, String> result = new LinkedHashMap<>();
        result.put("Nome", teacher.getPerson().getName());
        result.put("Nº Doc", teacher.getPerson().getDocumentIdNumber());
        result.put("Username", teacher.getPerson().getUsername());

        return result;
    }

    private static final RebidesReportEntryTarget EMPTY = new RebidesReportEntryTarget() {
        @Override
        public Map<String, String> asMap() {
            final Map<String, String> result = new LinkedHashMap<>();
            result.put("Nome", "");
            result.put("Nº Doc", "");
            result.put("Username", "");

            return result;

        }
    };

    public static RebidesReportEntryTarget of(Teacher teacher) {
        return new RebidesReportEntryTarget(teacher);
    }

    public static RebidesReportEntryTarget empty() {
        return EMPTY;
    }

}
