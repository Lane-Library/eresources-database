package edu.stanford.irt.eresources.sax;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.pubmed.PubmedSpecialTypesManager;

/**
 *
 */
public class PubmedEresourceBuilder extends DefaultHandler implements EresourceBuilder {

    private static final int SOLR_FIELD_MAX = 32766;

    protected static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss")
            .toFormatter();

    private EresourceHandler eresourceHandler;

    private PubmedSpecialTypesManager specialTypesManager;

    protected SAXEresource currentEresource;

    protected SAXLink currentLink;

    protected StringBuilder currentText;

    protected SAXVersion currentVersion;

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
                this.currentEresource.setPrimaryType(this.currentText.toString());
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
            case "instruction":
                this.currentLink.setInstruction(this.currentText.toString());
                break;
            case "er-description":
                this.currentEresource.setDescription(this.currentText.toString());
                break;
            case "publicationAuthor":
                limitFieldLength();
                this.currentEresource.addPublicationAuthor(this.currentText.toString());
                break;
            case "publicationAuthorFacetable":
                limitFieldLength();
                this.currentEresource.addPublicationAuthorFacetable(this.currentText.toString());
                break;
            case "publicationAuthorsText":
                this.currentEresource.setPublicationAuthorsText(this.currentText.toString());
                break;
            case "publicationDate":
                this.currentEresource.setPublicationDate(this.currentText.toString());
                break;
            case "publicationIssue":
                this.currentEresource.setPublicationIssue(this.currentText.toString());
                break;
            case "publicationLanguage":
                this.currentEresource.addPublicationLanguage(this.currentText.toString());
                break;
            case "publicationPages":
                this.currentEresource.setPublicationPages(this.currentText.toString());
                break;
            case "publicationTitle":
                this.currentEresource.setPublicationTitle(this.currentText.toString());
                break;
            case "publicationType":
                this.currentEresource.addPublicationType(this.currentText.toString());
                break;
            case "publicationVolume":
                this.currentEresource.setPublicationVolume(this.currentText.toString());
                break;
            case "year":
                this.currentEresource.setYear(Integer.parseInt(this.currentText.toString()));
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

    public void setSpecialTypesManager(final PubmedSpecialTypesManager specialTypesManager) {
        this.specialTypesManager = specialTypesManager;
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
            String recordId = atts.getValue("recordId");
            this.currentEresource = new SAXEresource();
            this.currentEresource.setRecordId(recordId);
            this.currentEresource.setRecordType(atts.getValue("type"));
            this.currentEresource.setId(atts.getValue("id"));
            getSpecialTypesForPmid(recordId);
        } else if ("version".equals(name)) {
            this.currentVersion = new SAXVersion();
        } else if ("link".equals(name)) {
            this.currentLink = new SAXLink();
        }
    }

    private void getSpecialTypesForPmid(final String pmid) {
        for (String[] fieldAndValue : this.specialTypesManager.getTypes(pmid)) {
            String field = fieldAndValue[0];
            String value = fieldAndValue[1];
            if ("publicationType".equals(field)) {
                this.currentEresource.addPublicationType(value);
            } else {
                throw new EresourceDatabaseException("unknown field: " + field + " pmid " + pmid + " value " + value);
            }
        }
    }

    // LANEWEB-10933: bad author data in one article - 35369709
    private void limitFieldLength() {
        try {
            while (this.currentText.toString().getBytes(StandardCharsets.UTF_8.name()).length > SOLR_FIELD_MAX) {
                this.currentText.deleteCharAt(this.currentText.toString().length() - 1);
            }
        } catch (UnsupportedEncodingException e) {
            // won't happen
        }
    }
}
