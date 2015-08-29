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

import edu.stanford.lane.mesh.MeshMapManager;

public class SolrEresourceHandler implements EresourceHandler {

    public static final int TEN = 10;

    public static final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private static final int SORT_TITLE_MAX = 150;

    private int count = 0;

    private volatile boolean keepGoing = true;

    private ObjectMapper mapper = new ObjectMapper();

    private MeshMapManager meshManager = new MeshMapManager();

    private BlockingQueue<Eresource> queue;

    private Collection<SolrInputDocument> solrDocs = new ArrayList<SolrInputDocument>();

    private int solrMaxDocs;

    private SolrServer solrServer;

    public SolrEresourceHandler(final BlockingQueue<Eresource> queue, final SolrServer solrServer, final int solrMaxDocs) {
        this.queue = queue;
        this.solrServer = solrServer;
        this.solrMaxDocs = solrMaxDocs;
    }

    private static String getSortText(final String text) {
        if (null == text || text.isEmpty()) {
            return "";
        }
        String sortText = text;
        try {
            sortText = sortText.substring(0, SORT_TITLE_MAX);
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        return sortText;
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
                    addSolrDocs();
                }
            }
            this.queue.notifyAll();
            if (!this.solrDocs.isEmpty()) {
                addSolrDocs();
            }
        }
    }

    @Override
    public void stop() {
        this.keepGoing = false;
    }

    protected void insertEresource(final Eresource eresource) {
        SolrInputDocument doc = new SolrInputDocument();
        String recordType = eresource.getRecordType();
        StringBuffer key = new StringBuffer();
        String title = eresource.getTitle();
        String sortTitle = getSortText(title);
        String keywords = eresource.getKeywords();
        String publicationText = eresource.getPublicationText();
        if (null != publicationText) {
            keywords = keywords + " " + publicationText;
        }
        List<Version> versions = new LinkedList<Version>();
        int[] itemCount = eresource.getItemCount();
        key.append(recordType).append("-").append(Integer.toString(eresource.getRecordId()));
        if (eresource.isClone()) {
            key.append("-clone");
        }
        doc.addField("id", key.toString());
        doc.addField("recordId", Integer.toString(eresource.getRecordId()));
        doc.addField("recordType", eresource.getRecordType());
        doc.addField("description", eresource.getDescription());
        doc.addField("text", keywords);
        doc.addField("title", title);
        doc.addField("title_sort", sortTitle);
        doc.addField("primaryType", eresource.getPrimaryType());
        doc.addField("totalItems", Integer.toString(itemCount[0]));
        doc.addField("availableItems", Integer.toString(itemCount[1]));
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
            doc.addField("mesh_parents", this.meshManager.getParentHeadings(mesh));
            if (!this.meshManager.isChecktag(mesh)) {
                doc.addField("mesh_NoCT", mesh);
            }
        }
        for (String type : eresource.getTypes()) {
            doc.addField("type", type);
        }
        String pmid = eresource.getPmid();
        if (null != pmid) {
            doc.addField("pmid", pmid);
        }
        String doi = eresource.getDoi();
        if (null != doi) {
            doc.addField("doi", doi);
        }
        String publicationAuthorsText = eresource.getPublicationAuthorsText();
        publicationAuthorsText = (null != publicationAuthorsText) ? publicationAuthorsText : eresource.getAuthor();
        if (null != publicationAuthorsText) {
            doc.addField("publicationAuthorsText", publicationAuthorsText);
            doc.addField("authors_sort", getSortText(publicationAuthorsText));
        }
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

    private void addSolrDocs() {
        try {
            this.solrServer.add(this.solrDocs);
            this.solrDocs.clear();
        } catch (SolrServerException | IOException e) {
            throw new EresourceDatabaseException("solr add failed", e);
        }
    }
}
