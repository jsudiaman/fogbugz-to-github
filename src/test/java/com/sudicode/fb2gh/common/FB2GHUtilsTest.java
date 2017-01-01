package com.sudicode.fb2gh.common;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipInputStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class FB2GHUtilsTest {

    @Test
    public void testConstructor() throws Exception {
        Constructor constructor = FB2GHUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail();
        } catch (InvocationTargetException expected) {
        }
    }

    @Test
    public void testCreateTempFile() throws Exception {
        File tempFile = FB2GHUtils.createTempFile("temp");
        assertThat(tempFile.toPath().getParent(), is(equalTo(Paths.get(System.getProperty("java.io.tmpdir")))));
        assertThat(tempFile.getName(), is(equalTo("temp")));
    }

    @Test
    public void testCreateTempZipFile() throws Exception {
        // Create file with random bytes
        byte[] randomBytes = RandomUtils.nextBytes(20);
        File tempFile = File.createTempFile("temp", null);
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rwd")) {
            raf.write(randomBytes);
        }

        // Zip & Unzip, then verify file integrity
        File zipFile = FB2GHUtils.createTempZipFile(tempFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            zis.getNextEntry();
            int bytesRead;
            while ((bytesRead = zis.read(buffer)) > 0) {
                baos.write(buffer, 0, bytesRead);
            }
        }
        assertArrayEquals(randomBytes, baos.toByteArray());
    }

    @Test
    public void testTrustInvalidCertificates() throws Exception {
        FB2GHUtils.trustInvalidCertificates();
        InputStream is = null;
        try {
            is = new URL("https://self-signed.badssl.com/").openStream();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Test
    public void testSleepQuietly() throws Exception {
        // Should be ignored
        FB2GHUtils.sleepQuietly(-1);

        // Interrupting should work
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(() -> FB2GHUtils.sleepQuietly(10 * 1000));
        es.shutdownNow();
        assertTrue(es.awaitTermination(5, SECONDS));
    }

}