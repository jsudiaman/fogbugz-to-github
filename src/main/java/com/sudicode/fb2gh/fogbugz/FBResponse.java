package com.sudicode.fb2gh.fogbugz;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FogBugz response. Represents the root element <code>&ltresponse&gt</code>
 * given by all FogBugz API requests.
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public final class FBResponse extends FBXmlObject {

    private String token;
    @XmlElementWrapper
    @XmlElement(name = "case")
    private List<FBCase> cases;
    @XmlElementWrapper
    @XmlElement(name = "fixfor")
    private List<FBMilestone> fixfors;

    public String getToken() {
        return token;
    }

    public List<FBCase> getCases() {
        return cases;
    }

    public List<FBMilestone> getMilestones() {
        return fixfors;
    }

}
