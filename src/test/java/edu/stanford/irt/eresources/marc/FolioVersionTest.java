package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.lane.catalog.FolioRecord;

public class FolioVersionTest {

    private Eresource eresource;

    private HTTPLaneLocationsService locationsService;

    private FolioRecord record;

    private FolioVersion version;

    @Before
    public void setUp() throws Exception {
        this.record = new FolioRecord(FolioVersionTest.class.getResourceAsStream("folio-record.json").readAllBytes());
        this.eresource = mock(Eresource.class);
        this.locationsService = mock(HTTPLaneLocationsService.class);
        this.version = new FolioVersion(this.record, this.record.getHoldings().get(0), this.eresource,
                this.locationsService);
    }

    @Test
    public void testGetAdditionalText() {
        assertNull(this.version.getAdditionalText());
    }

    @Test
    public void testGetCallNumber() {
        assertEquals("test CN", this.version.getCallnumber());
    }

    @Test
    public void testGetDates() {
        assertEquals("1900-", this.version.getDates());
    }

    @Test
    public void testGetDatesFromBib() throws Exception {
        this.record = new FolioRecord(
                FolioVersionTest.class.getResourceAsStream("folio-record-book.json").readAllBytes());
        this.version = new FolioVersion(this.record, this.record.getHoldings().get(0), this.eresource,
                this.locationsService);
        expect(this.eresource.getPublicationText()).andReturn("");
        expect(this.eresource.getPrimaryType()).andReturn("Book");
        replay(this.eresource);
        assertEquals("[2018]", this.version.getDates());
        verify(this.eresource);
    }

    @Test
    public void testGetItemCount() {
        int[] count = this.version.getItemCount();
        assertEquals(1, count[0]);
        assertEquals(1, count[1]);
    }

    @Test
    public void testGetLinks() {
        assertEquals(2, this.version.getLinks().size());
        assertEquals("https://test.com", this.version.getLinks().get(0).getUrl());
    }

    @Test
    public void testGetLinksHasNoLinks() throws Exception {
        this.record = new FolioRecord(
                FolioVersionTest.class.getResourceAsStream("folio-record-equipment.json").readAllBytes());
        this.version = new FolioVersion(this.record, this.record.getHoldings().get(0), this.eresource,
                this.locationsService);
        expect(this.eresource.getRecordId()).andReturn("123").times(2);
        replay(this.eresource);
        assertEquals(1, this.version.getLinks().size());
        assertEquals("https://searchworks.stanford.edu/view/L123", this.version.getLinks().get(0).getUrl());
        verify(this.eresource);
    }

    @Test
    public void testGetLocationName() {
        expect(this.locationsService.getLocationName("LANE-ECOLL")).andReturn("name");
        replay(this.locationsService);
        assertEquals("name", this.version.getLocationName());
        assertEquals("name", this.version.getLocationName());
        verify(this.locationsService);
    }

    @Test
    public void testGetLocationUrl() {
        expect(this.locationsService.getLocationUrl("LANE-ECOLL")).andReturn("url");
        replay(this.locationsService);
        assertEquals("url", this.version.getLocationUrl());
        assertEquals("url", this.version.getLocationUrl());
        verify(this.locationsService);
    }

    @Test
    public void testGetPublisher() {
        assertNull(this.version.getPublisher());
    }

    @Test
    public void testGetSummaryHoldings() {
        assertEquals("v. 1-", this.version.getSummaryHoldings());
    }

    @Test
    public void testIsProxy() {
        assertTrue(this.version.isProxy());
    }

}
