package edu.stanford.irt.eresources.sax.videos.jove;

import java.io.IOException;

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

    private StringBuilder text = new StringBuilder();

    private XPath xPath = XPathFactory.newInstance().newXPath();

    private Logger log = LoggerFactory.getLogger(JoveEresourceBuilder.class);
    
    private XPathExpression descriptionExpression = null;
    private XPathExpression authorExpression = null;
    private XPathExpression yearExpression = null;

    Header USER_AGENT = new BasicHeader("User-Agent",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:34.0) Gecko/20100101 Firefox/34.");

    CloseableHttpClient httpClient; 
    
    public JoveEresourceBuilder() {
        httpClient = HttpClients.createDefault();
    }
    
    
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


    private String getDescription(Document doc) throws XPathExpressionException {
        return (String)this.descriptionExpression.evaluate(doc, XPathConstants.STRING);
    }

    private String getAuthor(Document doc) throws XPathExpressionException {
        String authors =  (String) this.authorExpression.evaluate(doc, XPathConstants.STRING);
        authors = authors.replaceAll("\\d", "");
        authors = authors.replaceAll(",,", ",");
        authors = authors.replaceAll("\\*", "");
        return authors;
    }

    private int getYear(Document doc, String url) throws XPathExpressionException{
        String date =  (String) this.yearExpression.evaluate(doc, XPathConstants.STRING);
        date = date.substring( date.lastIndexOf("/")+1);
        date = date.trim().replace(",","");
        if("".equals(date) || date == null ){
            return 0;
        }
        try{
           return Integer.valueOf(date);
        }
        catch(NumberFormatException e){
           log.debug("exception date" +date + "\t\t"+url );
        }
        return 0;
    }
    
   
    private void getAdditionalField(String url, SAXEresource eresource) {
        CloseableHttpResponse response = null;
        try {
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader(USER_AGENT);
            response = this.httpClient.execute(httpget);
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
            String authors = getAuthor(doc);
            eresource.setAuthor(authors);
            int year = getYear(doc, url);
            eresource.setYear(year);
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        } finally {
            try {
                if(response != null)
                    response.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    
    public void setDescriptionExpression(String expression) {
        try {
            this.descriptionExpression = xPath.compile(expression);
        } catch (XPathExpressionException e) {
            throw new EresourceDatabaseException(e);
        }
    }
    
    public void setAuthorExpression(String expression) {
        try {
            this.authorExpression = xPath.compile(expression);
        } catch (XPathExpressionException e) {
            throw new EresourceDatabaseException(e);
        }
    }
    
    public void setYearExpression(String expression) {
        try {
            this.yearExpression = xPath.compile(expression);
        } catch (XPathExpressionException e) {
            throw new EresourceDatabaseException(e);
        }
    }
    
}