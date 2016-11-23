package edu.stanford.irt.eresources.sax.videos.sages;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.sax.videos.VideoEresourceProcessor;

public class SagesEresourceProcessor extends VideoEresourceProcessor {

    private XPathExpression nextPageExpression;

    private XPath xPath = XPathFactory.newInstance().newXPath();

    @Override
    public void process() {
        try {
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            int pageIndex = 10;
            for (String url : this.URLs) {
                while (url != null && !"".equals(url)) {
                    Document doc = getDocument(url);
                    Element root = doc.getDocumentElement();
                    root.setAttribute("id", Integer.toString(pageIndex));
                    pageIndex = pageIndex + 10;
                    this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
                    Thread.sleep(100);
                    url = (String) this.nextPageExpression.evaluate(doc, XPathConstants.STRING);
                }
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void setNextPageExpression(final String nextPageExpression) throws XPathExpressionException {
        this.nextPageExpression = this.xPath.compile(nextPageExpression);
    }
}
