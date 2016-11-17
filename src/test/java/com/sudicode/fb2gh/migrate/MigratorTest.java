package com.sudicode.fb2gh.migrate;

import com.sudicode.fb2gh.fogbugz.FBCase;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.github.GHIssue;
import com.sudicode.fb2gh.github.GHLabel;
import com.sudicode.fb2gh.github.GHMilestone;
import com.sudicode.fb2gh.github.GHRepo;
import com.sudicode.fb2gh.github.OfflineGHRepo;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.stream.StreamSource;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.joor.Reflect.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link Migrator}.
 */
public class MigratorTest {

    private GHRepo ghRepo;
    private Migrator migrator;

    @Before
    public void setUp() throws Exception {
        FogBugz fogBugz = mock(FogBugz.class);
        List<FBCase> caseList = on(JAXBContext
                .newInstance(Class.forName("com.sudicode.fb2gh.fogbugz.FBResponse"))
                .createUnmarshaller()
                .unmarshal(new StreamSource(getClass().getResourceAsStream("FogBugz.xml"))))
                .call("getCases")
                .get();
        ghRepo = new OfflineGHRepo();
        migrator = new Migrator.Builder(fogBugz, caseList, ghRepo).build();
    }

    @Test
    public void migrate() throws Exception {
        migrator.migrate();

        // Repo milestones
        GHMilestone undecided = on("com.sudicode.fb2gh.github.GHMilestone").create(1, "Undecided").get();
        assertThat(ghRepo.getMilestones(), contains(undecided));

        GHIssue issue = ghRepo.getIssue(1);

        // Issue title and body
        assertThat(issue.getTitle(), is(equalTo("Sample Bug")));
        assertThat(issue.getBody(), is(equalTo("<strong>Opened by Alice Adams</strong> 2007-06-27T16:37:13Z<hr>Something is wrong with our product.")));

        // Issue comments
        List<String> comments = issue.getComments();
        assertThat(comments.get(0), is(equalTo("<strong>Assigned to Bob Brown by Alice Adams</strong> 2007-06-27T16:37:13Z")));
        assertThat(comments.get(1), is(equalTo("<strong>Resolved (Won't Fix) and assigned to Alice Adams by Alice Adams</strong> 2009-01-07T22:04:31Z<br>Status changed from 'Active' to 'Resolved (Won't Fix)'.")));
        assertThat(comments.get(2), is(equalTo("<strong>Closed by Alice Adams</strong> 2009-01-07T22:04:31Z")));

        // Issue labels
        assertThat(issue.getLabels(), contains(new GHLabel("bug")));

        // Issue milestone
        assertThat(issue.getMilestone().isPresent(), is(equalTo(true)));
        assertThat(issue.getMilestone().get(), is(equalTo(undecided)));
    }

}
