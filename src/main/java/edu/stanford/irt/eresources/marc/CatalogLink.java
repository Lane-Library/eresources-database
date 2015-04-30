package edu.stanford.irt.eresources.marc;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class CatalogLink implements Link {

    private String url;

    public CatalogLink(final int recordId) {
        this.url = "http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID=" + recordId;
    }

    @Override
    public String getAdditionalText() {
        return null;
    }

    @Override
    public String getInstruction() {
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
    public void setVersion(final Version version) {
    }
}
