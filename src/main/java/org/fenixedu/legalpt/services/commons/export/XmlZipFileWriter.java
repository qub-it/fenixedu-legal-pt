package org.fenixedu.legalpt.services.commons.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.fenixedu.legalpt.domain.report.LegalReportRequest;
import org.fenixedu.legalpt.domain.report.LegalReportResultFile;
import org.fenixedu.legalpt.domain.report.LegalReportResultFileType;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipModel;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class XmlZipFileWriter {

    public static LegalReportResultFile write(final LegalReportRequest reportRequest, final LegalReportResultFile xmlResultFile,
            final String password) {

        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            parameters.setPassword(password);
            parameters.setFileNameInZip(xmlResultFile.getFilename());
            parameters.setSourceExternalStream(true);

            final ZipOutputStream zOut = new ZipOutputStream(baos, new ZipModel());
            zOut.putNextEntry(null, parameters);
            zOut.write(xmlResultFile.getContent());
            zOut.closeEntry();

            zOut.finish();
            zOut.close();

            return writeToFile(reportRequest, xmlResultFile, baos);

        } catch (ZipException | IOException e) {
            //TODO: improve error handling
            e.printStackTrace();
        }

        return null;

    }

    @Atomic(mode = TxMode.WRITE)
    private static LegalReportResultFile writeToFile(final LegalReportRequest reportRequest,
            final LegalReportResultFile xmlResultFile, final ByteArrayOutputStream baos) {
        return new LegalReportResultFile(reportRequest, LegalReportResultFileType.ZIP,
                xmlResultFile.getFilename().replace(".xml", ".zip"), baos.toByteArray());
    }
}
