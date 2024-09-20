package edu.stanford.irt.eresources.sax;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.stream.Collectors;

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

    private static final String ERESOURCES = "eresources";

    protected static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("EEE, d MMM yyyy HH:mm:ss Z").toFormatter();

    private ContentHandler contentHandler;

    private ErrorHandler errorHandler = new DefaultHandler();

    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private String rssURL;

    private String rssUserAgent;

    private TransformerFactory tf = TransformerFactory.newInstance();

    private int fetchIntervalMilliSeconds = 5_0000;

    private static final int FETCH_RETRIES = 3;

    public LaneblogEresourceProcessor(final String rssURL, final String rssUserAgent,
            final ContentHandler contentHandler) {
        this.rssURL = rssURL;
        this.rssUserAgent = rssUserAgent;
        this.contentHandler = contentHandler;
    }

    // for unit testing
    public void setFetchIntervalMilliSeconds(final int fetchIntervalMilliSeconds) {
        this.fetchIntervalMilliSeconds = fetchIntervalMilliSeconds;
    }

    private Document fetchDocument() {
        int retryCount = 0;
        Document doc = null;
        String xmlContent = "";
        while (retryCount < FETCH_RETRIES) {
            try {
                URL url = new URI(this.rssURL).toURL();
                URLConnection con = url.openConnection();
                con.setRequestProperty("User-Agent", this.rssUserAgent);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    xmlContent = reader.lines().collect(Collectors.joining("\n"));
                }
                this.factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder parser = this.factory.newDocumentBuilder();
                parser.setErrorHandler(this.errorHandler);
                return parser
                        .parse(new InputSource(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8))));
            } catch (IOException | ParserConfigurationException | URISyntaxException e) {
                throw new EresourceDatabaseException(e);
            } catch (SAXException e) {
                retryCount++;
                try {
                    Thread.sleep((long) this.fetchIntervalMilliSeconds * retryCount);
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                    throw new EresourceDatabaseException(e2);
                }
                if (retryCount >= FETCH_RETRIES) {
                    String message = String.format("Failed to parse XML after %d retries: %n%n%s%n", FETCH_RETRIES,
                            xmlContent);
                    throw new EresourceDatabaseException(message, e);
                }
            }
        }
        return doc;
    }

    @Override
    public void process() {
        Document doc;
        try {
            doc = fetchDocument();
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            if (getUpdateDate(doc) > getStartTime()) {
                this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (SAXException | TransformerException e) {
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
