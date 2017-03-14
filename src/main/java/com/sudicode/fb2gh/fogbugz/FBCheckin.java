package com.sudicode.fb2gh.fogbugz;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz checkin (from source control).
 */
@XmlRootElement(name = "checkin")
@EqualsAndHashCode
@ToString
public class FBCheckin {

    private int revisionNumber;
    private String filename;

    FBCheckin() {
    }

    /**
     * @return The post-checkin revision number.
     */
    public int getRevisionNumber() {
        return revisionNumber;
    }

    @XmlElement(name = "sNew")
    void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    /**
     * @return The name of the file being checked in.
     */
    public String getFilename() {
        return filename;
    }

    @XmlElement(name = "sFile")
    void setFilename(String filename) {
        this.filename = filename;
    }

}
