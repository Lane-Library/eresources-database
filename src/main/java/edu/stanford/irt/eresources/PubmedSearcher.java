package edu.stanford.irt.eresources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    private static final String BASE_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&email=ryanmax@stanford.edu&term=";

    private static final RequestConfig HTTP_CONFIG = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES)
            .build();

    private static final HttpClient httpClient = HttpClients.createDefault();

    private static final Logger LOG = LoggerFactory.getLogger(PubmedSearcher.class);

    private static final int RET_MAX = 500000;

    private DocumentBuilderFactory factory;

    private String field;

    private List<String> pmids;

    private String query;

    private String value;

    private XPath xpath;

    public PubmedSearcher(final String field, final String value, final String query) {
        this.field = field;
        this.query = query;
        this.value = value;
        this.factory = DocumentBuilderFactory.newInstance();
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    public String getField() {
        return this.field;
    }

    /**
     * @return a list of pmids
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
        int retStart = 0;
        int retMax = RET_MAX;
        while (retMax >= RET_MAX) {
            String xmlContent = getContent(BASE_URL + this.query + "&retmax=" + RET_MAX + "&retstart=" + retStart);
            retStart = retStart + RET_MAX;
            Document doc;
            NodeList retmaxNode = null;
            NodeList pmidNodes = null;
            try {
                doc = this.factory.newDocumentBuilder()
                        .parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
                retmaxNode = (NodeList) this.xpath.evaluate("/eSearchResult/RetMax", doc, XPathConstants.NODESET);
                retMax = Integer.parseInt(retmaxNode.item(0).getTextContent().trim());
                pmidNodes = (NodeList) this.xpath.evaluate("/eSearchResult/IdList/Id", doc, XPathConstants.NODESET);
            } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
                LOG.error("failed to fetch pmids", e);
            }
            if (null != pmidNodes) {
                for (int n = 0; n < pmidNodes.getLength(); n++) {
                    Node node = pmidNodes.item(n);
                    this.pmids.add(node.getTextContent().trim());
                }
            }
        }
        return this.pmids;
    }

    public String getValue() {
        return this.value;
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
        } catch (IOException e) {
            method.abort();
            throw new EresourceDatabaseException(e);
        }
        return htmlContent;
    }
}
