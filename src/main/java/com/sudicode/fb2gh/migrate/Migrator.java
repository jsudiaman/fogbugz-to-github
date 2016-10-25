package com.sudicode.fb2gh.migrate;

import com.sudicode.fb2gh.FB2GHException;
import com.sudicode.fb2gh.common.FB2GHUtils;
import com.sudicode.fb2gh.common.AbstractBuilder;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * <p>
 * Migrates FogBugz cases to GitHub issues.
 * </p>
 * <p>
 * This class cannot be instantiated using a traditional constructor. To instantiate, use the builder, like so:
 * </p>
 * <pre>
 * Migrator migrator = new Migrator.Builder(fogBugz, caseList, ghRepo) // Required
 *     .fbAttachmentConverter(fbAttachmentConverter) // Optional
 *     .build(); // Returns Migrator
 * </pre>
 */
public final class Migrator {

    private static final Logger logger = LoggerFactory.getLogger(Migrator.class);

    private final FogBugz fogBugz;
    private final List<FBCase> caseList;
    private final GHRepo ghRepo;
    private final FBAttachmentConverter fbAttachmentConverter;
    private final FBCaseLabeler fbCaseLabeler;
    private final Predicate<FBCase> closeIf;

    /**
     * Constructor.
     *
     * @param builder The {@link Builder} to initialize with
     */
    private Migrator(final Builder builder) {
        // Required
        fogBugz = builder.fogBugz;
        caseList = builder.caseList;
        ghRepo = builder.ghRepo;

        // Optional
        fbAttachmentConverter = builder.fbAttachmentConverter != null ? builder.fbAttachmentConverter
                : (fb, attachment) -> attachment.getAbsoluteUrl(fb);
        fbCaseLabeler = builder.fbCaseLabeler != null ? builder.fbCaseLabeler
                : fbCase -> Collections.singletonList(fbCase.getCategory());
        closeIf = builder.closeIf != null ? builder.closeIf
                : fbCase -> !fbCase.isOpen();
    }

    /**
     * Builder used to instantiate {@link Migrator}.
     */
    public static final class Builder extends AbstractBuilder<Migrator> {
        private final FogBugz fogBugz;
        private final List<FBCase> caseList;
        private final GHRepo ghRepo;
        private FBAttachmentConverter fbAttachmentConverter;
        private FBCaseLabeler fbCaseLabeler;
        private Predicate<FBCase> closeIf;

        /**
         * Constructor.
         *
         * @param fogBugz  The FogBugz instance
         * @param caseList A list of cases to migrate
         * @param ghRepo   The GitHub repository to migrate to
         */
        public Builder(final FogBugz fogBugz, final List<FBCase> caseList, final GHRepo ghRepo) {
            this.fogBugz = fogBugz;
            this.caseList = caseList;
            this.ghRepo = ghRepo;
        }

        /**
         * @param fbAttachmentConverter The {@link FBAttachmentConverter} to use
         * @return This object
         */
        public Builder fbAttachmentConverter(final FBAttachmentConverter fbAttachmentConverter) {
            this.fbAttachmentConverter = fbAttachmentConverter;
            return this;
        }

        /**
         * @param fbCaseLabeler The {@link FBCaseLabeler} to use
         * @return This object
         */
        public Builder fbCaseLabeler(final FBCaseLabeler fbCaseLabeler) {
            this.fbCaseLabeler = fbCaseLabeler;
            return this;
        }

        /**
         * After migrating a FogBugz case, close the corresponding GitHub issue if the {@link FBCase} passes the given
         * {@link Predicate}. By default, the GitHub issue will be closed if the FogBugz case is closed.
         *
         * @param closeIf The {@link Predicate} to use
         * @return This object
         */
        public Builder closeIf(final Predicate<FBCase> closeIf) {
            this.closeIf = closeIf;
            return this;
        }

        @Override
        public Migrator build() {
            return new Migrator(this);
        }
    }

    /**
     * Migrate the cases.
     *
     * @throws FB2GHException if there is an API issue.
     */
    public void migrate() throws FB2GHException {
        // Internal caches
        Set<String> labels = new HashSet<>(ghRepo.getLabels());
        Map<String, GHMilestone> milestones = new HashMap<>();
        for (GHMilestone milestone : ghRepo.getMilestones()) {
            milestones.put(milestone.getTitle(), milestone);
        }

        for (FBCase fbCase : caseList) {
            // Labels to attach to issue
            List<String> issueLabels = fbCaseLabeler.getLabels(fbCase);

            // If labels don't exist, create them
            for (String label : issueLabels) {
                if (!FB2GHUtils.containsIgnoreCase(labels, label)) {
                    ghRepo.addLabel(label);
                    labels.add(label);
                }
            }

            // If milestone doesn't exist, create it
            String milestoneTitle = fbCase.getMilestoneName();
            GHMilestone ghMilestone;
            if (milestones.containsKey(milestoneTitle)) {
                ghMilestone = milestones.get(milestoneTitle);
            } else {
                ghMilestone = ghRepo.addMilestone(milestoneTitle);
                milestones.put(milestoneTitle, ghMilestone);
            }

            // Get title and description
            List<FBCaseEvent> events = fbCase.getEvents();
            String title = fbCase.getTitle();
            String description = convertToComment(events.get(0));

            // Post the issue, along with remaining events (if any)
            GHIssue issue = ghRepo.addIssue(title, description);
            issue.addLabels(issueLabels);
            issue.setMilestone(ghMilestone);
            for (int i = 1; i < events.size(); i++) {
                issue.addComment(convertToComment(events.get(i)));
            }
            if (closeIf.test(fbCase)) {
                issue.close();
            }

            logger.info("Migrated case '{}'", title);
        }
    }

    /**
     * Represent the given {@link FBCaseEvent} as a GitHub issue comment.
     *
     * @param event The {@link FBCaseEvent}
     * @return The comment
     */
    private String convertToComment(final FBCaseEvent event) {
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
                if (FilenameUtils.getExtension(url).toLowerCase().matches("png|gif|jpg")) {
                    sb.append("!");
                }
                sb.append("[").append(filename).append("](").append(url).append(")<br>");
            }
        }

        return sb.toString();
    }

}
