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

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;

public class PmcEresourceProcessor extends AbstractEresourceProcessor {

    private static final String ERESOURCES = "eresources";

    private static final String HEADER_DEPOSIT_STATUS = " Deposit status";

    private static final String HEADER_EARLIEST_VOLUME = "Earliest volume";

    private static final String HEADER_EISSN = "eISSN";

    private static final String HEADER_FREE_ACCESS = "Free access";

    private static final String HEADER_JOURNAL_TITLE = "Journal title";

    private static final String HEADER_JOURNAL_URL = " Journal URL";

    private static final String HEADER_LATEST_ISSUE = "Latest issue";

    private static final String HEADER_LOCATOR_ID = "LOCATORplus ID";

    private static final String HEADER_NLM_TA = "NLM TA";

    private static final String HEADER_OPEN_ACCESS = "Open access";

    private static final String HEADER_PARTICIPATION_LEVEL = "Participation level";

    private static final String HEADER_PISSN = "pISSN";

    private static final String HEADER_PUBLISHER = "Publisher";

    private static final String[] HEADERS_CSV = { HEADER_JOURNAL_TITLE, HEADER_NLM_TA, HEADER_PISSN, HEADER_EISSN,
            HEADER_PUBLISHER, HEADER_LOCATOR_ID, HEADER_LATEST_ISSUE, HEADER_EARLIEST_VOLUME, HEADER_FREE_ACCESS,
            HEADER_OPEN_ACCESS, HEADER_PARTICIPATION_LEVEL, HEADER_DEPOSIT_STATUS, HEADER_JOURNAL_URL };

    private static final Logger log = LoggerFactory.getLogger(PmcEresourceProcessor.class);

    private static final int MAX_RETRIES = 3;

    private static final int SLEEP_TIME = 1_000;

    protected static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ssz").toFormatter();

    private String allJournalsCsvUrl;

    private String apiKey;

    private ContentHandler contentHandler;

    private String efetchBaseUrl;

    private ErrorHandler errorHandler = new DefaultHandler();

    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private LaneDedupAugmentation laneDedupAugmentation;

    private TransformerFactory tf = TransformerFactory.newInstance();

    public PmcEresourceProcessor(final String efetchBaseUrl, final String allJournalsCsvUrl,
            final ContentHandler contentHandler, final LaneDedupAugmentation laneDedupAugmentation,
            final String apiKey) {
        this.allJournalsCsvUrl = allJournalsCsvUrl;
        this.contentHandler = contentHandler;
        this.laneDedupAugmentation = laneDedupAugmentation;
        this.apiKey = apiKey;
        this.efetchBaseUrl = efetchBaseUrl;
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
                StringBuilder sb = new StringBuilder(this.efetchBaseUrl);
                sb.append("?db=nlmcatalog&retmode=xml&id=");
                sb.append(journal.getNlmId());
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
                root.setAttribute("freeAccess", journal.getFreeAccess());
                root.setAttribute("earliestVolume", journal.getEarliestVolume());
                root.setAttribute("lastIssue", journal.getLastIssue());
                root.setAttribute("depositStatus", journal.getDepositStatus());
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
            rateLimit = con.getHeaderField("X-RateLimit-Remaining");
            if (rateLimit != null && !rateLimit.isEmpty() && Integer.parseInt(rateLimit) <= 1) {
                log.info("NCBI connection rate limit reached so sleeping");
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
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    throw new EresourceDatabaseException(e1);
                }
                return doFetch(url, attempt - 1);
            }
            throw new EresourceDatabaseException(e);
        } catch (URISyntaxException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    private List<PmcJournal> getJournals() {
        List<PmcJournal> journals = new LinkedList<>();
        CSVFormat.Builder builder = CSVFormat.Builder.create().setHeader(HEADERS_CSV).setSkipHeaderRecord(true);
        try {
            Iterable<CSVRecord> records = builder.build()
                    .parse(new InputStreamReader(doFetch(this.allJournalsCsvUrl, MAX_RETRIES)));
            for (CSVRecord row : records) {
                PmcJournal journal = new PmcJournal();
                journal.setDepositStatus(row.get(HEADER_DEPOSIT_STATUS));
                journal.setEarliestVolume(row.get(HEADER_EARLIEST_VOLUME));
                journal.seteIssn(row.get(HEADER_EISSN));
                journal.setpIssn(row.get(HEADER_PISSN));
                journal.setFreeAccess(row.get(HEADER_FREE_ACCESS));
                journal.setJournalUrl(row.get(HEADER_JOURNAL_URL));
                journal.setParticipation(row.get(HEADER_PARTICIPATION_LEVEL));
                journal.setPublisher(row.get(HEADER_PUBLISHER));
                journal.setNlmId(row.get(HEADER_LOCATOR_ID));
                journal.setTitleTA(row.get(HEADER_NLM_TA));
                journal.setTitle(row.get(HEADER_JOURNAL_TITLE));
                journal.setLastIssue(row.get(HEADER_LATEST_ISSUE));
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
        return journal.getParticipation().equalsIgnoreCase("Full");
    }
}
