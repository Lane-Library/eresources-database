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
        expect(version.getAdditionalText()).andReturn("additional text");
        replay(version);
        assertEquals("publisher, additional text, instruction", this.link.getAdditionalText());
        verify(version);
        assertEquals("publisher, additional text, instruction", this.link.getAdditionalText());
    }

    @Test
    public void testGetLinkText() {
        Version version = createMock(Version.class);
        this.link.setVersion(version);
        expect(version.getHoldingsAndDates()).andReturn("summaryHoldings, dates");
        expect(version.getLinks()).andReturn(Collections.<Link> singletonList(this.link));
        replay(version);
        assertEquals("summaryHoldings, dates", this.link.getLinkText());
        verify(version);
        assertEquals("summaryHoldings, dates", this.link.getLinkText());
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
    public void testSetGetUrl() {
        this.link.setUrl("url");
        assertEquals("url", this.link.getUrl());
    }
}
