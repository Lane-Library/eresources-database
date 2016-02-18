package edu.stanford.irt.eresources.sax.videos.nejm;

import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
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

public class NejmEresourceBuilder extends DefaultEresourceBuilder {

    private StringBuilder text = new StringBuilder();

    XPath xPath = XPathFactory.newInstance().newXPath();

    List<String> expression = null;

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
            getAdditionalField(url, eresource);
            eresource.setKeywords(eresource.getTitle() + " " + eresource.getAuthor() + " " + eresource.getDescription());
        }
        this.text = new StringBuilder();
    }

    private void getAdditionalField(String url, SAXEresource eresource) {
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
            String description = getDescription(doc);
            eresource.setDescription(description);
            eresource.setAuthor(getAuthor(doc));            
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }

    private String getDescription(Document doc) throws XPathExpressionException {
        String description = (String) xPath.compile(this.expression.get(0)).evaluate(doc, XPathConstants.STRING);
        if (description == null || "".equals(description)) {
            description = (String) xPath.compile(this.expression.get(1)).evaluate(doc, XPathConstants.STRING);
        }
        if (description != null && description.length() > 200) {
            description = description.substring(0, 200);
        }
        if (description != null && description.trim().startsWith("Overview ")) {
            description = description.substring("Overview ".length());
        }
        return description;
    }

    private String getAuthor(Document doc) throws XPathExpressionException {
        return (String) xPath.compile("//p[@class='authors']").evaluate(doc, XPathConstants.STRING);
    }

    public void setExpression(List<String> expression) {
        this.expression = expression;
    }
}
