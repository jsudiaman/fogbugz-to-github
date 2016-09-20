package fb2gh.github;

import java.io.IOException;

import com.jcabi.github.Repo;

import fb2gh.FB2GHException;

/**
 * GitHub repository.
 */
public class GHRepo {

    private final Repo repo;

    /**
     * Constructor.
     * 
     * @param repo
     *            The {@link Repo} instance used to access the repository.
     * 
     */
    GHRepo(Repo repo) {
        this.repo = repo;
    }

    /**
     * Create a milestone.
     * 
     * @param milestoneName
     *            The name of the milestone
     * 
     * @throws FB2GHException
     */
    public void putMilestone(String milestoneName) throws FB2GHException {
        try {
            repo.milestones().create(milestoneName);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

}
