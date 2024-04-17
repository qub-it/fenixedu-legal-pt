package org.fenixedu.legalpt.services.commons.export;

import junit.framework.TestCase;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import org.junit.Test;

import java.io.*;

public class XmlZipFileWriterTest extends TestCase {

    private static String FILENAME = "test.txt";
    private static String PASSWORD = "password";
    private static byte[] CONTENT = "This is a test content".getBytes();

    @Test
    public void testCreateEncryptedZipIsDecryptedSuccessfully() throws IOException {
        byte[] encryptedZip = XmlZipFileWriter.createEncryptedZip(FILENAME, CONTENT, PASSWORD);

        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(encryptedZip))) {
            zipInputStream.setPassword(PASSWORD.toCharArray());
            LocalFileHeader entry = zipInputStream.getNextEntry();
            assertNotNull(entry);
            assertEquals(FILENAME, entry.getFileName());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = zipInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            assertEquals(new String(CONTENT), outputStream.toString());
        }
    }

    @Test
    public void testCreateEncryptedZipWithEmptyContentIsDecryptedSuccessfully() throws IOException {
        byte[] content = new byte[0];

        byte[] encryptedZip = XmlZipFileWriter.createEncryptedZip(FILENAME, content, PASSWORD);

        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(encryptedZip))) {
            zipInputStream.setPassword(PASSWORD.toCharArray());
            LocalFileHeader entry = zipInputStream.getNextEntry();
            assertNotNull(entry);
            assertEquals(FILENAME, entry.getFileName());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = zipInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            assertEquals(0, outputStream.size());
        }
    }

    @Test
    public void testCreateEncryptedZipDecryptsWrongPasswordThrowsException() throws IOException {
        String wrongPassword = "wrong_password";

        byte[] encryptedZip = XmlZipFileWriter.createEncryptedZip(FILENAME, CONTENT, PASSWORD);

        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(encryptedZip))) {
            zipInputStream.setPassword(wrongPassword.toCharArray());
            zipInputStream.getNextEntry();
        } catch (ZipException e) {
            assertEquals("Wrong password!", e.getMessage());
        }
    }

    @Test
    public void testCreateEncryptedZipDecryptsWithoutPasswordThrowsException() throws IOException {
        byte[] encryptedZip = XmlZipFileWriter.createEncryptedZip(FILENAME, CONTENT, PASSWORD);

        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(encryptedZip))) {
            zipInputStream.getNextEntry();
        } catch (ZipException e) {
            assertEquals("Wrong password!", e.getMessage());
        }
    }
}