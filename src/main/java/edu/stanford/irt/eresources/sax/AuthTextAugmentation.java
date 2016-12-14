package edu.stanford.irt.eresources.sax;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.sql.DataSource;
import javax.xml.XMLConstants;

import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

//import edu.stanford.irt.eresources.AuthAugmentationInputStream;
import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.lane.catalog.impl.xml.UTF8ComposingMarcReader;

public class AuthTextAugmentation extends DefaultHandler {

    private Map<String, String> augmentations = new HashMap<>();

    private StringBuilder augmentationText = new StringBuilder();

    private String code;

    private StringBuilder currentText = new StringBuilder();

    private DataSource dataSource;

    private Executor executor;

    private String tag;

    @SuppressWarnings("unchecked")
    public AuthTextAugmentation() {
        // create a new augmentation map each Sunday:
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            this.augmentations = new HashMap<>();
            // otherwise use the existing one:
        } else {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("augmentations.obj"))) {
                this.augmentations = (Map<String, String>) ois.readObject();
            } catch (IOException e) {
                LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                this.augmentations = new HashMap<>();
            } catch (ClassNotFoundException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }

    @Override
    public void characters(final char[] chars, final int start, final int length) throws SAXException {
        this.currentText.append(chars, start, length);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if ("subfield".equals(localName) && checkSaveContent()) {
            this.augmentationText.append(' ').append(this.currentText);
        }
    }

    // FIXME: load once a week
    public String getAuthAugmentations(final String controlNumber) {
        String result = this.augmentations.get(controlNumber);
//        if (null == result) {
//            this.augmentationText.setLength(0);
//            this.code = null;
//            this.currentText.setLength(0);
//            this.tag = null;
//            XMLReader xmlReader = new UTF8ComposingMarcReader();
//            try {
//                xmlReader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
//                xmlReader.setContentHandler(this);
//                xmlReader.parse(new InputSource(
//                        new AuthAugmentationInputStream(controlNumber, this.dataSource, this.executor)));
//            } catch (IOException e) {
//                throw new EresourceDatabaseException(e);
//            } catch (SAXException e) {
//                throw new EresourceDatabaseException(e);
//            }
//            result = this.augmentationText.toString().trim();
//            this.augmentations.put(controlNumber, result);
//        }
        return result;
    }

    public void save() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("augmentations.obj"))) {
            oos.writeObject(this.augmentations);
        }
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
            throws SAXException {
        this.currentText.setLength(0);
        if ("subfield".equals(localName)) {
            this.code = atts.getValue("code");
        } else if ("datafield".equals(localName)) {
            this.tag = atts.getValue("tag");
        }
    }

    private boolean checkSaveContent() {
        // verified by DM: people records won't have 450's and MeSH records
        // won't have 400's
        return ("400".equals(this.tag) || "450".equals(this.tag)) && "a".equals(this.code);
    }
}
