package com.sudicode.fb2gh.fogbugz;

import com.sudicode.fb2gh.FB2GHException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * <p>
 * This class uses the
 * <a href="http://help.fogcreek.com/the-fogbugz-api">FogBugz API</a> to
 * interact with the given FogBugz instance. You will need to supply the URL of
 * your FogBugz instance, which will be referenced here as <code>baseURL</code>.
 * </p>
 * <p>
 * If you have an <a href=
 * "http://help.fogcreek.com/8447/how-to-get-a-fogbugz-xml-api-token">API
 * token</a>, you can instantiate this class like so:
 * <pre>
 * FogBugz fb = new FogBugz(baseURL, authToken);
 * </pre>
 * </p>
 * <p>
 * Otherwise, you can alternatively use:
 * <pre>
 * FogBugz fb = new FogBugz(baseURL, email, password);
 * </pre>
 * After instantiating this class, you may then use
 * <code>fb.getAuthToken();</code> to obtain a valid API token for later.
 * </p>
 * <p>
 * If the constructors of this class are throwing {@link SSLHandshakeException},
 * then your FogBugz instance is most likely using an invalid SSL certificate.
 * This can be bypassed (at your own risk) like so:
 * <pre>
 * FogBugz.trustInvalidCertificates();
 * FogBugz fb = ...
 * </pre>
 * </p>
 */
public class FogBugz {

    private static final Logger logger = LoggerFactory.getLogger(FogBugz.class);

    private final String baseURL;
    private final String authToken;
    private final Unmarshaller jaxb;

    /**
     * Constructor.
     *
     * @param baseURL   The FogBugz URL
     * @param authToken FogBugz API token
     * @throws FB2GHException
     * @see <a href=
     * "http://help.fogcreek.com/8447/how-to-get-a-fogbugz-xml-api-token">How
     * To Get a FogBugz XML API Token</a>
     */
    public FogBugz(String baseURL, String authToken) throws FB2GHException {
        try {
            this.jaxb = JAXBContext.newInstance(FBResponse.class).createUnmarshaller();
            this.baseURL = normalize(baseURL);
            this.authToken = authToken;
        } catch (JAXBException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Constructor that obtains an <code>authToken</code> from the given email
     * and password.
     *
     * @param baseURL  The FogBugz URL
     * @param email    FogBugz email
     * @param password FogBugz password
     * @throws FB2GHException
     */
    public FogBugz(String baseURL, String email, String password) throws FB2GHException {
        try {
            this.jaxb = JAXBContext.newInstance(FBResponse.class).createUnmarshaller();
            this.baseURL = normalize(baseURL);
            this.authToken = parseApiRequest("logon", "email=" + email, "password=" + password).getToken();
            logger.info("Generated API token: {}", this.authToken);
        } catch (JAXBException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Call this method if your FogBugz instance has an untrusted security
     * certificate. (This is indicated by "Privacy error" in Chrome, or if
     * methods of this class are throwing {@link SSLHandshakeException}.)
     *
     * @throws FB2GHException
     */
    public static void trustInvalidCertificates() throws FB2GHException {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] tm = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // no check
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // no check
                }
            }};

            // Install the trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, tm, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * @return The URL of this FogBugz instance.
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * @return The <code>token</code> parameter used to access the API.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Get a list of all milestones from this FogBugz instance.
     *
     * @return The list
     * @throws FB2GHException
     */
    public List<FBMilestone> listMilestones() throws FB2GHException {
        return parseApiRequest("listFixFors").getMilestones();
    }

    /**
     * Search for cases.
     *
     * @param query The query term you are searching for. Can be a string, a case
     *              number, a comma separated list of case numbers without spaces,
     *              e.g. 12,25,556. This search acts exactly the same way the
     *              search box in FogBugz operates. To search for the number 123
     *              and not the case 123, enclose your search in quotes.
     * @return A list containing the search results
     * @throws FB2GHException
     */
    public List<FBCase> searchCases(String query) throws FB2GHException {
        try {
            String[] cols = { "ixBugParent", "fOpen", "sTitle", "sPersonAssignedTo", "sStatus", "ixBugOriginal",
                    "sPriority", "ixFixFor", "sFixFor", "sCategory", "events", "sCase" };
            List<FBCase> list = parseApiRequest("search", "q=" + URLEncoder.encode(query, "UTF-8"),
                    "cols=" + String.join(",", cols)).getCases();
            logger.info("Search for '{}' returned {} case(s)", query, list.size());
            return list;
        } catch (UnsupportedEncodingException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Perform the given API call, then parse the response.
     *
     * @param cmd        The <code>cmd</code> argument
     * @param parameters Additional parameters to include in the query string
     * @return The response.
     * @throws FB2GHException
     * @see <a href="http://help.fogcreek.com/the-fogbugz-api">The FogBugz
     * API</a>
     */
    private FBResponse parseApiRequest(String cmd, String... parameters) throws FB2GHException {
        try {
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
            InputStream inStream = new URL(url).openStream();
            FBResponse response = (FBResponse) jaxb.unmarshal(inStream);
            inStream.close();
            return response;
        } catch (IOException | JAXBException e) {
            throw new FB2GHException(e);
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
