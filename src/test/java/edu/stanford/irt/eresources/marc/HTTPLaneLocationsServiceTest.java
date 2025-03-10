package edu.stanford.irt.eresources.marc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class HTTPLaneLocationsServiceTest {

    private HTTPLaneLocationsService locationsService;

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() throws Exception {
        this.mapper = new ObjectMapper();
        this.locationsService = new HTTPLaneLocationsService(this.mapper,
                HTTPLaneLocationsServiceTest.class.getResource(".").toURI(), "locations");
    }

    @Test
    public final void testGetLocationName() throws Exception {
        assertEquals(".Periodicals: A-Z", this.locationsService.getLocationName("LANE-PER"));
        assertEquals(null, this.locationsService.getLocationName("none"));
    }

    @Test
    public final void testGetLocationsException() throws Exception {
        assertThrows(EresourceDatabaseException.class, () -> {
            this.locationsService = new HTTPLaneLocationsService(this.mapper, new URI("fake://foo"), "");
        });
    }

    @Test
    public final void testGetLocationUrl() {
        assertEquals(null, this.locationsService.getLocationUrl("LANE-PER"));
        assertEquals(null, this.locationsService.getLocationUrl("none"));
    }
}
