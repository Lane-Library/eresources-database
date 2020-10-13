package edu.stanford.irt.eresources.pubmed;

import static org.easymock.EasyMock.isA;

import javax.xml.XMLConstants;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class PubmedEresourceProcessorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    PubmedEresourceProcessor processor;

    XMLReader xmlReader;

    @Before
    public void setUp() throws Exception {
        this.xmlReader = EasyMock.mock(XMLReader.class);
        this.processor = new PubmedEresourceProcessor("src/test/resources/edu/stanford/irt/eresources/pubmed", this.xmlReader);
    }

    @Test(expected = EresourceDatabaseException.class)
    public final void testException() throws Exception {
        this.xmlReader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        this.xmlReader.parse(isA(InputSource.class));
        EasyMock.expectLastCall().andThrow(new SAXException("sax exception"));
        EasyMock.replay(this.xmlReader);
        this.processor.process();
        EasyMock.verify(this.xmlReader);
    }

    @Test
    public final void testNullBasePath() throws Exception {
        this.processor = new PubmedEresourceProcessor(null, this.xmlReader);
        this.thrown.expect(IllegalStateException.class);
        this.thrown.expectMessage("null basePath");
        this.processor.process();
    }

    @Test
    public final void testNullXmlReader() throws Exception {
        this.processor = new PubmedEresourceProcessor("src/test/resources/edu/stanford/irt/eresources", null);
        this.thrown.expect(IllegalStateException.class);
        this.thrown.expectMessage("null xmlReader");
        this.processor.process();
    }

    @Test
    public final void testProcessor() throws Exception {
        this.xmlReader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        EasyMock.expectLastCall().times(2);
        this.xmlReader.parse(isA(InputSource.class));
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(this.xmlReader);
        this.processor.process();
        EasyMock.verify(this.xmlReader);
    }
}
