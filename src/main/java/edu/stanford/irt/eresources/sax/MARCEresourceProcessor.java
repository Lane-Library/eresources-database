package edu.stanford.irt.eresources.sax;

import java.io.IOException;

import javax.xml.XMLConstants;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public class MARCEresourceProcessor extends AbstractEresourceProcessor {

    private CatalogRecordService service;

    private XMLReader xmlReader;

    public MARCEresourceProcessor(final CatalogRecordService service) {
        this.service = service;
    }
    @Override
    public void process() {
        if (null == this.service) {
            throw new IllegalStateException("null service");
        }
        if (null == this.xmlReader) {
            throw new IllegalStateException("null xmlReader");
        }
        InputSource source = new InputSource();
        try {
            source.setByteStream(this.service.getRecordStream(getStartTime()));
            this.xmlReader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            this.xmlReader.parse(source);
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        } catch (SAXException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    /**
     * @param xmlReader
     *            The reader to set.
     */
    public void setXmlReader(final XMLReader xmlReader) {
        if (null == xmlReader) {
            throw new IllegalArgumentException("null xmlReader");
        }
        this.xmlReader = xmlReader;
    }
}
