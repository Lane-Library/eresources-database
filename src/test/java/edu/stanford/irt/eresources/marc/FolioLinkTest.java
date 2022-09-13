package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.FolioRecord;

public class FolioLinkTest {

    private FolioLink link;

    private FolioRecord record;

    private Version version;

    @Before
    public void setUp() throws Exception {
        this.record = new FolioRecord(FolioLinkTest.class.getResourceAsStream("folio-record.json").readAllBytes());
        this.version = mock(Version.class);
        this.link = new FolioLink(((List<Map>) this.record.getHoldings().get(0).get("electronicAccess")).get(0),
                this.version);
    }

    @Test
    public void testGetAdditionalText() {
        assertNull(this.link.getAdditionalText());
    }

    @Test
    public void testGetLabel() {
        assertEquals("linkText", this.link.getLabel());
    }

    @Test
    public void testGetLinkText() {
        expect(this.version.getHoldingsAndDates()).andReturn("");
        expect(this.version.getLinks()).andReturn(Collections.emptyList());
        replay(this.version);
        assertEquals("linkText", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    public void testGetUrl() {
        assertEquals("https://test.com", this.link.getUrl());
    }

    @Test
    public void testIsRelatedResourceLink() {
        assertFalse(this.link.isRelatedResourceLink());
    }

    @Test
    public void testIsResourceLink() {
        assertTrue(this.link.isResourceLink());
    }
}
