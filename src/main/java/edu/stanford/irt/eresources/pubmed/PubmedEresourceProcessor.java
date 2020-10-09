package edu.stanford.irt.eresources.pubmed;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.XMLConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public class PubmedEresourceProcessor extends AbstractEresourceProcessor {

    private static final Logger log = LoggerFactory.getLogger(PubmedEresourceProcessor.class);

    private static final int MAX_PARSES = 5;

    private static final long SLEEP_TIME = 5_000L;

    private String basePath;

    private XMLReader xmlReader;

    public PubmedEresourceProcessor(final String basePath, final XMLReader xmlReader) {
        this.basePath = basePath;
        this.xmlReader = xmlReader;
    }

    @Override
    public void process() {
        if (null == this.basePath) {
            throw new IllegalStateException("null basePath");
        }
        if (null == this.xmlReader) {
            throw new IllegalStateException("null xmlReader");
        }
        List<File> filesToParse = getXMLFiles(new File(this.basePath));
        Collections.sort(filesToParse, new PubmedFilenameComparator());
        while (!filesToParse.isEmpty()) {
            File file = filesToParse.remove(0);
            if (getStartTime() <= file.lastModified()) {
                parseFile(file);
            }
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
            this.xmlReader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            this.xmlReader.parse(source);
        } catch (UnknownHostException euh) {
            log.warn("couldn't reach host: {}", euh.getMessage());
            retryParse(source, file, 1);
        } catch (IOException | SAXException e) {
            log.error("problem parsing {}", file);
            throw new EresourceDatabaseException(e);
        }
        // touch file so we don't load it next time
        if (file.setLastModified(System.currentTimeMillis())) {
            log.info("processed: {}", file);
        } else {
            log.error("couldn't update file's timestamp: {}; make sure it's not loading on every run", file);
        }
    }

    private void retryParse(final InputSource source, final File file, final int attempt) {
        if (attempt <= MAX_PARSES) {
            try {
                this.xmlReader.parse(source);
            } catch (UnknownHostException e) {
                try {
                    long wait = SLEEP_TIME * attempt;
                    log.warn("still can't reach host: {}", e.getMessage());
                    log.warn("waiting {}ms before trying parse #: {}", wait, attempt + 1);
                    Thread.sleep(wait);
                    retryParse(source, file, attempt + 1);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    log.warn("interrupted thread: ", e1);
                }
            } catch (IOException | SAXException e) {
                log.error("problem parsing {}", file);
                throw new EresourceDatabaseException(e);
            }
        } else {
            throw new EresourceDatabaseException("too many attempts to parse: " + file);
        }
    }
}
