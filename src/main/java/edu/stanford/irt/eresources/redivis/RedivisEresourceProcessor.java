package edu.stanford.irt.eresources.redivis;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.EresourceHandler;

public class RedivisEresourceProcessor extends AbstractEresourceProcessor {

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
                URL listUrl = new URL(this.datasetsListEndpoint + "?pageToken=" + pageToken);
                DatasetList datasetList = this.mapper.readValue(listUrl, DatasetList.class);
                // list API is missing crucial pieces of dataset data, so need to query for each dataset
                for (Dataset dataset : datasetList.getDatasets()) {
                    URL getUrl = new URL(this.datasetsGetEndpoint + "/" + dataset.getId());
                    Dataset detailedDataset = this.mapper.readValue(getUrl, Dataset.class);
                    datasets.add(detailedDataset);
                }
                pageToken = datasetList.nextPageToken;
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
        return datasets;
    }
}
