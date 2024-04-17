package org.fenixedu.legalpt.services.commons.export;

import junit.framework.TestCase;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import org.junit.Test;

import java.io.*;

public class XmlZipFileWriterTest extends TestCase {

    @Test
    public void testCreateEncryptedZipIsDecryptedSuccessfully() throws IOException {
        String fileName = "test.txt";
        byte[] content = "This is a test content".getBytes();
        String password = "password";

        byte[] encryptedZip = XmlZipFileWriter.createEncryptedZip(fileName, content, password);
        assertNotNull(encryptedZip);

        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(encryptedZip))) {
            zipInputStream.setPassword(password.toCharArray());
            LocalFileHeader entry = zipInputStream.getNextEntry();
            assertNotNull(entry);
            assertEquals(fileName, entry.getFileName());

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
    public void testCreateEncryptedZipWithEmptyContentIsDecryptedSuccessfully() throws IOException {
        String fileName = "empty.txt";
        byte[] content = new byte[0];
        String password = "password";

        byte[] encryptedZip = XmlZipFileWriter.createEncryptedZip(fileName, content, password);
        assertNotNull(encryptedZip);

        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(encryptedZip))) {
            zipInputStream.setPassword(password.toCharArray());
            LocalFileHeader entry = zipInputStream.getNextEntry();
            assertNotNull(entry);
            assertEquals(fileName, entry.getFileName());

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
        String fileName = "test.txt";
        byte[] content = "This is a test content".getBytes();
        String correctPassword = "password";
        String wrongPassword = "wrong_password";

        byte[] encryptedZip = XmlZipFileWriter.createEncryptedZip(fileName, content, correctPassword);
        assertNotNull(encryptedZip);

        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(encryptedZip))) {
            zipInputStream.setPassword(wrongPassword.toCharArray());
            zipInputStream.getNextEntry();
        } catch (ZipException e) {
            assertEquals("Wrong password!", e.getMessage());
        }
    }

    @Test
    public void testCreateEncryptedZipDecryptsWithoutPasswordThrowsException() throws IOException {
        String fileName = "test.txt";
        byte[] content = "This is a test content".getBytes();
        String password = "password";

        byte[] encryptedZip = XmlZipFileWriter.createEncryptedZip(fileName, content, password);
        assertNotNull(encryptedZip);

        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(encryptedZip))) {
            zipInputStream.getNextEntry();
        } catch (ZipException e) {
            assertEquals("Wrong password!", e.getMessage());
        }
    }
}