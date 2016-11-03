package com.sudicode.fb2gh.github;

import com.jcabi.github.Issue;
import com.sudicode.fb2gh.FB2GHException;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.List;

/**
 * GitHub issue.
 */
public final class GHIssue {

    private final Issue.Smart issue;

    /**
     * Constructor.
     *
     * @param issue The {@link Issue} instance to access.
     */
    GHIssue(final Issue issue) {
        this.issue = new Issue.Smart(issue);
    }

    /**
     * Add labels to this issue.
     *
     * @param labels The labels to add
     * @throws FB2GHException if an I/O error occurs
     */
    public void addLabels(final List<GHLabel> labels) throws FB2GHException {
        try {
            issue.labels().add(() -> labels.stream().map(GHLabel::getName).iterator());
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Add a comment.
     *
     * @param comment The contents of the comment. Supports Markdown.
     * @throws FB2GHException if an I/O error occurs
     */
    public void addComment(final String comment) throws FB2GHException {
        try {
            issue.comments().post(comment);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Close this issue.
     *
     * @throws FB2GHException if an I/O error occurs
     */
    public void close() throws FB2GHException {
        try {
            issue.close();
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Assign this issue to another user.
     *
     * @param ghUsername GitHub username
     * @throws FB2GHException if an I/O error occurs
     */
    public void assignTo(final String ghUsername) throws FB2GHException {
        try {
            issue.assign(ghUsername);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Add this issue to a milestone. <em>NOTE: Only users with push access can
     * set the milestone for issues. The milestone is silently dropped
     * otherwise.</em>
     *
     * @param milestone The {@link GHMilestone} to associate this issue with or <code>null</code> to remove current.
     * @throws FB2GHException if an I/O error occurs
     */
    public void setMilestone(final GHMilestone milestone) throws FB2GHException {
        try {
            if (milestone != null) {
                issue.patch(Json.createObjectBuilder().add("milestone", milestone.getNumber()).build());
            } else {
                issue.patch(Json.createObjectBuilder().add("milestone", JsonObject.NULL).build());
            }
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

}
