package com.sudicode.fb2gh.migrate;

import com.sudicode.fb2gh.FB2GHException;
import com.sudicode.fb2gh.fogbugz.FBCase;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.github.GHIssue;
import com.sudicode.fb2gh.github.GHLabel;
import com.sudicode.fb2gh.github.GHMilestone;
import com.sudicode.fb2gh.github.GHRepo;
import com.sudicode.fb2gh.github.OfflineGHRepo;
import org.joor.Reflect;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link Migrator}.
 */
public class MigratorTest {

    private static Unmarshaller jaxb;

    private FogBugz fogBugz;
    private List<FBCase> caseList;
    private GHRepo ghRepo;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        jaxb = JAXBContext.newInstance(Class.forName("com.sudicode.fb2gh.fogbugz.FBResponse")).createUnmarshaller();
    }

    @Before
    public void setUp() throws Exception {
        fogBugz = mock(FogBugz.class);
        caseList = Reflect.on(jaxb.unmarshal(new StreamSource(getClass().getResourceAsStream("Cases.xml"))))
                .call("getCases")
                .get();
        ghRepo = new OfflineGHRepo();
    }

    @Test
    public void migrate() throws Exception {
        Migrator migrator = new Migrator.Builder(fogBugz, caseList, ghRepo).build();
        migrator.migrate();

        // Repo milestones
        GHMilestone undecided = Reflect.on(GHMilestone.class).create(1, "Undecided").get();
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
        assertThat(issue.getMilestone().orElseThrow(() -> new AssertionError("Milestone was not present")), is(equalTo(undecided)));

        // Issue status
        assertFalse(issue.isOpen());
        assertTrue(issue.isClosed());
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

    @Test
    public void migrateWithCondition() throws Exception {
        Migrator migrator = new Migrator.Builder(fogBugz, caseList, ghRepo)
                .migrateIf(FBCase::isOpen)
                .build();
        migrator.migrate();

        // Nonexistent issue
        try {
            GHIssue issue = ghRepo.getIssue(1);
            issue.getTitle();
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void migrateWithAfter() throws Exception {
        AtomicBoolean postMigrate = new AtomicBoolean();

        Migrator migrator = new Migrator.Builder(fogBugz, caseList, ghRepo).afterMigrate((fbCase, ghIssue) -> {
            try {
                // Verify that fbCase and ghIssue are accurate
                assertThat(fbCase.getId(), is(equalTo(1)));
                assertThat(ghIssue.getTitle(), is(equalTo("Sample Bug")));

                // Indicate that this method was successfully invoked
                postMigrate.set(true);
            } catch (FB2GHException e) {
                fail(e.getMessage());
            }
        }).build();
        migrator.migrate();

        assertThat(postMigrate.get(), is(equalTo(true)));
    }

    @Test
    public void migrateWithExceptionHandler() throws Exception {
        // Generate FBCase which throws an exception when accessed
        FB2GHException accessException = new FB2GHException("Accessed a mock case!");
        FBCase mockCase = mock(FBCase.class, invocation -> {
            throw accessException;
        });
        caseList.add(0, mockCase);

        // Test exception handler
        AtomicReference<FBCase> failedCase = new AtomicReference<>();
        AtomicReference<Exception> exceptionHandled = new AtomicReference<>();
        new Migrator.Builder(fogBugz, caseList, ghRepo)
                .exceptionHandler((fbCase, e) -> {
                    failedCase.set(fbCase);
                    exceptionHandled.set(e);
                })
                .build()
                .migrate();
        assertThat(failedCase.get(), is(equalTo(mockCase)));
        assertThat(exceptionHandled.get(), is(equalTo(accessException)));

        // Ensure that the rest of the cases were migrated
        assertThat(ghRepo.getIssue(1), is(notNullValue()));
    }

    @Test
    public void migrateWithoutExceptionHandler() throws Exception {
        // Generate FBCase which throws an exception when accessed
        FBCase mockCase = mock(FBCase.class, invocation -> {
            throw new RuntimeException();
        });

        // Test without exception handler
        try {
            new Migrator.Builder(fogBugz, Collections.singletonList(mockCase), ghRepo)
                    .build()
                    .migrate();
            fail("Expected RuntimeException");
        } catch (RuntimeException expected) {
        }
    }

}
