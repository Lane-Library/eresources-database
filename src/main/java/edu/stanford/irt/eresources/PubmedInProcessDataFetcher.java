package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * fetch "as supplied by publisher" records from PubMed because they are not included in the licensed PubMed/Medline FTP
 * feed; queries just grab everything added in the last day so duplication can occur between this and the FTP data
 * fetcher
 *
 * @author ryanmax
 */
public class PubmedInProcessDataFetcher extends AbstractPubmedDataFetcher implements DataFetcher {

    private static final String PROP_FILE = "inprocess.properties";

    private static final String PROP_NAME = "pubmed.inprocess.lastUpdate";

    private static final String UPDATES_QUERY = "(\"?\"[EDAT] : \"3000\"[EDAT]) ";

    private Properties properties;

    private File propertiesFile;

    private PubmedSearcher searcher;

    @Override
    public void getUpdateFiles() {
        String date = getLastUpdateDate();
        String query = UPDATES_QUERY.replace("?", date);
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new EresourceDatabaseException(e);
        }
        this.searcher = new PubmedSearcher("No Field", "In-Process", query);
        pmidListToFiles(this.searcher.getPmids(), "inprocess-");
        writeLastRunDate();
    }

    private String getLastUpdateDate() {
        String updateDate = null;
        FileInputStream in = null;
        this.propertiesFile = new File(PROP_FILE);
        try {
            this.propertiesFile.createNewFile();
            in = new FileInputStream(this.propertiesFile);
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
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(this.propertiesFile);
            this.properties.store(fos, null);
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
