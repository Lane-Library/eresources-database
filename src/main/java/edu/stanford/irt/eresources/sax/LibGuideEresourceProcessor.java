package edu.stanford.irt.eresources.sax;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
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
import edu.stanford.irt.eresources.TextParserHelper;

public class LibGuideEresourceProcessor extends AbstractEresourceProcessor {

    private class Guide {

        protected String creator;

        protected String description;

        protected String id;

        protected String link;

        protected String modifiedDate;

        protected String title;

        public Guide(final String id, final String link, final String title, final String creator,
                final String description, final String modifiedDate) {
            this.id = id;
            this.link = link;
            this.title = title;
            this.creator = creator;
            this.description = description;
            this.modifiedDate = modifiedDate;
        }
    }

    private static final String ERESOURCES = "eresources";

    private static final Pattern NO_INDEX_KEYWORDS = Pattern
            .compile("libguides best practices|test guide|internal guide", Pattern.CASE_INSENSITIVE);

    protected static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ssz").toFormatter();

    private String allGuidesURL;

    private ContentHandler contentHandler;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private ErrorHandler errorHandler = new DefaultHandler();

    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private HTMLConfiguration nekoConfig = new HTMLConfiguration();

    private TransformerFactory tf = TransformerFactory.newInstance();

    private XPath xpath;

    public LibGuideEresourceProcessor(final String allGuidesURL, final ContentHandler contentHandler) {
        this.allGuidesURL = allGuidesURL;
        this.contentHandler = contentHandler;
        this.nekoConfig.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    @Override
    public void process() {
        if (null == this.allGuidesURL) {
            throw new IllegalArgumentException("null allGuidesURL");
        }
        if (null == this.contentHandler) {
            throw new IllegalArgumentException("null contentHandler");
        }
        try {
            this.factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            List<Guide> guides = getGuides();
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            while (!guides.isEmpty()) {
                Guide guide = guides.remove(0);
                Long updated = getUpdateDate(guide.modifiedDate);
                if (updated > getStartTime()) {
                    InputSource source = new InputSource(new URL(guide.link).openConnection().getInputStream());
                    DOMParser parser = new DOMParser(this.nekoConfig);
                    parser.parse(source);
                    Document doc = parser.getDocument();
                    Element root = doc.getDocumentElement();
                    root.setAttribute("id", guide.id);
                    root.setAttribute("creator", guide.creator);
                    root.setAttribute("description", guide.description);
                    root.setAttribute("title", guide.title);
                    root.setAttribute("link", guide.link);
                    root.setAttribute("update", this.dateFormat.format(updated));
                    this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
                }
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (IOException | SAXException | TransformerException | ParserConfigurationException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    private List<Guide> getGuides() {
        List<Guide> guides = new LinkedList<>();
        try {
            URL url = new URL(this.allGuidesURL);
            InputSource source = new InputSource(url.openConnection().getInputStream());
            DocumentBuilder parser = this.factory.newDocumentBuilder();
            parser.setErrorHandler(this.errorHandler);
            Document doc = parser.parse(source);
            NodeList recordList = doc.getElementsByTagName("record");
            for (int i = 0; i < recordList.getLength(); i++) {
                Element recordElm = (Element) recordList.item(i);
                String modifiedDate = maybeFetchTextContent(recordElm, "datestamp");
                String link = maybeFetchTextContent(recordElm, "dc:identifier");
                String id = TextParserHelper.cleanId(link.hashCode());
                String description = maybeFetchTextContent(recordElm, "dc:description");
                String title = maybeFetchTextContent(recordElm, "dc:title");
                String creator = maybeFetchTextContent(recordElm, "dc:creator");
                Guide guide = new Guide(id, link, title, creator, description, modifiedDate);
                if (isIndexable(guide)) {
                    guides.addAll(getSubGuides(guide));
                }
            }
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
        return guides;
    }

    private List<Guide> getSubGuides(final Guide guide) {
        List<Guide> subGuides = new LinkedList<>();
        try {
            URL url = new URL(guide.link);
            InputSource source = new InputSource(url.openConnection().getInputStream());
            DOMParser parser = new DOMParser(this.nekoConfig);
            parser.parse(source);
            parser.setErrorHandler(this.errorHandler);
            Document doc = parser.getDocument();
            NodeList guideAnchors = (NodeList) this.xpath.evaluate("//div[@id='s-lg-guide-tabs']/ul/li/a[starts-with(@href,'http') or starts-with(@href,'file')]", doc,
                    XPathConstants.NODESET);
            for (int i = 0; i < guideAnchors.getLength(); i++) {
                Element guideAnchor = (Element) guideAnchors.item(i);
                String link = guideAnchor.getAttribute("href");
                String id = TextParserHelper.cleanId(link.hashCode());
                String name = guide.title + " -- "
                        + StringEscapeUtils.unescapeHtml(guideAnchor.getAttribute("aria-label"));
                Guide subGuide = new Guide(id, link, name, guide.creator, guide.description, guide.modifiedDate);
                subGuides.add(subGuide);
            }
        } catch (SAXException | IOException | XPathExpressionException e) {
            throw new EresourceDatabaseException(e);
        }
        return subGuides;
    }

    private long getUpdateDate(final String date) {
        LocalDateTime ldt = LocalDateTime.parse(date.trim(), FORMATTER);
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private boolean isIndexable(final Guide guide) {
        StringBuilder sb = new StringBuilder();
        sb.append(guide.title);
        sb.append(' ');
        sb.append(guide.description);
        return !NO_INDEX_KEYWORDS.matcher(sb.toString()).find();
    }

    private String maybeFetchTextContent(final Element elm, final String tagName) {
        String value = "";
        NodeList nodeList = elm.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            value = nodeList.item(0).getTextContent();
        }
        return value;
    }
}
