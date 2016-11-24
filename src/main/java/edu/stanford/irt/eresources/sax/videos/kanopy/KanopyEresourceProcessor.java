package edu.stanford.irt.eresources.sax.videos.kanopy;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.sax.videos.VideoEresourceProcessor;

public class KanopyEresourceProcessor extends VideoEresourceProcessor {

    private XPathExpression nextPageExpression;

    private XPath xPath = XPathFactory.newInstance().newXPath();

    @Override
    public void process() {
        try {
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            int page = 0;
            String totalVideos = null;
            while (!"0".equals(totalVideos)) {
                Document doc = getDocument(this.urls.get(0).concat(String.valueOf(page++)));
                Element root = doc.getDocumentElement();
                Node node = doc.createElement("page_id");
                node.setTextContent(String.valueOf(page * 10));
                root.appendChild(node);
                this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
                totalVideos = this.nextPageExpression.evaluate(doc);
                Thread.sleep(1000);
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
