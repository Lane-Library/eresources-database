package edu.stanford.irt.eresources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.codehaus.jackson.map.ObjectMapper;

public class SolrEresourceHandler implements EresourceHandler {

    private int count = 0;

    private volatile boolean keepGoing = true;

    private ObjectMapper mapper = new ObjectMapper();

    private BlockingQueue<Eresource> queue;

    private Collection<SolrInputDocument> solrDocs = new ArrayList<SolrInputDocument>();

    private int solrMaxDocs;

    private SolrServer solrServer;

    public SolrEresourceHandler(final BlockingQueue<Eresource> queue, final SolrServer solrServer, final int solrMaxDocs) {
        this.queue = queue;
        this.solrServer = solrServer;
        this.solrMaxDocs = solrMaxDocs;
    }

    private void commitSolrDocs() {
        try {
            this.solrServer.add(this.solrDocs);
            this.solrServer.commit();
        } catch (SolrServerException | IOException e) {
            throw new EresourceDatabaseException("solr commit failed", e);
        }
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public void handleEresource(final Eresource eresource) {
        for (Version version : eresource.getVersions()) {
            for (Link link : version.getLinks()) {
                link.setVersion(version);
            }
        }
        try {
            this.queue.put(eresource);
        } catch (InterruptedException e) {
            throw new EresourceDatabaseException(e);
        }
        this.count++;
    }

    protected void insertEresource(final Eresource eresource) {
        SolrInputDocument doc = new SolrInputDocument();
        String recordType = eresource.getRecordType();
        StringBuffer key = new StringBuffer();
        String title = eresource.getTitle();
        List<Version> versions = new LinkedList<Version>();
        key.append(recordType).append("-").append(Integer.toString(eresource.getRecordId()));
        doc.addField("id", key.toString());
        doc.addField("recordId", Integer.toString(eresource.getRecordId()));
        doc.addField("recordType", eresource.getRecordType());
        doc.addField("description", eresource.getDescription());
        doc.addField("text", eresource.getKeywords());
        doc.addField("title", title);
        doc.addField("year", Integer.toString(eresource.getYear()));
        char firstCharOfTitle = '0';
        if (null != title && !title.isEmpty()) {
            firstCharOfTitle = title.trim().substring(0, 1).charAt(0);
        }
        if (!Character.isLetter((int) firstCharOfTitle)) {
            firstCharOfTitle = '1';
        }
        doc.addField("isCore", Boolean.toString(eresource.isCore()));
        for (String mesh : eresource.getMeshTerms()) {
            doc.addField("mesh", mesh);
        }
        for (String type : eresource.getTypes()) {
            doc.addField("type", type);
        }
        for (Version version : eresource.getVersions()) {
            versions.add(version);
            for (String subset : version.getSubsets()) {
                doc.addField("subset", subset);
            }
            for (Link link : version.getLinks()) {
                doc.addField("links", link.getUrl());
            }
        }
        try {
            doc.addField("versionsJson", this.mapper.writeValueAsString(versions));
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
        this.solrDocs.add(doc);
    }

    @Override
    public void run() {
        synchronized (this.queue) {
            while (!this.queue.isEmpty() || this.keepGoing) {
                try {
                    Eresource eresource = this.queue.poll(1, TimeUnit.SECONDS);
                    if (eresource != null) {
                        insertEresource(eresource);
                    }
                } catch (InterruptedException e) {
                    throw new EresourceDatabaseException(
                            "\nstop=" + this.keepGoing + "\nempty=" + this.queue.isEmpty(), e);
                }
                if (this.solrDocs.size() > this.solrMaxDocs) {
                    commitSolrDocs();
                    this.solrDocs.clear();
                }
            }
            this.queue.notifyAll();
            if (!this.solrDocs.isEmpty()) {
                commitSolrDocs();
            }
        }
    }

    @Override
    public void stop() {
        this.keepGoing = false;
    }
}
