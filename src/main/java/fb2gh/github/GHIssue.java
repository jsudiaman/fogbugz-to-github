package fb2gh.github;

import java.io.IOException;
import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonObject;

import com.jcabi.github.Issue;

import fb2gh.FB2GHException;

/**
 * GitHub issue.
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
     * @throws FB2GHException
     */
    public void addLabels(String... labels) throws FB2GHException {
        try {
            issue.labels().add(Arrays.asList(labels));
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
     * @throws FB2GHException
     */
    public void addComment(String comment) throws FB2GHException {
        try {
            issue.comments().post(comment);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Close this issue.
     * 
     * @throws FB2GHException
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
     * @param ghUsername
     *            GitHub username
     * 
     * @throws FB2GHException
     */
    public void assignTo(String ghUsername) throws FB2GHException {
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
     * @param milestoneNumber
     *            The <code>number</code> of the milestone to associate this
     *            issue with or <code>null</code> to remove current.
     * 
     * @throws FB2GHException
     */
    public void setMilestone(Integer milestoneNumber) throws FB2GHException {
        try {
            if (milestoneNumber != null) {
                issue.patch(Json.createObjectBuilder().add("milestone", milestoneNumber).build());
            } else {
                issue.patch(Json.createObjectBuilder().add("milestone", JsonObject.NULL).build());
            }
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

}
