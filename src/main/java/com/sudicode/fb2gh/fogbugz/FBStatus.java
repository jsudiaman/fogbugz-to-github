package com.sudicode.fb2gh.fogbugz;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz status ('Active', 'Resolved', etc).
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FBStatus fbStatus = (FBStatus) o;

        return new EqualsBuilder()
                .append(id, fbStatus.id)
                .append(categoryId, fbStatus.categoryId)
                .append(name, fbStatus.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(categoryId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return name;
    }

}
