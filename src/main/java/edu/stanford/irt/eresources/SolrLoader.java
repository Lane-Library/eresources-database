package edu.stanford.irt.eresources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class SolrLoader {

    public static void main(final String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("edu/stanford/irt/eresources/"
                + args[0] + ".xml");
        SolrLoader loader = (SolrLoader) context.getBean("solrLoader");
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) context.getBean("executor");
        try {
            loader.load();
        } finally {
            executor.shutdown();
        }
    }

    private int count;

    private Executor executor;

    private EresourceHandler handler;

    private boolean killPrevious;

    private Logger log = LoggerFactory.getLogger(getClass());

    private Collection<AbstractEresourceProcessor> processors = Collections.<AbstractEresourceProcessor> emptyList();

    private Queue<Eresource> queue;

    protected SolrServer solrServer;

    private String version;

    protected Date getUpdatedDate() {
        return new Date(0);
    }

    public void load() throws IOException {
        this.log.info(this.version + " starting up");
        managePIDFile();
        Date updated = getUpdatedDate();
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
                    throw new EresourceDatabaseException(e);
                }
            }
        }
        this.count = this.handler.getCount();
        this.log.info("handled " + this.count + " eresources.");
    }

    private void managePIDFile() throws IOException {
        final File pidFile = new File("eresources.pid");
        String pid = null;
        if (!pidFile.createNewFile()) {
            BufferedReader reader = new BufferedReader(new FileReader(pidFile));
            pid = reader.readLine();
            reader.close();
            if (this.killPrevious) {
                LoggerFactory.getLogger(DBReload.class).warn("pid " + pid + " exists, killing . . .");
                Runtime.getRuntime().exec(new String[] { "kill", pid });
            } else {
                IllegalStateException e = new IllegalStateException("pid " + pid + " already running");
                LoggerFactory.getLogger(DBUpdate.class).error(e.getMessage());
                throw e;
            }
        }
        pid = ManagementFactory.getRuntimeMXBean().getName();
        int index = pid.indexOf('@');
        pid = pid.substring(0, index);
        try (FileOutputStream out = new FileOutputStream(pidFile)) {
            out.write(pid.getBytes());
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                if (!pidFile.delete()) {
                    throw new IllegalStateException("failed to delete pid file");
                }
            }
        });
    }

    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    public void setHandler(final EresourceHandler handler) {
        this.handler = handler;
    }

    public void setKillPrevious(final boolean killPrevious) {
        this.killPrevious = killPrevious;
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

    public void setSolrServer(final SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public void setVersion(final String version) {
        this.version = version;
    }
}
