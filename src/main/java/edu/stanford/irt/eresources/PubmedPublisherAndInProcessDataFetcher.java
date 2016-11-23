package edu.stanford.irt.eresources;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * fetch "as supplied by publisher" and "in-process" records from PubMed because they are not included in the licensed
 * PubMed/Medline FTP feed
 *
 * @author ryanmax
 */
public class PubmedPublisherAndInProcessDataFetcher extends AbstractPubmedDataFetcher implements DataFetcher {

    private static final String QUERY = "publisher [sb] OR inprocess [sb]";

    @Override
    public void getUpdateFiles() {
        String query = QUERY;
        try {
            query = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new EresourceDatabaseException(e);
        }
        PubmedSearcher searcher = new PubmedSearcher("No Field", "As Supplied by Publisher And In-Process", query);
        pmidListToFiles(searcher.getPmids(), "publisher-inprocess-");
    }
}
