package com.sudicode.fb2gh.migrate;

import com.sudicode.fb2gh.FB2GHException;
import com.sudicode.fb2gh.common.AbstractBuilder;
import com.sudicode.fb2gh.common.FB2GHUtils;
import com.sudicode.fb2gh.fogbugz.FBAttachment;
import com.sudicode.fb2gh.fogbugz.FBCase;
import com.sudicode.fb2gh.fogbugz.FBCaseEvent;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.github.GHIssue;
import com.sudicode.fb2gh.github.GHLabel;
import com.sudicode.fb2gh.github.GHMilestone;
import com.sudicode.fb2gh.github.GHRepo;
import lombok.Lombok;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Migrates FogBugz cases to GitHub issues.
 * <p>
 * This class cannot be instantiated using a traditional constructor. To instantiate, use the builder, like so:
 * <pre>
 * Migrator migrator = new Migrator.Builder(fogBugz, cases, ghRepo) // Required
 *     .fbAttachmentConverter(fbAttachmentConverter)                // Optional
 *     .build();                                                    // Returns Migrator
 * </pre>
 */
public class Migrator {

    private static final long DEFAULT_POST_DELAY = 100;
    private static final Logger logger = LoggerFactory.getLogger(Migrator.class);
    private static final ThreadLocal<DateFormat> utcFormat = ThreadLocal.withInitial(() -> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    });

    private final FogBugz fogBugz;
    private final Iterable<FBCase> cases;
    private final GHRepo ghRepo;
    private final FBAttachmentConverter fbAttachmentConverter;
    private final FBCaseLabeler fbCaseLabeler;
    private final Predicate<FBCase> closeIf;
    private final Map<String, String> usernameMap;
    private final long postDelay;
    private final Predicate<FBCase> migrateIf;
    private final BiConsumer<FBCase, GHIssue> afterMigrate;
    private final BiConsumer<FBCase, Exception> exceptionHandler;
    private final ThreadLocal<DateFormat> dateFormat;

    /**
     * Constructor.
     *
     * @param builder The {@link Builder} to initialize with
     */
    private Migrator(final Builder builder) {
        // Required
        fogBugz = builder.fogBugz;
        cases = builder.cases;
        ghRepo = builder.ghRepo;

        // Optional
        fbAttachmentConverter = builder.fbAttachmentConverter != null ? builder.fbAttachmentConverter
                : (fb, attachment) -> attachment.getAbsoluteUrl(fb);
        fbCaseLabeler = builder.fbCaseLabeler != null ? builder.fbCaseLabeler
                : fbCase -> Collections.singletonList(new GHLabel(fbCase.getCategory()));
        closeIf = builder.closeIf != null ? builder.closeIf
                : FBCase::isClosed;
        usernameMap = builder.usernameMap != null ? builder.usernameMap
                : Collections.emptyMap();
        postDelay = builder.postDelay;
        migrateIf = builder.migrateIf != null ? builder.migrateIf
                : fbCase -> true;
        afterMigrate = builder.afterMigrate != null ? builder.afterMigrate
                : (fbCase, ghIssue) -> FB2GHUtils.nop();
        exceptionHandler = builder.exceptionHandler != null ? builder.exceptionHandler
                : (fbCase, e) -> {
            throw Lombok.sneakyThrow(e);
        };
        dateFormat = ThreadLocal.withInitial(() -> builder.dateFormat != null ? builder.dateFormat
                : new SimpleDateFormat("M/d/yyyy h:mm a z"));
    }

    /**
     * Builder used to instantiate {@link Migrator}.
     */
    public static final class Builder extends AbstractBuilder<Migrator> {
        private final FogBugz fogBugz;
        private final Iterable<FBCase> cases;
        private final GHRepo ghRepo;
        private FBAttachmentConverter fbAttachmentConverter;
        private FBCaseLabeler fbCaseLabeler;
        private Predicate<FBCase> closeIf;
        private Map<String, String> usernameMap;
        private long postDelay = DEFAULT_POST_DELAY;
        private Predicate<FBCase> migrateIf;
        private BiConsumer<FBCase, GHIssue> afterMigrate;
        private BiConsumer<FBCase, Exception> exceptionHandler;
        private DateFormat dateFormat;

        /**
         * Constructor.
         *
         * @param fogBugz The FogBugz instance
         * @param cases   Cases to migrate
         * @param ghRepo  The GitHub repository to migrate to
         */
        public Builder(final FogBugz fogBugz, final Iterable<FBCase> cases, final GHRepo ghRepo) {
            this.fogBugz = fogBugz;
            this.cases = cases;
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

        /**
         * Map FogBugz names to GitHub usernames.
         *
         * @param usernameMap A {@link Map} where <code>key</code> is a FogBugz name and <code>value</code> is the
         *                    corresponding GitHub username.
         * @return This object
         */
        public Builder usernameMap(final Map<String, String> usernameMap) {
            this.usernameMap = usernameMap;
            return this;
        }

        /**
         * @param postDelay Number of milliseconds to wait after posting an issue or comment, to avoid abuse detection.
         * @return This object
         * @throws IllegalArgumentException if <code>postDelay</code> is negative
         */
        public Builder postDelay(final long postDelay) {
            if (postDelay < 0) {
                throw new IllegalArgumentException("Post delay cannot be negative.");
            }
            this.postDelay = postDelay;
            return this;
        }

        /**
         * Only migrate FogBugz cases that pass the given {@link Predicate}. By default, all cases will be migrated.
         *
         * @param migrateIf The {@link Predicate} to use
         * @return This object
         */
        public Builder migrateIf(final Predicate<FBCase> migrateIf) {
            this.migrateIf = migrateIf;
            return this;
        }

        /**
         * After migrating a case to GitHub, perform some action specified by
         * the given {@link BiConsumer}.
         *
         * @param afterMigrate {@link BiConsumer} to use. The {@link BiConsumer} receives
         *                     (1) the FogBugz case that was migrated, and (2) the GitHub
         *                     issue that was posted.
         * @return This object
         */
        public Builder afterMigrate(final BiConsumer<FBCase, GHIssue> afterMigrate) {
            this.afterMigrate = afterMigrate;
            return this;
        }

        /**
         * If migrating a case causes an {@link Exception} to be thrown, catch and handle it using the given
         * {@link BiConsumer}. If no further exceptions are thrown, the migration process will continue as normal after
         * being handled in this way. By default, {@link Exception Exceptions} are propagated upwards as is (thus
         * ending the migration process).
         *
         * @param exceptionHandler {@link BiConsumer} to use. The {@link BiConsumer} receives (1) the FogBugz case that
         *                         was attempted to be migrated, and (2) the exception thrown.
         * @return This object
         */
        public Builder exceptionHandler(final BiConsumer<FBCase, Exception> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * Format timestamps using the given {@link DateFormat}. By default, they will be formatted as follows:
         * <code>4/18/2017 1:29 PM EDT</code>. (The time zone will vary based on the JVM default.)
         * <p>
         * Note: The default {@link DateFormat} is thread-safe. Should this method be invoked, the new
         * {@link DateFormat} may no longer be thread-safe. If using this configuration option, it is recommended to
         * avoid multithreaded access of the resulting {@link Migrator}.
         *
         * @param dateFormat {@link DateFormat} to use.
         * @return This object
         */
        public Builder dateFormat(final DateFormat dateFormat) {
            this.dateFormat = dateFormat;
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
        Set<String> labelNames = ghRepo.getLabels().stream().map(GHLabel::getName).collect(Collectors.toSet());
        Map<String, GHMilestone> milestones = new HashMap<>();
        for (GHMilestone milestone : ghRepo.getMilestones()) {
            milestones.put(milestone.getTitle(), milestone);
        }

        for (FBCase fbCase : cases) {
            try {
                // Skip if case shouldn't be migrated
                if (!migrateIf.test(fbCase)) {
                    continue;
                }

                // Labels to attach to issue
                List<GHLabel> issueLabels = fbCaseLabeler.getLabels(fbCase);

                // If labels don't exist, create them
                for (GHLabel label : issueLabels) {
                    if (!FB2GHUtils.containsIgnoreCase(labelNames, label.getName())) {
                        ghRepo.addLabel(label);
                        labelNames.add(label.getName());
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
                FB2GHUtils.sleepQuietly(postDelay);
                issue.addLabels(issueLabels);
                issue.setMilestone(ghMilestone);
                for (int i = 1; i < events.size(); i++) {
                    issue.addComment(convertToComment(events.get(i)));
                    FB2GHUtils.sleepQuietly(postDelay);
                }
                if (closeIf.test(fbCase)) {
                    issue.close();
                }

                // Set assignee
                if (issue.isOpen() && usernameMap.containsKey(fbCase.getAssignee())) {
                    issue.assignTo(usernameMap.get(fbCase.getAssignee()));
                }

                // Post-migration action
                afterMigrate.accept(fbCase, issue);

                logger.info("Migrated case '{}'", title);
                if (Thread.interrupted()) {
                    logger.info("Migration interrupted.");
                    break;
                }
            } catch (FB2GHException | RuntimeException e) {
                exceptionHandler.accept(fbCase, e);
            }
        }
    }

    /**
     * Represent the given {@link FBCaseEvent} as a GitHub issue comment.
     *
     * @param event The {@link FBCaseEvent}
     * @return The comment
     * @throws FB2GHException if there is an API issue.
     */
    private String convertToComment(final FBCaseEvent event) throws FB2GHException {
        StringBuilder sb = new StringBuilder();

        Date date;
        try {
            date = getUtcFormat().parse(event.getDateTime());
        } catch (ParseException e) {
            throw new FB2GHException(e);
        }
        sb.append("<strong>").append(event.getDescription()).append("</strong> ").append(getDateFormat().format(date));

        if (StringUtils.chomp(event.getChanges()).length() > 0) {
            sb.append("<br>").append(StringUtils.chomp(event.getChanges()));
        }

        if (event.getBody().length() > 0) {
            sb.append("<hr>").append(event.getBody());
        }

        if (!event.getAttachments().isEmpty()) {
            sb.append("<hr>");
            for (FBAttachment attachment : event.getAttachments()) {
                String filename = attachment.getFilename();
                String url = fbAttachmentConverter.convert(fogBugz, attachment);
                boolean isImage = FilenameUtils.getExtension(url).toLowerCase().matches("png|gif|jpg");

                sb.append(String.format("<a href=\"%s\">", url));
                if (isImage) {
                    sb.append(String.format("<img src=\"%s\" alt=\"%s\">", url, filename));
                } else {
                    sb.append(filename);
                }
                sb.append("</a><br>");
            }
        }

        return sb.toString();
    }

    /**
     * @return {@link DateFormat} used to parse FogBugz timestamps.
     */
    private static DateFormat getUtcFormat() {
        return utcFormat.get();
    }

    /**
     * @return {@link DateFormat} used to format FogBugz timestamps.
     */
    private DateFormat getDateFormat() {
        return dateFormat.get();
    }

}
