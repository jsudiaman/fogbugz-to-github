package com.sudicode.fb2gh.fogbugz;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sudicode.fb2gh.FB2GHException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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
    private void supposeThat(MappingBuilder mappingBuilder) {
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

    @Test
    public void testListProjects() throws Exception {
        supposeThat(aRequest()
                .withQueryParam("cmd", equalTo("listProjects"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withBody(IOUtils.toString(getClass().getResource("Projects.xml")))));

        FBProject project = fogBugz.listProjects().get(0);
        assertThat(project.getId(), is(10));
        assertThat(project.getName(), is("Kakapo"));
        assertThat(project.getOwner(), is("Evelyn the Project Manager"));
    }

    @Test
    public void testBadToken() throws Exception {
        supposeThat(aRequest()
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withBody(IOUtils.toString(getClass().getResource("Error3.xml")))));

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
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withBody(IOUtils.toString(getClass().getResource("Error1.xml")))));

        try {
            new FogBugzImpl(fogBugz.getBaseURL(), "foo", "bar");
            fail("Expected FB2GHException");
        } catch (FB2GHException expected) {
            assertThat(expected.getMessage(), is("Incorrect password or username"));
        }
    }

}
