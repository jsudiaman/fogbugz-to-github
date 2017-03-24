package com.sudicode.fb2gh.fogbugz;

import com.sudicode.fb2gh.FB2GHException;

/**
 * <p>
 * This factory uses the <a href="http://help.fogcreek.com/the-fogbugz-api">FogBugz API</a> to interact with a given
 * FogBugz instance. You will need to supply the URL of your FogBugz instance, which will be referenced here as
 * <code>baseURL</code>.
 * </p>
 * If you have an <a href="http://help.fogcreek.com/8447/how-to-get-a-fogbugz-xml-api-token">API token</a>, you can
 * generate {@link FogBugz} instances like so:
 * <pre>
 * FogBugz fb = FBFactory.newFogBugz(baseURL, authToken);
 * </pre>
 * Otherwise, you can alternatively use:
 * <pre>
 * FogBugz fb = FBFactory.newFogBugz(baseURL, email, password);
 * </pre>
 * After instantiating, you may then use <code>fb.getAuthToken();</code> to obtain a valid API token for later.
 */
public class FBFactory {

    /**
     * This class is not designed for instantiation.
     */
    private FBFactory() {
        throw new AssertionError("Cannot instantiate.");
    }

    /**
     * Create a new {@link FogBugz}.
     *
     * @param baseURL   The FogBugz URL
     * @param authToken FogBugz API token
     * @return The {@link FogBugz}
     * @throws FB2GHException if there is an API issue.
     * @see <a href="http://help.fogcreek.com/8447/how-to-get-a-fogbugz-xml-api-token">How To Get a FogBugz XML API
     * Token</a>
     */
    public static FogBugz newFogBugz(final String baseURL, final String authToken) throws FB2GHException {
        return new FogBugzImpl(baseURL, authToken);
    }

    /**
     * Create a new {@link FogBugz} from the given email and password.
     *
     * @param baseURL  The FogBugz URL
     * @param email    FogBugz email
     * @param password FogBugz password
     * @return The {@link FogBugz}
     * @throws FB2GHException if there is an API issue.
     */
    public static FogBugz newFogBugz(final String baseURL, final String email, final String password) throws FB2GHException {
        return new FogBugzImpl(baseURL, email, password);
    }

}
