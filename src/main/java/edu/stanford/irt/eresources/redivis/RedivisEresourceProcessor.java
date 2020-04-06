package edu.stanford.irt.eresources.redivis;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.EresourceHandler;

public class RedivisEresourceProcessor extends AbstractEresourceProcessor {

    // https://apidocs.redivis.com/limits-and-errors
    // "100 request per 100-second interval"
    private static final int SLEEP_TIME = 60_000;

    private String datasetsListEndpoint;

    private EresourceHandler eresourceHandler;

    private ObjectMapper mapper;

    private String token;

    public RedivisEresourceProcessor(final String listEndpoint, final String token,
            final EresourceHandler eresourceHandler) {
        this.token = token;
        this.eresourceHandler = eresourceHandler;
        this.datasetsListEndpoint = listEndpoint;
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void process() {
        for (Result dataset : getDatasetListResults()) {
            RedivisEresource eresource = new RedivisEresource(dataset);
            this.eresourceHandler.handleEresource(eresource);
        }
    }

    private String buildRequestString(final String pageToken) {
        if (!pageToken.isEmpty()) {
            return this.datasetsListEndpoint + "?pageToken=" + pageToken;
        }
        return this.datasetsListEndpoint;
    }

    private List<Result> getDatasetListResults() {
        String pageToken = "";
        List<Result> results = new ArrayList<>();
        try {
            while (null != pageToken) {
                InputStream input = throttledFetch(buildRequestString(pageToken));
                DatasetList datasetList = this.mapper.readValue(input, DatasetList.class);
                input.close();
                for (Result dataset : datasetList.getResults()) {
                    results.add(dataset);
                }
                pageToken = datasetList.getNextPageToken();
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
        return results;
    }
    
    private InputStream throttledFetch(final String url) {
        try {
            URL urlObject = new URL(url);
            URLConnection con = urlObject.openConnection();
            con.addRequestProperty("Authorization", "Bearer " + this.token);
            String rateLimit = con.getHeaderField("X-RateLimit-Remaining");
            if (rateLimit != null && !rateLimit.isEmpty() && Integer.parseInt(rateLimit) <= 1) {
                Thread.sleep(SLEEP_TIME);
            }
            return con.getInputStream();
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
            throw new EresourceDatabaseException(e1);
        }
    }
}
