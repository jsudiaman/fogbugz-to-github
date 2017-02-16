package com.sudicode.fb2gh.fogbugz;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * FogBugz case.
 */
@XmlRootElement(name = "case")
@EqualsAndHashCode
@ToString(of = {"id", "title"})
public class FBCase {

    private int id;
    private int parentCaseId;
    private boolean open;
    private String title;
    private String assignee;
    private String status;
    private int duplicateOfId;
    private String priority;
    private int milestoneId;
    private String milestoneName;
    private String category;
    private List<FBCaseEvent> events;
    private int salesforceCaseId;
    private int projectId;
    private String projectName;
    private String area;

    FBCase() {
    }

    /**
     * @return Case number
     */
    public int getId() {
        return id;
    }

    @XmlAttribute(name = "ixBug")
    void setId(int id) {
        this.id = id;
    }

    /**
     * @return Parent case number
     */
    public int getParentCaseId() {
        return parentCaseId;
    }

    @XmlElement(name = "ixBugParent")
    void setParentCaseId(int parentCaseId) {
        this.parentCaseId = parentCaseId;
    }

    /**
     * @return <code>true</code> if the case is open. <code>false</code> if it
     * is closed
     */
    public boolean isOpen() {
        return open;
    }

    @XmlElement(name = "fOpen")
    void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * @return <code>true</code> if the case is closed. <code>false</code> if it
     * is open
     */
    public boolean isClosed() {
        return !open;
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
    public int getDuplicateOfId() {
        return duplicateOfId;
    }

    @XmlElement(name = "ixBugOriginal")
    void setDuplicateOfId(int duplicateOfId) {
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
    public int getMilestoneId() {
        return milestoneId;
    }

    @XmlElement(name = "ixFixFor")
    void setMilestoneId(int milestoneId) {
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
    public int getSalesforceCaseId() {
        return salesforceCaseId;
    }

    @XmlElement(name = "sCase")
    void setSalesforceCaseId(int salesforceCaseId) {
        this.salesforceCaseId = salesforceCaseId;
    }

    /**
     * @return Project ID
     */
    public int getProjectId() {
        return projectId;
    }

    @XmlElement(name = "ixProject")
    void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     * @return Project name
     */
    public String getProjectName() {
        return projectName;
    }

    @XmlElement(name = "sProject")
    void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the area
     */
    public String getArea() {
        return area;
    }

    @XmlElement(name = "sArea")
    void setArea(String area) {
        this.area = area;
    }

}
