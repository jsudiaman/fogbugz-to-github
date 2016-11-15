package com.sudicode.fb2gh.github;

import com.jcabi.github.mock.MkGithub;
import com.sudicode.fb2gh.FB2GHException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * An in-memory GitHub repository for testing purposes.
 */
public class OfflineGHRepo implements GHRepo {

    private final GHRepo impl;

    /**
     * Construct a new <code>OfflineGHRepo</code>.
     */
    public OfflineGHRepo() {
        try {
            this.impl = new GHRepoImpl(new MkGithub().randomRepo());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public GHMilestone addMilestone(String title) throws FB2GHException {
        return impl.addMilestone(title);
    }

    @Override
    public List<GHMilestone> getMilestones() {
        return impl.getMilestones();
    }

    @Override
    public GHIssue addIssue(String title, String description) throws FB2GHException {
        return impl.addIssue(title, description);
    }

    @Override
    public GHIssue getIssue(int number) {
        return impl.getIssue(number);
    }

    @Override
    public void addLabel(GHLabel label) throws FB2GHException {
        impl.addLabel(label);
    }

    @Override
    public List<GHLabel> getLabels() throws FB2GHException {
        return impl.getLabels();
    }

    @Override
    public String getOwner() {
        return impl.getOwner();
    }

    @Override
    public String getName() {
        return impl.getName();
    }

}
