package edu.stanford.irt.eresources.gideon;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.DataFetcher;
import edu.stanford.irt.eresources.EresourceDatabaseException;
import net.sf.saxon.tree.util.AttributeCollectionImpl;

public class GideonEresourceProcessorTest {

    private String basePath;

    private ContentHandler contentHandler;

    private DataFetcher dataFetcher;

    private GideonEresourceProcessor processor;

    @Before
    public void setUp() {
        this.contentHandler = createMock(ContentHandler.class);
        this.basePath = new File(GideonEresourceProcessor.class.getResource("good/empty.xml").getPath()).getParent();
        this.dataFetcher = createMock(DataFetcher.class);
        this.processor = new GideonEresourceProcessor(this.basePath, this.dataFetcher, this.contentHandler);
    }

    @Test
    public void testProcess() throws Exception {
        this.contentHandler.startDocument();
        expectLastCall().atLeastOnce();
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributesImpl.class));
        expectLastCall().atLeastOnce();
        this.contentHandler.setDocumentLocator(isA(Locator.class));
        expectLastCall().times(2);
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributeCollectionImpl.class));
        expectLastCall().atLeastOnce();
        // this only works b/c there are no records to process in
        this.contentHandler.endElement(isA(String.class), isA(String.class), isA(String.class));
        expectLastCall().atLeastOnce();
        this.contentHandler.endDocument();
        expectLastCall().atLeastOnce();
        replay(this.contentHandler);
        this.processor.process();
        verify(this.contentHandler);
    }

    @Test
    public void testProcessBadXml() throws Exception {
        this.basePath = new File(GideonEresourceProcessor.class.getResource("bad/bad.xml").getPath()).getParent();
        this.contentHandler.startDocument();
        expectLastCall();
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributesImpl.class));
        expectLastCall().andThrow(new SAXException("bad xml"));
        replay(this.contentHandler);
        EresourceDatabaseException exception = assertThrows(EresourceDatabaseException.class, () -> {
            this.processor.process();
        });
        assertEquals("bad xml", exception.getCause().getMessage());
        verify(this.contentHandler);
    }

    @Test
    public final void testProcessException() throws Exception {
        this.contentHandler.startDocument();
        expectLastCall().andThrow(new SAXException("oops!"));
        replay(this.contentHandler);
        EresourceDatabaseException exception = assertThrows(EresourceDatabaseException.class, () -> {
            this.processor.process();
        });
        assertEquals("oops!", exception.getCause().getMessage());
        verify(this.contentHandler);
    }

    @Test
    public void testProcessNullBasePath() {
        this.processor = new GideonEresourceProcessor(null, this.dataFetcher, this.contentHandler);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            this.processor.process();
        });
        assertNotNull(exception);
    }

    @Test
    public void testProcessNullContentHandler() {
        this.processor = new GideonEresourceProcessor(this.basePath, this.dataFetcher, null);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            this.processor.process();
        });
        assertNotNull(exception);
    }

    @Test
    public void testProcessNullDataFetcher() {
        this.processor = new GideonEresourceProcessor(this.basePath, null, this.contentHandler);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            this.processor.process();
        });
        assertNotNull(exception);
    }
}