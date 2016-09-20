package fb2gh.fogbugz;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * FogBugz case event attachment.
 */
public final class FBAttachment extends FBXmlObject {

    private final String filename;
    private final String url;

    /**
     * Constructor.
     * 
     * @param attachment
     *            The <code>attachment</code> XML element that this object
     *            represents
     */
    private FBAttachment(Element attachment) {
        this.filename = getTextValue(attachment, "sFileName");
        this.url = getTextValue(attachment, "sURL");
    }

    /**
     * Get the attachments contained within an event.
     * 
     * @param event
     *            The event
     * 
     * @return A list of attachments
     */
    static List<FBAttachment> listAttachments(Element event) {
        List<FBAttachment> list = new ArrayList<>();
        NodeList nodes = event.getElementsByTagName("attachment");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                list.add(new FBAttachment((Element) node));
            }
        }
        return list;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

}
