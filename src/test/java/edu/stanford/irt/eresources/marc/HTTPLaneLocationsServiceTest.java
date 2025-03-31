package edu.stanford.irt.eresources.marc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.EresourceDatabaseException;

class HTTPLaneLocationsServiceTest {

    private HTTPLaneLocationsService locationsService;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        this.mapper = new ObjectMapper();
        this.locationsService = new HTTPLaneLocationsService(this.mapper,
                HTTPLaneLocationsServiceTest.class.getResource(".").toURI(), "locations");
    }

    @Test
    final void testGetLocationName() {
        assertEquals(".Periodicals: A-Z", this.locationsService.getLocationName("LANE-PER"));
        assertEquals(null, this.locationsService.getLocationName("none"));
    }

    @Test
    final void testGetLocationsException() throws Exception {
        URI fakeUri = new URI("fake://foo");
        assertThrows(EresourceDatabaseException.class, () -> {
            this.locationsService = new HTTPLaneLocationsService(this.mapper, fakeUri, "");
        });
    }

    @Test
    final void testGetLocationUrl() {
        assertEquals(null, this.locationsService.getLocationUrl("LANE-PER"));
        assertEquals(null, this.locationsService.getLocationUrl("none"));
    }
}
