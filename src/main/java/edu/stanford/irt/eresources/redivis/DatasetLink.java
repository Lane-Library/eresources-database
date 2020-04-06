package edu.stanford.irt.eresources.redivis;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class DatasetLink implements Link {

    private Result dataset;

    private String label = "Redivis";

    private Version version;

    public DatasetLink(final Result dataset, final DatasetVersion version) {
        this.dataset = dataset;
        this.version = version;
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
        return this.dataset.getUrl();
    }

    @Override
    public void setVersion(final Version version) {
        // not implemented
    }
}
