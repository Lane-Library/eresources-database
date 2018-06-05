package edu.stanford.irt.eresources.sax;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public class ClassesEresourceProcessor extends AbstractEresourceProcessor {

    private static final String ERESOURCES = "eresources";

    private List<String> allClassesURL;

    private ContentHandler contentHandler;

    private ErrorHandler errorHandler = new DefaultHandler();

    /** the DocumentBuilderFactory. */
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private TransformerFactory tf = TransformerFactory.newInstance();

    public ClassesEresourceProcessor(final List<String> allClassesURL, final ContentHandler contentHandler) {
        this.allClassesURL = new ArrayList<>(allClassesURL);
        this.contentHandler = contentHandler;
    }

    @Override
    public void process() {
        try {
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            for (String urlString : this.allClassesURL) {
                URL url = new URL(urlString);
                InputSource source = new InputSource(url.openConnection().getInputStream());
                process(source);
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (SAXException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void process(final InputSource source) {
        try {
            this.factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder parser = this.factory.newDocumentBuilder();
            parser.setErrorHandler(this.errorHandler);
            Document doc = parser.parse(source);
            this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
