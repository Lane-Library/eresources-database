package edu.stanford.irt.eresources.pmc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;

public class PmcEresourceProcessor extends AbstractEresourceProcessor {

    private static final String ERESOURCES = "eresources";

    private static final String EUTILS_EFETCH_BASE = "efetch.fcgi?db=nlmcatalog";

    private static final String EUTILS_ESEARCH_BASE = "esearch.fcgi?db=nlmcatalog";

    private static final String HEADER_AGREEMENT_STATUS = "Agreement Status";

    private static final String HEADER_AGREEMENT_TO_DEPOSIT = "Agreement to Deposit";

    private static final String HEADER_EARLIEST = "Earliest";

    private static final String HEADER_EMBARGO = "Release Delay (Embargo)";

    private static final String HEADER_ISSN_E = "ISSN (online)";

    private static final String HEADER_ISSN_P = "ISSN (print)";

    private static final String HEADER_JOURNAL_NOTE = "Journal Note";

    private static final String HEADER_JOURNAL_TITLE = "Journal Title";

    private static final String HEADER_JOURNAL_URL = "PMC URL";

    private static final String HEADER_MOST_RECENT = "Most Recent";

    private static final String HEADER_NLM_ID = "NLM Unique ID";

    private static final String HEADER_NLM_TA = "NLM Title Abbreviation (TA)";

    private static final String HEADER_PUBLISHER = "Publisher";

    private static final String[] HEADERS_CSV = { HEADER_JOURNAL_TITLE, HEADER_NLM_TA, HEADER_PUBLISHER, HEADER_ISSN_P,
            HEADER_ISSN_E, HEADER_NLM_ID, HEADER_MOST_RECENT, HEADER_EARLIEST, HEADER_EMBARGO, HEADER_AGREEMENT_STATUS,
            HEADER_AGREEMENT_TO_DEPOSIT, HEADER_JOURNAL_NOTE, HEADER_JOURNAL_URL };

    private static final Configuration JSON_CONF;

    private static final Logger log = LoggerFactory.getLogger(PmcEresourceProcessor.class);

    private static final int MAX_RETRIES = 3;

    private static final int SLEEP_TIME = 1_000;

    protected static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ssz").toFormatter();
    static {
        JSON_CONF = Configuration.defaultConfiguration().setOptions(Option.SUPPRESS_EXCEPTIONS);
    }

    private String allJournalsCsvUrl;

    private String apiKey;

    private ContentHandler contentHandler;

    private String efetchBaseUrl;

    private ErrorHandler errorHandler = new DefaultHandler();

    private String esearchBaseUrl;

    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private LaneDedupAugmentation laneDedupAugmentation;

    private TransformerFactory tf = TransformerFactory.newInstance();

    public PmcEresourceProcessor(final String eutilsBaseUrl, final String allJournalsCsvUrl,
            final ContentHandler contentHandler, final LaneDedupAugmentation laneDedupAugmentation,
            final String apiKey) {
        this.allJournalsCsvUrl = allJournalsCsvUrl;
        this.contentHandler = contentHandler;
        this.laneDedupAugmentation = laneDedupAugmentation;
        this.apiKey = apiKey;
        this.efetchBaseUrl = eutilsBaseUrl + EUTILS_EFETCH_BASE;
        this.esearchBaseUrl = eutilsBaseUrl + EUTILS_ESEARCH_BASE;
    }

