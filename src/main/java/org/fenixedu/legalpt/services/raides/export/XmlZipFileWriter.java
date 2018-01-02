package org.fenixedu.legalpt.services.raides.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesRequestParameter;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequest;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFile;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFileType;

import com.google.common.base.Strings;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipModel;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class XmlZipFileWriter {

    public static LegalReportResultFile write(final LegalReportRequest reportRequest,
            final RaidesRequestParameter raidesRequestParameter, final Raides raides, final LegalReportResultFile xmlResultFile) {

        final String passwordToZip = ((RaidesInstance) reportRequest.getLegalReport()).getPasswordToZip();
        if (Strings.isNullOrEmpty(passwordToZip)) {
            return null;
        }

        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            parameters.setPassword(passwordToZip);
            parameters.setFileNameInZip(xmlResultFile.getFilename());
            parameters.setSourceExternalStream(true);

            final ZipOutputStream zOut = new ZipOutputStream(baos, new ZipModel());
            zOut.putNextEntry(null, parameters);
            zOut.write(xmlResultFile.getContent());
            zOut.closeEntry();

            zOut.finish();
            zOut.close();

            return new LegalReportResultFile(reportRequest, LegalReportResultFileType.ZIP,
                    xmlResultFile.getFilename().replace(".xml", ".zip"), baos.toByteArray());

        } catch (ZipException | IOException e) {
            //TODO: improve error handling
            e.printStackTrace();
        }

        return null;

    }
}
