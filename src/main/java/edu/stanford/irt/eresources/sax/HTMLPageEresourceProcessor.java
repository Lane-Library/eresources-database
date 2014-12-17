package edu.stanford.irt.eresources.sax;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceException;

public class HTMLPageEresourceProcessor extends AbstractEresourceProcessor {

    private static final String ERESOURCES = "eresources";

    private String basePath;

    private ContentHandler contentHandler;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public void process() {
        if (null == this.basePath) {
            throw new IllegalArgumentException("null basePath");
        }
        if (null == this.contentHandler) {
            throw new IllegalArgumentException("null contentHandler");
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        HTMLConfiguration config = new HTMLConfiguration();
        config.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        List<File> filesToParse = getHTMLPages(new File(this.basePath));
        try {
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            while (!filesToParse.isEmpty()) {
                File file = filesToParse.remove(0);
                String fileName = file.getAbsolutePath();
                InputSource source = new InputSource();
                try {
                    source.setByteStream(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                    continue;
                }
                source.setEncoding("UTF-8");
                DOMParser parser = new DOMParser(config);
                parser.parse(source);
                Document doc = parser.getDocument();
                if (file.lastModified() > getStartTime() && isSearchable(doc)) {
                    Element root = doc.getDocumentElement();
                    root.setAttribute("id", Integer.toString(fileName.hashCode()));
                    root.setAttribute("update", this.dateFormat.format(file.lastModified()));
                    root.setAttribute("file", fileName.substring(this.basePath.length()));
                    tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
                }
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (SAXException e) {
            throw new EresourceException(e);
        } catch (TransformerConfigurationException e) {
            throw new EresourceException(e);
        } catch (TransformerException e) {
            throw new EresourceException(e);
        } catch (IOException e) {
            throw new EresourceException(e);
        }
    }

    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    private List<File> getHTMLPages(final File directory) {
        List<File> result = new LinkedList<File>();
        File[] files = directory.listFiles(new FileFilter() {

            @Override
            public boolean accept(final File file) {
                String name = file.getName();
                return name.endsWith(".html")
                        || (file.isDirectory() && !".svn".equals(name) && !"includes".equals(name)
                                && !"search".equals(name) && !"tobacco".equals(name) && !"samples".equals(name) && !"m"
                                .equals(name));
            }
        });
        for (File file : files) {
            if (file.isDirectory()) {
                result.addAll(getHTMLPages(file));
            } else {
                result.add(file);
            }
        }
        return result;
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
}
