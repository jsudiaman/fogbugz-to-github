package com.sudicode.fb2gh.fogbugz;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sudicode.fb2gh.FB2GHException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link FogBugzImpl}.
 */
public class FogBugzImplTest {

    @Rule
    public WireMockRule server = new WireMockRule();
    private FogBugz fogBugz;

    @Before
    public void setUp() throws Exception {
        fogBugz = new FogBugzImpl("http://localhost:8080", "token");
    }

    /**
     * Register a mapping.
     *
     * @param mappingBuilder The mapping
     */
    private void supposeThat(final MappingBuilder mappingBuilder) {
        server.givenThat(mappingBuilder);
    }

    /**
     * Initiate a mapping to <code>api.asp</code>.
     *
     * @return The {@link MappingBuilder} to use
     */
    private MappingBuilder aRequest() {
        return any(urlPathEqualTo("/api.asp"));
    }

    /**
     * Define the response.
     *
     * @param xmlFile Name of the XML resource
     * @return The {@link ResponseDefinitionBuilder} to use
     * @throws IOException if an I/O exception occurs.
     */
    private ResponseDefinitionBuilder theContentsOf(String xmlFile) throws IOException {
        return aResponse()
                .withHeader("Content-Type", "text/xml")
                .withBody(IOUtils.toString(getClass().getResource(xmlFile)));
    }

    @Test
    public void testListProjects() throws Exception {
        supposeThat(aRequest()
                .withQueryParam("cmd", equalTo("listProjects"))
                .willReturn(theContentsOf("Projects.xml")));

        FBProject project = fogBugz.listProjects().get(0);
        assertThat(project.getId(), is(10));
        assertThat(project.getName(), is("Kakapo"));
        assertThat(project.getOwner(), is("Evelyn the Project Manager"));
    }

    @Test
    public void testBadToken() throws Exception {
        supposeThat(aRequest()
                .willReturn(theContentsOf("Error3.xml")));

        try {
            fogBugz.listProjects(); // The actual command doesn't really matter
            fail("Expected FB2GHException");
        } catch (FB2GHException expected) {
            assertThat(expected.getMessage(), is("Not logged on"));
        }
    }

    @Test
    public void testBadCredentials() throws Exception {
        supposeThat(aRequest()
                .withQueryParam("cmd", equalTo("logon"))
                .willReturn(theContentsOf("Error1.xml")));

        try {
            new FogBugzImpl(fogBugz.getBaseURL(), "foo", "bar");
            fail("Expected FB2GHException");
        } catch (FB2GHException expected) {
            assertThat(expected.getMessage(), is("Incorrect password or username"));
        }
    }

    @Test
    public void testListMilestones() throws Exception {
        supposeThat(aRequest()
                .withQueryParam("cmd", equalTo("listFixFors"))
                .willReturn(theContentsOf("Milestones.xml")));

        FBMilestone milestone = fogBugz.listMilestones().get(0);
        assertThat(milestone.getId(), is(9));
        assertThat(milestone.getName(), is("Version 1.0"));
        assertThat(milestone.getProjectId(), is(10));
        assertThat(milestone.getProjectName(), is("Kakapo"));
    }

    @Test
    public void testListAreas() throws Exception {
        supposeThat(aRequest()
                .withQueryParam("cmd", equalTo("listAreas"))
                .willReturn(theContentsOf("Areas.xml")));

        FBArea area = fogBugz.listAreas().get(0);
        assertThat(area.getId(), is(6));
        assertThat(area.getName(), is("Spam"));
        assertThat(area.getProjectId(), is(2));
        assertThat(area.getProjectName(), is("Inbox"));
    }

    @Test
    public void testGetCase() throws Exception {
        supposeThat(aRequest()
                .withQueryParam("cmd", equalTo("search"))
                .withQueryParam("q", equalTo("123"))
                .willReturn(theContentsOf("Cases.xml")));

        FBCase fbCase = fogBugz.getCase(123);
        assertThat(fbCase.getParentCaseId(), is(234));
        assertFalse(fbCase.isClosed());
        assertThat(fbCase.getStatus(), is("Geschlossen (Fixed)"));
        assertThat(fbCase.getDuplicateOfId(), is(654));
        assertThat(fbCase.getMilestoneId(), is(3));
        assertThat(fbCase.getProjectId(), is(22));
        assertThat(fbCase.getProjectName(), is("The Farm"));
        assertThat(fbCase.getArea(), is("Pond"));
    }

}
