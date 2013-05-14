package edu.stanford.irt.eresources;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
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

public class ClassesEresourceProcessor extends AbstractEresourceProcessor {
    
    private static final String ERESOURCES = "eresources";

    private String allClassesURL;

    private ContentHandler contentHandler;

    private ErrorHandler errorHandler = new DefaultHandler();

    /** the DocumentBuilderFactory. */
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private TransformerFactory tf = TransformerFactory.newInstance();

    public void process() {
        try {
            URL url;
            url = new URL(this.allClassesURL);
            InputSource source;
            source = new InputSource(url.openConnection().getInputStream());
            DocumentBuilder parser;
            parser = this.factory.newDocumentBuilder();
            parser.setErrorHandler(this.errorHandler);
            Document doc = parser.parse(source);
            Date sometimeEarlier = new Date(1);
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            if (sometimeEarlier.getTime() > this.startDate.getTime()) {
                this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (SAXException e) {
            throw new EresourceDatabaseException(e);
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        } catch (ParserConfigurationException e) {
            throw new EresourceDatabaseException(e);
        } catch (TransformerConfigurationException e) {
            throw new EresourceDatabaseException(e);
        } catch (TransformerException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void setAllClassesURL(final String allClassesURL) {
        this.allClassesURL = allClassesURL;
    }

    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }
}
