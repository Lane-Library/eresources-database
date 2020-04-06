package edu.stanford.irt.eresources.redivis;

import java.util.ArrayList;
import java.util.List;

public class DatasetList {

    private List<Result> results;
    
    private String nextPageToken;

    public DatasetList() {
        // empty constructor
    }

    /**
     * @return the results (datasets)
     */
    public List<Result> getResults() {
        if (null != this.results) {
            return new ArrayList<>(this.results);
        }
        return new ArrayList<>();
    }
    
    /**
     * @return the nextPageToken
     */
    public String getNextPageToken() {
        return this.nextPageToken;
    }
}
