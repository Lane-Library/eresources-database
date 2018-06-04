package edu.stanford.irt.eresources;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrReload extends SolrLoader {

    private static final String BASE_QUERY = "(recordType:auth OR recordType:bib OR "
            + " recordType:class OR recordType:laneblog OR recordType:web)";

    private static final int EXPECTED_MIN_BIBS = 300_000;

    private static final Logger LOG = LoggerFactory.getLogger(SolrReload.class);

    public static void main(final String[] args) {
        SolrLoader.main(new String[] { "solr-reload" });
    }

    @Override
    public void load() {
        // fetch most recently updated eresource date from solr
        LocalDateTime updateDate = this.getUpdatedDate();
        super.load();
        maybeDeleteOldRecords(updateDate.format(SOLR_DATE_FIELD_FORMATTER));
    }

    private void maybeDeleteOldRecords(final String lastUpdate) {
        int updatedRecords = super.getHandler().getCount();
        if (updatedRecords < EXPECTED_MIN_BIBS) {
            LOG.error("fewer updated records than expected; won't delete old records;"
                    + " udpated records: {}; min expected: {}", updatedRecords, EXPECTED_MIN_BIBS);
        } else {
            try {
                // delete everything older than lastUpdate
                this.solrClient.deleteByQuery(BASE_QUERY + " AND updated:[* TO " + lastUpdate + "]");
                this.solrClient.commit();
            } catch (SolrServerException | IOException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }
}
