package fb2gh.migrate;

import fb2gh.fogbugz.FBAttachment;

/**
 * <p>
 * By default, {@link CaseMigrator} will reference FogBugz attachments using
 * their original FogBugz URLs. This may not be desired behavior, especially for
 * those who are planning to do away with FogBugz entirely. In such cases, this
 * interface can be used to override the default behavior.
 * </p>
 * 
 * <p>
 * To use this converter, pass it into the constructor of {@link CaseMigrator},
 * like so:
 * </p>
 * 
 * <pre>
 * CaseMigrator cm = new CaseMigrator(fbCase, ghRepo, new FBAttachmentConverter() {
 *     &#64;Override
 *     public String convert(FBAttachment fbAttachment) {
 *         // Handle this the way you want to. This is default behavior.
 *         return fbAttachment.getUrl();
 *     }
 * });
 * </pre>
 */
public interface FBAttachmentConverter {

    /**
     * Obtain the URL that should be used when posting a {@link FBAttachment} to
     * GitHub.
     * 
     * @param fbAttachment
     *            The {@link FBAttachment}
     * @return The URL
     */
    public String convert(FBAttachment fbAttachment);

}
