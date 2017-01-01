package com.sudicode.fb2gh.github;

import com.jcabi.github.mock.MkGithub;
import com.sudicode.fb2gh.FB2GHException;
import org.joor.Reflect;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * An in-memory GitHub repository for testing purposes.
 */
public class OfflineGHRepo implements GHRepo {

    private final GHRepo impl;
    private final List<GHMilestone> milestones;

    /**
     * Construct a new <code>OfflineGHRepo</code>.
     */
    public OfflineGHRepo() {
        try {
            this.impl = new GHRepoImpl(new MkGithub().randomRepo());
            this.milestones = new ArrayList<>();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public GHMilestone addMilestone(String title) throws FB2GHException {
        GHMilestone mls = new GHMilestone(milestones.size() + 1, title);
        milestones.add(mls);
        return mls;
    }

    @Override
    public List<GHMilestone> getMilestones() {
        return milestones;
    }

    @Override
    public GHIssue addIssue(String title, String description) throws FB2GHException {
        return impl.addIssue(title, description);
    }

    @Override
    public GHIssue getIssue(int number) {
        try {
            // Create spy object
            GHIssue ghIssue = spy(impl.getIssue(number));

            // If milestone != null, fetch it from memory
            doAnswer(call -> {
                String mlsNumber = Reflect.on(ghIssue).field("issue").call("json").call("getString", "milestone").get();
                if (mlsNumber.equals("null")) {
                    return Optional.empty();
                }
                return Optional.of(getMilestones().get(Integer.parseInt(mlsNumber) - 1));
            }).when(ghIssue).getMilestone();

            // Return spy object
            return ghIssue;
        } catch (FB2GHException e) {
            throw new RuntimeException(e);
        }
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
