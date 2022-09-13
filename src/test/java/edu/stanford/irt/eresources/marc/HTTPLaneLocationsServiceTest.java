package edu.stanford.irt.eresources.marc;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class HTTPLaneLocationsServiceTest {

    private HTTPLaneLocationsService locationsService;

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        this.mapper = new ObjectMapper();
        this.locationsService = new HTTPLaneLocationsService(
                HTTPLaneLocationsServiceTest.class.getResource(".").toURI(), this.mapper);
    }

    @Test
    public final void testGetLocationName() throws Exception {
        assertEquals(".Periodicals: A-Z", this.locationsService.getLocationName("LANE-PER"));
        assertEquals(null, this.locationsService.getLocationName("none"));
    }

    @Test(expected = EresourceDatabaseException.class)
    public final void testGetLocationsException() throws Exception {
        this.locationsService = new HTTPLaneLocationsService(new URI("fake://foo"), this.mapper);
    }

    @Test
    public final void testGetLocationUrl() {
        assertEquals(null, this.locationsService.getLocationUrl("LANE-PER"));
        assertEquals(null, this.locationsService.getLocationUrl("none"));
    }
}
