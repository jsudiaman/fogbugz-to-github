package com.sudicode.fb2gh.github;

import java.io.IOException;
import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonObject;

import com.jcabi.github.Issue;
import com.sudicode.fb2gh.FB2GHException;

/**
 * Fluent GitHub issue.
 */
public class GHIssue {

    private final Issue.Smart issue;

    /**
     * Constructor.
     * 
     * @param issue
     *            The {@link Issue} instance to access.
     */
    GHIssue(Issue issue) {
        this.issue = new Issue.Smart(issue);
    }

    /**
     * Add label(s) to this issue.
     * 
     * @param labels
     *            The label(s) to add
     * 
     * @return this issue
     * 
     * @throws FB2GHException
     */
    public GHIssue addLabels(String... labels) throws FB2GHException {
        try {
            issue.labels().add(Arrays.asList(labels));
            return this;
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Add a comment.
     * 
     * @param comment
     *            The contents of the comment. Supports Markdown.
     * 
     * @return this issue
     * 
     * @throws FB2GHException
     */
    public GHIssue addComment(String comment) throws FB2GHException {
        try {
            issue.comments().post(comment);
            return this;
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Close this issue.
     * 
     * @return this issue
     * 
     * @throws FB2GHException
     */
    public GHIssue close() throws FB2GHException {
        try {
            issue.close();
            return this;
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Assign this issue to another user.
     * 
     * @param ghUsername
     *            GitHub username
     * 
     * @return this issue
     * 
     * @throws FB2GHException
     */
    public GHIssue assignTo(String ghUsername) throws FB2GHException {
        try {
            issue.assign(ghUsername);
            return this;
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Add this issue to a milestone. <em>NOTE: Only users with push access can
     * set the milestone for issues. The milestone is silently dropped
     * otherwise.</em>
     * 
     * @param milestoneNumber
     *            The <code>number</code> of the milestone to associate this
     *            issue with or <code>null</code> to remove current.
     * 
     * @return this issue
     * 
     * @throws FB2GHException
     */
    public GHIssue setMilestone(Integer milestoneNumber) throws FB2GHException {
        try {
            if (milestoneNumber != null) {
                issue.patch(Json.createObjectBuilder().add("milestone", milestoneNumber).build());
            } else {
                issue.patch(Json.createObjectBuilder().add("milestone", JsonObject.NULL).build());
            }
            return this;
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

}
