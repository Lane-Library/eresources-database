package edu.stanford.irt.eresources;

import java.time.LocalDateTime;

public class Reloader extends SolrLoader {

    private String baseQuery;

    private final int expectedMinRecords;

    public Reloader(final String baseQuery, final int expectedMinRecords) {
        this.baseQuery = baseQuery;
        this.expectedMinRecords = expectedMinRecords;
    }

    @Override
    public void load() {
        // fetch most recently updated eresource date from solr
        this.setUpdatedDateQuery(this.baseQuery);
        LocalDateTime updateDate = this.getUpdatedDate();
        // set update date to null so Processors fetch everything
        this.setUpdatedDateQuery(null);
        super.load();
        maybeDeleteOldRecords(updateDate, this.baseQuery, this.expectedMinRecords);
    }
}
