package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
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

public class PubmedInProcessDataFetcher implements DataFetcher {

    private static final String _TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    private static final String BASE_FILENAME = "inprocess-" + _TODAY + "_";

    private static final String BASE_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?email=ryanmax@stanford.edu&db=pubmed&mode=xml&id=";

    private static final RequestConfig HTTP_CONFIG = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES)
            .build();

    private static final HttpClient httpClient = HttpClients.createDefault();

    private static final int PMIDS_PER_REQUEST = 500;

    private static final String PROP_FILE = "inprocess.properties";

    private static final String QUERY = "(publisher [sb] OR inprocess [sb]) AND (\"?\"[EDAT] : \"3000\"[EDAT]) ";

    private static final int SLEEP_TIME = 500;

    private String basePath;

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
        String updateDate = "0";
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
        try {
            query = URLEncoder.encode(QUERY.replace("?", date), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new EresourceDatabaseException(e);
        }
        this.searcher = new PubmedSearcher("In-Process and As Supplied by Publisher", query);
        pmidListToFiles(this.searcher.getPmids());
        writeLastRunDate();
    }

    private void pmidListToFiles(final List<String> pmids) {
        StringBuilder sb = new StringBuilder();
        int removed = 0;
        int filesWritten = 0;
        String url;
        int size = pmids.size();
        File parentDirectory = null;
        if (size > 0) {
            parentDirectory = new File(this.basePath + "/" + _TODAY);
            parentDirectory.mkdir();
        }
        for (int i = 0; i < size; i++) {
            sb.append(',').append(pmids.remove(0));
            if (++removed >= PMIDS_PER_REQUEST || pmids.size() == 0) {
                try {
                    url = BASE_URL + URLEncoder.encode(sb.toString().substring(1), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new EresourceDatabaseException(e);
                }
                removed = 0;
                sb = new StringBuilder();
                String content = getContent(url);
                if (null == content) {
                    // second request if content is null
                    content = getContent(url);
                }
                if (null == content) {
                    throw new EresourceDatabaseException("ncbi not responding; request: " + url);
                }
                File f = new File(parentDirectory.getAbsolutePath() + "/" + BASE_FILENAME + ++filesWritten + ".xml");
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
