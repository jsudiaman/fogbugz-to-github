package com.sudicode.fb2gh.github;

import com.jcabi.github.Comment;
import com.jcabi.github.Issue;
import com.jcabi.github.Label;
import com.sudicode.fb2gh.FB2GHException;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * GitHub issue.
 */
public class GHIssue {

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
     * Get labels from this issue.
     *
     * @return The labels
     * @throws FB2GHException if a GitHub error occurs
     */
    public List<GHLabel> getLabels() throws FB2GHException {
        try {
            List<GHLabel> list = new ArrayList<>();
            for (Label label : issue.labels().iterate()) {
                Label.Smart smartLabel = new Label.Smart(label);
                list.add(new GHLabel(smartLabel.name(), smartLabel.color()));
            }
            return list;
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Add label to this issue.
     *
     * @param label The label to add
     * @throws FB2GHException if a GitHub error occurs
     */
    public void addLabel(final GHLabel label) throws FB2GHException {
        try {
            issue.labels().add(Collections.singleton(label.getName()));
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Add labels to this issue.
     *
     * @param labels The labels to add
     * @throws FB2GHException if a GitHub error occurs
     */
    public void addLabels(final List<GHLabel> labels) throws FB2GHException {
        try {
            issue.labels().add(() -> labels.stream().map(GHLabel::getName).iterator());
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Get comments.
     *
     * @return All comments on this issue.
     * @throws FB2GHException if a GitHub error occurs
     */
    public List<GHComment> getComments() throws FB2GHException {
        try {
            List<GHComment> list = new ArrayList<>();
            for (Comment comment : issue.comments().iterate(new Date(0))) {
                list.add(new GHComment(comment));
            }
            return list;
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        }
    }

    /**
     * Add a comment.
     *
     * @param comment The contents of the comment. Supports Markdown.
     * @return The added comment.
     * @throws FB2GHException if a GitHub error occurs
     */
    public GHComment addComment(final String comment) throws FB2GHException {
        try {
            return new GHComment(issue.comments().post(comment));
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Close this issue.
     *
     * @throws FB2GHException if a GitHub error occurs
     */
    public void close() throws FB2GHException {
        try {
            issue.close();
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * @return <code>true</code> if the issue is open. <code>false</code> if it
     * is closed
     * @throws FB2GHException if a GitHub error occurs
     */
    public boolean isOpen() throws FB2GHException {
        try {
            return issue.state().equals(Issue.OPEN_STATE);
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * @return <code>true</code> if the issue is closed. <code>false</code> if
     * it is open
     * @throws FB2GHException if a GitHub error occurs
     */
    public boolean isClosed() throws FB2GHException {
        try {
            return issue.state().equals(Issue.CLOSED_STATE);
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Assign this issue to another user.
     *
     * @param ghUsername GitHub username
     * @throws FB2GHException if a GitHub error occurs
     */
    public void assignTo(final String ghUsername) throws FB2GHException {
        try {
            issue.assign(ghUsername);
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Get the milestone of this issue, if one exists.
     *
     * @return An {@link Optional} which contains the milestone if one exists.
     * @throws FB2GHException if a GitHub error occurs
     */
    public Optional<GHMilestone> getMilestone() throws FB2GHException {
        try {
            if (issue.json().isNull("milestone")) {
                return Optional.empty();
            }
            JsonObject milestone = issue.json().getJsonObject("milestone");
            int number = milestone.getInt("number");
            String title = milestone.getString("title");
            return Optional.of(new GHMilestone(number, title));
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
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
     * @throws FB2GHException if a GitHub error occurs
     */
    public void setMilestone(final GHMilestone milestone) throws FB2GHException {
        try {
            if (milestone != null) {
                issue.patch(Json.createObjectBuilder().add("milestone", milestone.getNumber()).build());
            } else {
                issue.patch(Json.createObjectBuilder().add("milestone", JsonObject.NULL).build());
            }
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Get the title of this issue.
     *
     * @return Title of issue
     * @throws FB2GHException if a GitHub error occurs
     */
    public String getTitle() throws FB2GHException {
        try {
            return issue.title();
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Get the body of this issue.
     *
     * @return Body of issue
     * @throws FB2GHException if a GitHub error occurs
     */
    public String getBody() throws FB2GHException {
        try {
            return issue.body();
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Get the ID of this issue.
     *
     * @return ID of issue
     * @throws FB2GHException if a GitHub error occurs
     */
    public int getId() throws FB2GHException {
        try {
            return issue.json().getInt("id");
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

}
