package com.sudicode.fb2gh.migrate;

import com.sudicode.fb2gh.common.FB2GHUtils;
import com.sudicode.fb2gh.fogbugz.FBAttachment;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.github.GHRepo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Logger logger = LoggerFactory.getLogger(GHAttachmentUploader.class);
    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 100;
    private static final FBAttachmentConverter DEFAULT_ATTACHMENT_CONVERTER = (fb, attachment) -> attachment.getAbsoluteUrl(fb);

    /**
     * File types supported by GitHub.
     */
    private static final String SUPPORTED_FILE_TYPES = "png|gif|jpg|txt|pdf|zip|gz";

    private final int timeoutInSeconds;
    private final WebDriver webDriver;
    private final FluentWait<WebDriver> wait;
    private final FBAttachmentConverter fallback;

    /**
     * Constructor.
     *
     * @param ghUsername GitHub username
     * @param ghPassword GitHub password
     * @param ghRepo     GitHub repository to upload to
     * @param browser    The {@link Browser} to use
     */
    public GHAttachmentUploader(final String ghUsername, final String ghPassword, final GHRepo ghRepo,
                                final Browser browser) {
        this(ghUsername, ghPassword, ghRepo, browser, DEFAULT_TIMEOUT_IN_SECONDS, DEFAULT_ATTACHMENT_CONVERTER);
    }

    /**
     * Constructor.
     *
     * @param ghUsername       GitHub username
     * @param ghPassword       GitHub password
     * @param ghRepo           GitHub repository to upload to
     * @param browser          The {@link Browser} to use
     * @param timeoutInSeconds The timeout used for blocking operations (downloading, uploading, etc.)
     */
    public GHAttachmentUploader(final String ghUsername, final String ghPassword, final GHRepo ghRepo,
                                final Browser browser, final int timeoutInSeconds) {
        this(ghUsername, ghPassword, ghRepo, browser, timeoutInSeconds, DEFAULT_ATTACHMENT_CONVERTER);
    }

    /**
     * Constructor.
     *
     * @param ghUsername GitHub username
     * @param ghPassword GitHub password
     * @param ghRepo     GitHub repository to upload to
     * @param browser    The {@link Browser} to use
     * @param fallback   The {@link FBAttachmentConverter} to use if uploading to GitHub fails.
     */
    public GHAttachmentUploader(final String ghUsername, final String ghPassword, final GHRepo ghRepo,
                                final Browser browser, final FBAttachmentConverter fallback) {
        this(ghUsername, ghPassword, ghRepo, browser, DEFAULT_TIMEOUT_IN_SECONDS, fallback);
    }

    /**
     * Constructor.
     *
     * @param ghUsername       GitHub username
     * @param ghPassword       GitHub password
     * @param ghRepo           GitHub repository to upload to
     * @param browser          The {@link Browser} to use
     * @param timeoutInSeconds The timeout used for blocking operations (downloading, uploading, etc.)
     * @param fallback         The {@link FBAttachmentConverter} to use if uploading to GitHub fails.
     */
    public GHAttachmentUploader(final String ghUsername, final String ghPassword, final GHRepo ghRepo,
                                final Browser browser, final int timeoutInSeconds, final FBAttachmentConverter fallback) {
        // Initialize
        this.timeoutInSeconds = timeoutInSeconds;
        this.fallback = fallback;
        webDriver = newWebDriver(browser);
        wait = new WebDriverWait(webDriver, timeoutInSeconds);

        // Log in to GitHub (required to access the issues page)
        webDriver.get("http://github.com/login/");
        webDriver.findElement(By.id("login_field")).sendKeys(ghUsername);
        webDriver.findElement(By.id("password")).sendKeys(ghPassword);
        webDriver.findElement(By.name("commit")).click();
        wait.until(webDriver -> "https://github.com/".equals(webDriver.getCurrentUrl()));
        webDriver.get(String.format("https://github.com/%s/%s/issues/new", ghRepo.getOwner(), ghRepo.getName()));
        logger.info("Constructed successfully");
    }

    /**
     * Web browser.
     */
    public enum Browser {
        FIREFOX, CHROME
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
    public String convert(final FogBugz fogBugz, final FBAttachment fbAttachment) {
        try {
            // Download FogBugz attachment
            String filename = fbAttachment.getFilename();
            String fbURL = fbAttachment.getAbsoluteUrl(fogBugz);
            File temp = FB2GHUtils.createTempFile(filename);
            int timeoutInMillis = timeoutInSeconds * 1000;
            FileUtils.copyURLToFile(new URL(fbURL), temp, timeoutInMillis, timeoutInMillis);

            // Upload to GitHub Issues
            return upload(temp);
        } catch (IOException | TimeoutException e) {
            logger.error("Could not convert: " + fbAttachment.getAbsoluteUrl(fogBugz), e);
            return fallback.convert(fogBugz, fbAttachment);
        }
    }

    /**
     * Upload a file to GitHub Issues, zipping if necessary.
     *
     * @param file The file to upload
     * @return URL of the uploaded file
     * @throws IOException if an I/O error occurs
     */
    public String upload(File file) throws IOException {
        String extension = FilenameUtils.getExtension(file.getName());

        // If file is incompatible, zip it
        long tenMB = 10L * 1000000;
        if (file.length() == 0L || file.length() >= tenMB || !extension.toLowerCase().matches(SUPPORTED_FILE_TYPES)) {
            file = FB2GHUtils.createTempZipFile(file);
        }

        // GitHub won't accept files over 25MB
        long twentyFiveMB = 25L * 1000000;
        if (file.length() >= twentyFiveMB) {
            throw new IOException("File '" + file.getAbsolutePath() + "' too large.");
        }

        // Upload to GH Issues
        webDriver.findElement(By.id("issue_body")).clear();
        webDriver.findElement(By.cssSelector("input.manual-file-chooser.js-manual-file-chooser")).sendKeys(file.getAbsolutePath());
        String url = wait.ignoring(StringIndexOutOfBoundsException.class).until(webDriver -> {
            String body = webDriver.findElement(By.id("issue_body")).getAttribute("value");

            // HTML
            if (body.startsWith("<img")) {
                Matcher matcher = Pattern.compile("src=\"(.*?)\"").matcher(body);
                return matcher.find() ? matcher.group(1) : null;
            }

            // Markdown
            body = body.substring(body.lastIndexOf('(') + 1, body.lastIndexOf(')'));
            return body.length() > 0 ? body : null;
        });
        logger.info("Uploaded file '{}' to URL '{}'", file.getAbsolutePath(), url);
        return url;
    }

    /**
     * Quit the WebDriver.
     */
    @Override
    public void close() {
        webDriver.quit();
    }

    /**
     * Construct the {@link WebDriver} instance to be used.
     *
     * @param browser The web browser to use.
     */
    private static WebDriver newWebDriver(final Browser browser) {
        final String driver, os;

        // Determine driver
        switch (browser) {
            case FIREFOX:
                driver = "gecko";
                break;
            case CHROME:
                driver = "chrome";
                break;
            default:
                throw new IllegalArgumentException("Invalid browser: " + browser);
        }

        // Determine os
        if (SystemUtils.IS_OS_WINDOWS) {
            os = "win.exe";
        } else if (SystemUtils.IS_OS_MAC) {
            os = "mac";
        } else if (SystemUtils.IS_OS_LINUX) {
            os = "linux";
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + SystemUtils.OS_NAME);
        }

        // Install driver file into temp directory
        InputStream src = null;
        OutputStream dest = null;
        try {
            // Create temp file
            File tmp = FB2GHUtils.createTempFile(driver + "driver-" + os);

            // Initialize streams
            src = GHAttachmentUploader.class.getResourceAsStream(driver + "driver-" + os);
            dest = new FileOutputStream(tmp);

            // Copy driver to temp file
            IOUtils.copy(src, dest);
            if (!tmp.setExecutable(true)) {
                logger.warn("Failed to set access permissions of file: {}", tmp);
            }

            // Set driver property
            String key = "webdriver." + driver + ".driver";
            String value = URLDecoder.decode(tmp.getAbsolutePath(), "UTF-8");
            System.setProperty(key, value);
            logger.info("System property '{}' was set to '{}'.", key, value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            IOUtils.closeQuietly(src);
            IOUtils.closeQuietly(dest);
        }

        // Return WebDriver
        switch (browser) {
            case FIREFOX:
                return new FirefoxDriver();
            case CHROME:
                return new ChromeDriver();
            default:
                throw new IllegalArgumentException("Invalid browser: " + browser);
        }
    }

}
