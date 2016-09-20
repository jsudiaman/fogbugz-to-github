package fb2gh.fogbugz;

import fb2gh.DataClass;

/**
 * FogBugz milestone.
 */
public class FBMilestone extends DataClass {

    private final Integer id;
    private final String name;
    private final Integer projectId;
    private final String projectName;

    /**
     * Constructor.
     * 
     * @param id
     *            ixFixFor
     * @param name
     *            sFixFor
     * @param projectId
     *            ixProject
     * @param projectName
     *            sProject
     */
    FBMilestone(Integer id, String name, Integer projectId, String projectName) {
        this.id = id;
        this.name = name;
        this.projectId = projectId;
        this.projectName = projectName;
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
