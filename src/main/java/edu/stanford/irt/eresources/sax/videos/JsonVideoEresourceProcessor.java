package edu.stanford.irt.eresources.sax.videos;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.cyberneko.html.HTMLConfiguration;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public abstract class JsonVideoEresourceProcessor extends AbstractEresourceProcessor {

    protected static final String ERESOURCES = "eresources";

    protected static final String ERESOURCE = "eresource";

    protected static final String TITLE = "title";

    protected static final String ID = "id";

    protected static final String TYPE = "type";

    protected static final String UPDATE = "update";

    protected static final String CDATA = "CDATA";

    protected static final String PRIMARY_TYPE = "primaryType";

    protected static final String VISUAL_MATERIAL = "Visual Material";

    protected static final String VIDEO = "Video";

    protected static final String INSTRUCTIONAL_VIDEO = "Intructional Video";

    protected static final String YEAR = "year";

    protected static final String DESCRIPTION = "description";

    protected static final String VERSION = "version";

    protected static final String LINK = "link";

    protected static final String URL = "url";

    protected static final String KEYWORDS = "keywords";

    protected ContentHandler contentHandler;

    protected List<String> URLs;

    protected HttpClient httpClient = null;

    private Header USER_AGENT = new BasicHeader("User-Agent",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:34.0) Gecko/20100101 Firefox/34.");

    
    
    public JsonVideoEresourceProcessor() {
        this.httpClient = HttpClients.createDefault();
    }

    protected JsonNode getJsonNode(String url) throws IOException {
        CloseableHttpResponse res = null;
        try {
            HTMLConfiguration config = new HTMLConfiguration();
            config.setFeature("http://xml.org/sax/features/namespaces", false);
            config.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
            config.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
            HttpGet get = new HttpGet(url);
            get.addHeader(USER_AGENT);
            res = (CloseableHttpResponse) httpClient.execute(get);
            
            ObjectMapper mapper = new ObjectMapper();
            
            InputStream in = res.getEntity().getContent();
            System.out.print("\n\n\n\n");
            int c = -1;
            while((c = in.read()) != -1){
                System.out.print((char)c);
            }
            System.out.print("\n\n\n\n");
            
            return mapper.readValue(res.getEntity().getContent(), JsonNode.class);
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        } finally {
            if (res != null) {
                res.close();
            }
        }
    }


    protected void processJson(String id, String eresoursceType, String title, String description, String keywords,
            String year, String url) throws SAXException {
        startEresourceElement(id, eresoursceType);
        if (title != null) {
            createElement(TITLE, title);
        }
        if (description != null) {
            createElement(DESCRIPTION, description);
        }
        createElement(KEYWORDS, keywords);
        if (null != year) {
            createElement(YEAR, year);
        }
        this.contentHandler.startElement("", VERSION, VERSION, new AttributesImpl());
        this.contentHandler.startElement("", LINK, LINK, new AttributesImpl());
        if (null != url) {
            createElement(URL, url);
        }
        this.contentHandler.endElement("", LINK, LINK);
        this.contentHandler.endElement("", VERSION, VERSION);
        this.contentHandler.endElement("", ERESOURCE, ERESOURCE);
    }

    protected void startEresourceElement(String id, String type) throws SAXException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", ID, ID, CDATA, id);
        attrs.addAttribute("", TYPE, TYPE, CDATA, type);
        attrs.addAttribute("", UPDATE, UPDATE, CDATA, "19690101000000");
        this.contentHandler.startElement("", ERESOURCE, ERESOURCE, attrs);
        createElement(PRIMARY_TYPE, VISUAL_MATERIAL);
        createElement(TYPE, INSTRUCTIONAL_VIDEO);
        createElement(TYPE, VIDEO);
    }

    protected void createElement(String name, String value) throws SAXException {
        this.contentHandler.startElement("", name, name, new AttributesImpl());
        this.contentHandler.characters(value.toCharArray(), 0, value.length());
        this.contentHandler.endElement("", name, name);
    }

    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public void setURLs(final List<String> URLs) {
        this.URLs = URLs;
    }
}
