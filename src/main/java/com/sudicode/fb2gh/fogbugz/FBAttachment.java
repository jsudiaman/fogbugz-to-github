package com.sudicode.fb2gh.fogbugz;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * FogBugz case event attachment.
 */
@XmlRootElement(name = "attachment")
public final class FBAttachment extends FBXmlObject {

    private String sFileName;
    private String sURL;

    /**
     * @return The filename of this attachment
     */
    public String getFilename() {
        return sFileName;
    }

    /**
     * @param fogBugz
     *            The <code>FogBugz</code> that owns this attachment
     * 
     * @return The URL of this attachment
     */
    public String getUrl(FogBugz fogBugz) {
        return fogBugz.getBaseURL() + "/" + StringEscapeUtils.unescapeHtml4(sURL) + "&token=" + fogBugz.getAuthToken();
    }

}
