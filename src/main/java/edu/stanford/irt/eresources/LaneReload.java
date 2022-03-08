package edu.stanford.irt.eresources;

import java.time.LocalDateTime;

public class LaneReload extends SolrLoader {

    private static final String BASE_QUERY = "(recordType:bib OR "
            + " recordType:class OR recordType:laneblog OR recordType:web)";

    private static final int EXPECTED_MIN_BIBS = 300_000;

    @Override
    public void load() {
        // fetch most recently updated eresource date from solr
        this.setUpdatedDateQuery(BASE_QUERY);
        LocalDateTime updateDate = this.getUpdatedDate();
        // set update date to null so Processors fetch everything
        this.setUpdatedDateQuery(null);
        super.load();
        maybeDeleteOldRecords(updateDate.format(SOLR_DATE_FIELD_FORMATTER), BASE_QUERY, EXPECTED_MIN_BIBS);
    }
}
