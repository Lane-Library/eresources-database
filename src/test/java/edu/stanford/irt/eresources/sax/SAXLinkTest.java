package edu.stanford.irt.eresources.sax;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;


public class SAXLinkTest {
    
    private SAXLink link;

    @Before
    public void setUp() {
        this.link = new SAXLink();
    }

    @Test
    public void testGetAdditionalText() {
        Version version = createMock(Version.class);
        this.link.setVersion(version);
        this.link.setInstruction("instruction");
        expect(version.getPublisher()).andReturn("publisher");
        replay(version);
        assertEquals(" instruction publisher", this.link.getAdditionalText());
        verify(version);
        assertEquals(" instruction publisher", this.link.getAdditionalText());
    }

    @Test
    public void testSetGetInstruction() {
        this.link.setInstruction("instruction");
        assertEquals("instruction", this.link.getInstruction());
    }

    @Test
    public void testSetGetLabel() {
        this.link.setLabel("label");
        assertEquals("label", this.link.getLabel());
    }

    @Test
    public void testGetLinkText() {
        Version version = createMock(Version.class);
        this.link.setVersion(version);
        expect(version.getSummaryHoldings()).andReturn("summaryHoldings");
        expect(version.getLinks()).andReturn(Collections.<Link>singletonList(this.link));
        expect(version.getDates()).andReturn("dates");
        expect(version.getDescription()).andReturn("description");
        replay(version);
        assertEquals("summaryHoldings, dates description", link.getLinkText());
        verify(version);
        assertEquals("summaryHoldings, dates description", link.getLinkText());
    }

    @Test
    public void testSetGetUrl() {
        this.link.setUrl("url");
        assertEquals("url", this.link.getUrl());
    }
}
