package edu.stanford.irt.eresources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SulCatalogRecordService implements CatalogRecordService {

    private static final Logger log = LoggerFactory.getLogger(SulCatalogRecordService.class);

    private static final int REQUESTS_BEFORE_SLEEP = 20;

    private static final int SLEEP_TIME = 1000;

    private URI catalogServiceURI;

    private DocumentBuilderFactory factory;

    private int requests;

    private XPath xpath;

    public SulCatalogRecordService(final URI catalogServiceURI) {
        this.catalogServiceURI = catalogServiceURI;
        this.factory = DocumentBuilderFactory.newInstance();
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    // super inefficient to make multiple requests for this
    // create cache? use record characteristics? parse from leader/008/006?
    public List<String> getRecordFormats(final String recordId) {
        maybeSleep();
        List<String> formats = new ArrayList<>();
        StringBuilder url = new StringBuilder(this.catalogServiceURI.getScheme());
        url.append("://");
        url.append(this.catalogServiceURI.getHost());
        url.append("/view/");
        url.append(recordId);
        url.append(".dc_xml");
        InputStream is;
        String xmlContent = null;
        try {
            is = IOUtils.getStream(new URL(url.toString()));
            xmlContent = org.apache.commons.io.IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
        if (null == xmlContent) {
            return formats;
        }
        Document doc;
        NodeList formatNodes = null;
        try {
            this.factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            doc = this.factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
            formatNodes = (NodeList) this.xpath.evaluate("//*[local-name()='format']", doc, XPathConstants.NODESET);
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            throw new EresourceDatabaseException(e);
        }
        for (int n = 0; null != formatNodes && n < formatNodes.getLength(); n++) {
            Node node = formatNodes.item(n);
            formats.add(node.getTextContent().trim());
        }
        return formats;
    }

    @Override
    public InputStream getRecordStream(final long time) {
        try {
            Set<String> urls = getRecordURLs();
            List<InputStream> streams = new ArrayList<>();
            for (String url : urls) {
                streams.add(getStreamIgnoring500Errors(url + ".marc"));
                maybeSleep();
            }
            return new SequenceInputStream(Collections.enumeration(streams));
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    private Set<String> getRecordURLs() throws IOException {
        Set<String> urlList = new HashSet<>();
        int page = 1;
        boolean more = true;
        while (more) {
            StringBuilder url = new StringBuilder(this.catalogServiceURI.toURL().toString().replace("#null", ""));
            url.append("&page=");
            url.append(page);
            InputStream is = IOUtils.getStream(new URL(url.toString()));
            String xmlContent = org.apache.commons.io.IOUtils.toString(is, StandardCharsets.UTF_8);
            if (null == xmlContent) {
                return urlList;
            }
            Document doc;
            NodeList linkNodes = null;
            try {
                this.factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                doc = this.factory.newDocumentBuilder()
                        .parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
                linkNodes = (NodeList) this.xpath.evaluate("//item/link", doc, XPathConstants.NODESET);
            } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
                throw new EresourceDatabaseException(e);
            }
            for (int n = 0; null != linkNodes && n < linkNodes.getLength(); n++) {
                Node node = linkNodes.item(n);
                urlList.add(node.getTextContent().trim());
            }
            if (null == linkNodes || linkNodes.getLength() == 0) {
                more = false;
            }
            page++;
        }
        return urlList;
    }

    private InputStream getStreamIgnoring500Errors(final String url) {
        try {
            return IOUtils.getStream(new URL(url));
        } catch (IOException e) {
            if (e.getMessage().startsWith("Server returned HTTP response code: 500 for URL")) {
                log.info("can't fetch marc for: {}", url);
            } else {
                throw new EresourceDatabaseException(e);
            }
        }
        return new ByteArrayInputStream(new byte[] {});
    }

    private void maybeSleep() {
        this.requests++;
        if (this.requests % REQUESTS_BEFORE_SLEEP == 0) {
            try {
                log.info("sleeping for {} second(s); total requests: {}", SLEEP_TIME / 1000, this.requests);
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new EresourceDatabaseException(e);
            }
        }
    }
}
