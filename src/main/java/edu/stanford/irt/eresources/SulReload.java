package edu.stanford.irt.eresources;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SulReload extends SolrLoader {

    private static final String BASE_QUERY = "(recordType:sul)";

    private static final int EXPECTED_MIN_BIBS = 100_000;

    private static final Logger LOG = LoggerFactory.getLogger(SolrReload.class);

    public static void main(final String[] args) {
        SolrLoader.main(new String[] { "sul-reload" });
    }

    @Override
    public void load() {
        // fetch most recently updated eresource date from solr
        this.setUpdatedDateQuery(BASE_QUERY);
        LocalDateTime updateDate = this.getUpdatedDate();
        // set update date to null so Processors fetch everything
        this.setUpdatedDateQuery(null);
        super.load();
        maybeDeleteOldRecords(updateDate.format(SOLR_DATE_FIELD_FORMATTER));
    }

    private void maybeDeleteOldRecords(final String lastUpdate) {
        int updatedRecords = super.getHandler().getCount();
        if (updatedRecords < EXPECTED_MIN_BIBS) {
            LOG.error("fewer updated sul records than expected; won't delete old records;"
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
