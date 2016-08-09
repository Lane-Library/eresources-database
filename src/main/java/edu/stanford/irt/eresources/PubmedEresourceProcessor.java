package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class PubmedEresourceProcessor extends AbstractEresourceProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PubmedEresourceProcessor.class);

    private String basePath;

    private XMLReader xmlReader;

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

    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    public void setXmlReader(final XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }

    private List<File> getXMLFiles(final File directory) {
        List<File> result = new LinkedList<File>();
        File[] files = directory.listFiles(new FileFilter() {

            @Override
            public boolean accept(final File file) {
                String name = file.getName();
                return file.isDirectory() || name.endsWith(".xml") || name.endsWith(".xml.gz");
            }
        });
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
        try {
            if (file.getName().matches(".*\\.gz")) {
                source.setByteStream(new GZIPInputStream(new FileInputStream(file)));
            } else {
                source.setByteStream(new FileInputStream(file));
            }
            this.xmlReader.parse(source);
            // touch file so we don't load it next time
            if (file.setLastModified(System.currentTimeMillis())) {
                LOG.info("processed: " + file);
            } else {
                LOG.error("couldn't update file's timestamp; make sure it's not loading on every run");
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        } catch (SAXException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
