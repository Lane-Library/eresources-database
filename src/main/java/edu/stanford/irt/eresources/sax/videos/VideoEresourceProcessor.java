package edu.stanford.irt.eresources.sax.videos;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import net.sf.joost.grammar.tree.GeTree;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public class VideoEresourceProcessor extends AbstractEresourceProcessor {

    Header USER_AGENT = new BasicHeader("User-Agent",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:34.0) Gecko/20100101 Firefox/34.");

    
    protected static final String ERESOURCES = "eresources";

    protected ContentHandler contentHandler;

    protected List<String> URLs;

    protected TransformerFactory tf = TransformerFactory.newInstance();

    HttpClient httpClient = null;
    
    public VideoEresourceProcessor(){
         this.httpClient =  HttpClients.createDefault();
    }
    
    
    @Override
    public void process() {
         try {
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            for (String url : URLs) {
                Document doc = getDocument(url);
                this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
            
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        }
    }

    protected Document getDocument(String url) {
        CloseableHttpResponse res = null;
        try {
            HTMLConfiguration config = new HTMLConfiguration();
            config.setFeature("http://xml.org/sax/features/namespaces", false);
            config.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
            config.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
            HttpGet get = new HttpGet(url);
            get.addHeader(USER_AGENT);
            res = (CloseableHttpResponse) httpClient.execute(get);
            InputSource source = new InputSource(res.getEntity().getContent());
            source.setEncoding("UTF-8");
            DOMParser parser = new DOMParser(config);
            parser.parse(source);
            return parser.getDocument();
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        } finally {
            try {
                if(res != null)
                    res.close();
            } catch (IOException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }

    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public void setURLs(final List<String> URLs) {
        this.URLs = URLs;
    }

    
    
    
}
