package com.sudicode.fb2gh;

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

    private FB2GHUtils() {
        throw new AssertionError("Cannot instantiate.");
    }

    /**
     * Case-insensitive search for a string in a collection of strings.
     *
     * @param strings      A collection of strings
     * @param searchString The string to search for
     * @return True if <code>searchString</code> is found in
     * <code>strings</code>, regardless of case
     */
    public static boolean containsIgnoreCase(Collection<String> strings, String searchString) {
        return strings.stream().filter(s -> s.equalsIgnoreCase(searchString)).findFirst().isPresent();
    }

    /**
     * Create a temporary file, which will be deleted on exit. Unlike {@link File#createTempFile(String, String)}, the
     * name of the temp file will <strong>not</strong> be randomly generated.
     *
     * @param filename Name of the temporary file. If the file already exists, it will be overwritten.
     *                 If it exists and is a non-empty directory, an {@link IOException} will occur.
     * @return The temporary file
     * @throws IOException If an I/O error occurs
     */
    public static File createTempFile(String filename) throws IOException {
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
     * @throws IOException If an I/O error occurs
     */
    public static File createTempZipFile(File file) throws IOException {
        // Define buffer
        byte[] buff = new byte[1024];

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

}
