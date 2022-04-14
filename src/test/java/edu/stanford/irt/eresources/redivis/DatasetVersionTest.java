package edu.stanford.irt.eresources.redivis;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class DatasetVersionTest {

    Result dataset;

    DatasetVersion datasetVersion;

    @Before
    public void setUp() throws Exception {
        this.dataset = mock(Result.class);
        this.datasetVersion = new DatasetVersion(this.dataset);
    }

    @Test
    public final void testGetAdditionalText() {
        expect(this.dataset.getAccessLevel()).andReturn("data");
        replay(this.dataset);
        assertNull(this.datasetVersion.getAdditionalText());
        verify(this.dataset);
        reset(this.dataset);
        expect(this.dataset.getAccessLevel()).andReturn("notdata");
        replay(this.dataset);
        assertEquals("Users will need to submit required information to view content",
                this.datasetVersion.getAdditionalText());
        verify(this.dataset);
    }

    @Test
    public final void testGetDates() {
        assertNull(this.datasetVersion.getDates());
    }

    @Test
    public final void testGetHoldingsAndDates() {
        assertNull(this.datasetVersion.getHoldingsAndDates());
    }

    @Test
    public final void testGetLinks() {
        assertNotNull(this.datasetVersion.getLinks());
    }

    @Test
    public final void testGetPublisher() {
        assertNull(this.datasetVersion.getPublisher());
    }

    @Test
    public final void testGetSummaryHoldings() {
        assertNull(this.datasetVersion.getSummaryHoldings());
    }

    @Test
    public final void testIsProxy() {
        assertFalse(this.datasetVersion.isProxy());
    }

    @Test
    public final void testVersionInterface() {
        assertNull(this.datasetVersion.getCallnumber());
        assertNull(this.datasetVersion.getLocationName());
        assertNull(this.datasetVersion.getLocationUrl());
    }
}
