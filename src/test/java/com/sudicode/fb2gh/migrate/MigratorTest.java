package com.sudicode.fb2gh.migrate;

import com.jcabi.github.mock.MkGithub;
import com.sudicode.fb2gh.fogbugz.FBCase;
import com.sudicode.fb2gh.fogbugz.FogBugz;
import com.sudicode.fb2gh.github.GHRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.joor.Reflect.*;
import static org.mockito.Mockito.*;

public class MigratorTest {

    private FogBugz fogBugz;
    private List<FBCase> caseList;
    private GHRepo ghRepo;
    private Migrator migrator;

    @Before
    public void setUp() throws Exception {
        fogBugz = mock(FogBugz.class);
        caseList = new ArrayList<>(); // TODO
        ghRepo = on(GHRepo.class).create(new MkGithub().randomRepo()).get(); // TODO
        migrator = new Migrator.Builder(fogBugz, caseList, ghRepo).build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void migrate() throws Exception {
        migrator.migrate();
    }

}
