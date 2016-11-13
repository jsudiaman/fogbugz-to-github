package com.sudicode.fb2gh.fogbugz;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * FogBugz case event.
 */
@XmlRootElement(name = "event")
public class FBCaseEvent {

    private int id;
    private int caseId;
    private String body;
    private String changes;
    private List<FBAttachment> attachments;
    private String description;
    private String dateTime;

    FBCaseEvent() {
    }

    /**
     * @return Identity field in the database for this event
     */
    public int getId() {
        return id;
    }

    @XmlAttribute(name = "ixBugEvent")
    void setId(int id) {
        this.id = id;
    }

    /**
     * @return Case number
     */
    public int getCaseId() {
        return caseId;
    }

    @XmlAttribute(name = "ixBug")
    void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    /**
     * @return The raw HTML version of the event
     */
    public String getBody() {
        return body;
    }

    @XmlElement(name = "sHtml")
    void setBody(String body) {
        this.body = body;
    }

    /**
     * @return Description of changes to the case during this event
     */
    public String getChanges() {
        return changes;
    }

    @XmlElement(name = "sChanges")
    void setChanges(String changes) {
        this.changes = changes;
    }

    /**
     * @return The attachments of the event
     */
    public List<FBAttachment> getAttachments() {
        return attachments;
    }

    @XmlElementWrapper(name = "rgAttachments")
    @XmlElement(name = "attachment")
    void setAttachments(List<FBAttachment> attachments) {
        this.attachments = attachments;
    }

    /**
     * @return Description of event <strong><em>in YOUR language</em></strong>
     */
    public String getDescription() {
        return description;
    }

    @XmlElement(name = "evtDescription")
    void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Date and time that the event happened, in RFC822 UTC format
     */
    public String getDateTime() {
        return dateTime;
    }

    @XmlElement(name = "dt")
    void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}
