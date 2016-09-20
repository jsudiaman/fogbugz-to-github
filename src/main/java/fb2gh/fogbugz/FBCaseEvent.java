package fb2gh.fogbugz;

import java.util.List;

import fb2gh.DataClass;

/**
 * FogBugz case event.
 */
public class FBCaseEvent extends DataClass {

    private final Integer id;
    private final Integer caseId;
    private final String body;
    private final String changes;
    private final List<FBAttachment> attachments;
    private final String description;

    /**
     * Constructor.
     * 
     * @param id
     *            ixBugEvent
     * @param caseId
     *            ixBug
     * @param body
     *            s
     * @param changes
     *            sChanges
     * @param attachments
     *            rgAttachments
     * @param description
     *            evtDescription
     */
    FBCaseEvent(Integer id, Integer caseId, String body, String changes, List<FBAttachment> attachments,
            String description) {
        this.id = id;
        this.caseId = caseId;
        this.body = body;
        this.changes = changes;
        this.attachments = attachments;
        this.description = description;
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
