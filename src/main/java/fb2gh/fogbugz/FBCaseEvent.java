package fb2gh.fogbugz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
     */
    private FBCaseEvent(Element event) {
        this.id = Integer.parseInt(event.getAttribute("ixBugEvent"));
        this.caseId = Integer.parseInt(event.getAttribute("ixBug"));
        this.body = getTextValue(event, "sHtml");
        this.changes = StringUtils.chomp(getTextValue(event, "sChanges"));
        this.attachments = Collections.unmodifiableList(FBAttachment.listAttachments(event));
        this.description = getTextValue(event, "evtDescription");
    }

    /**
     * Get the events contained within a case.
     * 
     * @param caze
     *            The case
     * 
     * @return A list of case events
     */
    static List<FBCaseEvent> listCaseEvents(Element caze) {
        List<FBCaseEvent> list = new ArrayList<>();
        NodeList nodes = caze.getElementsByTagName("event");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                list.add(new FBCaseEvent((Element) node));
            }
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
