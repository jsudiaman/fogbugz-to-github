package fb2gh.fogbugz;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Element;

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
        this.url = StringEscapeUtils.unescapeHtml4(getTextValue(attachment, "sURL"));
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
        for (Element attachment : new FBXmlElements(event.getElementsByTagName("attachment"))) {
            list.add(new FBAttachment(attachment));
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
