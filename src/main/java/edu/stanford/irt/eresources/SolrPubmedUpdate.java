package edu.stanford.irt.eresources;

import java.io.IOException;
import java.util.Date;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrPubmedUpdate extends SolrLoader {

    public static void main(final String[] args) throws IOException {
        SolrLoader.main(new String[] { "solr-pubmed-update" });
    }

    private SolrServer solrServer;

    @Override
    protected Date getUpdatedDate() {
        SolrQuery query = new SolrQuery();
        query.setQuery("id:pubmed-*");
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

    public void setSolrServer(final SolrServer solrServer) {
        this.solrServer = solrServer;
    }
}
