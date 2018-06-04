package edu.stanford.irt.eresources;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.apache.solr.client.solrj.SolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SolrLoader {

    private static final Logger log = LoggerFactory.getLogger(SolrLoader.class);

    protected SolrClient solrClient;

    private Executor executor;

    private EresourceHandler handler;

    private Collection<AbstractEresourceProcessor> processors = Collections.<AbstractEresourceProcessor> emptyList();

    private Queue<Eresource> queue;

    private String version;

    public static void main(final String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "edu/stanford/irt/eresources/" + args[0] + ".xml")) {
            SolrLoader loader = (SolrLoader) context.getBean("solrLoader");
            loader.load();
        }
    }

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
        this.processors = processors;
    }

    public void setQueue(final Queue<Eresource> queue) {
        this.queue = queue;
    }

    public void setSolrClient(final SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    protected EresourceHandler getHandler() {
        return this.handler;
    }

    protected LocalDateTime getUpdatedDate() {
        return LocalDateTime.MIN;
    }
}
