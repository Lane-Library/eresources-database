package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class PubmedEresourceProcessor extends AbstractEresourceProcessor {

    private String basePath;

    private XMLReader xmlReader;

    private List<File> getXMLFiles(final File directory) {
        List<File> result = new LinkedList<File>();
        File[] files = directory.listFiles(new FileFilter() {

            @Override
            public boolean accept(final File file) {
                String name = file.getName();
                return file.isDirectory() || name.endsWith(".xml") || name.endsWith(".xml.gz");
            }
        });
        for (File file : files) {
            if (file.isDirectory()) {
                result.addAll(getXMLFiles(file));
            } else {
                result.add(file);
            }
        }
        return result;
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
        while (filesToParse.size() > 0) {
            InputSource source = new InputSource();
            File file = filesToParse.remove(0);
            if (this.startDate.getTime() <= file.lastModified()) {
                try {
                    if (file.getName().matches(".*\\.gz")) {
                        source.setByteStream(new GZIPInputStream(new FileInputStream(file)));
                    } else {
                        source.setByteStream(new FileInputStream(file));
                    }
                    this.xmlReader.parse(source);
                } catch (IOException e) {
                    throw new EresourceDatabaseException(e);
                } catch (SAXException e) {
                    throw new EresourceDatabaseException(e);
                }
            }
        }
    }

    public void setBasePath(final String basePath) {
        if (null == basePath) {
            throw new IllegalArgumentException("null basePath");
        }
        this.basePath = basePath;
    }

    public void setXmlReader(final XMLReader xmlReader) {
        if (null == xmlReader) {
            throw new IllegalArgumentException("null xmlReader");
        }
        this.xmlReader = xmlReader;
    }
}
