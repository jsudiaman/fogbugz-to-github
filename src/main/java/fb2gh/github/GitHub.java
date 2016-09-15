package fb2gh.github;

import java.io.IOException;

import com.jcabi.github.Coordinates;
import com.jcabi.github.RtGithub;

import fb2gh.FB2GHException;

/**
 * Class used to interact with <a href="https://github.com">GitHub</a>.
 */
public class GitHub {

    private RtGithub rtGithub;

    /**
     * Constructor which authenticates via OAuth.
     * 
     * @param token
     *            The OAuth token.
     * 
     * @see <a href="https://developer.github.com/v3/oauth/">OAuth</a>
     */
    public GitHub(String token) {
        rtGithub = new RtGithub(token);
    }

    /**
     * Constructor which authenticates via username and password.
     * 
     * @param username
     *            GitHub username
     * @param password
     *            GitHub password
     */
    public GitHub(String username, String password) {
        rtGithub = new RtGithub(username, password);
    }

    /**
     * Create a milestone.
     * 
     * @param user
     *            Repo owner
     * @param repo
     *            Repo name
     * @param milestone
     *            Milestone name
     * 
     * @throws FB2GHException
     */
    public void putMilestoneInRepo(String user, String repo, String milestone) throws FB2GHException {
        try {
            rtGithub.repos().get(new Coordinates.Simple(user, repo)).milestones().create(milestone);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

}
