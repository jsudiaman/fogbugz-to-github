package com.sudicode.fb2gh.github;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for {@link GHComment}.
 */
public class GHCommentTest {

    private static String COMMENT_TEXT = "Issue comment.";

    private GHComment ghComment;

    @Before
    public void setUp() throws Exception {
        GHRepo ghRepo = new OfflineGHRepo();
        GHIssue ghIssue = ghRepo.addIssue("Issue title.", "Issue description.");
        ghComment = ghIssue.addComment(COMMENT_TEXT);
    }

    @Test
    public void getText() throws Exception {
        assertThat(ghComment.getText(), is(equalTo(COMMENT_TEXT)));
    }

    @Test
    public void setText() throws Exception {
        String newText = "Edited issue comment.";
        ghComment.setText(newText);
        assertThat(ghComment.getText(), is(equalTo(newText)));
    }

}