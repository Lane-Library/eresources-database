package edu.stanford.irt.eresources.pubmed;

import static org.easymock.EasyMock.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.xml.XMLConstants;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import edu.stanford.irt.eresources.EresourceDatabaseException;

class PubmedEresourceProcessorTest {

    PubmedEresourceProcessor processor;

    XMLReader xmlReader;

    @BeforeEach
    void setUp() {
        this.xmlReader = EasyMock.mock(XMLReader.class);
        this.processor = new PubmedEresourceProcessor("src/test/resources/edu/stanford/irt/eresources/pubmed",
                this.xmlReader);
    }

    @Test
    final void testException() throws Exception {
        this.xmlReader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        this.xmlReader.parse(isA(InputSource.class));
        EasyMock.expectLastCall().andThrow(new SAXException("sax exception"));
        EasyMock.replay(this.xmlReader);
        assertThrows(EresourceDatabaseException.class, () -> {
            this.processor.process();
        });
        EasyMock.verify(this.xmlReader);

    }

    @Test
    final void testNullBasePath() throws Exception {
        this.processor = new PubmedEresourceProcessor(null, this.xmlReader);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            this.processor.process();
        });
        assertEquals("null basePath", ex.getMessage());
    }

    @Test
    final void testNullXmlReader() throws Exception {
        this.processor = new PubmedEresourceProcessor("src/test/resources/edu/stanford/irt/eresources", null);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            this.processor.process();
        });
        assertEquals("null xmlReader", ex.getMessage());
    }

    @Test
    final void testProcessor() throws Exception {
        this.xmlReader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        EasyMock.expectLastCall().times(2);
        this.xmlReader.parse(isA(InputSource.class));
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(this.xmlReader);
        this.processor.process();
        EasyMock.verify(this.xmlReader);
    }
}
