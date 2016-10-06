package com.sudicode.fb2gh.fogbugz;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz case.
 */
@XmlRootElement(name = "case")
@XmlAccessorType(XmlAccessType.FIELD)
public final class FBCase extends FBXmlObject {

    @XmlAttribute
    private Integer ixBug;
    private Integer ixBugParent;
    private Boolean fOpen;
    private String sTitle;
    private String sPersonAssignedTo;
    private String sStatus;
    private Integer ixBugOriginal;
    private String sPriority;
    private Integer ixFixFor;
    private String sCategory;
    @XmlElementWrapper
    @XmlElement(name = "event")
    private List<FBCaseEvent> events;
    private Integer sCase;

    public Integer getId() {
        return ixBug;
    }

    public Integer getParentCaseId() {
        return ixBugParent;
    }

    public Boolean isOpen() {
        return fOpen;
    }

    public String getTitle() {
        return sTitle;
    }

    public String getAssignee() {
        return sPersonAssignedTo;
    }

    public String getStatus() {
        return sStatus;
    }

    public Integer getDuplicateOfId() {
        return ixBugOriginal;
    }

    public String getPriority() {
        return sPriority;
    }

    public Integer getMilestoneId() {
        return ixFixFor;
    }

    public String getCategory() {
        return sCategory;
    }

    public List<FBCaseEvent> getEvents() {
        return events;
    }

    public Integer getSalesforceCaseId() {
        return sCase;
    }

}
