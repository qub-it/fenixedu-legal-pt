package org.fenixedu.legalpt.services.commons.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.qubit.terra.framework.services.logging.Log;
import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.fenixedu.legalpt.domain.report.LegalReportRequest;
import org.fenixedu.legalpt.domain.report.LegalReportResultFile;
import org.fenixedu.legalpt.domain.report.LegalReportResultFileType;

import net.lingala.zip4j.model.ZipParameters;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class XmlZipFileWriter {

    public static LegalReportResultFile write(final LegalReportRequest reportRequest, final LegalReportResultFile xmlResultFile,
            final String password) {

        final byte[] baos = createEncryptedZip(xmlResultFile.getFilename(), xmlResultFile.getContent(), password);
        return baos == null ? null : writeToFile(reportRequest, xmlResultFile, baos);
    }

    public static byte[] createEncryptedZip(String fileName, byte[] content, String password) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            parameters.setCompressionLevel(CompressionLevel.NORMAL);
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
            parameters.setFileNameInZip(fileName);

            try(ZipOutputStream zipOutputStream = new ZipOutputStream(baos, password.toCharArray())) {
                zipOutputStream.putNextEntry(parameters);
                zipOutputStream.write(content);
            } catch (IOException e) {
                Log.error("Error while creating zip output stream", e);
                return null;
            }
            return baos.toByteArray();
        } catch (IOException e) {
            Log.error("Error while creating byte array output stream", e);
            return null;
        }

    }


    @Atomic(mode = TxMode.WRITE)
    private static LegalReportResultFile writeToFile(final LegalReportRequest reportRequest,
            final LegalReportResultFile xmlResultFile, final byte[] baos) {
        return new LegalReportResultFile(reportRequest, LegalReportResultFileType.ZIP,
                xmlResultFile.getFilename().replace(".xml", ".zip"), baos);
    }
}
