package com.sudicode.fb2gh.migrate;

import com.sudicode.fb2gh.fogbugz.FBAttachment;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.github.GHRepo;
import org.joor.Reflect;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GHAttachmentUploader}.
 */
public class GHAttachmentUploaderTest {

    @Test
    public void testConvert() {
        // Dependencies
        GHRepo ghRepo = mock(GHRepo.class);
        when(ghRepo.getOwner()).thenReturn(System.getenv("GH_USER"));
        when(ghRepo.getName()).thenReturn(System.getenv("GH_REPO"));

        FogBugz fogBugz = mock(FogBugz.class);
        FBAttachment fbAttachment = mock(FBAttachment.class);
        when(fbAttachment.getFilename()).thenReturn("Blank.JPG");
        when(fbAttachment.getAbsoluteUrl(any(FogBugz.class))).thenReturn("https://upload.wikimedia.org/wikipedia/en/4/48/Blank.JPG");

        // Convert
        try (GHAttachmentUploader ghau = new GHAttachmentUploader(System.getenv("GH_USER"), System.getenv("GH_PASS"), ghRepo, GHAttachmentUploader.Browser.FIREFOX)) {
            assertThat(ghau.convert(fogBugz, fbAttachment), startsWith("https://cloud.githubusercontent.com/assets/"));
        }
    }

    @Test
    public void testNewFirefoxDriver() {
        WebDriver webDriver = null;
        try {
            webDriver = Reflect.on(GHAttachmentUploader.class).call("newWebDriver", GHAttachmentUploader.Browser.FIREFOX).get();
            assertThat(webDriver, is(instanceOf(FirefoxDriver.class)));
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
    }

}
