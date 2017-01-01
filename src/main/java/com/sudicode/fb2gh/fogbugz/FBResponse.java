package com.sudicode.fb2gh.fogbugz;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * FogBugz response. Represents the root element <code>&lt;response&gt;</code>
 * given by all FogBugz API requests.
 */
@XmlRootElement(name = "response")
class FBResponse {

    private String token;
    private List<FBCase> cases;
    private List<FBMilestone> milestones;
    private List<FBProject> projects;
    private List<FBArea> areas;
    private List<FBCategory> categories;
    private List<FBStatus> statuses;
    private String error;

    FBResponse() {
    }

    /**
     * @return The descendant <code>&lt;token&gt;</code> element
     */
    public String getToken() {
        return token;
    }

    @XmlElement
    void setToken(String token) {
        this.token = token;
    }

    /**
     * @return Contents of the descendant <code>&lt;cases&gt;</code> element
     */
    public List<FBCase> getCases() {
        return cases;
    }

    @XmlElementWrapper
    @XmlElement(name = "case")
    void setCases(List<FBCase> cases) {
        this.cases = cases;
    }

    /**
     * @return Contents of the descendant <code>&lt;fixfors&gt;</code> element
     */
    public List<FBMilestone> getMilestones() {
        return milestones;
    }

    @XmlElementWrapper(name = "fixfors")
    @XmlElement(name = "fixfor")
    void setMilestones(List<FBMilestone> milestones) {
        this.milestones = milestones;
    }

    /**
     * @return Contents of the descendant <code>&lt;projects&gt;</code> element
     */
    public List<FBProject> getProjects() {
        return projects;
    }

    @XmlElementWrapper
    @XmlElement(name = "project")
    void setProjects(List<FBProject> projects) {
        this.projects = projects;
    }

    /**
     * @return Contents of the descendant <code>&lt;areas&gt;</code> element
     */
    public List<FBArea> getAreas() {
        return areas;
    }

    @XmlElementWrapper
    @XmlElement(name = "area")
    void setAreas(List<FBArea> areas) {
        this.areas = areas;
    }

    /**
     * @return Contents of the descendant <code>&lt;categories&gt;</code> element
     */
    public List<FBCategory> getCategories() {
        return categories;
    }

    @XmlElementWrapper
    @XmlElement(name = "category")
    void setCategories(List<FBCategory> categories) {
        this.categories = categories;
    }

    /**
     * @return Contents of the descendant <code>&lt;statuses&gt;</code> element
     */
    public List<FBStatus> getStatuses() {
        return statuses;
    }

    @XmlElementWrapper
    @XmlElement(name = "status")
    public void setStatuses(List<FBStatus> statuses) {
        this.statuses = statuses;
    }

    public String getError() {
        return error;
    }

    @XmlElement
    public void setError(String error) {
        this.error = error;
    }

}
