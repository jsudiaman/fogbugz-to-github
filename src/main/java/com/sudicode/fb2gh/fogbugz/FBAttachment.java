package com.sudicode.fb2gh.fogbugz;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz case event attachment.
 */
@XmlRootElement(name = "attachment")
public final class FBAttachment {

    private String filename;
    private String url;

    FBAttachment() {
    }

    /**
     * @return The filename of this attachment
     */
    public String getFilename() {
        return filename;
    }

    @XmlElement(name = "sFileName")
    void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return The relative URL of this attachment
     */
    public String getUrl() {
        return url;
    }

    @XmlElement(name = "sURL")
    void setUrl(String url) {
        this.url = url;
    }

    /**
     * @param fogBugz The <code>FogBugz</code> that owns this attachment
     * @return The absolute URL of this attachment, including FogBugz base URL and token string.
     */
    public String getAbsoluteUrl(FogBugz fogBugz) {
        return fogBugz.getBaseURL() + "/" + StringEscapeUtils.unescapeHtml4(url) + "&token=" + fogBugz.getAuthToken();
    }

}
