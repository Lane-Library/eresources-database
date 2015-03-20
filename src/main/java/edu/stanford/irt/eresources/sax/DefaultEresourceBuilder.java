package edu.stanford.irt.eresources.sax;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.EresourceException;

public class DefaultEresourceBuilder extends DefaultHandler {

    private SAXEresource currentEresource;

    private SAXLink currentLink;

    private StringBuilder currentText = new StringBuilder();

    private SAXVersion currentVersion;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private EresourceHandler eresourceHandler;

    public DefaultEresourceBuilder(final EresourceHandler eresourceHandler) {
        this.eresourceHandler = eresourceHandler;
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        this.currentText.append(ch, start, length);
    }

    @Override
    public void endElement(final String uri, final String localName, final String name) {
        if ("eresource".equals(name)) {
            this.eresourceHandler.handleEresource(this.currentEresource);
            this.currentEresource = null;
        } else if ("version".equals(name)) {
            this.currentEresource.addVersion(this.currentVersion);
            this.currentVersion = null;
        } else if ("link".equals(name)) {
            this.currentVersion.addLink(this.currentLink);
            this.currentLink.setVersion(this.currentVersion);
            this.currentLink = null;
        } else if ("url".equals(name)) {
            this.currentLink.setUrl(this.currentText.toString());
        } else if ("label".equals(name)) {
            this.currentLink.setLabel(this.currentText.toString());
        } else if ("date".equals(name)) {
            this.currentVersion.setDates(this.currentText.toString());
        } else if ("summary-holdings".equals(name)) {
            this.currentVersion.setSummaryHoldings(this.currentText.toString());
        } else if ("publisher".equals(name)) {
            this.currentVersion.setPublisher(this.currentText.toString());
        } else if ("type".equals(name)) {
            this.currentEresource.addType(this.currentText.toString());
        } else if ("primaryType".equals(name)) {
            String type = this.currentText.toString();
            this.currentEresource.setPrimaryType(type);
            this.currentEresource.addType(type);
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
            throw new EresourceException("cant handle " + name);
        }
    }

    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes atts) {
        this.currentText.setLength(0);
        if ("eresource".equals(name)) {
            this.currentEresource = new SAXEresource();
            this.currentEresource.setRecordId(Integer.parseInt(atts.getValue("id")));
            this.currentEresource.setRecordType(atts.getValue("type"));
            try {
                this.currentEresource.setUpdated(this.dateFormat.parse(atts.getValue("update")));
            } catch (ParseException e) {
                throw new EresourceException(e);
            }
        } else if ("version".equals(name)) {
            this.currentVersion = new SAXVersion();
        } else if ("link".equals(name)) {
            this.currentLink = new SAXLink();
        }
    }
}
