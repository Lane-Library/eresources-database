package edu.stanford.irt.eresources;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

public class SolrPubmedUpdate extends SolrLoader {

    public SolrPubmedUpdate(final List<DataFetcher> dataFetchers) {
        for (DataFetcher fetcher : dataFetchers) {
            fetcher.getUpdateFiles();
        }
    }

    @Override
    public void load() {
        this.setUpdatedDateQuery("recordType:pubmed");
        super.load();
        try {
            // pubmed2er.stx handles deletes from NCBI by zeroing out record data
            // here we delete them ... records lacking year and title
            this.solrClient.deleteByQuery("recordType:pubmed AND year:0 AND title:''");
            this.solrClient.commit();
        } catch (SolrServerException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
