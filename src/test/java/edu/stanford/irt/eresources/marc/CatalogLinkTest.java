package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Version;

public class CatalogLinkTest {

    CatalogLink link;

    Version version;

    @Before
    public void setUp() throws Exception {
        this.version = mock(Version.class);
        this.link = new CatalogLink("recordId", this.version, "url", "label");
    }

    @Test
    public final void testCatalogLink() {
        assertNotNull(this.link);
    }

    @Test
    public final void testGetAdditionalText() {
        assertNull(this.link.getAdditionalText());
    }

    @Test
    public final void testGetLabel() {
        assertEquals("label", this.link.getLabel());
    }

    @Test
    public final void testGetLinkText() {
        expect(this.version.getHoldingsAndDates()).andReturn("HoldingsAndDates");
        expect(this.version.getLinks()).andReturn(Collections.emptyList());
        replay(this.version);
        assertEquals("label", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    public final void testGetLinkTextImpactFactor() {
        assertEquals("Impact Factor",
                (new CatalogLink("recordId", this.version, "url", "imPact faCtor")).getLinkText());
    }

    @Test
    public final void testGetLinkTextWithHoldings() {
        expect(this.version.getHoldingsAndDates()).andReturn("HoldingsAndDates");
        expect(this.version.getLinks()).andReturn(Collections.singletonList(this.link));
        replay(this.version);
        assertEquals("HoldingsAndDates", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    public final void testGetUrl() {
        assertEquals("urlrecordId", this.link.getUrl());
    }
}
