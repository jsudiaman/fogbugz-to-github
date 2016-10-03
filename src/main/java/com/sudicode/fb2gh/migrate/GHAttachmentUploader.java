package com.sudicode.fb2gh.migrate;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;

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

import com.google.common.annotations.Beta;
import com.sudicode.fb2gh.FB2GHException;
import com.sudicode.fb2gh.fogbugz.FBAttachment;

/**
 * <p>
 * Uploads FogBugz attachments to GitHub Issues. Since the GitHub API does not
 * currently support this, Selenium WebDriver is used instead. This
 * implementation is therefore unstable and should be handled as such. But if it
 * works for you, the more power to you.
 * </p>
 * 
 * @see <a href=
 *      "https://help.github.com/articles/file-attachments-on-issues-and-pull-requests/">File
 *      attachments on issues and pull requests</a>
 */
@Beta
public class GHAttachmentUploader implements FBAttachmentConverter, Closeable {

    /** TODO Customizable timeout */
    private static final int TIMEOUT_IN_SECONDS = 100;

    private final WebDriver browser;
    private final FluentWait<WebDriver> wait;

    /**
     * Construct a new {@link GHAttachmentUploader} using the default
     * {@link WebDriver}. Since GitHub issues cannot be submitted anonymously,
     * valid credentials are required.
     * 
     * @param ghUsername
     *            GitHub username
     * @param ghPassword
     *            GitHub password
     */
    public GHAttachmentUploader(String ghUsername, String ghPassword) {
        this(ghUsername, ghPassword, newWebDriver());
    }

    /**
     * Construct a new {@link GHAttachmentUploader} using a specific
     * {@link WebDriver}.
     * 
     * @param ghUsername
     *            GitHub username
     * @param ghPassword
     *            GitHub password
     * @param webDriver
     *            The {@link WebDriver} to use
     */
    public GHAttachmentUploader(String ghUsername, String ghPassword, WebDriver webDriver) {
        // Initialize
        browser = (WebDriver) webDriver;
        wait = new WebDriverWait(browser, TIMEOUT_IN_SECONDS);

        // Log in to GitHub (required to access the issues page)
        browser.get("http://github.com/login/");
        browser.findElement(By.id("login_field")).sendKeys(ghUsername);
        browser.findElement(By.id("password")).sendKeys(ghPassword);
        browser.findElement(By.name("commit")).click();
        wait.until(ExpectedConditions.urlToBe("https://github.com/"));
        browser.get("https://github.com/sudiamanj/empty-repo/issues/new");
    }

    /**
     * Download the FogBugz attachment, then reupload it to GitHub Issues. If
     * the file type is incompatible with GitHub Issues, zip it beforehand. (ZIP
     * files are supported.)
     * 
     * @return URL of the uploaded file
     */
    @Override
    public String convert(FBAttachment fbAttachment) throws FB2GHException {
        try {
            // Download FogBugz attachment
            String filename = fbAttachment.getFilename();
            String extension = FilenameUtils.getExtension(filename);
            String fbURL = fbAttachment.getUrl();
            File temp = File.createTempFile(filename, "." + extension);
            FileUtils.copyURLToFile(new URL(fbURL), temp, TIMEOUT_IN_SECONDS * 1000, TIMEOUT_IN_SECONDS * 1000);
            temp.deleteOnExit();

            // TODO If file is incompatible, zip it

            // Upload to GH Issues
            browser.findElement(By.id("issue_body")).clear();
            browser.findElement(By.cssSelector("input.manual-file-chooser.js-manual-file-chooser"))
                    .sendKeys(temp.getAbsolutePath());
            String ghURL = wait.ignoring(StringIndexOutOfBoundsException.class).until(new ExpectedCondition<String>() {
                @Override
                public String apply(WebDriver webDriver) {
                    String body = webDriver.findElement(By.id("issue_body")).getAttribute("value");
                    body = body.substring(body.lastIndexOf('(') + 1, body.lastIndexOf(')'));
                    return body.length() > 0 ? body : null;
                }
            });
            return ghURL;
        } catch (IOException e) {
            throw new FB2GHException(e);
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
