package edu.stanford.irt.eresources.marc;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;


public class CatalogLink implements Link {
    
    private String url;

    public CatalogLink(int recordId) {
        
        this.url = "http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID=" + recordId;
    }

    @Override
    public String getAdditionalText() {
        return "";
    }

    @Override
    public String getInstruction() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLabel() {
        return "Lane Catalog record";
    }

    @Override
    public String getLinkText() {
        return "Lane Catalog record";
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setVersion(Version version) {
        // TODO Auto-generated method stub
    }
}
