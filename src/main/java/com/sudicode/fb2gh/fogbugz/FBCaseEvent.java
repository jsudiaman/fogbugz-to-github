package com.sudicode.fb2gh.fogbugz;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

/**
 * FogBugz case event.
 */
@XmlRootElement(name = "event")
public final class FBCaseEvent extends FBXmlObject {

    @XmlAttribute
    private Integer ixBugEvent;
    @XmlAttribute
    private Integer ixBug;
    private String sHtml;
    private String sChanges;
    @XmlElementWrapper
    @XmlElement(name = "attachment")
    private List<FBAttachment> rgAttachments;
    private String evtDescription;
    private String dt;

    /**
     * @return Identity field in the database for this event
     */
    public Integer getId() {
        return ixBugEvent;
    }

    /**
     * @return Case number
     */
    public Integer getCaseId() {
        return ixBug;
    }

    /**
     * @return The raw HTML version of the event
     */
    public String getBody() {
        return sHtml;
    }

    /**
     * @return Description of changes to the case during this event
     */
    public String getChanges() {
        return StringUtils.chomp(sChanges);
    }

    /**
     * @return The attachments of the event
     */
    public List<FBAttachment> getAttachments() {
        return rgAttachments;
    }

    /**
     * @return Description of event <strong><em>in YOUR language</em></strong>
     */
    public String getDescription() {
        return evtDescription;
    }

    /**
     * @return Date and time that the event happened, in RFC822 UTC format
     */
    public String getDateTime() {
        return dt;
    }

}
