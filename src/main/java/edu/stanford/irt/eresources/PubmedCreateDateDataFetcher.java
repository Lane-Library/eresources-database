package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * fetch "as supplied by publisher" records from PubMed because they are not included in the licensed PubMed/Medline FTP
 * feed; query includes every citation created in the last day; duplication can occur between this and FTP data fetcher;
 * explanation of CRDT http://www.ncbi.nlm.nih.gov/books/NBK3827/#pubmedhelp.Create_Date_CRDT
 *
 * @author ryanmax
 */
public class PubmedCreateDateDataFetcher extends AbstractPubmedDataFetcher implements DataFetcher {

    private static final String PROP_FILE = "createDate.properties";

    private static final String PROP_NAME = "pubmed.createDate.lastUpdate";

    private static final String UPDATES_QUERY = "(\"?\"[CRDT] : \"3000\"[CRDT]) ";

    private Properties properties;

    private File propertiesFile;

    @Override
    public void getUpdateFiles() {
        String date = getLastUpdateDate();
        String query = UPDATES_QUERY.replace("?", date);
        try {
            query = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new EresourceDatabaseException(e);
        }
        pmidListToFiles(new PubmedSearcher("No Field", "Create Date", query).getPmids(), "createDate-");
        writeLastRunDate();
    }

    private String getLastUpdateDate() {
        String updateDate = null;
        this.propertiesFile = new File(PROP_FILE);
        if (!this.propertiesFile.exists()) {
            throw new EresourceDatabaseException("missing " + PROP_FILE);
        }
        try (InputStream in = new FileInputStream(this.propertiesFile)) {
            this.properties = new Properties();
            this.properties.load(in);
            if (this.properties.containsKey(PROP_NAME)) {
                updateDate = this.properties.getProperty(PROP_NAME);
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
        return updateDate;
    }

    private void writeLastRunDate() {
        this.properties.setProperty(PROP_NAME, new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        try (OutputStream fos = new FileOutputStream(this.propertiesFile)) {
            this.properties.store(fos, null);
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
