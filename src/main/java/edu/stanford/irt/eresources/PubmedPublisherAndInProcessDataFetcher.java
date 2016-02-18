package edu.stanford.irt.eresources;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * fetch "as supplied by publisher" and "in-process" records from PubMed because they are not included in the licensed
 * PubMed/Medline FTP feed
 *
 * @author ryanmax
 */
public class PubmedPublisherAndInProcessDataFetcher extends AbstractPubmedDataFetcher implements DataFetcher {

    private static final String QUERY = "publisher [sb] OR inprocess [sb]";

    private PubmedSearcher searcher;

    @Override
    public void getUpdateFiles() {
        String query = QUERY;
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new EresourceDatabaseException(e);
        }
        this.searcher = new PubmedSearcher("No Field", "As Supplied by Publisher And In-Process", query);
        pmidListToFiles(this.searcher.getPmids(), "publisher-inprocess-");
    }
}
