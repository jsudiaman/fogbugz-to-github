package com.sudicode.fb2gh.fogbugz;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * FogBugz case.
 */
@XmlRootElement(name = "case")
public final class FBCase {

    private Integer id;
    private Integer parentCaseId;
    private Boolean open;
    private String title;
    private String assignee;
    private String status;
    private Integer duplicateOfId;
    private String priority;
    private Integer milestoneId;
    private String milestoneName;
    private String category;
    private List<FBCaseEvent> events;
    private Integer salesforceCaseId;

    FBCase() {
    }

    /**
     * @return Case number
     */
    public Integer getId() {
        return id;
    }

    @XmlAttribute(name = "ixBug")
    void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return Parent case number
     */
    public Integer getParentCaseId() {
        return parentCaseId;
    }

    @XmlElement(name = "ixBugParent")
    void setParentCaseId(Integer parentCaseId) {
        this.parentCaseId = parentCaseId;
    }

    /**
     * @return <code>true</code> if the case is open. <code>false</code> if it
     * is closed
     */
    public Boolean isOpen() {
        return open;
    }

    @XmlElement(name = "fOpen")
    void setOpen(Boolean open) {
        this.open = open;
    }

    /**
     * @return The title of the case
     */
    public String getTitle() {
        return title;
    }

    @XmlElement(name = "sTitle")
    void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return Name of the person assigned to the case
     */
    public String getAssignee() {
        return assignee;
    }

    @XmlElement(name = "sPersonAssignedTo")
    void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    /**
     * @return The status of the case
     */
    public String getStatus() {
        return status;
    }

    @XmlElement(name = "sStatus")
    void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return If marked as duplicate, the case that this case was a duplicate
     * of
     */
    public Integer getDuplicateOfId() {
        return duplicateOfId;
    }

    @XmlElement(name = "ixBugOriginal")
    void setDuplicateOfId(Integer duplicateOfId) {
        this.duplicateOfId = duplicateOfId;
    }

    /**
     * @return The priority of the case
     */
    public String getPriority() {
        return priority;
    }

    @XmlElement(name = "sPriority")
    void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * @return ID of the milestone this case is assigned to
     */
    public Integer getMilestoneId() {
        return milestoneId;
    }

    @XmlElement(name = "ixFixFor")
    void setMilestoneId(Integer milestoneId) {
        this.milestoneId = milestoneId;
    }

    /**
     * @return Name of the milestone this case is assigned to
     */
    public String getMilestoneName() {
        return milestoneName;
    }

    @XmlElement(name = "sFixFor")
    void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    /**
     * @return The category of the case
     */
    public String getCategory() {
        return category;
    }

    @XmlElement(name = "sCategory")
    void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return All of the events for the case
     */
    public List<FBCaseEvent> getEvents() {
        return events;
    }

    @XmlElementWrapper
    @XmlElement(name = "event")
    void setEvents(List<FBCaseEvent> events) {
        this.events = events;
    }

    /**
     * @return The Salesforce case ID of the case (Requires Salesforce plugin)
     */
    public Integer getSalesforceCaseId() {
        return salesforceCaseId;
    }

    @XmlElement(name = "sCase")
    void setSalesforceCaseId(Integer salesforceCaseId) {
        this.salesforceCaseId = salesforceCaseId;
    }

}
