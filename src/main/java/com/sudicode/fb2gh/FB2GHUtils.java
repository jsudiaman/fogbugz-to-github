package com.sudicode.fb2gh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Static utility methods.
 */
public final class FB2GHUtils {

    private static final Logger logger = LoggerFactory.getLogger(FB2GHUtils.class);

    /**
     * Size (in bytes) to use for file buffers.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * This is a utility class which is not designed for instantiation.
     */
    private FB2GHUtils() {
        throw new AssertionError("Cannot instantiate.");
    }

    /**
     * Case-insensitive search for a string in a collection of strings.
     *
     * @param strings      A collection of strings
     * @param searchString The string to search for
     * @return <code>true</code> if <code>searchString</code> is found in <code>strings</code>, regardless of case
     */
    public static boolean containsIgnoreCase(final Collection<String> strings, final String searchString) {
        return strings.stream().filter(s -> s.equalsIgnoreCase(searchString)).findFirst().isPresent();
    }

    /**
     * Create a temporary file, which will be deleted on exit. Unlike {@link File#createTempFile(String, String)}, the
     * name of the temp file will <strong>not</strong> be randomly generated.
     *
     * @param filename Name of the temporary file. If the file already exists, it will be overwritten.
     *                 If it exists and is a non-empty directory, an {@link IOException} will occur.
     * @return The temporary file
     * @throws IOException if an I/O error occurs
     */
    public static File createTempFile(final String filename) throws IOException {
        Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), filename);
        Files.deleteIfExists(tempPath);
        File tempFile = tempPath.toFile();
        tempFile.deleteOnExit();
        logger.info("Created temporary file: '{}'. Will delete on exit.", tempFile.getAbsolutePath());
        return tempFile;
    }

    /**
     * Compress a single file in ZIP format.
     *
     * @param file The file to compress
     * @return The ZIP file, which will be deleted on exit
     * @throws IOException if an I/O error occurs
     */
    public static File createTempZipFile(final File file) throws IOException {
        // Define buffer
        byte[] buff = new byte[BUFFER_SIZE];

        // Create zip file
        File zipFile = createTempFile(file.getName() + ".zip");

        // Output file to zip file
        ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipStream.putNextEntry(zipEntry);
        try (FileInputStream fileStream = new FileInputStream(file)) {
            int bytesRead;
            while ((bytesRead = fileStream.read(buff)) > 0) {
                zipStream.write(buff, 0, bytesRead);
            }
        }
        zipStream.closeEntry();
        zipStream.close();

        // Return zip file
        return zipFile;
    }

    /**
     * <p>
     * Trust invalid security certificates when browsing using HTTPS. <strong>Not</strong> recommended for production
     * code.
     * </p>
     * <p>
     * If you're using this to resolve {@link javax.net.ssl.SSLHandshakeException}, this might be a better idea:
     * <a href="http://stackoverflow.com/a/6742204/6268626">How to solve javax.net.ssl.SSLHandshakeException Error?</a>
     * </p>
     *
     * @throws FB2GHException if the trust manager could not be disabled
     */
    public static void trustInvalidCertificates() throws FB2GHException {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] tm = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, tm, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new FB2GHException(e);
        }
    }

}
