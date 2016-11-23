package edu.stanford.irt.eresources.sax.videos.jove;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.sax.DefaultEresourceBuilder;
import edu.stanford.irt.eresources.sax.SAXEresource;

public class JoveEresourceBuilder extends DefaultEresourceBuilder {

    CloseableHttpClient httpClient;

    NumberFormat nf = null;

    Header USER_AGENT = new BasicHeader("User-Agent",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:34.0) Gecko/20100101 Firefox/34.");

    private XPathExpression descriptionExpression = null;

    private Logger log = LoggerFactory.getLogger(JoveEresourceBuilder.class);

    private StringBuilder text = new StringBuilder();

    private XPath xPath = XPathFactory.newInstance().newXPath();

    private XPathExpression yearExpression = null;

    public JoveEresourceBuilder() {
        this.httpClient = HttpClients.createDefault();
        this.nf = NumberFormat.getInstance();
        this.nf.setMinimumIntegerDigits(2);
    }

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
            eresource.setKeywords("jove " + eresource.getTitle().trim() + " " + eresource.getDescription().trim());
        }
        this.text = new StringBuilder();
    }

    public void setDescriptionExpression(final String expression) {
        try {
            this.descriptionExpression = this.xPath.compile(expression);
        } catch (XPathExpressionException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void setYearExpression(final String expression) {
        try {
            this.yearExpression = this.xPath.compile(expression);
        } catch (XPathExpressionException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    private void getAdditionalField(final String url, final SAXEresource eresource) {
        CloseableHttpResponse response = null;
        try {
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader(this.USER_AGENT);
            response = this.httpClient.execute(httpget);
            InputSource source = new InputSource(response.getEntity().getContent());
            HTMLConfiguration conf = new HTMLConfiguration();
            conf.setFeature("http://xml.org/sax/features/namespaces", false);
            conf.setProperty("http://cyberneko.org/html/properties/default-encoding", StandardCharsets.UTF_8.name());
            conf.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
            DOMParser parser = new DOMParser(conf);
            parser.parse(source);
            Document doc = parser.getDocument();
            String description = getDescription(doc);
            eresource.setDescription(description);
            eresource.setKeywords("jove ".concat(description).concat(" ").concat(eresource.getTitle()));
            setDate(doc, url, eresource);
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                this.log.error(e.getMessage(), e);
            }
        }
    }

    private String getDescription(final Document doc) throws XPathExpressionException {
        return (String) this.descriptionExpression.evaluate(doc, XPathConstants.STRING);
    }

    private void setDate(final Document doc, final String url, final SAXEresource eresource)
            throws XPathExpressionException {
        String dateStr = (String) this.yearExpression.evaluate(doc, XPathConstants.STRING);
        String[] date = dateStr.split("/");
        if (date.length == 3) {
            String year = date[2].replace(",", "").trim();
            eresource.setDate(year.concat(this.nf.format(Long.valueOf(date[0].trim())))
                    .concat(this.nf.format(Long.valueOf(date[1].trim()))));
            eresource.setYear(Integer.valueOf(year));
        } else {
            eresource.setYear(0);
        }
    }
}