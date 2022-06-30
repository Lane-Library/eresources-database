package edu.stanford.irt.eresources;

public class Updater extends SolrLoader {

    private String baseQuery;

    public Updater(final String baseQuery) {
        this.baseQuery = baseQuery;
    }

    @Override
    public void load() {
        this.setUpdatedDateQuery(this.baseQuery);
        super.load();
    }
}
