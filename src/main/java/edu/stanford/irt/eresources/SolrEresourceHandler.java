package edu.stanford.irt.eresources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.marc.AbstractMarcEresource;
import edu.stanford.irt.eresources.sax.SAXEresource;
import edu.stanford.irt.suggest.MeshSuggestionManager;
import edu.stanford.lane.journals.JournalMapManager;
import edu.stanford.lane.mesh.MeshCheckTags;
import edu.stanford.lane.mesh.MeshMapManager;

public class SolrEresourceHandler implements EresourceHandler {

    public static final int TEN = 10;

    public static final int THIS_YEAR = ZonedDateTime.now(ZoneId.of("America/Los_Angeles")).getYear();

    private static final Pattern BASIC_NONFILING = Pattern.compile("^\\W?(?:A|An|The) ");

    private static final String EMPTY = "";

    private static final Logger log = LoggerFactory.getLogger(SolrEresourceHandler.class);

    private static final Pattern NOPROXY_HOSTS = Pattern
            .compile(".*\\.(stanford.edu|stanfordchildrens.org|stanfordhealthcare.org)", Pattern.CASE_INSENSITIVE);

    private static final int SORT_AUTHOR_MAX = 50;

    private static final int SORT_TITLE_MAX = 100;

    private static String getSortText(final String text, final int max) {
        if (null == text) {
            return EMPTY;
        }
        if (text.length() > max) {
            return text.substring(0, max);
        }
        return text;
    }

    private int count;

    private volatile boolean keepGoing = true;

    private ObjectMapper mapper = new ObjectMapper();

    private MeshMapManager meshManager = new MeshMapManager();

