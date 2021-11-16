package org.fenixedu.legalpt.domain.rebides;

import java.util.Set;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.mapping.ILegalMappingType;
import org.fenixedu.legalpt.domain.rebides.mapping.RebidesMappingType;
import org.fenixedu.legalpt.domain.rebides.report.RebidesRequestParameter;
import org.fenixedu.legalpt.domain.report.LegalReportRequest;
import org.fenixedu.legalpt.domain.report.LegalReportResultFile;
import org.fenixedu.legalpt.dto.rebides.RebidesBean;
import org.fenixedu.legalpt.services.commons.export.XmlZipFileWriter;
import org.fenixedu.legalpt.services.rebides.export.RebidesXlsExporter;
import org.fenixedu.legalpt.services.rebides.export.RebidesXmlToBaseFileWriter;
import org.fenixedu.legalpt.services.rebides.process.RebidesService;
import org.fenixedu.legalpt.services.report.log.XlsExporterLog;
import org.fenixedu.legalpt.util.LegalPTUtil;

import com.google.common.collect.Sets;

public class RebidesInstance extends RebidesInstance_Base {

    public RebidesInstance() {
        super();
    }

    @Override
    public LocalizedString getNameI18N() {
        return LegalPTUtil.bundleI18N("title." + RebidesInstance.class.getName());
    }

    @Override
    public Set<ILegalMappingType> getMappingTypes() {
        return Sets.<ILegalMappingType> newHashSet(RebidesMappingType.values());
    }

    @Override
    public Set<?> getPossibleKeys(String type) {
        return RebidesMappingType.valueOf(type).getValues();
    }

    @Override
    public LocalizedString getMappingTypeNameI18N(String type) {
        return RebidesMappingType.valueOf(type).getName();
    }

    @Override
    public LocalizedString getLocalizedNameMappingKey(String type, String key) {
        return RebidesMappingType.valueOf(type).getLocalizedNameKey(key);
    }

    public static RebidesInstance getInstance() {
        return find(RebidesInstance.class);
    }

    @Override
    public void executeProcessing(LegalReportRequest reportRequest) {
        RebidesService service = new RebidesService(reportRequest);

        final RebidesRequestParameter parameter = reportRequest.getParametersAs(RebidesRequestParameter.class);
        final RebidesBean rebidesBean = service.process(parameter.getExecutionYear());

        // XLS
        RebidesXlsExporter.write(reportRequest, rebidesBean);

        // XML
        LegalReportResultFile xml = RebidesXmlToBaseFileWriter.write(reportRequest, rebidesBean);

        // log
        XlsExporterLog.write(reportRequest, LegalReportContext.getReport());

        // XML password protected zip
        XmlZipFileWriter.write(reportRequest, xml, getPasswordToZip());

        reportRequest.markAsProcessed();
    }

}
