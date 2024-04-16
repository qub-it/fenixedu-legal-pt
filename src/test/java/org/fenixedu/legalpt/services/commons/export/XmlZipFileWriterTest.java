package org.fenixedu.legalpt.services.commons.export;

import junit.framework.TestCase;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import org.junit.Test;

import java.io.*;

public class XmlZipFileWriterTest extends TestCase {

    @Test
    public void testCreateEncryptedZip() throws IOException {
        String fileName = "test.txt";
        byte[] content = "This is a test content".getBytes();
        String password = "password";

        byte[] encryptedZip = XmlZipFileWriter.createEncryptedZip(fileName, content, password);
        assertNotNull(encryptedZip);

        // Verify that the zip contains the expected file
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(encryptedZip))) {
            zipInputStream.setPassword(password.toCharArray());
            LocalFileHeader entry = zipInputStream.getNextEntry();
            assertNotNull(entry);
            assertEquals(fileName, entry.getFileName());

            // Verify the content of the file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = zipInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            assertEquals(new String(content), outputStream.toString());
        }
    }

    @Test
    public void testCreateEncryptedZipWithEmptyContent() throws IOException {
        String fileName = "empty.txt";
        byte[] content = new byte[0];
        String password = "password";

        byte[] encryptedZip = XmlZipFileWriter.createEncryptedZip(fileName, content, password);
        assertNotNull(encryptedZip);

        // Verify that the zip contains the expected file
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(encryptedZip))) {
            zipInputStream.setPassword(password.toCharArray());
            LocalFileHeader entry = zipInputStream.getNextEntry();
            assertNotNull(entry);
            assertEquals(fileName, entry.getFileName());

            // Verify the content of the file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = zipInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            assertEquals(0, outputStream.size());
        }
    }
}