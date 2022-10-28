package edu.stanford.irt.eresources.pmc;

import static org.easymock.EasyMock.isA;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import net.sf.saxon.tree.util.AttributeCollectionImpl;

public class PmcEresourceProcessorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    ContentHandler contentHandler;

    LaneDedupAugmentation laneDedupAugmentation;

    PmcEresourceProcessor processor;

    @Before
    public void setUp() throws Exception {
        this.contentHandler = EasyMock.mock(ContentHandler.class);
        this.laneDedupAugmentation = EasyMock.mock(LaneDedupAugmentation.class);
        this.processor = new PmcEresourceProcessor(
                PmcEresourceProcessorTest.class.getResource("efetch.xml").toExternalForm(),
                PmcEresourceProcessorTest.class.getResource("jlist.csv").toExternalForm(), this.contentHandler,
                this.laneDedupAugmentation, "key");
    }

    @Test
    public final void testNullContentHandler() throws Exception {
        this.processor = new PmcEresourceProcessor(null, "url", null, null, null);
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("null contentHandler");
        this.processor.process();
    }

    @Test
    public final void testNullJournalsUrl() throws Exception {
        this.processor = new PmcEresourceProcessor(null, null, this.contentHandler, null, null);
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("null allJournalsCsvUrl");
        this.processor.process();
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
}
