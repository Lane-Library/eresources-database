package edu.stanford.irt.eresources.marc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.stanford.irt.eresources.AbstractVersion;
import edu.stanford.irt.eresources.Link;

public class CatalogVersion extends AbstractVersion {

    private List<Link> links;

    public CatalogVersion(final int recordId) {
        this.links = Collections.<Link> singletonList(new CatalogLink(recordId));
    }

    @Override
    public String getAdditionalText() {
        return " Lane Catalog record ";
    }

    @Override
    public String getDates() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<Link> getLinks() {
        return this.links;
    }

    @Override
    public String getPublisher() {
        return null;
    }

    @Override
    public Collection<String> getSubsets() {
        return Collections.emptySet();
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
        return true;
    }
}
