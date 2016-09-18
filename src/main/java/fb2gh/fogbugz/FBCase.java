package fb2gh.fogbugz;

import fb2gh.DataClass;

/**
 * FogBugz case.
 */
// TODO events
public class FBCase extends DataClass {

    private final Integer id;
    private final Boolean open;
    private final String title;
    private final String assignee;
    private final String status;
    private final Integer milestoneId;

    /**
     * Constructor.
     * 
     * @param id
     *            ixBug
     * @param open
     *            fOpen
     * @param title
     *            sTitle
     * @param assignee
     *            sPersonAssignedTo
     * @param status
     *            sStatus
     * @param milestoneId
     *            ixFixFor
     */
    public FBCase(Integer id, Boolean open, String title, String assignee, String status, Integer milestoneId) {
        this.id = id;
        this.open = open;
        this.title = title;
        this.assignee = assignee;
        this.status = status;
        this.milestoneId = milestoneId;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the open
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the assignee
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the milestoneId
     */
    public Integer getMilestoneId() {
        return milestoneId;
    }

}
