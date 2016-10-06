package com.sudicode.fb2gh.fogbugz;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

/**
 * FogBugz case event.
 */
@XmlRootElement(name = "event")
@XmlAccessorType(XmlAccessType.FIELD)
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

    public Integer getId() {
        return ixBugEvent;
    }

    public Integer getCaseId() {
        return ixBug;
    }

    public String getBody() {
        return sHtml;
    }

    public String getChanges() {
        return StringUtils.chomp(sChanges);
    }

    public List<FBAttachment> getAttachments() {
        return rgAttachments;
    }

    public String getDescription() {
        return evtDescription;
    }

    public String getDateTime() {
        return dt;
    }

}
