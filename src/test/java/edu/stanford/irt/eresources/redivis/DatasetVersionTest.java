package edu.stanford.irt.eresources.redivis;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatasetVersionTest {

    Result dataset;

    DatasetVersion datasetVersion;

    @BeforeEach
    void setUp() {
        this.dataset = mock(Result.class);
        this.datasetVersion = new DatasetVersion(this.dataset);
    }

    @Test
    final void testGetAdditionalText() {
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
    final void testGetDates() {
        assertNull(this.datasetVersion.getDates());
    }

    @Test
    final void testGetHoldingsAndDates() {
        assertNull(this.datasetVersion.getHoldingsAndDates());
    }

    @Test
    final void testGetLinks() {
        assertNotNull(this.datasetVersion.getLinks());
    }

    @Test
    final void testGetPublisher() {
        assertNull(this.datasetVersion.getPublisher());
    }

    @Test
    final void testGetSummaryHoldings() {
        assertNull(this.datasetVersion.getSummaryHoldings());
    }

    @Test
    final void testIsProxy() {
        assertFalse(this.datasetVersion.isProxy());
    }

    @Test
    final void testVersionInterface() {
        assertNull(this.datasetVersion.getCallnumber());
        assertNull(this.datasetVersion.getLocationName());
        assertNull(this.datasetVersion.getLocationUrl());
    }
}
