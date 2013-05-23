package edu.stanford.irt.eresources;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 */
/**
 * @author ceyates
 * @param <EresourceImpl>
 */
public class DefaultEresourceBuilder extends DefaultHandler implements EresourceBuilder {

    private DatabaseEresource currentEresource;

    private DatabaseLink currentLink;

    private StringBuilder currentText;

    private DatabaseVersion currentVersion;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private EresourceHandler eresourceHandler;

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.currentText.append(ch, start, length);
    }

    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if ("eresource".equals(name)) {
            this.eresourceHandler.handleEresource(this.currentEresource);
            this.currentEresource = null;
        } else if ("version".equals(name)) {
            this.currentEresource.addVersion(this.currentVersion);
            this.currentVersion = null;
        } else if ("link".equals(name)) {
            this.currentVersion.addLink(this.currentLink);
            this.currentLink = null;
        } else if ("url".equals(name)) {
            this.currentLink.setUrl(this.currentText.toString());
        } else if ("label".equals(name)) {
            this.currentLink.setLabel(this.currentText.toString());
        } else if ("date".equals(name)) {
            this.currentVersion.setDates(this.currentText.toString());
        } else if ("summary-holdings".equals(name)) {
            this.currentVersion.setSummaryHoldings(this.currentText.toString());
        } else if ("subset".equals(name)) {
            String subset = this.currentText.toString();
            if ("proxy".equals(subset)) {
                this.currentVersion.setProxy(true);
            } else if ("noproxy".equals(subset)) {
                this.currentVersion.setProxy(false);
            } else {
                this.currentVersion.addSubset(subset);
            }
        } else if ("publisher".equals(name)) {
            this.currentVersion.setPublisher(this.currentText.toString());
        } else if ("type".equals(name)) {
            this.currentEresource.addType(this.currentText.toString());
        } else if ("keywords".equals(name)) {
            this.currentEresource.setKeywords(this.currentText.toString());
        } else if ("mesh".equals(name)) {
            this.currentEresource.addMeshTerm(this.currentText.toString());
        } else if ("title".equals(name)) {
            this.currentEresource.setTitle(this.currentText.toString());
        } else if ("instruction".equals(name)) {
            this.currentLink.setInstruction(this.currentText.toString());
        } else if ("description".equals(name)) {
            this.currentVersion.setDescription(this.currentText.toString());
        } else if (!"eresources".equals(name)) {
            throw new EresourceDatabaseException("cant handle " + name);
        }
    }

    public void setEresourceHandler(final EresourceHandler eresourceHandler) {
        this.eresourceHandler = eresourceHandler;
    }

    @Override
    public void startDocument() throws SAXException {
        if (null == this.eresourceHandler) {
            throw new IllegalStateException("null eresourceHandler");
        }
        this.currentText = new StringBuilder();
    }

    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes atts)
            throws SAXException {
        this.currentText.setLength(0);
        if ("eresource".equals(name)) {
            this.currentEresource = new DatabaseEresource();
            this.currentEresource.setRecordId(Integer.parseInt(atts.getValue("id")));
            this.currentEresource.setRecordType(atts.getValue("type"));
            try {
                this.currentEresource.setUpdated(this.dateFormat.parse(atts.getValue("update")));
            } catch (ParseException e) {
                throw new EresourceDatabaseException(e);
            }
        } else if ("version".equals(name)) {
            this.currentVersion = new DatabaseVersion();
        } else if ("link".equals(name)) {
            this.currentLink = new DatabaseLink();
        }
    }
}
