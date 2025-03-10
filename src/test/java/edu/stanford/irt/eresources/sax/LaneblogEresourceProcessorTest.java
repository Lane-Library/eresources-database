package edu.stanford.irt.eresources.sax;

import static org.easymock.EasyMock.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import net.sf.saxon.tree.util.AttributeCollectionImpl;

public class LaneblogEresourceProcessorTest {

    ContentHandler contentHandler;

    LaneblogEresourceProcessor processor;

    @BeforeEach
    public void setUp() {
        this.contentHandler = EasyMock.mock(ContentHandler.class);
        this.processor = new LaneblogEresourceProcessor(
                "file:src/test/resources/edu/stanford/irt/eresources/sax/rss.xml", "user agent", this.contentHandler);
    }

    @Test
    public final void testProcess() throws Exception {
        this.contentHandler.startDocument();
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributesImpl.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.setDocumentLocator(isA(Locator.class));
        EasyMock.expectLastCall();
        this.contentHandler.startPrefixMapping(isA(String.class), isA(String.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributeCollectionImpl.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("\n".toCharArray(), 0, 1);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("\n        ".toCharArray(), 0, 9);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("Mon, 13 Nov 2017 23:59:02 +0000".toCharArray(), 0, 31);
        EasyMock.expectLastCall();
        this.contentHandler.endPrefixMapping(isA(String.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endElement(isA(String.class), isA(String.class), isA(String.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endDocument();
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.replay(this.contentHandler);
        this.processor.process();
        EasyMock.verify(this.contentHandler);
    }

    @Test
    public final void testProcessException() throws Exception {
        this.contentHandler.startDocument();
        EasyMock.expectLastCall().andThrow(new SAXException("foo"));
        EasyMock.replay(this.contentHandler);
        try {
            this.processor.process();
        } catch (EresourceDatabaseException e) {
            assertSame(EresourceDatabaseException.class, e.getClass());
            assertEquals("foo", e.getCause().getMessage());
        }
        EasyMock.verify(this.contentHandler);
    }

    @Test
    public final void testProcessBadXml() {
        this.processor = new LaneblogEresourceProcessor(
                "file:src/test/resources/edu/stanford/irt/eresources/sax/rss-bad.xml", "user agent",
                this.contentHandler);
        this.processor.setFetchIntervalMilliSeconds(2);
        try {
            this.processor.process();
        } catch (EresourceDatabaseException e) {
            assertTrue(e.getMessage().startsWith("Failed to parse XML"));
        }
    }
}
