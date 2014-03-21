package edu.stanford.irt.eresources;

import java.io.IOException;
import java.sql.Timestamp;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author ceyates To change the template for this generated type comment go to Window - Preferences - Java - Code
 *         Generation - Code and Comments
 */
public class MARCEresourceProcessor extends AbstractEresourceProcessor {

    private EresourceInputStream inputStream;

    private XMLReader xmlReader;

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
            throw new EresourceDatabaseException(e);
        } catch (SAXException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void setInputStream(final EresourceInputStream inputStream) {
        if (null == inputStream) {
            throw new IllegalArgumentException("null inputStream");
        }
        this.inputStream = inputStream;
    }

    /**
     * @param reader
     *            The reader to set.
     */
    public void setXmlReader(final XMLReader xmlReader) {
        if (null == xmlReader) {
            throw new IllegalArgumentException("null xmlReader");
        }
        this.xmlReader = xmlReader;
    }
}
