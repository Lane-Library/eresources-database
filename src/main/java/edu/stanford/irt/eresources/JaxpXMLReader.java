package edu.stanford.irt.eresources;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import edu.stanford.lane.catalog.ParserCreationException;

/**
 * @author ceyates
 */
public class JaxpXMLReader extends XMLFilterImpl {

    // TODO: copied from catalog project only to set the load-external-dtd feature; better way?
    public JaxpXMLReader() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            super.setParent(factory.newSAXParser().getXMLReader());
        } catch (ParserConfigurationException e) {
            throw new ParserCreationException(e);
        } catch (SAXException e) {
            throw new ParserCreationException(e);
        }
    }
}
