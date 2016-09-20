package fb2gh.fogbugz;

import fb2gh.DataClass;

/**
 * FogBugz case event attachment.
 */
public class FBAttachment extends DataClass {

    private final String filename;
    private final String url;

    /**
     * Constructor.
     * 
     * @param filename
     *            sFileName
     * @param url
     *            sURL
     */
    FBAttachment(String filename, String url) {
        this.filename = filename;
        this.url = url;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

}
