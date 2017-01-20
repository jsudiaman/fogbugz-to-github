package com.sudicode.fb2gh.github;

/**
 * Factory used to interact with <a href="https://github.com">GitHub</a>.
 */
public final class GHFactory {

    /**
     * This is a factory class which is not designed for instantiation.
     */
    private GHFactory() {
        throw new AssertionError("Cannot instantiate.");
    }

    /**
     * Construct a new {@link GitHub} without authentication.
     *
     * @return The {@link GitHub}
     */
    public static GitHub newGitHub() {
        return new GitHubImpl();
    }

    /**
     * Construct a new {@link GitHub}, authenticating via OAuth.
     *
     * @param token The OAuth token.
     * @return The {@link GitHub}
     * @see <a href="https://developer.github.com/v3/oauth/">OAuth</a>
     */
    public static GitHub newGitHub(final String token) {
        return new GitHubImpl(token);
    }

    /**
     * Construct a new {@link GitHub}, authenticating via username and password.
     *
     * @param username GitHub username
     * @param password GitHub password
     * @return The {@link GitHub}
     */
    public static GitHub newGitHub(final String username, final String password) {
        return new GitHubImpl(username, password);
    }

}
