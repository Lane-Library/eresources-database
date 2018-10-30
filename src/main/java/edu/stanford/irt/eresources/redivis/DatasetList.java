package edu.stanford.irt.eresources.redivis;

import java.util.List;

public class DatasetList {

    List<Dataset> datasets;

    String nextPageToken;

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

    /**
     * @param datasets
     *            the datasets to set
     */
    public void setDatasets(final List<Dataset> datasets) {
        this.datasets = datasets;
    }

    /**
     * @param nextPageToken
     *            the nextPageToken to set
     */
    public void setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
}
