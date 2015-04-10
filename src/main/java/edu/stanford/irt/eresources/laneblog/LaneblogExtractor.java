package edu.stanford.irt.eresources.laneblog;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Extractor;
import edu.stanford.irt.eresources.StartDate;

public class LaneblogExtractor implements Extractor<Document> {

    private DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

    private ErrorHandler errorHandler = new DefaultHandler();

    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private boolean hasNext;

    private Document parsedDocument;

    private URL rssURL;

    private StartDate startDate;

    public LaneblogExtractor(final URL url, final StartDate startDate) {
        this.rssURL = url;
        this.startDate = startDate;
        try {
            InputSource source = new InputSource(this.rssURL.openConnection().getInputStream());
            DocumentBuilder parser = this.factory.newDocumentBuilder();
            parser.setErrorHandler(this.errorHandler);
            this.parsedDocument = parser.parse(source);
            this.hasNext = getUpdateDate(this.parsedDocument) > this.startDate.getStartDate().getTime();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new EresourceException(e);
        }
    }

    private long getUpdateDate(final Document doc) {
        NodeList nodeList = doc.getElementsByTagName("lastBuildDate");
        Element lastBuildDateEl = (Element) nodeList.item(0);
        try {
            Date lastBuildDate = this.dateFormat.parse(lastBuildDateEl.getTextContent());
            return lastBuildDate.getTime();
        } catch (DOMException | ParseException e) {
            throw new EresourceException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public Document next() {
        this.hasNext = false;
        return this.parsedDocument;
    }
}
