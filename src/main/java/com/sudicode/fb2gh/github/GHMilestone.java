package com.sudicode.fb2gh.github;

import com.jcabi.github.Milestone;
import com.sudicode.fb2gh.FB2GHException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;

/**
 * GitHub milestone.
 */
@EqualsAndHashCode
@ToString(of = "title")
public class GHMilestone {

    private final int number;
    private final String title;

    /**
     * Constructor.
     *
     * @param number Number of the milestone.
     * @param title  Title of the milestone.
     */
    GHMilestone(final int number, final String title) {
        this.number = number;
        this.title = title;
    }

    /**
     * Constructor.
     *
     * @param milestone The {@link Milestone} instance to access.
     * @throws FB2GHException if an I/O error occurs
     */
    GHMilestone(final Milestone milestone) throws FB2GHException {
        try {
            Milestone.Smart smartMilestone = new Milestone.Smart(milestone);
            this.number = smartMilestone.number();
            this.title = smartMilestone.title();
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * @return The number of the milestone.
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return The title of the milestone.
     */
    public String getTitle() {
        return title;
    }

}
