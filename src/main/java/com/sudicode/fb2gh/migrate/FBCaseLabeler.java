package com.sudicode.fb2gh.migrate;

import com.sudicode.fb2gh.fogbugz.FBCase;
import com.sudicode.fb2gh.github.GHLabel;

import java.util.List;

/**
 * By default, {@link Migrator} will label FogBugz cases using only its <code>category</code>. This interface can be
 * used to override the default behavior.
 */
@FunctionalInterface
public interface FBCaseLabeler {

    /**
     * Obtain the labels that should be added when posting a {@link FBCase} to GitHub.
     *
     * @param fbCase The {@link FBCase}
     * @return A {@link List} of labels to add
     */
    List<GHLabel> getLabels(FBCase fbCase);

}
