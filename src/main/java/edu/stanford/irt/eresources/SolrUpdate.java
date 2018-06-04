package edu.stanford.irt.eresources;

public class SolrUpdate extends SolrLoader {

    public static void main(final String[] args) {
        SolrLoader.main(new String[] { "solr-update" });
    }

    @Override
    public void load() {
        this.setUpdatedDateQuery("NOT recordType:pubmed");
        super.load();
    }
}
