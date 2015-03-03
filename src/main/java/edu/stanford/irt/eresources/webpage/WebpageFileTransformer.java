package edu.stanford.irt.eresources.webpage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Transformer;
import edu.stanford.irt.eresources.sax.DefaultEresourceBuilder;
import edu.stanford.irt.eresources.sax.EresourceHandler;

public class WebpageFileTransformer implements Transformer<File>, EresourceHandler {

    private String basePath;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private DefaultEresourceBuilder eresourceBuilder;

    private List<Eresource> eresources;

    private HTMLConfiguration htmlConfig = new HTMLConfiguration();

    private javax.xml.transform.Transformer transformer;

    private DOMParser parser;

    public WebpageFileTransformer(final String basePath, final InputStream input) {
        this.basePath = basePath;
        this.eresourceBuilder = new DefaultEresourceBuilder(this);
        try {
            this.transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(input));
        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            throw new EresourceException(e);
        }
        this.htmlConfig = new HTMLConfiguration();
        this.htmlConfig.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        this.htmlConfig.setFeature("http://cyberneko.org/html/features/insert-namespaces", Boolean.TRUE);
        this.parser = new DOMParser(this.htmlConfig);
    }

    @Override
    public void handleEresource(final Eresource eresource) {
        this.eresources.add(eresource);
    }

    @Override
    public Eresource[] transform(final File input) {
        this.eresources = new ArrayList<Eresource>();
        parseFile(input);
        return this.eresources.toArray(new Eresource[this.eresources.size()]);
    }

    /**
     * @param document
     * @return whether there is a <code>&lt;meta name="robots" content="noindex"&gt;</code> element
     */
    private boolean isSearchable(final Document document) {
        boolean searchable = true;
        NodeList metaTags = document.getElementsByTagName("meta");
        for (int i = 0; i < metaTags.getLength(); i++) {
            Element meta = (Element) metaTags.item(i);
            if ("robots".equals(meta.getAttribute("name")) && "noindex".equals(meta.getAttribute("content"))) {
                searchable = false;
                break;
            }
        }
        return searchable;
    }

    private void parseFile(final File file) {
        String fileName = file.getAbsolutePath();
        InputSource source = new InputSource();
        try {
            source.setByteStream(new FileInputStream(file));
            source.setEncoding("UTF-8");
            this.parser.parse(source);
            Document doc = this.parser.getDocument();
            if (isSearchable(doc)) {
                Element root = doc.getDocumentElement();
                root.setAttribute("id", Integer.toString(fileName.hashCode()));
                root.setAttribute("update", this.dateFormat.format(file.lastModified()));
                root.setAttribute("file", fileName.substring(this.basePath.length()));
                this.transformer.transform(new DOMSource(doc), new SAXResult(this.eresourceBuilder));
            }
        } catch (SAXException | IOException | TransformerException e) {
            throw new EresourceException(e);
        }
    }
}
