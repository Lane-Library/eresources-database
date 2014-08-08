package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
 * fetch "as supplied by publisher" records from PubMed because they are not included in the licensed PubMed/Medline FTP
 * feed; initial query is "publisher [sb]"; subsequent queries just grab everything added in the last day so duplication
 * can occur between this and the FTP data fetcher
 * 
 * @author ryanmax
 */
public class PubmedInProcessDataFetcher implements DataFetcher {

    private static final String _TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    private static final String BASE_FILENAME = "inprocess-";

    private static final String BASE_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?email=ryanmax@stanford.edu&db=pubmed&mode=xml&id=";

    private static final RequestConfig HTTP_CONFIG = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES)
            .build();

    private static final HttpClient httpClient = HttpClients.createDefault();

    private static final String INITIAL_QUERY = "publisher [sb]";

    private static final int PMIDS_PER_REQUEST = 500;

    private static final String PROP_FILE = "inprocess.properties";

    private static final int SLEEP_TIME = 500;

    private static final String UPDATES_QUERY = "(\"?\"[EDAT] : \"3000\"[EDAT]) ";

    private String basePath;

    private Logger log = LoggerFactory.getLogger(getClass());

    private Properties properties;

    private File propertiesFile;

    private PubmedSearcher searcher;

    private String getContent(final String url) {
        String xmlContent = null;
        HttpResponse res = null;
        HttpGet method = new HttpGet(url);
        method.setConfig(HTTP_CONFIG);
        try {
            res = PubmedInProcessDataFetcher.httpClient.execute(method);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                xmlContent = EntityUtils.toString(res.getEntity());
            }
        } catch (Exception e) {
            method.abort();
        }
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            throw new EresourceDatabaseException(e);
        }
        return xmlContent;
    }

    private String getLastUpdateDate() {
        String updateDate = null;
        FileInputStream in = null;
        this.propertiesFile = new File(this.basePath + "/" + PROP_FILE);
        try {
            this.propertiesFile.createNewFile();
            in = new FileInputStream(this.propertiesFile);
            this.properties = new Properties();
            this.properties.load(in);
            if (this.properties.containsKey("lastUpdate")) {
                updateDate = this.properties.getProperty("lastUpdate");
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
        return updateDate;
    }

    @Override
    public void getUpdateFiles() {
        String date = getLastUpdateDate();
        String query;
        if (null == date) {
            query = INITIAL_QUERY;
        } else {
            query = UPDATES_QUERY.replace("?", date);
        }
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new EresourceDatabaseException(e);
        }
        this.searcher = new PubmedSearcher("In-Process and As Supplied by Publisher", query);
        pmidListToFiles(this.searcher.getPmids());
        writeLastRunDate();
    }

    private void pmidListToFiles(final List<String> pmids) {
        List<String> myPmids = (ArrayList<String>) ((ArrayList<String>) pmids).clone();
        File directory = new File(this.basePath + "/" + _TODAY);
        int i = 0;
        int start;
        int end;
        String url;
        while (myPmids.size() > 0) {
            start = i * PMIDS_PER_REQUEST;
            end = ++i * PMIDS_PER_REQUEST;
            if (end > pmids.size()) {
                end = pmids.size();
            }
            List<String> sublist = pmids.subList(start, end);
            myPmids.removeAll(sublist);
            try {
                url = BASE_URL + URLEncoder.encode(StringUtils.collectionToCommaDelimitedString(sublist), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new EresourceDatabaseException(e);
            }
            String content = getContent(url);
            if (null == content) {
                // second request if content is null
                content = getContent(url);
            }
            if (null == content) {
                this.log.error("ncbi not responding; request: " + url);
                // return without throwing an exception so other data fetching (ftp) can complete
                this.log.error("exiting in-process fetch without updating lastUpdate date");
                return;
            }
            if (!directory.exists()) {
                directory.mkdir();
            }
            File f = new File(directory.getAbsolutePath() + "/" + BASE_FILENAME + i + ".xml");
            FileOutputStream fos = null;
            try {
                f.createNewFile();
                fos = new FileOutputStream(f);
                fos.write(content.getBytes());
                fos.close();
            } catch (IOException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }

    public void setBasePath(final String basePath) {
        if (null == basePath) {
            throw new IllegalArgumentException("null basePath");
        }
        this.basePath = basePath;
    }

    private void writeLastRunDate() {
        this.properties.setProperty("lastUpdate", new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(this.propertiesFile);
            this.properties.store(fos, null);
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
