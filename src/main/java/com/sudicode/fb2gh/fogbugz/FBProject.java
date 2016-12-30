package com.sudicode.fb2gh.fogbugz;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz project.
 */
@EqualsAndHashCode
@ToString(of = "name")
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

}
