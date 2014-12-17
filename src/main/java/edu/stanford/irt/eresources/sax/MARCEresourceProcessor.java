package edu.stanford.irt.eresources.sax;

import java.io.IOException;
import java.sql.Timestamp;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.EresourceInputStream;

public class MARCEresourceProcessor extends AbstractEresourceProcessor {

    private EresourceInputStream inputStream;

    private XMLReader xmlReader;

    @Override
    public void process() {
        if (null == this.inputStream) {
            throw new IllegalStateException("null inputStream");
        }
        if (null == this.xmlReader) {
            throw new IllegalStateException("null xmlReader");
        }
        this.inputStream.setStartDate(new Timestamp(getStartTime()));
        InputSource source = new InputSource();
        try {
            source.setByteStream(this.inputStream);
            this.xmlReader.parse(source);
        } catch (IOException e) {
            throw new EresourceException(e);
        } catch (SAXException e) {
            throw new EresourceException(e);
        }
    }

    public void setInputStream(final EresourceInputStream inputStream) {
        if (null == inputStream) {
            throw new IllegalArgumentException("null inputStream");
        }
        this.inputStream = inputStream;
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
