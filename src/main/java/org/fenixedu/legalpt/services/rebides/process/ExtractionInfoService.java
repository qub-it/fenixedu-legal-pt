package org.fenixedu.legalpt.services.rebides.process;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.rebides.RebidesReportEntryTarget;
import org.fenixedu.legalpt.domain.rebides.report.RebidesRequestParameter;
import org.fenixedu.legalpt.domain.report.LegalReportRequest;
import org.fenixedu.legalpt.dto.rebides.ExtractionInfoBean;
import org.joda.time.LocalDate;

public class ExtractionInfoService {

    public ExtractionInfoService() {
    }

    public ExtractionInfoBean getExtractionInfoData(LegalReportRequest reportRequest) {

        ExtractionInfoBean bean = new ExtractionInfoBean();

        RebidesRequestParameter parameters = reportRequest.getParametersAs(RebidesRequestParameter.class);

        fillInstitutionCode(bean, parameters.getInstitutionCode()); // required
        fillMoment(bean, parameters.getMoment()); // required
        fillExtractionDate(bean, new LocalDate()); // required
        fillInterlocutorName(bean, parameters.getInterlocutorName()); // required
        fillInterlocutorEmail(bean, parameters.getInterlocutorEmail()); // required
        fillInterlocutorPhone(bean, parameters.getInterlocutorPhone()); // required

        return bean;
    }

    private void fillInstitutionCode(ExtractionInfoBean bean, String institutionCode) {
        if (StringUtils.isNotEmpty(institutionCode)) {
            if (validateMaxFieldSize("label.RebidesReportUI.institutionCode", institutionCode, RebidesService.SIZE_4CHARS)) {
                bean.setInterlocutorName(institutionCode);
            }
        } else {
            LegalReportContext.addError(RebidesReportEntryTarget.empty(),
                    RebidesService.createMissingFieldMessage("label.RebidesReportUI.institutionCode"));
        }
    }

    private void fillMoment(ExtractionInfoBean bean, String moment) {
        if (StringUtils.isNotEmpty(moment)) {
            bean.setMoment(moment);
        } else {
            LegalReportContext.addError(RebidesReportEntryTarget.empty(),
                    RebidesService.createMissingFieldMessage("label.RebidesReportUI.moment"));
        }
    }

    private void fillExtractionDate(ExtractionInfoBean bean, LocalDate localDate) {
        bean.setExtractionDate(localDate);
    }

    private void fillInterlocutorName(ExtractionInfoBean bean, String interlocutorName) {
        if (StringUtils.isNotEmpty(interlocutorName)) {
            if (validateMaxFieldSize("label.RebidesReportUI.interlocutorName", interlocutorName, RebidesService.LIMIT_80CHARS)) {
                bean.setInterlocutorName(interlocutorName);
            }
        } else {
            LegalReportContext.addError(RebidesReportEntryTarget.empty(),
                    RebidesService.createMissingFieldMessage("label.RebidesReportUI.interlocutorName"));
        }
    }

    private void fillInterlocutorEmail(ExtractionInfoBean bean, String interlocutorEmail) {
        if (StringUtils.isNotEmpty(interlocutorEmail)) {
            if (validateMaxFieldSize("label.RebidesReportUI.interlocutorEmail", interlocutorEmail,
                    RebidesService.LIMIT_50CHARS)) {
                bean.setInterlocutorEmail(interlocutorEmail);
            }
        } else {
            LegalReportContext.addError(RebidesReportEntryTarget.empty(),
                    RebidesService.createMissingFieldMessage("label.RebidesReportUI.interlocutorEmail"));
        }

    }

    private void fillInterlocutorPhone(ExtractionInfoBean bean, String interlocutorPhone) {
        if (StringUtils.isNotEmpty(interlocutorPhone)) {
            if (validateMaxFieldSize("label.RebidesReportUI.interlocutorPhone", interlocutorPhone, RebidesService.SIZE_9CHARS)) {
                bean.setInterlocutorPhone(interlocutorPhone);
            }
        } else {
            LegalReportContext.addError(RebidesReportEntryTarget.empty(),
                    RebidesService.createMissingFieldMessage("label.RebidesReportUI.interlocutorPhone"));
        }
    }

    public static Boolean validateMaxFieldSize(String field, String fieldContent, int maxFieldSize) {
        if (fieldContent.length() > maxFieldSize) {
            final String message = RebidesService.i18n("rebides.fieldSizeOverflow", RebidesService.i18n(field),
                    String.valueOf(fieldContent.length()), String.valueOf(maxFieldSize));
            LegalReportContext.addError(RebidesReportEntryTarget.empty(), message);
            return false;
        }
        return true;
    }

}