    private MeshSuggestionManager meshVariantsManager = new MeshSuggestionManager();

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
                } catch (InterruptedException | EresourceDatabaseException e) {
                    Thread.currentThread().interrupt();
                    log.error("\nstop=" + this.keepGoing + "\nempty=" + this.queue.isEmpty(), e);
                    this.queue.clear();
                    this.keepGoing = false;
                }
                if (this.solrDocs.size() >= this.solrMaxDocs) {
                    addSolrDocs(false);
                }
            }
            this.queue.notifyAll();
            if (!this.solrDocs.isEmpty()) {
                addSolrDocs(true);
            }
        }
    }

    @Override
    public void stop() {
        this.keepGoing = false;
    }

    private void addPublicationAuthors(final Eresource eresource, final SolrInputDocument doc) {
        Collection<String> authors;
        if (SAXEresource.class.isAssignableFrom(eresource.getClass())) {
            SAXEresource saxEresource = (SAXEresource) eresource;
            authors = saxEresource.getPublicationAuthorsFacetable();
        } else {
            authors = eresource.getPublicationAuthors();
        }
        doc.addField("publicationAuthor", authors);
    }

    private void addSolrDocs(final boolean commit) {
        try {
            this.solrClient.add(this.solrDocs);
            this.solrDocs.clear();
            if (commit) {
                this.solrClient.commit();
            }
        } catch (SolrServerException | IOException e) {
            throw new EresourceDatabaseException("solr add failed", e);
        }
    }

    private String buildCitationKeywords(final Eresource eresource) {
        Set<String> strings = new LinkedHashSet<>();
        String pubDate = eresource.getPublicationDate();
        String pubPages = eresource.getPublicationPages();
        String pubTitle = eresource.getPublicationTitle();
        strings.add(eresource.getPublicationText());
        strings.add(pubTitle);
        strings.add(JournalMapManager.getVariantJournalTitles(pubTitle));
        strings.add(pubDate);
        strings.add(TextParserHelper.explodeMonthAbbreviations(pubDate));
        strings.add(eresource.getPublicationVolume());
        strings.add(eresource.getPublicationIssue());
        strings.add(pubPages);
        strings.add(TextParserHelper.parseEndPages(pubPages));
        return strings.stream().filter(Objects::nonNull).filter((final String s) -> !s.isEmpty())
                .collect(Collectors.joining(" "));
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
     * sort title is only set for bibs, so default to title for other record types and remove some basic non-filing
     * strings
     *
     * @param eresource
     * @return sort title
     */
    private String getSortTitle(final Eresource eresource) {
        String st = eresource.getSortTitle();
        if (null == st && null != eresource.getTitle()) {
            st = eresource.getTitle();
            st = BASIC_NONFILING.matcher(st).replaceFirst(EMPTY);
        }
        return getSortText(st, SORT_TITLE_MAX);
    }

    private void handleMesh(final Eresource eresource, final SolrInputDocument doc) {
        Set<String> mesh = new HashSet<>();
        Set<String> meshParents = new HashSet<>();
        Set<String> meshVariants = new HashSet<>();
        for (String heading : eresource.getMeshTerms()) {
            if (!MeshCheckTags.getCheckTags().contains(heading)) {
                meshVariants.addAll(this.meshVariantsManager.getVariants(heading));
            }
            mesh.add(heading);
            meshParents.addAll(this.meshManager.getParentHeadings(heading, 1));
        }
        doc.addField("mesh", mesh);
        doc.addField("mesh_parents", meshParents);
        doc.addField("mesh_variants", meshVariants);
        Set<String> meshBroad = new HashSet<>();
        for (String broadHeading : eresource.getBroadMeshTerms()) {
            meshBroad.add(broadHeading);
        }
        doc.addField("mesh_broad", meshBroad);
    }

    private boolean isMarc(final Eresource eresource) {
        return AbstractMarcEresource.class.isAssignableFrom(eresource.getClass());
    }

    private void maybeAddDoi(final String keywords, final SolrInputDocument doc) {
        // for PubMed data, the first DOI returned by extractDois should be most authoritative
        List<String> dois = TextParserHelper.extractDois(keywords);
        if (!dois.isEmpty()) {
            doc.addField("dois", dois.get(0));
        }
    }

    private void maybeAddProxyHosts(final Eresource eresource, final SolrInputDocument doc) {
        // limit to SUL and Lane records
        // likely fine unlimited but seems silly to check millions of PubMed links w/o adding anything
        if (!isMarc(eresource)) {
            return;
        }
        Set<String> hosts = new HashSet<>();
        List<String> links = new ArrayList<>();
        eresource.getVersions().stream().filter(Version::isProxy).flatMap((final Version v) -> v.getLinks().stream())
                .collect(Collectors.toSet()).stream().filter((final Link l) -> l.getUrl() != null)
                .collect(Collectors.toSet()).forEach((final Link l) -> links.add(l.getUrl()));
        for (String link : links) {
            try {
                URI uri = new URI(link);
                String host = uri.getHost();
                if (null != host && !NOPROXY_HOSTS.matcher(host).matches()) {
                    hosts.add(host);
                }
            } catch (URISyntaxException e) {
                log.debug("uri problem: {}", e.getMessage(), e);
                // ok
                // maybe report these to Dick's group?
            }
        }
        doc.addField("proxyHosts", hosts);
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

    protected void insertEresource(final Eresource eresource) {
        SolrInputDocument doc = new SolrInputDocument();
        String sortTitle = getSortTitle(eresource);
        String kws = getKeywords(eresource);
        int[] itemCount = eresource.getItemCount();
        doc.addField("id", eresource.getId());
        doc.addField("recordId", Integer.toString(eresource.getRecordId()));
        doc.addField("recordType", eresource.getRecordType());
        doc.addField("description", eresource.getDescription());
        doc.addField("text", kws);
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
        doc.addField("isEnglish", Boolean.toString(eresource.isEnglish()));
        boolean isRecent = (THIS_YEAR - eresource.getYear()) <= TEN;
        doc.addField("isRecent", Boolean.toString(isRecent));
        boolean isRecentEdition = eresource.isRecentEdition() && isRecent;
        if (isRecentEdition) {
            doc.addField("isRecentEdition", Boolean.toString(isRecentEdition));
        }
        doc.addField("publicationAuthorsText", eresource.getPublicationAuthorsText());
        doc.addField("publicationText", eresource.getPublicationText());
        doc.addField("publicationTitle", eresource.getPublicationTitle());
        doc.addField("type", eresource.getTypes());
        StringBuilder authorSort = new StringBuilder();
        Collection<String> authors = eresource.getPublicationAuthors();
        doc.addField("author", authors);
        for (String author : authors) {
            authorSort.append(author);
        }
        addPublicationAuthors(eresource, doc);
        doc.addField("authors_sort", getSortText(authorSort.toString(), SORT_AUTHOR_MAX));
        doc.addField("publicationLanguage", eresource.getPublicationLanguages());
        for (String pubType : eresource.getPublicationTypes()) {
            doc.addField("publicationType", pubType);
            for (String parentType : this.meshManager.getParentHeadingsLimitToPubmedPublicationTypes(pubType)) {
                doc.addField("publicationType", parentType);
            }
        }
        doc.addField("versionsJson", versionsToJson(eresource));
        doc.addField("citationText", buildCitationKeywords(eresource));
        maybeAddDoi(kws, doc);
        doc.addField("isbns", eresource.getIsbns());
        doc.addField("issns", eresource.getIssns());
        maybeAddProxyHosts(eresource, doc);
        handleMesh(eresource, doc);
        this.solrDocs.add(doc);
    }
}
