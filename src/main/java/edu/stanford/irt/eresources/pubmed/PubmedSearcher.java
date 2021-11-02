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

    private static final String BASE_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/";

    private static final String EFETCH_URL = BASE_URL + "efetch.fcgi?db=pubmed&retmode=xml&rettype=uilist";

    private static final String ESEARCH_URL = BASE_URL + "esearch.fcgi?db=pubmed&usehistory=y";

    private static final RequestConfig HTTP_CONFIG = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES)
            .build();

    private static final HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(HTTP_CONFIG)
            .build();

    private static final Logger log = LoggerFactory.getLogger(PubmedSearcher.class);

    private static final int RET_MAX = 500_000;

    private String apiKey;

    private String appVersion;

    private DocumentBuilderFactory factory;

    private String field;

    private List<String> pmids;

    private String query;

    private String queryKey;

    private int returnCount;

    private String value;

    private String webEnv;

    private XPath xpath;

    public PubmedSearcher(final String field, final String value, final String query, final String apiKey,
            final String appVersion) {
        if (query == null) {
            throw new IllegalStateException("null query");
        }
        this.apiKey = apiKey;
        this.appVersion = "eresources-" + appVersion;
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
            doGet();
        }
        return new ArrayList<>(this.pmids);
    }

    public String getValue() {
        return this.value;
    }

    private void appendApiKey(final StringBuilder query) {
        // for testing, allow null api_key
        if (null != this.apiKey) {
            query.append("&api_key=");
            query.append(this.apiKey);
        }
    }

    private void doGet() {
        doSearch();
        this.pmids = new ArrayList<>();
        int retStart;
        int i = 0;
        while (this.pmids.size() < this.returnCount) {
            retStart = i++ * RET_MAX;
            StringBuilder q = new StringBuilder(EFETCH_URL);
            appendApiKey(q);
            q.append("&query_key=");
            q.append(this.queryKey);
            q.append("&WebEnv=");
            q.append(this.webEnv);
            q.append("&retmax=");
            q.append(RET_MAX);
            q.append("&retstart=");
            q.append(retStart);
            String xmlContent = getContent(q.toString());
            if (null == xmlContent) {
                log.error("null xmlContent for {}", q);
            } else {
                NodeList pmidNodes = null;
                try {
                    Document doc = this.factory.newDocumentBuilder()
                            .parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
                    pmidNodes = (NodeList) this.xpath.evaluate("/IdList/Id", doc, XPathConstants.NODESET);
                } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
                    log.error("failed to fetch pmids", e);
                }
                for (int n = 0; null != pmidNodes && n < pmidNodes.getLength(); n++) {
                    Node node = pmidNodes.item(n);
                    this.pmids.add(node.getTextContent().trim());
                }
            }
        }
    }

    private void doSearch() {
        StringBuilder q = new StringBuilder(ESEARCH_URL);
        appendApiKey(q);
        q.append("&term=");
        q.append(this.query);
        String xmlContent = getContent(q.toString());
        if (null == xmlContent) {
            log.error("null xmlContent for {}", q);
        } else {
            try {
                Document doc = this.factory.newDocumentBuilder()
                        .parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
                this.queryKey = getNodeContent("/eSearchResult/QueryKey", doc);
                this.webEnv = getNodeContent("/eSearchResult/WebEnv", doc);
                this.returnCount = Integer.parseInt(getNodeContent("/eSearchResult/Count", doc));
            } catch (SAXException | IOException | ParserConfigurationException e) {
                log.error("failed to fetch pmids", e);
            }
        }
    }

    private String getContent(final String url) {
        String htmlContent = null;
        HttpResponse res = null;
        HttpGet get = new HttpGet(url);
        get.setHeader("User-Agent", this.appVersion);
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

    private String getNodeContent(final String xpath, final Document doc) {
        try {
            Node node = ((NodeList) this.xpath.evaluate(xpath, doc, XPathConstants.NODESET)).item(0);
            return node.getTextContent().trim();
        } catch (XPathExpressionException e) {
            log.error("failed to fetch content", e);
        }
        return null;
    }
}
