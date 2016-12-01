package com.sudicode.fb2gh.fogbugz;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz category ('Bug', 'Feature', etc).
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FBCategory that = (FBCategory) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .toHashCode();
    }

    @Override
    public String toString() {
        return name;
    }

}
