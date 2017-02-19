package com.sudicode.fb2gh.migrate;

import org.joor.Reflect;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * Unit tests for {@link GHAttachmentUploader}.
 */
public class GHAttachmentUploaderTest {

    private WebDriver webDriver;

    @After
    public void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    @Test
    public void testNewFirefoxDriver() {
        webDriver = Reflect.on(GHAttachmentUploader.class).call("newWebDriver", GHAttachmentUploader.Browser.FIREFOX).get();
        assertThat(webDriver, is(instanceOf(FirefoxDriver.class)));
    }

}
