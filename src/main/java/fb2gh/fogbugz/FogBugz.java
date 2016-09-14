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
 * FogBugz session.
 */
public class FogBugz {

	private final String baseURL;
	private String authToken;
	private DocumentBuilder documentBuilder;

	private static final Logger logger = LoggerFactory.getLogger(FogBugz.class);

	/**
	 * Constructor which initializes <code>documentBuilder</code> and
	 * <code>baseURL</code>.
	 * 
	 * @throws FB2GHException
	 */
	private FogBugz(String baseURL) throws FB2GHException {
		try {
			// Initialize documentBuilderFactory
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			this.documentBuilder = documentBuilderFactory.newDocumentBuilder();

			// Remove trailing default.asp
			while (baseURL.endsWith("/")) {
				baseURL = StringUtils.chop(baseURL);
			}
			if (baseURL.endsWith("default.asp")) {
				baseURL = StringUtils.removeEnd(baseURL, "default.asp");
			}
			this.baseURL = baseURL;
		} catch (ParserConfigurationException e) {
			throw new FB2GHException(e);
		}
	}

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
		this(baseURL);
		setAuthToken(authToken);
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
		this(baseURL);
		Document doc = parseApiRequest("logon", "email=" + email, "password=" + password);
		String authToken = doc.getElementsByTagName("token").item(0).getTextContent();
		logger.info("Generated API token: {}", authToken);
		setAuthToken(authToken);
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
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, tm, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
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
	 * @param authToken
	 *            the authToken to set
	 */
	private void setAuthToken(String authToken) {
		this.authToken = authToken;
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
				Element element = (Element) node;

				// ID
				Integer id = null;
				String idString = element.getElementsByTagName("ixFixFor").item(0).getTextContent();
				if (!idString.isEmpty()) {
					id = Integer.parseInt(idString);
				}

				// Name
				String name = element.getElementsByTagName("sFixFor").item(0).getTextContent();

				// Project ID
				Integer projectId = null;
				String projectIdString = element.getElementsByTagName("ixProject").item(0).getTextContent();
				if (!projectIdString.isEmpty()) {
					projectId = Integer.parseInt(projectIdString);
				}

				// Project Name
				String projectName = element.getElementsByTagName("sProject").item(0).getTextContent();

				list.add(new FBMilestone(id, name, projectId, projectName));
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
			for (String arg : parameters) {
				url += "&" + arg;
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

}
