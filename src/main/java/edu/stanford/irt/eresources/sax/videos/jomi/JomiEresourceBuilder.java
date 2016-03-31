package edu.stanford.irt.eresources.sax.videos.jomi;

import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.sax.DefaultEresourceBuilder;
import edu.stanford.irt.eresources.sax.SAXEresource;

public class JomiEresourceBuilder extends DefaultEresourceBuilder {

    private StringBuilder text = new StringBuilder();

    XPath xPath = XPathFactory.newInstance().newXPath();

    String expression = null;

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        super.characters(ch, start, length);
        text.append(ch, start, length);
    }

    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        super.endElement(uri, localName, name);
        if ("url".equals(name)) {
            String url = this.text.toString();
            SAXEresource eresource = super.getCurrentEresource();
            getDescription(url, eresource);
            
        }
        this.text = new StringBuilder();
    }

    private void getDescription(String url, SAXEresource eresource) {
        CloseableHttpResponse response = null;
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);
            response = httpclient.execute(httpget);
            InputSource source = new InputSource(response.getEntity().getContent());
            HTMLConfiguration conf = new HTMLConfiguration();
            conf.setFeature("http://xml.org/sax/features/namespaces", false);
            conf.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
            conf.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
            DOMParser parser = new DOMParser(conf);
            parser.parse(source);
            Document doc = parser.getDocument();
            String description = (String) xPath.compile(this.expression).evaluate(doc, XPathConstants.STRING);
            eresource.setDescription(description);
            eresource.setKeywords( eresource.getKeywords().concat(description));
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        } finally {
            try {
                if(response != null){
                    response.close();
                }
            } catch (IOException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }
   public void setExpression(String expression) {
        this.expression = expression;
    }
}