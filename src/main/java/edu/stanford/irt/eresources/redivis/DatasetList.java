package edu.stanford.irt.eresources.redivis;

import java.util.List;

public class DatasetList {

    private List<Dataset> datasets;

    private String nextPageToken;

    public DatasetList() {
        // empty constructor
    }

    /**
     * @return the datasets
     */
    public List<Dataset> getDatasets() {
        return this.datasets;
    }

    /**
     * @return the nextPageToken
     */
    public String getNextPageToken() {
        return this.nextPageToken;
    }
}
