package edu.stanford.irt.eresources.redivis;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.redivis.Dataset.CurrentVersion;

public class DatasetVersion implements Version {

    private Dataset dataset;

    public DatasetVersion(final Dataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public String getAdditionalText() {
        if (!"data".equals(this.dataset.getAccessLevel())) {
            return "Users will need to submit required information to view content";
        }
        return null;
    }

    @Override
    public String getDates() {
        CurrentVersion cv = this.dataset.getCurrentVersion();
        TemporalRange tr = null;
        if (null != cv) {
            tr = cv.getTemporalRange();
        }
        if (null != tr) {
            return tr.getDisplayRange();
        }
        return null;
    }

    @Override
    public String getHoldingsAndDates() {
        return null;
    }

    @Override
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<>();
        links.add(new DatasetLink(this.dataset, this));
        return links;
    }

    @Override
    public String getPublisher() {
        return null;
    }

    @Override
    public String getSummaryHoldings() {
        return null;
    }

    @Override
    public boolean hasGetPasswordLink() {
        return false;
    }

    @Override
    public boolean isProxy() {
        return false;
    }
}
