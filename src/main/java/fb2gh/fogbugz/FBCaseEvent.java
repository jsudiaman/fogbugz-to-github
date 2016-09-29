package fb2gh.fogbugz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

/**
 * FogBugz case event.
 */
public final class FBCaseEvent extends FBXmlObject {

    private final Integer id;
    private final Integer caseId;
    private final String body;
    private final String changes;
    private final List<FBAttachment> attachments;
    private final String description;

    /**
     * Constructor.
     * 
     * @param event
     *            The <code>event</code> XML element that this object represents
     * @param baseURL
     *            The <code>baseURL</code> of the <code>FogBugz</code> instance
     *            that owns this case event
     */
    private FBCaseEvent(Element event, String baseURL) {
        this.id = Integer.parseInt(event.getAttribute("ixBugEvent"));
        this.caseId = Integer.parseInt(event.getAttribute("ixBug"));
        this.body = getTextValue(event, "sHtml");
        this.changes = StringUtils.chomp(getTextValue(event, "sChanges"));
        this.attachments = Collections.unmodifiableList(FBAttachment.listAttachments(event, baseURL));
        this.description = getTextValue(event, "evtDescription");
    }

    /**
     * Get the events contained within a case.
     * 
     * @param caze
     *            The case
     * @param baseURL
     *            The <code>baseURL</code> of the <code>FogBugz</code> instance
     *            that owns these case events
     * 
     * @return A list of case events
     */
    static List<FBCaseEvent> listCaseEvents(Element caze, String baseURL) {
        List<FBCaseEvent> list = new ArrayList<>();
        for (Element event : new FBXmlElements(caze.getElementsByTagName("event"))) {
            list.add(new FBCaseEvent(event, baseURL));
        }
        return list;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the caseId
     */
    public Integer getCaseId() {
        return caseId;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @return the changes
     */
    public String getChanges() {
        return changes;
    }

    /**
     * @return the attachments
     */
    public List<FBAttachment> getAttachments() {
        return attachments;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

}
