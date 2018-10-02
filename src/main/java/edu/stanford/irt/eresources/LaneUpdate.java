package edu.stanford.irt.eresources;

public class LaneUpdate extends SolrLoader {

    public static void main(final String[] args) {
        SolrLoader.main(new String[] { "lane-update" });
    }

    @Override
    public void load() {
        this.setUpdatedDateQuery("NOT recordType:pubmed");
        super.load();
    }
}
