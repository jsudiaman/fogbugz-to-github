package fb2gh.github;

import com.jcabi.github.RtGithub;

/**
 * Class used to interact with <a href="https://github.com">GitHub</a>.
 */
public class GitHub {

    private final RtGithub rtGithub;

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
     * Access a repository.
     * 
     * @param repoOwner
     *            The owner of the repository. For example, if accessing the
     *            <code>nodejs/node</code> repository, the owner would be
     *            <code>nodejs</code>.
     * 
     * @param repoName
     *            The name of the repository. For example, if accessing the
     *            <code>nodejs/node</code> repository, the name would be
     *            <code>node</code>.
     */
    public GHRepo getRepo(String repoOwner, String repoName) {
        return new GHRepo(this, repoOwner, repoName);
    }

    /**
     * @return the rtGithub
     */
    RtGithub getRtGithub() {
        return rtGithub;
    }

}
