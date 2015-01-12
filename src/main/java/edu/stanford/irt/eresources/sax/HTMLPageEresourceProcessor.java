package edu.stanford.irt.eresources.sax;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.slf4j.Logger;
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

    private static final String[] NO_SEARCH_DIRECTORIES = { ".svn", "includes", "search", "samples", "m" };

    private String basePath;

    private ContentHandler contentHandler;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private Set<String> noSearchDirectories;

    public HTMLPageEresourceProcessor(final String basePath, final ContentHandler contentHandler) {
        this.basePath = basePath;
        this.contentHandler = contentHandler;
        this.noSearchDirectories = new HashSet<String>();
        for (String element : NO_SEARCH_DIRECTORIES) {
            this.noSearchDirectories.add(element);
        }
    }

    @Override
    public void process() {
        Logger log = LoggerFactory.getLogger(getClass());
        log.info("enter process();");
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
                parseFile(filesToParse, config, tf);
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (SAXException e) {
            throw new EresourceException(e);
        }
        log.info("return process();");
    }

    private List<File> getHTMLPages(final File directory) {
        List<File> result = new ArrayList<File>();
        File[] files = directory.listFiles(new FileFilter() {

            @Override
            public boolean accept(final File file) {
                String name = file.getName();
                return name.endsWith(".html")
                        || (file.isDirectory() && !HTMLPageEresourceProcessor.this.noSearchDirectories.contains(name));
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

    private void parseFile(final List<File> filesToParse, final HTMLConfiguration config, final TransformerFactory tf) {
        File file = filesToParse.remove(0);
        String fileName = file.getAbsolutePath();
        InputSource source = new InputSource();
        try {
            source.setByteStream(new FileInputStream(file));
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
        } catch (FileNotFoundException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        } catch (SAXException | IOException | TransformerException e) {
            throw new EresourceException(e);
        }
    }
}
