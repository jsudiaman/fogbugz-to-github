package com.sudicode.fb2gh.github;

import com.jcabi.github.Coordinates;
import com.jcabi.github.RtGithub;
import com.jcabi.http.wire.RetryWire;

/**
 * {@link GitHub} implementation.
 */
class GitHubImpl implements GitHub {

    private final RtGithub connector;

    /**
     * Constructor which does not authenticate.
     */
    GitHubImpl() {
        connector = new RtGithub(new RtGithub().entry().through(RetryWire.class));
    }

    /**
     * Constructor which authenticates via OAuth.
     *
     * @param token The OAuth token.
     * @see <a href="https://developer.github.com/v3/oauth/">OAuth</a>
     */
    GitHubImpl(final String token) {
        connector = new RtGithub(new RtGithub(token).entry().through(RetryWire.class));
    }

    /**
     * Constructor which authenticates via username and password.
     *
     * @param username GitHub username
     * @param password GitHub password
     */
    GitHubImpl(final String username, final String password) {
        connector = new RtGithub(new RtGithub(username, password).entry().through(RetryWire.class));
    }

    @Override
    public GHRepo getRepo(final String repoOwner, final String repoName) {
        return new GHRepoImpl(connector.repos().get(new Coordinates.Simple(repoOwner, repoName)));
    }

}
