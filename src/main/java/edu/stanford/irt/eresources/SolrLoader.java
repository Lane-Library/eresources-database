package edu.stanford.irt.eresources;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SolrLoader {

    private static final Logger log = LoggerFactory.getLogger(SolrLoader.class);

    private static final LocalDateTime MIN_DT = LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault());

    protected static final DateTimeFormatter SOLR_DATE_FIELD_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss.S'Z'").toFormatter();

    public static void main(final String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "edu/stanford/irt/eresources/" + args[0] + ".xml")) {
            SolrLoader loader = (SolrLoader) context.getBean("solrLoader");
            // when not null, args[1] overrides the update date instead of querying for it in Solr
            loader.setUpdateDateOverride(args[1]);
            loader.load();
        }
    }

    private Executor executor = null;

    private EresourceHandler handler = null;

    private Collection<AbstractEresourceProcessor> processors = Collections.emptyList();

    private Queue<Eresource> queue = null;

    private String updateDateOverride = null;

    private String updatedDateQuery = null;

    private String version = null;

    protected SolrClient solrClient;

    public void load() {
        log.info("starting up version {}", this.version);
        LocalDateTime updated = getUpdatedDate();
        this.executor.execute(this.handler);
        for (AbstractEresourceProcessor processor : this.processors) {
            processor.setStartDate(updated);
            processor.process();
        }
        this.handler.stop();
        synchronized (this.queue) {
            while (!this.queue.isEmpty()) {
                try {
                    this.queue.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new EresourceDatabaseException(e);
                }
            }
        }
        log.info("handled {} eresources.", this.handler.getCount());
    }

    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    public void setHandler(final EresourceHandler handler) {
        this.handler = handler;
    }

    public void setProcessors(final Collection<AbstractEresourceProcessor> processors) {
        if (null == processors) {
            throw new IllegalArgumentException("null processors");
        }
        this.processors = new ArrayList<>(processors);
    }

    public void setQueue(final Queue<Eresource> queue) {
        this.queue = queue;
    }

    public void setSolrClient(final SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    public void setUpdateDateOverride(final String updateDate) {
        this.updateDateOverride = updateDate;
    }

    public void setUpdatedDateQuery(final String solrUpdatedDateQuery) {
        this.updatedDateQuery = solrUpdatedDateQuery;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    protected EresourceHandler getHandler() {
        return this.handler;
    }

    protected LocalDateTime getUpdatedDate() {
        if (null != this.updateDateOverride) {
            return LocalDateTime.parse(this.updateDateOverride);
        }
        if (null == this.updatedDateQuery) {
            return MIN_DT;
        }
        SolrQuery query = new SolrQuery();
        query.setQuery(this.updatedDateQuery);
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
            updated = MIN_DT;
        } else {
            SolrDocument firstResult = rdocs.get(0);
            Date solrDate = (Date) firstResult.getFieldValue("updated");
            updated = LocalDateTime.ofInstant(solrDate.toInstant(), ZoneId.systemDefault());
        }
        return updated;
    }

    protected void maybeDeleteOldRecords(final LocalDateTime lastUpdate, final String baseQuery,
            final int minExpectedRecords) {
        int updatedRecords = this.getHandler().getCount();
        if (updatedRecords < minExpectedRecords) {
            log.error("fewer updated records than expected; won't delete old records;"
                    + " udpated records: {}; min expected: {}", updatedRecords, minExpectedRecords);
        } else {
            try {
                // delete everything older than lastUpdate
                // need to adjust lastUpdate to Solr's timezone (UTC)
                LocalDateTime adjustedUpdateDate = LocalDateTime
                        .ofInstant(lastUpdate.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.of("UTC"));
                String query = baseQuery + " AND updated:[* TO " + adjustedUpdateDate.format(SOLR_DATE_FIELD_FORMATTER)
                        + "]";
                log.info("deleting: {}", query);
                this.solrClient.deleteByQuery(query);
                this.solrClient.commit();
            } catch (SolrServerException | IOException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }
}
