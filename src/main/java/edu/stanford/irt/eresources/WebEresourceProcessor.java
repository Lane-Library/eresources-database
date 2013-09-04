package edu.stanford.irt.eresources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.poi.hslf.exceptions.EncryptedPowerPointFileException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class WebEresourceProcessor extends AbstractEresourceProcessor {

    private static final String ERESOURCES = "eresources";

    private String basePath;

    private ContentHandler contentHandler;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private List<File> getFiles(final File directory) {
        List<File> result = new LinkedList<File>();
        File[] files = directory.listFiles(new FileFilter() {

            public boolean accept(final File file) {
                String name = file.getName();
                return name.matches(".*\\.(html|ppt|docx|pdf|xls?)$")
                        || (file.isDirectory() && !".svn".equals(name) && !"includes".equals(name)
                                && !"search".equals(name) && !"tobacco".equals(name) && !"samples".equals(name)
                                && !"m".equals(name) && !"secure".equals(name));
            }
        });
        for (File file : files) {
            if (file.isDirectory()) {
                result.addAll(getFiles(file));
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
    private boolean isSearchable(final Metadata metadata) {
        boolean searchable = true;
        String noIndex = metadata.get("robots");
        if (null != noIndex && "noindex".equalsIgnoreCase(noIndex)) {
            searchable = false;
        }
        return searchable;
    }

    public void process() {
        if (null == this.basePath) {
            throw new IllegalArgumentException("null basePath");
        }
        if (null == this.contentHandler) {
            throw new IllegalArgumentException("null contentHandler");
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        List<File> filesToParse = getFiles(new File(this.basePath));
        try {
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            while (filesToParse.size() > 0) {
                File file = filesToParse.remove(0);
                String fileName = file.getAbsolutePath();
                Metadata metadata = new Metadata();
                metadata.set("id", Integer.toString(fileName.hashCode()));
                metadata.set("file", fileName.substring(this.basePath.length()));
                metadata.set("update", this.dateFormat.format(file.lastModified()));
                AutoDetectParser parser = new AutoDetectParser();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ContentHandler tikaContenthandler = new ToXMLContentHandler(outputStream, "UTF-8");
                try {
                    parser.parse(new FileInputStream(file), tikaContenthandler, metadata, new ParseContext());
                } catch (FileNotFoundException e) {
                    continue;
                } catch (TikaException e) {
                    if (EncryptedPowerPointFileException.class.equals(e.getCause().getClass())) {
                        continue;
                    }
                    throw new EresourceDatabaseException(e);
                }
                InputSource source = new InputSource();
                source.setEncoding("UTF-8");
                source.setByteStream(new ByteArrayInputStream(outputStream.toByteArray()));
                if (file.lastModified() > this.startDate.getTime() && isSearchable(metadata)) {
                    tf.newTransformer().transform(new SAXSource(source), new SAXResult(this.contentHandler));
                }
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (SAXException e) {
            throw new EresourceDatabaseException(e);
        } catch (TransformerConfigurationException e) {
            throw new EresourceDatabaseException(e);
        } catch (TransformerException e) {
            throw new EresourceDatabaseException(e);
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }
}
