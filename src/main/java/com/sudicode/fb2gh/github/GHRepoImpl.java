package com.sudicode.fb2gh.github;

import com.google.common.collect.ImmutableMap;
import com.jcabi.github.Label;
import com.jcabi.github.Milestone;
import com.jcabi.github.Repo;
import com.sudicode.fb2gh.FB2GHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link GHRepo} implementation.
 */
class GHRepoImpl implements GHRepo {

    private static final Logger logger = LoggerFactory.getLogger(GHRepoImpl.class);

    private final Repo.Smart repo;

    /**
     * Constructor.
     *
     * @param repo The {@link Repo} instance used to access the repository.
     */
    GHRepoImpl(final Repo repo) {
        this.repo = new Repo.Smart(repo);
    }

    @Override
    public GHMilestone addMilestone(final String title) throws FB2GHException {
        try {
            return new GHMilestone(repo.milestones().create(title));
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    @Override
    public GHMilestone getMilestone(final int number) throws FB2GHException {
        Milestone milestone = repo.milestones().get(number);
        return new GHMilestone(milestone);
    }

    @Override
    public List<GHMilestone> getMilestones() throws FB2GHException {
        try {
            List<GHMilestone> milestones = new ArrayList<>();
            for (Milestone milestone : repo.milestones().iterate(ImmutableMap.of("state", "all"))) {
                try {
                    milestones.add(new GHMilestone(milestone));
                } catch (FB2GHException e) {
                    logger.warn("Couldn't get milestone.", e);
                }
            }
            return milestones;
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        }
    }

    @Override
    public GHIssue addIssue(final String title, final String description) throws FB2GHException {
        try {
            return new GHIssue(repo.issues().create(title, description));
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    @Override
    public GHIssue getIssue(final int number) {
        return new GHIssue(repo.issues().get(number));
    }

    @Override
    public void addLabel(final GHLabel label) throws FB2GHException {
        try {
            repo.labels().create(label.getName(), label.getHexColor());
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    @Override
    public List<GHLabel> getLabels() throws FB2GHException {
        try {
            List<GHLabel> labels = new ArrayList<>();
            for (Label label : repo.labels().iterate()) {
                Label.Smart smartLabel = new Label.Smart(label);
                labels.add(new GHLabel(smartLabel.name(), smartLabel.color()));
            }
            return labels;
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    @Override
    public String getOwner() {
        return repo.coordinates().user();
    }

    @Override
    public String getName() {
        return repo.coordinates().repo();
    }

    @Override
    public String toString() {
        return getOwner() + "/" + getName();
    }

}
