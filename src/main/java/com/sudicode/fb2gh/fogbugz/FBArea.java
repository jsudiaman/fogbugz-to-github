package com.sudicode.fb2gh.fogbugz;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz area.
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FBArea fbArea = (FBArea) o;

        return new EqualsBuilder()
                .append(id, fbArea.id)
                .append(projectId, fbArea.projectId)
                .append(name, fbArea.name)
                .append(projectName, fbArea.projectName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(projectId)
                .append(projectName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return name;
    }

}
