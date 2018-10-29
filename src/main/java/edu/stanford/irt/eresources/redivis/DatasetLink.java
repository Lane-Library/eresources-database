package edu.stanford.irt.eresources.redivis;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class DatasetLink implements Link {

    private Dataset dataset;

    private String label = "Redivis";

    private String url;

    private Version version;

    public DatasetLink(final Dataset dataset, final DatasetVersion version) {
        this.dataset = dataset;
        this.version = version;
        this.url = this.dataset.getUrl();
    }

    @Override
    public String getAdditionalText() {
        return null;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getLinkText() {
        return this.version.getDates();
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setVersion(final Version version) {
        // not implemented
    }
}
