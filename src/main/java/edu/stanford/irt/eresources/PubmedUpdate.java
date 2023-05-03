package edu.stanford.irt.eresources;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

public class PubmedUpdate extends SolrLoader {

    public PubmedUpdate(final List<DataFetcher> dataFetchers) {
        for (DataFetcher fetcher : dataFetchers) {
            fetcher.getUpdateFiles();
        }
    }

    @Override
    public void load() {
        this.getHandler().setSolrCollection(this.solrCollection);
        this.setUpdatedDateQuery("recordType:pubmed");
        super.load();
        try {
            // pubmed2er.stx handles deletes from NCBI by zeroing out record data
            // here we delete them ... records lacking year and title
            this.solrClient.deleteByQuery(this.solrCollection, "recordType:pubmed AND year:0 AND title:''");
            this.solrClient.commit(this.solrCollection);
        } catch (SolrServerException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
