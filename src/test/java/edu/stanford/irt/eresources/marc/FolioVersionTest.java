package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.lane.catalog.FolioRecord;

class FolioVersionTest {

    private Eresource eresource;

    private HTTPLaneLocationsService locationsService;

    private FolioRecord rec;

    private FolioVersion version;

    @BeforeEach
    void setUp() throws Exception {
        this.rec = new FolioRecord(FolioVersionTest.class.getResourceAsStream("folio-record.json").readAllBytes());
        this.eresource = mock(Eresource.class);
        this.locationsService = mock(HTTPLaneLocationsService.class);
        this.version = new FolioVersion(this.rec.getHoldings().get(0), this.eresource, this.locationsService);
    }

    @Test
    void testGetAdditionalText() {
        assertNull(this.version.getAdditionalText());
    }

    @Test
    void testGetCallNumber() {
        assertEquals("test CN", this.version.getCallnumber());
    }

    @Test
    void testGetDates() {
        assertEquals("1900-", this.version.getDates());
    }

    @Test
    void testGetItemCount() {
        int[] count = this.version.getItemCount();
        assertEquals(1, count[0]);
        assertEquals(1, count[1]);
    }

    @Test
    void testGetLinks() {
        assertEquals(2, this.version.getLinks().size());
        assertEquals("https://test.com", this.version.getLinks().get(0).getUrl());
    }

    @Test
    void testGetLocationName() {
        expect(this.locationsService.getLocationName("LANE-ECOLL")).andReturn("name");
        replay(this.locationsService);
        assertEquals("name", this.version.getLocationName());
        assertEquals("name", this.version.getLocationName());
        verify(this.locationsService);
    }

    @Test
    void testGetLocationUrl() {
        expect(this.locationsService.getLocationUrl("LANE-ECOLL")).andReturn("url");
        replay(this.locationsService);
        assertEquals("url", this.version.getLocationUrl());
        assertEquals("url", this.version.getLocationUrl());
        verify(this.locationsService);
    }

    @Test
    void testGetPublisher() {
        assertNull(this.version.getPublisher());
    }

    @Test
    void testGetSummaryHoldings() {
        assertEquals(this.version.getDates(), this.version.getSummaryHoldings());
    }

    @Test
    void testIsProxy() {
        assertTrue(this.version.isProxy());
    }
}
