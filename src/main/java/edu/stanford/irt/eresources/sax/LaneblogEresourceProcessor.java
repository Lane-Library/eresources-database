package edu.stanford.irt.eresources.sax;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

    protected static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("EEE, d MMM yyyy HH:mm:ss Z").toFormatter();

    private static final String ERESOURCES = "eresources";

    private ContentHandler contentHandler;

    private ErrorHandler errorHandler = new DefaultHandler();

    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private String rssURL;

    private String rssUserAgent;

    private TransformerFactory tf = TransformerFactory.newInstance();

    public LaneblogEresourceProcessor(final String rssURL, final String rssUserAgent,
            final ContentHandler contentHandler) {
        this.rssURL = rssURL;
        this.rssUserAgent = rssUserAgent;
        this.contentHandler = contentHandler;
    }

    @Override
    public void process() {
        try {
            URL url = new URL(this.rssURL);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", this.rssUserAgent);
            InputSource source = new InputSource(con.getInputStream());
            this.factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder parser = this.factory.newDocumentBuilder();
            parser.setErrorHandler(this.errorHandler);
            Document doc = parser.parse(source);
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            if (getUpdateDate(doc) > getStartTime()) {
                this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    private long getUpdateDate(final Document doc) {
        NodeList nodeList = doc.getElementsByTagName("lastBuildDate");
        Element lastBuildDateEl = (Element) nodeList.item(0);
        try {
            LocalDateTime ldt = LocalDateTime.parse(lastBuildDateEl.getTextContent().trim(), FORMATTER);
            return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (DOMException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
