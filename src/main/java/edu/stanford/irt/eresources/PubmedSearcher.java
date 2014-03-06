package edu.stanford.irt.eresources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author ryanmax
 */
public class PubmedSearcher {

    private static final String BASE_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax=1000000&email=ryanmax@stanford.edu&term=";

    private static final RequestConfig HTTP_CONFIG = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES)
            .build();

    private static final HttpClient httpClient = HttpClients.createDefault();

    private DocumentBuilderFactory factory;

    private Logger log = LoggerFactory.getLogger(getClass());

    private List<String> pmids;

    private String query;

    private String type;

    private XPath xpath;

    public PubmedSearcher(final String type, final String query) {
        this.type = type;
        this.query = query;
        this.factory = DocumentBuilderFactory.newInstance();
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    private String getContent(final String url) {
        String htmlContent = null;
        HttpResponse res = null;
        HttpGet method = new HttpGet(url);
        method.setConfig(HTTP_CONFIG);
        try {
            res = PubmedSearcher.httpClient.execute(method);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                htmlContent = EntityUtils.toString(res.getEntity());
            }
        } catch (Exception e) {
            method.abort();
        }
        return htmlContent;
    }

    /**
     * @param query
     *            PubMed search string
     * @return a list of pmids
     * @throws Exception
     */
    public List<String> getPmids() {
        if (this.pmids == null) {
            this.pmids = new ArrayList<String>();
        }
        if (!this.pmids.isEmpty()) {
            return this.pmids;
        }
        if (this.query == null) {
            throw new IllegalStateException("null query");
        }
        String xmlContent = getContent(BASE_URL + this.query);
        Document doc;
        NodeList nodes = null;
        try {
            doc = this.factory.newDocumentBuilder().parse(new ByteArrayInputStream(xmlContent.getBytes()));
            nodes = (NodeList) this.xpath.evaluate("/eSearchResult/IdList/Id", doc, XPathConstants.NODESET);
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            this.log.error("failed to fetch pmids", e);
        }
        for (int n = 0; n < nodes.getLength(); n++) {
            Node node = nodes.item(n);
            this.pmids.add(node.getTextContent().trim());
        }
        return this.pmids;
    }

    public String getType() {
        return this.type;
    }
}
