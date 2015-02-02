package edu.stanford.irt.eresources.sax;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.PubmedSpecialTypesManager;

/**
 *
 */
public class PubmedEresourceBuilder extends DefaultHandler implements EresourceBuilder {

    private SAXEresource currentEresource;

    private SAXLink currentLink;

    private StringBuilder currentText;

    private SAXVersion currentVersion;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private EresourceHandler eresourceHandler;

    private PubmedSpecialTypesManager specialTypesManager;

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
        } else if ("er-description".equals(name)) {
            this.currentEresource.setDescription(this.currentText.toString());
        } else if ("description".equals(name)) {
            this.currentVersion.setDescription(this.currentText.toString());
        } else if ("publicationAuthor".equals(name)) {
            this.currentEresource.addPublicationAuthor(this.currentText.toString());
        } else if ("publicationAuthorsText".equals(name)) {
            this.currentEresource.setPublicationAuthorsText(this.currentText.toString());
        } else if ("publicationDate".equals(name)) {
            String pDate = this.currentText.toString();
            this.currentEresource.setPublicationDate(pDate);
        } else if ("publicationIssue".equals(name)) {
            this.currentEresource.setPublicationIssue(this.currentText.toString());
        } else if ("publicationLanguage".equals(name)) {
            this.currentEresource.addPublicationLanguage(this.currentText.toString());
        } else if ("publicationPages".equals(name)) {
            this.currentEresource.setPublicationPages(this.currentText.toString());
        } else if ("publicationTitle".equals(name)) {
            this.currentEresource.setPublicationTitle(this.currentText.toString());
        } else if ("publicationType".equals(name)) {
            this.currentEresource.addPublicationType(this.currentText.toString());
        } else if ("publicationVolume".equals(name)) {
            this.currentEresource.setPublicationVolume(this.currentText.toString());
        } else if ("year".equals(name)) {
            this.currentEresource.setYear(Integer.parseInt(this.currentText.toString()));
        } else if ("pmid".equals(name)) {
            this.currentEresource.setPmid(this.currentText.toString());
        } else if ("doi".equals(name)) {
            this.currentEresource.setDoi(this.currentText.toString());
        } else if ("primaryType".equals(name)) {
            this.currentEresource.setPrimaryType(this.currentText.toString());
        } else if (!"eresources".equals(name)) {
            throw new EresourceDatabaseException("cant handle " + name);
        }
    }

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
            String id = atts.getValue("id");
            this.currentEresource = new SAXEresource();
            this.currentEresource.setRecordId(Integer.parseInt(id));
            this.currentEresource.setRecordType(atts.getValue("type"));
            for (String type : this.specialTypesManager.getTypes(id)) {
                this.currentEresource.addPublicationType(type);
            }
            try {
                this.currentEresource.setUpdated(this.dateFormat.parse(atts.getValue("update")));
            } catch (ParseException e) {
                throw new EresourceDatabaseException(e);
            }
        } else if ("version".equals(name)) {
            this.currentVersion = new SAXVersion();
        } else if ("link".equals(name)) {
            this.currentLink = new SAXLink();
        }
    }
}
