package com.sudicode.fb2gh.fogbugz;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz project.
 */
@XmlRootElement(name = "project")
public class FBProject {

    private int id;
    private String name;
    private String owner;

    FBProject() {
    }

    /**
     * @return The ID of the project
     */
    public int getId() {
        return id;
    }

    @XmlElement(name = "ixProject")
    void setId(int id) {
        this.id = id;
    }

    /**
     * @return The title of the project
     */
    public String getName() {
        return name;
    }

    @XmlElement(name = "sProject")
    void setName(String name) {
        this.name = name;
    }

    /**
     * @return The owner of the project
     */
    public String getOwner() {
        return owner;
    }

    @XmlElement(name = "sPersonOwner")
    void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FBProject fbProject = (FBProject) o;

        return new EqualsBuilder()
                .append(id, fbProject.id)
                .append(name, fbProject.name)
                .append(owner, fbProject.owner)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(owner)
                .toHashCode();
    }

    @Override
    public String toString() {
        return name;
    }

}
