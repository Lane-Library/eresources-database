package edu.stanford.irt.eresources.sax.videos.medlineplus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.sax.videos.VideoEresourceProcessor;

public class MedlineplusEresourceProcessor extends VideoEresourceProcessor {

    private List<String> expressions = null;

    private int id = 0;

    private XPath xPath = XPathFactory.newInstance().newXPath();

    @Override
    public void process() {
        try {
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            // entry url one the Health Videos and the other Surgical Videos
            for (int index = 0; index < this.urls.size(); index++) {
                Set<String> links = new HashSet<>();
                Document doc = getDocument(this.urls.get(index));
                // All links from Health Videos or Surgical Videos page
                NodeList nodes = (NodeList) this.xPath.compile(this.expressions.get(index)).evaluate(doc,
                        XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    String href = node.getTextContent();
                    if (!href.startsWith("http")) {
                        href = "https:" + href;
                    }
                    links.add(href);
                }
                process(links);
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (SAXException | XPathException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void process(final Set<String> urls) {
        try {
            for (String url : urls) {
                Document doc = getDocument(url);
                Element root = doc.getDocumentElement();
                root.setAttribute("id", String.valueOf(++this.id));
                root.setAttribute("url", url);
                this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
            }
        } catch (TransformerException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void setExpressions(final List<String> expressions) {
        this.expressions = expressions;
    }
}
