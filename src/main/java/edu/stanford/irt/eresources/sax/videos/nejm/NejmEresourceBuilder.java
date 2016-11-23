package edu.stanford.irt.eresources.sax.videos.nejm;

import java.nio.charset.StandardCharsets;
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

    private List<String> expression = null;

    private StringBuilder text = new StringBuilder();

    private XPath xPath = XPathFactory.newInstance().newXPath();

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        super.characters(ch, start, length);
        this.text.append(ch, start, length);
    }

    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        super.endElement(uri, localName, name);
        if ("url".equals(name)) {
            String url = this.text.toString();
            SAXEresource eresource = super.getCurrentEresource();
            getAdditionalField(url, eresource);
            eresource.setKeywords(eresource.getTitle() + " " + eresource.getDescription());
        }
        this.text = new StringBuilder();
    }

    public void setExpression(final List<String> expression) {
        this.expression = expression;
    }

    private void getAdditionalField(final String url, final SAXEresource eresource) {
        HttpGet httpget = new HttpGet(url);
        HTMLConfiguration conf = new HTMLConfiguration();
        conf.setFeature("http://xml.org/sax/features/namespaces", false);
        conf.setProperty("http://cyberneko.org/html/properties/default-encoding", StandardCharsets.UTF_8.name());
        conf.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        try (CloseableHttpClient httpclient = HttpClients.createDefault();
                CloseableHttpResponse response = httpclient.execute(httpget)) {
            InputSource source = new InputSource(response.getEntity().getContent());
            DOMParser parser = new DOMParser(conf);
            parser.parse(source);
            Document doc = parser.getDocument();
            String description = getDescription(doc);
            eresource.setDescription(description);
            eresource.setPublicationAuthorsText(getAuthor(doc));
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        }
    }

    private String getAuthor(final Document doc) throws XPathExpressionException {
        return (String) this.xPath.compile("//p[@class='authors']").evaluate(doc, XPathConstants.STRING);
    }

    private String getDescription(final Document doc) throws XPathExpressionException {
        String description = (String) this.xPath.compile(this.expression.get(0)).evaluate(doc, XPathConstants.STRING);
        if (description == null || "".equals(description)) {
            description = (String) this.xPath.compile(this.expression.get(1)).evaluate(doc, XPathConstants.STRING);
        }
        if (description != null && description.length() > 200) {
            description = description.substring(0, 200);
        }
        if (description != null && description.trim().startsWith("Overview ")) {
            description = description.substring("Overview ".length());
        }
        return description;
    }
}
