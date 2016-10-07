package com.sudicode.fb2gh.fogbugz;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * FogBugz response. Represents the root element <code>&lt;response&gt;</code>
 * given by all FogBugz API requests.
 */
@XmlRootElement(name = "response")
final class FBResponse extends FBXmlObject {

    private String token;
    private List<FBCase> cases;
    private List<FBMilestone> milestones;

    FBResponse() {
    }

    /**
     * @return The descendant <code>&lt;token&gt;</code> element
     */
    public String getToken() {
        return token;
    }

    @XmlElement
    void setToken(String token) {
        this.token = token;
    }

    /**
     * @return Contents of the descendant <code>&lt;cases&gt;</code> element
     */
    public List<FBCase> getCases() {
        return cases;
    }

    @XmlElementWrapper
    @XmlElement(name = "case")
    void setCases(List<FBCase> cases) {
        this.cases = cases;
    }

    /**
     * @return Contents of the descendant <code>&lt;fixfors&gt;</code> element
     */
    public List<FBMilestone> getMilestones() {
        return milestones;
    }

    @XmlElementWrapper(name = "fixfors")
    @XmlElement(name = "fixfor")
    void setMilestones(List<FBMilestone> milestones) {
        this.milestones = milestones;
    }

}
