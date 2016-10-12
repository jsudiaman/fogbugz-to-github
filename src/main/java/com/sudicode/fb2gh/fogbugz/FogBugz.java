package com.sudicode.fb2gh.fogbugz;

import com.sudicode.fb2gh.FB2GHException;

import java.util.List;

/**
 * Entry point for the FogBugz API. The provided implementing class is {@link FogBugzImpl}.
 */
public interface FogBugz {

    /**
     * Get a list of all milestones from this FogBugz instance.
     *
     * @return The list
     * @throws FB2GHException if there is an API issue.
     */
    List<FBMilestone> listMilestones() throws FB2GHException;

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
     * @throws FB2GHException if there is an API issue.
     */
    default String getBaseURL() throws FB2GHException {
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
     * @throws FB2GHException if there is an API issue.
     */
    default String getAuthToken() throws FB2GHException {
        return "";
    }

}
