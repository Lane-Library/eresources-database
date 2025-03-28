package edu.stanford.irt.eresources.redivis;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatasetLinkTest {

    private Result dataset;

    private DatasetLink datasetLink;

    private DatasetVersion version;

    @BeforeEach
    void setUp() {
        this.version = mock(DatasetVersion.class);
        this.dataset = mock(Result.class);
        this.datasetLink = new DatasetLink(this.dataset, this.version);
    }

    @Test
    final void testGetAdditionalText() {
        assertNull(this.datasetLink.getAdditionalText());
    }

    @Test
    final void testGetLabel() {
        assertEquals("Redivis", this.datasetLink.getLabel());
    }

    @Test
    final void testGetLinkText() {
        expect(this.version.getDates()).andReturn("dates");
        replay(this.version);
        assertEquals("dates", this.datasetLink.getLinkText());
        verify(this.version);
    }

    @Test
    final void testGetUrl() {
        expect(this.dataset.getUrl()).andReturn("url");
        replay(this.dataset);
        assertEquals("url", this.datasetLink.getUrl());
        verify(this.dataset);
    }
}
