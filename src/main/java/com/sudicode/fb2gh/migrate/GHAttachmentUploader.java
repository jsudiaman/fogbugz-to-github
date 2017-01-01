package com.sudicode.fb2gh.migrate;

import com.sudicode.fb2gh.common.FB2GHUtils;
import com.sudicode.fb2gh.fogbugz.FBAttachment;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.github.GHRepo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

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

    /**
     * File types supported by GitHub.
     */
    private static final String SUPPORTED_FILE_TYPES = "png|gif|jpg|docx|pptx|xlsx|txt|pdf|zip|gz";

    private final int timeoutInSeconds;
    private final WebDriver webDriver;
    private final FluentWait<WebDriver> wait;

    /**
     * Constructor.
     *
     * @param ghUsername GitHub username
     * @param ghPassword GitHub password
     * @param ghRepo     GitHub repository to upload to
     * @param browser    The {@link Browser} to use
     */
    private GHAttachmentUploader(final String ghUsername, final String ghPassword, final GHRepo ghRepo,
                                 final Browser browser) {
        this(ghUsername, ghPassword, ghRepo, browser, DEFAULT_TIMEOUT_IN_SECONDS);
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
    private GHAttachmentUploader(final String ghUsername, final String ghPassword, final GHRepo ghRepo,
                                 final Browser browser, final int timeoutInSeconds) {
        // Initialize
        this.timeoutInSeconds = timeoutInSeconds;
        webDriver = newWebDriver(browser);
        wait = new WebDriverWait(webDriver, timeoutInSeconds);

        // Log in to GitHub (required to access the issues page)
        webDriver.get("http://github.com/login/");
        webDriver.findElement(By.id("login_field")).sendKeys(ghUsername);
        webDriver.findElement(By.id("password")).sendKeys(ghPassword);
        webDriver.findElement(By.name("commit")).click();
        wait.until(ExpectedConditions.urlToBe("https://github.com/"));
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
            String extension = FilenameUtils.getExtension(filename);
            String fbURL = fbAttachment.getAbsoluteUrl(fogBugz);
            File temp = FB2GHUtils.createTempFile(filename);
            int timeoutInMillis = Math.toIntExact(TimeUnit.SECONDS.toMillis(timeoutInSeconds));
            FileUtils.copyURLToFile(new URL(fbURL), temp, timeoutInMillis, timeoutInMillis);

            // If file is incompatible, zip it
            if (!extension.toLowerCase().matches(SUPPORTED_FILE_TYPES)) {
                temp = FB2GHUtils.createTempZipFile(temp);
            }

            // Upload to GH Issues
            webDriver.findElement(By.id("issue_body")).clear();
            webDriver.findElement(By.cssSelector("input.manual-file-chooser.js-manual-file-chooser"))
                    .sendKeys(temp.getAbsolutePath());
            String url = wait.ignoring(StringIndexOutOfBoundsException.class).until(new ExpectedCondition<String>() {
                @Override
                public String apply(final WebDriver webDriver) {
                    String body = webDriver.findElement(By.id("issue_body")).getAttribute("value");
                    body = body.substring(body.lastIndexOf('(') + 1, body.lastIndexOf(')'));
                    return body.length() > 0 ? body : null;
                }
            });
            logger.info("Uploaded file '{}' to URL '{}'", temp.getAbsolutePath(), url);
            return url;
        } catch (IOException e) {
            // Checked exceptions are incompatible with the supertype
            throw new UncheckedIOException(e);
        }
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

        // Return WebDriver
        File exe = new File(GHAttachmentUploader.class.getResource(driver + "driver-" + os).getFile());
        if (!exe.setExecutable(true)) {
            logger.warn("Failed to set access permissions of file: {}", exe);
        }
        System.setProperty("webdriver." + driver + ".driver", exe.getAbsolutePath());
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
