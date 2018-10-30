package edu.stanford.irt.eresources;

import java.time.LocalDateTime;

public class RedivisReload extends SolrLoader {

    private static final String BASE_QUERY = "(recordType:redivis)";

    private static final int EXPECTED_MIN_RECORDS = 100;

    @Override
    public void load() {
        // fetch most recently updated eresource date from solr
        this.setUpdatedDateQuery(BASE_QUERY);
        LocalDateTime updateDate = this.getUpdatedDate();
        // set update date to null so Processors fetch everything
        this.setUpdatedDateQuery(null);
        super.load();
        maybeDeleteOldRecords(updateDate.format(SOLR_DATE_FIELD_FORMATTER), BASE_QUERY, EXPECTED_MIN_RECORDS);
    }
}
