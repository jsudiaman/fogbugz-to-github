package fb2gh.migrate;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
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
public class CaseMigrator {

    private final static Logger logger = LoggerFactory.getLogger(CaseMigrator.class);

    private final FBAttachmentConverter fbAttachmentConverter;
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
        this(fbCase, ghRepo, fbAttachment -> fbAttachment.getUrl());
    }

    /**
     * Constructor.
     * 
     * @param fbCase
     *            The FogBugz case to migrate
     * @param ghRepo
     *            The GitHub repo to post the issue in
     * @param fbAttachmentConverter
     *            The {@link FBAttachmentConverter} to use
     */
    public CaseMigrator(FBCase fbCase, GHRepo ghRepo, FBAttachmentConverter fbAttachmentConverter) {
        this.fbCase = fbCase;
        this.ghRepo = ghRepo;
        this.fbAttachmentConverter = fbAttachmentConverter;
    }

    /**
     * Migrate the case.
     * 
     * @return The generated {@link GHIssue}, which can be further interacted
     *         with.
     */
    public GHIssue migrate() {
        // Get title and description
        List<FBCaseEvent> events = fbCase.getEvents();
        String title = fbCase.getTitle();
        String description = eventToComment(events.get(0));

        // Post the issue, along with remaining events (if any)
        GHIssue issue = ghRepo.addIssue(title, description);
        for (int i = 1; i < events.size(); i++) {
            issue.addComment(eventToComment(events.get(i)));
        }

        logger.info("Migrated case '{}'", title);
        return issue;
    }

    /**
     * Represent the given {@link FBCaseEvent} as a GitHub issue comment.
     * 
     * @param event
     *            The {@link FBCaseEvent}
     * 
     * @return The comment
     */
    private String eventToComment(FBCaseEvent event) {
        StringBuilder sb = new StringBuilder();

        sb.append("<strong>").append(event.getDescription()).append("</strong> ").append(event.getDateTime());

        if (event.getChanges().length() > 0) {
            sb.append("<br>").append(event.getChanges());
        }

        if (event.getBody().length() > 0) {
            sb.append("<hr>").append(event.getBody());
        }

        if (event.getAttachments().size() > 0) {
            sb.append("<hr>");
            for (FBAttachment attachment : event.getAttachments()) {
                String filename = attachment.getFilename();
                String url = fbAttachmentConverter.convert(attachment);

                if (FilenameUtils.getExtension(url).toLowerCase().matches("png|gif|jpg|jpeg")) {
                    // If it's an image, prepend '!' to the Markdown string
                    sb.append("!");
                }
                sb.append("[").append(filename).append("](").append(url).append(")<br>");
            }
        }

        return sb.toString();
    }

}