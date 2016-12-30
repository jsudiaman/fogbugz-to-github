package com.sudicode.fb2gh.fogbugz;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz area.
 */
@EqualsAndHashCode
@ToString(of = "name")
@XmlRootElement(name = "area")
public class FBArea {

    private int id;
    private String name;
    private int projectId;
    private String projectName;

    FBArea() {
    }

    /**
     * @return ID of the area
     */
    public int getId() {
        return id;
    }

    @XmlElement(name = "ixArea")
    void setId(int id) {
        this.id = id;
    }

    /**
     * @return Name of the area
     */
    public String getName() {
        return name;
    }

    @XmlElement(name = "sArea")
    void setName(String name) {
        this.name = name;
    }

    /**
     * @return ID of the associated project
     */
    public int getProjectId() {
        return projectId;
    }

    @XmlElement(name = "ixProject")
    void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     * @return Name of the associated project
     */
    public String getProjectName() {
        return projectName;
    }

    @XmlElement(name = "sProject")
    void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}
