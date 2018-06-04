package edu.stanford.irt.eresources;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrUpdate extends SolrLoader {

    public static void main(final String[] args) {
        SolrLoader.main(new String[] { "solr-update" });
    }

    @Override
    protected LocalDateTime getUpdatedDate() {
        SolrQuery query = new SolrQuery();
        query.setQuery("NOT recordType:pubmed");
        query.add("sort", "updated desc");
        QueryResponse rsp = null;
        try {
            rsp = this.solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
        SolrDocumentList rdocs = rsp.getResults();
        LocalDateTime updated;
        if (rdocs.isEmpty()) {
            updated = LocalDateTime.MIN;
        } else {
            SolrDocument firstResult = rdocs.get(0);
            Date solrDate = (Date) firstResult.getFieldValue("updated");
            updated = LocalDateTime.ofInstant(solrDate.toInstant(), ZoneId.systemDefault());
        }
        return updated;
    }
}
