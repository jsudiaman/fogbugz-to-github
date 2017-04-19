package com.sudicode.fb2gh.migrate;

import com.sudicode.fb2gh.fogbugz.FBAttachment;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.github.GHRepo;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assume.assumeThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GHAttachmentUploader}.
 */
public class GHAttachmentUploaderTest {

    private GHAttachmentUploader ghAttachmentUploader;
    private FogBugz fogBugz;

    @Before
    public void setUp() {
        assumeThat(System.getenv("GH_USER"), is(not(nullValue())));
        assumeThat(System.getenv("GH_REPO"), is(not(nullValue())));
        assumeThat(System.getenv("GH_PASS"), is(not(nullValue())));

        // Init ghAttachmentUploader
        GHRepo ghRepo = mock(GHRepo.class);
        when(ghRepo.getOwner()).thenReturn(System.getenv("GH_USER"));
        when(ghRepo.getName()).thenReturn(System.getenv("GH_REPO"));
        ghAttachmentUploader = new GHAttachmentUploader(
                System.getenv("GH_USER"),
                System.getenv("GH_PASS"),
                ghRepo,
                GHAttachmentUploader.Browser.FIREFOX,
                (fb, fbAttachment) -> "Upload failed!"
        );

        // Init fogBugz
        fogBugz = mock(FogBugz.class);
    }

    @After
    public void tearDown() {
        ghAttachmentUploader.close();
    }

    /**
     * The resource to be uploaded. Used as a stub for {@link FBAttachment}.
     *
     * @param name name of the desired resource
     */
    private FBAttachment resource(final String name) {
        String url = getClass().getResource(name).toString();
        FBAttachment fbAttachment = mock(FBAttachment.class);
        when(fbAttachment.getFilename()).thenReturn(FilenameUtils.getName(url));
        when(fbAttachment.getAbsoluteUrl(any(FogBugz.class))).thenReturn(url);
        return fbAttachment;
    }

    @Test
    public void testConvert() {
        assertThat(ghAttachmentUploader.convert(fogBugz, resource("blank.jpg")), startsWith("https://cloud.githubusercontent.com/assets/"));
    }

    @Test
    public void testConvertLarge() {
        assertThat(ghAttachmentUploader.convert(fogBugz, resource("50MB.zip")), is(equalTo("Upload failed!")));
    }

}
