package com.sudicode.fb2gh.github;

import com.jcabi.github.Milestone;
import com.sudicode.fb2gh.FB2GHException;

import java.io.IOException;

/**
 * GitHub milestone.
 */
public class GHMilestone {

    private final Milestone.Smart milestone;

    /**
     * Constructor.
     *
     * @param milestone The {@link Milestone} instance to access.
     */
    GHMilestone(Milestone milestone) {
        this.milestone = new Milestone.Smart(milestone);
    }

    /**
     * @return The number of the milestone.
     */
    public int getNumber() {
        return milestone.number();
    }

    /**
     * @return The title of the milestone.
     * @throws FB2GHException if an I/O error occurs
     */
    public String getTitle() throws FB2GHException {
        try {
            return milestone.title();
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

}
