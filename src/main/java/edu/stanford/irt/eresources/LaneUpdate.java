package edu.stanford.irt.eresources;

public class LaneUpdate extends SolrLoader {

    private static final String BASE_QUERY = "(recordType:bib OR "
            + " recordType:class OR recordType:laneblog OR recordType:web)";

    @Override
    public void load() {
        this.setUpdatedDateQuery(BASE_QUERY);
        super.load();
    }
}
