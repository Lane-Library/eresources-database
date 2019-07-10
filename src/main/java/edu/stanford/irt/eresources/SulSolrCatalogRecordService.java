package edu.stanford.irt.eresources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CursorMarkParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.marc4j.MarcException;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SulSolrCatalogRecordService extends PipedInputStream implements Runnable, CatalogRecordService {

    private static final int FETCH_ROWS = 10_000;

    private static final String FORMAT_FIELD = "format_main_ssim";

    private static final String ID_FIELD = "id";

    private static final Logger log = LoggerFactory.getLogger(SulSolrCatalogRecordService.class);

    private static final String MARCBIBXML_FIELD = "marcbib_xml";

    private static final String MARCXML_FIELD = "marcxml";

    // https://en.wikipedia.org/wiki/Valid_characters_in_XML#XML_1.0
    private static final Pattern VALID_CODE_POINTS = Pattern
            .compile("[^\\u0009\\u000A\\u000D\u0020-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFF]+");

    private PipedOutputStream output;

    private Executor executor;

    private Map<String, List<String>> recordFormats;

    private SolrClient solrClient;

    public SulSolrCatalogRecordService(final SolrClient solrClient, final Executor executor) {
        this.solrClient = solrClient;
        this.executor = executor;
        this.recordFormats = new HashMap<>();
    }

    /**
     * fetch record formats from solr if not already present in recordFormats {@code Map}
     *
     * @param recordId
     *            id of the record to lookup
     * @return String array of formats
     */
    public List<String> getRecordFormats(final String recordId) {
        if (this.recordFormats.containsKey(recordId)) {
            return this.recordFormats.get(recordId);
        }
        QueryResponse rsp = queryForFields("id:" + recordId, FORMAT_FIELD);
        this.recordFormats.put(recordId, extractFormats(rsp.getResults().get(0)));
        return this.recordFormats.get(recordId);
    }

    @Override
    public InputStream getRecordStream(final long time) {
        return this;
    }

    @Override
    public int read() throws IOException {
        if (null == this.output) {
            this.output = new PipedOutputStream(this);
            this.executor.execute(this);
        }
        return super.read();
    }

    @Override
    public void run() {
        SolrQuery q = (new SolrQuery("*:*")).setRows(FETCH_ROWS).setSort(SortClause.asc(ID_FIELD));
        disableFaceting(q);
        q.setFields(ID_FIELD, FORMAT_FIELD, MARCXML_FIELD);
        String cursorMark = CursorMarkParams.CURSOR_MARK_START;
        boolean done = false;
        int i = 0;
        while (!done) {
            q.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
            QueryResponse rsp = doSolrQuery(q);
            String nextCursorMark = rsp.getNextCursorMark();
            for (SolrDocument doc : rsp.getResults()) {
                i++;
                String id = (String) doc.getFieldValue(ID_FIELD);
                this.recordFormats.put(id, extractFormats(doc));
                String xml = (String) doc.getFieldValue(MARCXML_FIELD);
                parseMarcAndWriteToOutputStream(id, MARCXML_FIELD, xml);
            }
            log.info("{} records fetched", i);
            if (cursorMark.equals(nextCursorMark)) {
                done = true;
            }
            cursorMark = nextCursorMark;
        }
        try {
            this.output.close();
        } catch (IOException e) {
            log.error("can't close output stream", e);
        }
    }

    // for unit testing
    public void setPipedOutputStream(final PipedOutputStream outputStream) {
        this.output = outputStream;
    }

    /**
     * need explicit facet=false param to disable faceting on SUL's default request handler (setFacet just removes all
     * facet params)
     *
     * @param q
     *            {@code SolrQuery} to append params to
     */
    private void disableFaceting(final SolrQuery q) {
        q.setFacet(false);
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("facet", "false");
        q.add(params);
    }

    /**
     * execute {@link SolrQuery}, catching exceptions
     *
     * @param q
     *            {@code SolrQuery}
     * @return {@code QueryResponse} from solr client
     */
    private QueryResponse doSolrQuery(final SolrQuery q) {
        QueryResponse rsp = null;
        try {
            rsp = this.solrClient.query(q);
        } catch (SolrServerException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
        return rsp;
    }

    /**
     * extract formats from {@code SolrDocument}
     *
     * @param solrDocument
     * @return {@code String} array of formats
     */
    private List<String> extractFormats(final SolrDocument solrDocument) {
        List<String> formats = new ArrayList<>();
        Collection<Object> formatValues = solrDocument.getFieldValues(FORMAT_FIELD);
        if (null != formatValues) {
            for (Object format : formatValues) {
                formats.add((String) format);
            }
        }
        return formats;
    }

    /**
     * convert marcxml to marc and write to copy to {@code OutputStream}. Since parsing marcxml throws frequent
     * {@code MarcException} errors, try a second time using marcbib_xml which lacks holdings info.
     *
     * @param recordId
     *            id of this record
     * @param fieldName
     *            MARCXML_FIELD or MARCBIBXML_FIELD
     * @param xml
     *            {@code String} marc xml
     */
    private void parseMarcAndWriteToOutputStream(final String recordId, final String fieldName, final String xml) {
        String sanitizedXml = VALID_CODE_POINTS.matcher(xml).replaceAll("");
        try (InputStream stream = new ByteArrayInputStream(sanitizedXml.getBytes(StandardCharsets.UTF_8));
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            MarcStreamWriter msw = new MarcStreamWriter(baos, "UTF-8");
            MarcReader reader = new MarcXmlReader(stream);
            Record r = reader.next();
            msw.write(r);
            IOUtils.copy(new ByteArrayInputStream(baos.toByteArray()), this.output);
            msw.close();
        } catch (MarcException e) {
            log.warn("Can't parse record: {}; {}", recordId, e.getMessage());
            if (MARCXML_FIELD.equals(fieldName)) {
                log.info("second attempt to fetch marc data, this time using {}", MARCBIBXML_FIELD);
                QueryResponse qr = queryForFields("id:" + recordId, MARCBIBXML_FIELD);
                String bibXml = (String) qr.getResults().get(0).getFieldValue(MARCBIBXML_FIELD);
                if (null != bibXml) {
                    parseMarcAndWriteToOutputStream(recordId, MARCBIBXML_FIELD, bibXml);
                }
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    /**
     * @param queryString
     *            Solr query {@code String}
     * @param field
     *            {@code String} field to pass to {@code SolrQuery.setFields}
     * @return
     */
    private QueryResponse queryForFields(final String queryString, final String field) {
        SolrQuery q = (new SolrQuery(queryString));
        disableFaceting(q);
        q.setFields(field);
        return doSolrQuery(q);
    }
}
