package edu.stanford.irt.eresources.sax;

import static org.easymock.EasyMock.isA;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import net.sf.saxon.tree.util.AttributeCollectionImpl;

public class LibGuideEresourceProcessorTest {

    ContentHandler contentHandler;

    LibGuideEresourceProcessor processor;

    @BeforeEach
    public void setUp() throws Exception {
        this.contentHandler = EasyMock.mock(ContentHandler.class);
        this.processor = new LibGuideEresourceProcessor(
                "file:src/test/resources/edu/stanford/irt/eresources/sax/oai-pmh-libguides.xml",
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
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributeCollectionImpl.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("\n".toCharArray(), 0, 1);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("Subject Guide".toCharArray(), 0, 13);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("\n\n".toCharArray(), 0, 2);
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
        assertThrows(EresourceDatabaseException.class, () -> {
            this.processor.process();
            EasyMock.verify(this.contentHandler);
        });
    }

    @Test
    public final void testProcessNullBasepath() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            (new LibGuideEresourceProcessor(null, this.contentHandler)).process();
        });
    }

    @Test
    public final void testProcessNullContentHandler() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            (new LibGuideEresourceProcessor("", null)).process();
        });
    }
}
