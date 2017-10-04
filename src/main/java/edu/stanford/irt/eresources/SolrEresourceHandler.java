package edu.stanford.irt.eresources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.lane.mesh.MeshMapManager;

public class SolrEresourceHandler implements EresourceHandler {

    public static final int TEN = 10;

    public static final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private static final Pattern BASIC_NONFILING = Pattern.compile("^\\W?(?:A|An|The) ");

    private static final Pattern CHILD = Pattern.compile(".*\\b(?:child|teen|adolesc|pediatric|infant|newborn).*",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern CHILD_MESH = Pattern.compile("^(?:infant|child|adolescent).*",
            Pattern.CASE_INSENSITIVE);

    private static final String EMPTY = "";

    private static final int SORT_TEXT_MAX = 150;

    private int count = 0;

    private volatile boolean keepGoing = true;

    private ObjectMapper mapper = new ObjectMapper();

    private MeshMapManager meshManager = new MeshMapManager();

    private BlockingQueue<Eresource> queue;

    private SolrClient solrClient;

    private Collection<SolrInputDocument> solrDocs = new ArrayList<>();

    private int solrMaxDocs;

    public SolrEresourceHandler(final BlockingQueue<Eresource> queue, final SolrClient solrClient,
            final int solrMaxDocs) {
        this.queue = queue;
        this.solrClient = solrClient;
        this.solrMaxDocs = solrMaxDocs;
    }

    private static String getSortText(final String text) {
        if (null == text) {
            return EMPTY;
        }
        if (text.length() > SORT_TEXT_MAX) {
            return text.substring(0, SORT_TEXT_MAX);
        }
        return text;
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
            Thread.currentThread().interrupt();
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
                    Thread.currentThread().interrupt();
                    throw new EresourceDatabaseException("\nstop=" + this.keepGoing + "\nempty=" + this.queue.isEmpty(),
                            e);
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
        String sortTitle = getSortTitle(eresource);
        int[] itemCount = eresource.getItemCount();
        doc.addField("id", eresource.getId());
        doc.addField("recordId", Integer.toString(eresource.getRecordId()));
        doc.addField("recordType", eresource.getRecordType());
        doc.addField("description", eresource.getDescription());
        doc.addField("text", getKeywords(eresource));
        doc.addField("title", eresource.getTitle());
        for (String altTitle : eresource.getAbbreviatedTitles()) {
            doc.addField("title_abbr", altTitle);
        }
        for (String altTitle : eresource.getAlternativeTitles()) {
            doc.addField("title_alt", altTitle);
        }
        doc.addField("title_short", eresource.getShortTitle());
        doc.addField("title_sort", sortTitle);
        doc.addField("primaryType", eresource.getPrimaryType());
        doc.addField("totalItems", Integer.toString(itemCount[0]));
        doc.addField("availableItems", Integer.toString(itemCount[1]));
        doc.addField("year", Integer.toString(eresource.getYear()));
        doc.addField("date", eresource.getDate());
        // ertlsw = random, uncommon string so single letter isn't stopword'd out of results
        doc.addField("title_starts", "ertlsw" + getFirstCharacter(sortTitle));
        doc.addField("isChild", Boolean.toString(isChild(eresource)));
        doc.addField("isCore", Boolean.toString(eresource.isCore()));
        doc.addField("isEnglish", Boolean.toString(eresource.isEnglish()));
        doc.addField("isLaneConnex", Boolean.toString(eresource.isLaneConnex()));
        doc.addField("isRecent", Boolean.toString(THIS_YEAR - eresource.getYear() <= TEN));
        doc.addField("publicationAuthorsText", eresource.getPublicationAuthorsText());
        doc.addField("publicationText", eresource.getPublicationText());
        doc.addField("publicationTitle", eresource.getPublicationTitle());
        for (String mesh : eresource.getMeshTerms()) {
            doc.addField("mesh", mesh);
            doc.addField("mesh_parents", this.meshManager.getParentHeadings(mesh));
        }
        for (String type : eresource.getTypes()) {
            doc.addField("type", type);
        }
        StringBuilder authorSort = new StringBuilder();
        for (String author : eresource.getPublicationAuthors()) {
            doc.addField("publicationAuthor", author);
            doc.addField("author", author);
            authorSort.append(author);
        }
        doc.addField("authors_sort", getSortText(authorSort.toString()));
        for (String pubLanguage : eresource.getPublicationLanguages()) {
            doc.addField("publicationLanguage", pubLanguage);
        }
        for (String pubType : eresource.getPublicationTypes()) {
            doc.addField("publicationType", pubType);
            for (String parentType : this.meshManager.getParentHeadingsLimitToPubmedPublicationTypes(pubType)) {
                doc.addField("publicationType", parentType);
            }
        }
        doc.addField("versionsJson", versionsToJson(eresource));
        this.solrDocs.add(doc);
    }

    private void addSolrDocs() {
        try {
            this.solrClient.add(this.solrDocs);
            this.solrDocs.clear();
        } catch (SolrServerException | IOException e) {
            throw new EresourceDatabaseException("solr add failed", e);
        }
    }

    private String getFirstCharacter(final String sortTitle) {
        char firstCharOfTitle = '0';
        String title = sortTitle.trim();
        if (null != title && !title.isEmpty()) {
            firstCharOfTitle = title.trim().substring(0, 1).charAt(0);
        }
        if (!Character.isLetter((int) firstCharOfTitle)) {
            firstCharOfTitle = '1';
        }
        return Character.toString(firstCharOfTitle);
    }

    private String getKeywords(final Eresource eresource) {
        StringBuilder keywords = new StringBuilder();
        String publicationText = eresource.getPublicationText();
        String text = eresource.getKeywords();
        if (null != text) {
            keywords.append(' ').append(text).append(' ');
        }
        if (null != publicationText) {
            keywords.append(' ').append(publicationText).append(' ');
        }
        for (String type : eresource.getTypes()) {
            keywords.append(' ').append(type).append(' ');
        }
        return keywords.toString();
    }

    /**
     * sort title is only set for bibs, so default to title for other record types and remove some basic
     * non-filing strings
     *
     * @param eresource
     * @return sort title
     */
    private String getSortTitle(final Eresource eresource) {
        String st = eresource.getSortTitle();
        if (null == st) {
            st = eresource.getTitle();
            st = BASIC_NONFILING.matcher(st).replaceFirst(EMPTY);
        }
        return getSortText(st);
    }

    /**
     * Determine if this eresource is about children </br>
     * Could use PubmedSpecialTypesManager instead but would miss Lane Catalog child articles </br>
     * Strategy is based on PubMed search in pubmed_allchild search engine: (child* [tiab] OR teen* [tiab] OR adolesc*
     * [tiab] OR pediatric* [tiab] OR infant* [tiab] OR newborn* [tiab] OR neonat* [tiab] OR "infant"[MeSH Terms] OR
     * "child"[MeSH Terms] OR "adolescent"[MeSH Terms])
     *
     * @param eresource
     * @return true if this eresource is about children
     */
    private boolean isChild(final Eresource eresource) {
        for (String m : eresource.getMeshTerms()) {
            if (CHILD_MESH.matcher(m).matches()) {
                return true;
            }
        }
        StringBuilder tiab = new StringBuilder();
        tiab.append(eresource.getTitle());
        tiab.append(' ');
        tiab.append(eresource.getDescription());
        if (CHILD.matcher(tiab.toString()).matches()) {
            return true;
        }
        return false;
    }

    private String versionsToJson(final Eresource eresource) {
        String json;
        List<Version> versions = new LinkedList<>();
        for (Version version : eresource.getVersions()) {
            versions.add(version);
        }
        try {
            json = this.mapper.writeValueAsString(versions);
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
        return json;
    }
}
