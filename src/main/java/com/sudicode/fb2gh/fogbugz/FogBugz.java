package com.sudicode.fb2gh.fogbugz;

import com.sudicode.fb2gh.FB2GHException;

import java.util.List;

/**
 * Entry point for the FogBugz API. Use {@link FBFactory} to instantiate.
 */
public interface FogBugz {

    /**
     * Get a list of all projects from this FogBugz instance.
     *
     * @return The list
     * @throws FB2GHException if there is an API issue.
     */
    List<FBProject> listProjects() throws FB2GHException;

    /**
     * Get a list of all milestones from this FogBugz instance.
     *
     * @return The list
     * @throws FB2GHException if there is an API issue.
     */
    List<FBMilestone> listMilestones() throws FB2GHException;

    /**
     * Get a list of all milestones from this FogBugz instance.
     *
     * @param project Only list milestones for this project.
     * @return The list
     * @throws FB2GHException if there is an API issue.
     */
    List<FBMilestone> listMilestones(FBProject project) throws FB2GHException;

    /**
     * Get a list of all areas from this FogBugz instance.
     *
     * @return The list
     * @throws FB2GHException if there is an API issue.
     */
    List<FBArea> listAreas() throws FB2GHException;

    /**
     * Get a list of all areas from this FogBugz instance.
     *
     * @param project Only list areas for this project.
     * @return The list
     * @throws FB2GHException if there is an API issue.
     */
    List<FBArea> listAreas(FBProject project) throws FB2GHException;

    /**
     * Get a list of all categories from this FogBugz instance.
     *
     * @return The list
     * @throws FB2GHException if there is an API issue.
     */
    List<FBCategory> listCategories() throws FB2GHException;

    /**
     * Get a list of all statuses from this FogBugz instance.
     *
     * @return The list
     * @throws FB2GHException if there is an API issue.
     */
    List<FBStatus> listStatuses() throws FB2GHException;

    /**
     * Get a list of all statuses from this FogBugz instance.
     *
     * @param category Only list statues from this category.
     * @return The list
     * @throws FB2GHException if there is an API issue.
     */
    List<FBStatus> listStatuses(FBCategory category) throws FB2GHException;

    /**
     * Get a single case.
     *
     * @param caseId Case number
     * @return The case
     * @throws FB2GHException if there is an API issue.
     */
    FBCase getCase(int caseId) throws FB2GHException;

    /**
     * Iterate multiple cases within a range.
     *
     * @param minId Case number to start at (inclusive)
     * @param maxId Case number to stop at (inclusive)
     * @return An {@link Iterable} of the cases
     * @throws FB2GHException if there is an API issue.
     */
    Iterable<FBCase> iterateCases(int minId, int maxId) throws FB2GHException;

    /**
     * Search for cases.
     *
     * @param query The query term you are searching for. Can be a string, a case
     *              number, a comma separated list of case numbers without spaces,
     *              e.g. 12,25,556. This search acts exactly the same way the
     *              search box in FogBugz operates. To search for the number 123
     *              and not the case 123, enclose your search in quotes.
     * @return A list containing the search results
     * @throws FB2GHException if there is an API issue.
     */
    List<FBCase> searchCases(String query) throws FB2GHException;

    /**
     * <p>
     * Get the FogBugz URL.
     * </p>
     * <p>
     * This method will return an empty <code>String</code> if left unimplemented.
     * </p>
     *
     * @return The URL of this FogBugz instance.
     */
    default String getBaseURL() {
        return "";
    }

    /**
     * <p>
     * Get the API token.
     * </p>
     * <p>
     * This method will return an empty <code>String</code> if left unimplemented.
     * </p>
     *
     * @return The <code>token</code> parameter used to access the API.
     */
    default String getAuthToken() {
        return "";
    }

}
