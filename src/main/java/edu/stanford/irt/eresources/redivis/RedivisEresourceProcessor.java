package edu.stanford.irt.eresources.redivis;

import java.io.IOException;
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

    private String datasetsGetEndpoint;

    private String datasetsListEndpoint;

    private EresourceHandler eresourceHandler;

    private ObjectMapper mapper;

    public RedivisEresourceProcessor(final String listEndpoint, final String getEndpoint,
            final EresourceHandler eresourceHandler) {
        this.eresourceHandler = eresourceHandler;
        this.datasetsGetEndpoint = getEndpoint;
        this.datasetsListEndpoint = listEndpoint;
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void process() {
        for (Dataset dataset : getDatasets()) {
            RedivisEresource eresource = new RedivisEresource(dataset);
            this.eresourceHandler.handleEresource(eresource);
        }
    }

    private List<Dataset> getDatasets() {
        String pageToken = "";
        List<Dataset> datasets = new ArrayList<>();
        try {
            while (null != pageToken) {
                URL listUrl = throttledURL(this.datasetsListEndpoint + "?pageToken=" + pageToken);
                DatasetList datasetList = this.mapper.readValue(listUrl, DatasetList.class);
                // list API is missing crucial pieces of dataset data, so need to query for each dataset
                for (Dataset dataset : datasetList.getDatasets()) {
                    URL getUrl = throttledURL(this.datasetsGetEndpoint + "/" + dataset.getId());
                    Dataset detailedDataset = this.mapper.readValue(getUrl, Dataset.class);
                    datasets.add(detailedDataset);
                }
                pageToken = datasetList.getNextPageToken();
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
        return datasets;
    }

    private URL throttledURL(final String url) {
        try {
            URL urlObject = new URL(url);
            URLConnection con = urlObject.openConnection();
            String rateLimit = con.getHeaderField("X-RateLimit-Remaining");
            if (rateLimit != null && !rateLimit.isEmpty() && Integer.parseInt(rateLimit) <= 1) {
                Thread.sleep(SLEEP_TIME);
            }
            return urlObject;
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
            throw new EresourceDatabaseException(e1);
        }
    }
}
