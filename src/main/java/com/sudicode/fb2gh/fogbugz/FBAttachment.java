package com.sudicode.fb2gh.fogbugz;

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
    private final FBCaseEvent fbCaseEvent;

    /**
     * Constructor.
     * 
     * @param attachment
     *            The <code>attachment</code> XML element that this object
     *            represents
     * @param fbCaseEvent
     *            The <code>FBCaseEvent</code> instance that owns this
     *            attachment
     */
    private FBAttachment(Element attachment, FBCaseEvent fbCaseEvent) {
        this.filename = getTextValue(attachment, "sFileName");
        this.url = fbCaseEvent.getFbCase().getFogBugz().getBaseURL() + "/"
                + StringEscapeUtils.unescapeHtml4(getTextValue(attachment, "sURL")) + "&token="
                + fbCaseEvent.getFbCase().getFogBugz().getAuthToken();
        this.fbCaseEvent = fbCaseEvent;
    }

    /**
     * Get the attachments contained within an event.
     * 
     * @param event
     *            The event
     * @param fbCaseEvent
     *            The <code>FBCaseEvent</code> instance that owns these
     *            attachments
     * 
     * @return A list of attachments
     */
    static List<FBAttachment> listAttachments(Element event, FBCaseEvent fbCaseEvent) {
        List<FBAttachment> list = new ArrayList<>();
        for (Element attachment : new FBXmlElements(event.getElementsByTagName("attachment"))) {
            list.add(new FBAttachment(attachment, fbCaseEvent));
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

    /**
     * @return the fbCaseEvent
     */
    public FBCaseEvent getFbCaseEvent() {
        return fbCaseEvent;
    }

}
