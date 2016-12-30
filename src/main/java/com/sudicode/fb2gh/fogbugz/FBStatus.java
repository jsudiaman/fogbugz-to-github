package com.sudicode.fb2gh.fogbugz;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz status ('Active', 'Resolved', etc).
 */
@EqualsAndHashCode
@ToString(of = "name")
@XmlRootElement(name = "status")
public class FBStatus {

    private int id;
    private String name;
    private int categoryId;

    /**
     * @return ID of the status.
     */
    public int getId() {
        return id;
    }

    @XmlElement(name = "ixStatus")
    void setId(int id) {
        this.id = id;
    }

    /**
     * @return Name of the status.
     */
    public String getName() {
        return name;
    }

    @XmlElement(name = "sStatus")
    void setName(String name) {
        this.name = name;
    }

    /**
     * @return ID of the associated category.
     */
    public int getCategoryId() {
        return categoryId;
    }

    @XmlElement(name = "ixCategory")
    void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

}
