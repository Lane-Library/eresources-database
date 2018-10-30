package edu.stanford.irt.eresources;

public class SolrUpdate extends SolrLoader {

    @Override
    public void load() {
        this.setUpdatedDateQuery("NOT recordType:pubmed");
        super.load();
    }
}
