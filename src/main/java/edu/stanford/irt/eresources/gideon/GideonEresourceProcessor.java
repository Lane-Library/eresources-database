package edu.stanford.irt.eresources.gideon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.DataFetcher;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public class GideonEresourceProcessor extends AbstractEresourceProcessor {

    private static final String ERESOURCES = "eresources";

    private static final Logger log = LoggerFactory.getLogger(GideonEresourceProcessor.class);

    private String basePath;

    private ContentHandler contentHandler;

    private ErrorHandler errorHandler = new DefaultHandler();

    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private DataFetcher gideonDataFetcher;

    private TransformerFactory tf = TransformerFactory.newInstance();

    public GideonEresourceProcessor(final String basePath, final DataFetcher dataFetcher,
            final ContentHandler contentHandler) {
        this.basePath = basePath;
        this.gideonDataFetcher = dataFetcher;
        this.contentHandler = contentHandler;
    }

    @Override
    public void process() {
        if (null == this.basePath) {
            throw new IllegalStateException("null basePath");
        }
        if (null == this.gideonDataFetcher) {
            throw new IllegalStateException("null dataFetcher");
        }
        if (null == this.contentHandler) {
            throw new IllegalStateException("null contentHandler");
        }
        this.gideonDataFetcher.getUpdateFiles();
        try {
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            List<File> filesToParse = getXMLFiles(new File(this.basePath));
            while (!filesToParse.isEmpty()) {
                parseFile(filesToParse.remove(0));
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (SAXException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    private List<File> getXMLFiles(final File directory) {
        File[] files = directory.listFiles((final File file) -> file.isDirectory() || file.getName().endsWith(".xml")
                || file.getName().endsWith(".xml.gz"));
        List<File> result = new LinkedList<>();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(getXMLFiles(file));
                } else {
                    result.add(file);
                }
            }
        }
        return result;
    }

    private void parseFile(final File file) {
        InputSource source = new InputSource();
        try (InputStream stream = new FileInputStream(file)) {
            if (file.getName().endsWith(".gz")) {
                InputStream gzstream = new GZIPInputStream(stream);
                source.setByteStream(gzstream);
            } else {
                source.setByteStream(stream);
            }
            this.factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            this.factory.setNamespaceAware(true);
            DocumentBuilder parser = this.factory.newDocumentBuilder();
            parser.setErrorHandler(this.errorHandler);
            Document doc = parser.parse(source);
            this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
        } catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
            log.error("problem parsing {}", file);
            throw new EresourceDatabaseException(e);
        }
    }
}
