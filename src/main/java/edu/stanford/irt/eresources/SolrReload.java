package edu.stanford.irt.eresources;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrReload extends SolrLoader {

    public static void main(final String[] args) {
        SolrLoader.main(new String[] { "solr-reload" });
    }

    @Override
    public void load() {
        // fetch most recently updated eresource date from solr
        String lastUpdate = getLastUpdate();
        super.load();
        try {
            // delete everything older than lastUpdate
            this.solrClient.deleteByQuery("(recordType:bib OR recordType:class OR recordType:laneblog OR recordType:web) AND updated:[* TO " + lastUpdate + "]");
            this.solrClient.commit();
        } catch (SolrServerException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    private String getLastUpdate() {
        SolrQuery query = new SolrQuery();
        query.setQuery("recordType:bib OR recordType:class OR recordType:laneblog OR recordType:web");
        query.add("sort", "updated desc");
        QueryResponse rsp = null;
        try {
            rsp = this.solrClient.query(query);
        } catch (SolrServerException | IOException e) {
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
}
