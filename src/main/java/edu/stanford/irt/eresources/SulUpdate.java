package edu.stanford.irt.eresources;

public class SulUpdate extends SolrLoader {

    @Override
    public void load() {
        this.setUpdatedDateQuery("recordType:sul");
        super.load();
    }
}
