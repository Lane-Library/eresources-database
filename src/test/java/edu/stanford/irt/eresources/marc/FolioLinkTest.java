package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.FolioRecord;

class FolioLinkTest {

    private FolioLink link;

    private FolioRecord rec;

    private Version version;

    @BeforeEach
    void setUp() throws Exception {
        this.rec = new FolioRecord(FolioLinkTest.class.getResourceAsStream("folio-record.json").readAllBytes());
        this.version = mock(Version.class);
        this.link = new FolioLink(((List<Map>) this.rec.getHoldings().get(0).get("electronicAccess")).get(0),
                this.version);
    }

    @Test
    void testGetAdditionalText() {
        assertNull(this.link.getAdditionalText());
    }

    @Test
    void testGetLabel() {
        assertEquals("linkText", this.link.getLabel());
    }

    @Test
    void testGetLinkText() {
        expect(this.version.getHoldingsAndDates()).andReturn("");
        expect(this.version.getLinks()).andReturn(Collections.emptyList());
        replay(this.version);
        assertEquals("linkText", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    void testGetUrl() {
        assertEquals("https://test.com", this.link.getUrl());
    }

    @Test
    void testIsRelatedResourceLink() {
        assertFalse(this.link.isRelatedResourceLink());
    }

    @Test
    void testIsResourceLink() {
        assertTrue(this.link.isResourceLink());
    }
}
