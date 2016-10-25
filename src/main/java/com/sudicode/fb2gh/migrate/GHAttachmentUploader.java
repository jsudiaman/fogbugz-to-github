package com.sudicode.fb2gh.migrate;

import com.sudicode.fb2gh.common.FB2GHUtils;
import com.sudicode.fb2gh.common.ObjectBuilder;
import com.sudicode.fb2gh.fogbugz.FBAttachment;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.github.GHRepo;
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
 * <p>
 * This class cannot be instantiated using a traditional constructor. To instantiate, use the builder, like so:
 * </p>
 * <pre>
 * GHAttachmentUploader ghau = new GHAttachmentUploader.Builder(ghUsername, ghPassword, ghRepo) // Required
 *     .webDriver(webDriver) // Optional
 *     .build(); // Returns GHAttachmentUploader
 * </pre>
 *
 * @see <a href=
 * "https://help.github.com/articles/file-attachments-on-issues-and-pull-requests/">File
 * attachments on issues and pull requests</a>
 */
public class GHAttachmentUploader implements FBAttachmentConverter, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(GHAttachmentUploader.class);

    /**
     * The timeout used for blocking operations (downloading, uploading, etc.)
     */
    private static final int TIMEOUT_IN_SECONDS = 100;

    /**
     * File types supported by GitHub.
     */
    private static final String SUPPORTED_FILE_TYPES = "png|gif|jpg|docx|pptx|xlsx|txt|pdf|zip|gz";

    private final WebDriver browser;
    private final FluentWait<WebDriver> wait;

    /**
     * Constructor.
     *
     * @param builder The {@link Builder} to initialize with
     */
    private GHAttachmentUploader(final Builder builder) {
        // Initialize
        this.browser = builder.webDriver != null ? builder.webDriver
                : newWebDriver();
        this.wait = new WebDriverWait(this.browser, TIMEOUT_IN_SECONDS);

        // Log in to GitHub (required to access the issues page)
        browser.get("http://github.com/login/");
        browser.findElement(By.id("login_field")).sendKeys(builder.ghUsername);
        browser.findElement(By.id("password")).sendKeys(builder.ghPassword);
        browser.findElement(By.name("commit")).click();
        wait.until(ExpectedConditions.urlToBe("https://github.com/"));
        browser.get(String.format("https://github.com/%s/%s/issues/new", builder.ghRepo.getOwner(),
                builder.ghRepo.getName()));
        logger.info("Constructed successfully");
    }

    /**
     * Builder used to instantiate {@link GHAttachmentUploader}.
     */
    public static final class Builder implements ObjectBuilder<GHAttachmentUploader> {
        private final String ghUsername;
        private final String ghPassword;
        private final GHRepo ghRepo;
        private WebDriver webDriver;

        /**
         * Constructor. Since GitHub issues cannot be submitted anonymously, valid credentials are required.
         *
         * @param ghUsername GitHub username
         * @param ghPassword GitHub password
         * @param ghRepo     GitHub repository to upload to
         */
        public Builder(final String ghUsername, final String ghPassword, final GHRepo ghRepo) {
            this.ghUsername = ghUsername;
            this.ghPassword = ghPassword;
            this.ghRepo = ghRepo;
        }

        /**
         * @param webDriver The {@link WebDriver} to use
         * @return This object
         */
        public Builder webDriver(final WebDriver webDriver) {
            this.webDriver = webDriver;
            return this;
        }

        @Override
        public GHAttachmentUploader build() {
            return new GHAttachmentUploader(this);
        }
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
            int timeoutInMillis = Math.toIntExact(TimeUnit.SECONDS.toMillis(TIMEOUT_IN_SECONDS));
            FileUtils.copyURLToFile(new URL(fbURL), temp, timeoutInMillis, timeoutInMillis);

            // If file is incompatible, zip it
            if (!extension.toLowerCase().matches(SUPPORTED_FILE_TYPES)) {
                temp = FB2GHUtils.createTempZipFile(temp);
            }

            // Upload to GH Issues
            browser.findElement(By.id("issue_body")).clear();
            browser.findElement(By.cssSelector("input.manual-file-chooser.js-manual-file-chooser"))
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

}
