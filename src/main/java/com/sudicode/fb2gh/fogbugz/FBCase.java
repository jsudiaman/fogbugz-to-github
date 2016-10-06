package com.sudicode.fb2gh.fogbugz;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz case.
 */
@XmlRootElement(name = "case")
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

    private FBCase() {
        // Constructed through JAXB
    }

    /**
     * @return Case number
     */
    public Integer getId() {
        return ixBug;
    }

    /**
     * @return Parent case number
     */
    public Integer getParentCaseId() {
        return ixBugParent;
    }

    /**
     * @return <code>true</code> if the case is open. <code>false</code> if it
     *         is closed
     */
    public Boolean isOpen() {
        return fOpen;
    }

    /**
     * @return The title of the case
     */
    public String getTitle() {
        return sTitle;
    }

    /**
     * @return Name of the person assigned to the case
     */
    public String getAssignee() {
        return sPersonAssignedTo;
    }

    /**
     * @return The status of the case
     */
    public String getStatus() {
        return sStatus;
    }

    /**
     * @return If marked as duplicate, the case that this case was a duplicate
     *         of
     */
    public Integer getDuplicateOfId() {
        return ixBugOriginal;
    }

    /**
     * @return The priority of the case
     */
    public String getPriority() {
        return sPriority;
    }

    /**
     * @return ID of the milestone this case is assigned to
     */
    public Integer getMilestoneId() {
        return ixFixFor;
    }

    /**
     * @return The category of the case
     */
    public String getCategory() {
        return sCategory;
    }

    /**
     * @return All of the events for the case
     */
    public List<FBCaseEvent> getEvents() {
        return events;
    }

    /**
     * @return The Salesforce case ID of the case (Requires Salesforce plugin)
     */
    public Integer getSalesforceCaseId() {
        return sCase;
    }

}
