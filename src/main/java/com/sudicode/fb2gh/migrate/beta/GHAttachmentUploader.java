package com.sudicode.fb2gh.migrate.beta;

import com.sudicode.fb2gh.fogbugz.FBAttachment;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.migrate.FBAttachmentConverter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * Uploads FogBugz attachments to GitHub Issues. Since the GitHub API does not
 * currently support this, Selenium WebDriver is used instead. This
 * implementation is therefore unstable and should be handled as such. But if it
 * works for you, the more power to you.
 * </p>
 *
 * @see <a href=
 * "https://help.github.com/articles/file-attachments-on-issues-and-pull-requests/">File
 * attachments on issues and pull requests</a>
 */
public class GHAttachmentUploader implements FBAttachmentConverter, Closeable {

    /**
     * The timeout used for blocking operations (downloading, uploading, etc.)
     */
    private static final int TIMEOUT_IN_SECONDS = 100;

    /**
     * A "New Issue" page, which is used for uploading purposes. Issues won't actually be posted here.
     */
    private static final String ISSUES_LINK = "https://github.com/sudiamanj/empty-repo/issues/new";

    /**
     * File types supported by GitHub.
     */
    private static final String SUPPORTED_FILE_TYPES = "png|gif|jpg|docx|pptx|xlsx|txt|pdf|zip|gz";

    private final WebDriver browser;
    private final FluentWait<WebDriver> wait;

    /**
     * Construct a new {@link GHAttachmentUploader} using the default
     * {@link WebDriver}. Since GitHub issues cannot be submitted anonymously,
     * valid credentials are required.
     *
     * @param ghUsername GitHub username
     * @param ghPassword GitHub password
     */
    public GHAttachmentUploader(String ghUsername, String ghPassword) {
        this(ghUsername, ghPassword, newWebDriver());
    }

    /**
     * Construct a new {@link GHAttachmentUploader} using a specific
     * {@link WebDriver}.
     *
     * @param ghUsername GitHub username
     * @param ghPassword GitHub password
     * @param webDriver  The {@link WebDriver} to use
     */
    public GHAttachmentUploader(String ghUsername, String ghPassword, WebDriver webDriver) {
        // Initialize
        this.browser = webDriver;
        this.wait = new WebDriverWait(browser, TIMEOUT_IN_SECONDS);

        // Log in to GitHub (required to access the issues page)
        browser.get("http://github.com/login/");
        browser.findElement(By.id("login_field")).sendKeys(ghUsername);
        browser.findElement(By.id("password")).sendKeys(ghPassword);
        browser.findElement(By.name("commit")).click();
        wait.until(ExpectedConditions.urlToBe("https://github.com/"));
        browser.get(ISSUES_LINK);
    }

    /**
     * Download the FogBugz attachment, then reupload it to GitHub Issues. If
     * the file type is incompatible with GitHub Issues, zip it beforehand.
     * Since ZIP files are supported by GitHub Issues, this guarantees that any
     * attachment (within size constraints) will be accepted.
     *
     * @param fogBugz      The {@link FogBugz} instance that owns the
     *                     {@link FBAttachment}
     * @param fbAttachment The {@link FBAttachment}
     * @return URL of the uploaded file
     */
    @Override
    public String convert(FogBugz fogBugz, FBAttachment fbAttachment) {
        try {
            // Download FogBugz attachment
            String filename = fbAttachment.getFilename();
            String extension = FilenameUtils.getExtension(filename);
            String fbURL = fbAttachment.getAbsoluteUrl(fogBugz);
            File temp = createTempFile(filename);
            FileUtils.copyURLToFile(new URL(fbURL), temp, TIMEOUT_IN_SECONDS * 1000, TIMEOUT_IN_SECONDS * 1000);
            temp.deleteOnExit();

            // If file is incompatible, zip it
            if (!extension.toLowerCase().matches(SUPPORTED_FILE_TYPES)) {
                temp = zipFile(temp);
                temp.deleteOnExit();
            }

            // Upload to GH Issues
            browser.findElement(By.id("issue_body")).clear();
            browser.findElement(By.cssSelector("input.manual-file-chooser.js-manual-file-chooser"))
                    .sendKeys(temp.getAbsolutePath());
            return wait.ignoring(StringIndexOutOfBoundsException.class).until(new ExpectedCondition<String>() {
                @Override
                public String apply(WebDriver webDriver) {
                    String body = webDriver.findElement(By.id("issue_body")).getAttribute("value");
                    body = body.substring(body.lastIndexOf('(') + 1, body.lastIndexOf(')'));
                    return body.length() > 0 ? body : null;
                }
            });
        } catch (IOException e) {
            // Checked exceptions are incompatible with the supertype
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Quit the browser.
     */
    @Override
    public void close() {
        browser.quit();
    }

    /**
     * Construct the {@link WebDriver} instance to be used.
     */
    private static WebDriver newWebDriver() {
        String name;
        if (SystemUtils.IS_OS_WINDOWS) {
            name = "geckodriver-win.exe";
        } else if (SystemUtils.IS_OS_MAC) {
            name = "geckodriver-mac";
        } else if (SystemUtils.IS_OS_LINUX) {
            name = "geckodriver-linux";
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + SystemUtils.OS_NAME);
        }

        File geckoDriver = new File(GHAttachmentUploader.class.getResource(name).getFile());
        geckoDriver.setExecutable(true);
        System.setProperty("webdriver.gecko.driver", geckoDriver.getAbsolutePath());
        return new FirefoxDriver();
    }

    /**
     * Compress a single file in ZIP format.
     *
     * @param file The file to compress
     * @return The ZIP file
     * @throws IOException If an I/O error occurs
     */
    private static File zipFile(File file) throws IOException {
        // Define buffer
        byte[] buff = new byte[1024];

        // Create zip file
        File zipFile = createTempFile(file.getName() + ".zip");

        // Output file to zip file
        ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipStream.putNextEntry(zipEntry);
        FileInputStream fileStream = new FileInputStream(file);
        int bytesRead;
        while ((bytesRead = fileStream.read(buff)) > 0) {
            zipStream.write(buff, 0, bytesRead);
        }
        fileStream.close();
        zipStream.closeEntry();
        zipStream.close();

        // Return zip file
        return zipFile;
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
    private static File createTempFile(String filename) throws IOException {
        Path tmp = Paths.get(System.getProperty("java.io.tmpdir"), filename);
        Files.deleteIfExists(tmp);
        return tmp.toFile();
    }

}
