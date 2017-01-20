package com.sudicode.fb2gh.github;

import com.sudicode.fb2gh.FB2GHException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;

/**
 * Utility methods for the GitHub API.
 */
final class GHUtils {

    private static final Logger logger = LoggerFactory.getLogger(GHUtils.class);

    /**
     * This is a utility class which is not designed for instantiation.
     */
    private GHUtils() {
        throw new AssertionError("Cannot instantiate.");
    }

    /**
     * When the GitHub API encounters an error, it returns a JSON object which describes it. The GitHub <em>library</em>
     * will in turn throw an {@link AssertionError} with a rather verbose message that contains the JSON object.
     * <p>
     * Passing the {@link AssertionError} to this method does the following:
     * <ul>
     * <li>Logs the JSON object.</li>
     * <li>Throws an {@link FB2GHException} with the error message from GitHub API.</li>
     * </ul>
     * </p>
     * This method will <strong>never</strong> complete normally, but the compiler doesn't know this. Therefore,
     * its return type has been made a {@link RuntimeException} so that it can be invoked using
     * <code>throw GHUtils.rethrow(e)</code>.
     *
     * @param e An {@link AssertionError} <em>from the GitHub library</em>
     * @return Never returns.
     * @throws FB2GHException normally
     */
    static RuntimeException rethrow(final AssertionError e) throws FB2GHException {
        try {
            // Parse e.getMessage() for GitHub's JSON response.
            String error = e.getMessage();
            String response = StringUtils.removeEnd(error.substring(error.lastIndexOf("\n")).trim(), ">");

            // Log it.
            logger.error("GitHub error: {}", response);

            // Parse the JSON and throw FB2GHException.
            JsonObject json = Json.createReader(new StringReader(response)).readObject();
            throw new FB2GHException(json.getString("message"));
        } catch (RuntimeException ex) {
            logger.debug("Could not parse JSON response.", ex);
            throw new FB2GHException("GitHub error.");
        }
    }

}
