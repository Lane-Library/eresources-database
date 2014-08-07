package edu.stanford.irt.eresources;

import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrPubmedUpdate extends SolrLoader {

    public SolrPubmedUpdate(final List<DataFetcher> dataFetchers) {
        for (DataFetcher fetcher : dataFetchers) {
            fetcher.getUpdateFiles();
        }
    }

    @Override
    protected Date getUpdatedDate() {
        SolrQuery query = new SolrQuery();
        query.setQuery("recordType:pubmed");
        query.add("sort", "updated desc");
        QueryResponse rsp = null;
        try {
            rsp = this.solrServer.query(query);
        } catch (SolrServerException e) {
            throw new EresourceDatabaseException(e);
        }
        SolrDocumentList rdocs = rsp.getResults();
        Date updated;
        if (rdocs.isEmpty()) {
            updated = new Date(0);
        } else {
            SolrDocument firstResult = rdocs.get(0);
            updated = (Date) firstResult.getFieldValue("updated");
        }
        return updated;
    }
}
