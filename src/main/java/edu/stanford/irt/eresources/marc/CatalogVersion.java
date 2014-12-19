package edu.stanford.irt.eresources.marc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class CatalogVersion implements Version {

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Link> getLinks() {
        return this.links;
    }

    @Override
    public String getPublisher() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> getSubsets() {
        return Collections.emptySet();
    }

    @Override
    public String getSummaryHoldings() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasGetPasswordLink() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isProxy() {
        return true;
    }
}
