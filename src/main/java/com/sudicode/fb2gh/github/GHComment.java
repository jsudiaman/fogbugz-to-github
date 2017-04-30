package com.sudicode.fb2gh.github;

import com.jcabi.github.Comment;
import com.sudicode.fb2gh.FB2GHException;

import java.io.IOException;

/**
 * GitHub issue comment.
 */
public class GHComment {

    private final Comment.Smart comment;

    /**
     * Constructor.
     *
     * @param comment The {@link Comment} instance to access.
     */
    GHComment(final Comment comment) {
        this.comment = new Comment.Smart(comment);
    }

    /**
     * Get this comment's text.
     *
     * @return Text content of this comment.
     * @throws FB2GHException if a GitHub error occurs
     */
    public String getText() throws FB2GHException {
        try {
            return comment.body();
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Set this comment's text.
     *
     * @param text Text content.
     * @throws FB2GHException if a GitHub error occurs
     */
    public void setText(String text) throws FB2GHException {
        try {
            comment.body(text);
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

}
