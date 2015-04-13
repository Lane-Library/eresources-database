package edu.stanford.irt.eresources.sax;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public class LaneblogEresourceProcessor extends AbstractEresourceProcessor {

    private static final String ERESOURCES = "eresources";

    private ContentHandler contentHandler;

    private DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

    private ErrorHandler errorHandler = new DefaultHandler();

    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private String rssURL;

    private TransformerFactory tf = TransformerFactory.newInstance();

    private long getUpdateDate(final Document doc) {
        NodeList nodeList = doc.getElementsByTagName("lastBuildDate");
        Element lastBuildDateEl = (Element) nodeList.item(0);
        try {
            Date lastBuildDate = this.dateFormat.parse(lastBuildDateEl.getTextContent());
            return lastBuildDate.getTime();
        } catch (DOMException | ParseException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    @Override
    public void process() {
        try {
            URL url;
            url = new URL(this.rssURL);
            InputSource source;
            source = new InputSource(url.openConnection().getInputStream());
            DocumentBuilder parser;
            parser = this.factory.newDocumentBuilder();
            parser.setErrorHandler(this.errorHandler);
            Document doc = parser.parse(source);
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            if (getUpdateDate(doc) > getStartTime()) {
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

    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public void setRssURL(final String allClassesURL) {
        this.rssURL = allClassesURL;
    }
}
