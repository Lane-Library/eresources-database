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
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class SolrVideosReload extends SolrLoader {

    private static String recordType = null;

    public static void main(final String[] args) throws IOException {
        recordType = args[0];
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("edu/stanford/irt/eresources/videos/" + recordType + "-processor.xml");
        SolrLoader loader = (SolrLoader) context.getBean("solrLoader");
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) context.getBean("executor");
        try {
            loader.load();
        } finally {
            executor.shutdown();
        }
    }

    @Override
    public void load() throws IOException {
        // fetch most recently updated eresource date from solr
        String lastUpdate = getLastUpdate();
        super.load();
        try {
            // delete everything older than lastUpdate
            this.solrClient.deleteByQuery("type:\"Instructional Video\" AND updated:[* TO " + lastUpdate + "] AND recordType:"+recordType);
            this.solrClient.commit();
        } catch (SolrServerException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    
    private String getLastUpdate() {
        SolrQuery query = new SolrQuery();
        query.setQuery("type:\"Instructional Video\" AND recordType:"+recordType);
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
