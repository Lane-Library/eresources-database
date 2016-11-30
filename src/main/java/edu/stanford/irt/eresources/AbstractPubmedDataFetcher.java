package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * abstract class to handle NCBI eutils data fetching and writing data to files
 *
 * @author ryanmax
 */
public abstract class AbstractPubmedDataFetcher {

    private static final String BASE_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?email=ryanmax@stanford.edu&db=pubmed&mode=xml&id=";

    private static final RequestConfig HTTP_CONFIG = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES)
            .build();

    private static final HttpClient httpClient = HttpClients.createDefault();

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPubmedDataFetcher.class);

    private static final int PMIDS_PER_REQUEST = 500;

    private static final int SLEEP_TIME = 500;

    private static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    private String basePath;

    public void setBasePath(final String basePath) {
        if (null == basePath) {
            throw new IllegalArgumentException("null basePath");
        }
        this.basePath = basePath;
        File dir = new File(this.basePath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalArgumentException("missing and can't create " + this.basePath);
        }
    }

    protected void pmidListToFiles(final List<String> pmids, final String baseFilename) {
        List<String> myPmids = (ArrayList) ((ArrayList) pmids).clone();
        new File(this.basePath + "/" + TODAY);
        int i = 0;
        int start;
        int end;
        String url;
        while (!myPmids.isEmpty()) {
            start = i * PMIDS_PER_REQUEST;
            i++;
            end = i * PMIDS_PER_REQUEST;
            if (end > pmids.size()) {
                end = pmids.size();
            }
            List<String> sublist = pmids.subList(start, end);
            myPmids.removeAll(sublist);
            try {
                url = BASE_URL + URLEncoder.encode(StringUtils.collectionToCommaDelimitedString(sublist),
                        StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new EresourceDatabaseException(e);
            }
            String content = getContent(url);
            if (null == content) {
                // second request if content is null
                content = getContent(url);
            }
            if (null == content) {
                LOG.error("ncbi not responding; request: {}", url);
                // return without throwing an exception so other data fetching can complete
                LOG.error("exiting eutils fetch");
                return;
            }
            writeContent(content, baseFilename + i + ".xml");
        }
    }

    private String getContent(final String url) {
        String xmlContent = null;
        HttpResponse res = null;
        HttpGet get = new HttpGet(url);
        get.setConfig(HTTP_CONFIG);
        try {
            res = AbstractPubmedDataFetcher.httpClient.execute(get);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                xmlContent = EntityUtils.toString(res.getEntity());
            }
        } catch (IOException e) {
            get.abort();
            throw new EresourceDatabaseException(e);
        } finally {
            get.reset();
        }
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EresourceDatabaseException(e);
        }
        return xmlContent;
    }

    private void writeContent(final String content, final String filename) {
        File directory = new File(this.basePath + "/" + TODAY);
        if (!directory.exists() && !directory.mkdir()) {
            LOG.error("can't make {}", directory.getAbsolutePath());
        }
        File f = new File(directory.getAbsolutePath() + "/" + filename);
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
