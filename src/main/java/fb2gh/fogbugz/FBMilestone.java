package fb2gh.fogbugz;

import fb2gh.DataClass;

/**
 * FogBugz milestone.
 */
public class FBMilestone extends DataClass {

    private Integer id;
    private String name;
    private Integer projectId;
    private String projectName;

    /**
     * Constructor.
     */
    public FBMilestone() {
    }

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
    public FBMilestone(Integer id, String name, Integer projectId, String projectName) {
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
     * @param id
     *            the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the projectId
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * @param projectId
     *            the projectId to set
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName
     *            the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}
