package com.sudicode.fb2gh.fogbugz;

import com.sudicode.fb2gh.FB2GHException;
import lombok.Lombok;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

/**
 * {@link FogBugz} implementation.
 */
class FogBugzImpl implements FogBugz {

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
        this.authToken = authToken;
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
        logger.info("Generated API token: {}", this.authToken);
    }

    @Override
    public List<FBProject> listProjects() throws FB2GHException {
        return parseApiRequest("listProjects").getProjects();
    }

    @Override
    public List<FBMilestone> listMilestones() throws FB2GHException {
        return parseApiRequest("listFixFors").getMilestones();
    }

    @Override
    public List<FBMilestone> listMilestones(final FBProject project) throws FB2GHException {
        return parseApiRequest("listFixFors", "ixProject=" + project.getId()).getMilestones();
    }

    @Override
    public List<FBArea> listAreas() throws FB2GHException {
        return parseApiRequest("listAreas").getAreas();
    }

    @Override
    public List<FBArea> listAreas(final FBProject project) throws FB2GHException {
        return parseApiRequest("listAreas", "ixProject=" + project.getId()).getAreas();
    }

    @Override
    public List<FBCategory> listCategories() throws FB2GHException {
        return parseApiRequest("listCategories").getCategories();
    }

    @Override
    public List<FBStatus> listStatuses() throws FB2GHException {
        return parseApiRequest("listStatuses").getStatuses();
    }

    @Override
    public List<FBStatus> listStatuses(final FBCategory category) throws FB2GHException {
        return parseApiRequest("listStatuses", "ixCategory=" + category.getId()).getStatuses();
    }

    @Override
    public FBCase getCase(final int caseId) throws FB2GHException {
        List<FBCase> caseList = searchCases(String.valueOf(caseId));
        if (caseList.isEmpty()) {
            throw new FB2GHException("Case " + caseId + " not found");
        }
        return caseList.get(0);
    }

    @Override
    public Iterable<FBCase> iterateCases(final int minId, final int maxId) throws FB2GHException {
        return () -> IntStream.rangeClosed(minId, maxId)
                .mapToObj(value -> {
                    try {
                        return searchCases(String.valueOf(value));
                    } catch (FB2GHException e) {
                        // Method throws FB2GHException, so this is safe to do
                        throw Lombok.sneakyThrow(e);
                    }
                })
                .filter(list -> !list.isEmpty())
                .flatMap(Collection::stream)
                .iterator();
    }

    @Override
    public List<FBCase> searchCases(final String query) throws FB2GHException {
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Shouldn't happen
            throw Lombok.sneakyThrow(e);
        }
        String cols = String.join(",", "ixBugParent", "fOpen", "sTitle", "sPersonAssignedTo", "sStatus",
                "ixBugOriginal", "sPriority", "ixFixFor", "sFixFor", "sCategory", "events",
                "plugin_customfields_at_fogcreek_com_scasexxs01", "ixProject", "sProject", "sArea",
                "plugin_customfields_at_fogcreek_com_customery84");
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
        if (!"logon".equals(cmd)) {
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
            FBResponse response = (FBResponse) jaxb.unmarshal(new StreamSource(url));
            if (response.getError() != null) {
                throw new FB2GHException(response.getError());
            }
            return response;
        } catch (JAXBException e) {
            throw new FB2GHException("Could not parse " + url, e);
        }
    }

    /**
     * Remove trailing "default.asp" (if present) from the given URL. This
     * method might be a bit of a misnomer, as it does not perform a fully
     * extensive URL normalization. It does, however, suffice for the purpose of
     * this class.
     *
     * @param baseURL The URL
     * @return The normalized URL
     */
    private static String normalize(final String baseURL) {
        String url = baseURL;
        while (url.endsWith("/")) {
            url = StringUtils.chop(url);
        }
        if (url.endsWith("default.asp")) {
            url = StringUtils.removeEnd(url, "default.asp");
        }
        return url;
    }

}
