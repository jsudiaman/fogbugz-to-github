package com.sudicode.fb2gh.github;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.jcabi.github.Label;
import com.jcabi.github.Milestone;
import com.jcabi.github.Repo;
import com.sudicode.fb2gh.FB2GHException;

/**
 * GitHub repository.
 */
public class GHRepo {

    private static final String DEFAULT_LABEL_COLOR = "ffffff";

    private final Repo repo;

    /**
     * Constructor.
     * 
     * @param repo
     *            The {@link Repo} instance used to access the repository.
     */
    GHRepo(Repo repo) {
        this.repo = repo;
    }

    /**
     * Create a milestone.
     * 
     * @param title
     *            The title of the milestone
     * 
     * @return Milestone number
     * 
     * @throws FB2GHException
     */
    public int addMilestone(String title) throws FB2GHException {
        try {
            return repo.milestones().create(title).number();
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
     * @param title
     *            Title of the issue
     * @param description
     *            Description of the issue
     * 
     * @return The created issue
     * 
     * @throws FB2GHException
     */
    public GHIssue addIssue(String title, String description) throws FB2GHException {
        try {
            return new GHIssue(repo.issues().create(title, description));
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Get an issue by number.
     * 
     * @param number
     *            Number of the issue
     * 
     * @return The issue
     */
    public GHIssue getIssue(int number) {
        return new GHIssue(repo.issues().get(number));
    }

    /**
     * Create a label with the default label color.
     * 
     * @param name
     *            The name of the label.
     * 
     * @throws FB2GHException
     */
    public void addLabel(String name) throws FB2GHException {
        addLabel(name, DEFAULT_LABEL_COLOR);
    }

    /**
     * Create a label with a specific label color.
     * 
     * @param name
     *            The name of the label.
     * @param color
     *            The {@link Color} to use.
     * 
     * @throws FB2GHException
     */
    public void addLabel(String name, Color color) throws FB2GHException {
        addLabel(name, String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
    }

    /**
     * Create a label with a specific label color.
     * 
     * @param name
     *            The name of the label.
     * @param hexColor
     *            A 6 character hex code, without the leading #, identifying the
     *            color.
     * 
     * @throws FB2GHException
     */
    public void addLabel(String name, String hexColor) throws FB2GHException {
        try {
            repo.labels().create(name, hexColor);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Get the names of all labels within this repository.
     * 
     * @return A list of the label names.
     */
    public List<String> getLabels() {
        List<String> labels = new ArrayList<>();
        for (Label label : repo.labels().iterate()) {
            labels.add(label.name());
        }
        return labels;
    }

}
