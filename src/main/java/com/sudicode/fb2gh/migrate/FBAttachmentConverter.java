package com.sudicode.fb2gh.migrate;

import com.sudicode.fb2gh.fogbugz.FBAttachment;
import com.sudicode.fb2gh.fogbugz.FogBugz;

/**
 * By default, {@link Migrator} will reference FogBugz attachments using
 * their original FogBugz URLs. This may not be desired behavior, especially for
 * those who are planning to do away with FogBugz entirely. In such cases, this
 * interface can be used to override the default behavior.
 */
public interface FBAttachmentConverter {

    /**
     * Obtain the URL that should be used when posting a {@link FBAttachment} to
     * GitHub.
     *
     * @param fogBugz      The {@link FogBugz} instance that owns the
     *                     {@link FBAttachment}
     * @param fbAttachment The {@link FBAttachment}
     * @return The URL
     */
    String convert(FogBugz fogBugz, FBAttachment fbAttachment);

}
