package com.sudicode.fb2gh.fogbugz;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz milestone.
 */
@XmlRootElement(name = "fixfor")
public final class FBMilestone {

    private int id;
    private String name;
    private int projectId;
    private String projectName;

    FBMilestone() {
    }

    /**
     * @return ID of the milestone
     */
    public int getId() {
        return id;
    }

    @XmlElement(name = "ixFixFor")
    void setId(int id) {
        this.id = id;
    }

    /**
     * @return Name of the milestone
     */
    public String getName() {
        return name;
    }

    @XmlElement(name = "sFixFor")
    void setName(String name) {
        this.name = name;
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

}