    @Override
    public void process() {
        if (null == this.allJournalsCsvUrl) {
            throw new IllegalArgumentException("null allJournalsCsvUrl");
        }
        if (null == this.contentHandler) {
            throw new IllegalArgumentException("null contentHandler");
        }
        try {
            this.factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            List<PmcJournal> journals = getJournals();
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            while (!journals.isEmpty()) {
                PmcJournal journal = journals.remove(0);
                String nlmcatalogId = doSearch(journal.getNlmId());
                if (nlmcatalogId == null) {
                    log.warn("problem fetching nlmcatalogId for journal: {}; defaulting to nlm id from jlist",
                            journal.getNlmId());
                    nlmcatalogId = journal.getNlmId();
                }
                StringBuilder sb = new StringBuilder(this.efetchBaseUrl);
                sb.append("&retmode=xml&id=");
                sb.append(nlmcatalogId);
                sb.append("&api_key=");
                sb.append(this.apiKey);
                InputSource source = new InputSource(doFetch(sb.toString(), MAX_RETRIES));
                this.factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder parser = this.factory.newDocumentBuilder();
                parser.setErrorHandler(this.errorHandler);
                Document doc = parser.parse(source);
                this.contentHandler.startDocument();
                this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
                Element root = doc.getDocumentElement();
                root.setAttribute("journalUrl", journal.getJournalUrl());
                root.setAttribute("eIssn", journal.geteIssn());
                root.setAttribute("pIssn", journal.getpIssn());
                root.setAttribute("embargo", journal.getEmbargo());
                root.setAttribute("earliestVolume", journal.getEarliest());
                root.setAttribute("lastIssue", journal.getLastIssue());
                root.setAttribute("agreementStatus", journal.getAgreementStatus());
                root.setAttribute("normedTitle", TextParserHelper.toTitleCase(journal.getTitle()));
                this.tf.newTransformer().transform(new DOMSource(doc), new SAXResult(this.contentHandler));
                this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
                this.contentHandler.endDocument();
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (IOException | SAXException | TransformerException | ParserConfigurationException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    private InputStream doFetch(final String url, final int attempt) {
        int remaining = attempt - 1;
        String rateLimit = null;
        try {
            URL urlObject = new URI(url).toURL();
            URLConnection con = urlObject.openConnection();
            con.setRequestProperty("User-Agent", "Lane Indexer");
            rateLimit = con.getHeaderField("X-RateLimit-Remaining");
            if (rateLimit != null && !rateLimit.isEmpty() && Integer.parseInt(rateLimit) <= 1) {
                log.info("NCBI connection rate limit reached so sleeping");
                Thread.sleep(SLEEP_TIME);
            }
            if ("gzip".equals(con.getContentEncoding())) {
                return new GZIPInputStream(con.getInputStream());
            }
            return con.getInputStream();
        } catch (IOException e) {
            // ncbi sometimes returns 400s even when well-below the rate limit, so try again
            if (remaining > 0) {
                log.info("NBCI returned an error", e);
                log.info("RateLimit-Remaining: {}", rateLimit);
                log.info("will try {} more times", remaining);
                return doFetch(url, attempt - 1);
            }
            throw new EresourceDatabaseException(e);
        } catch (URISyntaxException e) {
            throw new EresourceDatabaseException(e);
        } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
            throw new EresourceDatabaseException(e1);
        }
    }

    private String doSearch(final String nlmUniqueId) {
        StringBuilder sb = new StringBuilder(this.esearchBaseUrl);
        sb.append("&retmode=json");
        sb.append("&api_key=");
        sb.append(this.apiKey);
        sb.append("&term=");
        sb.append(nlmUniqueId);
        sb.append("[NLM%20Unique%20ID]");
        InputStream is = doFetch(sb.toString(), MAX_RETRIES);
        ReadContext ctx = JsonPath.using(JSON_CONF).parse(is);
        return ctx.read("$.esearchresult.idlist[0]");
    }

    private List<PmcJournal> getJournals() {
        List<PmcJournal> journals = new LinkedList<>();
        CSVFormat.Builder builder = CSVFormat.Builder.create().setHeader(HEADERS_CSV).setSkipHeaderRecord(true);
        try {
            Iterable<CSVRecord> records = builder.build()
                    .parse(new InputStreamReader(doFetch(this.allJournalsCsvUrl, MAX_RETRIES)));
            for (CSVRecord row : records) {
                PmcJournal journal = new PmcJournal();
                journal.setAgreementStatus(row.get(HEADER_AGREEMENT_STATUS));
                journal.setEarliest(row.get(HEADER_EARLIEST));
                journal.seteIssn(row.get(HEADER_ISSN_E));
                journal.setpIssn(row.get(HEADER_ISSN_P));
                journal.setEmbargo(row.get(HEADER_EMBARGO));
                journal.setJournalUrl(row.get(HEADER_JOURNAL_URL));
                journal.setParticipation(row.get(HEADER_AGREEMENT_TO_DEPOSIT));
                journal.setPublisher(row.get(HEADER_PUBLISHER));
                journal.setNlmId(row.get(HEADER_NLM_ID));
                journal.setTitleTA(row.get(HEADER_NLM_TA));
                journal.setTitle(row.get(HEADER_JOURNAL_TITLE));
                journal.setLastIssue(row.get(HEADER_MOST_RECENT));
                if (isIndexable(journal) && !isDuplicate(journal)) {
                    journals.add(journal);
                }
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
        return journals;
    }

    private boolean isDuplicate(final PmcJournal journal) {
        return this.laneDedupAugmentation.isDuplicate(LaneDedupAugmentation.KEY_DNLM_CONTROL_NUMBER, journal.getNlmId())
                || this.laneDedupAugmentation.isDuplicate(LaneDedupAugmentation.KEY_ISSN, journal.geteIssn())
                || this.laneDedupAugmentation.isDuplicate(LaneDedupAugmentation.KEY_ISSN, journal.getpIssn());
    }

    private boolean isIndexable(final PmcJournal journal) {
        // limit to "full" participation as per Thea and Sonam
        // "participation" field changed to "Agreement to Deposit" in 2024
        // and value to look for is now "All"
        return journal.getParticipation().equalsIgnoreCase("All articles");
    }
}
