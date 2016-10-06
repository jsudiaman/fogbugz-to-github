package com.sudicode.fb2gh.fogbugz;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz milestone.
 */
@XmlRootElement(name = "fixfor")
public final class FBMilestone extends FBXmlObject {

    private Integer ixFixFor;
    private String sFixFor;
    private Integer ixProject;
    private String sProject;

    private FBMilestone() {
        // Constructed through JAXB
    }

    /**
     * @return ID of the milestone
     */
    public Integer getId() {
        return ixFixFor;
    }

    /**
     * @return Name of the milestone
     */
    public String getName() {
        return sFixFor;
    }

    /**
     * @return Project ID
     */
    public Integer getProjectId() {
        return ixProject;
    }

    /**
     * @return Project name
     */
    public String getProjectName() {
        return sProject;
    }

}
