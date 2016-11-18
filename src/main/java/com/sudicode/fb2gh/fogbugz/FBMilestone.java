package com.sudicode.fb2gh.fogbugz;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz milestone.
 */
@XmlRootElement(name = "fixfor")
public class FBMilestone {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FBMilestone that = (FBMilestone) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(projectId, that.projectId)
                .append(name, that.name)
                .append(projectName, that.projectName)
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("name", name)
                .append("projectId", projectId)
                .append("projectName", projectName)
                .toString();
    }

}
