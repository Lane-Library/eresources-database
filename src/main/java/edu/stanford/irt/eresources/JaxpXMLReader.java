package edu.stanford.irt.eresources;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * @author ceyates
 */
public class JaxpXMLReader extends XMLFilterImpl {

    public JaxpXMLReader() {
        // network requests to NCBI intermittently fail, so cache DTDs
        // http://xerces.apache.org/xerces2-j/faq-grammars.html#faq-4
        System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration",
                "org.apache.xerces.parsers.XMLGrammarCachingConfiguration");
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
            super.setParent(factory.newSAXParser().getXMLReader());
        } catch (ParserConfigurationException | SAXException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
