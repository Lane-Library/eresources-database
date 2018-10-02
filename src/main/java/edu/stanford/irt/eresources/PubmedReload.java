package edu.stanford.irt.eresources;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.solr.client.solrj.SolrServerException;

public class PubmedReload extends SolrLoader {

    @Override
    public void load() {
        // fetch most recently updated eresource date from solr
        this.setUpdatedDateQuery("recordType:pubmed");
        LocalDateTime updateDate = this.getUpdatedDate();
        String lastUpdate = updateDate.format(SOLR_DATE_FIELD_FORMATTER);
        super.load();
        try {
            // delete everything older than lastUpdate
            this.solrClient.deleteByQuery("recordType:pubmed AND updated:[* TO " + lastUpdate + "]");
            this.solrClient.commit();
        } catch (SolrServerException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
