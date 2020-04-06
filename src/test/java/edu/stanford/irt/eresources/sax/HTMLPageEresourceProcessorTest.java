package edu.stanford.irt.eresources.sax;

import static org.easymock.EasyMock.isA;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import net.sf.saxon.tree.util.AttributeCollectionImpl;

public class HTMLPageEresourceProcessorTest {

    ContentHandler contentHandler;

    HTMLPageEresourceProcessor processor;

    @Before
    public void setUp() throws Exception {
        this.contentHandler = EasyMock.mock(ContentHandler.class);
        this.processor = new HTMLPageEresourceProcessor("src/test/resources/edu/stanford/irt/eresources/sax/",
                this.contentHandler);
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
        this.contentHandler.characters("Page".toCharArray(), 0, 4);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("\n\n".toCharArray(), 0, 2);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endPrefixMapping("");
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endElement(isA(String.class), isA(String.class), isA(String.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endDocument();
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.replay(this.contentHandler);
        this.processor.process();
        EasyMock.verify(this.contentHandler);
    }

    @Test(expected = EresourceDatabaseException.class)
    public final void testProcessException() throws Exception {
        this.contentHandler.startDocument();
        EasyMock.expectLastCall().andThrow(new SAXException("foo"));
        EasyMock.replay(this.contentHandler);
        this.processor.process();
        EasyMock.verify(this.contentHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testProcessNullBasepath() throws Exception {
        (new HTMLPageEresourceProcessor(null, this.contentHandler)).process();
        ;
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testProcessNullContentHandler() throws Exception {
        (new HTMLPageEresourceProcessor("", null)).process();
        ;
    }
}
