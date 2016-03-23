package edu.stanford.irt.eresources.sax.videos.bates;

import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.sax.videos.AbstractVideoEresourceProcessor;

public class BatesEresourceProcessor extends AbstractVideoEresourceProcessor {

    private final String ERESOURCE_TYPE = "bates";

    private String title = null;
    private String description = null;
    private String year = null;
    private String authors = null;
    
    
    @Override
    public void process() {
        try {
            StringBuilder keywords = new StringBuilder();
            keywords.append(title);
            keywords.append(" physical exam bates ");
            keywords.append(description);
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            super.processEresource("1", ERESOURCE_TYPE, title, description, keywords.toString(), year, null, this.URLs.get(0), authors);
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        }
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
  
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setYear(String year) {
        this.year = year;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }
}
