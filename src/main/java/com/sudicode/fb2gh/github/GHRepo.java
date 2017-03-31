package com.sudicode.fb2gh.github;

import com.sudicode.fb2gh.FB2GHException;

import java.util.List;

/**
 * GitHub repository.
 */
public interface GHRepo {

    /**
     * Create a milestone.
     *
     * @param title The title of the milestone
     * @return The milestone
     * @throws FB2GHException if a GitHub error occurs
     */
    GHMilestone addMilestone(final String title) throws FB2GHException;

    /**
     * Get a single milestone.
     *
     * @param number The number of the milestone
     * @return The milestone
     * @throws FB2GHException if a GitHub error occurs
     */
    GHMilestone getMilestone(int number) throws FB2GHException;

    /**
     * Get all milestones within this repository.
     *
     * @return A list of the milestones.
     * @throws FB2GHException if a GitHub error occurs
     */
    List<GHMilestone> getMilestones() throws FB2GHException;

    /**
     * Create an issue.
     *
     * @param title       Title of the issue
     * @param description Description of the issue
     * @return The created issue
     * @throws FB2GHException if a GitHub error occurs
     */
    GHIssue addIssue(final String title, final String description) throws FB2GHException;

    /**
     * Get an issue by number.
     *
     * @param number Number of the issue
     * @return The issue
     */
    GHIssue getIssue(final int number);

    /**
     * Add a label.
     *
     * @param label The label to add.
     * @throws FB2GHException if a GitHub error occurs
     */
    void addLabel(final GHLabel label) throws FB2GHException;

    /**
     * Get the labels within this repository.
     *
     * @return A list of the labels.
     * @throws FB2GHException if a GitHub error occurs
     */
    List<GHLabel> getLabels() throws FB2GHException;

    /**
     * @return The owner of this repository.
     */
    String getOwner();

    /**
     * @return The name of this repository.
     */
    String getName();

}
