package com.sudicode.fb2gh.github;

import com.jcabi.github.Coordinates;
import com.jcabi.github.RtGithub;
import com.jcabi.github.wire.CarefulWire;

/**
 * Class used to interact with <a href="https://github.com">GitHub</a>.
 */
public final class GitHubImpl implements GitHub {

    private final RtGithub connector;

    /**
     * Constructor which authenticates via OAuth.
     *
     * @param token The OAuth token.
     * @see <a href="https://developer.github.com/v3/oauth/">OAuth</a>
     */
    public GitHubImpl(final String token) {
        connector = new RtGithub(new RtGithub(token).entry().through(CarefulWire.class, 50));
    }

    /**
     * Constructor which authenticates via username and password.
     *
     * @param username GitHub username
     * @param password GitHub password
     */
    public GitHubImpl(final String username, final String password) {
        connector = new RtGithub(new RtGithub(username, password).entry().through(CarefulWire.class, 50));
    }

    @Override
    public GHRepo getRepo(final String repoOwner, final String repoName) {
        return new GHRepo(connector.repos().get(new Coordinates.Simple(repoOwner, repoName)));
    }

}
