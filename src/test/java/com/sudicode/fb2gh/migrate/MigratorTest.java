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
import java.util.ArrayList;
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

    private FogBugz fogBugz;
    private List<FBCase> caseList;
    private GHRepo ghRepo;

    @Before
    public void setUp() throws Exception {
        fogBugz = mock(FogBugz.class);
        caseList = on(JAXBContext.newInstance(Class.forName("com.sudicode.fb2gh.fogbugz.FBResponse"))
                .createUnmarshaller()
                .unmarshal(new StreamSource(getClass().getResourceAsStream("FogBugz.xml")))
        ).call("getCases").get();
        ghRepo = new OfflineGHRepo();
    }

    @Test
    public void migrate() throws Exception {
        Migrator migrator = new Migrator.Builder(fogBugz, caseList, ghRepo).build();
        migrator.migrate();

        // Repo milestones
        GHMilestone undecided = on(GHMilestone.class).create(1, "Undecided").get();
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

    @Test
    public void migrateWithLabels() throws Exception {
        Migrator migrator = new Migrator.Builder(fogBugz, caseList, ghRepo)
                .fbCaseLabeler(fbCase -> {
                    List<GHLabel> list = new ArrayList<>();
                    list.add(new GHLabel("F" + fbCase.getId(), "92602c"));
                    if (fbCase.getSalesforceCaseId() != 0) {
                        list.add(new GHLabel("S" + fbCase.getSalesforceCaseId(), "178cda"));
                    }
                    list.add(new GHLabel(fbCase.getCategory()));
                    list.add(new GHLabel(fbCase.getPriority()));
                    return list;
                })
                .build();
        migrator.migrate();

        GHIssue issue = ghRepo.getIssue(1);

        // Issue labels
        assertThat(issue.getLabels(), contains(new GHLabel("F1", "92602c"), new GHLabel("Bug"), new GHLabel("High")));
    }

}
