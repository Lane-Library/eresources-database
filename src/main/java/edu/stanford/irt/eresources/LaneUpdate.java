package edu.stanford.irt.eresources;

public class LaneUpdate extends SolrLoader {

    @Override
    public void load() {
        this.setUpdatedDateQuery("NOT recordType:pubmed");
        super.load();
    }
}
