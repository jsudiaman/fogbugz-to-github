package com.sudicode.fb2gh.fogbugz;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz milestone.
 */
@XmlRootElement(name = "fixfor")
@XmlAccessorType(XmlAccessType.FIELD)
public final class FBMilestone extends FBXmlObject {

    private Integer ixFixFor;
    private String sFixFor;
    private Integer ixProject;
    private String sProject;

    public Integer getId() {
        return ixFixFor;
    }

    public String getName() {
        return sFixFor;
    }

    public Integer getProjectId() {
        return ixProject;
    }

    public String getProjectName() {
        return sProject;
    }

}
