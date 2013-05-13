package edu.stanford.irt.eresources;

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

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.lane.catalog.impl.xml.UTF8ComposingMarcReader;

public class AuthTextAugmentation extends DefaultHandler {

    private Map<String, String> augmentations = new HashMap<String, String>();

    private StringBuilder augmentationText = new StringBuilder();

    private String code;

    private StringBuilder currentText = new StringBuilder();

    private DataSource dataSource;

    private Executor executor;

    // private String ind1;
    //
    // private String ind2;
    private String tag;

    @SuppressWarnings("unchecked")
    public AuthTextAugmentation() {
        // create a new augmentation map each Sunday:
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            this.augmentations = new HashMap<String, String>();
            // otherwise use the existing one:
        } else {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream("augmentations.obj"));
                this.augmentations = (Map<String, String>) ois.readObject();
            } catch (IOException e) {
                this.augmentations = new HashMap<String, String>();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void characters(final char[] chars, final int start, final int length) throws SAXException {
        this.currentText.append(chars, start, length);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (checkSaveContent()) {
            this.augmentationText.append(' ').append(this.currentText);
        }
    }

    public String getAuthAugmentations(final String term, final String lookupTag) {
        String result = this.augmentations.get(term);
        if (null == result) {
            XMLReader xmlReader = new UTF8ComposingMarcReader();
            xmlReader.setContentHandler(this);
            try {
                xmlReader.parse(new InputSource(new AuthAugmentationInputStream(term, lookupTag, this.dataSource,
                        this.executor)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
            result = this.augmentationText.toString().trim();
            this.augmentations.put(term, result);
        }
        return result;
    }

    public void save() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("augmentations.obj"));
        oos.writeObject(this.augmentations);
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
            // this.ind1 = atts.getValue("ind1");
            // this.ind2 = atts.getValue("ind2");
        } else if ("record".equals(localName)) {
            this.augmentationText.setLength(0);
        }
    }

    private boolean checkSaveContent() {
        // verified by DM: people records won't have 450's and MeSH records
        // won't have 400's
        return (("400".equals(this.tag) || "450".equals(this.tag)) && "a".equals(this.code));
    }
}
