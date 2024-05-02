package edu.stanford.irt.eresources.sax;

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
        switch (name) {
            case "eresource":
                this.eresourceHandler.handleEresource(this.currentEresource);
                this.currentEresource = null;
                break;
            case "version":
                this.currentEresource.addVersion(this.currentVersion);
                this.currentVersion = null;
                break;
            case "link":
                this.currentVersion.addLink(this.currentLink);
                this.currentLink = null;
                break;
            case "url":
                this.currentLink.setUrl(this.currentText.toString());
                break;
            case "label":
                this.currentLink.setLabel(this.currentText.toString());
                break;
            case "date":
                this.currentVersion.setDates(this.currentText.toString());
                break;
            case "summary-holdings":
                this.currentVersion.setSummaryHoldings(this.currentText.toString());
                break;
            case "publisher":
                this.currentVersion.setPublisher(this.currentText.toString());
                break;
            case "type":
                this.currentEresource.addType(this.currentText.toString());
                break;
            case "primaryType":
                String type = this.currentText.toString();
                this.currentEresource.setPrimaryType(type);
                this.currentEresource.addType(type);
                break;
            case "keywords":
                this.currentEresource.setKeywords(this.currentText.toString());
                break;
            case "mesh":
                this.currentEresource.addMeshTerm(this.currentText.toString());
                break;
            case "mesh_broad":
                this.currentEresource.addBroadMeshTerm(this.currentText.toString());
                break;
            case "title":
                this.currentEresource.setTitle(this.currentText.toString());
                break;
            case "title_abbr":
                this.currentEresource.addAbbreviatedTitle(this.currentText.toString());
                break;
            case "title_alt":
                this.currentEresource.addAlternativeTitle(this.currentText.toString());
                break;
            case "title_short":
                this.currentEresource.setShortTitle(this.currentText.toString());
                break;
            case "version-additionalText":
                this.currentVersion.setAdditionalText(this.currentText.toString());
                break;
            case "instruction":
                this.currentLink.setInstruction(this.currentText.toString());
                break;
            case "description":
                this.currentEresource.setDescription(this.currentText.toString());
                break;
            case "publicationAuthorsText":
                this.currentEresource.setPublicationAuthorsText(this.currentText.toString());
                break;
            case "publicationAuthor":
                this.currentEresource.addPublicationAuthor(this.currentText.toString());
                break;
            case "publicationAuthorFacetable":
                this.currentEresource.addPublicationAuthorFacetable(this.currentText.toString());
                break;
            case "year":
                this.currentEresource.setYear(Integer.parseInt(this.currentText.toString()));
                break;
            case "er-date":
                this.currentEresource.setDate(this.currentText.toString());
                break;
            case "issn":
                this.currentEresource.addIssn(this.currentText.toString());
                break;
            case "eresources":
                break;
            default:
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
