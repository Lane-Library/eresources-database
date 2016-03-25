package edu.stanford.irt.eresources.sax.videos;

import java.util.List;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;

    

    public abstract class AbstractVideoEresourceProcessor  extends AbstractEresourceProcessor {

    protected ContentHandler contentHandler;
    
    protected List<String> URLs;
    
    public static final String EXTRENAL_VIDEO = "instructional_videos";
    
    public static final String INSTRUCTIONAL_VIDEO = "Video: Instructional";
    
    public static final String VIDEO_PHYSICAL_EXAM = "Video: Physical Exam";
        
    protected static final String ERESOURCES = "eresources";

    protected static final String ERESOURCE = "eresource";

    protected static final String TITLE = "title";

    protected static final String ID = "id";
    
    protected static final String RECORD_ID = "recordId";

    protected static final String TYPE = "type";

    protected static final String UPDATE = "update";

    protected static final String CDATA = "CDATA";

    protected static final String PRIMARY_TYPE = "primaryType";

    protected static final String VISUAL_MATERIAL = "Visual Material";

    protected static final String VIDEO = "Video";
   
    protected static final String YEAR = "year";
    
    protected static final String AUTHORS = "publicationAuthorsText";

    protected static final String DESCRIPTION = "description";

    protected static final String VERSION = "version";

    protected static final String LINK = "link";

    protected static final String URL = "url";

    protected static final String LABEL = "label";
    
    protected static final String KEYWORDS = "keywords";

    protected static final String ER_DATE = "er-date";
    
    protected void startEresourceElement(String id, String recordId, String type) throws SAXException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", ID, ID, CDATA, id);
        attrs.addAttribute("", RECORD_ID, RECORD_ID, CDATA, recordId);
        attrs.addAttribute("", TYPE, TYPE, CDATA, type);
        attrs.addAttribute("", UPDATE, UPDATE, CDATA, "19690101000000");
        this.contentHandler.startElement("", ERESOURCE, ERESOURCE, attrs);
        createElement(PRIMARY_TYPE, VISUAL_MATERIAL);
        createElement(TYPE, INSTRUCTIONAL_VIDEO);
        createElement(TYPE, VIDEO);
    }

    protected void createElement(String name, String value) throws SAXException {
        this.contentHandler.startElement("", name, name, new AttributesImpl());
        this.contentHandler.characters(value.toCharArray(), 0, value.length());
        this.contentHandler.endElement("", name, name);
    }
    
    protected void processEresource(String id, String recordId, String eresoursceType, String title, String description, String keywords,
            String year, String date, String url, String authors) throws SAXException {
        startEresourceElement(id, recordId, eresoursceType);
        if (title != null) {
            createElement(TITLE, title);
        }
        if (description != null) {
            createElement(DESCRIPTION, description);
        }
        createElement(KEYWORDS, keywords);
        if (null != year) {
            createElement(YEAR, year);
        }
        if (date != null) {
            createElement(ER_DATE,date);
        }
        this.contentHandler.startElement("", VERSION, VERSION, new AttributesImpl());
        this.contentHandler.startElement("", LINK, LINK, new AttributesImpl());
        if (null != url) {
            createElement(URL, url);
        }
        if (null != authors) {
            createElement(AUTHORS, authors);
        }
        this.contentHandler.endElement("", LINK, LINK);
        this.contentHandler.endElement("", VERSION, VERSION);
        this.contentHandler.endElement("", ERESOURCE, ERESOURCE);
    }
    
    

    
    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public void setURLs(final List<String> URLs) {
        this.URLs = URLs;
    }
    
}


    
