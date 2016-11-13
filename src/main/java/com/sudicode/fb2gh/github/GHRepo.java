package com.sudicode.fb2gh.github;

import com.google.common.collect.ImmutableMap;
import com.jcabi.github.Label;
import com.jcabi.github.Milestone;
import com.jcabi.github.Repo;
import com.sudicode.fb2gh.FB2GHException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GitHub repository.
 */
public class GHRepo {

    /**
     * Hex code of the default label color.
     */
    private static final String DEFAULT_LABEL_COLOR = "ffffff";

    private final Repo.Smart repo;

    /**
     * Constructor.
     *
     * @param repo The {@link Repo} instance used to access the repository.
     */
    GHRepo(final Repo repo) {
        this.repo = new Repo.Smart(repo);
    }

    /**
     * Create a milestone.
     *
     * @param title The title of the milestone
     * @return The milestone
     * @throws FB2GHException if an I/O error occurs
     */
    public GHMilestone addMilestone(final String title) throws FB2GHException {
        try {
            return new GHMilestone(repo.milestones().create(title));
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Get all milestones within this repository.
     *
     * @return A list of the milestones.
     */
    public List<GHMilestone> getMilestones() {
        List<GHMilestone> milestones = new ArrayList<>();
        for (Milestone milestone : repo.milestones().iterate(ImmutableMap.of("state", "all"))) {
            milestones.add(new GHMilestone(milestone));
        }
        return milestones;
    }

    /**
     * Create an issue.
     *
     * @param title       Title of the issue
     * @param description Description of the issue
     * @return The created issue
     * @throws FB2GHException if an I/O error occurs
     */
    public GHIssue addIssue(final String title, final String description) throws FB2GHException {
        try {
            return new GHIssue(repo.issues().create(title, description));
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Get an issue by number.
     *
     * @param number Number of the issue
     * @return The issue
     */
    public GHIssue getIssue(final int number) {
        return new GHIssue(repo.issues().get(number));
    }

    /**
     * Add a label.
     *
     * @param label The label to add.
     * @throws FB2GHException if an I/O error occurs
     */
    public void addLabel(final GHLabel label) throws FB2GHException {
        try {
            repo.labels().create(label.getName(), label.getHexColor());
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Get the labels within this repository.
     *
     * @return A list of the labels.
     * @throws FB2GHException if an I/O error occurs
     */
    public List<GHLabel> getLabels() throws FB2GHException {
        try {
            List<GHLabel> labels = new ArrayList<>();
            for (Label label : repo.labels().iterate()) {
                Label.Smart smartLabel = new Label.Smart(label);
                labels.add(new GHLabel(smartLabel.name(), smartLabel.color()));
            }
            return labels;
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * @return The owner of this repository.
     */
    public String getOwner() {
        return repo.coordinates().user();
    }

    /**
     * @return The name of this repository.
     */
    public String getName() {
        return repo.coordinates().repo();
    }

}
