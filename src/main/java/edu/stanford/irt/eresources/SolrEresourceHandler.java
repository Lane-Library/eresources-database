package edu.stanford.irt.eresources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SolrEresourceHandler implements EresourceHandler {

    private static final int SORT_TITLE_MAX = 150;

    public static final int TEN = 10;

    public static final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private static String getSortTitle(final String title) {
        if (null == title || title.isEmpty()) {
            return title;
        }
        String sortTitle = title.toLowerCase().replaceAll("[^0-9a-z ]", "").trim();
        try {
            sortTitle = sortTitle.substring(0, SORT_TITLE_MAX);
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        return sortTitle;
    }

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
        String sortTitle = getSortTitle(title);
        List<Version> versions = new LinkedList<Version>();
        key.append(recordType).append("-").append(Integer.toString(eresource.getRecordId()));
        doc.addField("id", key.toString());
        doc.addField("recordId", Integer.toString(eresource.getRecordId()));
        doc.addField("recordType", eresource.getRecordType());
        doc.addField("description", eresource.getDescription());
        doc.addField("text", eresource.getKeywords());
        doc.addField("title", title);
        doc.addField("title_sort", sortTitle);
        doc.addField("year", Integer.toString(eresource.getYear()));
        char firstCharOfTitle = '0';
        if (null != sortTitle && !sortTitle.isEmpty()) {
            firstCharOfTitle = sortTitle.trim().substring(0, 1).charAt(0);
        }
        if (!Character.isLetter((int) firstCharOfTitle)) {
            firstCharOfTitle = '1';
        }
        // ertlsw = random, uncommon string so single letter isn't stopword'd out of results
        doc.addField("title_starts", "ertlsw" + Character.toString(firstCharOfTitle));
        doc.addField("isCore", Boolean.toString(eresource.isCore()));
        doc.addField("isEnglish", Boolean.toString(eresource.isEnglish()));
        doc.addField("isRecent", Boolean.toString(THIS_YEAR - eresource.getYear() <= TEN));
        for (String mesh : eresource.getMeshTerms()) {
            doc.addField("mesh", mesh);
        }
        for (String type : eresource.getTypes()) {
            doc.addField("type", type);
        }
        String publicationAuthorsText = eresource.getPublicationAuthorsText();
        if (null != publicationAuthorsText) {
            doc.addField("publicationAuthorsText", publicationAuthorsText);
        }
        String publicationText = eresource.getPublicationText();
        if (null != publicationText) {
            doc.addField("publicationText", publicationText);
        }
        String publicationTitle = eresource.getPublicationTitle();
        if (null != publicationTitle) {
            doc.addField("publicationTitle", publicationTitle);
        }
        for (String author : eresource.getPublicationAuthors()) {
            doc.addField("publicationAuthor", author);
        }
        for (String pubLanguage : eresource.getPublicationLanguages()) {
            doc.addField("publicationLanguage", pubLanguage);
        }
        for (String pubType : eresource.getPublicationTypes()) {
            doc.addField("publicationType", pubType);
        }
        for (Version version : eresource.getVersions()) {
            versions.add(version);
            for (String subset : version.getSubsets()) {
                doc.addField("subset", subset);
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
                if (this.solrDocs.size() >= this.solrMaxDocs) {
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
