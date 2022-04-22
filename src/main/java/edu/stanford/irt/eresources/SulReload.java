package edu.stanford.irt.eresources;

import java.time.LocalDateTime;

public class SulReload extends SolrLoader {

    private static final String BASE_QUERY = "(recordType:sul)";

    private static final int EXPECTED_MIN_RECORDS = 1_000_000;

    @Override
    public void load() {
        // fetch most recently updated eresource date from solr
        this.setUpdatedDateQuery(BASE_QUERY);
        LocalDateTime updateDate = this.getUpdatedDate();
        // set update date to null so Processors fetch everything
        this.setUpdatedDateQuery(null);
        super.load();
        maybeDeleteOldRecords(updateDate, BASE_QUERY, EXPECTED_MIN_RECORDS);
    }
}
