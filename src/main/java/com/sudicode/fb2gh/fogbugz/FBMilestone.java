package com.sudicode.fb2gh.fogbugz;

import org.w3c.dom.Element;

/**
 * FogBugz milestone.
 */
public final class FBMilestone extends FBXmlObject {

    private final Integer id;
    private final String name;
    private final Integer projectId;
    private final String projectName;

    /**
     * Constructor.
     * 
     * @param fixFor
     *            The <code>fixfor</code> XML element that this object
     *            represents
     */
    FBMilestone(Element fixFor) {
        this.id = getIntValue(fixFor, "ixFixFor");
        this.name = getTextValue(fixFor, "sFixFor");
        this.projectId = getIntValue(fixFor, "ixProject");
        this.projectName = getTextValue(fixFor, "sProject");
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the projectId
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

}
