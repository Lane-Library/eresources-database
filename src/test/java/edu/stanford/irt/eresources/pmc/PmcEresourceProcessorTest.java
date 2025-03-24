package edu.stanford.irt.eresources.pmc;

import static org.easymock.EasyMock.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import net.sf.saxon.tree.util.AttributeCollectionImpl;

public class PmcEresourceProcessorTest {

    ContentHandler contentHandler;

    LaneDedupAugmentation laneDedupAugmentation;

    PmcEresourceProcessor processor;

    @BeforeEach
    public void setUp() throws Exception {
        this.contentHandler = EasyMock.mock(ContentHandler.class);
        this.laneDedupAugmentation = EasyMock.mock(LaneDedupAugmentation.class);
        this.processor = new PmcEresourceProcessor(PmcEresourceProcessorTest.class.getResource(".").toExternalForm(),
                PmcEresourceProcessorTest.class.getResource("jlist.csv").toExternalForm(), this.contentHandler,
                this.laneDedupAugmentation, "key");
    }

    @Test
    public final void testBadEutilsUrl() throws Exception {
        this.processor = new PmcEresourceProcessor("file:/",
                PmcEresourceProcessorTest.class.getResource("jlist.csv").toExternalForm(), this.contentHandler,
                this.laneDedupAugmentation, "key");
        this.contentHandler.startDocument();
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributesImpl.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.setDocumentLocator(isA(Locator.class));
        EasyMock.expectLastCall();
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("dnlm", "101565857")).andReturn(false);
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("issn", "2190572x")).andReturn(false);
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("issn", "21905738")).andReturn(false);
        EasyMock.replay(this.contentHandler, this.laneDedupAugmentation);
        EresourceDatabaseException ex = assertThrows(EresourceDatabaseException.class, () -> {
            this.processor.process();
        });
        assertEquals("java.io.FileNotFoundException: /esearch.fcgi (No such file or directory)", ex.getMessage());
    }

    @Test
    public final void testBadFetchUrl() throws Exception {
        this.processor = new PmcEresourceProcessor("[]",
                PmcEresourceProcessorTest.class.getResource("jlist.csv").toExternalForm(), this.contentHandler,
                this.laneDedupAugmentation, "key");
        this.contentHandler.startDocument();
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributesImpl.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.setDocumentLocator(isA(Locator.class));
        EasyMock.expectLastCall();
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("dnlm", "101565857")).andReturn(false);
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("issn", "2190572x")).andReturn(false);
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("issn", "21905738")).andReturn(false);
        EasyMock.replay(this.contentHandler, this.laneDedupAugmentation);
        EresourceDatabaseException ex = assertThrows(EresourceDatabaseException.class, () -> {
            this.processor.process();
        });
        assertTrue(ex.getLocalizedMessage().contains("Illegal character"));
    }

    @Test
    public final void testBadSearchXml() throws Exception {
        this.processor = new PmcEresourceProcessor(
                PmcEresourceProcessorTest.class.getResource("./bad-xml/").toExternalForm(),
                PmcEresourceProcessorTest.class.getResource("jlist.csv").toExternalForm(), this.contentHandler,
                this.laneDedupAugmentation, "key");
        this.contentHandler.startDocument();
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributesImpl.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.setDocumentLocator(isA(Locator.class));
        EasyMock.expectLastCall();
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("dnlm", "101565857")).andReturn(false);
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("issn", "2190572x")).andReturn(false);
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("issn", "21905738")).andReturn(false);
        EasyMock.replay(this.contentHandler, this.laneDedupAugmentation);
        EresourceDatabaseException ex = assertThrows(EresourceDatabaseException.class, () -> {
            this.processor.process();
        });
        assertTrue(ex.getLocalizedMessage().contains("efetch.fcgi"));
    }

    @Test
    public final void testNullContentHandler() throws Exception {
        this.processor = new PmcEresourceProcessor(null, "url", null, null, null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            this.processor.process();
        });
        assertEquals("null contentHandler", ex.getMessage());
    }

    @Test
    public final void testNullJournalsUrl() throws Exception {
        this.processor = new PmcEresourceProcessor(null, null, this.contentHandler, null, null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            this.processor.process();
        });
        assertEquals("null allJournalsCsvUrl", ex.getMessage());
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
        this.contentHandler.characters("101565857".toCharArray(), 0, 9);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("3 Biotech.".toCharArray(), 0, 10);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("3 Biotech".toCharArray(), 0, 9);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("Three biotech".toCharArray(), 0, 13);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("2011".toCharArray(), 0, 4);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("9999".toCharArray(), 0, 4);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.characters("Biotechnology".toCharArray(), 0, 13);
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endElement(isA(String.class), isA(String.class), isA(String.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endDocument();
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("dnlm", "101565857")).andReturn(false);
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("issn", "2190572x")).andReturn(false);
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("issn", "21905738")).andReturn(false);
        EasyMock.replay(this.contentHandler, this.laneDedupAugmentation);
        this.processor.process();
        EasyMock.verify(this.contentHandler, this.laneDedupAugmentation);
    }

    @Test
    public final void testProcessDup1() throws Exception {
        this.contentHandler.startDocument();
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributesImpl.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endElement(isA(String.class), isA(String.class), isA(String.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endDocument();
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("dnlm", "101565857")).andReturn(true);
        EasyMock.replay(this.contentHandler, this.laneDedupAugmentation);
        this.processor.process();
        EasyMock.verify(this.contentHandler, this.laneDedupAugmentation);
    }

    @Test
    public final void testProcessDup2() throws Exception {
        this.contentHandler.startDocument();
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributesImpl.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endElement(isA(String.class), isA(String.class), isA(String.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endDocument();
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("dnlm", "101565857")).andReturn(false);
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("issn", "21905738")).andReturn(true);
        EasyMock.replay(this.contentHandler, this.laneDedupAugmentation);
        this.processor.process();
        EasyMock.verify(this.contentHandler, this.laneDedupAugmentation);
    }

    @Test
    public final void testProcessDup3() throws Exception {
        this.contentHandler.startDocument();
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.startElement(isA(String.class), isA(String.class), isA(String.class),
                isA(AttributesImpl.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endElement(isA(String.class), isA(String.class), isA(String.class));
        EasyMock.expectLastCall().atLeastOnce();
        this.contentHandler.endDocument();
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("dnlm", "101565857")).andReturn(false);
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("issn", "21905738")).andReturn(false);
        EasyMock.expect(this.laneDedupAugmentation.isDuplicate("issn", "2190572x")).andReturn(true);
        EasyMock.replay(this.contentHandler, this.laneDedupAugmentation);
        this.processor.process();
        EasyMock.verify(this.contentHandler, this.laneDedupAugmentation);
    }
}
