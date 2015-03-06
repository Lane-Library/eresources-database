package edu.stanford.irt.eresources.sax;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.stanford.irt.eresources.Eresource;

public class DefaultEresourceBuilderTest {

    private Attributes attributes;

    private DefaultEresourceBuilder builder;

    private EresourceHandler eresourceHandler;

    @Before
    public void setUp() {
        this.eresourceHandler = createMock(EresourceHandler.class);
        this.builder = new DefaultEresourceBuilder(this.eresourceHandler);
        this.attributes = createMock(Attributes.class);
    }

    @Test
    public void test() throws SAXException, IOException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setContentHandler(this.builder);
        reader.parse(getClass().getResource("eresources.xml").toExternalForm());
    }

    @Test
    public void testCharacters() throws SAXException {
        this.builder.characters("characters".toCharArray(), 0, "characters".length());
    }

    @Test
    public void testStartEndEresource() throws SAXException, InterruptedException {
        expect(this.attributes.getValue("id")).andReturn("1");
        expect(this.attributes.getValue("type")).andReturn("type");
        expect(this.attributes.getValue("update")).andReturn("19550519120000");
        this.eresourceHandler.handleEresource(isA(Eresource.class));
        replay(this.attributes, this.eresourceHandler);
        this.builder.startElement("", "", "eresource", this.attributes);
        this.builder.endElement("", "", "eresource");
        verify(this.attributes, this.eresourceHandler);
    }
}
