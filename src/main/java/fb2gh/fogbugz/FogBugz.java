package fb2gh.fogbugz;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fb2gh.FB2GHException;

/**
 * <p>
 * This class uses the
 * <a href="http://help.fogcreek.com/the-fogbugz-api">FogBugz API</a> to
 * interact with the given FogBugz instance. You will need to supply the URL of
 * your FogBugz instance, which will be referenced here as <code>baseURL</code>.
 * </p>
 * 
 * <p>
 * If you have an <a href=
 * "http://help.fogcreek.com/8447/how-to-get-a-fogbugz-xml-api-token">API
 * token</a>, you can instantiate this class like so:
 * 
 * <pre>
 * FogBugz fb = new FogBugz(baseURL, authToken);
 * </pre>
 * </p>
 * 
 * <p>
 * Otherwise, you can alternatively use:
 * 
 * <pre>
 * FogBugz fb = new FogBugz(baseURL, email, password);
 * </pre>
 * 
 * After instantiating this class, you may then use
 * <code>fb.getAuthToken();</code> to obtain a valid API token for later.
 * </p>
 * 
 * <p>
 * If the constructors of this class are throwing {@link SSLHandshakeException},
 * then your FogBugz instance is most likely using an invalid SSL certificate.
 * This can be bypassed (at your own risk) like so:
 * 
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
    private final DocumentBuilder documentBuilder;

    /**
     * Constructor.
     * 
     * @param baseURL
     *            The FogBugz URL
     * @param authToken
     *            FogBugz API token
     * 
     * @throws FB2GHException
     * 
     * @see <a href=
     *      "http://help.fogcreek.com/8447/how-to-get-a-fogbugz-xml-api-token">How
     *      To Get a FogBugz XML API Token</a>
     */
    public FogBugz(String baseURL, String authToken) throws FB2GHException {
        try {
            this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.baseURL = normalize(baseURL);
            this.authToken = authToken;
        } catch (ParserConfigurationException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Constructor that obtains an <code>authToken</code> from the given email
     * and password.
     * 
     * @param baseURL
     *            The FogBugz URL
     * @param email
     *            FogBugz email
     * @param password
     *            FogBugz password
     * 
     * @throws FB2GHException
     */
    public FogBugz(String baseURL, String email, String password) throws FB2GHException {
        try {
            this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.baseURL = normalize(baseURL);
            Document doc = parseApiRequest("logon", "email=" + email, "password=" + password);
            String authToken = getTextValue(doc.getDocumentElement(), "token");
            logger.info("Generated API token: {}", authToken);
            this.authToken = authToken;
        } catch (ParserConfigurationException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Remove trailing "default.asp" (if present) from the given URL. This
     * method might a bit of a misnomer, as it does not perform a fully
     * extensive URL normalization. It does, however, suffice for the purpose of
     * this class.
     * 
     * @param baseURL
     *            The URL
     * 
     * @return The normalized URL
     */
    private String normalize(String baseURL) {
        while (baseURL.endsWith("/")) {
            baseURL = StringUtils.chop(baseURL);
        }
        if (baseURL.endsWith("default.asp")) {
            baseURL = StringUtils.removeEnd(baseURL, "default.asp");
        }
        return baseURL;
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
            TrustManager[] tm = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            } };

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
     * 
     * @throws FB2GHException
     */
    public List<FBMilestone> listMilestones() throws FB2GHException {
        List<FBMilestone> list = new ArrayList<>();
        Document doc = parseApiRequest("listFixFors");

        // Loop through XML elements
        NodeList nodes = doc.getElementsByTagName("fixfor");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element fixFor = (Element) node;
                Integer id = getIntValue(fixFor, "ixFixFor");
                String name = getTextValue(fixFor, "sFixFor");
                Integer projectId = getIntValue(fixFor, "ixProject");
                String projectName = getTextValue(fixFor, "sProject");
                list.add(new FBMilestone(id, name, projectId, projectName));
            }
        }
        return list;
    }

    /**
     * Search for cases.
     * 
     * @param query
     *            The query term you are searching for. Can be a string, a case
     *            number, a comma separated list of case numbers without spaces,
     *            e.g. 12,25,556. This search acts exactly the same way the
     *            search box in FogBugz operates. To search for the number 123
     *            and not the case 123, enclose your search in quotes.
     * 
     * @return A list containing the search results
     * 
     * @throws FB2GHException
     */
    public List<FBCase> searchCases(String query) throws FB2GHException {
        List<FBCase> list = new ArrayList<>();
        Document doc = parseApiRequest("search", "q=" + query,
                "cols=fOpen,sTitle,sPersonAssignedTo,sStatus,ixFixFor,events");

        // Loop through XML elements
        NodeList nodes = doc.getElementsByTagName("case");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element bug = (Element) node;
                Integer id = Integer.parseInt(bug.getAttribute("ixBug"));
                Boolean open = getBooleanValue(bug, "fOpen");
                String title = getTextValue(bug, "sTitle");
                String assignee = getTextValue(bug, "sPersonAssignedTo");
                String status = getTextValue(bug, "sStatus");
                Integer milestoneId = getIntValue(bug, "ixFixFor");
                List<FBCaseEvent> events = getCaseEvents(bug);
                list.add(new FBCase(id, open, title, assignee, status, milestoneId, events));
            }
        }
        return list;
    }

    /**
     * Get the events contained within this case.
     * 
     * @param bug
     *            The case
     * 
     * @return A list of case events
     */
    private List<FBCaseEvent> getCaseEvents(Element bug) {
        List<FBCaseEvent> list = new ArrayList<>();
        NodeList nodes = bug.getElementsByTagName("event");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element event = (Element) node;
                Integer id = Integer.parseInt(event.getAttribute("ixBugEvent"));
                Integer caseId = Integer.parseInt(event.getAttribute("ixBug"));
                String body = getTextValue(event, "s");
                String changes = getTextValue(event, "sChanges");
                List<FBAttachment> attachments = getAttachments(event);
                String description = getTextValue(event, "evtDescription");
                list.add(new FBCaseEvent(id, caseId, body, changes, attachments, description));
            }
        }
        return list;
    }

    /**
     * Get the attachments contained within this event.
     * 
     * @param event
     *            The event
     * 
     * @return A list of attachments
     */
    private List<FBAttachment> getAttachments(Element event) {
        List<FBAttachment> list = new ArrayList<>();
        NodeList nodes = event.getElementsByTagName("attachment");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element attachment = (Element) node;
                String filename = getTextValue(attachment, "sFileName");
                String url = getTextValue(attachment, "sURL");
                list.add(new FBAttachment(filename, url));
            }
        }
        return list;
    }

    /**
     * Perform the given API call, then parse the response as a
     * {@link Document}.
     * 
     * @param cmd
     *            The <code>cmd</code> argument
     * @param parameters
     *            Additional parameters to include in the query string
     * 
     * @return The response, which is an XML document.
     * 
     * @throws FB2GHException
     * 
     * @see <a href="http://help.fogcreek.com/the-fogbugz-api">The FogBugz
     *      API</a>
     */
    private Document parseApiRequest(String cmd, String... parameters) throws FB2GHException {
        try {
            // Required
            String url = getBaseURL() + "/api.asp?cmd=" + cmd;
            if (!cmd.equals("logon")) {
                url += "&token=" + getAuthToken();
            }

            // Optional
            for (String param : parameters) {
                url += "&" + param;
            }

            // Parse XML response
            logger.debug("Opening URL: {}", url);
            InputStream inStream = new URL(url).openStream();
            Document doc = documentBuilder.parse(inStream);
            inStream.close();
            return doc;
        } catch (IOException | SAXException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * Scan the element for the tag and get its text content.
     * 
     * @param element
     *            The element
     * @param tagName
     *            The name of the tag
     * @return The text content, or <code>null</code> if not found
     */
    private String getTextValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Scan the element for the tag and get its text content, then parse it as
     * an integer.
     * 
     * @param element
     *            The element
     * @param tagName
     *            The name of the tag
     * @return The parsed integer, or <code>null</code> if parsing failed
     */
    private Integer getIntValue(Element element, String tagName) {
        String textValue = getTextValue(element, tagName);
        if (textValue == null) {
            return null;
        }
        try {
            return Integer.parseInt(textValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Scan the element for the tag and get its text content. If the content
     * equals <code>"true"</code>, return <code>true</code>. If the content
     * equals <code>"false"</code>, return <code>false</code>. Otherwise, return
     * <code>null</code>.
     * 
     * @param element
     *            The element
     * @param tagName
     *            The name of the tag
     * @return The boolean value represented by this content, or
     *         <code>null</code>
     */
    private Boolean getBooleanValue(Element element, String tagName) {
        String textValue = getTextValue(element, tagName);
        if (textValue.equals("true")) {
            return true;
        } else if (textValue.equals("false")) {
            return false;
        } else {
            return null;
        }
    }

}
