package fb2gh.github;

import java.io.IOException;

import com.jcabi.github.Coordinates;
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
     * @param gitHub
     *            The {@link GitHub} instance used to access the repository.
     * @param repoOwner
     *            The repository owner.
     * @param repoName
     *            The name of the repository.
     * 
     */
    GHRepo(GitHub gitHub, String repoOwner, String repoName) {
        this.repo = gitHub.getRtGithub().repos().get(new Coordinates.Simple(repoOwner, repoName));
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
