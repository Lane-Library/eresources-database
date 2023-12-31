package edu.stanford.irt.eresources.sax;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.EresourceHandler;

public class DefaultEresourceBuilder extends DefaultHandler implements EresourceBuilder {

    protected static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss")
            .toFormatter();

    private SAXEresource currentEresource;

    private SAXLink currentLink;

    private StringBuilder currentText;

    private SAXVersion currentVersion;

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
        } else if ("mesh_broad".equals(name)) {
            this.currentEresource.addBroadMeshTerm(this.currentText.toString());
        } else if ("title".equals(name)) {
            this.currentEresource.setTitle(this.currentText.toString());
        } else if ("title_abbr".equals(name)) {
            this.currentEresource.addAbbreviatedTitle(this.currentText.toString());
        } else if ("title_alt".equals(name)) {
            this.currentEresource.addAlternativeTitle(this.currentText.toString());
        } else if ("title_short".equals(name)) {
            this.currentEresource.setShortTitle(this.currentText.toString());
        } else if ("version-additionalText".equals(name)) {
            this.currentVersion.setAdditionalText(this.currentText.toString());
        } else if ("instruction".equals(name)) {
            this.currentLink.setInstruction(this.currentText.toString());
        } else if ("description".equals(name)) {
            this.currentEresource.setDescription(this.currentText.toString());
        } else if ("publicationAuthorsText".equals(name)) {
            this.currentEresource.setPublicationAuthorsText(this.currentText.toString());
        } else if ("publicationAuthor".equals(name)) {
            this.currentEresource.addPublicationAuthor(this.currentText.toString());
        } else if ("publicationAuthorFacetable".equals(name)) {
            this.currentEresource.addPublicationAuthorFacetable(this.currentText.toString());
        } else if ("year".equals(name)) {
            this.currentEresource.setYear(Integer.parseInt(this.currentText.toString()));
        } else if ("er-date".equals(name)) {
            this.currentEresource.setDate(this.currentText.toString());
        } else if ("issn".equals(name)) {
            this.currentEresource.addIssn(this.currentText.toString());
        } else if (!"eresources".equals(name)) {
            throw new EresourceDatabaseException("cant handle " + name);
        }
    }

    @Override
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
            this.currentEresource = new SAXEresource();
            this.currentEresource.setId(atts.getValue("id"));
            this.currentEresource.setRecordId(atts.getValue("recordId"));
            this.currentEresource.setRecordType(atts.getValue("type"));
            this.currentEresource.setUpdated(LocalDateTime.parse(atts.getValue("update"), FORMATTER));
        } else if ("version".equals(name)) {
            this.currentVersion = new SAXVersion();
        } else if ("link".equals(name)) {
            this.currentLink = new SAXLink();
        }
    }

    protected SAXEresource getCurrentEresource() {
        return this.currentEresource;
    }

    protected String getCurrentText() {
        return this.currentText.toString();
    }
}
