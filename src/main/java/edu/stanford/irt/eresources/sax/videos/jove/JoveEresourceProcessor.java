package edu.stanford.irt.eresources.sax.videos.jove;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.sax.videos.VideoEresourceProcessor;

public class JoveEresourceProcessor extends VideoEresourceProcessor {

    private XPathExpression nextPageExpression;

    private XPath xPath = XPathFactory.newInstance().newXPath();

    @Override
    public void process() {
        try {
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            for (int index = 0; index < this.URLs.size(); index++) {
                String nextPageUrl = this.URLs.get(index);
                while (!"".equals(nextPageUrl)) {
                    Document doc = getDocument(nextPageUrl);
                    this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
                    nextPageUrl = (String) this.nextPageExpression.evaluate(doc, XPathConstants.STRING);
                    Thread.sleep(500);
                }
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (InterruptedException | SAXException | TransformerException | XPathExpressionException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void setNextPageExpression(final String nextPageExpression) throws XPathExpressionException {
        this.nextPageExpression = this.xPath.compile(nextPageExpression);
    }
}
