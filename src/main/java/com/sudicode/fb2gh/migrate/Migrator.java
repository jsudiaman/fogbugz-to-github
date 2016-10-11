package com.sudicode.fb2gh.migrate;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.sudicode.fb2gh.FB2GHException;
import com.sudicode.fb2gh.FB2GHUtils;
import com.sudicode.fb2gh.fogbugz.FBAttachment;
import com.sudicode.fb2gh.fogbugz.FBCase;
import com.sudicode.fb2gh.fogbugz.FBCaseEvent;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.github.GHIssue;
import com.sudicode.fb2gh.github.GHMilestone;
import com.sudicode.fb2gh.github.GHRepo;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Migrates FogBugz cases to GitHub issues.
 */
public class Migrator {

    private static final Logger logger = LoggerFactory.getLogger(Migrator.class);

    private final FogBugz fogBugz;
    private final List<FBCase> caseList;
    private final GHRepo ghRepo;
    private final FBAttachmentConverter fbAttachmentConverter;

    /**
     * Constructor.
     *
     * @param fogBugz  The FogBugz instance
     * @param caseList A list of cases to migrate
     * @param ghRepo   The GitHub repository to migrate to
     */
    public Migrator(FogBugz fogBugz, List<FBCase> caseList, GHRepo ghRepo) {
        this(fogBugz, caseList, ghRepo, (fb, attachment) -> attachment.getAbsoluteUrl(fb));
    }

    public Migrator(FogBugz fogBugz, List<FBCase> caseList, GHRepo ghRepo, FBAttachmentConverter fbAttachmentConverter) {
        this.fogBugz = fogBugz;
        this.caseList = caseList;
        this.ghRepo = ghRepo;
        this.fbAttachmentConverter = fbAttachmentConverter;
    }

    /**
     * Migrate the cases.
     *
     * @throws FB2GHException
     */
    public void migrate() throws FB2GHException {
        // Internal caches
        Set<String> repoLabels = new HashSet<>(ghRepo.getLabels());
        BiMap<Integer, String> repoMilestones = repoMilestoneMap();

        for (FBCase fbCase : caseList) {
            // Labels to attach to issue
            String[] issueLabels = {fbCase.getCategory(), fbCase.getPriority()};

            // If labels don't exist, create them
            for (String label : issueLabels) {
                if (!FB2GHUtils.containsIgnoreCase(repoLabels, label)) {
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

            // Get title and description
            List<FBCaseEvent> events = fbCase.getEvents();
            String title = fbCase.getTitle();
            String description = convertToComment(events.get(0));

            // Post the issue, along with remaining events (if any)
            GHIssue issue = ghRepo.addIssue(title, description).addLabels(issueLabels).setMilestone(milestoneNumber);
            for (int i = 1; i < events.size(); i++) {
                issue.addComment(convertToComment(events.get(i)));
            }

            logger.info("Migrated case '{}'", title);
        }
    }

    /**
     * Generates a {@link BiMap} of GitHub milestones, mapping milestone numbers
     * to their respective titles. The reason for using {@link BiMap} rather
     * than {@link Map} is to allow for a two-way lookup - number to title, and
     * vice versa.
     *
     * @return A {@link BiMap} of GitHub milestones, where <code>key</code> is
     * the milestone number and <code>value</code> is its title.
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
     * Represent the given {@link FBCaseEvent} as a GitHub issue comment.
     *
     * @param event The {@link FBCaseEvent}
     * @return The comment
     */
    private String convertToComment(FBCaseEvent event) {
        StringBuilder sb = new StringBuilder();

        sb.append("<strong>").append(event.getDescription()).append("</strong> ").append(event.getDateTime());

        if (StringUtils.chomp(event.getChanges()).length() > 0) {
            sb.append("<br>").append(StringUtils.chomp(event.getChanges()));
        }

        if (event.getBody().length() > 0) {
            sb.append("<hr>").append(event.getBody());
        }

        if (event.getAttachments().size() > 0) {
            sb.append("<hr>");
            for (FBAttachment attachment : event.getAttachments()) {
                String filename = attachment.getFilename();
                String url = fbAttachmentConverter.convert(fogBugz, attachment);

                // If it's an image, prepend '!' to the Markdown string
                if (FilenameUtils.getExtension(url).toLowerCase().matches("png|gif|jpg|jpeg")) {
                    sb.append("!");
                }
                sb.append("[").append(filename).append("](").append(url).append(")<br>");
            }
        }

        return sb.toString();
    }

}
