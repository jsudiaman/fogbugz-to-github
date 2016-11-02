package com.sudicode.fb2gh.fogbugz;

import com.sudicode.fb2gh.FB2GHException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link FogBugz} implementation.
 */
final class FogBugzImpl implements FogBugz {

    private static final Logger logger = LoggerFactory.getLogger(FogBugzImpl.class);

    private final String baseURL;
    private final String authToken;
    private final Unmarshaller jaxb;

    /**
     * Constructor.
     *
     * @param baseURL   The FogBugz URL
     * @param authToken FogBugz API token
     * @throws FB2GHException if there is an API issue.
     * @see <a href="http://help.fogcreek.com/8447/how-to-get-a-fogbugz-xml-api-token">How To Get a FogBugz XML API
     * Token</a>
     */
    FogBugzImpl(final String baseURL, final String authToken) throws FB2GHException {
        try {
            this.jaxb = JAXBContext.newInstance(FBResponse.class).createUnmarshaller();
        } catch (JAXBException e) {
            throw new FB2GHException("Failed to initialize XML parser", e);
        }
        this.baseURL = normalize(baseURL);
        this.authToken = authToken; // TODO Validate the token
    }

    /**
     * Constructor that obtains an <code>authToken</code> from the given email
     * and password.
     *
     * @param baseURL  The FogBugz URL
     * @param email    FogBugz email
     * @param password FogBugz password
     * @throws FB2GHException if there is an API issue.
     */
    FogBugzImpl(final String baseURL, final String email, final String password) throws FB2GHException {
        try {
            this.jaxb = JAXBContext.newInstance(FBResponse.class).createUnmarshaller();
        } catch (JAXBException e) {
            throw new FB2GHException("Failed to initialize XML parser", e);
        }
        this.baseURL = normalize(baseURL);
        this.authToken = parseApiRequest("logon", "email=" + email, "password=" + password).getToken();
        if (this.authToken == null) {
            throw new FB2GHException("Authentication failed.");
        }
        logger.info("Generated API token: {}", this.authToken);
    }

    @Override
    public List<FBMilestone> listMilestones() throws FB2GHException {
        return parseApiRequest("listFixFors").getMilestones();
    }

    @Override
    public List<FBCase> searchCases(final String query) throws FB2GHException {
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new FB2GHException("Caught UnsupportedEncodingException which should NOT happen. "
                    + "Please raise an issue at: https://github.com/sudiamanj/fogbugz-to-github/issues", e);
        }
        String cols = String.join(",", "ixBugParent", "fOpen", "sTitle", "sPersonAssignedTo", "sStatus",
                "ixBugOriginal", "sPriority", "ixFixFor", "sFixFor", "sCategory", "events", "sCase");
        List<FBCase> list = parseApiRequest("search", "q=" + encodedQuery, "cols=" + cols).getCases();
        if (list == null) {
            list = new ArrayList<>();
        }
        logger.info("Search for '{}' returned {} case(s)", query, list.size());
        return list;
    }

    @Override
    public String getBaseURL() {
        return baseURL;
    }

    @Override
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Perform the given API call, then parse the response.
     *
     * @param cmd        The <code>cmd</code> argument
     * @param parameters Additional parameters to include in the query string
     * @return The response.
     * @throws FB2GHException if there is an API issue.
     * @see <a href="http://help.fogcreek.com/the-fogbugz-api">The FogBugz
     * API</a>
     */
    private FBResponse parseApiRequest(final String cmd, final String... parameters) throws FB2GHException {
        // Required
        StringBuilder urlBuilder = new StringBuilder(getBaseURL()).append("/api.asp?cmd=").append(cmd);
        if (!cmd.equals("logon")) {
            urlBuilder.append("&token=").append(getAuthToken());
        }

        // Optional
        for (String param : parameters) {
            urlBuilder.append('&').append(param);
        }

        // Parse XML response
        String url = urlBuilder.toString();
        logger.info("Opening URL: {}", url);
        try {
            return (FBResponse) jaxb.unmarshal(new StreamSource(url));
        } catch (JAXBException e) {
            throw new FB2GHException("Could not parse " + url, e);
        }
    }

    /**
     * Remove trailing "default.asp" (if present) from the given URL. This
     * method might a bit of a misnomer, as it does not perform a fully
     * extensive URL normalization. It does, however, suffice for the purpose of
     * this class.
     *
     * @param baseURL The URL
     * @return The normalized URL
     */
    private static String normalize(String baseURL) {
        while (baseURL.endsWith("/")) {
            baseURL = StringUtils.chop(baseURL);
        }
        if (baseURL.endsWith("default.asp")) {
            baseURL = StringUtils.removeEnd(baseURL, "default.asp");
        }
        return baseURL;
    }

}