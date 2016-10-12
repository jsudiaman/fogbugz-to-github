package com.sudicode.fb2gh.github;

/**
 * Entry point for the GitHub API. The provided implementing class is {@link GitHubImpl}.
 */
public interface GitHub {

    /**
     * Access a repository.
     *
     * @param repoOwner The owner of the repository. For example, if accessing the
     *                  <code>twbs/bootstrap</code> repository, the owner would be
     *                  <code>twbs</code>.
     * @param repoName  The name of the repository. For example, if accessing the
     *                  <code>twbs/bootstrap</code> repository, the name would be
     *                  <code>bootstrap</code>.
     * @return The repository.
     */
    GHRepo getRepo(String repoOwner, String repoName);

}
