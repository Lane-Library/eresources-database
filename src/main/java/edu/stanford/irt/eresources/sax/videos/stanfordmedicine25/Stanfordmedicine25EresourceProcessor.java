package edu.stanford.irt.eresources.sax.videos.stanfordmedicine25;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.sax.videos.VideoEresourceProcessor;

public class Stanfordmedicine25EresourceProcessor extends VideoEresourceProcessor {

    XPath xPath = XPathFactory.newInstance().newXPath();

    XPathExpression linksExpression;

    @Override
    public void process() {
        try {
            Document doc = getDocument(super.URLs.get(0));
            NodeList nodes = (NodeList) this.linksExpression.evaluate(doc, XPathConstants.NODESET);
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            process(nodes);
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        }
    }

    private void process(NodeList nodes) throws TransformerConfigurationException, TransformerException {
        for (int i = 0; i < nodes.getLength(); i++) {
            String href = super.URLs.get(1).concat(nodes.item(i).getTextContent());
            Document doc = getDocument(href);
            Element root = doc.getDocumentElement();
            root.setAttribute("pageid", String.valueOf(i));
            root.setAttribute("url", href);
            this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
        }
    }

    public void setLinksExpression(String expression) throws XPathExpressionException {
        this.linksExpression = xPath.compile(expression);
    }
}
