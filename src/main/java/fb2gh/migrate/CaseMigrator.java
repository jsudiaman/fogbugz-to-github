package fb2gh.migrate;

import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fb2gh.fogbugz.FBAttachment;
import fb2gh.fogbugz.FBCase;
import fb2gh.fogbugz.FBCaseEvent;
import fb2gh.github.GHIssue;
import fb2gh.github.GHRepo;

/**
 * Migrates FogBugz case to GitHub issue.
 */
// TODO Use object-oriented constructs rather than functions
public class CaseMigrator {

    private final static Logger logger = LoggerFactory.getLogger(CaseMigrator.class);

    private final FBCase fbCase;
    private final GHRepo ghRepo;

    /**
     * Constructor.
     * 
     * @param fbCase
     *            The FogBugz case to migrate
     * @param ghRepo
     *            The GitHub repo to post the issue in
     */
    public CaseMigrator(FBCase fbCase, GHRepo ghRepo) {
        this.fbCase = fbCase;
        this.ghRepo = ghRepo;
    }

    /**
     * Migrate the case using the default attachment conversion strategy.
     * 
     * @return The generated {@link GHIssue}, which can be further interacted
     *         with.
     */
    public GHIssue migrate() {
        return migrate(attachment -> "[" + attachment.getFilename() + "](" + attachment.getUrl() + ")");
    }

    /**
     * Migrate the case using a specific attachment conversion strategy.
     * 
     * @param attachmentHandler
     *            Function that gives the {@link String} representation of an
     *            {@link FBAttachment}
     * 
     * @return The generated {@link GHIssue}, which can be further interacted
     *         with.
     */
    public GHIssue migrate(Function<FBAttachment, String> attachmentHandler) {
        // Get title and description
        List<FBCaseEvent> events = fbCase.getEvents();
        String title = fbCase.getTitle();
        String description = eventToComment(events.get(0), attachmentHandler);

        // Post the issue, along with remaining events (if any)
        GHIssue issue = ghRepo.addIssue(title, description);
        for (int i = 1; i < events.size(); i++) {
            issue.addComment(eventToComment(events.get(i), attachmentHandler));
        }

        logger.info("Migrated case '{}'", title);
        return issue;
    }

    /**
     * Represent the given {@link FBCaseEvent} as a GitHub issue comment.
     * 
     * @param event
     *            The {@link FBCaseEvent}
     * @param attachmentHandler
     *            Function that gives the {@link String} representation of an
     *            {@link FBAttachment}
     * 
     * @return The comment
     */
    private String eventToComment(FBCaseEvent event, Function<FBAttachment, String> attachmentHandler) {
        StringBuilder sb = new StringBuilder();

        sb.append("<strong>").append(event.getDescription()).append("</strong>");

        if (event.getChanges().length() > 0) {
            sb.append("<br>").append(event.getChanges());
        }

        if (event.getBody().length() > 0) {
            sb.append("<hr>").append(event.getBody());
        }

        if (event.getAttachments().size() > 0) {
            sb.append("<hr>");
            for (FBAttachment attachment : event.getAttachments()) {
                sb.append(attachmentHandler.apply(attachment)).append("<br>");
            }
        }

        return sb.toString();
    }

}
