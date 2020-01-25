package edu.stanford.irt.eresources.pubmed;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
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
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.stanford.irt.eresources.EresourceDatabaseException;

/**
 * @author ryanmax
 */
public class PubmedSearcher {

    private static final String BASE_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed";

    private static final RequestConfig HTTP_CONFIG = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES)
            .build();

    private static final HttpClient httpClient = HttpClientBuilder.create().setUserAgent(PubmedSearcher.class.getName())
            .setDefaultRequestConfig(HTTP_CONFIG).build();

    private static final Logger log = LoggerFactory.getLogger(PubmedSearcher.class);

    private static final int RET_MAX = 500_000;

    private String apiKey;

    private DocumentBuilderFactory factory;

    private String field;

    private List<String> pmids;

    private String query;

    private String value;

    private XPath xpath;

    public PubmedSearcher(final String field, final String value, final String query, final String apiKey) {
        if (query == null) {
            throw new IllegalStateException("null query");
        }
        this.apiKey = apiKey;
        this.field = field;
        this.query = query;
        this.value = value;
        this.xpath = XPathFactory.newInstance().newXPath();
        this.factory = DocumentBuilderFactory.newInstance();
        try {
            this.factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
        } catch (ParserConfigurationException e) {
            log.error("can't set {}", XMLConstants.FEATURE_SECURE_PROCESSING, e);
        }
    }

    public String getField() {
        return this.field;
    }

    /**
     * @return a list of pmids
     */
    public List<String> getPmids() {
        if (this.pmids == null) {
            this.pmids = doGet();
        }
        return new ArrayList<>(this.pmids);
    }

    public String getValue() {
        return this.value;
    }

    private List<String> doGet() {
        List<String> pmidList = new ArrayList<>();
        int retStart = 0;
        int retMax = RET_MAX;
        while (retMax >= RET_MAX) {
            StringBuilder q = new StringBuilder(BASE_URL);
            // for testing, allow null api_key
            if (null != this.apiKey) {
                q.append("&api_key=");
                q.append(this.apiKey);
            }
            q.append("&term=");
            q.append(this.query);
            q.append("&retmax=");
            q.append(RET_MAX);
            q.append("&retstart=");
            q.append(retStart);
            String xmlContent = getContent(q.toString());
            if (null == xmlContent) {
                log.error("null xmlContent for {}", q);
                return pmidList;
            }
            retStart = retStart + RET_MAX;
            Document doc;
            Node retmaxNode = null;
            NodeList retmaxNodes = null;
            NodeList pmidNodes = null;
            try {
                doc = this.factory.newDocumentBuilder()
                        .parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
                retmaxNodes = (NodeList) this.xpath.evaluate("/eSearchResult/RetMax", doc, XPathConstants.NODESET);
                retmaxNode = retmaxNodes.item(0);
                if (null == retmaxNode || null == retmaxNode.getTextContent()) {
                    log.error("null eSearchResult/RetMax for {}", q);
                } else {
                    retMax = Integer.parseInt(retmaxNode.getTextContent().trim());
                    pmidNodes = (NodeList) this.xpath.evaluate("/eSearchResult/IdList/Id", doc, XPathConstants.NODESET);
                }
            } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
                log.error("failed to fetch pmids", e);
            }
            for (int n = 0; null != pmidNodes && n < pmidNodes.getLength(); n++) {
                Node node = pmidNodes.item(n);
                pmidList.add(node.getTextContent().trim());
            }
        }
        return pmidList;
    }

    private String getContent(final String url) {
        String htmlContent = null;
        HttpResponse res = null;
        HttpGet get = new HttpGet(url);
        try {
            res = PubmedSearcher.httpClient.execute(get);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                htmlContent = EntityUtils.toString(res.getEntity());
            }
        } catch (IOException e) {
            get.abort();
            throw new EresourceDatabaseException(e);
        } finally {
            get.reset();
        }
        return htmlContent;
    }
}
