package org.fenixedu.legalpt.dto.a3es;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService._UNSUPPORTED;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.i18n;
import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.label;

import java.util.Locale;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.IBean;
import org.fenixedu.commons.i18n.LocalizedString;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;

@SuppressWarnings("deprecation")
public class A3esBeanField implements IBean {

    static final public String CUT = " ...";

    private final static Function<String, String> INVALID_CHARS_CLEANER =
            source -> source.replace("\\u2013", "-").replace("\\u2014", "-").replace("\\u2022", "-").replace("\\u201C", "\\\"")
                    .replace("\\u201D", "\\\"").replace("\\/", "|");

    private String id;
    private String label;
    private Locale locale;
    private String value;
    private Integer limit;
    private String report;
    private String reportType;

    private A3esBeanField() {
    }

    static protected A3esBeanField create(final String id, final String fieldName, final Locale locale,
            final LocalizedString source, final int size) {

        final String value = source == null ? null : source.getContent(locale);
        return create(id, fieldName, locale, value, size);
    }

    static protected A3esBeanField create(final String id, final String fieldName, final String source, final int size) {
        return create(id, fieldName, (Locale) null, source, size);
    }

    static private A3esBeanField create(final String id, final String fieldName, final Locale locale, final String source,
            final int limit) {
        final A3esBeanField result = new A3esBeanField();
        result.setId(id);
        result.setLocale(locale);
        result.setLimit(limit);

        final String language = locale == null ? "" : " (" + locale.getDisplayLanguage() + ")";
        final String labelKey = "label." + fieldName;
        final String label = i18n(labelKey) + language;
        result.setLabel(label);

        String value = StringUtils.isBlank(source) ? null : limit == _UNSUPPORTED ? source : INVALID_CHARS_CLEANER
                .apply(JSONObject.escape(Jsoup.parse(source).text()));

        if (StringUtils.isBlank(value)) {
            if (limit == _UNSUPPORTED) {
                result.addReport(labelFieldUnsupported(), "info");
            } else {
                result.addReport(labelFieldMissing(), "error");
            }

        } else if (limit > 0) {
            final int length = value.length();

            if (length > limit) {
                result.addReport(i18n("label.field.cut", String.valueOf(limit)), "error");
                value = value.substring(0, limit - 4 - (length - value.length())) + CUT;
            } else {
                result.addReport(i18n("label.field.status", String.valueOf(limit - length), String.valueOf(limit)), "-");
            }
        }
        result.setValue(value);

        return result;
    }

    static public String labelFieldMissing() {
        return label("field.missing");
    }

    static private String labelFieldUnsupported() {
        return label("field.unsupported");
    }

    static public String labelFieldCutInfo() {
        return label("field.cut.info");
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    public String getLanguage() {
        return this.locale == null ? null : this.locale.getLanguage();
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(final Integer limit) {
        this.limit = limit;
    }

    public String getReport() {
        return report;
    }

    public void setReport(final String report) {
        this.report = report;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(final String reportType) {
        this.reportType = reportType;
    }

    public void addReport(final String report, final String reportType) {
        setReport(report);
        setReportType(reportType);
    }

}
