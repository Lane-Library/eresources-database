package edu.stanford.irt.eresources;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import edu.stanford.lane.catalog.ParserCreationException;

/**
 * @author ceyates
 */
public class JaxpXMLReader extends XMLFilterImpl {

    public JaxpXMLReader() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
            super.setParent(factory.newSAXParser().getXMLReader());
        } catch (ParserConfigurationException | SAXException e) {
            throw new ParserCreationException(e);
        }
    }
}
