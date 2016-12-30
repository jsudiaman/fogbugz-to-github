package com.sudicode.fb2gh.fogbugz;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz category ('Bug', 'Feature', etc).
 */
@EqualsAndHashCode
@ToString(of = "name")
@XmlRootElement(name = "category")
public class FBCategory {

    private int id;
    private String name;

    /**
     * @return ID of the category.
     */
    public int getId() {
        return id;
    }

    @XmlElement(name = "ixCategory")
    void setId(int id) {
        this.id = id;
    }

    /**
     * @return Name of the category.
     */
    public String getName() {
        return name;
    }

    @XmlElement(name = "sCategory")
    void setName(String name) {
        this.name = name;
    }

}
