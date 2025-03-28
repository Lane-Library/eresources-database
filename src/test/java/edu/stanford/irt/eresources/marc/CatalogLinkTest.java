package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.marc.CatalogLink.Type;

class CatalogLinkTest {

    CatalogLink link;

    Version version;

    @BeforeEach
    void setUp() {
        this.version = mock(Version.class);
        this.link = new CatalogLink(Type.BIB, "recordId", this.version);
    }

    @Test
    final void testCatalogLink() {
        assertNotNull(this.link);
    }

    @Test
    final void testGetAdditionalText() {
        assertNull(this.link.getAdditionalText());
    }

    @Test
    final void testGetLabel() {
        assertEquals("Lane Record in SearchWorks", this.link.getLabel());
    }

    @Test
    final void testGetLinkText() {
        expect(this.version.getHoldingsAndDates()).andReturn("HoldingsAndDates");
        expect(this.version.getLinks()).andReturn(Collections.emptyList());
        replay(this.version);
        assertEquals("Lane Record in SearchWorks", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    final void testGetLinkTextWithHoldings() {
        expect(this.version.getHoldingsAndDates()).andReturn("HoldingsAndDates");
        expect(this.version.getLinks()).andReturn(Collections.singletonList(this.link));
        replay(this.version);
        assertEquals("HoldingsAndDates", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    final void testGetUrl() {
        assertEquals("https://searchworks.stanford.edu/view/LrecordId", this.link.getUrl());
    }
}
