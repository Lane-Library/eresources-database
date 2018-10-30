package edu.stanford.irt.eresources.redivis;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class DatasetLinkTest {

    private Dataset dataset;

    private DatasetLink datasetLink;

    private DatasetVersion version;

    @Before
    public void setUp() throws Exception {
        this.version = mock(DatasetVersion.class);
        this.dataset = mock(Dataset.class);
        this.datasetLink = new DatasetLink(this.dataset, this.version);
    }

    @Test
    public final void testGetAdditionalText() {
        assertNull(this.datasetLink.getAdditionalText());
    }

    @Test
    public final void testGetLabel() {
        assertEquals("Redivis", this.datasetLink.getLabel());
    }

    @Test
    public final void testGetLinkText() {
        expect(this.version.getDates()).andReturn("dates");
        replay(this.version);
        assertEquals("dates", this.datasetLink.getLinkText());
        verify(this.version);
    }

    @Test
    public final void testGetUrl() {
        expect(this.dataset.getUrl()).andReturn("url");
        replay(this.dataset);
        assertEquals("url", this.datasetLink.getUrl());
        verify(this.dataset);
    }
}
