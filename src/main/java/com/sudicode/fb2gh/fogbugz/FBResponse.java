package com.sudicode.fb2gh.fogbugz;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz response. Represents the root element <code>&lt;response&gt;</code>
 * given by all FogBugz API requests.
 */
@XmlRootElement(name = "response")
final class FBResponse extends FBXmlObject {

    private String token;
    @XmlElementWrapper
    @XmlElement(name = "case")
    private List<FBCase> cases;
    @XmlElementWrapper
    @XmlElement(name = "fixfor")
    private List<FBMilestone> fixfors;

    /**
     * @return The descendant <code>&lt;token&gt;</code> element
     */
    public String getToken() {
        return token;
    }

    /**
     * @return Contents of the descendant <code>&lt;cases&gt;</code> element
     */
    public List<FBCase> getCases() {
        return cases;
    }

    /**
     * @return Contents of the descendant <code>&lt;fixfors&gt;</code> element
     */
    public List<FBMilestone> getMilestones() {
        return fixfors;
    }

}
