package com.sudicode.fb2gh.migrate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.sudicode.fb2gh.FB2GHException;
import com.sudicode.fb2gh.fogbugz.FBCase;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.github.GHMilestone;
import com.sudicode.fb2gh.github.GHRepo;

/**
 * Migrates FogBugz cases to GitHub issues. This is the main "one-and-done"
 * migrator.
 */
public class Migrator {

    private final FogBugz fogBugz;
    private final List<FBCase> caseList;
    private final GHRepo ghRepo;

    /**
     * Constructor.
     * 
     * @param fogBugz
     *            The FogBugz instance
     * @param caseList
     *            A list of cases to migrate
     * @param ghRepo
     *            The GitHub repository to migrate to
     */
    public Migrator(FogBugz fogBugz, List<FBCase> caseList, GHRepo ghRepo) {
        this.fogBugz = fogBugz;
        this.caseList = caseList;
        this.ghRepo = ghRepo;
    }

    /**
     * Migrate the cases.
     * 
     * @throws FB2GHException
     */
    // TODO Align FogBugz case numbers with GitHub issue numbers
    public void migrate() throws FB2GHException {
        // Internal caches
        Set<String> repoLabels = new HashSet<>(ghRepo.getLabels());
        BiMap<Integer, String> repoMilestones = repoMilestoneMap();

        // Sort FogBugz cases by ID
        Collections.sort(caseList, (lhs, rhs) -> lhs.getId().compareTo(rhs.getId()));

        for (FBCase fbCase : caseList) {
            // Labels to attach to issue
            String[] issueLabels = { fbCase.getCategory(), fbCase.getPriority() };

            // If labels don't exist, create them
            for (String label : issueLabels) {
                if (!containsIgnoreCase(repoLabels, label)) {
                    ghRepo.addLabel(label);
                    repoLabels.add(label);
                }
            }

            // If milestone doesn't exist, create it
            String milestoneTitle = fbCase.getMilestoneName();
            int milestoneNumber;
            if (!repoMilestones.containsValue(milestoneTitle)) {
                milestoneNumber = ghRepo.addMilestone(milestoneTitle);
                repoMilestones.put(milestoneNumber, milestoneTitle);
            } else {
                milestoneNumber = repoMilestones.inverse().get(milestoneTitle);
            }

            // Use CaseMigrator to migrate case
            new CaseMigrator(fogBugz, fbCase, ghRepo).migrate().addLabels(issueLabels).setMilestone(milestoneNumber);
        }
    }

    /**
     * Generates a {@link BiMap} of GitHub milestones, mapping milestone numbers
     * to their respective titles. The reason for using {@link BiMap} rather
     * than {@link Map} is to allow for a two-way lookup - number to title, and
     * vice versa.
     * 
     * @return A {@link BiMap} of GitHub milestones, where <code>key</code> is
     *         the milestone number and <code>value</code> is its title.
     * 
     * @throws FB2GHException
     */
    private BiMap<Integer, String> repoMilestoneMap() throws FB2GHException {
        BiMap<Integer, String> biMap = HashBiMap.create();
        for (GHMilestone milestone : ghRepo.getMilestones()) {
            biMap.put(milestone.getNumber(), milestone.getTitle());
        }
        return biMap;
    }

    /**
     * Case-insensitive search for a string in a collection of strings.
     * 
     * @param strings
     *            A collection of strings
     * @param searchString
     *            The string to search for
     * @return True if <code>searchString</code> is found in
     *         <code>strings</code>, regardless of case
     */
    private static boolean containsIgnoreCase(Collection<String> strings, String searchString) {
        return strings.stream().filter(s -> s.equalsIgnoreCase(searchString)).findFirst().isPresent();
    }

}
