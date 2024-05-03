package edu.stanford.irt.eresources.sax;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.pubmed.PubmedSpecialTypesManager;

public class PubmedEresourceBuilder extends DefaultEresourceBuilder {

    private static final Collection<String> HANDLED_BY_SUPER = Arrays.asList("eresource", "version", "link", "url",
            "label", "date", "summary-holdings", "publisher", "type", "primaryType", "keywords", "mesh", "mesh_broad",
            "title", "instruction", "description", "publicationAuthorsText", "year", "eresources");

    private static final int SOLR_FIELD_MAX = 32766;

    private PubmedSpecialTypesManager specialTypesManager;

    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if (HANDLED_BY_SUPER.contains(name)) {
            super.endElement(uri, localName, name);
        } else {
            switch (name) {
                case "publicationAuthor":
                    limitFieldLength();
                    this.currentEresource.addPublicationAuthor(this.currentText.toString());
                    break;
                case "publicationAuthorFacetable":
                    limitFieldLength();
                    this.currentEresource.addPublicationAuthorFacetable(this.currentText.toString());
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
                default:
                    throw new EresourceDatabaseException("cant handle " + name);
            }
        }
    }

    public void setSpecialTypesManager(final PubmedSpecialTypesManager specialTypesManager) {
        this.specialTypesManager = specialTypesManager;
    }

    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes atts)
            throws SAXException {
        super.startElement(uri, localName, name, atts);
        if ("eresource".equals(name)) {
            getSpecialTypesForPmid(atts.getValue("recordId"));
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
