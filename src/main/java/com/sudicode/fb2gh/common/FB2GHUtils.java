package com.sudicode.fb2gh.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        return strings.stream().anyMatch(s -> s.equalsIgnoreCase(searchString));
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
        try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipStream.putNextEntry(zipEntry);
            try (FileInputStream fileStream = new FileInputStream(file)) {
                int bytesRead;
                while ((bytesRead = fileStream.read(buff)) > 0) {
                    zipStream.write(buff, 0, bytesRead);
                }
            }
            zipStream.closeEntry();
        }

        // Return zip file
        return zipFile;
    }

    /**
     * A "quiet" version of {@link Thread#sleep(long)} which, rather than throwing {@link InterruptedException}, simply
     * restores the <code>interrupted</code> status if interrupted. Also silently ignores invalid <code>millis</code>
     * values which would normally throw {@link IllegalArgumentException}.
     *
     * @param millis The length of time to sleep in milliseconds. Can be negative, in which case the call will be
     *               silently ignored.
     */
    public static void sleepQuietly(final long millis) {
        if (millis <= 0) return;
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Perform no operation.
     */
    public static void nop() {
        // Do nothing.
    }

}
