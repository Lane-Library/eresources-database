package edu.stanford.irt.eresources.sax.videos.bates;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.sax.videos.AbstractVideoEresourceProcessor;

public class BatesEresourceProcessor extends AbstractVideoEresourceProcessor {

    private List<String> authors = null;

    private String description = null;

    private final String ERESOURCE_TYPE = "bates";

    private String title = null;

    private String year = null;

    @Override
    public void process() {
        try {
            StringBuilder keywords = new StringBuilder();
            keywords.append(this.title);
            keywords.append(" physical exam bates ");
            keywords.append(this.description);
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            super.processEresource(this.ERESOURCE_TYPE + "-1", "1", AbstractVideoEresourceProcessor.EXTRENAL_VIDEO,
                    this.title, this.description, keywords.toString(), this.year, null, this.URLs.get(0), this.authors);
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void setAuthors(final String author) {
        this.authors = new ArrayList<String>();
        this.authors.add(author);
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setYear(final String year) {
        this.year = year;
    }

    @Override
    protected void startEresourceElement(final String id, final String recordId, final String type)
            throws SAXException {
        super.startEresourceElement(id, recordId, type);
        createElement(TYPE, AbstractVideoEresourceProcessor.VIDEO_PHYSICAL_EXAM);
    }
}
