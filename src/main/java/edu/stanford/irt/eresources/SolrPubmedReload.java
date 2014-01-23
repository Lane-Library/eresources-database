package edu.stanford.irt.eresources;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrPubmedReload extends SolrLoader {

    public static void main(final String[] args) throws IOException {
        SolrLoader.main(new String[] { "solr-pubmed-reload" });
    }

    private SolrServer solrServer;

    private String getLastUpdate() {
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
            updated = new Date();
        } else {
            SolrDocument firstResult = rdocs.get(0);
            updated = (Date) firstResult.getFieldValue("updated");
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(updated);
    }

    @Override
    public void load() throws IOException {
        // fetch most recently updated eresource date from solr
        String lastUpdate = getLastUpdate();
        super.load();
        try {
            // delete everything older than lastUpdate
            this.solrServer.deleteByQuery("id:pubmed-* AND updated:[* TO " + lastUpdate + "]");
            this.solrServer.commit();
        } catch (SolrServerException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void setSolrServer(final SolrServer solrServer) {
        this.solrServer = solrServer;
    }
}
